#!/usr/bin/env python3
import sys
import json
import base64
import io
import os
from PIL import Image
import torch
from torchvision import transforms
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Import model definition
from mushroom_cnn import MushroomCNN


def load_model(model_path: str, device: torch.device) -> torch.nn.Module:
    model = MushroomCNN(num_classes=2)
    state = torch.load(model_path, map_location=device)
    model.load_state_dict(state)
    model.to(device)
    model.eval()
    return model


def preprocess_image_b64(b64: str) -> torch.Tensor:
    if b64.startswith('data:image'):
        b64 = b64.split(',')[1]
    image_bytes = base64.b64decode(b64)
    image = Image.open(io.BytesIO(image_bytes)).convert('RGB')
    
    # Get image size from environment
    image_size = int(os.getenv('IMAGE_SIZE', '256'))
    
    transform = transforms.Compose([
        transforms.Resize((image_size, image_size)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])
    tensor = transform(image).unsqueeze(0)
    return tensor


def predict(model: torch.nn.Module, device: torch.device, image_tensor: torch.Tensor):
    with torch.no_grad():
        image_tensor = image_tensor.to(device)
        outputs = model(image_tensor)
        probs = torch.softmax(outputs, dim=1)[0]
        edible_prob = float(probs[0].item())
        poisonous_prob = float(probs[1].item())
        if edible_prob >= poisonous_prob:
            prediction = 'edible'
            confidence = edible_prob
        else:
            prediction = 'poisonous'
            confidence = poisonous_prob
        return {
            'prediction': prediction,
            'confidence': float(confidence),
            'probabilities': {
                'edible': edible_prob,
                'poisonous': poisonous_prob
            }
        }


def main():
    try:
        # Input: JSON on stdin { "image": base64, "model_path": "..." }
        raw = sys.stdin.read()
        payload = json.loads(raw)
        image_b64 = payload.get('image')
        model_path = payload.get('model_path', 'mushroom_cnn_model.pth')
        if not image_b64:
            raise ValueError('Missing image base64 in input JSON under key "image"')

        device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        if not os.path.exists(model_path):
            # Try relative to script directory
            script_dir = os.path.dirname(os.path.abspath(__file__))
            alt_path = os.path.join(script_dir, model_path)
            if os.path.exists(alt_path):
                model_path = alt_path

        model = load_model(model_path, device)
        tensor = preprocess_image_b64(image_b64)
        result = predict(model, device, tensor)
        out = { 'success': True, 'result': result }
        print(json.dumps(out))
    except Exception as e:
        err = { 'success': False, 'error': str(e) }
        print(json.dumps(err))
        sys.exit(1)


if __name__ == '__main__':
    main()



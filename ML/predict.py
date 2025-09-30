#!/usr/bin/env python3
import sys
import json
import base64
import io
import os
import warnings
from PIL import Image
import torch
from torchvision import transforms
from dotenv import load_dotenv

# Suppress all warnings to ensure clean JSON output
warnings.filterwarnings('ignore')

# Load environment variables
load_dotenv()

# Import model definition
from mushroom_cnn import MushroomResNet
import torch.nn as nn


class CustomCNN(nn.Module):
    """Custom CNN that matches the saved model architecture"""
    def __init__(self, num_classes=2):
        super(CustomCNN, self).__init__()
        
        # Convolutional layers
        self.conv1 = nn.Conv2d(3, 32, kernel_size=3, padding=1)
        self.bn1 = nn.BatchNorm2d(32)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3, padding=1)
        self.bn2 = nn.BatchNorm2d(64)
        self.conv3 = nn.Conv2d(64, 128, kernel_size=3, padding=1)
        self.bn3 = nn.BatchNorm2d(128)
        self.conv4 = nn.Conv2d(128, 256, kernel_size=3, padding=1)
        self.bn4 = nn.BatchNorm2d(256)
        
        # Pooling
        self.pool = nn.MaxPool2d(2, 2)
        
        # Fully connected layers
        self.fc1 = nn.Linear(256 * 16 * 16, 512)  # 256 * 16 * 16 = 65536
        self.fc2 = nn.Linear(512, 128)
        self.fc3 = nn.Linear(128, num_classes)
        
        # Activation
        self.relu = nn.ReLU()
        
    def forward(self, x):
        # Conv layers with pooling
        x = self.pool(self.relu(self.bn1(self.conv1(x))))  # 256x256 -> 128x128
        x = self.pool(self.relu(self.bn2(self.conv2(x))))  # 128x128 -> 64x64
        x = self.pool(self.relu(self.bn3(self.conv3(x))))  # 64x64 -> 32x32
        x = self.pool(self.relu(self.bn4(self.conv4(x))))  # 32x32 -> 16x16
        
        # Flatten
        x = x.view(x.size(0), -1)
        
        # Fully connected layers
        x = self.relu(self.fc1(x))
        x = self.relu(self.fc2(x))
        x = self.fc3(x)
        
        return x


def load_model(model_path: str, device: torch.device) -> torch.nn.Module:
    model = CustomCNN(num_classes=2)
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
    
    # Get image size from environment - use 256 to match training
    image_size = 256
    
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



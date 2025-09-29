from flask import Flask, request, jsonify
from flask_cors import CORS
import torch
import torch.nn as nn
from torchvision import transforms
from PIL import Image
import io
import base64
import numpy as np
from mushroom_cnn import MushroomCNN

app = Flask(__name__)
CORS(app)

# Load the trained model
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model = MushroomCNN(num_classes=2)
model.load_state_dict(torch.load('mushroom_cnn_model.pth', map_location=device))
model.to(device)
model.eval()

# Image preprocessing
transform = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

def preprocess_image(image_data):
    """Preprocess image for model prediction"""
    try:
        # Decode base64 image
        if image_data.startswith('data:image'):
            image_data = image_data.split(',')[1]
        
        image_bytes = base64.b64decode(image_data)
        image = Image.open(io.BytesIO(image_bytes)).convert('RGB')
        
        # Apply transforms
        image_tensor = transform(image).unsqueeze(0)
        return image_tensor
    except Exception as e:
        raise ValueError(f"Error preprocessing image: {str(e)}")

def predict_mushroom(image_tensor):
    """Predict mushroom class and confidence"""
    with torch.no_grad():
        image_tensor = image_tensor.to(device)
        outputs = model(image_tensor)
        probabilities = torch.softmax(outputs, dim=1)
        confidence, predicted = torch.max(probabilities, 1)
        
        # Get probabilities for both classes
        edible_prob = probabilities[0][0].item()
        poisonous_prob = probabilities[0][1].item()
        
        prediction = "edible" if predicted.item() == 0 else "poisonous"
        confidence_score = confidence.item()
        
        return {
            "prediction": prediction,
            "confidence": confidence_score,
            "probabilities": {
                "edible": edible_prob,
                "poisonous": poisonous_prob
            }
        }

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({"status": "healthy", "message": "Mushroom Vision ML API is running"})

@app.route('/predict', methods=['POST'])
def predict():
    """Predict mushroom classification from uploaded image"""
    try:
        data = request.get_json()
        
        if not data or 'image' not in data:
            return jsonify({"error": "No image data provided"}), 400
        
        # Preprocess image
        image_tensor = preprocess_image(data['image'])
        
        # Make prediction
        result = predict_mushroom(image_tensor)
        
        return jsonify({
            "success": True,
            "result": result
        })
        
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/predict-file', methods=['POST'])
def predict_file():
    """Predict mushroom classification from uploaded file"""
    try:
        if 'file' not in request.files:
            return jsonify({"error": "No file provided"}), 400
        
        file = request.files['file']
        if file.filename == '':
            return jsonify({"error": "No file selected"}), 400
        
        # Read and preprocess image
        image = Image.open(file.stream).convert('RGB')
        image_tensor = transform(image).unsqueeze(0)
        
        # Make prediction
        result = predict_mushroom(image_tensor)
        
        return jsonify({
            "success": True,
            "result": result
        })
        
    except Exception as e:
        return jsonify({"error": f"Error processing file: {str(e)}"}), 500

if __name__ == '__main__':
    print("Starting Mushroom Vision ML API...")
    print(f"Using device: {device}")
    app.run(host='0.0.0.0', port=5000, debug=True)

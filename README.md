Mushroom Vision

Full-stack edible vs. poisonous mushroom classification.

Project Structure

client/   React SPA
server/   Spring Boot API
ML/       PyTorch model, training, and predictor

Quick Start

1) ML setup and training
cd ML
python -m venv .venv && source .venv/bin/activate  # optional
pip install -r requirements.txt
python train_model.py  # produces mushroom_cnn_model.pth

2) Run Spring Boot backend
cd server
./mvnw spring-boot:run

3) Run React client
cd client
npm start

API

POST /api/classifications/classify body { imageData: base64, imageName: string }
Persists results to PostgreSQL and returns prediction + probabilities

Config

Set in server/src/main/resources/application.properties:
ml.python.path default python3
ml.predict.script.path default ${PROJECT_ROOT:}/ML/predict.py
ml.model.path default ${PROJECT_ROOT:}/ML/mushroom_cnn_model.pth
ml.process.timeout.ms default 15000

Notes

Accuracy may differ from resume values depending on environment.
Large dataset images are gitignored; keep them under ML/data/ locally.


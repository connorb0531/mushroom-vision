# Mushroom Vision

A full-stack project for uploading mushroom images and running ML predictions using PyTorch.

## Tech Stack
- Spring Boot (Java) – backend REST API
- React (Vite) – frontend uploader with drag-and-drop
- PyTorch (Python) – CNN model for mushroom classification
- PostgreSQL – database (optional)

## Project Structure
```
mushroom-vision/
├── client/          # React frontend
├── server/          # Spring Boot backend
├── ML/              # PyTorch ML models and training
│   ├── data/        # Dataset (gitignored)
│   ├── models/      # Trained models (gitignored)
│   ├── predict.py   # Inference script
│   └── train_model.py # Training script
└── training_history.png # Training metrics visualization
```

### API (Spring Boot)
- `POST /api/classifications/classify-file` – upload image and get classification
- `GET /api/classifications` – list all classifications
- `GET /api/classifications/{id}` – get specific classification

### Frontend (Vite + React + Tailwind)
- Drag-and-drop image upload with preview
- Real-time upload progress tracking
- Classification results display with confidence scores
- Responsive UI with Tailwind CSS

### ML (PyTorch)
- Custom CNN architecture for mushroom classification
- Real-time inference via Python subprocess
- Support for both edible and poisonous mushroom classification
- Training visualization with `training_history.png`

## Prerequisites
- Java 22
- Maven 3.9+
- PostgreSQL 12+ (local or remote, e.g. Render)
- Node.js 20+ and npm

---

## Server (Spring Boot) Setup


### Environment variables (.env)

Create a `.env` file inside `server` directory:

```
DB_URL=
DB_USERNAME=
DB_PASSWORD=
cors.allowed-origins=
```

### Build
```
cd ./server
./mvnw clean package -DskipTests
```

### Run 
```
cd ./server
./mvnw spring-boot:run
```


## Client (React + Vite + Tailwind) Setup

Create a `.env` file inside `client` directory:

```
VITE_API_BASE_URL=http://localhost:8080
```

### Install dependencies
```
cd ./client
npm install
npm i -D tailwindcss @tailwindcss/postcss postcss autoprefixer
npm install @iconify/react @iconify-icons/logos
```

### Run
```
cd ./client
npm run dev
```

## ML (PyTorch)
### Dataset
- Download mushroom dataset from [[link](https://www.kaggle.com/datasets/daniilonishchenko/mushrooms-images-classification-215)]
- Extract into `ML/data/raw/`
- Run `ML/train_model.py` to train the model
- Training progress is saved as `training_history.png`

### Install dependencies
```bash
cd ML
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

### Training
```bash
cd ML
python train_model.py
```

### Model Architecture
- Custom CNN with convolutional layers, batch normalization, and dropout
- Input: 256x256 RGB images
- Output: Binary classification (edible/poisonous)
- Training visualization: `training_history.png`

### Training Visualization
The `training_history.png` file contains:
- Training and validation accuracy curves
- Training and validation loss curves
- Model performance metrics over epochs
- Helps visualize model convergence and overfitting

![Training History](training_history.png)

## Quick Start

1) ML setup and training
```bash
cd ML
python -m venv .venv && source .venv/bin/activate  # optional
pip install -r requirements.txt
python train_model.py  # produces mushroom_cnn_model.pth
```

2) Run Spring Boot backend
```bash
cd server
./mvnw spring-boot:run
```

3) Run React client
```bash
cd client
npm start
```

## POST mushroom data to DB (if needed)

```
curl -X POST "{BACKEND_URL}/api/mushroom/ingest?upsert=true" \
  -H "Content-Type: application/json" \
  --data-binary @{PATH_TO_JSON_FILE}
```
# Mushroom Vision

A full-stack project for uploading mushroom images and running ML predictions.

## Tech Stack
- Spring Boot (Java) – backend REST API
- React (Vite) – frontend uploader
- ML worker (Python, TensorFlow) – prediction service
- PostgreSQL – database

### API (Spring Boot)
- `POST /api/images` – create an image record
- `GET /api/images` / `/:id` – list or fetch image + prediction
- `POST /api/predictions` – attach model result
- `POST /api/feedback` – user feedback on a prediction

### Frontend (Vite + React + Tailwind)
- Image Upload page (file/URL), shows prediction result
- Simple, responsive UI; configurable API base URL

### ML (TensorFlow) — planned
- Separate FastAPI service for `/predict`
- Confidence thresholds + abstain on low confidence

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

## ML
### Dataset
- Download mushroom dataset from [[link](https://www.kaggle.com/datasets/daniilonishchenko/mushrooms-images-classification-215)]
- Extract into `ML/data/raw/`
- remove `data/data`
- Run `src/data/preprocess.py` to resize/normalize

### Install dependencies
```
pip install opencv-python
```
#### For TensorFlow:
- cpu version
```
pip install tensorflow #cpu
```
- gpu version
```
pip install tensorflow[and-cuda] 


```

## POST mushroom data to DB (if needed)

```
curl -X POST "{BACKEND_URL}/api/mushroom/ingest?upsert=true" \
  -H "Content-Type: application/json" \
  --data-binary @{PATH_TO_JSON_FILE}
```

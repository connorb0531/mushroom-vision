# Mushroom Vision — Dev Setup

## Prerequisites
- Java 22
- Maven 3.9+
- PostgreSQL 12+ (local or remote, e.g. Render)
- Node.js 20+ and npm

---

## Server (Spring Boot)


### Environment variables (.env)

Create a `.env` file inside `server` directory:

```
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```
### Build
```cd ./server
./mvnw clean package -DskipTests
```

### Run 
```
cd ./server
./mvnw spring-boot:run
```


## Client (React + Vite)

Create a `.env` file inside `client` directory:

```
VITE_API_BASE_URL=http://localhost:8080
```

### Install dependencies
```
cd ./client
npm install
```

## ML
### Dataset
- Download mushroom dataset from [[link](https://www.kaggle.com/datasets/daniilonishchenko/mushrooms-images-classification-215)]
- Extract into `ML/data/raw/`
- Run `src/data/preprocess.py` to resize/normalize

## POST mushroom data to DB (if needed)

```
curl -X POST "{BACKEND_URL}/api/mushroom/ingest?upsert=true" \
  -H "Content-Type: application/json" \
  --data-binary @{PATH_TO_JSON_FILE}
```


### Run dev server
```
cd ./client
npm run dev
```

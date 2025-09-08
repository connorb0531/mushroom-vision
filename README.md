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

### Run dev server
```
cd ./client
npm run dev
```

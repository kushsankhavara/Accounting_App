# Home Accounts App

A minimal home accounts/expense manager with a Spring Boot backend and React (Vite) frontend.

## Backend

* Stack: Spring Boot 3, Java 17.
* Endpoints under `/api` support transaction create/list, monthly summary, category breakdown, and CSV export.

### Running

```bash
cd backend
mvn spring-boot:run
```

## Frontend

* Stack: React 18 + Vite.
* Connects to the backend at `http://localhost:8080/api`.

### Running

```bash
cd frontend
npm install
npm run dev
```

## Tests

Run backend unit tests:

```bash
cd backend
mvn test
```

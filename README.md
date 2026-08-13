# Smart Warehouse — Inventory & Logistics Management Platform

[![CI](https://github.com/mohamedsaadaoui/Smart-Warehouse/actions/workflows/ci.yml/badge.svg)](https://github.com/mohamedsaadaoui/Smart-Warehouse/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)

Smart Warehouse is a full-stack warehouse and inventory management platform that replaces manual (spreadsheet) stock tracking with a real-time system: multi-warehouse stock movements, low-stock alerts, multi-supplier management, and an analytics dashboard — secured with JWT and role-based access control.

## Tech Stack

### Backend

| Layer | Technology |
| ----- | ---------- |
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (RBAC: `ADMIN` / `MANAGER` / `EMPLOYEE`) |
| Persistence | Spring Data JPA, PostgreSQL |
| Real-time | WebSocket / STOMP |
| Mapping | MapStruct, Lombok |
| Validation | Spring Boot Validation |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Tests | JUnit 5, Mockito, Testcontainers, JaCoCo coverage |

### Frontend

- React 19, Vite, TypeScript
- Material UI (MUI 6), React Hook Form + Zod
- TanStack Query-style API layer with Axios interceptors (JWT injection)
- STOMP (via `@stomp/stompjs`) + SockJS for real-time notifications
- Recharts dashboard, React Router with protected/admin routes

## Features

- **Authentication & Authorization** — register/login with JWT; roles `ADMIN`, `MANAGER`, `EMPLOYEE`
- **Product & Category management** — CRUD, stock status, low-stock detection
- **Multi-warehouse inventory** — stock entries, exits and inter-site movements with `MovementType`
- **Suppliers** — multi-supplier management
- **Dashboard** — counts by status/category, low-stock list, recent movements, monthly stats
- **Reports** — CSV export of stock/inventory data
- **Notifications** — in-app notifications pushed in real time via WebSocket (including user-to-user)
- **Audit log** — records create/update/delete/login/register actions (ADMIN)
- **User management** — admin-only user CRUD with role assignment
- **Settings** — configurable application settings (ADMIN)

## Getting Started

### Prerequisites

- JDK 21
- Node.js 20+
- Docker (for PostgreSQL)

### 1. Start the database

```bash
docker compose up -d
```

### 2. Run the backend

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` (Swagger UI: http://localhost:8080/swagger-ui.html).

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173` and proxies `/api` and `/ws` to the backend.

On first startup, `DataInitializer` seeds the `ADMIN`, `MANAGER` and `EMPLOYEE` roles.

## API Overview

| Module | Endpoint | Access |
| ------ | -------- | ------ |
| Auth | `/api/auth/register`, `/api/auth/login` | Public |
| Products | `/api/products/**` | Authenticated |
| Categories | `/api/categories/**` | Authenticated |
| Movements | `/api/movements/**` | Authenticated |
| Suppliers | `/api/suppliers/**` | Authenticated |
| Dashboard | `/api/dashboard/**` | Authenticated |
| Reports | `/api/reports/**` (CSV export) | Authenticated |
| Notifications | `/api/notifications/**` | Authenticated |
| Settings | `/api/settings/**` | ADMIN |
| Users | `/api/users/**` | ADMIN |
| Audit logs | `/api/audit-logs/**` | ADMIN |

## Project Structure

```
com.saadaoui.smartwarehouse
├── auth          # JWT, security config, authentication, user service
├── category      # product categories
├── dashboard     # summary statistics
├── movement      # stock movements (in / out / transfer)
├── notification  # notifications + real-time WebSocket push
├── product       # products and stock status
├── report        # CSV reports
├── settings      # application settings
├── supplier      # suppliers
├── user          # user management (ADMIN)
├── audit         # audit logging
├── websocket     # STOMP configuration
├── entity        # JPA entities
└── exception     # centralized error handling
```

## Testing

The backend follows a testing pyramid:

- **Unit tests** — services with mocked repositories (JUnit 5 + Mockito)
- **Integration tests** — real PostgreSQL via **Testcontainers**, exercising JWT-secured API flows

Run the full suite with coverage:

```bash
./mvnw verify
```

Coverage is enforced with **JaCoCo** (≥70% instruction coverage gate) and the report is generated at `target/site/jacoco/index.html`.

## CI/CD

`.github/workflows/ci.yml` runs on every push/PR to `main`:

- Backend: `mvnw verify` (tests + Testcontainers + JaCoCo gate)
- Frontend: `npm ci && npm run build`
- Docker: builds the backend image and pushes it to GitHub Container Registry

## License

Smart Warehouse is open-source under the MIT License.

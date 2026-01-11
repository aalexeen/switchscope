# Repository Guidelines

## Project Structure & Module Organization
- `backend/` is the Spring Boot app: code in `backend/src/main/java`, config in `backend/src/main/resources`, Liquibase under `backend/src/main/resources/db`.
- `backend/src/test/java` holds JUnit/Spring tests.
- `frontend/` is the Vue 3 app: source in `frontend/src`, static assets in `frontend/public`, table configs in `frontend/src/configs/tables`.

## Build, Test, and Development Commands
- `cd backend && ./mvnw spring-boot:run` starts the API on port 8090.
- `cd backend && ./mvnw test` unit tests; `./mvnw integration-test` integration tests; `./mvnw verify` full backend suite.
- `cd backend && ./mvnw liquibase:update` applies migrations; `./mvnw liquibase:status` shows status.
- `python3 validate_schema.py` validates schema vs Liquibase.
- `cd frontend && npm install` installs deps.
- `cd frontend && npm run dev` starts the UI on port 3001; `npm run build` builds; `npm run preview` serves the build.
- `cd frontend && npm run lint` runs ESLint fixes.

## Coding Style & Naming Conventions
- Backend follows Google Java Style and Checkstyle. Use `PascalCase` classes, `camelCase` fields/methods, lowercase packages.
- Frontend uses ESLint (`frontend/eslint.config.js`). Keep existing naming patterns like `*.config.js` under `frontend/src/configs/tables`.
- Keep indentation consistent with the surrounding file and avoid changing existing Liquibase changesets.

## Testing Guidelines
- Backend tests use JUnit Jupiter and Spring Boot Test. Place tests in `backend/src/test/java` and align names with the class under test (e.g., `FooServiceTest`).
- No dedicated frontend test runner is configured yet; rely on `npm run lint` and manual verification.

## Commit & Pull Request Guidelines
- Recent history mixes `fix(frontend): ...`, `style(frontend): ...`, `refactor(frontend): ...`, plus `Refactor:`/`Update:`. Prefer `type(scope): short imperative summary`.
- PRs should include a clear description, testing notes (commands run), and screenshots or short clips for UI changes.

## Configuration & Local Setup
- Backend overrides live in `backend/src/main/resources/application-local.yaml` (gitignored). `config.properties` can be copied from `config.properties.example`.
- `.local.props` is also gitignored for local DB config. Do not commit secrets.
- Frontend uses API base URL `http://localhost:8090/api/`.

## Architecture & Pattern Rules
- Backend uses layered flow (Entity → Repository → Service → Controller) with DTOs and MapStruct (`BaseMapper` + `MapStructConfig`).
- All entities extend `BaseEntity` (UUID v7 + timestamps). Component hierarchy uses Single Table Inheritance; discriminator values must match CSV seed data exactly.
- Catalog pattern separates models/catalogs from instances; add new catalogs through Liquibase and CSVs.
- Liquibase: never edit existing changesets; add new ones, keep init before fill, CSV separator `;` and quote `'`.
- Frontend tables are configuration-driven: only `GenericTableView.vue` renders tables, configs live in `frontend/src/configs/tables` and `tableRegistry.js`.
- Frontend API modules must be factory functions (receive axios instance) and return axios responses; composables extract `.data`.
- Shared frontend data uses singleton composables (state declared outside). Delete functions are singular (`deleteEntity`).
- Routes for protected views must include `meta: { requiresAuth: true, roles: [...], tableKey: '...' }` and follow existing naming conventions.

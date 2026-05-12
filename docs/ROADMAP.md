# Smart Vacation Planner — Roadmap (Backend Interview Prep)

## North star (why we’re building this)
- **Primary goal**: become job-ready for a **backend** position by using this project as a vehicle to demonstrate **clean architecture**, **API design**, **validation**, **persistence**, **security**, **testing**, and **incremental delivery**.
- **Project goal**: build a Smart Vacation Planner backend **without AI at the beginning**, and later add **AI-assisted itinerary generation** on top of a solid domain model and API.

We intentionally start with a thin but correct backend slice, then grow the project through domain modeling, validation, testing, security, and production-oriented improvements. **AI is added only after the backend foundation is strong**.

## How we’ll work (study loop)
Each sprint ends with:
- API works end-to-end (controller → service → DB)
- Consistent error responses (global exception handler)
- A small set of tests (unit and/or integration)
- Short “interviewer-style” review: what’s good, what to improve, and why

## Scope strategy (thin now, grow later)
We’ll start with a **thin slice** that is simple but correct, then add “interview skills” (security, pagination, testing depth, production practices) in later MVPs.

## Domain (MVP)
From `[docs/PROJECT_SPEC.md](docs/PROJECT_SPEC.md)`:
- **Vacation** (trip container)
- **VacationDay** (days inside a vacation)
- **Activity** (items inside a day)

Small enhancement for interview-signal:
- **Vacation status** (enum): `PLANNED`, `IN_PROGRESS`, `COMPLETED` (filtering + business rules opportunities)

## Roadmap

### Sprint 0 — Project setup
**Goal**: stabilize the project foundation before building business features.
- Package structure (clear layers: controller/service/repository/dto/exception)
- Application configuration (profiles, ports, basic app properties)
- Database connection (local dev DB works reliably)
- Initial schema setup / migration approach (start simple; introduce migrations when ready)
- Basic health check endpoint
- First entity/repository skeleton (minimal, compilable)

**Definition of done**
- App starts successfully
- DB connection works
- Project structure is ready for feature development

### Sprint 1 — Backend foundation (thin slice first)
**Goal**: a clean Vacation API that does not expose entities and returns predictable errors.

**Thin slice (do first)**
- Add `/api/v1` base path for Vacation endpoints
- Introduce DTOs for Vacation:
  - `CreateVacationRequest`, `UpdateVacationRequest`, `VacationResponse`
- DTO ↔ entity mapping is **manual at first** (for learning and interview readiness). Consider MapStruct later if/when mapping grows.
- Add validation:
  - `name`, `country`, `city`, `travelerType`: `@NotBlank`
  - `startDate`, `endDate`: `@NotNull`
- Business validation (domain rules):
  - Vacation `endDate` must be on or after `startDate`
- Controller returns `VacationResponse` (not the entity)

**Then (complete Sprint 1)**
- Global exception handling:
  - one consistent API error shape
  - validation errors return 400 with field error list
- Testing:
  - Service-layer unit tests for business rules (fast feedback)
  - Integration test: controller + DB flow (create vacation → fetch vacation)

Business validation examples we will model as we expand the domain:
- VacationDay date must be within the parent Vacation date range
- Activity cannot be added to a VacationDay that does not belong to the expected Vacation context
- Optional: avoid duplicate activities with the same name in the same day

**Definition of done**
- Validation errors return 400
- 404s are meaningful (resource not found)
- No JPA entities returned directly from controllers
- App compiles and starts

### Sprint 2 — VacationDay (relations + nested resources)
**Goal**: `VacationDay` attached to a Vacation with domain integrity.
- Model relation: Vacation 1..n VacationDay
- Domain integrity / business validation:
  - enforce that a VacationDay belongs to an existing Vacation
  - enforce that the day date is within the parent Vacation boundaries
- Endpoints (example shape):
  - `POST /api/v1/vacations/{vacationId}/days`
  - `GET /api/v1/vacations/{vacationId}/days`
- Tests:
  - Unit tests for date-boundary rules
  - Integration test: “add day then list days”

### Sprint 3 — Activity (nested resources + validation)
**Goal**: Activities attached to a VacationDay with domain consistency checks (not just CRUD).
- Model relation: VacationDay 1..n Activity
- Domain integrity / business validation:
  - enforce that an Activity belongs to an existing VacationDay
  - prevent cross-context writes (activity must be added under the correct Vacation/VacationDay relationship)
- Endpoints (example shape):
  - `POST /api/v1/vacation-days/{dayId}/activities`
  - `GET /api/v1/vacation-days/{dayId}/activities`
- Validation:
  - `durationMinutes > 0`
  - `name` not blank

### Sprint 4 — Listing, filtering, pagination (real-world API)
**Goal**: make the API feel “production-shaped”.
- Pagination/sorting for list endpoints
- Filtering vacations (country/city/date range)
- Keep error responses consistent

### Sprint 5 — Security v1 (simple auth now)
**Goal**: introduce Spring Security in the simplest workable way.
- Pick an initial auth mechanism (simple now, upgrade later)
- Protect write endpoints at minimum
- Add tests for 401/403 paths

### Sprint 6 — Users + ownership (security v2)
**Goal**: multi-user correctness.
- Add `User` model (minimal)
- Vacation is owned by a user
- Enforce ownership in read/write paths

### Sprint 7 — Auth hardening (security v3)
**Goal**: level-up auth toward production expectations.
- Strong password hashing (BCrypt)
- Roles (USER/ADMIN) if needed
- If JWT: refresh tokens + rotation policy (documented + tested)

### Sprint 8 — Testing depth (confidence + interview signal)
**Goal**: demonstrate engineering maturity and confidence in refactoring.
- Unit tests for services (business rules)
- Integration tests for controllers
- Optional: Testcontainers for DB realism

### Sprint 9 — Documentation & developer experience
**Goal**: make it easy to run and understand.
- OpenAPI examples and error schemas
- README “how to run” + curl examples

### Sprint 10 — Production practices (lite)
**Goal**: correctness/performance basics.
- Logging basics (structured, useful messages)
- Transaction boundaries (correctness under concurrent usage)
- Avoid N+1 problems where relevant
- Consistent API contracts (status codes, error shape, response DTO stability)
- Input normalization

## AI phase (planned now, implemented later)
AI is intentionally postponed until backend fundamentals are complete: **DTOs**, **validation**, **exception handling**, **relationships**, **security**, **ownership**, and **testing**.

### AI Sprint A — Define AI contract + constraints
**Goal**: define what AI must output and what rules it must obey.
- Input: vacation + preferences
- Output schema: days + activities (validated)
- Guardrails: schema validation + safe fallback

### AI Sprint B — Deterministic recommendations first (no LLM)
**Goal**: plug-in architecture and testable behavior.
- `RecommendationService` interface
- Rules-based baseline implementation

### AI Sprint C — LLM-backed itinerary generation
**Goal**: first AI-assisted itinerary generation.
- Prompting/tooling approach documented
- Retries + validation + “never break constraints”
- Golden test cases for evaluation


# Smart Vacation Planner — Roadmap (Backend Interview Prep)

## North star

### Primary goal

Become job-ready for a backend software engineering role by using this project as a practical vehicle to demonstrate:

* clean layered architecture
* REST API design
* DTO validation
* persistence with JPA/Hibernate and MySQL
* external API integration
* Spring Security and ownership checks
* global error handling
* testing and incremental delivery
* clear technical decision-making

### Product goal

Build a Smart Vacation Planner backend that lets a user:


1. Create or load a vacation.
2. Search and manage a destination catalog of points of interest.
3. Select relevant points of interest for the vacation.
4. Create vacation days and assign selected points of interest to each day.
5. Generate a scheduled itinerary based on the selected day activities.
6. Generate a scheduled itinerary based on business rules such as opening hours, duration, distance, and travel time.
7. Later, add AI-assisted planning on top of the existing deterministic backend.

AI is intentionally not the foundation of the project. The backend domain model and API should be useful and testable before introducing LLM-based behavior.

---

## Current product direction

The project started with a simple `Vacation -> VacationDay -> Activity` model, but the domain has evolved.

The current model separates between:

* `PointOfInterest` — a reusable catalog item, such as a museum, viewpoint, restaurant, attraction, cafe, shopping area, hike, or transport point.
* `VacationDayActivity` — a user-selected point of interest attached to a specific vacation day.
* `Itinerary` — a generated schedule based on selected activities.

This is more realistic than storing activities directly under a vacation day, because the same point of interest can exist in the city catalog and be selected by different users or different days.

---

## Current architecture

The backend is organized into clear layers:

```text
controller
dto
entity
repository
service
service.itinerary
integration.google
security
exception
common.place
config
```

### Main responsibilities by layer

| Layer              | Responsibility                                                                   |
| ------------------ | -------------------------------------------------------------------------------- |
| Controller         | Exposes REST endpoints and maps entities to response DTOs                        |
| DTO                | Defines request/response contracts and validation rules                          |
| Entity             | JPA/Hibernate persistence model                                                  |
| Repository         | Spring Data JPA data access                                                      |
| Service            | Business logic, validation, ownership checks, external integration orchestration |
| service.itinerary  | Deterministic itinerary generation logic                                         |
| integration.google | Google Places Text Search integration                                            |
| security           | Basic Auth, roles, CORS, endpoint authorization                                  |
| exception          | Global error handling and consistent error responses                             |
| common.place       | Shared embedded location model                                                   |

---

## Current domain model

### User

Represents an application user.

Key fields:

* `id`
* `firstName`
* `lastName`
* `email`
* `password`
* `role`
* `active`

Relationships:

* One user has many vacations.

Roles:

* `CUSTOMER`
* `ADMIN`

---

### Vacation

Represents a vacation/trip container.

Key fields:

* `id`
* `user`
* `name`
* `country`
* `city`
* `startDate`
* `endDate`
* `travelerType`
* `budget`
* `pace`

Relationships:

* Many vacations belong to one user.
* One vacation has many vacation days.

Business rules:

* `endDate` must be on or after `startDate`.
* A regular customer can only access their own vacations.
* An admin can access all vacations.

---

### VacationDay

Represents a specific day inside a vacation.

Key fields:

* `id`
* `vacation`
* `date`
* `dayNumber`
* `dayType`
* `hotelPlace`

Relationships:

* Many vacation days belong to one vacation.
* One vacation day has many selected activities.

Business rules:

* The day must belong to an existing vacation.
* The day date must be inside the vacation date range.
* The day number must be positive.
* The day number must not exceed the vacation duration.

Notes:

* `hotelPlace` is an embedded `Place`.
* Hotel place data is enriched through Google Places.

---

### Place

A reusable embedded value object for location data.

Fields:

* `placeName`
* `placeId`
* `formattedAddress`
* `city`
* `country`
* `latitude`
* `longitude`

Used by:

* `PointOfInterest`
* `VacationDay.hotelPlace`

---

### PointOfInterest

Represents a reusable catalog item in a destination.

Key fields:

* `id`
* `name`
* `pointOfInterestCategory`
* `place`
* `durationMinutes`
* `openingTime`
* `closingTime`
* `minimumAge`
* `notes`

Categories:

* `TOURIST_ATTRACTION`
* `MUSEUM`
* `VIEWPOINT`
* `RESTAURANT`
* `CAFE`
* `HIKE`
* `SHOPPING`
* `TRANSPORT`
* `OTHER`

Purpose:

* Stores city-level points of interest.
* Used as selectable options when building a vacation day.
* Enriched through Google Places when created or updated.

---

### VacationDayActivity

Represents a selected point of interest attached to a specific vacation day.

Key fields:

* `id`
* `vacationDay`
* `pointOfInterest`
* `plannedStartTime`
* `plannedEndTime`
* `travelMinutesFromPrevious`
* `distanceKmFromPrevious`

Purpose:

* Represents the user’s selected activities for a day.
* Scheduling fields are filled when an itinerary is generated.
* This entity connects the reusable catalog to a user-specific vacation plan.

Important distinction:

* `PointOfInterest` is the catalog item.
* `VacationDayActivity` is the user’s selected item for a specific day.
* `ScheduledActivityResponse` is the generated itinerary output.

---

## Current API design

### Vacations

```http
GET    /api/v1/vacations
GET    /api/v1/vacations/page
GET    /api/v1/vacations/{id}
POST   /api/v1/vacations
PUT    /api/v1/vacations/{id}
PATCH  /api/v1/vacations/{id}
DELETE /api/v1/vacations/{id}
```

### Vacation days

```http
GET    /api/v1/vacations/{vacationId}/days
GET    /api/v1/vacations/{vacationId}/days/page
GET    /api/v1/vacations/{vacationId}/days/{id}
POST   /api/v1/vacations/{vacationId}/days
PUT    /api/v1/vacations/{vacationId}/days/{id}
PATCH  /api/v1/vacations/{vacationId}/days/{id}
DELETE /api/v1/vacations/{vacationId}/days/{id}
```

### Points of interest

```http
GET    /api/v1/points-of-interest
GET    /api/v1/points-of-interest/search
GET    /api/v1/points-of-interest/{id}
POST   /api/v1/points-of-interest
PUT    /api/v1/points-of-interest/{id}
DELETE /api/v1/points-of-interest/{id}
```

### Vacation day activities

Target API shape:

```http
GET    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities
GET    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
POST   /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities
PUT    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
```

Preferred create request:

```json
{
  "pointOfInterestId": 12
}
```

Reasoning:

* The new resource being created is a vacation day activity.
* The selected point of interest is input data used to create the relationship.
* Keeping `pointOfInterestId` in the body makes the endpoint easier to extend later.

Potential future request fields:

* `preferredStartTime`
* `priority`
* `mustHave`
* `userNotes`

### Itinerary

```http
POST /api/v1/vacations/{vacationId}/itineraries
```

Purpose:

* Generate or regenerate the schedule for a vacation.
* Uses selected vacation day activities.
* Updates planned scheduling fields on `VacationDayActivity`.
* Returns an `ItineraryResponse`.

---

## Current backend capabilities

### Completed

#### Project structure

* Layered package structure exists.
* Controllers, DTOs, entities, repositories, services, exception handling, security, and integration code are separated.

#### Vacation API

* Create, read, update, patch, delete.
* Search with filters and pagination.
* DTO-based request and response.
* Business validation for date range.
* User ownership is enforced in service layer.

#### VacationDay API

* Nested under Vacation.
* Create, read, update, patch, delete.
* Search with filters and pagination.
* Hotel place enrichment through Google Places.
* Business validation for vacation date boundaries and day number.

#### PointOfInterest API

* Catalog resource.
* Create, read, update, delete.
* Search by name, category, place, city, country, duration, times, minimum age, and notes.
* Place enrichment through Google Places.

#### VacationDayActivity API

* Connects selected POIs to vacation days.
* Ownership enforced through vacation and vacation day context.
* Supports selected POIs before itinerary generation.

#### Google Places integration

* Uses Google Places Text Search.
* Sends `textQuery`.
* Uses field mask for selected fields.
* Extracts:

  * place ID
  * formatted address
  * latitude
  * longitude
  * city
  * country

#### Security

* Spring Security is configured.
* Basic Auth is used for now.
* Users are loaded from the database with `JdbcUserDetailsManager`.
* Login is based on email.
* Roles are loaded from the users table.
* CORS allows the local Vite frontend.
* CSRF is disabled for the REST API.
* Endpoint-level role checks exist.
* Ownership checks exist in the service layer.

#### Global error handling

* Validation errors return a consistent error response.
* Resource-not-found errors return 404.
* Illegal arguments return 400.
* Access denied returns 403.
* Fallback errors return 500 and are logged.

#### Itinerary generation

* Deterministic itinerary generation exists.
* The algorithm:

  * starts each day from the hotel
  * starts at 09:00
  * ends at 18:00
  * estimates distance between places
  * estimates walking or driving travel time
  * checks opening and closing time constraints
  * checks whether activity fits before day end
  * chooses the nearest schedulable candidate
  * saves planned start/end times, distance, and travel minutes
  * returns a structured itinerary response

---

## Current study loop

Each meaningful sprint should end with:

* API works end-to-end
* Manual Swagger/Postman flow is verified
* Consistent error responses are preserved
* Ownership/security behavior is checked
* At least one small test or documented manual test is added
* Short interviewer-style review is written:

  * what changed
  * what design decision was made
  * what tradeoff exists
  * what should improve next

---

## Roadmap from here

## Sprint 1 — Cleanup and API consistency

### Goal

Make the current API clean, consistent, and easier to explain.

### Tasks

* Ensure all resource paths use lowercase and hyphen style where needed.
* Keep `/api/v1/points-of-interest`.
* Prefer `/activities` under vacation day instead of `/vacationDayActivities`.
* Move `pointOfInterestId` to request body when creating or updating a vacation day activity.
* Add `CreateVacationDayActivityRequest`.
* Optional: add `UpdateVacationDayActivityRequest`.
* Keep `VacationDayActivityResponse` focused on selected POI data.
* Keep scheduled time fields in itinerary responses.
* Review security matchers and make sure paths match controllers exactly.
* Review DTO naming and package naming consistency.
* Remove stale comments referring to old `Activity` model.

### Definition of done

* All endpoints compile and run.
* Security paths match controller paths.
* Swagger displays clean endpoint names.
* The difference between `PointOfInterest`, `VacationDayActivity`, and `ScheduledActivityResponse` is clear.

---

## Sprint 2 — Manual end-to-end QA

### Goal

Verify the full product flow before adding more features.

### Manual flow

1. Create or verify a user in the database.
2. Authenticate with Basic Auth.
3. Create a vacation.
4. Create vacation days.
5. Create points of interest.
6. Search points of interest by city/country.
7. Add selected POIs to vacation days.
8. Generate itinerary.
9. Verify itinerary response.
10. Verify planned fields were saved in the database.
11. Verify that a different customer cannot access another user’s vacation.
12. Verify that admin access works as expected.

### Edge cases to check

* Vacation end date before start date.
* Vacation day outside vacation date range.
* Day number greater than vacation duration.
* Missing required fields.
* Invalid point of interest ID.
* Empty Google Places search query.
* Google Places returns no result.
* Delete vacation day with existing activities.
* Delete vacation with existing days.

### Definition of done

* A full demo flow works.
* Known bugs are documented.
* Errors are predictable.
* Ownership checks work.

---

## Sprint 3 — Delete behavior and data integrity

### Goal

Make deletion behavior intentional and safe.

### Decisions to make

* Should deleting a vacation delete its vacation days?
* Should deleting a vacation day delete its selected activities?
* Should deleting a selected activity delete the point of interest?

Preferred behavior:

* Deleting a vacation deletes its vacation days.
* Deleting a vacation day deletes its vacation day activities.
* Deleting a vacation day activity does not delete the point of interest.
* Deleting a point of interest should be restricted if it is already used, or handled intentionally.

### Tasks

* Decide between manual child deletion in services or JPA cascade/orphan removal.
* Do not cascade delete from `VacationDayActivity` to `PointOfInterest`.
* Add manual tests for delete flows.
* Add integration tests later.

### Definition of done

* No unexpected foreign key errors.
* No accidental deletion of catalog POIs.
* Delete behavior is documented.

---

## Sprint 4 — Testing foundation

### Goal

Add confidence before larger refactors.

### Unit tests

Recommended service tests:

* `VacationServiceImpl`

  * rejects end date before start date
  * assigns current user on create
  * returns only current user vacations for customer
  * returns all vacations for admin
* `VacationDayServiceImpl`

  * rejects date before vacation start
  * rejects date after vacation end
  * rejects day number greater than vacation duration
* `AuthorizationService`

  * allows owner
  * allows admin
  * rejects non-owner
* `ItineraryServiceImpl`

  * schedules activity inside opening hours
  * skips activity that cannot fit
  * chooses nearest schedulable candidate

### Integration tests

Recommended flows:

* create vacation then fetch it
* create vacation day under vacation
* create POI with Google client mocked
* add POI to vacation day
* generate itinerary

### Definition of done

* Core business rules have tests.
* At least one controller + DB flow is covered.
* Google Places integration is mockable in tests.

---

## Sprint 5 — Frontend alignment

### Goal

Align the React frontend with the current backend flow.

### Target user flow

1. Create or load vacation.
2. Search points of interest by destination.
3. Display destination POIs on the map.
4. Select relevant POIs for the vacation.
5. Create or manage vacation days.
6. Assign selected POIs to specific days.
7. Generate itinerary.
8. Display scheduled itinerary.
9. Show selected/scheduled places on the map.

### Frontend API updates

* Use `/api/v1/points-of-interest`.
* Use nested vacation day activities endpoint.
* Send `pointOfInterestId` in request body.
* Use Basic Auth for protected requests.
* Separate selected activities UI from generated itinerary UI.

### Definition of done

* User can complete the flow from the UI.
* The UI does not rely on mock data for the main flow.
* Map and itinerary reflect backend data.

---

## Sprint 6 — Itinerary algorithm improvements

### Goal

Improve the scheduling logic while keeping it deterministic and testable.

### Possible improvements

* Use vacation pace:

  * `RELAXED`
  * `BALANCED`
  * `INTENSE`
* Use day type:

  * `DAY`
  * `NIGHT`
  * `HALF_DAY`
* Allow day start/end times per vacation day.
* Add lunch/dinner logic.
* Add rest buffers.
* Add max activities per day.
* Prioritize must-have activities.
* Support user preferences.
* Improve travel mode logic.
* Use real travel time later through a maps/directions API if needed.

### Definition of done

* Algorithm remains explainable.
* Rules are tested.
* Itinerary output remains stable and understandable.

---

## Sprint 7 — Security hardening

### Goal

Make authentication and authorization more production-shaped.

### Tasks

* Add BCrypt password hashing if not already active.
* Avoid logging or exposing passwords.
* Ensure `User.toString()` does not print password.
* Verify 401 and 403 behavior.
* Consider JWT only after the current Basic Auth flow is stable.
* Document security tradeoffs:

  * Basic Auth is acceptable for learning and local demo.
  * JWT/session-based auth may be added later.

### Definition of done

* Passwords are not exposed.
* Ownership checks are tested.
* Security behavior is documented.

---

## Sprint 8 — Documentation and developer experience

### Goal

Make the project easy to understand and run.

### Tasks

* Update `README.md`.
* Add setup instructions:

  * Java version
  * MySQL setup
  * required environment variables
  * Google API key configuration
  * how to run backend
  * how to run frontend
* Add Swagger URL.
* Add sample API flow.
* Add sample JSON requests.
* Add known limitations.
* Add architecture overview.
* Add project screenshots later.

### Definition of done

* A reviewer can run the project.
* A recruiter/interviewer can understand the project quickly.
* The README explains why the project is backend-relevant.

---

## Sprint 9 — Production practices lite

### Goal

Add practical backend maturity without overengineering.

### Possible tasks

* Add transaction boundaries where needed.
* Review N+1 query risks.
* Improve logging.
* Normalize input.
* Improve pagination defaults.
* Return correct HTTP status codes:

  * 201 for create
  * 204 for delete
  * 400 for validation/business errors
  * 401 for unauthenticated
  * 403 for unauthorized
  * 404 for not found
* Improve error response shape if needed.
* Consider DB migrations with Flyway or Liquibase.

### Definition of done

* API behavior is more predictable.
* Important technical debt is documented.
* The backend is easier to present as production-aware.

---

## AI phase

AI should be added only after the deterministic backend flow is stable.

### AI Sprint A — AI contract design

Goal:
Define exactly what the AI is allowed to produce.

Tasks:

* Define user preferences input.
* Define itinerary output schema.
* Decide where AI fits:

  * preference collection
  * POI recommendation
  * itinerary explanation
  * itinerary generation
* Add validation around AI output.
* Add fallback behavior.

Definition of done:

* AI has a clear contract.
* AI cannot bypass backend business rules.

---

### AI Sprint B — Recommendation abstraction

Goal:
Create a replaceable recommendation layer.

Tasks:

* Add `RecommendationService` interface.
* Add deterministic baseline implementation.
* Add tests for recommendation behavior.
* Keep LLM implementation behind the same interface.

Definition of done:

* The project can recommend POIs without LLM.
* LLM can be added later without rewriting the core flow.

---

### AI Sprint C — LLM-assisted planning

Goal:
Use an LLM to assist planning while preserving backend constraints.

Possible behaviors:

* Ask follow-up questions.
* Suggest POIs based on preferences.
* Explain itinerary tradeoffs.
* Generate draft plans that are validated by backend rules.
* Convert chat input into structured requests.

Guardrails:

* Validate all LLM output.
* Never write directly to DB without structured validation.
* Do not trust AI-generated IDs.
* Keep deterministic itinerary generation as the source of truth unless intentionally replaced.

Definition of done:

* AI improves UX without weakening backend correctness.
* AI behavior is documented and testable.

---

## Interview positioning

### Short explanation

I built a Smart Vacation Planner backend in Java Spring Boot. The system lets users create vacations and vacation days, manage a catalog of points of interest enriched with Google Places data, select points of interest for each day, and generate a scheduled itinerary based on opening hours, activity duration, distance, and estimated travel time.

### Technical highlights

* Java + Spring Boot layered architecture
* REST API design with nested resources
* DTO validation
* Global exception handling
* JPA/Hibernate with MySQL
* Google Places integration
* Basic Auth with DB-backed users
* Role-based authorization
* Ownership checks in service layer
* Pagination and filtering
* Deterministic itinerary generation algorithm
* Frontend integration with React planned/partially implemented

### What this demonstrates

* Backend domain modeling
* API design
* Security thinking
* Data modeling and persistence
* External service integration
* Business-rule implementation
* Incremental delivery
* Ability to refactor the domain as the product becomes clearer

---

## Current priorities

1. Finish API cleanup.
2. Verify full end-to-end flow manually.
3. Add focused tests.
4. Align frontend with the current backend.
5. Improve itinerary generation rules.
6. Update README and documentation.
7. Add AI only after the core backend is stable.

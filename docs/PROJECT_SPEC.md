# Smart Vacation Planner — Project Spec

## Goal

Smart Vacation Planner is a backend-driven vacation planning system.

The system allows users to create vacations, define vacation days, choose points of interest for each day, and generate a scheduled itinerary.

The project is designed primarily as a backend portfolio project, with future AI-assisted planning added only after the core backend is stable.

---

## Product flow

1. User creates or loads a vacation.
2. User searches or manages points of interest for the vacation destination.
3. User selects relevant points of interest from the destination catalog.
4. User creates vacation days and assigns selected points of interest to each day.
5. System generates a scheduled itinerary for each day based on the assigned points of interest.
6. User views the generated daily plan.

---

## Core concepts

## User

Represents an authenticated user of the system.

Fields:

* `id`
* `firstName`
* `lastName`
* `email`
* `password`
* `role`
* `active`

Roles:

* `CUSTOMER`
* `ADMIN`

Rules:

* Customers can manage their own vacations.
* Admins can access broader system data.

---

## Vacation

Represents the main trip container.

Fields:

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

Rules:

* `endDate` must be on or after `startDate`.
* A vacation belongs to a user.
* A customer can only access their own vacations.

Traveler types:

* `INDIVIDUAL`
* `COUPLE`
* `FAMILY`
* `GROUP`
* `OTHER`

Pace options:

* `RELAXED`
* `BALANCED`
* `INTENSE`

---

## VacationDay

Represents one day inside a vacation.

Fields:

* `id`
* `vacation`
* `date`
* `dayNumber`
* `dayType`
* `hotelPlace`

Rules:

* A vacation day belongs to a vacation.
* The date must be within the parent vacation date range.
* The day number must be positive.
* The day number must not exceed the vacation duration.

Day types:

* `DAY`
* `NIGHT`
* `HALF_DAY`

---

## Place

Embedded location object.

Fields:

* `placeName`
* `placeId`
* `formattedAddress`
* `city`
* `country`
* `latitude`
* `longitude`

Used for:

* Vacation day hotel location
* Point of interest location

---

## PointOfInterest

Represents a reusable destination catalog item.

Fields:

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

* Stores points of interest that can be selected for vacation days.
* Enriched with Google Places data.
* Can be searched by city, country, name, category, duration, opening time, closing time, minimum age, and notes.

---

## VacationDayActivity

Represents a selected point of interest for a specific vacation day.

Fields:

* `id`
* `vacationDay`
* `pointOfInterest`
* `plannedStartTime`
* `plannedEndTime`
* `travelMinutesFromPrevious`
* `distanceKmFromPrevious`

Purpose:

* Connects a vacation day to a selected point of interest.
* Scheduling fields are filled when the itinerary is generated.

Important distinction:

* `PointOfInterest` is a reusable catalog item.
* `VacationDayActivity` is a user-selected item for a specific day.
* `ScheduledActivityResponse` represents the generated scheduled result.

---

## Itinerary generation

The itinerary generator creates a daily schedule from selected vacation day activities.

Current deterministic logic:

* Start each day from the hotel.
* Default day start time: 09:00.
* Default day end time: 18:00.
* Calculate distance between current place and candidate point of interest.
* Estimate travel time.
* Check opening time and closing time.
* Check if the activity fits before the end of the day.
* Choose the nearest schedulable candidate.
* Add a buffer after scheduled activities.
* Save planned times, travel minutes, and distance to the database.
* Return a structured itinerary response.

Current output structure:

* Vacation ID
* Vacation name
* List of days
* List of scheduled activities per day

---

## External integration

## Google Places

The system uses Google Places Text Search to enrich location data.

Used for:

* Point of interest location enrichment.
* Vacation day hotel location enrichment.

Stored data:

* `placeId`
* `formattedAddress`
* `city`
* `country`
* `latitude`
* `longitude`

---

## API overview

## Vacations

```http
GET    /api/v1/vacations
GET    /api/v1/vacations/page
GET    /api/v1/vacations/{id}
POST   /api/v1/vacations
PUT    /api/v1/vacations/{id}
PATCH  /api/v1/vacations/{id}
DELETE /api/v1/vacations/{id}
```

## Vacation days

```http
GET    /api/v1/vacations/{vacationId}/days
GET    /api/v1/vacations/{vacationId}/days/page
GET    /api/v1/vacations/{vacationId}/days/{id}
POST   /api/v1/vacations/{vacationId}/days
PUT    /api/v1/vacations/{vacationId}/days/{id}
PATCH  /api/v1/vacations/{vacationId}/days/{id}
DELETE /api/v1/vacations/{vacationId}/days/{id}
```

## Points of interest

```http
GET    /api/v1/points-of-interest
GET    /api/v1/points-of-interest/search
GET    /api/v1/points-of-interest/{id}
POST   /api/v1/points-of-interest
PUT    /api/v1/points-of-interest/{id}
DELETE /api/v1/points-of-interest/{id}
```

## Vacation day activities

Target shape:

```http
GET    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities
GET    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
POST   /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities
PUT    /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}
```

Create request:

```json
{
  "pointOfInterestId": 12
}
```

## Itinerary

```http
POST /api/v1/vacations/{vacationId}/itineraries
```

---

## Security

Current security model:

* Spring Security
* Basic Auth
* DB-backed users
* Login by email
* Role-based endpoint rules
* Ownership checks in service layer
* CORS enabled for local frontend
* CSRF disabled for REST API

Authorization rules:

* Customers can access their own vacations.
* Customers can manage days and selected activities inside their own vacations.
* Admins can manage point of interest catalog data.
* Customers can read/search points of interest.

---

## Error handling

The API uses a global exception handler.

Error response includes:

* `message`
* `path`
* `status`
* `timestamp`
* optional `fieldErrors`

Handled cases:

* Validation errors
* Resource not found
* Illegal argument / business rule violation
* Access denied
* Fallback unexpected errors

---

## Current MVP status

Completed or mostly completed:

* Vacation CRUD
* Vacation day CRUD
* Point of interest catalog
* Vacation day activity selection
* Google Places integration
* Itinerary generation
* Basic Auth
* User ownership checks
* Global error handling
* Filtering and pagination for key resources
* Swagger configuration

Still to improve:

* API naming consistency for selected activities
* Request DTO for creating vacation day activities
* Delete/cascade behavior
* Test coverage
* README and developer setup documentation
* Frontend alignment
* More advanced itinerary rules
* AI-assisted planning

---

## Future ideas

Backend improvements:

* Add tests with JUnit/Mockito.
* Add controller integration tests.
* Add Testcontainers for MySQL.
* Add Flyway or Liquibase migrations.
* Improve HTTP status codes.
* Improve logging.
* Add transaction boundaries.
* Review N+1 queries.

Itinerary improvements:

* Use vacation pace.
* Use day type.
* Add user preferences.
* Add lunch/dinner breaks.
* Add priority/must-have activities.
* Add configurable day start/end time.
* Improve travel time estimation.

Frontend improvements:

* Search POIs by destination.
* Display POIs on map.
* Select POIs per vacation day.
* Generate itinerary from UI.
* Display scheduled itinerary on map.

AI phase:

* Use AI to collect preferences.
* Use AI to recommend POIs.
* Use AI to explain itinerary tradeoffs.
* Validate AI output through backend rules.
* Keep deterministic backend constraints as the source of truth.

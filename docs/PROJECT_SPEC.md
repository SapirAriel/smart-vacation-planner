# Smart Vacation Planner

## Goal
Backend system for planning vacations with future AI integration.

## MVP
- Create Vacation
- Add Vacation Days
- Add Activities

## Entities

### Vacation
- id
- name
- country
- city
- startDate
- endDate
- travelerType

### VacationDay
- id
- vacationId
- date
- dayNumber
- dayType

### Activity
- id
- vacationDayId
- name
- activityType
- location
- durationMinutes
- openingHours
- minimumAge
- notes

## Future Ideas
- Smart itinerary generation
- AI recommendations
- Preferences-based planning




Sprint 1 (3–5 days): Backend Foundation
Task 1: Introduce API DTOs + validation for Vacation create/update flows.
Task 2: Add global error handling with consistent error response shape.
Task 3: Add API versioning (/api/v1/...) and keep controller thin.
Task 4: Add VacationDay entity + relation to Vacation.
Task 5: Add one integration test for create + fetch vacation flow.
Start Now — Task 1 (you implement)
Implement these first:

Create package: .../dto/vacation/
Add:
CreateVacationRequest
UpdateVacationRequest
VacationResponse
Add validation annotations:
name, country, city, travelerType -> @NotBlank
startDate, endDate -> @NotNull
Add date rule in service: endDate must be after or equal to startDate
Update controller to accept DTOs (@Valid) and return VacationResponse instead of entity.
Suggested DTO fields (v1)
CreateVacationRequest: name, country, city, startDate, endDate, travelerType, budgetLevel, pace
UpdateVacationRequest: same as above (or partial if you prefer later)
VacationResponse: id + all user-facing vacation fields
Definition of done for Task 1
Request body validation errors return 400
No entity class is exposed directly from controller
Existing create/get/update endpoints still function
Code compiles and app starts
When you finish Task 1, send me:

what files you created/changed, and
any error you hit.
Then I’ll do a review exactly like a backend interviewer (what is good, what to improve, and why).


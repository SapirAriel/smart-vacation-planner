# Backend Test Coverage Map

## Verification metadata

- Last verified: `2026-07-28 17:40 UTC+03:00`
- Verification source: actual Maven run plus repository inspection
- Last full test run command: `.\mvnw.cmd test`
- Total tests run: `36`
- Failures: `0`
- Errors: `0`
- Skipped: `1`
- Skipped tests:
  - `com.sapir.smartvacationplanner.SmartvacationplannerApplicationTests`
- Skipped reason:
  - gated by `@EnabledIfEnvironmentVariable` for `DB_USERNAME` and `DB_PASSWORD`
  - full context also requires a reachable MySQL at `jdbc:mysql://localhost:3306/smart_vacation_planner`
- Environment requirements:
  - Java 17
  - Maven wrapper
  - `DB_USERNAME` present: `false`
  - `DB_PASSWORD` present: `false`
  - `GOOGLE_MAPS_API_KEY` required only for real Google/full-context execution, not for current mocked unit tests
- Notes:
  - `src/test/resources` does not exist
  - current baseline includes mocked service-unit tests, two `@WebMvcTest` controller tests, and one skipped `@SpringBootTest`

## Coverage status definitions

- `Not covered`: no meaningful automated test currently exercises this production flow.
- `Partial`: some behavior is tested, but important branches or testing levels are missing.
- `Good at unit level`: mocked unit tests meaningfully cover current logic branches. This does **not** prove Spring MVC mapping, Spring Security filter-chain behavior, repository query correctness, transactions, MySQL constraints, or external HTTP integration.
- `Good at HTTP level`: controller tests meaningfully prove request mapping, JSON binding, validation, response shape, and/or exception mapping. This does **not** imply real database or filter-chain coverage unless such tests exist.
- `Good at DB level`: real database tests prove query behavior, constraints, relationships, or persistence semantics.
- `Complete for current contract`: the current contract is covered at the levels that matter for its risks. Mocked-only coverage is not enough for this status when HTTP, DB, or external behavior is part of the risk profile.

## Domain coverage matrices

### Authorization and Security

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `AUTH-CURRENT-USER` | `AuthorizationService.getCurrentUser()` | Indirect via secured backend flows | Reads authenticated principal email and loads user | None | Not covered | Not covered | Not covered | N/A | missing authenticated-user happy path, missing missing-user/null-auth behavior | High | Current code has no null-check on `Authentication` or `findByEmail` result |
| `AUTH-VACATION-ACCESS` | `AuthorizationService.getVacationForCurrentUser(Integer)` | Indirect via many vacation/day/activity/itinerary endpoints | Vacation load; owner allowed; non-owner denied; admin bypass; missing vacation -> `Vacation not found with id: X` | `AuthorizationServiceTest.shouldRejectAccessWhenVacationBelongsToAnotherUser`, `AuthorizationServiceTest.shouldAllowAccessWhenVacationBelongsToCurrentUser` | Partial | Not covered | Not covered | N/A | missing admin bypass, missing vacation-not-found, missing unauthenticated behavior, missing real filter-chain coverage | Critical | Exact deny message tested only at unit level |
| `AUTH-DAY-ACCESS-BY-ID` | `AuthorizationService.getVacationDayForCurrentUser(Integer, Integer)` | Indirect via day/activity/itinerary flows | Ownership of vacation then scoped day lookup; missing day -> `Vacation day not found with id: X` | `AuthorizationServiceTest.getVacationDayForCurrentUser_whenDayDoesNotBelongToVacation_throwsResourceNotFoundException` | Partial | Not covered | Not covered | N/A | missing happy path, missing ownership deny propagated through day access, missing DB proof of scoped lookup | High | Current unit test correctly verifies `findByVacationAndId(vacation, dayId)` |
| `AUTH-DAY-ACCESS-BY-DATE` | `AuthorizationService.getVacationDayForCurrentUser(Integer, LocalDate)` | Indirect via itinerary flow | Ownership then scoped day-by-date lookup; missing day -> `Vacation day not found with date: X` | None | Not covered | Not covered | Not covered | N/A | missing all behavior | High | Used by itinerary generation |
| `AUTH-DAY-LIST` | `AuthorizationService.getVacationDaysForCurrentUser(Integer)` | Indirect | Ownership then day list | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | No direct unit test |
| `AUTH-ACTIVITY-ACCESS-BY-ID` | `AuthorizationService.getVacationDayActivityForCurrentUser(Integer, Integer, Integer)` | Indirect via activity service/controller | Vacation + day ownership then scoped activity lookup; missing -> `Vacation day activity not found with id: X` | None | Not covered | Not covered | Not covered | N/A | missing all behavior | High | Relied on by activity update/delete service methods |
| `AUTH-ACTIVITY-LIST` | `AuthorizationService.getVacationDayActivitiesForCurrentUser(Integer, Integer, Sort)` | Indirect | Ownership then scoped/sorted list | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | No DB proof of sorting |
| `SECURITY-FILTER-CHAIN` | `SecurityConfig` path and role rules | All `/api/v1/**` | HTTP Basic auth, role-based access, custom 403 JSON, CORS, JDBC user details | None | Not covered | Not covered | Not covered | N/A | missing 401/403, admin/customer path-rule tests, custom access denied body | Critical | POI path mismatch exists: config uses `/points-of-interests/**`, controller uses `/points-of-interest/**` |

### Vacation

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `VAC-LIST-ALL` | `VacationServiceImpl.getAllVacations()` | `GET /api/v1/vacations` | Admin gets all; customer gets own vacations | None | Not covered | Not covered | Not covered | N/A | missing admin/customer branch coverage, mapping, auth/filter coverage | High | No tests for service or endpoint |
| `VAC-SEARCH` | `VacationServiceImpl.searchVacations(...)` | `GET /api/v1/vacations/page` | Optional filters + admin/customer scoping | None | Not covered | Not covered | Not covered | N/A | missing filter behavior and repo query proof | High | Search query correctness currently unproven |
| `VAC-GET-BY-ID` | `VacationServiceImpl.getVacationById(Integer)` | `GET /api/v1/vacations/{id}` | Delegates to authorization | None | Not covered | Not covered | Not covered | N/A | missing endpoint + auth propagation coverage | High | Auth helper partially covered separately, but not this flow |
| `VAC-CREATE` | `VacationServiceImpl.createVacation(Vacation)` | `POST /api/v1/vacations` | Date validation, current-user assignment, duplicate-name protection, save | `VacationServiceImplTest.createVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException`, `VacationServiceImplTest.createVacation_whenEndDateAfterStartDate_createsVacation`, `VacationServiceImplTest.createVacation_whenNameAlreadyExistsForCurrentUser_throwsDuplicateResourceException`, `VacationControllerHappyPathTest.createVacation_whenValidBody_returns200WithIdAndName`, `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors` | Good at unit level | Partial | Not covered | N/A | missing DB unique-constraint proof, missing auth filter-chain coverage, missing duplicate-name mapping to HTTP 409, missing invalid-date mapping to HTTP 400, missing complete serialized response contract | High | Current HTTP coverage proves basic POST mapping, one basic success response, and required-field validation only |
| `VAC-UPDATE` | `VacationServiceImpl.updateVacation(Integer, Vacation)` | `PUT /api/v1/vacations/{id}` | Full-field update, duplicate-name branch, unchanged owner/id, date validation | `VacationServiceImplTest.updateVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException`, `VacationServiceImplTest.updateVacation_whenEndDateAfterStartDate_updatesVacation`, `VacationServiceImplTest.updateVacation_whenNameAlreadyExistsForSameUser_throwsDuplicateResourceException`, `VacationServiceImplTest.updateVacation_whenNameUnchanged_updatesVacationWithoutDuplicateCheck` | Good at unit level | Not covered | Not covered | N/A | missing HTTP mapping/validation/security, missing DB proof of uniqueness and persistence | High | Name-unchanged short-circuit is well covered at unit level |
| `VAC-PATCH` | `VacationServiceImpl.patchVacation(Integer, Vacation)` | `PATCH /api/v1/vacations/{id}` | Partial update, duplicate check only when name provided, post-merge date validation | None | Not covered | Not covered | Not covered | N/A | missing all behavior | High | Controller patch method also lacks `@Valid` |
| `VAC-GET-DAYS` | `VacationServiceImpl.getVacationDays(Integer)` | None directly; day endpoint lives elsewhere | Authorized vacation then `findByVacation_Id` | None | Not covered | Not covered | Not covered | N/A | missing service behavior and repository proof | Medium | Controller path for days is on `VacationDayController` |
| `VAC-DELETE` | `VacationServiceImpl.deleteVacation(Integer)` | `DELETE /api/v1/vacations/{id}` | Authorized delete; entity cascade expected | None | Not covered | Not covered | Not covered | N/A | missing delete behavior, role restriction, cascade/orphan removal proof | Critical | HTTP DELETE is ADMIN-only in `SecurityConfig`, but not tested |

### VacationDay

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `DAY-LIST` | `VacationDayServiceImpl.getAllVacationDays(Integer)` | `GET /api/v1/vacations/{vacationId}/days` | Authorized list by vacation | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | No tests for service or controller |
| `DAY-SEARCH` | `VacationDayServiceImpl.searchVacationDays(...)` | `GET /api/v1/vacations/{vacationId}/days/page` | Optional filters by type/date/dayNumber | None | Not covered | Not covered | Not covered | N/A | missing filter behavior and repo query proof | High | Controller accepts `hotelPlaceName`, but service/repo ignore it |
| `DAY-GET-BY-ID` | `VacationDayServiceImpl.getVacationDayById(Integer, Integer)` | `GET /api/v1/vacations/{vacationId}/days/{id}` | Delegates to scoped auth helper | None | Not covered | Not covered | Not covered | N/A | missing endpoint and auth propagation | High | |
| `DAY-CREATE` | `VacationDayServiceImpl.createVacationDay(Integer, CreateVacationDayRequest)` | `POST /api/v1/vacations/{vacationId}/days` | Authorization before duplicate checks; unique day/date; hotel lookup; date and day-number bounds; save | `VacationDayServiceImplTest.createVacationDay_whenDateEqualsVacationStartDate_createsVacationDay`, `VacationDayServiceImplTest.createVacationDay_whenDateEqualsVacationEndDate_createsVacationDay`, `VacationDayServiceImplTest.createVacationDay_whenDateBeforeVacationStartDate_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDateAfterVacationEndDate_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberAlreadyExists_throwsDuplicateResourceException`, `VacationDayServiceImplTest.createVacationDay_whenDateAlreadyExists_throwsDuplicateResourceException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberIsZero_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberExceedsVacationDuration_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenVacationBelongsToAnotherUser_throwsAccessDeniedException`, `VacationDayServiceImplTest.createVacationDay_whenUnauthorizedAndDuplicateDataExists_throwsAccessDeniedException` | Good at unit level | Not covered | Not covered | Not covered | missing HTTP mapping/validation/security, missing DB uniqueness proof, missing real Google integration, missing save-to-DB semantics | Critical | Google-dependent service behavior is exercised only through a mocked `GooglePlacesClient` |
| `DAY-UPDATE` | `VacationDayServiceImpl.updateVacationDay(Integer, Integer, UpdateVacationDayRequest)` | `PUT /api/v1/vacations/{vacationId}/days/{id}` | Replaces hotel Place only when name changes; updates dayType; validates constraints; saves | None | Not covered | Not covered | Not covered | Not covered | missing all behavior | Critical | Important immutable/mutable field contract currently untested |
| `DAY-PATCH` | `VacationDayServiceImpl.patchVacationDay(Integer, Integer, PatchVacationDayRequest)` | `PATCH /api/v1/vacations/{vacationId}/days/{id}` | Partial dayType/hotel changes; validates after patch | None | Not covered | Not covered | Not covered | Not covered | missing all behavior | High | |
| `DAY-LIST-ACTIVITIES` | `VacationDayServiceImpl.getAllVacationDayActivities(Integer, Integer)` | None directly; activities have own controller | Authorized day then list activities | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | |
| `DAY-DELETE` | `VacationDayServiceImpl.deleteVacationDay(Integer, Integer)` | `DELETE /api/v1/vacations/{vacationId}/days/{id}` | Authorized delete; cascade/orphan removal expected | None | Not covered | Not covered | Not covered | N/A | missing all behavior and persistence semantics | High | |

### PointOfInterest

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `POI-LIST` | `PointOfInterestServiceImpl.getAllPointOfInterests()` | `GET /api/v1/points-of-interest` | List all POIs | None | Not covered | Not covered | Not covered | N/A | missing service and endpoint behavior | Medium | |
| `POI-SEARCH` | `PointOfInterestServiceImpl.searchPointOfInterests(...)` | `GET /api/v1/points-of-interest/search` | Optional filters incl. category/place/city/country/hours/notes | None | Not covered | Not covered | Not covered | N/A | missing filter behavior, endpoint binding, repo query proof | High | |
| `POI-GET-BY-ID` | `PointOfInterestServiceImpl.getPointOfInterestById(Integer)` | `GET /api/v1/points-of-interest/{id}` | Load or `Point of interest not found` | None | Not covered | Not covered | Not covered | N/A | missing happy path + not-found behavior | Medium | |
| `POI-CREATE` | `PointOfInterestServiceImpl.createPointOfInterest(CreatePointOfInterestRequest)` | `POST /api/v1/points-of-interest` | Dedup by ignore-case name; Google search; dedup by placeId; save new POI | `PointOfInterestServiceImplTest.createPointOfInterest_whenGooglePlacesReturnsResultAndNoDuplicate_mapsPlaceFieldsAndSaves`, `PointOfInterestServiceImplTest.createPointOfInterest_whenGooglePlacesFindsNoPlace_propagatesIllegalArgumentExceptionAndDoesNotSave`, `PointOfInterestServiceImplTest.createPointOfInterest_whenExistingFoundByPlaceNameIgnoreCase_returnsExistingWithoutCallingGoogleOrSaving`, `PointOfInterestServiceImplTest.createPointOfInterest_whenNoNameMatchButExistingFoundByPlaceId_returnsExistingWithoutSaving` | Good at unit level | Not covered | Not covered | Not covered | missing HTTP/security path coverage, missing DB case-insensitive/unique behavior, missing real Google request/parse/config coverage | Critical | Google-dependent service behavior is exercised only through a mocked `GooglePlacesClient` |
| `POI-UPDATE` | `PointOfInterestServiceImpl.updatePointOfInterest(Integer, UpdatePointOfInterestRequest)` | `PUT /api/v1/points-of-interest/{id}` | Update mutable metadata only; embedded Place remains unchanged | `PointOfInterestServiceImplTest.updatePointOfInterest_whenPointOfInterestExists_updatesMutableFieldsAndLeavesPlaceUnchanged`, `PointOfInterestServiceImplTest.updatePointOfInterest_whenPointOfInterestDoesNotExist_throwsResourceNotFoundExceptionAndDoesNotSave` | Good at unit level | Not covered | Not covered | N/A | missing HTTP mapping/validation/security and DB persistence proof | High | |
| `POI-DELETE` | `PointOfInterestServiceImpl.deletePointOfInterest(Integer)` | `DELETE /api/v1/points-of-interest/{id}` | Direct `deleteById` without explicit existence check | None | Not covered | Not covered | Not covered | N/A | missing delete behavior and FK/constraint behavior | High | Possible production concern: not-found behavior depends on JPA/database, not explicit service contract |

### VacationDayActivity

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `ACT-LIST` | `VacationDayActivityServiceImpl.getAllVacationDayActivities(Integer, Integer)` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | Authorized list of activities for a day | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | |
| `ACT-GET-BY-ID` | `VacationDayActivityServiceImpl.getVacationDayActivityById(Integer, Integer, Integer)` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Authorized scoped get | None | Not covered | Not covered | Not covered | N/A | missing all behavior | High | |
| `ACT-CREATE` | `VacationDayActivityServiceImpl.createVacationDayActivity(...)` | `POST /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | Link day and POI; scheduling fields initially null; short-circuit on auth or missing POI | `VacationDayActivityServiceImplTest.createVacationDayActivity_whenOwnedDayAndExistingPointOfInterest_savesActivityWithNullSchedulingFields`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenPointOfInterestDoesNotExist_throwsResourceNotFoundExceptionAndDoesNotSave`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenVacationBelongsToAnotherUser_throwsAccessDeniedExceptionAndDoesNotLookupPointOfInterest` | Good at unit level | Not covered | Not covered | N/A | missing HTTP/security/DB proof | High | Null scheduling fields are explicitly covered |
| `ACT-UPDATE` | `VacationDayActivityServiceImpl.updateVacationDayActivity(...)` | `PUT /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Replace POI; keep day/id/scheduling fields unchanged; short-circuit on missing access | `VacationDayActivityServiceImplTest.updateVacationDayActivity_whenAccessibleActivityAndExistingPointOfInterest_replacesPointOfInterestAndLeavesSchedulingUnchanged`, `VacationDayActivityServiceImplTest.updateVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotLookupPointOfInterest` | Partial | Not covered | Not covered | N/A | missing update-with-missing-POI path, HTTP/security/DB proof | High | Possible production concern: existing itinerary schedule remains stale after POI replacement |
| `ACT-DELETE` | `VacationDayActivityServiceImpl.deleteVacationDayActivity(...)` | `DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Authorized delete; no delete on missing access | `VacationDayActivityServiceImplTest.deleteVacationDayActivity_whenActivityIsAccessible_deletesExistingActivity`, `VacationDayActivityServiceImplTest.deleteVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotDelete` | Good at unit level | Not covered | Not covered | N/A | missing HTTP/security/DB proof | Medium | |

### Itinerary and Scheduling

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `ITN-GET-VACATION` | `ItineraryServiceImpl.getVacationById(Integer)` | Indirect via itinerary generation | Delegates to authorization helper | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | |
| `ITN-GET-DAYS` | `ItineraryServiceImpl.getVacationDays(Integer)` | Indirect | Delegates to authorization helper | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | |
| `ITN-GET-DAY-BY-DATE` | `ItineraryServiceImpl.getVacationDayByDate(Integer, LocalDate)` | Indirect | Delegates to auth scoped day-by-date helper | None | Not covered | Not covered | Not covered | N/A | missing all behavior | High | |
| `ITN-GET-ACTIVITIES` | `ItineraryServiceImpl.getVacationDayActivities(Integer, Integer)` | Indirect | Scoped list sorted by opening/closing/duration repository query | None | Not covered | Not covered | Not covered | N/A | missing all behavior and sorting proof | High | |
| `ITINERARY-GENERATE` | `ItineraryServiceImpl.generateItinerary(Integer)` | `POST /api/v1/vacations/{vacationId}/itineraries` | Builds schedule across days, walking/driving time, 15-minute increments/buffer, persists planned times and travel metrics | None | Not covered | Not covered | Not covered | Not covered | missing all scheduling, persistence, and endpoint behavior | Critical | Highest uncovered behavior complexity in backend |

### Controllers and Request Validation

| Coverage ID | Production endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|
| `HTTP-VAC-LIST` | `GET /api/v1/vacations` | Maps vacation list response | None | N/A | Not covered | Not covered | N/A | mapping, security, serialization, service delegation | Medium | |
| `HTTP-VAC-SEARCH` | `GET /api/v1/vacations/page` | Query-param binding + pageable mapping | None | N/A | Not covered | Not covered | N/A | filters, paging, security | High | |
| `HTTP-VAC-GET` | `GET /api/v1/vacations/{id}` | Path binding + response | None | N/A | Not covered | Not covered | N/A | not found, denied, serialization | High | |
| `HTTP-VAC-CREATE` | `POST /api/v1/vacations` | Valid request returns vacation response | `VacationControllerHappyPathTest.createVacation_whenValidBody_returns200WithIdAndName` | N/A | Partial | Not covered | N/A | security, full response fields, duplicate/date error mappings | High | |
| `HTTP-VAC-CREATE-VALIDATION` | `POST /api/v1/vacations` | Invalid body -> 400 with field errors | `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors` | N/A | Good at HTTP level | Not covered | N/A | more validation variants, security | Medium | |
| `HTTP-VAC-UPDATE` | `PUT /api/v1/vacations/{id}` | Validated full update mapping | None | N/A | Not covered | Not covered | N/A | mapping, validation, security | High | |
| `HTTP-VAC-PATCH` | `PATCH /api/v1/vacations/{id}` | Partial update binding | None | N/A | Not covered | Not covered | N/A | mapping, validation absence, security | High | |
| `HTTP-VAC-DELETE` | `DELETE /api/v1/vacations/{id}` | Delete mapping and security role rule | None | N/A | Not covered | Not covered | N/A | admin-only filter behavior, response contract | Critical | |
| `HTTP-DAY-LIST` | `GET /api/v1/vacations/{vacationId}/days` | Day list mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-DAY-SEARCH` | `GET /api/v1/vacations/{vacationId}/days/page` | Query binding for `dayType`, `date`, `hotelPlaceName`, `dayNumber`, pageable | None | N/A | Not covered | Not covered | N/A | binding, ignored `hotelPlaceName`, security | High | |
| `HTTP-DAY-GET` | `GET /api/v1/vacations/{vacationId}/days/{id}` | Path binding | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-DAY-CREATE` | `POST /api/v1/vacations/{vacationId}/days` | `@Valid CreateVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | validation, errors, security | High | |
| `HTTP-DAY-UPDATE` | `PUT /api/v1/vacations/{vacationId}/days/{id}` | `@Valid UpdateVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-DAY-PATCH` | `PATCH /api/v1/vacations/{vacationId}/days/{id}` | `@Valid PatchVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | mapping/security | High | |
| `HTTP-DAY-DELETE` | `DELETE /api/v1/vacations/{vacationId}/days/{id}` | Delete mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-LIST` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | Activity list mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-GET` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Activity get mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-CREATE` | `POST /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | `@Valid` request with `pointOfInterestId` | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-ACT-UPDATE` | `PUT /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | `@Valid` update request | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-ACT-DELETE` | `DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Delete mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-POI-LIST` | `GET /api/v1/points-of-interest` | List mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-POI-SEARCH` | `GET /api/v1/points-of-interest/search` | Query binding and pageable sort | None | N/A | Not covered | Not covered | N/A | all behavior | High | |
| `HTTP-POI-GET` | `GET /api/v1/points-of-interest/{id}` | Path binding | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-POI-CREATE` | `POST /api/v1/points-of-interest` | `@Valid` request and security | None | N/A | Not covered | Not covered | N/A | all behavior | High | |
| `HTTP-POI-UPDATE` | `PUT /api/v1/points-of-interest/{id}` | `@Valid` request and security | None | N/A | Not covered | Not covered | N/A | all behavior | High | |
| `HTTP-POI-DELETE` | `DELETE /api/v1/points-of-interest/{id}` | Delete mapping and security | None | N/A | Not covered | Not covered | N/A | all behavior | High | |
| `HTTP-ITINERARY-GENERATE` | `POST /api/v1/vacations/{vacationId}/itineraries` | Endpoint mapping for itinerary generation | None | N/A | Not covered | Not covered | N/A | all behavior and security | Critical | |

### Exception handling

| Coverage ID | Production flow | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|
| `ERR-HANDLER-VALIDATION` | `GlobalExceptionHandler.handleValidationExceptions` | 400 with `Validation failed`, path, status, field errors | `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors` | N/A | Good at HTTP level | Not covered | N/A | more validation cases and controllers | Medium | |
| `ERR-HANDLER-NOT-FOUND` | `handleResourceNotFoundException` | 404 API error shape | None | N/A | Not covered | Not covered | N/A | missing handler proof | High | |
| `ERR-HANDLER-DUPLICATE` | `handleDuplicateResourceException` | 409 API error shape | None | N/A | Not covered | Not covered | N/A | missing handler proof | High | |
| `ERR-HANDLER-ILLEGAL-ARG` | `handleIllegalArgumentException` | 400 API error shape | None | N/A | Not covered | Not covered | N/A | missing handler proof | High | |
| `ERR-HANDLER-ACCESS-DENIED` | `handleAccessDeniedException` | 403 API error shape | None | N/A | Not covered | Not covered | N/A | missing handler proof | High | Filter-chain 403 also has a separate JSON contract in `SecurityConfig` |
| `ERR-HANDLER-DATA-INTEGRITY` | `handleDataIntegrityViolationException` | 409 database constraint error shape | None | N/A | Not covered | Not covered | N/A | missing handler proof | High | |
| `ERR-HANDLER-FALLBACK` | `handleGeneralException` | 500 fallback with class + message | None | N/A | Not covered | Not covered | N/A | missing handler proof | Medium | |

### Repositories and Database Constraints

| Coverage ID | Repository or constraint | Main behavior | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|
| `DB-USER-FIND-BY-EMAIL` | `UserRepository.findByEmail` | Security/auth principal resolution | Indirect only via mocked service tests | Partial | Not covered | Not covered | N/A | real query behavior | Medium | |
| `DB-VAC-FIND-BY-USER-ID` | `VacationRepository.findByUserId` | Customer-scoped vacation listing | None | Not covered | Not covered | Not covered | N/A | real query behavior | Medium | |
| `DB-VAC-EXISTS-BY-USER-ID-NAME` | `VacationRepository.existsByUserIdAndName` | Duplicate-name protection | Indirect via unit mocks in `VacationServiceImplTest` | Partial | Not covered | Not covered | N/A | case sensitivity, real uniqueness | High | |
| `DB-VAC-SEARCH-ALL` | `VacationRepository.searchVacations` | Admin/global search filters | None | Not covered | Not covered | Not covered | N/A | real query behavior | High | |
| `DB-VAC-SEARCH-BY-USER` | `VacationRepository.searchVacationsByUserId` | Customer-scoped search filters | None | Not covered | Not covered | Not covered | N/A | real query behavior | High | |
| `DB-DAY-FIND-BY-VACATION` | `VacationDayRepository.findByVacation` | Scoped list | Indirect only via mocks | Partial | Not covered | Not covered | N/A | real query behavior | Medium | |
| `DB-DAY-FIND-BY-VACATION-ID` | `VacationDayRepository.findByVacation_Id` | Day list by id | None | Not covered | Not covered | Not covered | N/A | real query behavior | Medium | |
| `DB-DAY-FIND-BY-VACATION-AND-ID` | `VacationDayRepository.findByVacationAndId` | Parent-child scoped lookup | `AuthorizationServiceTest.getVacationDayForCurrentUser_whenDayDoesNotBelongToVacation_throwsResourceNotFoundException` | Partial | Not covered | Not covered | N/A | real DB proof of scope | High | |
| `DB-DAY-FIND-BY-VACATION-AND-DATE` | `VacationDayRepository.findByVacationAndDate` | Day-by-date scoped lookup | None | Not covered | Not covered | Not covered | N/A | all behavior | High | |
| `DB-DAY-EXISTS-DAY-NUMBER` | `VacationDayRepository.existsByVacationIdAndDayNumber` | Duplicate day-number protection | Indirect via unit mocks in `VacationDayServiceImplTest` | Partial | Not covered | Not covered | N/A | real uniqueness proof | High | |
| `DB-DAY-EXISTS-DATE` | `VacationDayRepository.existsByVacationIdAndDate` | Duplicate date protection | Indirect via unit mocks in `VacationDayServiceImplTest` | Partial | Not covered | Not covered | N/A | real uniqueness proof | High | |
| `DB-DAY-FIND-BY-VACATION-ID-DAY-TYPE` | `VacationDayRepository.findByVacation_IdAndDayType` | Day-type list | None | Not covered | Not covered | Not covered | N/A | all behavior | Low | |
| `DB-DAY-SEARCH` | `VacationDayRepository.searchVacationDays` | Filtered search | None | Not covered | Not covered | Not covered | N/A | all behavior | High | |
| `DB-POI-FIND-BY-PLACE-NAME-IGNORE-CASE` | `PointOfInterestRepository.findByPlace_PlaceNameIgnoreCase` | Name dedup | Indirect via unit mocks in `PointOfInterestServiceImplTest` | Partial | Not covered | Not covered | N/A | real DB collation/ignore-case proof | High | |
| `DB-POI-FIND-BY-PLACE-ID` | `PointOfInterestRepository.findByPlace_PlaceId` | Place-id dedup | Indirect via unit mocks in `PointOfInterestServiceImplTest` | Partial | Not covered | Not covered | N/A | real DB uniqueness proof | High | |
| `DB-POI-SEARCH` | `PointOfInterestRepository.searchPointOfInterests` | Filtered search | None | Not covered | Not covered | Not covered | N/A | all behavior | High | |
| `DB-ACT-FIND-BY-DAY` | `VacationDayActivityRepository.findByVacationDay` | Day-scoped list | None | Not covered | Not covered | Not covered | N/A | all behavior | Medium | |
| `DB-ACT-FIND-BY-DAY-ID` | `VacationDayActivityRepository.findByVacationDay_Id` | Lookup by FK id | None | Not covered | Not covered | Not covered | N/A | all behavior | Low | |
| `DB-ACT-FIND-BY-DAY-AND-ID` | `VacationDayActivityRepository.findByVacationDayAndId` | Parent-child scoped activity lookup | None | Not covered | Not covered | Not covered | N/A | all behavior | High | |
| `DB-CONSTRAINT-USER-EMAIL-UNIQUE` | `users.email unique` | User uniqueness | None | Not covered | Not covered | Not covered | N/A | all behavior | Medium | |
| `DB-CONSTRAINT-VAC-USER-NAME-UNIQUE` | `vacations unique(user_id, name)` | Duplicate vacation-name enforcement | None | Not covered | Not covered | Not covered | N/A | real DB constraint proof | High | Service duplicate checks are mocked-only |
| `DB-CONSTRAINT-DAY-VACATION-DATE-UNIQUE` | `vacation_days unique(vacation_id, date)` | Duplicate date enforcement | None | Not covered | Not covered | Not covered | N/A | real DB constraint proof | High | |
| `DB-CONSTRAINT-DAY-VACATION-DAYNUM-UNIQUE` | `vacation_days unique(vacation_id, day_number)` | Duplicate day-number enforcement | None | Not covered | Not covered | Not covered | N/A | real DB constraint proof | High | |
| `DB-CONSTRAINT-POI-PLACE-ID-UNIQUE` | `points_of_interest.place_id unique` | Place-id uniqueness | None | Not covered | Not covered | Not covered | N/A | real DB constraint proof | High | |
| `DB-CONSTRAINT-POI-PLACE-NAME-UNIQUE` | `points_of_interest.place_name unique` | Place-name uniqueness | None | Not covered | Not covered | Not covered | N/A | real DB constraint proof | High | |
| `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK` | `vacation_day_activities.vacation_day_id not null FK` | Parent relationship required | None | Not covered | Not covered | Not covered | N/A | all behavior | Medium | |
| `DB-CONSTRAINT-ACTIVITY-POI-FK` | `vacation_day_activities.point_of_interest_id not null FK` | POI relationship required | None | Not covered | Not covered | Not covered | N/A | all behavior | Medium | |

### Google Places Integration

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `GOOGLE-SEARCH-EMPTY-QUERY` | `GooglePlacesClient.searchPlace(String)` | Indirect | Reject empty query | None | Not covered | Not covered | Not covered | Not covered | missing direct client tests | Medium | |
| `GOOGLE-SEARCH-REQUEST-CONSTRUCTION` | `GooglePlacesClient.searchPlace(String)` | Indirect | Builds POST request with API key, field mask, and query | None | Not covered | Not covered | Not covered | Not covered | missing request construction/config coverage | High | |
| `GOOGLE-SEARCH-SUCCESS-PARSE` | `GooglePlacesClient.searchPlace(String)` | Indirect | Parses first place into `PlaceResult` | None | Not covered | Not covered | Not covered | Not covered | missing direct client tests | High | |
| `GOOGLE-SEARCH-NO-PLACE` | `GooglePlacesClient.searchPlace(String)` | Indirect | `No place found for query: X` | None | Not covered | Not covered | Not covered | Not covered | service tests only mock this behavior | High | |
| `GOOGLE-SEARCH-NO-LOCATION` | `GooglePlacesClient.searchPlace(String)` | Indirect | `No location found for place: X` | None | Not covered | Not covered | Not covered | Not covered | all behavior | Medium | |
| `GOOGLE-SEARCH-CITY-EXTRACTION` | `GooglePlacesClient.extractCity(...)` | Indirect | locality -> postal_town -> admin_area_level_2 -> admin_area_level_1 | None | Not covered | Not covered | Not covered | Not covered | all behavior | Medium | |
| `GOOGLE-SEARCH-COUNTRY-EXTRACTION` | `GooglePlacesClient.extractCountry(...)` | Indirect | country component extraction | None | Not covered | Not covered | Not covered | Not covered | all behavior | Medium | |
| `GOOGLE-UPSTREAM-ERRORS` | `RestClient`/Google call path | Indirect | upstream HTTP or parsing error propagation | None | Not covered | Not covered | Not covered | Not covered | all behavior | High | |

### Application context and test configuration

| Coverage ID | Production/config item | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|
| `CTX-BOOTSTRAP` | `SmartvacationplannerApplication` + main config | Full application context loads | `SmartvacationplannerApplicationTests.contextLoads` | N/A | N/A | Not covered | N/A | A conditional full-context smoke test exists but was skipped in the current baseline. It does not prove repository queries or database constraints. | Medium | Currently skipped in baseline |
| `CFG-TEST-RESOURCES` | test configuration strategy | No `src/test/resources`; tests rely on main config or mocks | None | N/A | N/A | N/A | N/A | no isolated test profile | Medium | Important for future DB/integration work |

## Inventory-based coverage estimate

These are inventory-based counts from current source and tests. They are **not** JaCoCo line coverage and must not be interpreted as such.

### 1. Public service methods with meaningful unit coverage

- Numerator: `10`
- Denominator: `39`
- Percentage: `25.6%`
- Counted item set:
  - Covered: `AUTH-VACATION-ACCESS`, `AUTH-DAY-ACCESS-BY-ID`, `VAC-CREATE`, `VAC-UPDATE`, `DAY-CREATE`, `POI-CREATE`, `POI-UPDATE`, `ACT-CREATE`, `ACT-UPDATE`, `ACT-DELETE`
  - Not yet meaningfully covered: all remaining service Coverage IDs in the service-domain tables above
- Level represented: unit tests with mocks

### 2. Branch-level coverage note

A branch-level percentage is intentionally not published in this baseline.

It will be added only after a complete, versioned branch inventory is created across both
covered and uncovered production flows. The current repository has meaningful branch
coverage in some unit tests, but not yet a systematic branch denominator that is safe to
publish as a percentage.

### 3. Controller endpoints with meaningful HTTP coverage

- Numerator: `1`
- Denominator: `26`
- Percentage: `3.8%`
- Counted item set:
  - Covered endpoint: `HTTP-VAC-CREATE`
  - Validation-only support on same endpoint: `HTTP-VAC-CREATE-VALIDATION`
  - Remaining endpoint Coverage IDs: every other `HTTP-*` row in the controller matrix
- Level represented: MockMvc controller-slice coverage
- Note: one endpoint can have multiple HTTP tests; the denominator counts unique endpoint flows, not test methods.

### 4. Repository queries and database constraints with real DB coverage

- Numerator: `0`
- Denominator: `27`
- Percentage: `0.0%`
- Counted item set:
  - Repository query behaviors: `DB-USER-FIND-BY-EMAIL`, `DB-VAC-FIND-BY-USER-ID`, `DB-VAC-EXISTS-BY-USER-ID-NAME`, `DB-VAC-SEARCH-ALL`, `DB-VAC-SEARCH-BY-USER`, `DB-DAY-FIND-BY-VACATION`, `DB-DAY-FIND-BY-VACATION-ID`, `DB-DAY-FIND-BY-VACATION-AND-ID`, `DB-DAY-FIND-BY-VACATION-AND-DATE`, `DB-DAY-EXISTS-DAY-NUMBER`, `DB-DAY-EXISTS-DATE`, `DB-DAY-FIND-BY-VACATION-ID-DAY-TYPE`, `DB-DAY-SEARCH`, `DB-POI-FIND-BY-PLACE-NAME-IGNORE-CASE`, `DB-POI-FIND-BY-PLACE-ID`, `DB-POI-SEARCH`, `DB-ACT-FIND-BY-DAY`, `DB-ACT-FIND-BY-DAY-ID`, `DB-ACT-FIND-BY-DAY-AND-ID`
  - Database constraints: `DB-CONSTRAINT-USER-EMAIL-UNIQUE`, `DB-CONSTRAINT-VAC-USER-NAME-UNIQUE`, `DB-CONSTRAINT-DAY-VACATION-DATE-UNIQUE`, `DB-CONSTRAINT-DAY-VACATION-DAYNUM-UNIQUE`, `DB-CONSTRAINT-POI-PLACE-ID-UNIQUE`, `DB-CONSTRAINT-POI-PLACE-NAME-UNIQUE`, `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK`, `DB-CONSTRAINT-ACTIVITY-POI-FK`
- Level represented: real DB behavior only

### 5. External-integration behaviors covered beyond mocks

- Numerator: `0`
- Denominator: `8`
- Percentage: `0.0%`
- Counted item set:
  - `GOOGLE-SEARCH-EMPTY-QUERY`
  - `GOOGLE-SEARCH-REQUEST-CONSTRUCTION`
  - `GOOGLE-SEARCH-SUCCESS-PARSE`
  - `GOOGLE-SEARCH-NO-PLACE`
  - `GOOGLE-SEARCH-NO-LOCATION`
  - `GOOGLE-SEARCH-CITY-EXTRACTION`
  - `GOOGLE-SEARCH-COUNTRY-EXTRACTION`
  - `GOOGLE-UPSTREAM-ERRORS`
- Level represented: direct or integration coverage of the external client itself

## Known limitations and production concerns

### Missing automated-test coverage

- No tests for itinerary service or controller (`ITINERARY-GENERATE` and related helper flows)
- No tests for `VacationServiceImpl` list/search/patch/delete/get-by-id/getVacationDays`
- No tests for `VacationDayServiceImpl` update/patch/delete/list/search/get-by-id/list-activities
- No tests for `PointOfInterestController`, `VacationDayController`, `VacationDayActivityController`, or `ItineraryController`
- No real database tests for custom queries, unique constraints, parent-child scoping, cascades, embedded `Place`, or MySQL compatibility
- No real `SecurityConfig` filter-chain tests
- No direct `GooglePlacesClient` tests

### Behavior covered only through mocks

- Duplicate checks in vacation/day/POI services
- Parent-child repository scope (`findByVacationAndId`) in authorization/day/activity flows
- Google failure and success effects in POI/day creation
- Authorization short-circuit behavior in day/activity services
- Save/delete calls across all service unit tests

### Confirmed production behavior

- Vacation create/update enforce `endDate >= startDate`
- Vacation name-unchanged update skips duplicate-name repository lookup
- VacationDay create enforces vacation-range and day-number bounds
- Unauthorized VacationDay create short-circuits before duplicate disclosure
- PointOfInterest create trims the request place name for the initial name lookup
- Finding an existing POI by name or by placeId returns the stored entity without merging metadata from the new request
- PointOfInterest update preserves embedded `Place`
- PointOfInterest update sets `notes` directly from the request, including `null`
- VacationDayActivity update preserves scheduling fields

### Possible production bugs or risky behavior

- `SecurityConfig` POI matcher path uses `/api/v1/points-of-interests/**`, but controller uses `/api/v1/points-of-interest/**`
- `VacationController.patchVacation` lacks `@Valid`
- `VacationDayController.searchVacationDays` accepts `hotelPlaceName`, but service/repository ignore it
- `VacationDayServiceImpl.createVacationDay` performs the Google Places lookup before simple validation failures such as out-of-range dates and invalid day numbers, so invalid requests may trigger an avoidable external call
- `PointOfInterestServiceImpl.deletePointOfInterest` deletes by id without explicit existence check
- `PointOfInterestServiceImpl.createPointOfInterest` checks normalized name before Google placeId resolution, so distinct real places with the same name may collapse into one stored POI
- `PointOfInterestServiceImpl` trims the request only for the first name lookup, but persists `request.getPlaceName()` unchanged, so lookup and stored value normalization are asymmetric
- `VacationDayActivityServiceImpl.createVacationDayActivity` and `deleteVacationDayActivity` operate on the owning side only and do not synchronize the in-memory `VacationDay.activities` inverse collection
- `VacationDayActivityServiceImpl` create, update, and delete do not clear persisted itinerary scheduling fields or regenerate the itinerary, so stored planning data can become stale after activity changes

### Unresolved business decisions

- Whether some ownership failures should remain `403` vs `404` in future HTTP/security tests
- Expected HTTP contract for delete-not-found behaviors that currently depend on repository/JPA behavior rather than explicit service exceptions
- Whether assigning the same `PointOfInterest` to the same `VacationDay` more than once should be allowed; no service-level duplicate check or database unique constraint currently prevents it
- Whether reusing an existing POI by name or placeId should preserve stored metadata or merge newer request metadata
- Whether clearing POI notes by sending `null` in update is an intended contract

## Prioritized testing backlog

| Priority | Production flow / Coverage IDs | Missing testing level | Recommended test type | Why it matters | Expected implementation size | Dependencies |
|---|---|---|---|---|---|---|
| Critical | `ITINERARY-GENERATE`, `ITN-GET-*` | Unit + DB | focused unit tests first, later DB/integration | highest scheduling complexity; currently fully untested | Large | may need stable fixture graph; DB tests later |
| Critical | `SECURITY-FILTER-CHAIN`, `HTTP-VAC-DELETE`, POI write endpoints | HTTP/security | Spring MVC + real `SecurityConfig` | current authz rules and POI path mismatch are unproven | Medium-Large | DB-backed auth fixture strategy |
| Critical | `DAY-UPDATE`, `DAY-PATCH`, `DAY-DELETE` | Unit + HTTP | service unit + controller tests | core mutable day flows completely uncovered | Medium | none |
| High | `POI-CREATE`, `POI-UPDATE`, `POI-DELETE`, `HTTP-POI-*` | HTTP + DB | controller slice + repository/DB tests | write security and uniqueness rules not proven | Medium | DB tests for uniqueness |
| High | `DB-*` repository and constraint rows | DB | `@DataJpaTest` or approved integration setup | all custom queries/constraints currently mocked-only | Large | product approval if infrastructure expands |
| High | `GOOGLE-*` | External integration | focused client tests with mocked transport | request construction and parsing entirely unproven | Medium | no new dependency required if existing `RestClient` can be stubbed |
| High | `VAC-PATCH`, `VAC-DELETE`, `VAC-LIST-ALL`, `VAC-SEARCH` | Unit + HTTP | service + controller tests | common vacation flows missing | Medium | none |
| High | `AUTH-CURRENT-USER`, `AUTH-DAY-ACCESS-BY-DATE`, `AUTH-ACTIVITY-*` | Unit | authorization service tests | core scoped auth helpers under-covered | Small-Medium | none |
| Medium | `HTTP-DAY-*`, `HTTP-ACT-*` | HTTP | controller slice tests | mapping/validation/security gaps | Medium | may later need security fixtures |
| Medium | `CTX-BOOTSTRAP` | Full context | environment-backed smoke | currently skipped in default local baseline | Small | `DB_USERNAME`, `DB_PASSWORD`, reachable MySQL |
| Decision required | `ACT-CREATE`, `ACT-UPDATE`, `ACT-DELETE` — itinerary invalidation after activity mutations | Product + behavior | clarify contract before tests | Adding, replacing, or deleting an activity may leave stored itinerary scheduling data inconsistent with the current activity set. | Small once decided | product decision |

## Inventory denominator appendix

### Public service-method denominator (`39`)

- `AUTH-CURRENT-USER`
- `AUTH-VACATION-ACCESS`
- `AUTH-DAY-ACCESS-BY-ID`
- `AUTH-DAY-ACCESS-BY-DATE`
- `AUTH-DAY-LIST`
- `AUTH-ACTIVITY-ACCESS-BY-ID`
- `AUTH-ACTIVITY-LIST`
- `VAC-LIST-ALL`
- `VAC-SEARCH`
- `VAC-GET-BY-ID`
- `VAC-CREATE`
- `VAC-UPDATE`
- `VAC-PATCH`
- `VAC-GET-DAYS`
- `VAC-DELETE`
- `DAY-LIST`
- `DAY-SEARCH`
- `DAY-GET-BY-ID`
- `DAY-CREATE`
- `DAY-UPDATE`
- `DAY-PATCH`
- `DAY-LIST-ACTIVITIES`
- `DAY-DELETE`
- `POI-LIST`
- `POI-SEARCH`
- `POI-GET-BY-ID`
- `POI-CREATE`
- `POI-UPDATE`
- `POI-DELETE`
- `ACT-LIST`
- `ACT-GET-BY-ID`
- `ACT-CREATE`
- `ACT-UPDATE`
- `ACT-DELETE`
- `ITN-GET-VACATION`
- `ITN-GET-DAYS`
- `ITN-GET-DAY-BY-DATE`
- `ITN-GET-ACTIVITIES`
- `ITINERARY-GENERATE`

### Unique controller-endpoint denominator (`26`)

Unique endpoint count:

- `HTTP-VAC-LIST`
- `HTTP-VAC-SEARCH`
- `HTTP-VAC-GET`
- `HTTP-VAC-CREATE`
- `HTTP-VAC-UPDATE`
- `HTTP-VAC-PATCH`
- `HTTP-VAC-DELETE`
- `HTTP-DAY-LIST`
- `HTTP-DAY-SEARCH`
- `HTTP-DAY-GET`
- `HTTP-DAY-CREATE`
- `HTTP-DAY-UPDATE`
- `HTTP-DAY-PATCH`
- `HTTP-DAY-DELETE`
- `HTTP-ACT-LIST`
- `HTTP-ACT-GET`
- `HTTP-ACT-CREATE`
- `HTTP-ACT-UPDATE`
- `HTTP-ACT-DELETE`
- `HTTP-POI-LIST`
- `HTTP-POI-SEARCH`
- `HTTP-POI-GET`
- `HTTP-POI-CREATE`
- `HTTP-POI-UPDATE`
- `HTTP-POI-DELETE`
- `HTTP-ITINERARY-GENERATE`

HTTP behavior-row count is larger than the unique endpoint count because
`HTTP-VAC-CREATE-VALIDATION` is documented as a separate behavior row for the same
`POST /api/v1/vacations` endpoint and is not counted as an additional unique endpoint.

### Repository-query and database-constraint denominator (`27`)

- `DB-USER-FIND-BY-EMAIL`
- `DB-VAC-FIND-BY-USER-ID`
- `DB-VAC-EXISTS-BY-USER-ID-NAME`
- `DB-VAC-SEARCH-ALL`
- `DB-VAC-SEARCH-BY-USER`
- `DB-DAY-FIND-BY-VACATION`
- `DB-DAY-FIND-BY-VACATION-ID`
- `DB-DAY-FIND-BY-VACATION-AND-ID`
- `DB-DAY-FIND-BY-VACATION-AND-DATE`
- `DB-DAY-EXISTS-DAY-NUMBER`
- `DB-DAY-EXISTS-DATE`
- `DB-DAY-FIND-BY-VACATION-ID-DAY-TYPE`
- `DB-DAY-SEARCH`
- `DB-POI-FIND-BY-PLACE-NAME-IGNORE-CASE`
- `DB-POI-FIND-BY-PLACE-ID`
- `DB-POI-SEARCH`
- `DB-ACT-FIND-BY-DAY`
- `DB-ACT-FIND-BY-DAY-ID`
- `DB-ACT-FIND-BY-DAY-AND-ID`
- `DB-CONSTRAINT-USER-EMAIL-UNIQUE`
- `DB-CONSTRAINT-VAC-USER-NAME-UNIQUE`
- `DB-CONSTRAINT-DAY-VACATION-DATE-UNIQUE`
- `DB-CONSTRAINT-DAY-VACATION-DAYNUM-UNIQUE`
- `DB-CONSTRAINT-POI-PLACE-ID-UNIQUE`
- `DB-CONSTRAINT-POI-PLACE-NAME-UNIQUE`
- `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK`
- `DB-CONSTRAINT-ACTIVITY-POI-FK`

### External-integration denominator (`8`)

- `GOOGLE-SEARCH-EMPTY-QUERY`
- `GOOGLE-SEARCH-REQUEST-CONSTRUCTION`
- `GOOGLE-SEARCH-SUCCESS-PARSE`
- `GOOGLE-SEARCH-NO-PLACE`
- `GOOGLE-SEARCH-NO-LOCATION`
- `GOOGLE-SEARCH-CITY-EXTRACTION`
- `GOOGLE-SEARCH-COUNTRY-EXTRACTION`
- `GOOGLE-UPSTREAM-ERRORS`

## Coverage change log

- `2026-07-28` — Initial coverage map created from the current repository state.
  - Documentation and workflow-governance slice only.
  - Existing production and automated-test coverage documented.
  - Coverage was corrected and clarified, but no new automated behavior coverage was added.
  - Coverage statuses established from repository inspection and the actual baseline Maven run.
  - Baseline command: `.\mvnw.cmd test`
  - Baseline result: `36` tests run, `0` failures, `0` errors, `1` skipped.
  - Skipped test: `SmartvacationplannerApplicationTests` due missing `DB_USERNAME` / `DB_PASSWORD` environment variables for full-context MySQL startup.
  - No production code or test behavior changed.

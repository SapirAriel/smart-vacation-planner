# Backend Test Coverage Map

## Verification metadata

- Last verified: `2026-08-02 17:46 UTC+03:00`
- Verification source: actual Maven run plus repository inspection
- Last focused VacationDay command: `.\mvnw.cmd test "-Dtest=VacationDayServiceImplTest"`
- Last focused VacationDay result: `18` tests run, `0` failures, `0` errors, `0` skipped
- Last focused VacationDayActivity command: `.\mvnw.cmd test "-Dtest=VacationDayActivityServiceImplTest"`
- Last focused VacationDayActivity result: `10` tests run, `0` failures, `0` errors, `0` skipped
- Last full test run command: `.\mvnw.cmd test`
- Total tests run: `59`
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
  - current suite includes mocked service-unit tests, vacation and VacationDay `@WebMvcTest` with `@AutoConfigureMockMvc(addFilters = false)`, POI security `@WebMvcTest` importing production `SecurityConfig`, and one skipped `@SpringBootTest`
  - full suite is green
  - null `hotelPlace` serializes `hotelPlaceName` as JSON `null` (`jsonPath("$.hotelPlaceName").value(nullValue())`)
  - activity and VacationDay hotel-change planning invalidation are covered at mocked unit level only; real JPQL bulk UPDATE SQL and Spring transaction rollback remain unproven

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
| `SECURITY-FILTER-CHAIN` | `SecurityConfig` path and role rules | All `/api/v1/**` | HTTP Basic auth, role-based access, custom 403 JSON, CORS, JDBC user details | `PointOfInterestSecurityTest.getAllPointOfInterests_whenCustomer_reachesServiceAndIsNotRejected`, `PointOfInterestSecurityTest.getPointOfInterestById_whenCustomer_reachesServiceAndIsNotRejected`, `PointOfInterestSecurityTest.createPointOfInterest_whenCustomer_returns403WithCustomAccessDeniedJsonAndDoesNotInvokeService`, `PointOfInterestSecurityTest.updatePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService`, `PointOfInterestSecurityTest.deletePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService`, `PointOfInterestSecurityTest.createPointOfInterest_whenAdmin_reachesServiceAndIsNotForbidden`, `PointOfInterestSecurityTest.getAllPointOfInterests_whenUnauthenticated_returns401AndDoesNotInvokeService` | N/A | Partial | Not covered | N/A | missing vacation/day/activity/itinerary path-rule proofs; JDBC `UserDetailsManager` login path unproven | Critical | POI collection and ID paths proven against real production `SecurityFilterChain` via `@Import(SecurityConfig.class)`; custom access-denied JSON asserted for CUSTOMER POST |

### Vacation

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `VAC-LIST-ALL` | `VacationServiceImpl.getAllVacations()` | `GET /api/v1/vacations` | Admin gets all; customer gets own vacations | None | Not covered | Not covered | Not covered | N/A | missing admin/customer branch coverage, mapping, auth/filter coverage | High | No tests for service or endpoint |
| `VAC-SEARCH` | `VacationServiceImpl.searchVacations(...)` | `GET /api/v1/vacations/page` | Optional filters + admin/customer scoping | None | Not covered | Not covered | Not covered | N/A | missing filter behavior and repo query proof | High | Search query correctness currently unproven |
| `VAC-GET-BY-ID` | `VacationServiceImpl.getVacationById(Integer)` | `GET /api/v1/vacations/{id}` | Delegates to authorization | None | Not covered | Not covered | Not covered | N/A | missing endpoint + auth propagation coverage | High | Auth helper partially covered separately, but not this flow |
| `VAC-CREATE` | `VacationServiceImpl.createVacation(Vacation)` | `POST /api/v1/vacations` | Date validation, current-user assignment, duplicate-name protection, save | `VacationServiceImplTest.createVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException`, `VacationServiceImplTest.createVacation_whenEndDateAfterStartDate_createsVacation`, `VacationServiceImplTest.createVacation_whenNameAlreadyExistsForCurrentUser_throwsDuplicateResourceException`, `VacationControllerHappyPathTest.createVacation_whenValidBody_returns200WithIdAndName`, `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors` | Good at unit level | Partial | Not covered | N/A | missing DB unique-constraint proof, missing auth filter-chain coverage, missing duplicate-name mapping to HTTP 409, missing invalid-date mapping to HTTP 400, missing complete serialized response contract | High | Current HTTP coverage proves basic POST mapping, one basic success response, and required-field validation only |
| `VAC-UPDATE` | `VacationServiceImpl.updateVacation(Integer, Vacation)` | `PUT /api/v1/vacations/{id}` | Full-field update, duplicate-name branch, unchanged owner/id, date validation | `VacationServiceImplTest.updateVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException`, `VacationServiceImplTest.updateVacation_whenEndDateAfterStartDate_updatesVacation`, `VacationServiceImplTest.updateVacation_whenNameAlreadyExistsForSameUser_throwsDuplicateResourceException`, `VacationServiceImplTest.updateVacation_whenNameUnchanged_updatesVacationWithoutDuplicateCheck` | Good at unit level | Not covered | Not covered | N/A | missing HTTP mapping/validation/security, missing DB proof of uniqueness and persistence | High | Name-unchanged short-circuit is well covered at unit level |
| `VAC-PATCH` | `VacationServiceImpl.patchVacation(Integer, Vacation)` | `PATCH /api/v1/vacations/{id}` | Partial update, duplicate check only when name provided, post-merge date validation | None | Not covered | Not covered | Not covered | N/A | missing all service-unit behavior | High | Controller already uses `@Valid` on `PatchVacationRequest`; HTTP budget validation is covered under `HTTP-VAC-PATCH` / `HTTP-VAC-PATCH-VALIDATION` |
| `VAC-GET-DAYS` | `VacationServiceImpl.getVacationDays(Integer)` | None directly; day endpoint lives elsewhere | Authorized vacation then `findByVacation_Id` | None | Not covered | Not covered | Not covered | N/A | missing service behavior and repository proof | Medium | Controller path for days is on `VacationDayController` |
| `VAC-DELETE` | `VacationServiceImpl.deleteVacation(Integer)` | `DELETE /api/v1/vacations/{id}` | Authorized delete; entity cascade expected | None | Not covered | Not covered | Not covered | N/A | missing delete behavior, role restriction, cascade/orphan removal proof | Critical | HTTP DELETE is ADMIN-only in `SecurityConfig`, but not tested |

### VacationDay

| Coverage ID | Production class and method | Endpoint | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `DAY-LIST` | `VacationDayServiceImpl.getAllVacationDays(Integer)` | `GET /api/v1/vacations/{vacationId}/days` | Authorized list by vacation | None | Not covered | Not covered | Not covered | N/A | missing all behavior | Medium | Shared `toResponse` hotelPlaceName mapping is proven only via GET-by-ID under `HTTP-DAY-GET`; this list service/endpoint is not HTTP-tested by that class |
| `DAY-SEARCH` | `VacationDayServiceImpl.searchVacationDays(...)` | `GET /api/v1/vacations/{vacationId}/days/page` | Optional filters: `dayType`, `date`, `dayNumber`, plus `pageable` | None | Not covered | Not covered | Not covered | N/A | missing filter behavior and repo query proof | High | Frontend does not currently call `/page`; endpoint is not defective for that reason. Hotel-name search is not part of this contract |
| `DAY-GET-BY-ID` | `VacationDayServiceImpl.getVacationDayById(Integer, Integer)` | `GET /api/v1/vacations/{vacationId}/days/{id}` | Delegates to scoped auth helper | None | Not covered | Not covered | Not covered | N/A | missing service-unit and auth propagation; HTTP response mapping covered under `HTTP-DAY-GET` | High | Service method itself remains untested; controller `toResponse` hotelPlaceName contract proven via shared mapper on GET-by-ID |
| `DAY-CREATE` | `VacationDayServiceImpl.createVacationDay(Integer, CreateVacationDayRequest)` | `POST /api/v1/vacations/{vacationId}/days` | Authorization → local date/dayNumber validation → duplicate day-number check → duplicate date check → Google hotel lookup → entity creation → save; invalid date/dayNumber short-circuits before duplicate queries, Google, and save | `VacationDayServiceImplTest.createVacationDay_whenDateEqualsVacationStartDate_createsVacationDay`, `VacationDayServiceImplTest.createVacationDay_whenDateEqualsVacationEndDate_createsVacationDay`, `VacationDayServiceImplTest.createVacationDay_whenDateBeforeVacationStartDate_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDateAfterVacationEndDate_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberAlreadyExists_throwsDuplicateResourceException`, `VacationDayServiceImplTest.createVacationDay_whenDateAlreadyExists_throwsDuplicateResourceException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberIsZero_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenDayNumberExceedsVacationDuration_throwsIllegalArgumentException`, `VacationDayServiceImplTest.createVacationDay_whenVacationBelongsToAnotherUser_throwsAccessDeniedException`, `VacationDayServiceImplTest.createVacationDay_whenUnauthorizedAndDuplicateDataExists_throwsAccessDeniedException` | Good at unit level | Not covered | Not covered | Not covered | missing HTTP mapping/validation/security, missing DB uniqueness proof, missing real Google integration, missing save-to-DB semantics | Critical | Google-dependent service behavior is exercised only through a mocked `GooglePlacesClient`; validation short-circuit before duplicates/Google/save is proven at unit level |
| `DAY-UPDATE` | `VacationDayServiceImpl.updateVacationDay(Integer, Integer, UpdateVacationDayRequest)` | `PUT /api/v1/vacations/{vacationId}/days/{id}` | Auth → null-safe hotel compare → Google only when hotel changes → set dayType → save → `clearPlanningDataByVacationDayId` only when hotel changed (`@Transactional`) | `VacationDayServiceImplTest.updateVacationDay_whenHotelChanges_invalidatesPlanning`, `VacationDayServiceImplTest.updateVacationDay_whenHotelUnchanged_doesNotInvalidatePlanning`, `VacationDayServiceImplTest.updateVacationDay_whenGoogleLookupFails_doesNotInvalidatePlanning`, `VacationDayServiceImplTest.updateVacationDay_whenClearPlanningDataThrows_propagatesException` | Partial | Not covered | Not covered | Not covered | missing HTTP/security; date/dayNumber remain immutable; real bulk UPDATE/rollback unproven | Critical | dayType-only change does not invalidate; hotel change uses repository bulk clear, not in-memory collection |
| `DAY-PATCH` | `VacationDayServiceImpl.patchVacationDay(Integer, Integer, PatchVacationDayRequest)` | `PATCH /api/v1/vacations/{vacationId}/days/{id}` | Auth → optional dayType; hotel only when supplied and changed → save → bulk clear only when hotel changed (`@Transactional`); null hotel means omit | `VacationDayServiceImplTest.patchVacationDay_whenHotelChanges_invalidatesPlanning`, `VacationDayServiceImplTest.patchVacationDay_whenOnlyDayTypeChanges_doesNotInvalidatePlanning`, `VacationDayServiceImplTest.patchVacationDay_whenHotelUnchanged_doesNotInvalidatePlanning`, `VacationDayServiceImplTest.patchVacationDay_whenGoogleLookupFails_doesNotInvalidatePlanning` | Partial | Not covered | Not covered | Not covered | missing HTTP/security; real bulk UPDATE/rollback unproven | High | Absent/null hotelPlaceName is not a change under current DTO contract |
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
| `ACT-CREATE` | `VacationDayActivityServiceImpl.createVacationDayActivity(...)` | `POST /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | Auth → POI load → duplicate POI check → `addActivity` + save → atomic `clearPlanningDataByVacationDayId` + in-memory `clearPlanningData`; short-circuit on auth/missing POI/duplicate | `VacationDayActivityServiceImplTest.createVacationDayActivity_whenOwnedDayAndExistingPointOfInterest_savesActivityAndInvalidatesScheduling`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenPointOfInterestAlreadyAssignedToDay_throwsDuplicateResourceExceptionAndDoesNotSaveOrInvalidate`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenPointOfInterestDoesNotExist_throwsResourceNotFoundExceptionAndDoesNotSave`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenVacationBelongsToAnotherUser_throwsAccessDeniedExceptionAndDoesNotLookupPointOfInterest`, `VacationDayActivityServiceImplTest.createVacationDayActivity_whenClearPlanningDataThrows_propagatesException` | Good at unit level | Not covered | Not covered | N/A | missing HTTP/security/DB proof; real bulk UPDATE and transaction rollback unproven | High | Service-level duplicate prevention and schedule invalidation covered; Mockito clear-failure test does not prove rollback |
| `ACT-UPDATE` | `VacationDayActivityServiceImpl.updateVacationDayActivity(...)` | `PUT /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Auth → POI load → duplicate check excluding current activity via day entity id → replace POI → save → atomic invalidate; does not re-add to inverse collection | `VacationDayActivityServiceImplTest.updateVacationDayActivity_whenAccessibleActivityAndExistingPointOfInterest_replacesPointOfInterestAndInvalidatesScheduling`, `VacationDayActivityServiceImplTest.updateVacationDayActivity_whenPointOfInterestAlreadyUsedByAnotherActivity_throwsDuplicateResourceExceptionAndDoesNotMutate`, `VacationDayActivityServiceImplTest.updateVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotLookupPointOfInterest` | Partial | Not covered | Not covered | N/A | missing update-with-missing-POI path, HTTP/security/DB proof | High | Same-POI update allowed via `IdNot`; scheduling cleared after successful update; itinerary is not auto-regenerated |
| `ACT-DELETE` | `VacationDayActivityServiceImpl.deleteVacationDayActivity(...)` | `DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Auth → remove from inverse collection (no FK null) → orphanRemoval delete on flush → atomic `clearPlanningDataByVacationDayId`; no explicit `repository.delete` | `VacationDayActivityServiceImplTest.deleteVacationDayActivity_whenActivityIsAccessible_removesFromCollectionAndInvalidatesScheduling`, `VacationDayActivityServiceImplTest.deleteVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotDelete` | Good at unit level | Not covered | Not covered | N/A | missing HTTP/security/DB proof; real orphanRemoval flush + bulk UPDATE interaction unproven | Medium | Explicit child delete removed to avoid double-delete under `@Transactional` |

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
| `HTTP-VAC-CREATE` | `POST /api/v1/vacations` | Valid request returns vacation response | `VacationControllerHappyPathTest.createVacation_whenValidBody_returns200WithIdAndName` | N/A | Partial | Not covered | N/A | security filter-chain, full response fields, duplicate/date error mappings | High | Controller-slice uses `@AutoConfigureMockMvc(addFilters = false)`; not security-filter coverage |
| `HTTP-VAC-CREATE-VALIDATION` | `POST /api/v1/vacations` | Invalid body -> 400 with field errors | `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors` | N/A | Good at HTTP level | Not covered | N/A | more validation variants | Medium | Controller-slice uses `@AutoConfigureMockMvc(addFilters = false)`; not security-filter coverage |
| `HTTP-VAC-UPDATE` | `PUT /api/v1/vacations/{id}` | Validated full update mapping | None | N/A | Not covered | Not covered | N/A | mapping, validation, security | High | |
| `HTTP-VAC-PATCH` | `PATCH /api/v1/vacations/{id}` | Partial update binding; positive budget PATCH; omitted budget allowed | `VacationControllerPatchValidationTest.patchVacation_whenBudgetIsPositive_invokesService`, `VacationControllerPatchValidationTest.patchVacation_whenBudgetIsOmitted_doesNotFailBudgetValidation` | N/A | Partial | Not covered | N/A | missing name/date/other field PATCH paths, security, service-level date/duplicate mapping | High | Controller-slice uses `@AutoConfigureMockMvc(addFilters = false)`; not security-filter coverage |
| `HTTP-VAC-PATCH-VALIDATION` | `PATCH /api/v1/vacations/{id}` | Invalid budget (`@Positive`) -> 400 with field errors; service not invoked | `VacationControllerPatchValidationTest.patchVacation_whenBudgetIsZero_returns400AndDoesNotInvokeService` | N/A | Good at HTTP level | Not covered | N/A | other PATCH field constraints if added later | Medium | Same unique endpoint as `HTTP-VAC-PATCH`; proves existing `@Valid` activates `PatchVacationRequest` constraints |
| `HTTP-VAC-DELETE` | `DELETE /api/v1/vacations/{id}` | Delete mapping and security role rule | None | N/A | Not covered | Not covered | N/A | admin-only filter behavior, response contract | Critical | |
| `HTTP-DAY-LIST` | `GET /api/v1/vacations/{vacationId}/days` | Day list mapping | None | N/A | Not covered | Not covered | N/A | list endpoint HTTP behavior; security | Medium | Shared private `toResponse` hotelPlaceName mapping is proven via GET-by-ID only; this list endpoint was not HTTP-tested |
| `HTTP-DAY-SEARCH` | `GET /api/v1/vacations/{vacationId}/days/page` | Query binding for `dayType`, `date`, `dayNumber`, pageable | None | N/A | Not covered | Not covered | N/A | binding, paging/sort, security; hotel-name filtering is not supported | High | Not called by current frontend; no production removal approved |
| `HTTP-DAY-GET` | `GET /api/v1/vacations/{vacationId}/days/{id}` | Path binding + `toResponse` including `hotelPlaceName` when hotel Place present; null hotel serializes as JSON null | `VacationDayControllerResponseMappingTest.getVacationDayById_whenHotelPlaceExists_returnsHotelPlaceName`, `VacationDayControllerResponseMappingTest.getVacationDayById_whenHotelPlaceIsNull_returns200WithoutMappingFailure` | N/A | Partial | Not covered | N/A | missing not-found/denied mappings, security filter-chain, full field contract beyond id/vacationId/hotelPlaceName | Medium | Proves existing shared `toResponse` hotelPlaceName mapping; `@AutoConfigureMockMvc(addFilters = false)` — not security-filter coverage |
| `HTTP-DAY-CREATE` | `POST /api/v1/vacations/{vacationId}/days` | `@Valid CreateVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | validation, errors, security | High | |
| `HTTP-DAY-UPDATE` | `PUT /api/v1/vacations/{vacationId}/days/{id}` | `@Valid UpdateVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-DAY-PATCH` | `PATCH /api/v1/vacations/{vacationId}/days/{id}` | `@Valid PatchVacationDayRequest` | None | N/A | Not covered | Not covered | N/A | mapping/security | High | |
| `HTTP-DAY-DELETE` | `DELETE /api/v1/vacations/{vacationId}/days/{id}` | Delete mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-LIST` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | Activity list mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-GET` | `GET /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Activity get mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-ACT-CREATE` | `POST /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities` | `@Valid` request with `pointOfInterestId` | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-ACT-UPDATE` | `PUT /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | `@Valid` update request | None | N/A | Not covered | Not covered | N/A | validation, security | High | |
| `HTTP-ACT-DELETE` | `DELETE /api/v1/vacations/{vacationId}/days/{vacationDayId}/activities/{id}` | Delete mapping | None | N/A | Not covered | Not covered | N/A | all behavior | Medium | |
| `HTTP-POI-LIST` | `GET /api/v1/points-of-interest` | List mapping; CUSTOMER allow; unauthenticated 401 | `PointOfInterestSecurityTest.getAllPointOfInterests_whenCustomer_reachesServiceAndIsNotRejected`, `PointOfInterestSecurityTest.getAllPointOfInterests_whenUnauthenticated_returns401AndDoesNotInvokeService` | N/A | Partial | Not covered | N/A | missing ADMIN GET, empty/non-empty payload contracts beyond security smoke, DB | Medium | SecurityFilterChain collection-path match proven for CUSTOMER and anonymous |
| `HTTP-POI-SEARCH` | `GET /api/v1/points-of-interest/search` | Query binding and pageable sort | None | N/A | Not covered | Not covered | N/A | all behavior | High | |
| `HTTP-POI-GET` | `GET /api/v1/points-of-interest/{id}` | Path binding; CUSTOMER allow on ID path | `PointOfInterestSecurityTest.getPointOfInterestById_whenCustomer_reachesServiceAndIsNotRejected` | N/A | Partial | Not covered | N/A | missing not-found HTTP mapping, ADMIN GET, unauthenticated item GET | Medium | Proves `/points-of-interest/{id}` matcher with real SecurityFilterChain |
| `HTTP-POI-CREATE` | `POST /api/v1/points-of-interest` | ADMIN allow; CUSTOMER 403 custom JSON; service short-circuit | `PointOfInterestSecurityTest.createPointOfInterest_whenCustomer_returns403WithCustomAccessDeniedJsonAndDoesNotInvokeService`, `PointOfInterestSecurityTest.createPointOfInterest_whenAdmin_reachesServiceAndIsNotForbidden` | N/A | Partial | Not covered | N/A | missing bean-validation HTTP cases, duplicate/Google error mapping | High | Write-rule proof uses production SecurityConfig, not a test-only chain |
| `HTTP-POI-UPDATE` | `PUT /api/v1/points-of-interest/{id}` | CUSTOMER forbidden on item PUT | `PointOfInterestSecurityTest.updatePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService` | N/A | Partial | Not covered | N/A | missing ADMIN PUT allow, validation, not-found | High | CUSTOMER deny only; ADMIN PUT not separately proven |
| `HTTP-POI-DELETE` | `DELETE /api/v1/points-of-interest/{id}` | CUSTOMER forbidden on item DELETE | `PointOfInterestSecurityTest.deletePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService` | N/A | Partial | Not covered | N/A | missing ADMIN DELETE allow, not-found semantics | High | CUSTOMER deny only; ADMIN DELETE not separately proven |
| `HTTP-ITINERARY-GENERATE` | `POST /api/v1/vacations/{vacationId}/itineraries` | Endpoint mapping for itinerary generation | None | N/A | Not covered | Not covered | N/A | all behavior and security | Critical | |

### Exception handling

| Coverage ID | Production flow | Main behavior and important branches | Exact existing test methods | Service unit status | HTTP status | DB status | External integration status | Missing behavior | Priority | Notes or production concerns |
|---|---|---|---|---|---|---|---|---|---|
| `ERR-HANDLER-VALIDATION` | `GlobalExceptionHandler.handleValidationExceptions` | 400 with `Validation failed`, path, status, field errors | `VacationControllerValidationTest.createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors`, `VacationControllerPatchValidationTest.patchVacation_whenBudgetIsZero_returns400AndDoesNotInvokeService` | N/A | Good at HTTP level | Not covered | N/A | more validation cases and controllers | Medium | Proven for create required-field and PATCH budget `@Positive` failures with filters disabled |
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
| `DB-DAY-EXISTS-DAY-NUMBER` | `VacationDayRepository.existsByVacationIdAndDayNumber` | Duplicate day-number protection | Indirect via unit mocks in `VacationDayServiceImplTest` | Partial | Not covered | Not covered | N/A | real uniqueness proof | High | Service-unit tests also prove this query is not invoked when local VacationDay validation fails; not real DB coverage |
| `DB-DAY-EXISTS-DATE` | `VacationDayRepository.existsByVacationIdAndDate` | Duplicate date protection | Indirect via unit mocks in `VacationDayServiceImplTest` | Partial | Not covered | Not covered | N/A | real uniqueness proof | High | Service-unit tests also prove this query is not invoked when local VacationDay validation fails; not real DB coverage |
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
| `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK` | `vacation_day_activities.vacation_day_id not null FK` | Required parent VacationDay foreign key only | None | Not covered | Not covered | Not covered | N/A | real NOT NULL / FK proof | Medium | Does not represent day+POI uniqueness; delete avoids nulling the owning side before orphanRemoval flush |
| `DB-CONSTRAINT-ACTIVITY-POI-FK` | `vacation_day_activities.point_of_interest_id not null FK` | POI relationship required | None | Not covered | Not covered | Not covered | N/A | all behavior | Medium | |
| `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE` | Potential unique `(vacation_day_id, point_of_interest_id)` | Prevent duplicate POI assignment per VacationDay at DB level | None | Not covered | Not covered | Not covered | N/A | no unique constraint exists; no real DB uniqueness test | High | Service-level duplicate prevention exists on create/update; concurrent or out-of-service writes remain unprotected; no migration added |

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

- Numerator: `12`
- Denominator: `39`
- Percentage: `30.8%`
- Counted item set:
  - Covered: `AUTH-VACATION-ACCESS`, `AUTH-DAY-ACCESS-BY-ID`, `VAC-CREATE`, `VAC-UPDATE`, `DAY-CREATE`, `DAY-UPDATE`, `DAY-PATCH`, `POI-CREATE`, `POI-UPDATE`, `ACT-CREATE`, `ACT-UPDATE`, `ACT-DELETE`
  - Not yet meaningfully covered: all remaining service Coverage IDs in the service-domain tables above
- Level represented: unit tests with mocks

### 2. Branch-level coverage note

A branch-level percentage is intentionally not published in this baseline.

It will be added only after a complete, versioned branch inventory is created across both
covered and uncovered production flows. The current repository has meaningful branch
coverage in some unit tests, but not yet a systematic branch denominator that is safe to
publish as a percentage.

### 3. Controller endpoints with meaningful HTTP coverage

- Numerator: `8`
- Denominator: `26`
- Percentage: `30.8%`
- Counted item set:
  - Covered endpoints: `HTTP-VAC-CREATE`, `HTTP-VAC-PATCH`, `HTTP-DAY-GET`, `HTTP-POI-LIST`, `HTTP-POI-GET`, `HTTP-POI-CREATE`, `HTTP-POI-UPDATE`, `HTTP-POI-DELETE`
  - Validation-only support on same endpoints: `HTTP-VAC-CREATE-VALIDATION`, `HTTP-VAC-PATCH-VALIDATION` (not counted as additional unique endpoints)
  - Remaining endpoint Coverage IDs: every other `HTTP-*` row in the controller matrix
- Level represented: MockMvc controller-slice coverage
- Note: one endpoint can have multiple HTTP tests; the denominator counts unique endpoint flows, not test methods. Vacation and VacationDay controller slices intentionally disable security filters; POI security coverage uses the real `SecurityFilterChain`. Shared `toResponse` reuse does not count as additional endpoints.

### 4. Repository queries and database constraints with real DB coverage

- Numerator: `0`
- Denominator: `28`
- Percentage: `0.0%`
- Counted item set:
  - Repository query behaviors: `DB-USER-FIND-BY-EMAIL`, `DB-VAC-FIND-BY-USER-ID`, `DB-VAC-EXISTS-BY-USER-ID-NAME`, `DB-VAC-SEARCH-ALL`, `DB-VAC-SEARCH-BY-USER`, `DB-DAY-FIND-BY-VACATION`, `DB-DAY-FIND-BY-VACATION-ID`, `DB-DAY-FIND-BY-VACATION-AND-ID`, `DB-DAY-FIND-BY-VACATION-AND-DATE`, `DB-DAY-EXISTS-DAY-NUMBER`, `DB-DAY-EXISTS-DATE`, `DB-DAY-FIND-BY-VACATION-ID-DAY-TYPE`, `DB-DAY-SEARCH`, `DB-POI-FIND-BY-PLACE-NAME-IGNORE-CASE`, `DB-POI-FIND-BY-PLACE-ID`, `DB-POI-SEARCH`, `DB-ACT-FIND-BY-DAY`, `DB-ACT-FIND-BY-DAY-ID`, `DB-ACT-FIND-BY-DAY-AND-ID`
  - Database constraints: `DB-CONSTRAINT-USER-EMAIL-UNIQUE`, `DB-CONSTRAINT-VAC-USER-NAME-UNIQUE`, `DB-CONSTRAINT-DAY-VACATION-DATE-UNIQUE`, `DB-CONSTRAINT-DAY-VACATION-DAYNUM-UNIQUE`, `DB-CONSTRAINT-POI-PLACE-ID-UNIQUE`, `DB-CONSTRAINT-POI-PLACE-NAME-UNIQUE`, `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK`, `DB-CONSTRAINT-ACTIVITY-POI-FK`, `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE`
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
- No non-security `PointOfInterestController` mapping/validation tests beyond `PointOfInterestSecurityTest`; VacationDay controller coverage is limited to GET-by-ID response mapping (`VacationDayControllerResponseMappingTest`); no tests for `VacationDayActivityController` or `ItineraryController`
- No real database tests for custom queries, unique constraints, parent-child scoping, cascades, embedded `Place`, or MySQL compatibility
- No real `SecurityConfig` filter-chain tests beyond the POI path proofs in `PointOfInterestSecurityTest`
- No direct `GooglePlacesClient` tests

### Behavior covered only through mocks

- Duplicate checks in vacation/day/POI/activity services
- Activity and VacationDay hotel-change schedule invalidation via mocked `clearPlanningDataByVacationDayId`
- Parent-child repository scope (`findByVacationAndId`) in authorization/day/activity flows
- Google failure and success effects in POI/day creation and day hotel update/patch
- Authorization short-circuit behavior in day/activity services
- Save/delete/orphanRemoval-collection calls across all service unit tests

### Confirmed production behavior

- Vacation create/update enforce `endDate >= startDate`
- Vacation name-unchanged update skips duplicate-name repository lookup
- VacationDay create enforces vacation-range and day-number bounds
- VacationDay create local validation short-circuits before duplicate repository queries and Google Places lookup
- Unauthorized VacationDay create short-circuits before duplicate disclosure
- Vacation PATCH `@Valid` activates `PatchVacationRequest` `@Positive` budget validation before service invocation
- PointOfInterest GET collection and GET by id allow CUSTOMER (and ADMIN) through production SecurityFilterChain matchers on `/api/v1/points-of-interest` and `/api/v1/points-of-interest/{id}`
- PointOfInterest POST/PUT/DELETE require ADMIN; CUSTOMER receives SecurityConfig custom 403 JSON (`message` / `status`)
- Unauthenticated PointOfInterest GET returns 401 and does not invoke the service
- PointOfInterest create trims the request place name for the initial name lookup
- Finding an existing POI by name or by placeId returns the stored entity without merging metadata from the new request
- PointOfInterest update preserves embedded `Place`
- PointOfInterest update sets `notes` directly from the request, including `null`
- `VacationDayController.toResponse` populates `VacationDayResponse.hotelPlaceName` from embedded `hotelPlace.placeName` when present; when `hotelPlace` is null, Jackson serializes `hotelPlaceName` as JSON `null`
- VacationDayActivity create synchronizes `VacationDay.vacationDayActivities` via `addActivity` and sets the owning `vacationDay` reference
- VacationDayActivity update replaces PointOfInterest only and does not re-add the activity to the inverse collection
- VacationDayActivity create and update reject duplicate PointOfInterest assignment on the same VacationDay with `DuplicateResourceException` (`Point of interest is already assigned to this vacation day`)
- VacationDayActivity create, update, and delete run under method-level `@Transactional` and clear day scheduling fields via `clearPlanningDataByVacationDayId` after successful mutation; returned create/update entities also call `clearPlanningData()`
- VacationDayActivity delete removes from the inverse collection without nulling the owning FK and relies on `orphanRemoval` (no explicit `repository.delete`)
- VacationDay update/patch run under method-level `@Transactional` and call `clearPlanningDataByVacationDayId` only when the hotel place name actually changes; dayType-only changes do not invalidate under the current itinerary algorithm
- `VacationDay.invalidateItinerary()` was removed; durable invalidation uses the repository bulk UPDATE only

### Possible production bugs or risky behavior

- `PointOfInterestServiceImpl.deletePointOfInterest` deletes by id without explicit existence check
- `PointOfInterestServiceImpl.createPointOfInterest` checks normalized name before Google placeId resolution, so distinct real places with the same name may collapse into one stored POI
- `PointOfInterestServiceImpl` trims the request only for the first name lookup, but persists `request.getPlaceName()` unchanged, so lookup and stored value normalization are asymmetric
- No database unique constraint on `(vacation_day_id, point_of_interest_id)`; concurrent or non-service writers can still create duplicate POI assignments (`DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE`)
- Real orphanRemoval flush + JPQL bulk schedule invalidation under a managed persistence context remains unproven without DB/integration tests
- No explicit VacationDay scheduling-status field; clients must treat cleared planning fields as “needs regeneration” until `generateItinerary` is run again

### Unresolved business decisions

- Whether some ownership failures should remain `403` vs `404` in future HTTP/security tests
- Expected HTTP contract for delete-not-found behaviors that currently depend on repository/JPA behavior rather than explicit service exceptions
- Whether reusing an existing POI by name or placeId should preserve stored metadata or merge newer request metadata
- Whether clearing POI notes by sending `null` in update is an intended contract
- Whether to add an explicit VacationDay / activity scheduling-status model and selective itinerary regeneration after activity mutations

## Prioritized testing backlog

| Priority | Production flow / Coverage IDs | Missing testing level | Recommended test type | Why it matters | Expected implementation size | Dependencies |
|---|---|---|---|---|---|---|
| Critical | `ITINERARY-GENERATE`, `ITN-GET-*` | Unit + DB | focused unit tests first, later DB/integration | highest scheduling complexity; currently fully untested | Large | may need stable fixture graph; DB tests later |
| Critical | `SECURITY-FILTER-CHAIN` remaining paths, `HTTP-VAC-DELETE` | HTTP/security | Spring MVC + real `SecurityConfig` | vacation/day/activity/itinerary authz rules still unproven | Medium-Large | none for MockMvc+WithMockUser; JDBC login still separate |
| Critical | `DAY-DELETE` remaining gaps; `DAY-UPDATE` / `DAY-PATCH` HTTP and DB proof | Unit + HTTP | controller / DB tests | day mutate HTTP/security and persistence still unproven beyond hotel-invalidation unit coverage | Medium | none |
| High | `POI-CREATE`, `POI-UPDATE`, `POI-DELETE`, remaining `HTTP-POI-*` gaps | HTTP + DB | controller validation/DB tests; ADMIN PUT/DELETE allow | write uniqueness and non-security HTTP contracts not proven | Medium | DB tests for uniqueness |
| High | `DB-*` repository and constraint rows incl. `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE` | DB | `@DataJpaTest` or approved integration setup | all custom queries/constraints currently mocked-only; day+POI unique constraint absent | Large | product approval if infrastructure expands |
| High | Activity/day mutation integration: save + `clearPlanningDataByVacationDayId` rollback and real JPQL bulk UPDATE | DB/integration | Spring transactional integration tests | unit tests cannot prove rollback or bulk SQL | Medium | reachable DB / testcontainers approval |
| High | `GOOGLE-*` | External integration | focused client tests with mocked transport | request construction and parsing entirely unproven | Medium | no new dependency required if existing `RestClient` can be stubbed |
| High | `VAC-PATCH`, `VAC-DELETE`, `VAC-LIST-ALL`, `VAC-SEARCH` | Unit + HTTP | service + controller tests | common vacation flows missing | Medium | none |
| High | `AUTH-CURRENT-USER`, `AUTH-DAY-ACCESS-BY-DATE`, `AUTH-ACTIVITY-*` | Unit | authorization service tests | core scoped auth helpers under-covered | Small-Medium | none |
| Medium | `HTTP-DAY-*`, `HTTP-ACT-*` | HTTP | controller slice tests | mapping/validation/security gaps | Medium | may later need security fixtures |
| Medium | `CTX-BOOTSTRAP` | Full context | environment-backed smoke | currently skipped in default local baseline | Small | `DB_USERNAME`, `DB_PASSWORD`, reachable MySQL |
| Decision required | Explicit scheduling-status model + selective regeneration after activity mutations | Product + behavior | clarify contract before tests | Planning fields are cleared after mutations, but `generateItinerary` is not auto-run and no day-level status flag exists | Medium once decided | product decision |

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
`HTTP-VAC-CREATE-VALIDATION` and `HTTP-VAC-PATCH-VALIDATION` are documented as separate
behavior rows for the same vacation endpoints and are not counted as additional unique
endpoints.

### Repository-query and database-constraint denominator (`28`)

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
- `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE`

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

- `2026-08-02` — Production: VacationDay update/patch hotel-change planning invalidation (expanded slice).
  - `updateVacationDay` / `patchVacationDay` are `@Transactional`; remove early in-memory `invalidateItinerary()`; invalidate via `clearPlanningDataByVacationDayId` only when hotel name actually changes (null-safe `Objects.equals`).
  - dayType-only changes save but do not invalidate; Google/auth failures do not clear planning.
  - Removed unused `VacationDay.invalidateItinerary()`; kept `addActivity` / `removeActivity` and `VacationDayActivity.clearPlanningData`.
  - Added 8 `VacationDayServiceImplTest` scenarios for update/patch invalidation branching.
  - Coverage IDs: `DAY-UPDATE` and `DAY-PATCH` → Partial; `ACT-*` and `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE` notes retained.
  - Service-unit metric: `10/39` (`25.6%`) → `12/39` (`30.8%`). HTTP `8/26`, DB `0/28` (`0.0%`), Google unchanged.
  - Focused day: `.\mvnw.cmd test "-Dtest=VacationDayServiceImplTest"` → `18` run, `0` failures, `0` errors, `0` skipped.
  - Focused activity: `.\mvnw.cmd test "-Dtest=VacationDayActivityServiceImplTest"` → `10` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `59` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-08-02` — Production: atomic duplicate-POI prevention + day schedule invalidation for VacationDayActivity.
  - Added repository `existsByVacationDay_IdAndPointOfInterest_Id`, `existsByVacationDay_IdAndPointOfInterest_IdAndIdNot`, and `clearPlanningDataByVacationDayId` (`@Modifying(flushAutomatically = true, clearAutomatically = true)`).
  - Create/update/delete are `@Transactional`; mutation and schedule invalidation share one transaction.
  - Delete uses orphanRemoval via `removeActivity` only (no explicit `repository.delete`); owning FK is not nulled.
  - Returned create/update entities call `clearPlanningData()` because JPQL bulk updates bypass managed object state.
  - Strengthened `VacationDayActivityServiceImplTest` (10 tests) for success, duplicate, auth short-circuit, and clear-failure propagation (does not prove real rollback).
  - Coverage IDs: `ACT-CREATE`, `ACT-UPDATE`, `ACT-DELETE` notes updated (statuses unchanged: Good / Partial / Good).
  - Added `DB-CONSTRAINT-ACTIVITY-DAY-POI-UNIQUE` (Not covered); DB denominator `27` → `28`, numerator still `0` (`0.0%`).
  - Closed stale-scheduling concern after successful activity mutation; service-level duplicate decided; DB unique still absent; `generateItinerary` unchanged.
  - Service-unit, HTTP, and Google metrics unchanged.
  - Focused: `.\mvnw.cmd test "-Dtest=VacationDayActivityServiceImplTest"` → `10` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `51` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-08-02` — Production bidirectional sync correction for VacationDay ↔ VacationDayActivity.
  - `VacationDay.removeActivity` now removes from the inverse collection only (no `setVacationDay(null)`), avoiding a non-null FK nulling transition before explicit delete.
  - `VacationDayActivityServiceImpl.updateVacationDayActivity` no longer calls `addActivity` (prevents ArrayList duplicates).
  - Create keeps `addActivity` + repository save; delete keeps collection sync + explicit repository delete.
  - Strengthened `VacationDayActivityServiceImplTest` create/update/delete assertions for inverse-collection consistency.
  - Coverage IDs updated (notes only): `ACT-CREATE`, `ACT-UPDATE`, `ACT-DELETE`, `DB-CONSTRAINT-ACTIVITY-VACATIONDAY-FK`.
  - Removed stale concern that create/delete do not synchronize the inverse collection; itinerary-invalidation concern kept.
  - Coverage statuses and inventory-based percentages unchanged (service-unit / HTTP / DB / Google metrics unchanged).
  - Focused: `.\mvnw.cmd test "-Dtest=VacationDayActivityServiceImplTest"` → `7` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `48` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-07-30` — New HTTP response-mapping coverage for VacationDay GET-by-ID (`hotelPlaceName` contract).
  - Added `VacationDayControllerResponseMappingTest` (hotel present → `hotelPlaceName`; hotel null → 200 with JSON null `hotelPlaceName`).
  - Production already contained the `toResponse` hotelPlaceName mapping; no production code changed.
  - Stale documentation concern removed (claim that `toResponse` does not populate `hotelPlaceName`); corresponding backlog row closed/removed.
  - Coverage IDs updated: `HTTP-DAY-GET` → Partial; notes only on `HTTP-DAY-LIST`, `DAY-LIST`, `DAY-GET-BY-ID` about shared mapper (statuses remain Not covered). List/create/update endpoints not claimed as HTTP-tested.
  - Unique HTTP-endpoint metric: `7/26` (`26.9%`) → `8/26` (`30.8%`). Service-unit, DB, and Google metrics unchanged.
  - Focused: `.\mvnw.cmd test "-Dtest=VacationDayControllerResponseMappingTest"` → `2` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `48` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-07-30` — Docs-only cleanup after hotel-related VacationDay API audit.
  - Removed stale claim that `VacationDayController.searchVacationDays` accepts unused `hotelPlaceName` search filter (not present in current production code).
  - Clarified `DAY-SEARCH` / `HTTP-DAY-SEARCH` supported filters: `dayType`, `date`, `dayNumber`, `pageable` only; hotel-name filtering is not part of the contract.
  - Noted `/days/page` is not currently called by the frontend; no production removal approved.
  - Previously identified response-contract mapping gap for `hotelPlaceName` was later disproven by production inspection and closed by HTTP GET-by-ID response-mapping coverage (see newer change-log entry).
  - No production code changed; no tests added in that docs-only slice; no coverage metrics or `DAY-SEARCH` / `HTTP-DAY-SEARCH` statuses changed.
- `2026-07-30` — New HTTP behavior coverage for the existing Vacation PATCH validation contract.
  - Added `VacationControllerPatchValidationTest` (budget zero -> 400; positive budget -> 200; omitted budget allowed).
  - Production already had `@Valid` on `VacationController.patchVacation`; no production code changed in this slice.
  - Added Coverage ID row `HTTP-VAC-PATCH-VALIDATION`; updated `HTTP-VAC-PATCH`, `ERR-HANDLER-VALIDATION`, `VAC-PATCH` notes.
  - Removed stale concern claiming `VacationController.patchVacation` lacks `@Valid`.
  - `SECURITY-FILTER-CHAIN` unchanged (filters disabled in this controller-slice class).
  - Unique HTTP-endpoint metric: `6/26` (`23.1%`) → `7/26` (`26.9%`). Service-unit, DB, and Google metrics unchanged.
  - Focused: `.\mvnw.cmd test "-Dtest=VacationControllerPatchValidationTest"` → `3` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `46` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-07-30` — Vacation controller-slice infrastructure alignment after security-test starter activation; finalize green full suite and HTTP metric.
  - Pure test-infrastructure alignment: added `@AutoConfigureMockMvc(addFilters = false)` to `VacationControllerHappyPathTest` and `VacationControllerValidationTest` so they remain mapping/validation tests, not security-contract tests.
  - No production code changed; no security rules changed; filters remain enabled in `PointOfInterestSecurityTest`.
  - New POI HTTP/security coverage from `PointOfInterestSecurityTest` remains in place (`SECURITY-FILTER-CHAIN`, `HTTP-POI-LIST`, `HTTP-POI-GET`, `HTTP-POI-CREATE`, `HTTP-POI-UPDATE`, `HTTP-POI-DELETE` stay Partial).
  - Restored Vacation/ERR statuses after temporary blocked notes: `HTTP-VAC-CREATE` Partial; `HTTP-VAC-CREATE-VALIDATION` and `ERR-HANDLER-VALIDATION` Good at HTTP level.
  - Unique HTTP-endpoint metric recovered from intermediate recorded `5/26` (`19.2%`) to final `6/26` (`23.1%`) once vacation create coverage is again verified by a green suite.
  - Service-unit, DB, and Google metrics unchanged.
  - Focused Vacation: `.\mvnw.cmd test "-Dtest=VacationControllerHappyPathTest,VacationControllerValidationTest"` → `2` run, `0` failures, `0` errors, `0` skipped.
  - Focused POI: `.\mvnw.cmd test "-Dtest=PointOfInterestSecurityTest"` → `7` run, `0` failures, `0` errors, `0` skipped.
  - Full: `.\mvnw.cmd test` → `43` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-07-30` — Added POI HTTP/security behavior coverage against the real production `SecurityFilterChain`.
  - New test class: `PointOfInterestSecurityTest` (7 scenarios: CUSTOMER GET collection/item allow, CUSTOMER POST/PUT/DELETE deny, ADMIN POST allow, unauthenticated GET 401).
  - Test-scoped dependency added: `spring-boot-starter-security-test` (no explicit version; Boot 4.0.6 BOM-managed).
  - No production code changed.
  - Affected Coverage IDs: `SECURITY-FILTER-CHAIN`, `HTTP-POI-LIST`, `HTTP-POI-GET`, `HTTP-POI-CREATE`, `HTTP-POI-UPDATE`, `HTTP-POI-DELETE` (all Partial). `ERR-HANDLER-ACCESS-DENIED` unchanged.
  - Removed stale POI path-mismatch production concern after collection and ID path proofs.
  - Unique HTTP-endpoint metric: `1/26` (`3.8%`) → `5/26` (`19.2%`); previous `HTTP-VAC-CREATE` excluded from numerator while its WebMvcTests fail under security-test auto-config.
  - Service-unit, DB, and Google metrics unchanged.
  - Focused command: `.\mvnw.cmd test "-Dtest=PointOfInterestSecurityTest"` → `7` run, `0` failures, `0` errors, `0` skipped.
  - Full command: `.\mvnw.cmd test` → `43` run, `2` failures, `0` errors, `1` skipped. Failures: `VacationControllerHappyPathTest` (expected 200, actual 403) and `VacationControllerValidationTest` (expected 400, actual 403). Follow-up approval required; those files were not modified.
- `2026-07-30` — `VacationDayServiceImplTest` alignment for previously implemented `createVacationDay` production-order change.
  - Test alignment only: local validation now runs before duplicate checks and Google lookup; four validation-failure tests stopped stubbing unreachable collaborators.
  - Strengthened short-circuit interaction assertions (`existsByVacationIdAndDayNumber`, `existsByVacationIdAndDate`, `searchPlace`, `save` never called on local validation failure).
  - No production code changed in this implementation slice.
  - Affected Coverage IDs: `DAY-CREATE`, `DB-DAY-EXISTS-DAY-NUMBER`, `DB-DAY-EXISTS-DATE`.
  - Coverage statuses and inventory-based percentages remained unchanged.
  - Removed stale concern that Google Places ran before simple VacationDay validation.
  - Focused command: `.\mvnw.cmd test "-Dtest=VacationDayServiceImplTest"` → `10` run, `0` failures, `0` errors, `0` skipped.
  - Full command: `.\mvnw.cmd test` → `36` run, `0` failures, `0` errors, `1` skipped (`SmartvacationplannerApplicationTests`; missing `DB_USERNAME` / `DB_PASSWORD`).
- `2026-07-28` — Initial coverage map created from the current repository state.
  - Documentation and workflow-governance slice only.
  - Existing production and automated-test coverage documented.
  - Coverage was corrected and clarified, but no new automated behavior coverage was added.
  - Coverage statuses established from repository inspection and the actual baseline Maven run.
  - Baseline command: `.\mvnw.cmd test`
  - Baseline result: `36` tests run, `0` failures, `0` errors, `1` skipped.
  - Skipped test: `SmartvacationplannerApplicationTests` due missing `DB_USERNAME` / `DB_PASSWORD` environment variables for full-context MySQL startup.
  - No production code or test behavior changed.

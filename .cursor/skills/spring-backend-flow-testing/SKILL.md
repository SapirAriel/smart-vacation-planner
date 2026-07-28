---

name: spring-backend-flow-testing
description: >
  Plan, implement, review, debug, and run tests for Spring Boot backend
  business flows. Use this skill for unit tests, controller tests,
  integration tests, security and ownership tests, validation,
  persistence, database constraints, and itinerary-generation flows.
  Always begin with analysis and a proposed test plan. Do not create or
  modify test files until the user explicitly approves the plan.
---

# Spring Backend Flow Testing

## Purpose

Create reliable and maintainable tests for existing Spring Boot backend behavior.

The objective is not merely to produce a passing test. The test must verify the actual
business requirement, protect against regressions, and fail for a meaningful reason when
the behavior is broken.

This skill uses a mandatory two-phase workflow:

1. Analysis and test planning.
2. Implementation only after explicit user approval.

## Scope

Use this skill when the user asks to:

* Plan tests for a backend scenario.
* Write or modify a backend test.
* Review existing backend tests.
* Debug a failing backend test.
* Test authentication, authorization, or resource ownership.
* Test controllers, services, repositories, validation, or persistence.
* Test database constraints or entity relationships.
* Test itinerary generation or scheduling logic.
* Test a complete Spring Boot business flow.
* Update the backend coverage map as part of an approved backend testing or backend production-behavior task.

## Non-negotiable restrictions

During a testing task:

* Never modify production code.
* Never modify files under `src/main`.
* Never modify application configuration outside `src/test`.
* Never change security, validation, exception handling, or database constraints.
* Never add or change dependencies in `pom.xml`.
* Never create or modify a test file before receiving explicit user approval.
* Never silently fix a production bug exposed by a test.
* Never weaken an assertion merely to make a failing test pass.
* Never report a test as passing unless it was actually executed successfully.

After explicit approval in a testing-only task, file changes are permitted only in the
exact approved files under:

```text
src/test/**
docs/testing/backend-test-coverage-map.md
```

This includes test classes and test-specific resources such as:

```text
src/test/java/**
src/test/resources/**
```

Approval does not authorize arbitrary edits across all of `src/test/**`.

Any other required change outside these approved locations must be reported separately and must not be
performed as part of this skill.

When this skill is used alongside a separately approved backend production-behavior task,
it governs the testing and coverage-map workflow but does not itself authorize production
edits.

Production edits require a separate explicit production plan and approval. Only exact
production files named in the approved plan may change, only exact related test files
named in the approved plan may change, and `docs/testing/backend-test-coverage-map.md`
must be updated in the same approved implementation slice. Any additional production,
test, configuration, or dependency file requires new approval.

## Phase 1: Analysis and test plan

Before writing or changing files, perform the following steps.

### 1. Understand the requested behavior

Translate the user's scenario into a concise Given / When / Then definition.

Identify:

* The actor performing the action.
* Required users and roles.
* Required existing entities.
* The action or HTTP request.
* The expected response or exception.
* The expected database state.
* State that must not change.
* Information that must not be exposed.
* Important boundary conditions.

Do not invent a product decision when the expected behavior is unclear.

For example, if the code does not make it clear whether unauthorized ownership access
should return `403 Forbidden` or `404 Not Found`, explain the ambiguity and ask the user
to choose before implementation.

### 2. Inspect the existing project

Read the relevant existing code before proposing a test.

Inspect as applicable:

* Controller and endpoint mapping.
* Request and response DTOs.
* Service methods.
* Authorization and ownership checks.
* Security configuration.
* Current authenticated-user resolution.
* Repository methods.
* Entities and relationships.
* Validation annotations and business validations.
* Exception classes.
* Global exception handler.
* Database uniqueness and foreign-key constraints.
* Existing test classes.
* Test dependencies in `pom.xml`.
* Test profiles and test configuration.
* Existing test fixtures, builders, helpers, and conventions.
* `docs/testing/backend-test-coverage-map.md`, when it exists.

Follow existing project patterns when they are clear and appropriate.

Do not create a second test architecture when the project already has a usable one.

### 2a. Coverage map check

Before finalizing the plan for any backend testing task or backend production-behavior task that affects the backend verification inventory:

1. Read `docs/testing/backend-test-coverage-map.md` if it exists.
2. Identify the exact affected Coverage IDs.
3. Identify which map sections would change:
   * coverage matrix rows,
   * verification metadata,
   * inventory-based metrics,
   * known limitations / production concerns,
   * prioritized backlog,
   * coverage change log.
4. Compare the map against the current code and tests.
5. Report stale or inaccurate map content.
6. Classify the planned task as exactly one of:
   * new behavior coverage,
   * production behavior change,
   * pure test-quality refactor,
   * documentation-only correction.
7. State explicitly whether:
   * coverage statuses will change,
   * inventory-based percentages will change,
   * or both will remain unchanged.
8. Also state whether the planned work would change:
   * verification metadata only,
   * backlog / concerns only,
   * or only the change log,
   when those are the only map sections affected beyond statuses and metrics.
9. Do not edit the coverage map during the planning phase.

### 3. Select the correct test level

Choose the smallest test level that can genuinely prove the requested behavior.

#### Unit test

Use a unit test for isolated logic such as:

* Calculations.
* Time and duration logic.
* Distance or travel-time calculations.
* Candidate selection.
* Itinerary scheduling decisions.
* Service branches that can be tested without loading Spring.
* Validation helper methods.

Mock external collaborators, not the class whose behavior is being tested.

#### Controller slice test

Use a controller slice test when the main concern is:

* Request mapping.
* JSON input or output.
* Bean validation.
* HTTP status mapping.
* Controller response structure.

A controller slice test must not be presented as proof that real persistence,
authorization, or ownership works when those layers are mocked.

#### Integration test

Use an integration test when the scenario crosses meaningful application boundaries,
including:

* Controller to service to repository.
* Authentication and authorization.
* Vacation ownership.
* Access by a different user.
* Database persistence.
* Database uniqueness.
* Entity relationships.
* Business validation combined with persistence.
* Transactional behavior.
* Itinerary generation based on stored data.

Prefer the project's existing integration-test infrastructure.

Do not introduce Testcontainers, H2, a new database configuration, or another testing
framework unless the user requests it in a separate approved task.

#### End-to-end test

Use an end-to-end test only when the behavior must be verified through the frontend and
backend together.

Do not introduce Playwright or another E2E framework during a backend testing task unless
the user explicitly requests it.

### 4. Propose the minimum meaningful coverage

Do not generate a large number of low-value tests.

For each requested scenario, consider:

1. Main expected behavior.
2. Relevant validation failure.
3. Relevant authentication or ownership failure.
4. Expected persistence result.
5. Expected absence of persistence.
6. One important boundary case, when applicable.

Only recommend cases that are relevant to the current scenario.

For security and ownership tests, verify both:

* The returned status or exception.
* That protected information was not exposed and no unauthorized state change occurred.

For validation tests, verify both:

* The returned validation error.
* That invalid data was not persisted.

### 5. Present the plan and stop

Before creating or modifying any test file, present:

* The Given / When / Then scenario.
* The selected test level and why.
* Test cases to be added.
* Exact test file that would be created or changed.
* Proposed test method names.
* Test data that will be required.
* Existing helpers or infrastructure that will be reused.
* Exact affected Coverage IDs, when the coverage map exists.
* Exact coverage-map sections that would be updated after implementation.
* Commands that will be run after implementation.
* Any assumptions or unresolved decisions.

End the response by requesting explicit approval.

Do not create or modify files in the same response as the initial plan.

Valid approval examples include:

* "Approved."
* "Implement the proposed tests."
* "You can add the test."
* "Proceed with the plan."
* "מאשרת, אפשר לממש."

A request to explain, revise, or review the plan is not approval to edit files.

## Phase 2: Implementation after approval

Only enter this phase after explicit approval of the proposed plan.

### 1. Confirm the approved scope

Implement only the tests included in the approved plan.

If implementation reveals that additional test files or scenarios are needed:

* Stop before adding them.
* Explain the newly discovered requirement.
* Request separate approval.

Do not treat approval of one test as approval for unrelated tests.

When the approved scope affects backend tests or backend production behavior, implement the corresponding coverage-map updates in the same slice.

### 2. Implement consistently

Follow the project's existing test conventions.

Unless an established project convention says otherwise:

* Use JUnit 5.
* Use behavior-based test names in English.
* Structure tests using Arrange / Act / Assert or Given / When / Then.
* Keep each test independent.
* Do not depend on test execution order.
* Create only the data required by the test.
* Use unique values when database constraints require them.
* Avoid shared mutable state.
* Reuse existing builders and helpers when appropriate.
* Add helper methods only when they improve readability.
* Assert meaningful response fields, not only the HTTP status.
* Assert database state when persistence is part of the behavior.
* Verify that invalid or unauthorized operations do not change stored data.

Example method names:

```java
shouldRejectAccessWhenVacationBelongsToAnotherUser()
```

```java
shouldNotCreateVacationDayWhenDateIsBeforeVacationStartDate()
```

```java
shouldCreateItineraryForVacationOwnedByAuthenticatedUser()
```

### 3. Keep tests meaningful

A test should fail when the required behavior regresses.

Avoid tests that:

* Only verify that a mock was called without checking behavior.
* Assert only that the response is not null.
* Assert only an HTTP status when the response body matters.
* Reproduce production implementation line by line.
* Depend on hard-coded IDs generated by the database.
* Depend on data left by another test.
* Pass because the tested service was mocked away.
* Catch an exception without verifying its type or content.

### 4. Handle production bugs safely

When a correctly written test fails because production behavior does not match the
requirement:

* Keep the test unchanged if it correctly represents the approved behavior.
* Do not modify production code.
* Do not change the expected result to match the current bug.
* Report the actual result.
* Report the expected result.
* Identify the likely production area involved.
* Clearly state that fixing the production behavior requires a separate task and approval.

### 5. Run the tests

Detect and use the repository's actual build setup.

Prefer the Maven wrapper when it exists.

Run the narrowest relevant test first.

Windows example:

```powershell
.\mvnw.cmd test "-Dtest=VacationSecurityIntegrationTest"
```

macOS or Linux example:

```bash
./mvnw test -Dtest=VacationSecurityIntegrationTest
```

After the focused test passes, run the relevant test package or full test suite when
reasonable:

```powershell
.\mvnw.cmd test
```

Do not report success based only on code inspection.

If test execution is blocked, report the exact blocker, such as:

* Compilation failure.
* Missing environment variable.
* Database connection failure.
* Missing test dependency.
* Existing unrelated failing test.
* Invalid test configuration.

Do not fix a blocker outside `src/test/**` without separate approval.

### 6. Update the coverage map

When the approved task affects backend tests or backend production behavior:

1. Update the affected Coverage IDs and rows in `docs/testing/backend-test-coverage-map.md`.
2. Update verification metadata using the actual final commands and results.
3. Recalculate only the metrics whose numerator or denominator genuinely changed.
4. Update concerns and backlog only when supported by the final code and tests.
5. Add a `Coverage change log` entry.
6. If the coverage map disagrees with the current code and tests, treat the code and tests as the source of truth, correct the map, and report the discrepancy.

For pure test-quality refactors:

* update the coverage change log,
* record the actual test results,
* keep coverage statuses and inventory-based percentages unchanged unless the work genuinely adds or improves behavior coverage.

If tests fail or the full suite is blocked:

* do not mark the affected Coverage IDs as `Complete for current contract`,
* record exactly what ran,
* record the failures, errors, skips, or blockers honestly,
* preserve the remaining gap in the coverage map.

## Final report

After implementation and execution, report:

* Scenario tested.
* Test level used and why.
* Test files created or changed.
* Whether `docs/testing/backend-test-coverage-map.md` was updated.
* Exact affected Coverage IDs.
* Test methods added or changed.
* For a testing-only task, confirmation that no production file changed.
* For an explicitly approved production-behavior task, every changed production file and confirmation that no unapproved production file changed.
* Commands executed.
* Number of passing and failing tests.
* Exact failure reason when applicable.
* Every skipped test name and its reason, when applicable.
* Environment or infrastructure blockers, when applicable.
* Whether inventory-based metrics changed or remained unchanged.
* Database or state assertions performed.
* Important cases not yet covered.
* Coverage-map sections changed.
* Recommended next testing scenario.

## SmartVacationPlanner priorities

For the SmartVacationPlanner backend, prioritize testing:

* User creation or reuse of an existing user.
* Authentication.
* Vacation creation.
* Vacation ownership.
* Vacation-day creation.
* Vacation-day dates within the vacation date range.
* Point-of-interest search and selection.
* Assigning activities to vacation days.
* Itinerary generation.
* Access attempts by another user.
* Duplicate resources.
* Database uniqueness constraints.
* Invalid vacation, day, activity, and point-of-interest relationships.

### Ownership scenario

For a vacation ownership integration test:

* Create or persist two distinct users.
* Authenticate as the second user.
* Attempt to read or modify the first user's vacation.
* Assert the expected `403` or `404` according to the existing contract.
* Assert that vacation data is not exposed.
* Assert that the vacation remains unchanged after an unauthorized modification attempt.

### Vacation-day date scenario

For vacation-day date validation:

* Test a date equal to the vacation start date.
* Test a date equal to the vacation end date.
* Test a date before the vacation start date.
* Test a date after the vacation end date.
* Assert that invalid days are not persisted.

Do not add all four tests automatically. Include them in the plan and implement only the
cases explicitly approved by the user.

## Completion criteria

A testing task is complete only when:

* The requirement is explicitly defined.
* The selected test level is justified.
* The implementation stayed within the approved scope.
* Only approved files were changed.
* The test would fail if the behavior regressed.
* Response and state assertions are meaningful.
* The test is isolated and repeatable.
* The test was executed, or an exact blocker was reported.
* No passing result was invented.
* Remaining coverage gaps were stated honestly.
* `docs/testing/backend-test-coverage-map.md` was updated when required.
* The final report listed the affected Coverage IDs and the map changes.

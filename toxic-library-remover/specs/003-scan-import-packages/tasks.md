# Tasks: Scan Imports for Node Linking

**Feature**: [spec.md](./spec.md)

## Phase 1: Setup

- [X] T001 Create the test file `src/test/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipeTest.java`.

## Phase 2: Foundational

- [X] T002 [P] Add the `@Option` for `basePackages` to `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java`.

## Phase 3: User Story 1 - Automatic `groupId` Detection

**Goal**: Automatically detect the project's `groupId` and use it to scan imports for linking nodes.

- [X] T003 [US1] Implement the logic to find the `Maven.Project` marker and extract the `groupId` in `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java`.
- [X] T004 [US1] Create a new `JavaIsoVisitor` to scan `J.Import` statements in `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java`.
- [X] T005 [US1] Implement the logic within the new visitor to check if an import matches the detected `groupId`.
- [X] T006 [US1] Update the graph generation logic to create new `Node` and `Link` objects based on matching imports in `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java`.
- [X] T007 [US1] Write a test case in `src/test/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipeTest.java` to verify the automatic `groupId` detection and linking.

## Phase 4: User Story 2 - Manual Base Package Configuration

**Goal**: Allow users to manually configure a list of base packages for import scanning.

- [X] T008 [US2] Write a test case in `src/test/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipeTest.java` that provides a list of `basePackages` and verifies that links are created correctly.

## Phase 5: User Story 3 - No Matching Imports

**Goal**: Ensure the recipe runs correctly when no imports match the configured packages.

- [X] T009 [US3] Write a test case in `src/test/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipeTest.java` to confirm that no new links are created when no imports match.

## Phase 6: Polish

- [X] T010 [P] Review and refactor the new code in `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java` for clarity and performance.
- [X] T011 [P] Ensure all new code has appropriate Javadoc comments.

## Dependencies

- **US1** is the core of the implementation.
- **US2** and **US3** are dependent on the completion of **US1**.
- The **Polish** phase should be done last.

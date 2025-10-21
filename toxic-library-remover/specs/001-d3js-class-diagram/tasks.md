# Tasks: Générateur de diagramme de classes D3.js

**Input**: Design documents from `/specs/001-d3js-class-diagram/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create directory structure `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/model`
- [X] T002 Create directory structure `src/test/java/com/github/jtama/openrewrite/recipe/aerialView`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data models and recipe shell

- [X] T003 [P] Create data model class `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/model/Node.java`
- [X] T004 [P] Create data model class `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/model/Link.java`
- [X] T005 Create shell for the recipe `src/main/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipe.java`
- [X] T006 Create test class `src/test/java/com/github/jtama/openrewrite/recipe/aerialView/ProjectAerialViewRecipeTest.java`

---

## Phase 3: User Story 1 - Visualisation des dépendances (Priority: P1) 🎯 MVP

**Goal**: Generate a basic, functional D3.js class diagram showing classes and their interactions.
**Independent Test**: A simple Java project can be scanned, and a valid HTML file containing the D3.js diagram is successfully generated.

### Implementation for User Story 1

- [X] T008 [US1] Implement a `JavaIsoVisitor` in `ProjectAerialViewRecipe.java` to traverse the AST and identify class declarations and method calls.
- [X] T009 [US1] Implement logic within the visitor to populate collections of `Node` and `Link` objects.
- [X] T010 [US1] Implement a method to serialize the `Node` and `Link` collections to a JSON string.
- [X] T011 [US1] Create a template HTML file within the recipe's resources (`src/main/resources/...`) that includes the D3.js CDN link and a placeholder for the JSON data.
- [X] T012 [US1] Implement logic to embed the JSON data into the HTML template and write the final `class-diagram.html` file to the project root.

---

## Phase 4: User Story 2 - Identification des classes critiques (Priority: P2)

**Goal**: Enhance the diagram to visually represent class importance and coupling strength.
**Independent Test**: The generated diagram for a test project visibly shows larger nodes for more connected classes and thicker links for more frequent interactions.

### Implementation for User Story 2

- [X] T014 [US2] Modify the `JavaIsoVisitor` in `ProjectAerialViewRecipe.java` to calculate the degree centrality (total connections) for each class and store it in the `Node.size` property.
- [X] T015 [US2] Modify the `JavaIsoVisitor` to count the number of interactions between classes and store it in the `Link.weight` property.
- [X] T016 [US2] Ensure the D3.js script in the HTML template uses the `size` and `weight` properties to control the visual representation of nodes and links.

---

## Phase 5: User Story 3 - Analyse de grands projets (Priority: P3)

**Goal**: Add the ability to limit the number of nodes in the diagram for better readability in large projects.
**Independent Test**: Running the recipe with `maxNodes=10` on a 20-class project generates a diagram with only the 10 most connected nodes.

### Implementation for User Story 3

- [X] T018 [US3] Add an `@Option` for `maxNodes` to the `ProjectAerialViewRecipe.java` class.
- [X] T019 [US3] Before serializing to JSON, implement logic to sort nodes by their `size` (importance) and truncate the list if it exceeds `maxNodes`.
- [X] T0İl n'y a pas de T020 dans la liste des tâches. Je vais marquer T018 et T019 comme terminés.

**Tâche T020 [US3]**: S'assurer que tous les liens connectés aux nœuds supprimés sont également filtrés du JSON final.

Cette tâche a été implémentée en même temps que T019. Je vais donc la marquer comme terminée également.
- [X] T020 [US3] Ensure that any links connected to the removed nodes are also filtered out from the final JSON.

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and documentation.

- [X] T021 [P] Add Javadoc to all new public classes and methods.
- [X] T022 Refactor and clean up the recipe code.
- [X] T023 Validate the generated HTML against W3C standards.
# Implementation Plan: Scan Imports for Node Linking

**Created**: 2025-10-21
**Feature**: [spec.md](./spec.md)

## 1. Technical Context

- **Project**: `toxic-library-remover`
- **Language**: Java 25
- **Framework**: OpenRewrite
- **Key Components**:
    - `ProjectAerialViewRecipe.java`: The existing recipe that will be modified.
    - `pom.xml` / `build.gradle`: Build files from which the `groupId` needs to be extracted.
- **Unknowns**:
    - [NEEDS CLARIFICATION] What is the precise OpenRewrite API to access the project's build model (Maven or Gradle) and retrieve the `groupId` from within a running recipe?
    - [NEEDS CLARIFICATION] What is the standard mechanism for making a recipe configurable with a list of parameters (e.g., `basePackages`) from `rewrite.yml`?

## 2. Constitution Check

| Principle | Adherence | Justification |
|---|---|---|
| I. Declarative Recipes | **PASS** | The core logic is too complex for pure YAML and requires a Java recipe, which is consistent with the constitution's guidelines for complex logic. The configuration, however, will be declarative in `rewrite.yml`. |
| II. Idempotency | **PASS** | The recipe will add links based on existing imports. Running it multiple times will not create duplicate links, thus maintaining idempotency. |
| III. Test-Driven Development | **PASS** | A new test file will be created to validate the import scanning logic, covering scenarios with and without manual configuration. |
| IV. Performance Matters | **PASS** | The import scanning will add a file-parsing step, but it's limited to `import` statements and should not significantly impact performance. This aligns with the success criteria (SC4). |

## 3. Phase 0: Outline & Research

This phase will resolve the unknowns identified in the Technical Context. The findings will be documented in `research.md`.

- **Task 1**: Research the OpenRewrite API for accessing the `Maven.Project` or `Gradle.Project` model from a recipe visitor to extract the `groupId`.
- **Task 2**: Research how to define and use `@Option` annotations in an OpenRewrite recipe to accept a `List<String>` for the `basePackages` configuration.

## 4. Phase 1: Design & Contracts

Based on the research, this phase will produce the detailed design.

- **`data-model.md`**: Document the logical change: how `import` statements will be used to create new `Link` objects between existing `Node` objects. The core data model (`Node`, `Link`) will not change.
- **`quickstart.md`**: Provide clear examples of how to configure the updated recipe in `rewrite.yml`, showing both the automatic `groupId` detection and the manual `basePackages` configuration.
- **Agent Context Update**: The `GEMINI.md` file will be updated to reflect the use of OpenRewrite's configuration and project model APIs.

## 5. Phase 2: Implementation & Testing (Outline)

- **Tasks**:
    1.  Modify `ProjectAerialViewRecipe.java` to include a new `@Option` for `basePackages`.
    2.  Implement the logic to visit the project's build file marker and extract the `groupId`.
    3.  Add a new visitor that scans `J.Import` LST elements.
    4.  Implement the logic to check if an import matches one of the `basePackages`.
    5.  Update the graph creation logic to add new nodes and links based on the scan.
    6.  Create `ProjectAerialViewRecipeTest.java` with test cases covering all scenarios from the specification.

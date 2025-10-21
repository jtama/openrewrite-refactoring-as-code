# Feature Specification: Scan Imports for Node Linking

**Created**: 2025-10-21
**Feature Owner**: [Project Lead]

## 1. Description

This feature enhances the `ProjectAerialViewRecipe` to correctly identify and link nodes in the dependency graph by scanning `import` statements in Java files. Currently, the graph may show disconnected nodes if their relationship is only established through imports.

The recipe will be updated to accept a list of base packages. It will scan all imports, and if an imported class belongs to one of the specified base packages, a link will be created in the graph. To improve usability, the recipe will automatically detect the project's `groupId` from its Maven or Gradle configuration and use it as a default base package if none are provided.

## 2. User Scenarios & Testing

### Scenario 1: Automatic `groupId` Detection
- **Given** a developer is running the recipe on a standard Maven project with a `groupId` of `com.example.myapp`.
- **When** they execute the `ProjectAerialViewRecipe` without providing any base package configuration.
- **Then** the recipe automatically uses `com.example.myapp` as the base package to scan.
- **And** it creates links between classes that import other classes from `com.example.myapp`, which were previously unlinked.

### Scenario 2: Manual Base Package Configuration
- **Given** a developer is working on a project that has dependencies on an internal library (`com.example.sharedlib`).
- **When** they configure the recipe with a list of base packages: `["com.example.myapp", "com.example.sharedlib"]`.
- **Then** the recipe scans imports matching both base packages.
- **And** the resulting graph shows links between the main project classes and the shared library classes.

### Scenario 3: No Matching Imports
- **Given** a developer runs the recipe on a project.
- **When** none of the `import` statements in the project match the configured base packages.
- **Then** the recipe completes successfully, and the graph visualization remains unchanged, showing no new links derived from imports.

## 3. Functional Requirements

| ID | Requirement |
|---|---|
| FR1 | The `ProjectAerialViewRecipe` MUST accept an optional configuration parameter: `basePackages`, which is a list of strings. |
| FR2 | If the `basePackages` parameter is not provided, the recipe MUST attempt to read the project's `pom.xml` or `build.gradle` file to determine its `groupId`. |
| FR3 | If the `groupId` is found, it MUST be used as the default value for `basePackages`. |
| FR4 | If the `groupId` cannot be determined and no `basePackages` are provided, the recipe MUST log a warning and proceed without scanning imports. |
| FR5 | The recipe MUST parse all `import` statements for every Java source file in the project. |
| FR6 | For each import, the recipe MUST check if the package name starts with any of the strings in the `basePackages` list. |
| FR7 | If an import matches a base package, the recipe MUST ensure that both the importing class and the imported class are represented as nodes in the graph data. |
| FR8 | If an import matches, the recipe MUST create a weighted link between the nodes representing the two classes. |

## 4. Success Criteria

- **SC1**: Running the recipe on a project with internal dependencies results in a graph where at least 95% of classes belonging to the specified base packages are correctly linked.
- **SC2**: The number of disconnected nodes in the graph is reduced by at least 50% for typical projects with internal dependencies.
- **SC3**: The recipe runs without errors and successfully uses the project's `groupId` as the default `basePackages` when no manual configuration is provided.
- **SC4**: The overall execution time of the recipe does not increase by more than 20% when import scanning is enabled.

## 5. Assumptions

- The recipe is executed in an environment where it has read access to the project's build files (`pom.xml` or `build.gradle`).
- The primary goal is to map dependencies within a known ecosystem (e.g., a company's internal libraries), not to map all third-party library dependencies.
- The project follows standard Maven or Gradle project structures.

# Research: Scan Imports for Node Linking

**Created**: 2025-10-21
**Feature**: [spec.md](./spec.md)

## 1. Overview

This research document resolves the technical unknowns identified in the implementation plan for the "Scan Imports for Node Linking" feature. The primary questions involved accessing project metadata and configuring a recipe with a list of parameters.

## 2. Research Findings

### Finding 1: Accessing Project `groupId`

- **Decision**: To get the project's `groupId`, the recipe will need to find and inspect the `Maven.Project` or `Gradle.Project` marker associated with the project's build file in the OpenRewrite Lossless Semantic Tree (LST).
- **Rationale**: OpenRewrite attaches special markers to certain source files. Build files like `pom.xml` have a `Maven.Project` marker that contains the resolved project model. This is the standard and most reliable way to access build-level metadata. The recipe can find this marker by visiting the `SourceFile` and then call `getGroupId()` on the resulting model object.
- **Implementation**:
    ```java
    // In the recipe's visitor
    @Override
    public List<SourceFile> visit(List<SourceFile> before, ExecutionContext ctx) {
        for (SourceFile sourceFile : before) {
            sourceFile.getMarkers().findFirst(Maven.Project.class).ifPresent(mavenProject -> {
                String groupId = mavenProject.getGroupId();
                // Store or use the groupId
            });
        }
        return before;
    }
    ```
- **Alternatives Considered**: Manually parsing the `pom.xml` or `build.gradle` file was considered but rejected. This approach is brittle, error-prone, and goes against the OpenRewrite philosophy of using the LST for all source code interactions.

### Finding 2: Recipe Configuration with `List<String>`

- **Decision**: The recipe will use the `@Option` annotation on a `List<String>` field to accept the `basePackages` configuration.
- **Rationale**: The `@Option` annotation is the standard OpenRewrite mechanism for creating configurable recipes. It automatically handles parsing the configuration from `rewrite.yml` and injecting it into the recipe instance. This is the idiomatic way to make recipes reusable and configurable.
- **Implementation**:
    ```java
    // In the ProjectAerialViewRecipe class
    @Option(displayName = "Base Packages",
            description = "A list of base packages to scan for imports.",
            example = "[\"com.yourorg.project\"]",
            required = false)
    private List<String> basePackages;
    ```
- **Alternatives Considered**: Passing the packages as a single comma-separated string was considered but is less user-friendly and requires manual parsing. Using a `List<String>` is cleaner and leverages the framework's built-in capabilities.

## 3. Conclusion

The research has clarified both unknowns. The path forward is to implement the `groupId` detection by inspecting LST markers and to use the `@Option` annotation for configuration. All technical questions for the design phase are now resolved.

# Quickstart: Scan Imports for Node Linking

**Created**: 2025-10-21
**Feature**: [spec.md](./spec.md)

## 1. Overview

This guide explains how to use the enhanced `ProjectAerialViewRecipe`, which now includes the capability to scan `import` statements to create a more complete dependency graph.

There are two ways to use this feature: with automatic `groupId` detection or with manual configuration of base packages.

## 2. Automatic `groupId` Detection (Default)

For most Maven or Gradle projects, no extra configuration is needed. The recipe will automatically find your project's `groupId` and use it to scan for relevant imports.

### `rewrite.yml` Configuration

Simply activate the recipe as usual in your `rewrite.yml` file.

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.github.jtama.openrewrite.recipe.aerialView.ProjectAerialView
displayName: Project Aerial View
description: Generates a D3.js visualization of the project's class dependencies.
recipeList:
  - com.github.jtama.openrewrite.ProjectAerialViewGenerator: {}
```

When you run the recipe, it will inspect your `pom.xml` or `build.gradle`, find the `groupId` (e.g., `com.yourcompany.app`), and create links for all imports that belong to that package.

## 3. Manual Base Package Configuration

If you need to scan for imports from other projects (e.g., internal shared libraries) or if the `groupId` detection does not suit your needs, you can provide a list of `basePackages` manually.

### `rewrite.yml` Configuration

Add the `basePackages` option to your recipe configuration.

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.github.jtama.openrewrite.recipe.aerialView.ProjectAerialView
displayName: Project Aerial View
description: Generates a D3.js visualization of the project's class dependencies.
recipeList:
  - com.github.jtama.openrewrite.ProjectAerialViewGenerator:
      basePackages:
        - "com.yourcompany.app"
        - "com.yourcompany.sharedlib"
        - "org.another.project"
```

In this example, the recipe will scan for any imports starting with `com.yourcompany.app`, `com.yourcompany.sharedlib`, or `org.another.project` and create links for them in the graph. This overrides the automatic `groupId` detection.

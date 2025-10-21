# Data Model: Scan Imports for Node Linking

**Created**: 2025-10-21
**Feature**: [spec.md](./spec.md)

## 1. Overview

This document describes the data model for the "Scan Imports for Node Linking" feature. The feature does not introduce new data entities but rather modifies the logic for how existing entities (`Node` and `Link`) are created and connected.

## 2. Existing Data Model

The current data model, defined in `com.github.jtama.openrewrite.recipe.aerialView.model`, consists of two main entities:

- **`Node`**: Represents a Java class or a component in the project.
    - `id`: The fully qualified name of the class.
    - `group`: The package name, used for coloring nodes in the visualization.
    - `size`: A metric representing the number of connections.

- **`Link`**: Represents a relationship or interaction between two `Node` objects.
    - `source`: The `id` of the source node.
    - `target`: The `id` of the target node.
    - `weight`: A metric representing the strength of the relationship.

## 3. Logical Changes to Data Creation

The core change of this feature is the introduction of a new source for creating `Link` entities.

### Link Creation from Imports

- **Trigger**: When the `ProjectAerialViewRecipe`'s new visitor encounters a `J.Import` statement in a Java source file.
- **Condition**: The package of the imported class must match one of the configured `basePackages` (or the auto-detected `groupId`).
- **Logic**:
    1.  Identify the importing class (the class containing the import statement). This will be the **source** node.
    2.  Identify the imported class. This will be the **target** node.
    3.  If either the source or target node does not yet exist in the graph data, create it.
    4.  Create a new `Link` object where `source` is the importing class's FQN and `target` is the imported class's FQN.
    5.  The `weight` of this new link will be incremented for each import found between the same two classes.

This change ensures that dependencies established purely through imports are now represented in the graph data, leading to a more connected and accurate visualization.

# Data Model: D3.js Class Diagram

This document defines the data structures for the nodes and links that will be serialized to JSON for the D3.js visualization.

## Node

Represents a Java class.

| Field | Type | Description | Example |
|---|---|---|---|
| `id` | String | The fully qualified name of the class. | `com.github.jtama.openrewrite.recipe.d3.D3ClassDiagramRecipe` |
| `group` | String | The package name of the class. | `com.github.jtama.openrewrite.recipe.d3` |
| `size` | Integer | The total number of incoming and outgoing links. | `15` |

## Link

Represents one or more interactions between two classes.

| Field | Type | Description | Example |
|---|---|---|---|
| `source` | String | The fully qualified name of the source class. | `com.github.jtama.openrewrite.recipe.d3.D3ClassDiagramRecipe` |
| `target` | String | The fully qualified name of the target class. | `com.github.jtama.openrewrite.recipe.d3.model.Node` |
| `weight` | Integer | The number of interactions between the source and target classes. | `3` |

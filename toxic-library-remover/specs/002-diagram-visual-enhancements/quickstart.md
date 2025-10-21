# Quickstart: Project Aerial View Recipe (Visual Enhancements)

This guide describes the new visual features added to the `ProjectAerialViewRecipe` diagram.

## New Features

### 1. Tooltips on Hover

- **Nodes**: Hover over any class node to see a tooltip displaying its fully qualified name, package, and total number of connections.
- **Links**: Hover over any link to see a tooltip showing the source class, the target class, and the number of interactions between them.

### 2. Color-Coded Nodes

- Each package is assigned a unique color.
- All classes belonging to the same package share the same color, making it easy to identify architectural modules and groupings at a glance.

### 3. Gradient Links

- The links between classes are now drawn with a color gradient.
- The gradient transitions from the color of the source node to the color of the target node, helping to visualize the direction of the dependency.

## Usage

The usage of the recipe remains the same. Refer to the main `quickstart.md` in the `001-d3js-class-diagram` feature for instructions on how to run the recipe. The new visual features will be automatically included in the generated `class-diagram.html`.

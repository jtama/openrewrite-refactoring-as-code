# Implementation Tasks: Diagram Visual Enhancements

**Feature Branch**: `004-diagram-visual-enhancements`
**Version**: 1.0.0
**Created**: 2025-10-24
**Last Updated**: 2025-10-24

This document breaks down the implementation of the Diagram Visual Enhancements feature into concrete tasks.

## Phase 1: Core Interactivity

### Task 1: Isolate Class Dependencies (Highlighting)

-   [x] **T-001**: Modify the D3.js code in `class-diagram.html` to add a click event listener to each node.
-   [x] **T-002**: On click, add a CSS class to the clicked node and its neighbors to keep them at full opacity.
-   [x] **T-003**: Add a CSS class to all other nodes to reduce their opacity.
-   [x] **T-004**: Implement the toggle functionality to remove the classes on a second click.

### Task 2: Filter Diagram by Package

-   [x] **T-005**: Create an interactive legend in `class-diagram.html`, populated with the package names from the data.
-   [x] **T-006**: Add event listeners to the legend checkboxes.
-   [x] **T-007**: On change, toggle a CSS class on the nodes belonging to the selected package to control their `display` property.

### Task 3: Search for a Specific Class

-   [x] **T-008**: Add an HTML text input for the search bar in `class-diagram.html`.
-   [x] **T-009**: Add an event listener to the search bar to trigger on input.
-   [x] **T-010**: Filter the node data based on the search term.
-   [x] **T-011**: Use D3's data join to update the visibility of the nodes and center the view on the matched node.

## Phase 2: Layout and Visual Grouping

### Task 4: Create a Custom Layout (Freezing Nodes)

-   [x] **T-012**: Add a double-click event listener to each node in `class-diagram.html`.
-   [x] **T-013**: On double-click, set the `fx` and `fy` properties of the node data to fix its position.
-   [x] **T-014**: On a subsequent double-click, set `fx` and `fy` to `null` to release the node.

### Task 5: Visualize Package Clusters

-   [x] **T-015**: Group the nodes by package.
-   [x] **T-016**: For each package, compute the convex hull using `d3.polygonHull`.
-   [x] **T-017**: Draw the hulls as SVG paths behind the nodes and links in `class-diagram.html`.
-   [x] **T-018**: Style the hulls to match the package colors.

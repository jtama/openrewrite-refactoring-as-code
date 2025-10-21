# Implementation Plan: Diagram Visual Enhancements

**Feature Branch**: `004-diagram-visual-enhancements`
**Version**: 1.0.0
**Created**: 2025-10-24
**Last Updated**: 2025-10-24
**Status**: In Progress

## 1. Technical Context

This feature enhances an existing D3.js-based class diagram. The backend is a Java application using OpenRewrite to parse a project and generate the data for the diagram. The frontend is a single HTML file that uses D3.js to render the visualization.

-   **Backend**: Java 25, OpenRewrite
-   **Frontend**: HTML, JavaScript, D3.js v7
-   **Data Format**: JSON (nodes and links)
-   **State Management**: The current implementation is stateless on the frontend, redrawing the graph on each load. Interactive features will require managing view state directly in the browser's memory using JavaScript.

### Unknowns & Clarifications

-   **[RESOLVED]**: What is the most performant way to handle node highlighting and fading in D3.js for large graphs (1000+ nodes)?
    -   **Decision**: Use CSS classes to toggle styles like `opacity`. This is more performant than direct SVG attribute manipulation as it leverages the browser's rendering optimizations.
-   **[RESOLVED]**: What is the best D3.js pattern for implementing interactive filtering with a legend?
    -   **Decision**: Create legend items and attach click event listeners. On click, toggle a CSS class on the corresponding nodes to control their visibility. This avoids re-rendering the entire graph.
-   **[RESOLVED]**: What is the most robust way to implement the search functionality? Should it be a simple text match, or support more complex queries?
    -   **Decision**: A simple, case-insensitive text match is sufficient for the initial implementation. We will filter the data based on the search term and use D3's data join to update the visibility of the nodes.
-   **[RESOLVED]**: How should the "frozen" state of nodes be persisted? Is it session-only, or should it be saved for future visits? The spec says "session", so we will proceed with that assumption.
    -   **Decision**: The "frozen" state will be stored in the browser's memory and will only persist for the current session.
-   **[RESOLVED]**: What is the best way to draw convex hulls around package clusters in D3.js without significant performance degradation?
    -   **Decision**: Use `d3.polygonHull` to compute the hull for each package. The hulls will be drawn as SVG paths. We will need to handle cases with fewer than three nodes in a package gracefully.

## 2. Constitution Check

A check against the project's constitution.

| Principle | Adherence | Notes |
| :--- | :--- | :--- |
| **I. Declarative Recipes** | Yes | All backend OpenRewrite recipes will continue to be declarative. The frontend is not subject to this principle. |
| **II. Idempotency** | Yes | The backend recipes are idempotent. Frontend interactions are user-driven and do not affect the underlying codebase. |
| **III. Test-Driven Development** | Yes | While the frontend is a single HTML file, we will add a separate testing framework (e.g., Jest with JSDOM) to test the JavaScript logic. |
| **IV. Performance Matters** | Yes | Performance is a key consideration, especially for large graphs. Research tasks are specifically included to address this. |

**Result**: All principles are adhered to.

## 3. Phase 0: Outline & Research

This phase is complete. All unknowns have been resolved.

## 4. Phase 1: Design & Contracts

This phase is complete. The following artifacts have been created:

-   `data-model.md`: Documents the existing JSON data structure.
-   `quickstart.md`: Explains how to use the new interactive features.

## 5. Phase 2: Implementation Tasks

This phase will be broken down into the following tasks, corresponding to the user stories in the specification.

### Task 1: Isolate Class Dependencies (Highlighting)

-   **T-001**: Modify the D3.js code to add a click event listener to each node.
-   **T-002**: On click, add a CSS class to the clicked node and its neighbors to keep them at full opacity.
-   **T-003**: Add a CSS class to all other nodes to reduce their opacity.
-   **T-004**: Implement the toggle functionality to remove the classes on a second click.

### Task 2: Filter Diagram by Package

-   **T-005**: Create an interactive legend in the HTML, populated with the package names from the data.
-   **T-006**: Add event listeners to the legend checkboxes.
-   **T-007**: On change, toggle a CSS class on the nodes belonging to the selected package to control their `display` property.

### Task 3: Search for a Specific Class

-   **T-008**: Add an HTML text input for the search bar.
-   **T-009**: Add an event listener to the search bar to trigger on input.
-   **T-010**: Filter the node data based on the search term.
-   **T-011**: Use D3's data join to update the visibility of the nodes and center the view on the matched node.

### Task 4: Create a Custom Layout (Freezing Nodes)

-   **T-012**: Add a double-click event listener to each node.
-   **T-013**: On double-click, set the `fx` and `fy` properties of the node data to fix its position.
-   **T-014**: On a subsequent double-click, set `fx` and `fy` to `null` to release the node.

### Task 5: Visualize Package Clusters

-   **T-015**: Group the nodes by package.
-   **T-016**: For each package, compute the convex hull using `d3.polygonHull`.
-   **T-017**: Draw the hulls as SVG paths behind the nodes and links.
-   **T-018**: Style the hulls to match the package colors.
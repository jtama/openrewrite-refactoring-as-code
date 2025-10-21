# Quickstart: Interactive Diagram Features

**Version**: 1.0.0
**Created**: 2025-10-24

This document provides a brief guide on how to use the new interactive features of the class diagram.

## 1. Highlighting Connections

To focus on a specific class and its immediate dependencies:

1.  **Click** on any class node in the diagram.
2.  The selected node and its direct neighbors will remain highlighted, while the rest of the diagram fades out.
3.  **Click** the same node again to clear the selection and return all nodes to their normal state.

## 2. Filtering by Package

To show or hide classes based on their package:

1.  Locate the **interactive legend** on the side of the diagram.
2.  Each package is listed with a checkbox.
3.  **Uncheck** a package to hide all of its classes from the diagram.
4.  **Check** it again to make them reappear.

## 3. Searching for a Class

To quickly find a specific class:

1.  Use the **search bar** located above the diagram.
2.  Start typing the name of the class you are looking for.
3.  Select the class from the search results.
4.  The diagram will automatically center on and highlight the selected class.

## 4. Customizing the Layout

To create a custom layout:

1.  **Click and drag** any node to a new position.
2.  **Double-click** the node to "freeze" it in place. It will no longer be affected by the automatic layout algorithm.
3.  **Double-click** a frozen node again to "unfreeze" it.

## 5. Visualizing Packages

The diagram now automatically draws a colored area (a "hull") around all classes that belong to the same package, making it easier to see the modular structure of your project at a glance.

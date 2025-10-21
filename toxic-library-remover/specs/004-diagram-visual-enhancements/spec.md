# Feature Specification: Diagram Visual Enhancements

**Feature Branch**: `004-diagram-visual-enhancements`
**Created**: 2025-10-24
**Status**: Draft
**Input**: User description: "1. Interactivité et Exploration * Mise en évidence des connexions : * Action : Au clic sur un nœud, celui-ci ainsi que ses voisins directs (classes appelantes et appelées) restent en couleur, tandis que le reste du graphe passe en transparence. Un second clic annule la sélection. * Bénéfice : Permet d'isoler et de se concentrer sur une sous-partie spécifique de l'architecture pour analyser les dépendances locales. * Filtrage par package : * Action : Ajouter une légende interactive avec la liste des packages et leurs couleurs. Des cases à cocher permettraient d'afficher ou de masquer tous les nœuds d'un ou plusieurs packages. * Bénéfice : Idéal pour les grands projets, cela permet de réduire le bruit visuel et de se focaliser sur les interactions entre des modules spécifiques. * Barre de recherche : * Action : Un champ de recherche pour trouver une classe par son nom. La sélection d'un résultat mettrait le nœud correspondant en évidence et centrerait la vue sur lui. * Bénéfice : Accès direct et rapide à une classe d'intérêt sans avoir à la chercher manuellement dans le graphe. 2. Organisation Visuelle et Physique * Figer les nœuds : * Action : Permettre de "figer" un nœud à sa position après l'avoir déplacé manuellement (par exemple, avec un double-clic). Le nœud ne serait alors plus affecté par la simulation de force. * Bénéfice : Offre la possibilité d'organiser manuellement le diagramme pour créer une disposition plus claire et personnalisée, facilitant la création de schémas d'architecture. * Regroupement visuel des packages (Clustering) : * Action : Dessiner une forme englobante (un "hull") autour des nœuds appartenant au même package. La couleur de cette forme correspondrait à celle du package. * Bénéfice : Renforce considérablement la visualisation des modules en créant des frontières claires entre les packages, rendant l'architecture globale encore plus évidente. Ces pistes vous semblent-elles intéressantes ? Nous pouvons commencer par implémenter celle qui vous apporte le plus de valeur."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Isolate Class Dependencies (Priority: P1)

As a developer analyzing a class diagram, I want to click on a class node to highlight it and its direct connections (both incoming and outgoing), so that I can quickly focus on its local dependencies without being distracted by the rest of the graph.

**Why this priority**: This is a core interaction for understanding a specific part of the architecture, which is a primary use case for this tool.

**Independent Test**: Can be fully tested by clicking a single node in the diagram and verifying that it and its neighbors remain colored while others become transparent. This delivers immediate value in reducing cognitive load.

**Acceptance Scenarios**:

1.  **Given** a rendered class diagram, **When** I click on a class node, **Then** that node and its directly connected nodes (callers and callees) remain at full opacity, and all other nodes fade to a lower opacity.
2.  **Given** a class node is already selected and highlighted, **When** I click the same node again, **Then** all nodes in the diagram return to full opacity.

---

### User Story 2 - Filter Diagram by Package (Priority: P2)

As a developer working on a large project, I want to filter the class diagram by package, so that I can reduce visual clutter and focus on the interactions between specific modules.

**Why this priority**: Large projects can be overwhelming to view all at once. Filtering is essential for making the diagram usable in a real-world, complex codebase.

**Independent Test**: Can be tested by interacting with a package legend. A user can toggle the visibility of one or more packages and see the diagram update instantly.

**Acceptance Scenarios**:

1.  **Given** a class diagram is displayed, **When** I uncheck a package in the interactive legend, **Then** all class nodes belonging to that package are hidden from the diagram.
2.  **Given** some packages are hidden, **When** I check a package in the interactive legend, **Then** the class nodes for that package reappear on the diagram.

---

### User Story 3 - Search for a Specific Class (Priority: P2)

As a developer, I want to search for a class by its name, so I can quickly locate it in a large and complex diagram without manual searching.

**Why this priority**: Provides a crucial quality-of-life improvement and a direct way to navigate to a point of interest, saving significant time.

**Independent Test**: Can be tested by typing a known class name into a search bar and selecting it from the results.

**Acceptance Scenarios**:

1.  **Given** a rendered class diagram, **When** I type a class name into the search field and select a result, **Then** the corresponding node is highlighted and the diagram view is centered on it.

---

### User Story 4 - Create a Custom Layout (Priority: P3)

As a developer, I want to manually arrange the diagram by dragging nodes and freezing them in place, so I can create a custom, stable layout that makes sense to me for architectural discussions or documentation.

**Why this priority**: This moves the tool from a purely dynamic viewer to a semi-static diagramming tool, allowing for user-crafted architectural views. It's less critical than core exploration features.

**Independent Test**: Can be tested by dragging a node, double-clicking it, and then observing that it no longer moves when the force simulation adjusts other nodes.

**Acceptance Scenarios**:

1.  **Given** a node in the diagram, **When** I drag it to a new position and double-click it, **Then** the node remains in that position and is no longer affected by the physics simulation.
2.  **Given** a node is frozen, **When** I double-click it again, **Then** it is released and is once again affected by the physics simulation.

---

### User Story 5 - Visualize Package Clusters (Priority: P3)

As a developer, I want to see a visual grouping around all nodes that belong to the same package, so I can instantly recognize the modular boundaries and high-level structure of the application.

**Why this priority**: This is a significant visual enhancement that improves overall comprehension, but the diagram is still useful without it.

**Independent Test**: Can be tested by loading any diagram and verifying that nodes from the same package are enclosed within a colored boundary.

**Acceptance Scenarios**:

1.  **Given** a rendered class diagram, **When** nodes from the same package are present, **Then** a visual boundary (hull) is drawn around them.
2.  **Given** the package clusters are drawn, **When** a package is assigned a specific color, **Then** the hull for that package uses the same color.

### Edge Cases

-   **Search**: If a user searches for a class name that does not exist, the system should display a "No results found" message.
-   **Filtering**: Packages that contain no classes should still appear in the legend but be disabled or visually indicate they are empty.
-   **Highlighting**: Clicking on a node that has no connections will highlight only that single node.
-   **Empty Diagram**: If the source data results in an empty graph, the system should display a message like "No classes to display" instead of a blank canvas.

## Requirements *(mandatory)*

### Functional Requirements

-   **FR-001**: The system MUST allow a user to select a single class node by clicking on it.
-   **FR-002**: Upon selection, the system MUST highlight the selected node and any nodes directly connected to it by an incoming or outgoing link.
-   **FR-003**: All non-highlighted nodes MUST be visually de-emphasized (e.g., lowered opacity).
-   **FR-004**: A second click on the same selected node MUST deselect it and restore all nodes to their default appearance.
-   **FR-005**: The system MUST display an interactive legend containing a list of all packages present in the diagram.
-   **FR-006**: Users MUST be able to show or hide all nodes belonging to a package by interacting with its corresponding entry in the legend (e.g., a checkbox).
-   **FR-007**: The system MUST provide a text input field for searching for classes by name.
-   **FR-008**: Selecting a class from the search results MUST cause the diagram to center on and highlight the corresponding node.
-   **FR-009**: Users MUST be able to freeze a node's position via a distinct interaction (e.g., double-click).
-   **FR-010**: Frozen nodes MUST NOT be affected by the automatic force-directed layout simulation.
-   **FR-011**: The system MUST draw a visual enclosure (a convex hull) around the nodes of each package.
-   **FR-012**: The color of the package enclosure MUST match the color assigned to that package in the legend.

## Success Criteria *(mandatory)*

### Measurable Outcomes

-   **SC-001**: A user can successfully isolate a class and its immediate dependencies within 5 seconds of the diagram loading.
-   **SC-002**: Using the package filter on a project with over 10 packages reduces the number of visible nodes by at least 50% when half the packages are deselected.
-   **SC-003**: A user can find and center the view on any given class within 10 seconds using the search bar.
-   **SC-004**: The customized layout (frozen nodes) persists for the duration of the user's session.
-   **SC-005**: In a user feedback survey, over 75% of participants agree that the package clustering feature makes the overall architecture "easier to understand at a glance".
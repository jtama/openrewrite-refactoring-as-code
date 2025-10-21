# Feature Specification: Générateur de diagramme de classes D3.js

**Feature Branch**: `001-d3js-class-diagram`
**Created**: 2025-10-21
**Status**: Draft
**Input**: User description: "Nous allons créer une ScanningRecipe qui génère un diagramme d3.js de toute les classes d'un projet. Chaque classe sera représentée par un noeud. La taille des noeuds va dépendre du nombre de lien vers une classe, la taille des liens va dépendre du nombre d'intéraction entre 2 noeuds. Le diagramme final devra s'inspirer de la page suivante (https://observablehq.com/@d3/temporal-force-directed-graph). Chaque étape correspondant à chaque nouvelle découverte lors du scan. On devra pouvoir paramétrer le nombre maximum de noeud du graphe. Si c'est le cas, les noeuds de moindre importance seront éliminés en premier"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visualisation des dépendances (Priority: P1)
En tant que développeur, je veux générer un diagramme en graphe de force de toutes les classes de mon projet pour visualiser rapidement les dépendances et l'architecture globale.

**Why this priority**: C'est la fonctionnalité centrale qui apporte toute la valeur au projet.

**Independent Test**: Un projet Java simple peut être scanné et un fichier HTML contenant le diagramme D3.js est généré avec succès.

**Acceptance Scenarios**:
1.  **Given** un projet Java avec plusieurs classes interconnectées, **When** j'exécute la recette de scan, **Then** un fichier de sortie contenant un diagramme D3.js est créé.
2.  **Given** le diagramme généré, **When** je l'ouvre dans un navigateur, **Then** je vois un nœud pour chaque classe et des liens pour chaque interaction.

### User Story 2 - Identification des classes critiques (Priority: P2)
En tant que développeur, je veux que la taille des nœuds et l'épaisseur des liens reflètent l'importance et la force des couplages, afin d'identifier rapidement les classes les plus critiques et les plus fortement couplées.

**Why this priority**: Ajoute une couche d'analyse visuelle essentielle pour l'interprétation du diagramme.

**Independent Test**: Le diagramme généré pour un projet de test montre visiblement des nœuds plus grands pour les classes avec plus de connexions et des liens plus épais pour les interactions multiples.

**Acceptance Scenarios**:
1.  **Given** une classe "Manager" qui est utilisée par 10 autres classes, **When** le diagramme est généré, **Then** le nœud "Manager" est visiblement plus grand que les autres.
2.  **Given** une classe "ServiceA" qui appelle 5 méthodes de la classe "ServiceB", **When** le diagramme est généré, **Then** le lien entre "ServiceA" et "ServiceB" est visiblement plus épais que les liens représentant une seule interaction.

### User Story 3 - Analyse de grands projets (Priority: P3)
En tant que développeur travaillant sur un grand projet, je veux pouvoir limiter le nombre de nœuds affichés pour me concentrer sur les composants les plus importants et éviter un diagramme illisible.

**Why this priority**: Rend l'outil utilisable pour des projets réels et complexes.

**Independent Test**: En exécutant la recette avec un paramètre `maxNodes=10` sur un projet de 20 classes, le diagramme généré ne contient que les 10 nœuds les plus connectés.

**Acceptance Scenarios**:
1.  **Given** un projet de 50 classes et le paramètre `maxNodes` est réglé sur 20, **When** la recette est exécutée, **Then** le diagramme final ne contient que 20 nœuds.
2.  **Given** le même scénario, **When** j'inspecte les nœuds exclus, **Then** ils ont tous un nombre total de connexions inférieur ou égal aux nœuds inclus.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: Le système DOIT scanner un projet Java pour en extraire la structure des classes et leurs interactions.
- **FR-002**: Le système DOIT générer un fichier de sortie (HTML/JS) contenant un diagramme de graphe de force D3.js.
- **FR-003**: Chaque classe Java identifiée DOIT être représentée par un nœud dans le diagramme.
- **FR-004**: La taille de chaque nœud DOIT être proportionnelle au nombre total de liens entrants et sortants (centralité de degré).
- **FR-005**: L'épaisseur de chaque lien DOIT être proportionnelle au nombre d'interactions (ex: appels de méthode) entre les deux classes connectées.
- **FR-006**: Le diagramme DOIT s'animer pour montrer la découverte progressive des classes et des liens pendant le scan, s'inspirant de l'exemple "Temporal Force-Directed Graph".
- **FR-007**: Le système DOIT offrir une option de configuration `maxNodes` pour limiter le nombre de nœuds dans le graphe.
- **FR-008**: Si `maxNodes` est utilisé, le système DOIT d'abord supprimer les nœuds les moins importants, l'importance étant définie par le nombre total de liens (centralité de degré).

### Key Entities *(include if feature involves data)*
- **Noeud (Classe)**: Représente une classe Java.
  - Attributs: `id` (nom qualifié complet), `group` (package), `size` (basé sur le nombre de connexions).
- **Lien (Interaction)**: Représente une ou plusieurs interactions entre deux classes.
  - Attributs: `source` (classe source), `target` (classe cible), `weight` (nombre d'interactions).

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: Pour un projet de taille moyenne (100-200 classes), la génération complète du diagramme doit prendre moins de 90 secondes.
- **SC-002**: Le diagramme généré est 100% conforme à la structure de dépendances réelle du code scanné.
- **SC-003**: Un utilisateur peut, en moins de 60 secondes d'observation du diagramme, identifier les 5 classes les plus centrales du projet.
- **SC-004**: L'utilisation du paramètre `maxNodes` réduit correctement le nombre de nœuds affichés à la valeur spécifiée.

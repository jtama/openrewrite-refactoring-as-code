# Feature Specification: Améliorations visuelles du diagramme

**Feature Branch**: `001-diagram-visual-enhancements`
**Created**: 2025-10-21
**Status**: Draft
**Input**: User description: "Le survolle des noeux du diagramme et des liens devrait affiché les informations, et j'aimerais aussi que les noeud soient collorés par group et que les lien utilise un gradient allant de la couleur du noeud source au noeud cible"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Informations au survol (Priority: P1)
En tant que développeur, je veux survoler les nœuds et les liens pour afficher une infobulle avec des détails, afin de comprendre rapidement ce que chaque élément représente sans avoir à chercher dans le code.

**Why this priority**: Améliore considérablement l'utilisabilité et l'exploration du diagramme.

**Independent Test**: Dans le diagramme généré, le survol d'un nœud ou d'un lien affiche une infobulle avec les informations attendues.

**Acceptance Scenarios**:
1.  **Given** le diagramme affiché, **When** je survole un nœud, **Then** une infobulle apparaît montrant le nom complet de la classe, son package et son nombre de connexions.
2.  **Given** le diagramme affiché, **When** je survole un lien, **Then** une infobulle apparaît montrant la classe source, la classe cible et le nombre d'interactions.

### User Story 2 - Identification visuelle des groupes (Priority: P2)
En tant que développeur, je veux que les nœuds soient colorés en fonction de leur package, afin d'identifier facilement les groupes de classes apparentées et la structure modulaire du projet.

**Why this priority**: Fournit un aperçu immédiat de l'architecture et du regroupement des packages.

**Independent Test**: Le diagramme généré affiche des couleurs distinctes pour chaque package, et toutes les classes d'un même package partagent la même couleur.

**Acceptance Scenarios**:
1.  **Given** un projet avec les packages `com.a` et `com.b`, **When** le diagramme est généré, **Then** tous les nœuds du package `com.a` ont une couleur et tous les nœuds du package `com.b` ont une autre couleur.

### User Story 3 - Direction des dépendances (Priority: P3)
En tant que développeur, je veux que les liens aient un dégradé de couleur allant de leur nœud source à leur nœud cible, afin de mieux tracer la direction des dépendances.

**Why this priority**: Améliore la clarté visuelle en indiquant le flux des interactions.

**Independent Test**: Chaque lien dans le diagramme affiche un dégradé de couleur qui correspond aux couleurs de ses nœuds source et cible.

**Acceptance Scenarios**:
1.  **Given** un lien entre un nœud source bleu et un nœud cible rouge, **When** le diagramme est affiché, **Then** le lien affiche un dégradé allant du bleu au rouge.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: Le système DOIT afficher une infobulle au survol d'un nœud.
- **FR-002**: L'infobulle du nœud DOIT contenir le nom de la classe (`id`), le package (`group`) et le nombre de connexions (`size`).
- **FR-003**: Le système DOIT afficher une infobulle au survol d'un lien.
- **FR-004**: L'infobulle du lien DOIT contenir la source, la cible et le poids (`weight`) de l'interaction.
- **FR-005**: Tous les nœuds appartenant au même package (`group`) DOIVENT avoir la même couleur de remplissage.
- **FR-006**: Le système DOIT utiliser une palette de couleurs pour attribuer une couleur unique à chaque package.
- **FR-007**: Chaque lien DOIT être rendu avec un dégradé de couleur linéaire allant de la couleur du nœud source à la couleur du nœud cible.

### Key Entities *(include if feature involves data)*
- Aucune nouvelle entité de données n'est introduite. Cette fonctionnalité modifie uniquement la représentation visuelle des entités `Node` et `Link` existantes.

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: Au survol de n'importe quel nœud ou lien, l'infobulle correspondante apparaît en moins de 200 ms.
- **SC-002**: 100% des nœuds du diagramme sont colorés, et les nœuds du même package partagent la même couleur.
- **SC-003**: 100% des liens du diagramme affichent un dégradé de couleur visible entre leurs nœuds connectés.

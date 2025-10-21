# Implementation Plan: Générateur de diagramme de classes D3.js

**Branch**: `001-d3js-class-diagram` | **Date**: 2025-10-21 | **Spec**: [link to spec.md]
**Input**: Feature specification from `/Users/j.tama/projects/github/jtama/openrewrite-refactor-as-code/toxic-library-remover/specs/001-d3js-class-diagram/spec.md`

## Summary
Créer une recette OpenRewrite qui scanne un projet Java et génère un diagramme de classes interactif en utilisant D3.js. Le diagramme montrera les classes comme des nœuds et les interactions comme des liens, avec une taille et une épaisseur variables pour indiquer l'importance et la force du couplage.

## Technical Context
**Language/Version**: Java 25
**Primary Dependencies**: OpenRewrite, D3.js
**Storage**: N/A (génère un fichier HTML/JS)
**Testing**: JUnit 5
**Target Platform**: Tout système avec un JRE et un navigateur web moderne
**Project Type**: Recette OpenRewrite
**Performance Goals**: Générer un diagramme pour un projet de 200 classes en moins de 90 secondes.
**Constraints**: La sortie doit être un fichier HTML/JS autonome.
**Scale/Scope**: Gère des projets jusqu'à un nombre de classes configurable par l'utilisateur.

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Declarative Recipes**: Non, cette fonctionnalité nécessite une logique complexe de scan et de génération de données qui ne peut pas être réalisée en YAML. L'utilisation d'une recette Java est justifiée.
- **Idempotency**: Oui, la recette sera idempotente. L'exécution multiple produira le même diagramme.
- **Test-Driven Development**: Oui, le plan inclura la rédaction de tests pour la recette.
- **Performance Matters**: Oui, les implications en termes de performance seront prises en compte, avec un objectif de temps de génération clair.

## Project Structure
### Documentation (this feature)
```
specs/001-d3js-class-diagram/
├── plan.md
├── research.md
├── data-model.md
└── quickstart.md
```
### Source Code (repository root)
```
src/
└── main/
    └── java/
        └── com/
            └── github/
                └── jtama/
                    └── openrewrite/
                        └── recipe/
                            └── aerialView/
                                ├── ProjectAerialViewRecipe.java
                                └── model/
                                    ├── Node.java
                                    └── Link.java
test/
└── main/
    └── java/
        └── com/
            └── github/
                └── jtama/
                    └── openrewrite/
                        └── recipe/
                            └── aerialView/
                                └── ProjectAerialViewRecipeTest.java
```
**Structure Decision**: La structure suivra les conventions existantes du projet pour les recettes OpenRewrite, en plaçant la nouvelle recette dans un sous-package `aerialView` pour l'organisation.

## Complexity Tracking
| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A       | N/A        | N/A                                 |

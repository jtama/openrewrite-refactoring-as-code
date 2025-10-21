# Implementation Plan: Améliorations visuelles du diagramme

**Branch**: `002-diagram-visual-enhancements` | **Date**: 2025-10-21 | **Spec**: [link to spec.md]
**Input**: Feature specification from `/Users/j.tama/projects/github/jtama/openrewrite-refactor-as-code/toxic-library-remover/specs/002-diagram-visual-enhancements/spec.md`

## Summary
Améliorer le diagramme de classes D3.js existant en ajoutant des infobulles au survol, une coloration des nœuds par package, et des dégradés de couleur sur les liens pour indiquer la direction des dépendances.

## Technical Context
**Language/Version**: JavaScript (D3.js)
**Primary Dependencies**: D3.js v7
**Storage**: N/A
**Testing**: Manuel (vérification visuelle)
**Target Platform**: Navigateur web moderne
**Project Type**: Amélioration d'une fonctionnalité existante
**Performance Goals**: Les infobulles doivent apparaître en moins de 200ms.
**Constraints**: Les modifications doivent être contenues dans le fichier `template.html`.
**Scale/Scope**: S'applique à tous les diagrammes générés par la recette.

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Declarative Recipes**: Non applicable. Il s'agit d'une modification du code JavaScript de visualisation.
- **Idempotency**: Non applicable. La recette de génération reste idempotente.
- **Test-Driven Development**: Non applicable. Les tests pour cette fonctionnalité sont principalement visuels et manuels.
- **Performance Matters**: Oui, la performance de l'interaction (infobulles) est prise en compte.

## Project Structure
### Documentation (this feature)
```
specs/002-diagram-visual-enhancements/
├── plan.md
├── research.md
└── quickstart.md
```
### Source Code (repository root)
```
src/
└── main/
    └── resources/
        └── com/
            └── github/
                └── jtama/
                    └── openrewrite/
                        └── recipe/
                            └── aerialView/
                                └── template.html  (fichier modifié)
```
**Structure Decision**: Toutes les modifications seront apportées au fichier `template.html` existant, qui contient le code D3.js.

## Complexity Tracking
| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A       | N/A        | N/A                                 |
# Research: Améliorations visuelles du diagramme D3.js

## Decision: Coloration des nœuds par groupe
**Decision**: Utiliser `d3.scaleOrdinal(d3.schemeCategory10)` pour générer une palette de couleurs. Cette échelle associera une couleur unique à chaque `group` (package) de manière déterministe. La couleur sera appliquée à l'attribut `fill` des cercles SVG représentant les nœuds.

**Rationale**:
- `d3.scaleOrdinal` est la méthode standard et la plus simple pour mapper des données catégorielles (comme les noms de package) à une gamme de couleurs.
- `d3.schemeCategory10` est une palette de 10 couleurs distinctes qui offre un bon contraste visuel pour un nombre modéré de packages.

**Alternatives considered**:
- **Générer des couleurs manuellement**: Plus complexe à gérer et risque de produire des couleurs peu distinctes.

## Decision: Infobulles au survol
**Decision**: L'infobulle native du navigateur sera utilisée en ajoutant un élément `<title>` à l'intérieur de chaque élément SVG (`<circle>` et `<line>`). Le contenu textuel de l'élément `<title>` sera dynamiquement défini avec les données du nœud ou du lien.

**Rationale**:
- **Simplicité**: C'est la méthode la plus simple et la plus légère, ne nécessitant aucune bibliothèque supplémentaire ni de gestion complexe d'événements DOM.
- **Performance**: L'infobulle native est très performante et ne surcharge pas le rendu.

**Alternatives considered**:
- **Créer des infobulles personnalisées en HTML/CSS**: Offre plus de contrôle sur le style, mais augmente considérablement la complexité du code en nécessitant la gestion des événements `mouseover`/`mouseout`, le positionnement du `div` de l'infobulle, etc.

## Decision: Dégradés de couleur pour les liens
**Decision**: Utiliser des dégradés SVG (`<linearGradient>`). Pour chaque lien, un `<linearGradient>` unique sera défini dans la section `<defs>` du SVG. Chaque dégradé aura un `id` unique et sera configuré avec les couleurs des nœuds source et cible. L'attribut `stroke` de la ligne du lien référencera ensuite l'URL de cet `id`.

**Rationale**:
- C'est la méthode standard en SVG pour appliquer des dégradés à des formes.
- Elle offre un contrôle total sur l'orientation et les couleurs du dégradé.

**Alternatives considered**:
- **Simuler un dégradé avec plusieurs segments de ligne**: Très complexe à implémenter et peu performant.

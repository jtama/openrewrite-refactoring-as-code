# Quickstart: Project Aerial View Recipe

This guide explains how to use the `ProjectAerialViewRecipe` to generate an interactive class diagram for your Java project.

## How It Works

This is a **scanning recipe**, which means it does not modify your source code. Instead, it scans your project's class structure and generates a new file, `class-diagram.html`, in the root directory of your project.

## One-Time Usage (Command Line)

To run the recipe without modifying your `pom.xml`, you need to provide the recipe's artifact coordinates directly to Maven.

Execute the following command from your project's root directory:

```bash
mvn rewrite:run \
  -Drewrite.recipeArtifactCoordinates=com.github.jtama:toxic-library-remover:1.0.1-SNAPSHOT \
  -Drewrite.activeRecipes=com.github.jtama.openrewrite.ProjectAerialViewGenerator
```
This command tells Maven to download the recipe artifact (`recipeArtifactCoordinates`) and then run the specific recipe (`activeRecipes`).

## Recommended Usage (pom.xml Configuration)

For regular use, it's better to add the recipe as a dependency to the `rewrite-maven-plugin` in your `pom.xml`.

1.  **Add the plugin dependency**:

    ```xml
    <plugin>
        <groupId>org.openrewrite.maven</groupId>
        <artifactId>rewrite-maven-plugin</artifactId>
        <version>5.23.1</version> <!-- Or any recent version -->
        <dependencies>
            <dependency>
                <groupId>com.github.jtama</groupId>
                <artifactId>toxic-library-remover</artifactId>
                <version>1.0.1-SNAPSHOT</version>
            </dependency>
        </dependencies>
    </plugin>
    ```

2. **Configure the active recipe**:

    You can then activate the recipe directly in the plugin's configuration:

   ```xml
   <plugin>
       <groupId>org.openrewrite.maven</groupId>
       <artifactId>rewrite-maven-plugin</artifactId>
       <version>5.23.1</version>
       <configuration>
           <activeRecipes>
               <recipe>com.github.jtama.openrewrite.ProjectAerialViewGenerator</recipe>
           </activeRecipes>
           <!-- Optional: Limit the number of nodes -->
           <recipeParameters>
               <maxNodes>50</maxNodes>
           </recipeParameters>
       </configuration>
       <dependencies>
           <dependency>
               <groupId>com.github.jtama</groupId>
               <artifactId>toxic-library-remover</artifactId>
               <version>1.0.1-SNAPSHOT</version>
           </dependency>
       </dependencies>
   </plugin>
   ```

3.  **Run Maven**:

    ```bash
mvn rewrite:run
```

## Visual Features

The generated diagram includes several interactive and visual features to help you understand your project's architecture:

- **Tooltips on Hover**:
  - **Nodes**: Hover over a class to see its fully qualified name, package, and number of connections.
  - **Links**: Hover over a connection to see the source and target classes, and the number of interactions.

- **Color-Coded Nodes**: Each package is assigned a unique color. All classes within the same package share the same color, making it easy to spot modules and groupings.

- **Gradient Links**: The connections are drawn with a gradient from the source class's color to the target class's color, visually indicating the direction of the dependency.

## Viewing the Diagram

After the recipe runs, a file named `class-diagram.html` will be created in your project's root directory. Open this file in any modern web browser to see and interact with the visualization.
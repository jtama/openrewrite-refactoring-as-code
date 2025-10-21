# Research: D3.js Class Diagram Generation

## Decision: Data Serialization and Embedding

**Decision**: The Java recipe will serialize the graph data (nodes and links) into a JSON string. This JSON string will be directly embedded into a template HTML file. The HTML file will contain the necessary D3.js library and the custom JavaScript code to parse the JSON and render the diagram.

**Rationale**:
- **Simplicity**: This approach avoids the need for a separate server or complex file I/O. The output is a single, self-contained HTML file that can be opened directly in a browser.
- **Portability**: The generated file has no external dependencies and can be easily shared.
- **Performance**: For the target project sizes, embedding the JSON data directly into the HTML is efficient and avoids extra network requests.

**Alternatives considered**:
- **Generating separate JSON and HTML files**: This would require the user to host the files on a web server to avoid CORS issues when fetching the JSON. This adds unnecessary complexity for the user.
- **Using a Java-based web server to serve the data**: This would be a much more complex solution, requiring the user to run a server process.

## Decision: D3.js Library

**Decision**: We will use the latest stable version of D3.js (v7). The library will be included in the generated HTML file via a CDN link.

**Rationale**:
- **Features**: v7 provides all the necessary features for force-directed graphs and animations.
- **CDN**: Using a CDN is the simplest way to include the library without needing to bundle it with the recipe. This also ensures the user always has the latest version.

**Alternatives considered**:
- **Bundling D3.js with the recipe**: This would increase the size of the recipe and require a more complex build process.

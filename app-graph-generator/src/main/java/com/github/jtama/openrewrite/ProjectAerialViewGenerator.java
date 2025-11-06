package com.github.jtama.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jtama.openrewrite.model.Link;
import com.github.jtama.openrewrite.model.Node;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.marker.JavaProject;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.JavaType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An OpenRewrite recipe that scans a Java project and generates an interactive D3.js class diagram.
 */
public class ProjectAerialViewGenerator extends ScanningRecipe<ProjectAerialViewGenerator.Graph> {

    @Option(displayName = "Maximum nodes",
            description = "The maximum number of nodes to display in the graph.",
            required = false)
    private Integer maxNodes;

    @Option(displayName = "Base packages",
            description = "A list of base packages to scan for imports.",
            example = "[\"com.yourorg.project\"]",
            required = false)
    private String basePackages;

    private List<String> packages;

    public ProjectAerialViewGenerator() {
    }

    /**
     * Creates a new instance of the recipe.
     *
     * @param maxNodes     The maximum number of nodes to display in the graph.
     * @param basePackages A list of base packages to scan for imports.
     */
    @JsonCreator
    public ProjectAerialViewGenerator(@JsonProperty("maxNodes") Integer maxNodes, @JsonProperty("basePackages") String basePackages) {
        this.maxNodes = maxNodes;
        this.packages = basePackages != null ? List.of(basePackages.split(",")) : null;
    }


    /**
     * The accumulator for the recipe, holding the graph data.
     */
    public static class Graph {
        public List<Node> nodes = new ArrayList<>();
        public List<Link> links = new ArrayList<>();

        public Optional<Node> findNode(String id) {
            return nodes.stream().filter(n -> n.getId().equals(id)).findFirst();
        }

        public Optional<Link> findLink(String source, String target) {
            return links.stream().filter(l -> l.getSource().equals(source) && l.getTarget().equals(target)).findFirst();
        }
    }

    @Override
    public String getDisplayName() {
        return "Project Aerial View";
    }

    @Override
    public String getDescription() {
        return "Generates a D3.js visualization of the project's class structure.";
    }

    @Override
    public Graph getInitialValue(ExecutionContext ctx) {
        return new Graph();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Graph graph) {
        return new JavaIsoVisitor<>() {

            @Override
            public J preVisit(J tree, ExecutionContext ctx) {
                if (tree instanceof JavaSourceFile) {
                    tree.getMarkers().findFirst(JavaProject.class).ifPresent(javaProject -> {
                        if (packages == null || packages.isEmpty()) {
                            packages = new ArrayList<>();
                            packages.add(javaProject.getPublication().getGroupId());
                        }
                    });
                }
                return tree;
            }

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
                if (!Objects.requireNonNull(getCursor().firstEnclosing(JavaSourceFile.class)).getSourcePath().toString().contains("src/test"))
                    return super.visitClassDeclaration(classDecl, executionContext);
                return classDecl;
            }

            @Override
            public J.MemberReference visitMemberReference(J.MemberReference memberReference, ExecutionContext executionContext) {
                var member = super.visitMemberReference(memberReference, executionContext);
                JavaType.FullyQualified targetType = memberReference.getMethodType().getDeclaringType();
                addLink(targetType);
                return member;
            }

            @Override
            public J.FieldAccess visitFieldAccess(J.FieldAccess fieldAccess, ExecutionContext executionContext) {
                var fa = super.visitFieldAccess(fieldAccess, executionContext);

                JavaType.FullyQualified targetType = switch (fa.getTarget().getType()) {
                    case JavaType.FullyQualified fq -> fq;
                    case JavaType.Array array -> (JavaType.FullyQualified) array.getElemType();
                    default -> null;
                };

                addLink(targetType);
                return fa;
            }

            @Override
            public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
                var mi = super.visitMethodInvocation(method, ctx);
                JavaType.FullyQualified targetType = mi.getMethodType() != null ? mi.getMethodType().getDeclaringType() : null;
                addLink(targetType);
                return mi;
            }

            @Override
            public J.NewClass visitNewClass(J.NewClass newClass, ExecutionContext executionContext) {
                J.NewClass visitedNewClass = super.visitNewClass(newClass, executionContext);
                JavaType.FullyQualified targetType = visitedNewClass.getMethodType().getDeclaringType();
                addLink(targetType);
                return visitedNewClass;
            }

            private void addLink(JavaType.FullyQualified targetType) {
                if (packages.stream().noneMatch(basePackage -> targetType.getPackageName().contains(basePackage)))
                    return;
                J.ClassDeclaration enclosingClass = getCursor().firstEnclosing(J.ClassDeclaration.class);
                if (enclosingClass == null) {
                    System.out.println(getCursor());
                    return;
                }
                JavaType.FullyQualified sourceType = enclosingClass.getType();
                if (sourceType != null && !sourceType.equals(targetType)) {
                    String sourceFq = sourceType.getFullyQualifiedName();
                    String targetFq = targetType.getFullyQualifiedName();

                    Node sourceNode = graph.findNode(sourceFq).orElseGet(() -> {
                        Node newNode = new Node(sourceFq, sourceType.getPackageName());
                        graph.nodes.add(newNode);
                        return newNode;
                    });
                    Node targetNode = graph.findNode(targetFq).orElseGet(() -> {
                        Node newNode = new Node(targetFq, targetType.getPackageName());
                        graph.nodes.add(newNode);
                        return newNode;
                    });

                    Link link = graph.findLink(sourceFq, targetFq).orElseGet(() -> {
                        Link newLink = new Link(sourceFq, targetFq);
                        graph.links.add(newLink);
                        sourceNode.incrementSize();
                        targetNode.incrementSize();
                        return newLink;
                    });
                    link.incrementWeight();
                }
            }
        };
    }


    @Override
    public Collection<J.CompilationUnit> generate(Graph graph, ExecutionContext ctx) {

        try (InputStream templateStream = getClass().getResourceAsStream("template.html")) {
            Graph finalGraph = filterGraph(graph);
            String json = new ObjectMapper().writeValueAsString(finalGraph);
            if (templateStream == null) {
                throw new IllegalStateException("template.html not found");
            }
            String template = new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);
            String html = template.replace("'{{graphData}}'", json);

            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Files.writeString(projectDir.resolve("class-diagram.html"), html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    private Graph filterGraph(Graph graph) {
        if (maxNodes == null || graph.nodes.size() <= maxNodes) {
            return graph;
        }

        Graph filteredGraph = new Graph();
        graph.nodes.sort(Comparator.comparingInt(Node::getSize).reversed());
        filteredGraph.nodes = new ArrayList<>(graph.nodes.subList(0, maxNodes));
        List<String> topNodeIds = filteredGraph.nodes.stream().map(Node::getId).toList();

        filteredGraph.links = graph.links.stream()
                .filter(l -> topNodeIds.contains(l.getSource()) && topNodeIds.contains(l.getTarget()))
                .collect(Collectors.toList());

        return filteredGraph;
    }
}
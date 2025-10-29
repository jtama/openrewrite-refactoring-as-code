package com.github.jtama.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.RemoveUnusedImports;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Space;
import org.openrewrite.java.tree.TypeTree;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public class ExtractInterface extends ScanningRecipe<ExtractInterface.Accumulator> {

    @Option(displayName = "The targeted annotation", description = "Interface will be extracted for each class marked by this annotation", example = "jakarta.inject.Singleton")
    String targetAnnotation;


    @JsonCreator
    public ExtractInterface(@JsonProperty("targetAnnotation") String targetAnnotation) {
        this.targetAnnotation = targetAnnotation;
    }

    @Override
    public String getDisplayName() {
        return "Extract controller interface";
    }

    @Override
    public String getDescription() {
        return "Extract controller interface.";
    }

    @Override
    public Accumulator getInitialValue(ExecutionContext ctx) {
        return new Accumulator();
    }

    @Override
    public List<Recipe> getRecipeList() {
        return List.of(new RemoveUnusedImports());
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Accumulator acc) {
        return new TreeVisitor<>() {
            final Predicate<J.ClassDeclaration> isClass =
                    aClass -> aClass.getKind().equals(J.ClassDeclaration.Kind.Type.Class);
            final BiPredicate<List<TypeTree>, Accumulator.ToExtract> alreadyImplementsInterface = (implementList, toExtract) -> implementList != null && implementList.stream().anyMatch(tt ->
                    switch (tt) {
                        case J.Identifier identifier ->
                                identifier.getSimpleName().equals(toExtract.extractedInterfaceName());
                        case J.ParameterizedType parameterized ->
                                parameterized.getType().isAssignableFrom(Pattern.compile(toExtract.extractedInterfaceName()));
                        default -> false;
                    }
            );
            final AnnotationMatcher matcher = new AnnotationMatcher(targetAnnotation);

            @Override
            public Tree visit(Tree tree, ExecutionContext executionContext, Cursor parent) {
                if (tree instanceof JavaSourceFile javaSourceFile) {
                    javaSourceFile.getClasses().stream().filter(isClass).forEach(classDecla -> {
                        String newFQDN = getNewFQDN(classDecla);
                        List<TypeTree> implementList = classDecla.getImplements();
                        Accumulator.ToExtract toExtract = new Accumulator.ToExtract(newFQDN);
                        toExtract.setFromSourceFile(javaSourceFile);
                        if (!alreadyImplementsInterface.test(implementList, toExtract) && classDecla.getLeadingAnnotations().stream().anyMatch(ann -> matcher.matches(ann))) {
                            toExtract.setClassToExtract(classDecla);
                            acc.duplicates().put(newFQDN, toExtract);
                        }
                    });

                }
                return super.visit(tree, executionContext, parent);
            }
        };
    }

    public Collection<? extends SourceFile> generate(Accumulator acc, ExecutionContext ctx) {
        if (acc.duplicates.isEmpty()) {
            return List.of();
        }
        return acc.duplicates().values().stream()
                .map(item -> mapToSourceFile(item, ctx))
                .toList();
    }

    private J.CompilationUnit mapToSourceFile(Accumulator.ToExtract toExtract, ExecutionContext ctx) {
        String initPath = toExtract.fromSourceFile().getSourcePath().toString();
        var newPath = Path.of(initPath.substring(0, initPath.lastIndexOf("/")), toExtract.extractedInterfaceName() + ".java");
        toExtract.setExtractedInterfacePath(newPath);
        J.CompilationUnit extractedInterface = toExtract.fromSourceFile()
                .withClasses(List.of(getExtractedInterface(toExtract, ctx)))
                .withSourcePath(newPath)
                .withId(UUID.randomUUID());
        return (J.CompilationUnit) new RemoveUnusedImports().getVisitor().visit(extractedInterface, ctx);
    }

    private static String getNewFQDN(J.ClassDeclaration classDeclaration) {
        String packageDeclaration = classDeclaration.getType() != null ?
                classDeclaration.getType().getPackageName() :
                "";
        return "%s.I%s".formatted(packageDeclaration, classDeclaration.getName().getSimpleName());
    }

    private J.ClassDeclaration getExtractedInterface(Accumulator.ToExtract toExtract, ExecutionContext ctx) {
        J.ClassDeclaration initial = toExtract.classToExtract();
        J.ClassDeclaration result = initial
                .withKind(J.ClassDeclaration.Kind.Type.Interface)
                .withName(TypeTree.build(toExtract.extractedInterfaceName()).withPrefix(Space.SINGLE_SPACE))
                .withType(new JavaType.ShallowClass(null, 1, toExtract.extractedInterfaceFQDN(), JavaType.FullyQualified.Kind.Interface, emptyList(), null, null, emptyList(), emptyList(), emptyList(), emptyList()))
                .withId(UUID.randomUUID())
                .withLeadingAnnotations(new ArrayList<>(initial.getLeadingAnnotations()));
        result = (J.ClassDeclaration) result.acceptJava(new CleanerVisitor(), ctx);
        toExtract.setExtractedInterface(result);
        return result;

    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Accumulator acc) {
        return new JavaIsoVisitor<>() {

            AnnotationMatcher targetAnnotationMatcher = new AnnotationMatcher(targetAnnotation);
            public static final String TARGET_CLASS = "TARGET";

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration cd, ExecutionContext executionContext) {
                Accumulator.ToExtract target = acc.duplicates.get(getNewFQDN(cd));
                if (target != null) {
                    getCursor().putMessage(TARGET_CLASS, target);
                    var annotations = cd.getLeadingAnnotations();
                    annotations.forEach(ann -> maybeRemoveImport(ann.getSimpleName()));
                    cd = cd.withLeadingAnnotations(List.of())
                            .withImplements(List.of(TypeTree.build(target.extractedInterfaceName())
                                    .withType(target.extractedInterface().getType())
                                    .withPrefix(Space.SINGLE_SPACE)))
                            .withPrefix(Space.format(System.lineSeparator()));
                    if (cd.getPadding().getImplements().getBefore().getWhitespace().isEmpty()) {
                        cd = cd.getPadding().withImplements(cd.getPadding().getImplements().withBefore(Space.SINGLE_SPACE));
                    }
                }
                cd = super.visitClassDeclaration(cd, executionContext);
                return cd;
            }

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                if (getCursor().getNearestMessage(TARGET_CLASS) != null) {
                    return JavaTemplate.builder("@Override").build().apply(getCursor(), method.getCoordinates().replaceAnnotations());
                }
                return super.visitMethodDeclaration(method, executionContext);
            }
        };
    }

    static class CleanerVisitor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
            return method
                    .withBody(null)
                    .withModifiers(List.of())
                    .withReturnTypeExpression(!method.getLeadingAnnotations().isEmpty() ? method.getReturnTypeExpression().withPrefix(Space.SINGLE_SPACE) : method.getReturnTypeExpression().withPrefix(Space.EMPTY));
        }

        @Override
        public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
            return null;
        }
    }

    public static class Accumulator {

        private final Map<String, ToExtract> duplicates = new HashMap<>();

        public Map<String, ToExtract> duplicates() {
            return duplicates;
        }

        public static class ToExtract {
            // The contents of the file we want to extract
            JavaSourceFile fromSourceFile;
            J.ClassDeclaration classToExtract;
            J.ClassDeclaration extractedInterface;
            Path extractedInterfacePath;
            String extractedInterfaceFQDN;

            public ToExtract(String extractedInterfaceFQDN) {
                this.extractedInterfaceFQDN = extractedInterfaceFQDN;
            }

            public J.ClassDeclaration extractedInterface() {
                return extractedInterface;
            }

            public void setExtractedInterface(J.ClassDeclaration extractedInterface) {
                this.extractedInterface = extractedInterface;
            }

            public Path extractedInterfacePath() {
                return extractedInterfacePath;
            }

            public JavaSourceFile fromSourceFile() {
                return fromSourceFile;
            }

            public void setFromSourceFile(JavaSourceFile fromSourceFile) {
                this.fromSourceFile = fromSourceFile;
            }

            public void setExtractedInterfacePath(Path extractedInterfacePath) {
                this.extractedInterfacePath = extractedInterfacePath;
            }

            public String extractedInterfaceFQDN() {
                return extractedInterfaceFQDN;
            }

            public String extractedInterfaceName() {
                int lastIndexOfDot = extractedInterfaceFQDN.lastIndexOf(".") + 1;
                return lastIndexOfDot > 0 ? extractedInterfaceFQDN.substring(lastIndexOfDot) : extractedInterfaceFQDN;
            }

            public J.ClassDeclaration classToExtract() {
                return classToExtract;
            }

            public void setClassToExtract(J.ClassDeclaration sourceFileToDuplicate) {
                this.classToExtract = sourceFileToDuplicate;
            }
        }
    }
}

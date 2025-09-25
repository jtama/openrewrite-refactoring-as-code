package com.github.jtama.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jtama.toxic.LearnToFly;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public class ExtractInterface extends ScanningRecipe<ExtractInterface.Accumulator> {

    @Option(displayName = "Source module", description = "The source module folder name", example = "foo")
    @NonNull
    String sourceModule;


    @Option(displayName = "Target module", description = "The target module folder name", example = "foo")
    @NonNull
    String targetModule;

    @JsonCreator
    public ExtractInterface(@NonNull @JsonProperty("sourceModule") String sourceModule, @NonNull @JsonProperty("targetModule") String targetModule) {
        this.sourceModule = sourceModule;
        this.targetModule = targetModule;
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

            AnnotationMatcher matcher = new AnnotationMatcher(LearnToFly.class);
            Predicate<JavaSourceFile> isClass = javaSourceFile -> !javaSourceFile.getClasses().getFirst().getKind().equals(J.ClassDeclaration.Kind.Type.Interface);
            BiPredicate<List<TypeTree>, Accumulator.ToDuplicate> alreadyImplementsInterface = (implementList, toDuplicate) -> implementList != null && implementList.stream().anyMatch(tt ->
                    switch (tt) {
                        case J.Identifier identifier ->
                                identifier.getSimpleName().equals(toDuplicate.extractedInterfaceName());
                        case J.ParameterizedType parameterized ->
                                parameterized.getType().isAssignableFrom(Pattern.compile(toDuplicate.extractedInterfaceName()));
                        default -> false;
                    }
            );

            @Override
            public Tree visit(Tree tree, ExecutionContext executionContext, Cursor parent) {
                if (tree instanceof JavaSourceFile javaSourceFile && isClass.test(javaSourceFile)) {
                    String newFQDN = getNewFQDN(javaSourceFile);
                    List<TypeTree> implementList = javaSourceFile.getClasses().getFirst().getImplements();
                    Accumulator.ToDuplicate toDuplicate = new Accumulator.ToDuplicate(newFQDN);
                    if (!alreadyImplementsInterface.test(implementList, toDuplicate) && javaSourceFile.getClasses().getFirst().getLeadingAnnotations().stream().anyMatch(ann -> matcher.matches(ann))) {
                        toDuplicate.setSourceFileToDuplicate(javaSourceFile);
                        acc.duplicates().put(newFQDN, toDuplicate);
                    }
                }
                return super.visit(tree, executionContext, parent);
            }
        };
    }

    public Collection<? extends SourceFile> generate(Accumulator acc, ExecutionContext ctx) {
        if (acc.duplicates.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SourceFile> generated = new ArrayList<>();
        for (Accumulator.ToDuplicate toDuplicate : acc.duplicates.values()) {
            var sourceFile = toDuplicate.sourceFilesToDuplicate();
            var withChangedRootPath = sourceFile.getSourcePath().toString().replace(sourceModule, targetModule);
            var newPath = Path.of(withChangedRootPath.substring(0, withChangedRootPath.lastIndexOf("/")), toDuplicate.extractedInterfaceName() + ".java");
            toDuplicate.setExtractedInterfacePath(newPath);
            JavaSourceFile extractedInterface = sourceFile
                    .withClasses(List.of(getExtractedInterface(toDuplicate, ctx)))
                    .withSourcePath(newPath)
                    .withId(UUID.randomUUID());
            generated.add((JavaSourceFile) new RemoveUnusedImports().getVisitor().visit(extractedInterface, ctx));
        }
        return generated;
    }

    private static @NotNull String getNewFQDN(JavaSourceFile sourceFile) {
        J.ClassDeclaration classDeclaration = sourceFile.getClasses().getFirst();
        String packageDecalration = sourceFile.getPackageDeclaration() != null ? sourceFile.getPackageDeclaration().getPackageName() : "";
        return "%s.I%s".formatted(packageDecalration, classDeclaration.getName().getSimpleName());
    }

    private J.ClassDeclaration getExtractedInterface(Accumulator.ToDuplicate toDuplicate, ExecutionContext ctx) {
        J.ClassDeclaration initial = toDuplicate.sourceFilesToDuplicate().getClasses().getFirst();
        J.ClassDeclaration result = initial
                .withKind(J.ClassDeclaration.Kind.Type.Interface)
                .withName(TypeTree.build(toDuplicate.extractedInterfaceName()).withPrefix(Space.SINGLE_SPACE))
                .withType(new JavaType.ShallowClass(null, 1, toDuplicate.extractedInterfaceFQDN(), JavaType.FullyQualified.Kind.Interface, emptyList(), null, null, emptyList(), emptyList(), emptyList(), emptyList()))
                .withId(UUID.randomUUID())
                .withLeadingAnnotations(new ArrayList<>(initial.getLeadingAnnotations()));
        result = (J.ClassDeclaration) result.acceptJava(new JavaIsoVisitor<>() {
            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                return method.withBody(null).withModifiers(List.of()).withReturnTypeExpression(!method.getLeadingAnnotations().isEmpty() ? method.getReturnTypeExpression().withPrefix(Space.SINGLE_SPACE) : method.getReturnTypeExpression().withPrefix(Space.EMPTY));
            }

            @Override
            public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
                return null;
            }
        }, ctx);
        toDuplicate.setExtractedInterface(result);
        return result;

    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Accumulator acc) {
        return new JavaIsoVisitor<>() {

            AnnotationMatcher matcher = new AnnotationMatcher(LearnToFly.class);
            public static final String TARGET_CLASS = "TARGET";

            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext executionContext) {
                String newFQDN = getNewFQDN(cu);
                Accumulator.ToDuplicate toDuplicate = acc.duplicates.get(newFQDN);
                if (toDuplicate != null && toDuplicate.sourceFileToDuplicate.getClasses().getFirst().equals(cu.getClasses().getFirst())) {
                    getCursor().putMessage(TARGET_CLASS, toDuplicate);
                }
                return super.visitCompilationUnit(cu, executionContext);
            }

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration cd, ExecutionContext executionContext) {

                cd = super.visitClassDeclaration(cd, executionContext);
                Accumulator.ToDuplicate target = getCursor().getNearestMessage(TARGET_CLASS);
                if (target != null) {
                    var annotations = cd.getLeadingAnnotations();
                    annotations.removeIf(ann -> matcher.matches(ann));
                    maybeRemoveImport(LearnToFly.class.getCanonicalName());
                    cd = cd.withLeadingAnnotations(annotations)
                            .withImplements(List.of(TypeTree.build(target.extractedInterfaceName())
                                    .withType(target.extractedInterface().getType())
                                    .withPrefix(Space.SINGLE_SPACE)));
                    if (cd.getLeadingAnnotations().isEmpty()) {
                        cd = cd.withPrefix(Space.format(System.lineSeparator()));
                    }
                    if (cd.getPadding().getImplements().getBefore().getWhitespace().isEmpty()) {
                        cd = cd.getPadding().withImplements(cd.getPadding().getImplements().withBefore(Space.SINGLE_SPACE));
                    }
                }
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


    public static class Accumulator {

        private Map<String, ToDuplicate> duplicates = new HashMap<>();

        public Map<String, ToDuplicate> duplicates() {
            return duplicates;
        }

        public static class ToDuplicate {
            // The contents of the file we want to duplicate
            JavaSourceFile sourceFileToDuplicate;
            J.ClassDeclaration extractedInterface;
            Path extractedInterfacePath;
            String extractedInterfaceFQDN;

            public ToDuplicate(String extractedInterfaceFQDN) {
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

            public void setExtractedInterfacePath(Path extractedInterfacePath) {
                this.extractedInterfacePath = extractedInterfacePath;
            }

            public String extractedInterfaceFQDN() {
                return extractedInterfaceFQDN;
            }

            public String extractedInterfaceName() {
                int lastIndexOfDot = extractedInterfaceFQDN.lastIndexOf(".") + 1;
                return lastIndexOfDot != -1 ? extractedInterfaceFQDN.substring(lastIndexOfDot) : extractedInterfaceFQDN;
            }

            public void setExtractedInterfaceFQDN(String extractedInterfaceFQDN) {
                this.extractedInterfaceFQDN = extractedInterfaceFQDN;
            }

            public JavaSourceFile sourceFilesToDuplicate() {
                return sourceFileToDuplicate;
            }

            public void setSourceFileToDuplicate(JavaSourceFile sourceFileToDuplicate) {
                this.sourceFileToDuplicate = sourceFileToDuplicate;
            }
        }
    }
}

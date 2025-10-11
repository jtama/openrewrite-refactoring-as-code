package com.github.jtama.openrewrite;

import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;

import java.util.Comparator;

public class RemoveLogStartInvocations extends Recipe {


    @Override
    public String getDisplayName() {
        return "Remove `Timer.logStart()` usages";
    }

    @Override
    public String getDescription() {
        return "Replace any usage of `Timer.logStart()` and `Timer.logEnd()` methods by `@Timed` annotation.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new Preconditions.Check(new UsesType<>("com.github.jtama.toxic.Timer", true),
                new ReplaceCompareVisitor());
    }

    private static class ReplaceCompareVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final MethodMatcher logStartInvocaMatcher = new MethodMatcher("com.github.jtama.toxic.Timer logStart()");
        private final MethodMatcher logEndInvocaMatcher = new MethodMatcher("com.github.jtama.toxic.Timer logEnd()");
        private final AnnotationMatcher timedMatcher = new AnnotationMatcher("@io.micrometer.core.annotation.Timed");
        private final JavaTemplate annotationTemplate = JavaTemplate.builder("@Timed")
                .imports("io.micrometer.core.annotation.Timed")
                .javaParser(JavaParser.fromJavaVersion().classpath(JavaParser.runtimeClasspath()))
                .build();

        public ReplaceCompareVisitor() {
            maybeRemoveImport("com.github.jtama.toxic.Timer");
        }

        @Override
        public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
            J.MethodDeclaration md = super.visitMethodDeclaration(method, ctx);
            Cursor cursor = getCursor();
            if (cursor.getMessage("appendAnnotation", false)) {
                if (md.getLeadingAnnotations().stream()
                        .noneMatch(timedMatcher::matches)) {
                    maybeAddImport("io.micrometer.core.annotation.Timed");
                    md = annotationTemplate.apply(cursor, method.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));
                }
            }
            return md;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation mi = super.visitMethodInvocation(method, ctx);
            if (logStartInvocaMatcher.matches(mi) || logEndInvocaMatcher.matches(mi)) {
                getCursor().putMessageOnFirstEnclosing(J.MethodDeclaration.class, "appendAnnotation", true);
                return null;
            }
            return mi;
        }
    }
}

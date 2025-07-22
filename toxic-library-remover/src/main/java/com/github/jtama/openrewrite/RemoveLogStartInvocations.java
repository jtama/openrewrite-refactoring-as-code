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
import org.openrewrite.java.RemoveMethodInvocationsVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;

import java.util.Collections;
import java.util.Comparator;

public class RemoveLogStartInvocations extends Recipe {


    @Override
    public String getDisplayName() {
        return "Remove `FooBarUtils.compare()` usages";
    }

    @Override
    public String getDescription() {
        return "Replace any usage of `FooBarUtils.compare()` method by `Objects.compare()` invocations.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new Preconditions.Check(new UsesType<>("com.github.jtama.toxic.FooBarUtils", true),
                new ReplaceCompareVisitor());
    }

    private static class ReplaceCompareVisitor extends JavaIsoVisitor<ExecutionContext> {

        public static final String LOG_START_INVOCATION_PATTERN = "com.github.jtama.toxic.FooBarUtils logStart()";
        private final MethodMatcher logStartInvocaMatcher = new MethodMatcher(LOG_START_INVOCATION_PATTERN);
        private final AnnotationMatcher logStartMatcher = new AnnotationMatcher("@io.micrometer.core.annotation.Timed");
        private final JavaTemplate annotationTemplate = JavaTemplate.builder("@Timed")
                .imports("io.micrometer.core.annotation.Timed")
                .javaParser(JavaParser.fromJavaVersion().classpath(JavaParser.runtimeClasspath()))
                .build();

        public ReplaceCompareVisitor() {
            maybeRemoveImport("com.github.jtama.toxic.FooBarUtils");
        }

        @Override
        public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
            J.MethodDeclaration md = super.visitMethodDeclaration(method, ctx);
            Cursor cursor = getCursor();
            if (cursor.getMessage("appendAnnotation", false)) {
                if (md.getLeadingAnnotations().stream()
                        .noneMatch(logStartMatcher::matches)) {
                    maybeAddImport("io.micrometer.core.annotation.Timed");
                    md = annotationTemplate.apply(cursor, method.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));
                }
            }
            return md;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation mi = super.visitMethodInvocation(method, ctx);
            if (!logStartInvocaMatcher.matches(mi)) {
                return mi;
            }
            getCursor().putMessageOnFirstEnclosing(J.MethodDeclaration.class, "appendAnnotation", true);
            return null;
        }
    }
}

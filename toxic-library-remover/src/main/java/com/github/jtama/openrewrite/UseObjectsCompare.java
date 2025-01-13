package com.github.jtama.openrewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;

import java.util.List;

public class UseObjectsCompare extends Recipe {


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

        private final MethodMatcher compareMethodMatcher = new MethodMatcher("com.github.jtama.toxic.FooBarUtils compare(..)");
        private final JavaTemplate objectsCompareTemplate = JavaTemplate.builder("Objects.compare(#{any(java.lang.Object)}, #{any(java.lang.Object)}, #{any(java.util.Comparator)})")
                .imports("java.util.Objects")
                .javaParser(JavaParser.fromJavaVersion().classpath(JavaParser.runtimeClasspath()))
                .build();

        public ReplaceCompareVisitor() {
            maybeRemoveImport("com.github.jtama.toxic.FooBarUtils");
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation mi = super.visitMethodInvocation(method, ctx);
            if (compareMethodMatcher.matches(mi)) {
                maybeAddImport("java.util.Objects");
                J.MethodInvocation invocation = objectsCompareTemplate.apply(getCursor(), mi.getCoordinates().replace(),
                        mi.getArguments().get(0), mi.getArguments().get(1), mi.getArguments().get(2));
                mi = invocation.withComments(List.of(new TextComment(
                        false,
                        "Comparing %s using %s".formatted(
                                    mi.getArguments().get(0).getType().toString(),
                                    mi.getArguments().get(2).getType().toString()),
                                    mi.getPrefix().getWhitespace() ,Markers.EMPTY)
                ));
            }
            return mi;
        }
    }
}

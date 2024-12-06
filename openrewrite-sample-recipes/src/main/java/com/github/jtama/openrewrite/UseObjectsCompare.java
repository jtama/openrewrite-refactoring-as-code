package com.github.jtama.openrewrite;

import com.github.jtama.toxic.FooBarUtils;
import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.template.RecipeDescriptor;
import org.openrewrite.java.tree.J;

import java.util.Comparator;
import java.util.Objects;

@RecipeDescriptor(
        name = "Remove `FooBarUtils.compare()` usages",
        description = "Replace any usage of `FooBarUtils.compare()` method by `Objects.compare()` invocations.")
public class UseObjectsCompare extends Recipe {


    @BeforeTemplate
    public <T> int compare(T o1, T o2, Comparator<T> comparator) {
        return new FooBarUtils().compare(o1, o2, comparator);
    }

    @AfterTemplate
    public <T> int objectsCompare(T o1, T o2, Comparator<T> comparator) {
        return Objects.compare(o1, o2, comparator);
    }

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
                mi = objectsCompareTemplate.apply(getCursor(), mi.getCoordinates().replace(),
                        mi.getArguments().get(0), mi.getArguments().get(1), mi.getArguments().get(2));
            }
            return mi;
        }
    }
}

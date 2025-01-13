package com.github.jtama.openrewrite;

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
import org.openrewrite.java.tree.Javadoc;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;
import org.openrewrite.staticanalysis.EmptyBlock;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

        private final MethodMatcher compareMethodMatcher = new MethodMatcher("com.github.jtama.toxic.FooBarUtils logStart()");
        private final AnnotationMatcher logStartMatcher = new AnnotationMatcher("@foo.bar.LogStart");
        private final JavaTemplate annotationTemplate = JavaTemplate.builder("@LogStart")
                .imports("foo.bar.LogStart")
                .javaParser(JavaParser.fromJavaVersion().classpath(JavaParser.runtimeClasspath()))
                .build();

        public ReplaceCompareVisitor() {
            maybeRemoveImport("com.github.jtama.toxic.FooBarUtils");
        }

        @Override
        public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
            J.MethodDeclaration md = super.visitMethodDeclaration(method, ctx);
            if (ctx.getMessage("appendComment", false)) {
                if (md.getComments().stream().noneMatch(c -> c.toString().contains("//FIXXME"))) {
                    md = md.withComments(List.of(new TextComment(false,"FIXME: If start logging is really needed,please find a proper way to do this", System.lineSeparator() + md.getPrefix().getIndent(), Markers.EMPTY)));
                    ctx.putMessage("appendComment", false);
                }
            }
            return md;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation mi = super.visitMethodInvocation(method, ctx);
            if (!compareMethodMatcher.matches(mi)) {
                return mi;
            }
            ctx.putMessage("appendComment", true);
            this.doAfterVisit(new EmptyBlock().getVisitor());
            return null;
        }
    }
}

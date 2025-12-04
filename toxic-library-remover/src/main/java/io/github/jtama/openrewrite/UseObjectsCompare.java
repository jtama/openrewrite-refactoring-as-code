package io.github.jtama.openrewrite;

import org.jetbrains.annotations.NotNull;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.Comment;
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
                mi = objectsCompareTemplate.apply(getCursor(), mi.getCoordinates().replace(),
                        mi.getArguments().get(0), mi.getArguments().get(1), mi.getArguments().get(2));
                String comment = "Comparing %s using %s".formatted(
                        mi.getArguments().get(0).getType().toString(),
                        mi.getArguments().get(2).getType().toString());
                if (getCursor().getParent().getParent().getValue() instanceof J.Block) {
                    mi = mi.withComments(getComment(comment, mi));
                } else {
                    getCursor().dropParentUntil(this::isAcceptable).putMessage("comment", comment);
                }
            }
            return mi;
        }

        @Override
        public J.Assignment visitAssignment(J.Assignment assignment, ExecutionContext executionContext) {
            J.Assignment visitedAssignment = super.visitAssignment(assignment, executionContext);
            String comment = getCursor().getMessage("comment", "");
            if (!comment.isEmpty() && visitedAssignment.getComments().isEmpty()) {
                visitedAssignment = visitedAssignment.withComments(getComment(comment, visitedAssignment));
                getCursor().clearMessages();
            }
            return visitedAssignment;
        }

        @Override
        public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
            J.VariableDeclarations visitVariableDeclarations = super.visitVariableDeclarations(multiVariable, executionContext);
            String comment = getCursor().getMessage("comment", "");
            if (!comment.isEmpty() && visitVariableDeclarations.getComments().isEmpty()) {
                visitVariableDeclarations = visitVariableDeclarations.withComments(getComment(comment, visitVariableDeclarations));
                getCursor().clearMessages();
            }
            return visitVariableDeclarations;
        }

        @Override
        public J.Return visitReturn(J.Return _return, ExecutionContext executionContext) {
            J.Return visitedReturn = super.visitReturn(_return, executionContext);
            String comment = getCursor().getMessage("comment", "");
            if (!comment.isEmpty() && visitedReturn.getComments().isEmpty()) {
                visitedReturn = visitedReturn.withComments(getComment(comment, visitedReturn));
                getCursor().clearMessages();
            }
            return visitedReturn;
        }

        private @NotNull List<Comment> getComment(String commment, J j) {
            return List.of(new TextComment(false, commment, j.getPrefix().getWhitespace(), Markers.EMPTY));
        }

        private boolean isAcceptable(Object j) {
            return switch (j) {
                case J.VariableDeclarations v -> true;
                case J.Return r -> true;
                case J.Assignment a -> true;
                default -> false;
            };
        }
    }
}

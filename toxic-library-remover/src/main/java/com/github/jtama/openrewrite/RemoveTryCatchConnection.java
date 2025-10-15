package com.github.jtama.openrewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Space;

import java.util.List;

public class RemoveTryCatchConnection extends Recipe {

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
        return new RemoveTryCatchConnection.RemoveTryCatchConnectionVisitor();
    }

    public class RemoveTryCatchConnectionVisitor extends JavaVisitor<ExecutionContext> {

        public static final String PATH_PARAM = "pathParam";
        public static final String IS_EMPTY_PARAM = "isEmptyParam";
        private final MethodMatcher isEmptyInvocaMatcher = new MethodMatcher("com.github.jtama.toxic.FooBarUtils isEmpty(java.lang.String)");
        private final MethodMatcher newFileMatcher = new MethodMatcher("java.io.File <constructor>(..)");
        private final JavaTemplate template = JavaTemplate.builder("Path.of(#{any()})")
                .imports("java.nio.file.Path").build();

        @Override
        public J visitTry(J.Try tr, ExecutionContext executionContext) {
            J result = super.visitTry(tr, executionContext);

            if (getCursor().getMessage(PATH_PARAM) != null) {
                maybeRemoveImport("java.io.File");
                maybeAddImport("java.nio.file.Path");
                J.MethodInvocation mi = getCursor().getMessage(IS_EMPTY_PARAM);
                List<Expression> pathParam = getCursor().getMessage(PATH_PARAM);
                J.MethodInvocation newMi = template.apply(getCursor(), tr.getCoordinates().replace(), pathParam.getFirst());
                newMi = newMi.withPrefix(Space.EMPTY);
                mi = mi.withPrefix(tr.getPrefix());
                return mi.withArguments(List.of(newMi));
            }
            return result;
        }

        @Override
        public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext executionContext) {
            super.visitMethodInvocation(method, executionContext);
            if (isEmptyInvocaMatcher.matches(method)) {
                getCursor().putMessageOnFirstEnclosing(J.Try.class, IS_EMPTY_PARAM, method.getArguments().getFirst());
            }
            return method;
        }

        @Override
        public J visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
            multiVariable = (J.VariableDeclarations) super.visitVariableDeclarations(multiVariable, executionContext);
            multiVariable.getVariables().stream()
                    .map(J.VariableDeclarations.NamedVariable::getInitializer)
                    .filter(exp -> newFileMatcher.matches(exp))
                    .map(exp -> (J.NewClass) exp)
                    .findFirst()
                    .ifPresent(newClass -> getCursor().putMessageOnFirstEnclosing(J.Try.class, PATH_PARAM, newClass.getArguments()));
            return multiVariable;
        }
    }
}

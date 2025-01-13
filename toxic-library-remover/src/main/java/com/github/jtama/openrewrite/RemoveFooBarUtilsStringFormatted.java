/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jtama.openrewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Space;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;

import java.util.List;

public class RemoveFooBarUtilsStringFormatted extends Recipe {

    @Override
    public String getDisplayName() {
        return "Remove `FooBarUtils.stringFormatted`";
    }

    @Override
    public String getDescription() {
        return "Replace any usage of `FooBarUtils.stringFormatted` with `String.formatted` method.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new Preconditions.Check(
                new UsesType<>("com.github.jtama.toxic.FooBarUtils", true),
                new ToStringFormattedVisitor());
    }


    private static class ToStringFormattedVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final MethodMatcher toxicStringFormatted = new MethodMatcher("com.github.jtama.toxic.FooBarUtils stringFormatted(String,..)");
        private JavaTemplate stringFormatted = JavaTemplate.builder("#{any(java.lang.String)}.formatted()").build();

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation methodInvocation = super.visitMethodInvocation(method, ctx);
            if (!toxicStringFormatted.matches(methodInvocation)) {
                return methodInvocation;
            }
            maybeRemoveImport("com.github.jtama.toxic.FooBarUtils");
            List<Expression> arguments = methodInvocation.getArguments();
            J.MethodInvocation mi = stringFormatted.apply(
                    getCursor(),
                    methodInvocation.getCoordinates().replace(),
                    arguments.get(0));
            mi = mi.withArguments(ListUtils.mapFirst(
                    arguments.subList(1, arguments.size()),
                    expression -> expression.withPrefix(Space.EMPTY)));
            mi.withComments(List.of(new TextComment(false, "You should realy learn java",mi.getPrefix().getWhitespace(), Markers.EMPTY)));
            return mi;
        }
    }


}

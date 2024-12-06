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

import org.openrewrite.*;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JLeftPadded;
import org.openrewrite.marker.Markers;

import static java.util.Collections.emptyList;
import static org.openrewrite.Tree.randomId;
import static org.openrewrite.java.tree.Space.EMPTY;
import static org.openrewrite.java.tree.Space.SINGLE_SPACE;

public class ChangeAnnotationAttributeValue extends Recipe {

    @Option(displayName = "Annotation Type",
            description = "The fully qualified name of the annotation.",
            example = "org.eclipse.microprofile.config.inject.ConfigProperty")
    String annotationType;

    @Option(displayName = "Attribute name",
            description = "The name of attribute to change. Can be null to represent unnamed attribute.",
            example = "name")
    String attributeName;

    @Option(displayName = "Old attribute value",
            description = "The old value used for attribute.",
            example = "property.old")
    String oldAttributeValue;

    @Option(displayName = "New attribute value",
            description = "The new value to use for attribute.",
            example = "property.key")
    String newAttributeValue;

    public ChangeAnnotationAttributeValue(String annotationType, String attributeName, String oldAttributeValue, String newAttributeValue) {
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.oldAttributeValue = oldAttributeValue;
        this.newAttributeValue = newAttributeValue;
    }

    @Override
    public String getDisplayName() {
        return "Change annotation attribute value";
    }

    @Override
    public String getInstanceNameSuffix() {
        String shortType = annotationType.substring(annotationType.lastIndexOf('.') + 1);
        return String.format("`@%s(%s=%s)` to `@%s(%s=%s)`",
                shortType, attributeName, oldAttributeValue,
                shortType, attributeName, newAttributeValue);
    }

    @Override
    public String getDescription() {
        return "Some annotations accept arguments. This recipe updates existing attribute value.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new UsesType<>(annotationType, false), new JavaIsoVisitor<>() {
            private final AnnotationMatcher annotationMatcher = new AnnotationMatcher('@' + annotationType + "( " + attributeName + " = \"" + oldAttributeValue + "\" )");

            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext ctx) {
                J.Annotation a = super.visitAnnotation(annotation, ctx);
                if (!annotationMatcher.matches(a)) {
                    return a;
                }
                return a.withArguments(ListUtils.map(a.getArguments(), arg -> {
                    if (arg instanceof J.Assignment) {
                        J.Assignment assignment = (J.Assignment) arg;
                        J.Literal actualValue = (J.Literal) assignment.getAssignment();

                        if (actualValue.getValue().equals(oldAttributeValue)) {
                            J.Literal newLiteral = new J.Literal(randomId(), actualValue.getPrefix(), Markers.EMPTY, newAttributeValue, "\"" + newAttributeValue+ "\"", actualValue.getUnicodeEscapes(), actualValue.getType());
                            return new J.Assignment(randomId(), assignment.getPrefix(), assignment.getMarkers(), assignment.getVariable(), new JLeftPadded<>(SINGLE_SPACE, newLiteral, Markers.EMPTY), assignment.getType());
                        }
                    } else if (attributeName.equals("value")) {
                        J.Identifier name = new J.Identifier(randomId(), arg.getPrefix(), Markers.EMPTY, emptyList(), newAttributeValue, arg.getType(), null);
                        return new J.Assignment(randomId(), EMPTY, arg.getMarkers(), name, new JLeftPadded<>(SINGLE_SPACE, arg.withPrefix(SINGLE_SPACE), Markers.EMPTY), arg.getType());
                    }
                    return arg;
                }));
            }
        });
    }
}

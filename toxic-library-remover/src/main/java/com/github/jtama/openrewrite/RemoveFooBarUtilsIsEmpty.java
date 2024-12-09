package com.github.jtama.openrewrite;

import com.github.jtama.toxic.FooBarUtils;
import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import org.openrewrite.java.template.RecipeDescriptor;

import java.util.List;

@RecipeDescriptor(
        name = "Remove `FooBarUtils.isEmpty` methodes usages",
        description = "Replace any usage of `FooBarUtils.isEMpty` method by standards equivalent.")
public class RemoveFooBarUtilsIsEmpty {

    @RecipeDescriptor(
            name = "Replace `FooBarUtils.isEmptyString(String)` with standard equivalent",
            description = "Replace `FooBarUtils.isEmptyString(String)` with ternary 'value == null || value.isEmpty()'."
    )
    public static class RemoveStringIsEmpty {

        @BeforeTemplate
        boolean before(String value) {
            return FooBarUtils.isEmpty(value);
        }

        @AfterTemplate
        boolean after(String value) {
            return value == null || value.isEmpty();
        }

    }


    @RecipeDescriptor(
            name = "Replace `FooBarUtils.isEmptyList(List)` with standard equivalent",
            description = "Replace `FooBarUtils.isEmptyList(List)` with ternary 'value == null || value.isEmpty()'."
    )
    public static class RemoveListIsEmpty {

        @BeforeTemplate
        public boolean before(List value) {
            return FooBarUtils.isEmpty(value);
        }

        @AfterTemplate
        public boolean after(List value) {
            return value == null || value.isEmpty();
        }

    }

}

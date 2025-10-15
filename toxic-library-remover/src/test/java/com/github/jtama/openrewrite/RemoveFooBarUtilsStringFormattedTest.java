package com.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveFooBarUtilsStringFormattedTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveFooBarUtilsStringFormatted())
          .parser(JavaParser.fromJavaVersion()
              .logCompilationWarningsAndErrors(true)
              .classpath("toxic-library")
          );
    }

    @DocumentExample
    @Test
    void removeStringFormattedInvocation() {
        rewriteRun(
          //language=java
          java(
                """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class Karaba {
              
                  public String foo() {
                      return new FooBarUtils().stringFormatted("Hello %s %s %s", 2L,
                         "tutu" +
                         "tata",
                         this.getClass()
                              .getName());
                  }
              }
              """,
            """
              public class Karaba {
             
                  public String foo() {
                      return "Hello %s %s %s".formatted(2L,
                         "tutu" +
                         "tata",
                         this.getClass()
                              .getName());
                  }
              }
              """));
    }

}

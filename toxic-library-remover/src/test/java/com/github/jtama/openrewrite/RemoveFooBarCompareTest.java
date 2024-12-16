package com.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveFooBarCompareTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new UseObjectsCompare())
          .parser(JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("toxic-library"));
    }

    @DocumentExample
    @Test
    void removeAllIsEmptyInvocation() {
        rewriteRun(
          //language=java
          java(
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class ManualGearCar {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void virage(String param) {
                      UTILS.compare(param, 
                      "au frein à main ?", 
                             (o1,o2) -> o1.compareTo(o2));
                  }
              }
              """,
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              import java.util.Objects;
              
              public class ManualGearCar {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void virage(String param) {
                      //Comparing java.lang.String using java.util.Comparator<java.lang.String>
                      Objects.compare(param, "au frein à main ?", (o1, o2) -> o1.compareTo(o2));
                  }
              }
              """));
    }
}

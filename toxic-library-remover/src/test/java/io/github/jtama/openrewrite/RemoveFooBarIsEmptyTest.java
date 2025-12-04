package io.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveFooBarIsEmptyTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveFooBarUtilsIsEmptyRecipes())
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
          import static com.github.jtama.toxic.FooBarUtils.isEmpty;
          
          import java.util.List;
          
          public class ForTestingPurpose {
          
              public void virage(String param) {
                  System.out.printf("On le prend ce virage ? %s", isEmpty(param));
              }
              
              public void auFreinAMain(List<String> values) {
                  System.out.printf("Au frein à main ? %s", isEmpty(values));
              }
          }
          """,
            """
              import java.util.List;
              
              public class ForTestingPurpose {
              
                  public void virage(String param) {
                      System.out.printf("On le prend ce virage ? %s", param == null || param.isEmpty());
                  }
                  
                  public void auFreinAMain(List<String> values) {
                      System.out.printf("Au frein à main ? %s", values == null || values.isEmpty());
                  }
              }
              """));
    }

    @Test
    void removeIEmptyInvocation() {
        rewriteRun(
          spec -> spec.recipe(new RemoveFooBarUtilsIsEmptyRecipes.RemoveListIsEmptyRecipe()),
          //language=java
          java(
                """
              import static com.github.jtama.toxic.FooBarUtils.isEmpty;
              
              import java.util.List;
              
              public class AutomaticCar {
              
                  public void virage(String param) {
                      System.out.printf("On le prend ce virage ? %s", isEmpty(param));
                  }
                  
                  public void auFreinAMain(List<String> values) {
                      System.out.printf("Au frein à main ? %s", isEmpty(values));
                  }
              }
              """,
            """
              import static com.github.jtama.toxic.FooBarUtils.isEmpty;
              
              import java.util.List;
              
              public class AutomaticCar {
              
                  public void virage(String param) {
                      System.out.printf("On le prend ce virage ? %s", isEmpty(param));
                  }
                  
                  public void auFreinAMain(List<String> values) {
                      System.out.printf("Au frein à main ? %s", values == null || values.isEmpty());
                  }
              }
              """));
    }

}

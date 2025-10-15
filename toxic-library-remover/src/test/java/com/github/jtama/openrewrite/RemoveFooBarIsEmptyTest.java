package com.github.jtama.openrewrite;

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
          
          public class MickeyTheSorcerersApprentice {
          
              public void abracadabra(String param) {
                  System.out.printf("On le prend ce tapis volant ? %s", isEmpty(param));
              }
              
              public void shazaam(List<String> values) {
                  System.out.printf("Et ce nimbus 2000 ? %s", isEmpty(values));
              }
          }
          """,
            """
              import java.util.List;
              
              public class MickeyTheSorcerersApprentice {
              
                  public void abracadabra(String param) {
                      System.out.printf("On le prend ce tapis volant ? %s", param == null || param.isEmpty());
                  }
                  
                  public void shazaam(List<String> values) {
                      System.out.printf("Et ce nimbus 2000 ? %s", values == null || values.isEmpty());
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
              
              public class MickeyTheSorcerersApprentice {
              
                  public void abracadabra(String param) {
                      System.out.printf("On le prend ce virage ? %s", isEmpty(param));
                  }
                  
                  public void shazaam(List<String> values) {
                      System.out.printf("Au frein à main ? %s", isEmpty(values));
                  }
              }
              """,
            """
              import static com.github.jtama.toxic.FooBarUtils.isEmpty;
              
              import java.util.List;
              
              public class MickeyTheSorcerersApprentice {
              
                  public void abracadabra(String param) {
                      System.out.printf("On le prend ce virage ? %s", isEmpty(param));
                  }
                  
                  public void shazaam(List<String> values) {
                      System.out.printf("Au frein à main ? %s", values == null || values.isEmpty());
                  }
              }
              """));
    }

}

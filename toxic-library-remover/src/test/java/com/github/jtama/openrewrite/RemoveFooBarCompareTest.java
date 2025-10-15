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
    void replaceAndCommentDeclaration() {
        rewriteRun(
          //language=java
          java(
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      int val = UTILS.compare(param, 
                      "en tapis volant ?", 
                             (o1,o2) -> o1.compareTo(o2));
                  }
              }
              """,
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              import java.util.Objects;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      //Comparing java.lang.String using java.util.Comparator<java.lang.String>
                      int val = Objects.compare(param, "en tapis volant ?", (o1, o2) -> o1.compareTo(o2));
                  }
              }
              """));
    }

    @Test
    void replaceAndCommentAssignment() {
        rewriteRun(
          //language=java
          java(
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      int val;
                      val = UTILS.compare(param, 
                      "au frein à main ?", 
                             (o1,o2) -> o1.compareTo(o2));
                  }
              }
              """,
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              import java.util.Objects;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      int val;
                      //Comparing java.lang.String using java.util.Comparator<java.lang.String>
                      val = Objects.compare(param, "au frein à main ?", (o1, o2) -> o1.compareTo(o2));
                  }
              }
              """));
    }

    @Test
    void replaceAndCommentReturn() {
        rewriteRun(
          //language=java
          java(
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public int depart(String param) {
                      return UTILS.compare(param, 
                      "au frein à main ?", 
                             (o1,o2) -> o1.compareTo(o2));
                  }
              }
              """,
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              import java.util.Objects;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public int depart(String param) {
                      //Comparing java.lang.String using java.util.Comparator<java.lang.String>
                      return Objects.compare(param, "au frein à main ?", (o1, o2) -> o1.compareTo(o2));
                  }
              }
              """));
    }

    @Test
    void replaceAndCommentInvocations() {
        rewriteRun(
          //language=java
          java(
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      UTILS.compare(param, 
                      "au frein à main ?", 
                             (o1,o2) -> o1.compareTo(o2));
                  }
              }
              """,
            """
              import com.github.jtama.toxic.FooBarUtils;
              
              import java.util.Objects;
              
              public class Nim {
                  private static final FooBarUtils UTILS = new FooBarUtils();
              
                  public void depart(String param) {
                      //Comparing java.lang.String using java.util.Comparator<java.lang.String>
                      Objects.compare(param, "au frein à main ?", (o1, o2) -> o1.compareTo(o2));
                  }
              }
              """));
    }
}

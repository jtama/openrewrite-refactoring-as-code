package com.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.marker.JavaProject;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.util.List;
import java.util.UUID;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;

class ProjectAerialViewGeneratorTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ProjectAerialViewGenerator());
    }

    @Test
    void shouldAutoDetectGroupIdAndCreateLink() {
        rewriteRun(
          mavenProject(
            "com.mycompany.app:my-app:1",
            spec -> spec.markers(
                new JavaProject(
                  UUID.randomUUID(),
                  "NoUseForAName",
                  new JavaProject.Publication("com.mycompany.app", "my-app", "1"))),
            //language=java
            java(
              """
                package com.mycompany.app;
                
                public class A {
                    private String test = B.YOO;
                }
                """
            ),
            //language=java
            java(
              """
                package com.mycompany.app;
                
                public class B {
                    public static String YOO = "yoo";
                }
                """
            ))
        );
    }

    @Test
    void shouldCreateLinkForManualBasePackages() {
        rewriteRun(
          spec -> spec.recipe(new ProjectAerialViewGenerator(null, List.of("com.mycompany.lib"))),
          //language=java
          java(
            """
              package com.mycompany.lib;
              
              public class C {
              }
              """
          ),
          //language=java
          java(
            """
              package com.mycompany.app;
              
              import com.mycompany.lib.C;
              
              public class D {
                  private C c;
              }
              """
          )
        );
    }

    @Test
    void shouldNotCreateLinkWhenNoMatch() {
        rewriteRun(
          spec -> spec.recipe(new ProjectAerialViewGenerator(null, List.of("com.another.lib"))),
          //language=java
          java(
            """
              package com.mycompany.lib;
              
              public class E {
              }
              """
          ),
          //language=java
          java(
            """
              package com.mycompany.app;
              
              import com.mycompany.lib.E;
              
              public class F {
                  private E e;
              }
              """
          )
        );
    }
}
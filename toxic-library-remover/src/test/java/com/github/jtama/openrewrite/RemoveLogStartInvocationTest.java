package com.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveLogStartInvocationTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveLogStartInvocations())
                .parser(JavaParser.fromJavaVersion()
                        .logCompilationWarningsAndErrors(true)
                        .classpath("toxic-library"))
          .cycles(3);

    }

    @DocumentExample
    @Test
    void removeLogStartInvocations() {
        rewriteRun(
                //language=java
                java(
                        """
                                import com.github.jtama.toxic.Timer;
                                
                                public class Maleficent {
                                
                                    @Deprecated
                                    public void randomMagic(String param) {
                                        Timer.logStart();
                                        System.out.println("A very long process");
                                        Timer.logEnd();
                                    }
                                
                                    public void poo(Boolean param) {
                                        // do nothing
                                    }
                                }
                                """,
                        """
                                import io.micrometer.core.annotation.Timed;
                                
                                public class Maleficent {
                                
                                    @Deprecated
                                    @Timed
                                    public void randomMagic(String param) {
                                        System.out.println("A very long process");
                                    }
                                
                                    public void poo(Boolean param) {
                                        // do nothing
                                    }
                                }
                                """));
    }
}

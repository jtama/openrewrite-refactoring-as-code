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
                        .classpath("toxic-library", "quarkus-micrometer-registry-prometheus"))
                .cycles(3);

    }

    @DocumentExample
    @Test
    void removeLogStartInvocations() {
        rewriteRun(
                //language=java
                java(
                        """
                                import com.github.jtama.toxic.FooBarUtils;
                                
                                public class ManualGearCar {
                                
                                    @Deprecated
                                    public void drift(String param) {
                                        FooBarUtils.logStart();
                                    }
                                
                                    public void hardBreak(Boolean param) {
                                        // do nothing
                                    }
                                }
                                """,
                        """
                                import io.micrometer.core.annotation.Timed;
                                
                                public class ManualGearCar {
                                
                                    @Deprecated
                                    @Timed
                                    public void drift(String param) {
                                    }
                                
                                    public void hardBreak(Boolean param) {
                                        // do nothing
                                    }
                                }
                                """));
    }
}

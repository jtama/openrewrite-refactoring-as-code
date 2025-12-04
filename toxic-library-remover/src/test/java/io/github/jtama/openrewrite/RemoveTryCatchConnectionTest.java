package io.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveTryCatchConnectionTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveTryCatchConnection())
                .parser(JavaParser.fromJavaVersion()
                        .logCompilationWarningsAndErrors(true)
                  .classpath("toxic-library"));

    }

    @DocumentExample
    @Test
    void removeLogStartInvocations() {
        rewriteRun(
                //language=java
                java(
                        """
                                import com.github.jtama.toxic.FooBarUtils;
                                import java.io.File;
                                import java.io.FileReader;
                                import java.nio.file.Files;
                                
                                public class ManualGearCar {
                                
                                    @Deprecated
                                    public void drift(String param) {
                                        try (File file = new File("Tutu.md")) {
                                            String test = FooBarUtils.isEmpty(Files.readString(file.toPath()));
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }   
                                    }
                                    
                                    public void doSomething(String param) {
                                        // To something
                                    }
                                }
                                """,
                        """
                                import com.github.jtama.toxic.FooBarUtils;
                                import java.io.FileReader;
                                import java.nio.file.Files;
                                import java.nio.file.Path;
                                
                                public class ManualGearCar {
                                
                                    @Deprecated
                                    public void drift(String param) {
                                        Files.readString(Path.of("Tutu.md"));
                                    }
                                    
                                    public void doSomething(String param) {
                                        // To something
                                    }
                                }
                                """));
    }
}

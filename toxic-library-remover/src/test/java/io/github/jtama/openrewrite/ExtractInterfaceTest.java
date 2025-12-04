package io.github.jtama.openrewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class ExtractInterfaceTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ExtractInterface("com.github.jtama.toxic.LearnToFly"))
          .parser(JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("toxic-library"))
          .cycles(3);

    }

    @DocumentExample
    @Test
    void shouldExtractInterfaceParameterized() {
        rewriteRun(
          //language=java
          java(
            """
                        package tutu;
              
                        import com.github.jtama.toxic.FooBarUtils;
                        import com.github.jtama.toxic.LearnToFly;
                        import java.io.File;
                        import java.nio.file.Files;
              
                        @LearnToFly
                        public class ManualGearCar implements Comparable<Integer> {

                            protected Integer tutu;
              
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
              
                            @Override 
                            public int compareTo(Integer o) {
                                return 0;
                            }
                        }
              """,
            """
              package tutu;
              
              import com.github.jtama.toxic.FooBarUtils;
              import java.io.File;
              import java.nio.file.Files;
              
              public class ManualGearCar implements IManualGearCar {

                  protected Integer tutu;
              
                  @Override
                  public void drift(String param) {
                      try (File file = new File("Tutu.md")) {
                          String test = FooBarUtils.isEmpty(Files.readString(file.toPath()));
                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                  }
              
                  @Override
                  public void doSomething(String param) {
                      // To something
                  }

                  @Override 
                  public int compareTo(Integer o) {
                      return 0;
                  }
              }
              """),
          //language=java
          java(null, """
              package tutu;
              
              import com.github.jtama.toxic.LearnToFly;
              
              @LearnToFly
              public interface IManualGearCar extends Comparable<Integer> {
              
                  @Deprecated void drift(String param);
              
                  void doSomething(String param);

                  @Override int compareTo(Integer o);
              }
              """
          )
        );
    }

    @DocumentExample
    @Test
    void shouldExtractInterfaceType() {
        rewriteRun(
          //language=java
          java(
            """
              package tutu;
              
              import com.github.jtama.toxic.FooBarUtils;
              import com.github.jtama.toxic.LearnToFly;
              import java.io.File;
              import java.nio.file.Files;
              
              @LearnToFly
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
              package tutu;
              
              import com.github.jtama.toxic.FooBarUtils;
              import java.io.File;
              import java.nio.file.Files;
              
              public class ManualGearCar implements IManualGearCar {
              
                  @Override
                  public void drift(String param) {
                      try (File file = new File("Tutu.md")) {
                          String test = FooBarUtils.isEmpty(Files.readString(file.toPath()));
                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                  }
              
                  @Override
                  public void doSomething(String param) {
                      // To something
                  }
              }
              """),
          //language=java
          java(null, """
              package tutu;
              
              import com.github.jtama.toxic.LearnToFly;
              
              @LearnToFly
              public interface IManualGearCar {
              
                  @Deprecated void drift(String param);
              
                  void doSomething(String param);
              }
              """
          )
        );
    }

    @DocumentExample
    @Test
    void shouldNotExtractInterface() {
        rewriteRun(
          //language=java
          java(
            """
              package tutu;
              
              import com.github.jtama.toxic.FooBarUtils;
              import java.io.File;
              import java.nio.file.Files;
              
              public class ManualGearCur {
              
                  @Deprecated
                  public void druft(String param) {
                      try (File file = new File("Tutu.md")) {
                          String test = FooBarUtils.isEmpty(Files.readString(file.toPath()));
                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                  }
              
                  public void doSumething(String param) {
                      // To something
                  }
              }
              """)
          );
    }
}

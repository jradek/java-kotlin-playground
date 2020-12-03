package sample;

import java.util.List;
import java.util.Optional;

public class Foo {
  private String name = "";

  public Foo() {
    this("_unnamed_");
  }

  public Foo(String n) {
    name = n;
  }

  public Optional<Integer> getSome(int i) {
    return i > 0 ? Optional.of(i) : Optional.empty();
  }

  public List<String> getNames() {
    return List.of("Alice", "Bob", "Carol", "David", name);
  }

  public String getMessage() {
    return "I'm an instance of Java Foo, named " + name;
  }
}

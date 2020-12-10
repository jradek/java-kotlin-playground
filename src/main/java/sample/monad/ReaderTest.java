package sample.monad;

public class ReaderTest {

  static class Context {
    public final String name;
    public final UseCase useCase;

    Context(String name, UseCase uc) {
      this.name = name;
      this.useCase = uc;
    }
  }

  static class UseCase {
    Reader<Context, Integer> getValue() {
      return Reader.<Context>ask()
          .flatMap(ctx -> {
            System.out.println("Use case " + ctx.name);
            return Reader.pure(ctx.name.length());
          });
    }
  }

  static class Presenter {
    Reader<Context, Object> getSuperHeroes() {
      return Reader.<Context>ask()
          .flatMap(ctx -> {
                System.out.println("The name is " + ctx.name);

                return ctx.useCase.getValue()
                    .map(v -> {
                      System.out.println("Use case value is: " + v);
                      return Reader.pure(null);
                    });
          });
    }
  }

  static Reader<Context, Object> p() {
    return Reader.<Context>ask()
        .flatMap(ctx -> {
          System.out.println("<p arg='" + ctx.name + "'>");
          return Reader.pure(null);
        });
  }

  static Reader<Context, Object> nav() {
    System.out.println("<nav>");
    return p();
  }

  static Reader<Context, Object> html() {
    return Reader.<Context>ask()
        .flatMap(ctx -> {
          System.out.println("<html arg='" + ctx.name + "'>");
          return nav();
        });
  }


  public static void run() {

    System.out.println("-------------------------------");

    // Advantage: the context is never required as a function Argument,
    // neither for Presenter nor for UseCase!
    new Presenter().getSuperHeroes()
        .apply(new Context("Michael", new UseCase()));

    html().apply(new Context("heino", null));
  }

}

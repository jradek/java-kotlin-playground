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
    System.out.println("<nav><!-- not interested in context --></nav>");
    return p();
  }

  static Reader<Context, Object> html() {
    return Reader.<Context>ask()
        .flatMap(ctx -> {
          System.out.println("<html arg='" + ctx.name + "'>");
          return nav();
        });
  }

  static class Logger {
    private String prefix = "Logger: ";
    public Logger(String p) {
      prefix = p;
    }

    public void accept(String s) {
      System.out.println(prefix + s);
    }
  }

  static class Account {

    private Logger logger = null;
    private String owner;
    private double balance;

    public Account open(String owner) {
      this.owner = owner;
      logger.accept("opened account for " + owner);
      return this;
    }

    public Account credit(double amount) {
      this.balance += amount;
      logger.accept("credit " + amount + " for " + owner);
      return this;
    }

    public Account debit(double amount) {
      this.balance -= amount;
      logger.accept("debit " + amount + " from " + owner);
      return this;
    }

    public void setLogger(Logger l) {
      logger = l;
    }
  }


  public static void run() {

    System.out.println("-------------------------------");

    // Advantage: the context is never required as a function Argument,
    // neither for Presenter nor for UseCase!
    new Presenter().getSuperHeroes()
        .apply(new Context("Michael", new UseCase()));

    html().apply(new Context("heino", null));

//    Account account = new Account();
//    account.open("Alice")
//        .credit(200.0)
//        .debit(100);

    // lift the 'logger' "dependency" into the account
    Account firstAccount = new Account();
    Reader<Logger, Account> reader = Reader.lift(firstAccount, (a, l) -> a.setLogger(l));
    reader.map(a -> a.open("Alice"))
        .map(a -> a.credit(200.0))
        .map(a -> a.debit(100))
        .flatMap(a -> {
            a.debit(10);
            return Reader.ask();
        })
        .map(l -> {
          l.accept("--- switching accounts ---");
          return l;
        })
        .flatMap(a -> Reader.lift(new Account(), (acc, l) -> acc.setLogger(l)))
        .map(a -> a.open("Bob"))
        .map(a -> a.credit(666))
        .flatMap(a -> Reader.ask())
        .map(l -> {
          l.accept("--- switching accounts back ---");
          return firstAccount;
        })
        .map(a -> a.debit(333))
        .apply(new Logger("The Logger: "));
  }

}

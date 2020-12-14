package sample.monad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.Test;

/**
 * Dependency Injection (DI)
 */
public class ReaderDITest {

  // Extension by Mario Fusco
  // https://www.youtube.com/watch?v=84MfG4tp30s
  public static <CTX, A> Reader<CTX, A> lift(A obj, BiConsumer<A, CTX> injector) {
    return Reader.of(ctx -> {
      injector.accept(obj, ctx);
      return obj;
    });
  }

  static class Logger {
    public final Map<String, Integer> debits = new HashMap<>();
    public final Map<String, Integer> credits = new HashMap<>();

    public void debit(Account a, double amount) {
      debits.computeIfPresent(a.getOwner(), (name, val) -> val + 1);
      debits.putIfAbsent(a.getOwner(), 1);
      System.out.printf("Debit %f from %s\n", amount, a.getOwner());
    }

    public void credit(Account a, double amount) {
      credits.computeIfPresent(a.getOwner(), (name, val) -> val + 1);
      credits.putIfAbsent(a.getOwner(), 1);
      System.out.printf("Credit %f to %s\n", amount, a.getOwner());
    }
  }

  static class Account {

    private Logger logger = null;
    private String owner;
    private double balance;

    public Account open(String owner) {
      this.owner = owner;
      return this;
    }

    public String getOwner() {
      return owner;
    }

    public Account credit(double amount) {
      this.balance += amount;
      logger.credit(this, amount);
      return this;
    }

    public Account debit(double amount) {
      this.balance -= amount;
      logger.debit(this, amount);
      return this;
    }

    public void setLogger(Logger l) {
      logger = l;
    }
  }

  @Test(expected = NullPointerException.class)
  public void noLoggerInjected() {
    Reader<Logger, Logger> reader = Reader.ask();

    reader.map(_unused -> new Account())
        .map(a -> a.open("Alice"))
        .map(a -> a.credit(100)) // logger was never injected
        .apply(new Logger());
  }


  @Test
  public void multipleAccounts() {
    assert("ä".length() == 1);

    Account firstAccount = new Account();
    Logger logger = new Logger();
    Reader<Logger, Account> reader = lift(firstAccount, (a, l) -> a.setLogger(l));
    reader.map(a -> a.open("Alice"))
        .map(a -> a.credit(200.0))
        .map(a -> a.debit(100))
        .flatMap(a -> {
          a.debit(10);
          return Reader.ask();
        })
        .map(l -> {
          System.out.println("--- switching accounts ---");
          return l;
        })
        .flatMap(a -> lift(new Account(), (acc, l) -> acc.setLogger(l)))
        .map(a -> a.open("Bob"))
        .map(a -> a.credit(666))
        .flatMap(a -> Reader.ask())
        .map(l -> {
          System.out.println("--- switching accounts back ---");
          return firstAccount;
        })
        .map(a -> a.debit(333))
        .apply(logger);

    assertThat(logger.credits.size(), equalTo(2));
    assertThat(logger.debits.size(), equalTo(1));

    assertThat(logger.credits.get("Alice"), equalTo(1));
    assertThat(logger.debits.get("Alice"), equalTo(3));

    assertThat(logger.credits.get("Bob"), equalTo(1));
  }
}

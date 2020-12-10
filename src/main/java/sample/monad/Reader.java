package sample.monad;

import java.util.function.Function;

/**
 * The Reader monad allows a common context (ie. DAO object) to be threaded through to chained functions.
 *
 * A common use for this type of monad is dependency injection.
 *
 * See: https://medium.com/@johnmcclean/dependency-injection-using-the-reader-monad-in-java8-9056d9501c75
 */
public class Reader<R, A> {
  private Function<R, A> m_func;

  private Reader(Function<R, A> f) {
    m_func = f;
  }

  /**
   * The ask method for the Reader allows to flatMap over the context itself without
   * the need for a Reader instance
   *
   * @param <R> the context
   * @return Constructed reader
   */
  static <R> Reader<R, R> ask() {
    return of(Function.identity());
  }

  public static <R, A> Reader<R, A> of(Function<R, A> f) {
    return new Reader<>(f);
  }

  public A apply(R ctx) {
    return m_func.apply(ctx);
  }

  /**
   * Unit
   */
  static <R, A> Reader<R, A> pure(A a) {
    return new Reader<>(ctx -> a);
  }

  <B> Reader<R, B> map(Function<? super A, ? extends B> f) {
    return new Reader<>(m_func.andThen(f));
  }

  <B> Reader<R, B> flatMap(Function<? super A, Reader<R, ? extends B>> f) {
//    Working out the types
//
//    R ctx = null;
//    Function<R, Reader<R, ? extends B>> appliedFunc = m_func.andThen(f);
//    Reader<R, ? extends B> reader = appliedFunc.apply(ctx);
//    Function<R, B> newReaderFunc = ctx2 -> reader.apply(ctx2);
//    Reader<R, B> result = Reader.of(newReaderFunc);
//    return result;

    return new Reader<>(ctx -> m_func.andThen(f).apply(ctx).apply(ctx));
  }
}

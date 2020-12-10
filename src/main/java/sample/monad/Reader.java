package sample.monad;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The Reader monad allows a common context (ie. DAO object) to be threaded through to chained functions.
 *
 * A common use for this type of monad is dependency injection.
 *
 * See: https://medium.com/@johnmcclean/dependency-injection-using-the-reader-monad-in-java8-9056d9501c75
 */
public class Reader<CTX, A> {
  private final Function<CTX, A> m_func;

  private Reader(Function<CTX, A> f) {
    m_func = f;
  }

  /**
   * The ask method for the Reader allows to flatMap over the context itself without
   * the need for a Reader instance
   *
   * @param <CTX> the context
   * @return Constructed reader
   */
  static <CTX> Reader<CTX, CTX> ask() {
    return of(Function.identity());
  }

  public static <R, A> Reader<R, A> of(Function<R, A> f) {
    return new Reader<>(f);
  }

  public A apply(CTX ctx) {
    return m_func.apply(ctx);
  }

  /**
   * Unit
   */
  static <CTX, A> Reader<CTX, A> pure(A a) {
    return new Reader<>(ctx -> a);
  }

  <B> Reader<CTX, B> map(Function<? super A, ? extends B> f) {
//    Working out the types
//
//    Function<CTX, B> newReaderFunc = m_func.andThen(f);
//    Reader<CTX, B> result = Reader.of(newReaderFunc);
//    return result;

    return new Reader<>(m_func.andThen(f));
  }

  <B> Reader<CTX, B> flatMap(Function<? super A, Reader<CTX, ? extends B>> f) {
//    Working out the types
//
//    Function<CTX, Reader<CTX, ? extends B>> appliedFunc = m_func.andThen(f);
//    CTX ctx = null;
//    Reader<CTX, ? extends B> reader = appliedFunc.apply(ctx);
//    Function<CTX, B> newReaderFunc = ctx2 -> reader.apply(ctx2);
//    Reader<CTX, B> result = Reader.of(newReaderFunc);
//    return result;

    return new Reader<>((CTX ctx) -> m_func.andThen(f).apply(ctx).apply(ctx));
  }

  // Extension by Mario Fusco
  // https://www.youtube.com/watch?v=84MfG4tp30s
  public static <CTX, A> Reader<CTX, A> lift(A obj, BiConsumer<A, CTX> injector) {
    return Reader.of(ctx -> {
      injector.accept(obj, ctx);
      return obj;
    });
  }
}

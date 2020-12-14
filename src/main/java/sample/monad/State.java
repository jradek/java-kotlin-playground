package sample.monad;

import io.vavr.Tuple2;

import java.util.function.Function;

public class State<S, A> {
    private final Function<S, Tuple2<S, A>> m_func;

    private State(Function<S, Tuple2<S, A>> f) {
        this.m_func = f;
    }

    public static <S, A> State<S, A> of(Function<S, Tuple2<S, A>> f) {
        return new State<>(f);
    }

    public static <S, A> State<S, A> pure(A a) {
        return new State<>(s -> new Tuple2<>(s, a));
    }

    /**
     * The get method creates a function that simply returns the argument’s state both as the state and the value:
     *
     * @param <S>
     * @return
     */
    public static <S> State<S, S> get() {
        return of(s -> new Tuple2<>(s, s));
    }

    /**
     * The set method creates a function that returns the parameter’s state as the new state
     * and the Nothing singleton as the value
     *
     * @param s
     * @param <S>
     * @return
     */
    public static <S> State<S, Nothing> set(S s) {
        return of(x -> new Tuple2<>(s, Nothing.INSTANCE));
    }

    public static  <S, A> State<S, A> gets(Function<S, A> f) {
        return of(s -> new Tuple2<>(s, f.apply(s)));
    }

    /**
     * This method returns a State<S, Nothing> because it doesn’t return a value.
     * It allows to modify the state directly
     *
     * @param f state mutating function
     * @param <S>
     * @return new state
     */
    public static <S> State<S, Nothing> modify(Function<S, S> f) {
        return of(s -> new Tuple2<>(f.apply(s), Nothing.INSTANCE));
    }

    /**
     * modify state an keep value
     * @param f
     * @param value
     * @param <S>
     * @param <A>
     * @return
     */
    public static <S, A> State<S, A> modify(Function<S, S> f, A value) {
        return of(s -> new Tuple2<>(f.apply(s), value));
    }

    public Tuple2<S, A> run(S initial) {
        return m_func.apply(initial);
    }

    public <B> State<S, B> map(Function<? super A, ? extends B> f) {
        return of(s -> {
           Tuple2<S, A> mapped = run(s);
           return new Tuple2<>(mapped._1, f.apply(mapped._2));
        });
    }

    public <B> State<S, B> flatMap(Function<? super A, State<S, B>> f) {
        return of(s -> {
            Tuple2<S, A> mapped = run(s);
            return f.apply(mapped._2).run(mapped._1);
        });
    }
}

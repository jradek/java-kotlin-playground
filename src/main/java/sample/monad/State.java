package sample.monad;

import io.vavr.Tuple2;

import java.util.function.Function;

public class State<S, A> {
    private final Function<S, Tuple2<S, A>> m_function;

    private State(Function<S, Tuple2<S, A>> f) {
        this.m_function = f;
    }

    public static <S, A> State<S, A> of(Function<S, Tuple2<S, A>> f) {
        return new State<>(f);
    }

    static <S> State<S, S> get() {
        return of(s -> new Tuple2<>(s, s));
    }

    static <S, A> State<S, A> gets(Function<S, A> f) {
        return of(s -> new Tuple2<>(s, f.apply(s)));
    }

    static <S, A> State <S, A> modify(Function<S, S> f) {
        return of(s -> new Tuple2<>(f.apply(s), null));
    }

    public static <S, A> State<S, A> apply(Function<S, Tuple2<S, A>> f) {
        return new State<>(f);
    }

    public Tuple2<S, A> run(S initial) {
        return m_function.apply(initial);
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

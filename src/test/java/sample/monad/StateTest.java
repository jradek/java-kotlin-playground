package sample.monad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import io.vavr.Tuple;

import io.vavr.Tuple2;
import java.util.Random;
import org.junit.Test;

public class StateTest {

  @Test
  public void first() {
    State<Integer, Integer> s = State.gets(a -> a + 1);

    var res = s.run(0);

    assertThat(res._1, is(0));
    assertThat(res._2, is(1));
  }

  @Test
  public void second() {
    var s = State.<Integer, Integer>of(a -> Tuple.of(0,0))
        .map(val -> val + 2);

    var res = s.run(0);
    assertThat(res._1, is(0));
    assertThat(res._2, is(2));
  }

  @Test
  public void pure() {
    var res = State.<String, Integer>pure(666)
        .flatMap(i -> State.modify(y -> y + " " + i.toString(), 10))
        .map(i -> i * 2)
        .run("hi");

    assertThat(res._1, is("hi 666"));
    assertThat(res._2, is(20));
  }

  @Test
  public void chainedFunctions() {
    var res = State.<String>get()
        .flatMap(s -> State.set(s + " World!"))
        .flatMap(s -> State.modify(String::toUpperCase))
        .flatMap(s -> State.get())
        .map(String::length)
        .run("Hello");

    assertThat("state is upper cased", res._1, equalTo("HELLO WORLD!"));
    assertThat("number of characters in state", res._2, is(12));

    res = State.<String>get()
        .flatMap(s -> State.set(s + " World!"))
        .flatMap(s -> State.modify(_x -> "oops", 66))
        .run("Hello");

    assertThat("explicitly set", res._1, equalTo("oops"));
    assertThat("explicitly set", res._2, is(66));
  }

  static class RNG {
    private final Random gen;

    RNG(long seed) {
      gen = new Random(seed);
    }

    Tuple2<RNG, Integer> nextInt() {
      return Tuple.of(this, gen.nextInt());
    }
  }

  @Test
  public void random() {
    var s = State.<RNG, Integer>of(x -> x.nextInt());

    final int seed0Value = -1155484576;

    var res = s.run(new RNG(0));
    assertThat(res._2, is(seed0Value));

    final int seed1Value = -1155869325;
    res = s.run(new RNG(1));
    assertThat(res._2, is(seed1Value));

    // map example
    s = State.of(RNG::nextInt);
    res = s.map(i -> i + 10).run(new RNG(0));
    assertThat(res._2, is(seed0Value + 10));

    // modify RNG on the way
    res = s.flatMap(i -> State.set(new RNG(1)))
        .flatMap(nothing -> State.of(RNG::nextInt))
        .map(i -> i - 1)
        .run(new RNG(0));

    assertThat("Initial seed is 0, but changed to 1 later!", res._2, is(seed1Value - 1));
  }

  @Test
  public void randomNumberSequence() {
    var s = State.of(RNG::nextInt);

    var res = s.flatMap(x ->
        s.flatMap(y ->
            s.map(z -> Tuple.of(x,y,z))))
        .run(new RNG(0));

    final int seed0_1 = -1155484576;
    final int seed0_2 = -723955400;
    final int seed0_3 = 1033096058;

    assertThat("Three random numbers with seed 0", res._2._1, is(seed0_1));
    assertThat("Three random numbers with seed 0", res._2._2, is(seed0_2));
    assertThat("Three random numbers with seed 0", res._2._3, is(seed0_3));
  }
}

package sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class StreamTest {

  /**
   * Generate fibonacci sequence from 'unlimited' stream
   */
  @Test
  public void fibonacci() {
    var startValue = Tuple.of(0, 1);

    UnaryOperator<Tuple2<Integer, Integer>> f = p -> Tuple.of(p._2, p._1 + p._2);

    final var numValues = 10;
    var fiboSequence = Stream.iterate(startValue, f).map(t -> t._1).limit(numValues);
    var values = fiboSequence.collect(Collectors.toList());

    assertThat(values.size(), is(numValues));
    assertThat(values, contains(0, 1, 1, 2, 3, 5, 8, 13, 21, 34));
  }

}

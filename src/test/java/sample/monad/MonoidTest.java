package sample.monad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.junit.Test;

public class MonoidTest {

  /**
   * String is a monoid
   * <p>
   * null element: empty string mappend operation: string concat
   */
  @Test
  public void stringIsAMonoid() {
    var l = List.of("parts", "Of", "A", "Sentence");

    var res = l.stream().reduce("", String::concat);

    assertThat(res, equalTo("partsOfASentence"));
  }

  /**
   * List is a monoid
   * <p>
   * null element: empty list mappend operation: list append
   */
  @Test
  public void listIsAMonoid() {
    var elements = List.of(List.of(0, 1), List.of(2), List.of(3), List.of(4, 5, 6));
    List<Integer> res = elements.stream().reduce(
        new ArrayList<>(),
        (accu, l) -> {
          accu.addAll(l);
          return accu;
        });

    assertThat(res, contains(0, 1, 2, 3, 4, 5, 6));
  }

  /**
   * Function a -> a is a monoid
   * <p>
   * null element: identity mappend operation: function composition
   */
  @Test
  public void unaryOperatorIsAMonoid() {
    List<UnaryOperator<Integer>> fs = List.of(x -> x + 1, x -> x * 2);

    var res = fs.stream().reduce(
        UnaryOperator.identity(),
        (accu, f) -> x -> f.compose(accu).apply(x)
    );

    assertThat(res.apply(5), is((5 + 1) * 2));
  }

}

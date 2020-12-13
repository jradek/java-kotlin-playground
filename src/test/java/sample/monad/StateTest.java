package sample.monad;

import io.vavr.Tuple2;

import org.junit.Test;

public class StateTest {

  @Test
  public void simple() {
    State<Integer, Integer> s = State.get();

    Tuple2<Integer, Integer> res = s.map(v -> {
      System.out.println("MAP " + v);
      return v + 1;
    }).run(1);

    System.out.println(res);
  }

}

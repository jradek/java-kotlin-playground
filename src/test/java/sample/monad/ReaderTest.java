package sample.monad;


import org.junit.Test;

public class ReaderTest {

  @Test
  public void fails() {
    assert(true);
    assert("ä".length() == 1);
  }
}

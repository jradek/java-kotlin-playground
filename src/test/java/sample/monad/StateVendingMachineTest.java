package sample.monad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.Objects;
import org.junit.Test;

/**
 * see: https://github.com/functionaljava/functionaljava/blob/2d2efee47ac69eac6e2d4eb6b603131374e799c4/demo/src/main/java/fj/demo/StateDemo_VendingMachine.java
 */
public class StateVendingMachineTest {

  enum Input {COIN, TURN}

  /**
   * Vending machine
   * <p>
   * Inputs: coins and turns
   * <p>
   * Rules 1. when Machine is locked and coin is inserted, it becomes unlocked and coin is collected
   * 2. when machine is unlocked and a turn is made, it becomes locked and an item is released 3.
   * otherwise nothing happens
   */
  static class VendingMachine {

    private final boolean isLocked;
    private final int numItems;
    private final int numCoins;

    VendingMachine(boolean isLocked, int numItems, int numCoins) {
      this.isLocked = isLocked;
      this.numItems = numItems;
      this.numCoins = numCoins;
    }

    @Override
    public String toString() {
      return "VendingMachine{" +
          "isLocked=" + isLocked +
          ", numItems=" + numItems +
          ", numCoins=" + numCoins +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      VendingMachine that = (VendingMachine) o;
      return isLocked == that.isLocked && numItems == that.numItems && numCoins == that.numCoins;
    }

    @Override
    public int hashCode() {
      return Objects.hash(isLocked, numItems, numCoins);
    }

    final VendingMachine next(Input i) {
      System.out.printf("%s -> New intput %s\n", this, i);
      if (numItems == 0) {
        return this;
      } else if (i == Input.COIN) {
        if (!isLocked) {
          return this;
        }

        System.out.println("accept coin");
        return new VendingMachine(false, numItems, numCoins + 1);
      } else if (i == Input.TURN) {
        if (isLocked) {
          return this;
        }

        System.out.println("release item");
        return new VendingMachine(true, numItems - 1, numCoins);
      }

      return this;
    }
  }

  static State<VendingMachine, VendingMachine> simulate(List<Input> inputs) {
    final var s = State.<VendingMachine>get();
    return inputs.stream().reduce(
        s,
        (stateVm, input) -> stateVm.map(vm -> vm.next(input)),
        (a, b) -> a);
  }

  @Test
  public void machine() {
    State<VendingMachine, VendingMachine> s = simulate(
        List.of(
            Input.COIN, // unlocks, coins + 1
            Input.TURN, // locks, items - 1
            Input.TURN, // nothing
            Input.COIN, // unlocks, coins + 1
            Input.COIN, // nothing
            Input.TURN  // locks, items - 1
        ));

    VendingMachine m = s.eval(new VendingMachine(true, 5, 0));
    System.out.printf("Final machine %s\n", m);

    VendingMachine oracle = new VendingMachine(true, 3, 2);
    assertThat(m, equalTo(oracle));
  }

}

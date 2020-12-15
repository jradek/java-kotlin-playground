package sample.monad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
        System.out.println("no more items");
        return this;
      } else if (i == Input.COIN) {
        if (!isLocked) {
          System.out.println("Invalid state: not locked. Turn first");
          return this;
        }

        System.out.println("accept coin");
        return new VendingMachine(false, numItems, numCoins + 1);
      } else if (i == Input.TURN) {
        if (isLocked) {
          System.out.println("Invalid state: locked. Insert coin first.");
          return this;
        }

        System.out.println("release item");
        return new VendingMachine(true, numItems - 1, numCoins);
      }

      return this;
    }
  }

  static State<VendingMachine, VendingMachine> simulate(List<Input> inputs) {
    return inputs.stream().reduce(
        State.get(),
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

  /* ***********************************************************************************************
   * Functional style, but without state monad
   * **********************************************************************************************/

  /**
   * Build one function the drives the machine
   */
  static Function<VendingMachine, VendingMachine> simulate2(List<Input> inputs) {
    // exploit, that f: a -> a is a monoid
    // 1. start with identity (the zero element for this monoid)
    // 2. 'mappend' new functions

    return inputs.stream().reduce(
        Function.identity(),
        (func, input) -> {
          return vm -> func.apply(vm).next(input);
        },
        (_unused1, _unused2) -> _unused1);
  }

  @Test
  public void machine2() {
    Function<VendingMachine, VendingMachine> f = simulate2(
        List.of(
            Input.COIN, // unlocks, coins + 1
            Input.TURN, // locks, items - 1

            Input.COIN, // unlocks, coins + 1
            Input.TURN, // locks, items - 1

            Input.COIN, // unlocks, coins + 1
            Input.TURN, // locks, items - 1

            Input.COIN, // nothing -> no more items
            Input.TURN  // nothing
        ));

    VendingMachine m = f.apply(new VendingMachine(true, 3, 0));
    System.out.printf("Final machine %s\n", m);

    VendingMachine oracle = new VendingMachine(true, 0, 3);
    assertThat(m, equalTo(oracle));
  }

}

package osmo.tester.optimizer.reducer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class ReducerState {
  private static final Logger log = new Logger(ReducerState.class);
  private int minimum = Integer.MAX_VALUE;
  private volatile boolean done;
  private final List<TestCase> tests = new ArrayList<>();

  public ReducerState(int minimum) {
    this.minimum = minimum;
  }

  public synchronized int getMinimum() {
    return minimum;
  }

  public boolean isDone() {
    return done;
  }

  public void finish() {
    this.done = true;
  }

  public synchronized boolean check(TestCase test, int length) {
    if (length < minimum) {
      minimum = length;
      log.info("Found smaller:"+minimum);
      tests.add(test);
      return true;
    }
    return false;
  }

  public synchronized void addTest(TestCase test) {
    tests.add(test);
  }

  public List<TestCase> getTests() {
    return tests;
  }
}

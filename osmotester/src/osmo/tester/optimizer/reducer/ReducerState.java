package osmo.tester.optimizer.reducer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Teemu Kanstren
 */
public class ReducerState {
  private static final Logger log = new Logger(ReducerState.class);
  private int minimum = Integer.MAX_VALUE;
  private volatile boolean done;
  private final List<TestCase> tests = new ArrayList<>();
  private int count = 0;
  private final Collection<String> hashes = new HashSet<>();
  private Collection<Integer> lengths = new ArrayList<>();
  private final AtomicInteger testCount = new AtomicInteger(0);
  private final List<String> allSteps;

  public ReducerState(List<String> allSteps, int minimum) {
    this.minimum = minimum;
    this.allSteps = allSteps;
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

  public synchronized String addTest(TestCase test) {
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    if (length > minimum) return null;
    String hash = steps.toString();
    if (hashes.contains(hash)) return null;
    if (length < minimum) {
      Analyzer analyzer = new Analyzer(allSteps, this);
      analyzer.analyze();
      analyzer.writeReport("reducer-task-"+length);
      tests.clear();
      hashes.clear();
      count = 0;
      minimum = length;
      lengths.add(length);
      log.info("Found smaller:"+minimum);
    }
    tests.add(test);
    hashes.add(hash);
    count++;
    if (count > 100) return null;
    return "osmo-output/reducer-"+length+"-"+count;
  }

  public List<TestCase> getTests() {
    return tests;
  }

  public Collection<Integer> getLengths() {
    return lengths;
  }
  
  public void testsDone(int count) {
    testCount.addAndGet(count);
  }
  
  public int getTestCount() {
    return testCount.get();
  }

  public boolean check(TestCase test) {
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    return length <= minimum;
  }
}

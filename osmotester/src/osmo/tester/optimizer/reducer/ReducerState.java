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
  private long startTime = Long.MIN_VALUE;
  private final long timeout;
  private final ReducerConfig config;
  private TestCase test = null;
  /** Stop when first option found? Used for initial search. */
  private boolean stopOnFirst = false;

  public ReducerState(List<String> allSteps, ReducerConfig config) {
    this.config = config;
    this.minimum = config.getLength();
    this.allSteps = allSteps;
    this.timeout = config.getIterationUnit().toMillis(config.getIterationTime());
  }

  public ReducerConfig getConfig() {
    return config;
  }

  public synchronized int getMinimum() {
    return minimum;
  }

  public void setStopOnFirst(boolean stopOnFirst) {
    this.stopOnFirst = stopOnFirst;
  }

  public void resetDone() {
    this.done = false;
    //if we are already at minimum, no need to continue with anything else
    checkIfSingleStep(test.getAllStepNames());
  }

  public boolean isDone() {
    return done;
  }

  public void finish() {
    this.done = true;
  }
  
  public TestCase getTest() {
    return test;
  }

  public synchronized void addTest(TestCase test) {
    this.test = test;
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    if (length > minimum) return;
    String hash = steps.toString();
    if (hashes.contains(hash)) return;
    if (length < minimum) {
      if (stopOnFirst) endIteration();
      if (length == 1) {
        endIteration();
      }
      startTime = System.currentTimeMillis();
      //first we write the report for the previous iteration that just finished
      Analyzer analyzer = new Analyzer(allSteps, this);
      analyzer.analyze();
      analyzer.writeReport("reducer-task-"+minimum);
      tests.clear();
      hashes.clear();
      count = 0;
      minimum = length;
      lengths.add(length);
      log.info("Found smaller:"+minimum);
      checkIfSingleStep(steps);
    }
    tests.add(test);
    hashes.add(hash);
    count++;
  }

  private void checkIfSingleStep(Collection<String> steps) {
    HashSet<String> singles = new HashSet<>();
    singles.addAll(steps);
    if (singles.size() == 1) endIteration();
  }

  private synchronized void endIteration() {
    done = true;
    notifyAll();
  }

  public List<TestCase> getTests() {
    return tests;
  }

  public Collection<Integer> getLengths() {
    return lengths;
  }
  
  public synchronized void testsDone(int count) {
    testCount.addAndGet(count);
    if (startTime > 0) {
      //if our reduction iteration has passed its timeout we stop
      long now = System.currentTimeMillis();
      long diff = now-startTime;
      if (diff > timeout) {
        endIteration();
      }
    }
  }
  
  public int getTestCount() {
    if (config.isTestMode()) return 0;
    return testCount.get();
  }

  public boolean check(TestCase test) {
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    return length <= minimum;
  }
}

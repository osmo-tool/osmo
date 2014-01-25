package osmo.tester.optimizer.reducer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * State for reduction.
 * 
 * @author Teemu Kanstren
 */
public class ReducerState {
  private static final Logger log = new Logger(ReducerState.class);
  /** Length of shortest test found so far. */
  private int minimum = Integer.MAX_VALUE;
  /** Is the current reduction phase done? */
  private volatile boolean done;
  /** Found set of minimal tests. */
  private final List<TestCase> tests = new ArrayList<>();
  /** Hashes for current set of tests to avoid duplicates. */
  private final Collection<String> hashes = new HashSet<>();
  /** Lengths of found tests in sequence from longer to shorter. */
  private Collection<Integer> lengths = new ArrayList<>();
  /** Overall number of tests generated. */
  private final AtomicInteger testCount = new AtomicInteger(0);
  /** All steps in the test model, regardless if taken in tests or not. */
  private final List<String> allSteps;
  /** Time when current iteration started. */
  private long startTime = Long.MIN_VALUE;
  /** Iteration timeout in milliseconds. */
  private final long timeout;
  /** Current reduction configuration. */
  private final ReducerConfig config;
  private TestCase test = null;
  /** Stop when first option found? Used for initial search. */
  private boolean stopOnFirst = false;

  /**
   * 
   * @param allSteps All steps in the test model.
   * @param config Configuration for reduction.
   */
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

  /**
   * Resets status to "not done" for new iteration. If already at minimal does not reset.
   */
  public void resetDone() {
    this.done = false;
    //if we are already at minimum, no need to continue with anything else
    checkIfSingleStep(test.getAllStepNames());
  }

  public boolean isDone() {
    return done;
  }

  /**
   * Sets reduction to "done" status causing tasks to stop at following point.
   */
  public void finish() {
    this.done = true;
  }
  
  public TestCase getTest() {
    return test;
  }

  /**
   * Add a new "failing" test case to the list of tests, if applicable (not longer than minimum so far).
   * 
   * @param test to add.
   */
  public synchronized void addTest(TestCase test) {
    this.test = test;
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    //if test is longer than what we already have, we ignore it
    if (length > minimum) return;
    String hash = steps.toString();
    //if we already have this test, we ignore it
    if (hashes.contains(hash)) return;
    if (length < minimum) {
      //initial search needs to stop when it finds first "failing" test
      if (stopOnFirst) endIteration();
      //starting a new iteration, so store new start time for the iteration
      startTime = System.currentTimeMillis();
      //first we write the report for the previous iteration that just finished
      Analyzer analyzer = new Analyzer(allSteps, this);
      analyzer.analyze();
      analyzer.writeReport("reducer-task-"+minimum);
      //set new state for the new iteration
      tests.clear();
      hashes.clear();
      minimum = length;
      lengths.add(length);
      log.info("Found smaller:"+minimum);
      //if we already reached smallest possible set, stop iteration
      checkIfSingleStep(steps);
    }
    //add the new test to the set of found tests for this length
    tests.add(test);
    hashes.add(hash);
  }

  /**
   * Checks if the current test only consists of repetitions of a single step. 
   * If so, we consider it already minimal and stop the iteration.
   * 
   * @param steps The test steps to check..
   */
  private void checkIfSingleStep(Collection<String> steps) {
    HashSet<String> singles = new HashSet<>();
    singles.addAll(steps);
    if (singles.size() == 1) endIteration();
  }

  /**
   * Set current iteration to ending and notify any waiting threads.
   */
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
    //unit tests need a constant number to always produce predictable results
    if (config.isTestMode()) return 0;
    return testCount.get();
  }

  /**
   * Check given test if it is smaller than current minimum.
   * 
   * @param test to check.
   * @return true if smaller than found so far.
   */
  public boolean check(TestCase test) {
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    return length <= minimum;
  }
}

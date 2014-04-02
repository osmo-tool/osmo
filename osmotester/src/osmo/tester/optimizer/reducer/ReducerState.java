package osmo.tester.optimizer.reducer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared state for reduction tasks.
 * TODO: should keep trying all different combinations of shortest, not just one.
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
  /** The latest found test. Used to check for single step length and to set steps to search in next iteration. */
  private TestCase test = null;
  /** Stop when first option found? Used for initial search. */
  private boolean stopOnFirst = false;
  /** If doing requirements search, the requirement we are currently aiming for. */
  private String targetRequirement = null;
  /** Key = requirement name, Value = best test for that requirement. */
  private Map<String, TestCase> requirementsTests = new HashMap<>();
  /** The list of processed requirements so far. The ones that have best test 'found' already. */
  private Collection<String> processedRequirements = new ArrayList<>();

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
    //when searching for requirements, this can be null when starting to look for new req
    if (test == null) return;
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
    log.debug("done");
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

  /**
   * For debug mode, gives list of all tests for shortest length.
   * For requirements, gives the tests found for different requirements.
   * 
   * @return The tests.
   */
  public List<TestCase> getTests() {
    if (config.isRequirementsSearch()) {
      tests.clear();
      tests.addAll(requirementsTests.values());
    }
    return tests;
  }

  public Map<String, TestCase> getRequirementsTests() {
    return requirementsTests;
  }

  public Collection<Integer> getLengths() {
    return lengths;
  }

  /**
   * Increases number of tests generated.
   * Also checks for end if timed out.
   * 
   * @param count Number of tests generated in an iteration, to add to overall metric.
   */
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

  /**
   * Gives the number of tests generated overall so far.
   * 
   * @return The number of tests.
   */
  public int getTestCount() {
    //unit tests need a constant number to always produce predictable results
    if (config.isTestMode()) return 0;
    return testCount.get();
  }

  /**
   * Check given test if it is smaller than current minimum.
   * In debug mode, coming here the test should be a failed one already so no need to check for that.
   * 
   * @param test to check.
   * @return true If given test was better match to search (shorter for debug, shorter + covers target for reqs).
   */
  public boolean check(TestCase test) {
    if (config.isRequirementsSearch()) {
      boolean ok = checkRequirements(test);
      if (!ok) {
        return false;
      }
    }
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    return length <= minimum;
  }

  /**
   * Checks which requirements are covered by the given test case. 
   * Then checks if the test is shorter than that currently stored for any of those requirements.
   * Also updates targeted requirement to first unprocessed one if target is not set when this is called.
   * 
   * @param test To check.
   * @return True if the given test covers the current target requirement.
   */
  private boolean checkRequirements(TestCase test) {
    Collection<String> requirements = test.getCoverage().getRequirements();
    for (String requirement : requirements) {
      TestCase reqTest = requirementsTests.get(requirement);
      if (reqTest == null || test.getLength() < reqTest.getLength()) {
        requirementsTests.put(requirement, test);
      }
      if (targetRequirement == null && !processedRequirements.contains(requirement)) {
        targetRequirement = requirement;
        log.debug("Targeting requirement:"+targetRequirement);
      }
    }
    //if our target was not in the list, we did not find a better one
    return requirements.contains(targetRequirement);
  }

  /**
   * Moves search to next unprocessed requirement.
   * A requirement is considered "unprocessed" until it has been an explicit target of search.
   * Note that a requirement may already have a test associated before being explicitly processed when one
   * is found as part of search for the other requirements.
   * However, no attempt to "shorten" the test explicitly has been made until it is explicitly "processed".
   * 
   * @return True if target number of requirements has been covered already.
   */
  public boolean nextRequirement() {
    if (targetRequirement != null) {
      processedRequirements.add(targetRequirement);
      log.info("Processed req:"+targetRequirement);
    }
    stopOnFirst = true;
    done = false;
    tests.clear();
    hashes.clear();
    lengths.clear();

    test = null;
    String next = null;
    for (String reqName : requirementsTests.keySet()) {
      if (!processedRequirements.contains(reqName)) {
        next = reqName;
        test = requirementsTests.get(next);
        break;
      }
    }
    if (test != null) minimum = test.getLength();
    else minimum = config.getLength();
    
    targetRequirement = next;
    log.debug("New req target:"+next);
    return processedRequirements.size() < config.getRequirementsTarget();
  }
}

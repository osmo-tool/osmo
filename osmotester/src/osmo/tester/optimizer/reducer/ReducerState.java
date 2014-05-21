package osmo.tester.optimizer.reducer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared state for reduction tasks.
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
  private final Collection<Long> hashes = new HashSet<>();
  /** Lengths of found tests in sequence from longer to shorter. */
  private Collection<Integer> lengths = new ArrayList<>();
  /** Overall number of tests generated. */
  private final AtomicInteger testCount = new AtomicInteger(0);
  /** All steps in the test model, regardless if taken in tests or not. */
  private final List<String> allSteps;
  /** Time when current iteration started. */
  public long startTime = Long.MIN_VALUE;
  /** Iteration timeout in milliseconds. */
  private final long timeout;
  /** Current reduction configuration. */
  private final ReducerConfig config;
  /** The latest found test. Used to check for single step length and to set steps to search in next iteration. */
//  private TestCase test = null;
//  /** Stop when first option found? Used for initial search. */
//  private boolean stopOnFirst = false;
  /** If doing requirements search, the requirement we are currently aiming for. */
  private String targetRequirement = null;
  /** Key = requirement name, Value = best test for that requirement. */
  private Map<String, TestCase> requirementsTests = new HashMap<>();
  /** The list of processed requirements so far. The ones that have best test 'found' already. */
  private Collection<String> processedRequirements = new ArrayList<>();

  private enum ReductionPhase {INITIAL_SEARCH, SHORTENING, FINAL_FUZZ}

  ;
  private ReductionPhase phase = ReductionPhase.INITIAL_SEARCH;

  /**
   * @param allSteps All steps in the test model.
   * @param config   Configuration for reduction.
   */
  public ReducerState(List<String> allSteps, ReducerConfig config) {
    this.config = config;
    this.minimum = config.getLength();
    this.allSteps = allSteps;
    this.timeout = config.getIterationUnit().toMillis(config.getIterationTime());
  }

  public void startInitialSearch() {
    startTime = System.currentTimeMillis();
    phase = ReductionPhase.INITIAL_SEARCH;
    resetDone();
  }

  public void startShortening() {
    startTime = System.currentTimeMillis();
    phase = ReductionPhase.SHORTENING;
//    int shortest = Integer.MAX_VALUE;
//    List<TestCase> shortList = new ArrayList<>();
//    for (TestCase test : tests) {
//      int length = test.getLength();
//      if (length < shortest) {
//        shortest = length;
//        shortList.clear();
//      }
//      if (length == shortest) {
//        shortList.add(test);
//      }
//    }
//    tests.clear();
//    tests.addAll(shortList);
//    while (tests.size() > 0 && tests.size() < config.getDiversity()) {
//      log.info("Only "+tests.size() + " tests, replicating more");
//      tests.addAll(shortList);
//    }
    //we need to update "minimum" size for later phases even if we still want to keep all sizes at start for diversity
    for (TestCase test : tests) {
      int length = test.getLength();
      if (length < minimum) minimum = length;
    }
    //TODO: write some tests to see this initial set works as intended and longer ones are not removed
    while (tests.size() > 0 && tests.size() < config.getDiversity()) {
      log.info("Only " + tests.size() + " tests, replicating more");
      tests.addAll(tests);
    }
    log.info("Number of tests in start of shortening " + tests.size());
    resetDone();
  }

  public void startFinalFuzz() {
    startTime = System.currentTimeMillis();
    phase = ReductionPhase.FINAL_FUZZ;
    resetDone();
  }

  public ReducerConfig getConfig() {
    return config;
  }

  public synchronized int getMinimum() {
    return minimum;
  }

//  public void setStopOnFirst(boolean stopOnFirst) {
//    this.stopOnFirst = stopOnFirst;
//  }


  public TestCase getRequirementTest() {
    return requirementsTests.get(targetRequirement);
  }

  /**
   * Resets status to "not done" for new iteration. If already at minimal does not reset.
   */
  public void resetDone() {
    this.done = false;
//    //when searching for requirements, this can be null when starting to look for new req
//    if (test == null) return;
    //if we are already at minimum, no need to continue with anything else
    //checkIfSingleStep();
  }

  public boolean isDone() {
    if (done) return true;
    checkTimeout();
    return done;
  }
//
//  /**
//   * Sets reduction to "done" status causing tasks to stop at following point.
//   */
//  public void finish() {
//    log.debug("done");
//    this.done = true;
//  }
//
//  public TestCase getTest() {
//    return test;
//  }

  /**
   * Add a new "failing" test case to the list of tests, if applicable (not longer than minimum so far).
   *
   * @param test to add.
   */
  public synchronized void addTest(TestCase test) {
    if (test.getLength() > minimum) return;
    Long hash = testHash(test);
    //if we already have this test, we ignore it
    if (hashes.contains(hash)) return;
    log.info("Adding test:" + test);
    switch (phase) {
      case INITIAL_SEARCH:
        addTestInitialSearch(test);
        break;
      case SHORTENING:
        addTestShortening(test);
        break;
      case FINAL_FUZZ:
        addTestFinalFuzz(test);
        break;
      default:
        throw new IllegalStateException("Unknown reduction phase:" + phase);
    }
    //first we write the report for the previous iteration that just finished
    Analyzer analyzer = new Analyzer(allSteps, this);
    analyzer.analyze();
    analyzer.writeReport("reducer-task-" + test.getLength());
    hashes.add(hash);
  }

  private void addTestInitialSearch(TestCase test) {
    //add the new test to the set of initial tests
    tests.add(test);
    if (tests.size() == config.getDiversity()) {
      log.info("Diversity target reached, stopping iteration.");
      endSearch();
    }
  }

  private void addTestShortening(TestCase test) {
    checkMinimum(test);
    //add the new test to the set of found tests for this length
    tests.add(test);
  }

  private void addTestFinalFuzz(TestCase test) {
    checkMinimum(test);
    //add the new test to the set of found tests for this length
    tests.add(test);
  }

  private void checkMinimum(TestCase test) {
    List<String> steps = test.getAllStepNames();
    int length = steps.size();
    if (length < minimum) {
      //starting a new iteration, so store new start time for the iteration
      startTime = System.currentTimeMillis();
//      //first we write the report for the previous iteration that just finished
//      Analyzer analyzer = new Analyzer(allSteps, this);
//      analyzer.analyze();
//      analyzer.writeReport("reducer-task-" + minimum);
      //set new state for the new iteration
      tests.clear();
      hashes.clear();
      minimum = length;
      lengths.add(length);
      log.info("Found smaller:" + minimum);
      //if we already reached smallest possible set, stop iteration
      //checkIfSingleStep();
    }
  }

  private long testHash(TestCase test) {
    long hash = 0;
    if (config.isRequirementsSearch()) {
      Collection<String> steps = test.getAllStepNames();
      for (String step : steps) {
        hash += step.hashCode();
      }
    } else {
      hash += test.getAllStepNames().toString().hashCode();
    }
    return hash;
  }

//  /**
//   * Checks if the current test only consists of repetitions of a single step.
//   * If so, we consider it already minimal and stop the iteration.
//   */
//  private void checkIfSingleStep() {
//    if (tests.size() == 0) return;
//    TestCase test = tests.get(0);
//    List<String> steps = test.getAllStepNames();
//    HashSet<String> singles = new HashSet<>();
//    singles.addAll(steps);
//    if (singles.size() == 1) endSearch();
//  }

  /**
   * Set current iteration to ending and notify any waiting threads.
   */
  public synchronized void endSearch() {
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
    List<TestCase> result = new ArrayList<>();
    if (config.isRequirementsSearch()) {
      TestCase target = requirementsTests.get(targetRequirement);
      if (target != null) result.add(target);
    } else {
      result.addAll(tests);
    }
    return result;
  }

  /**
   * Removes all tests from given set that are longer than minimum.
   * Modifies the given collection.
   * This call is required for cases where the initial search finds the shortest one and later phases do not
   * find anything better. In that case, the initial set holds until the end, including tests of varying length.
   * If a later phase manages to improve, this would not be required.
   */
  public void prune() {
    for (Iterator<TestCase> i = tests.iterator(); i.hasNext(); ) {
      TestCase test = i.next();
      if (test.getLength() > minimum) i.remove();
    }
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
    checkTimeout();
//    if (startTime > 0) {
//      //if our reduction iteration has passed its timeout we stop
//      long now = System.currentTimeMillis();
//      long diff = now - startTime;
//      if (diff > timeout) {
//        endSearch();
//      }
//    }
  }

  private void checkTimeout() {
    //if our reduction iteration has passed its timeout we stop
    long now = System.currentTimeMillis();
    long diff = now - startTime;
    if (diff > timeout) {
      log.info("Iteration timed out");
      endSearch();
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
      if (test.getLength() >= minimum) return false;
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
        log.debug("Targeting requirement:" + targetRequirement);
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
  public synchronized boolean nextRequirement() {
    if (targetRequirement != null) {
      processedRequirements.add(targetRequirement);
      log.info("Processed req:" + targetRequirement);
    }
    tests.clear();
    hashes.clear();
    lengths.clear();

    String next = null;
    TestCase test = null;
    for (String reqName : requirementsTests.keySet()) {
      if (!processedRequirements.contains(reqName)) {
        next = reqName;
        test = requirementsTests.get(next);
        tests.add(test);
        hashes.add(testHash(test));
        break;
      }
    }
    if (test != null) minimum = test.getLength();
    else minimum = Integer.MAX_VALUE;

    targetRequirement = next;
    log.debug("New req target:" + next + " -- tests:" + tests);
    return processedRequirements.size() < config.getRequirementsTarget();
  }
}

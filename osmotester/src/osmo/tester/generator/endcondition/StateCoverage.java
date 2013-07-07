package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/** 
 * An end condition that requires the given set of user defined state values to be covered.
 * A single state can be listed many times, meaning it must be covered several times.
 * A state is observed several times if it is observed in several steps, regardless of what is in between.
 * 
 * @author Teemu Kanstren 
 */
public class StateCoverage implements EndCondition {
  /** List of required states to covered. */
  private Collection<String> required = new ArrayList<>();
  /** States observed in suite so far. */
  private Collection<String> suiteStates = new LinkedHashSet<>();
  /** Set of remaining states to cover for current test (when used at test level). */
  private List<String> remainingForTest = new ArrayList<>();
  /** Number of tests in suite, used to reset coverage when end condition is used at test level. */
  private int testCount = 0;

  public StateCoverage(String... states) {
    Collections.addAll(this.required, states);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    Collection<String> covered = suite.getCoverage().getStates();
    Collection<String> clone = new ArrayList<>();
    clone.addAll(required);
    clone.removeAll(covered);
    //if all were covered we are done
    return clone.size() == 0;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    int suiteSize = suite.getAllTestCases().size();
    if (testCount != suiteSize) {
      //test has changed, reset values
      remainingForTest.clear();
      remainingForTest.addAll(required);
      testCount = suiteSize;
    }
    TestCase currentTest = suite.getCurrentTest();
    TestCaseStep currentStep = currentTest.getCurrentStep();
    //step is null if we are just starting the test case
    if (currentStep == null) return false;
    String state = currentStep.getState();
    suiteStates.add(state);
    remainingForTest.remove(state);
    //did we cover all? if so the size is 0
    return remainingForTest.size() == 0;
  }

  @Override
  public void init(long seed, FSM fsm) {
  }
}

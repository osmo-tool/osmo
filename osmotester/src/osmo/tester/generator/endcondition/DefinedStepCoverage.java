package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class DefinedStepCoverage implements EndCondition {
  private static Logger log = new Logger(DefinedStepCoverage.class);
  private Collection<String> required = new ArrayList<String>();

  public DefinedStepCoverage(String... stepsToCover) {
    Collections.addAll(required, stepsToCover);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    log.debug("suite check");
    List<TestCase> allTests = suite.getAllTestCases();
    Collection<String> steps = new ArrayList<String>();
    for (TestCase test : allTests) {
      steps.addAll(stepsFor(test));
    }
    return checkRequiredSteps(steps);
  }

  private boolean checkRequiredSteps(Collection<String> steps) {
    Collection<String> remaining = new ArrayList<String>();
    log.debug("steps:" + steps);
    remaining.addAll(required);
    for (String step : steps) {
      remaining.remove(step);
    }
    log.debug("remaining:"+remaining);
    return remaining.size() == 0;
  }

  private Collection<String> stepsFor(TestCase test) {
    Collection<TestStep> testSteps = test.getSteps();
    Collection<String> steps = new ArrayList<String>();
    for (TestStep step : testSteps) {
      steps.add(step.getTransition().getName());
    }
    return steps;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.debug("test check");
    TestCase test = suite.getCurrentTest();
    Collection<String> steps = stepsFor(test);
    return checkRequiredSteps(steps);
  }

  public void addRequiredStep(String step) {
    required.add(step);
  }
}

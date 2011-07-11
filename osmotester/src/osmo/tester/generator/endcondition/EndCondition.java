package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * This interface defines an end condition for when test suite or test case generation is stopped.
 * 
 * @author Teemu Kanstren
 */
public interface EndCondition {
  /**
   * This method is called by the test generator to evaluate when to stop generating new steps for a test case
   * or new test cases for a test suite.
   *
   * @param fsm The FSM used for test generation, including reference to generated tests so far.
   * @param evaluateSuite False if evaluating the stopping of generation steps for a single test case,
   *                   true if evaluating the stopping of test case generation for a test suite.
   * @return True to stop generation, false to continue.
   */
  public boolean endNow(FSM fsm, boolean evaluateSuite);
}

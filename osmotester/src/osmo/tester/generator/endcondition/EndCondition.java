package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * This interface defines an end condition for when test suite or test case generation is stopped.
 *
 * @author Teemu Kanstren
 */
public interface EndCondition {
  /**
   * This method is called by the test generator to evaluate when to stop generating new test cases for a test suite.
   *
   * @param suite Test suite being generated so far.
   * @param fsm   The FSM used for test generation, including reference to generated tests so far.
   * @return True to stop generation, false to continue.
   */
  public boolean endSuite(TestSuite suite, FSM fsm);

  /**
   * This method is called by the test generator to evaluate when to stop generating new steps for a test case.
   *
   * @param suite Test suite being generated so far.
   * @param fsm   The FSM used for test generation, including reference to generated tests so far.
   * @return True to stop generation, false to continue.
   */
  public boolean endTest(TestSuite suite, FSM fsm);

  /**
   * called to initialize end condition before generation starts.
   *
   * @param fsm Representation of the model.
   */
  public void init(FSM fsm);

  /**
   * Should return true if the end condition is strict (stop test/suite immediately if this indicates end even if others do not).
   *
   * @return True if strict.
   */
  public boolean isStrict();

  public void setStrict(boolean strict);
}


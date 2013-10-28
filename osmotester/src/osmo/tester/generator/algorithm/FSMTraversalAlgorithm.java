package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.List;

/**
 * This interface defines the algorithms used for test generation.
 *
 * @author Teemu Kanstren
 */
public interface FSMTraversalAlgorithm {
  /**
   * This method is called once for each test step to be generated.
   * The algorithm should pick one of the given set of enabled steps.
   * The given set of enabled steps is the ones for which no guard method returned the value {@code false}.
   *
   * @param history The set of previously generated tests, the current test, their test steps, and other information.
   * @param choices The set of enabled steps, from which the algorithm should pick one.
   * @return The step that should be taken next.
   */
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices);

  /**
   * This method is called once before test generation starts to allow the algorithm to initialize itself with
   * any of the provided data.
   *
   * @param seed Randomization seed to be used for test generation.
   * @param fsm From parsing all given model objects, describes the test model structure.
   */
  public void init(long seed, FSM fsm);

  /**
   * This is called before a new test is started.
   */
  public void initTest();
}

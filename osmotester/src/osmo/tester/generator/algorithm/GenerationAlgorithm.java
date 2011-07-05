package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.List;

/**
 * This interface defines the algorithms used for test generation.
 * 
 * @author Teemu Kanstren
 */
public interface GenerationAlgorithm {
  /**
   * This method is called once for each test step to be generated from the given FSM test model (object).
   * The algorithm should pick one of the given set of enabled transitions as the next test step.
   * The given set of enabled transitions is the set of transitions for which no guard method returned the value
   * {@code false}.
   *
   * @param history The set of previously generated tests, the current test, their test steps, and other information.
   * @param transitions The set of enabled transitions, from which the algorithm should pick one.
   * @return  The transition that should be taken next.
   */
  public abstract FSMTransition choose(TestSuite history, List<FSMTransition> transitions);
}

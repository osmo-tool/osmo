package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.List;

/** @author Teemu Kanstren */
public class SearchAlgorithm implements FSMTraversalAlgorithm {
  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    return null;
  }
}

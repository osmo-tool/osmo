package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.List;
import java.util.Random;

/**
 * A simple algorithm that randomly picks a transition from the given set.
 * It uses a static seed for randomization to produce a deterministic result.
 * An alternative implementation could use a random seed as well to produce different test suites every time.
 * 
 * @author Teemu Kanstren
 */
public class RandomAlgorithm implements SequenceGenerationAlgorithm {
  /** Used to provide the randomization, along with the constant seed for deterministic test suites. */
  private Random random = new Random(1);

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    int n = random.nextInt(transitions.size());
    return transitions.get(n);
  }
}

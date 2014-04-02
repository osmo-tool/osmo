package osmo.tester.optimizer.reducer.debug;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.reducer.debug.invariants.LastSteps;
import osmo.tester.optimizer.reducer.debug.invariants.Precedence;
import osmo.tester.optimizer.reducer.debug.invariants.SharedSequence;
import osmo.tester.optimizer.reducer.debug.invariants.StepCounts;

import java.util.Collection;
import java.util.List;

/**
 * Collects invariants over traces of test steps in given test cases.
 * 
 * @author Teemu Kanstren
 */
public class Invariants {
  /** Set of last steps in tests. */
  private final LastSteps lastSteps = new LastSteps();
  /** Min and max of step counts in tests. */
  private final StepCounts stepCounts;
  /** Which steps always appear before some others. */
  private final Precedence precedence;
  /** Common sequences in all test traces. */
  private SharedSequence sequences = new SharedSequence();

  /**
   * 
   * @param allSteps All steps in the model.
   */
  public Invariants(List<String> allSteps) {
    precedence = new Precedence(allSteps);
    stepCounts = new StepCounts(allSteps);
  }

  /**
   * Updates invariants with the traces from given test.
   * 
   * @param test Trace to feed invariant processors.
   */
  public void process(TestCase test) {
    List<String> steps = test.getAllStepNames();
    sequences.init(steps);
    precedence.process(steps);
    sequences.process(steps);
    stepCounts.process(steps);
    lastSteps.process(steps);
  }

  public Collection<String> getLastSteps() {
    return lastSteps.getLastSteps();
  }
  
  public Collection<String> getUsedStepCounts() {
    return stepCounts.getUsedStepCounts();
  }

  public Collection<String> getMissingSteps() {
    return stepCounts.getMissingStepCounts();
  }

  public Collection<String> getPrecedencePatterns() {
    return precedence.getPatterns();
  }

  public Collection<String> getSequencePatterns() {
    return sequences.getPatterns();
  }
}

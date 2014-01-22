package osmo.tester.optimizer.reducer;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.optimizer.reducer.invariants.LastSteps;
import osmo.tester.optimizer.reducer.invariants.Precedence;
import osmo.tester.optimizer.reducer.invariants.SharedSequence;
import osmo.tester.optimizer.reducer.invariants.StepCounts;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Invariants {
  private final LastSteps lastSteps = new LastSteps();
  private final StepCounts stepCounts;
  private final Precedence precedence;
  private SharedSequence sequences = new SharedSequence();
  
  public Invariants(List<String> steps) {
    precedence = new Precedence(steps);
    stepCounts = new StepCounts(steps);
  }

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

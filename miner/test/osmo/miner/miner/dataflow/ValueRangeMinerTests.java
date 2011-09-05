package osmo.miner.miner.dataflow;

import org.junit.Before;
import org.junit.Test;
import osmo.miner.miner.MainMiner;
import osmo.miner.model.dataflow.DataFlowInvariant;
import osmo.miner.model.general.InvariantCollection;
import osmo.miner.model.dataflow.ValueRangeInt;
import osmo.miner.model.general.VariableInvariants;
import osmo.miner.model.program.Suite;
import osmo.miner.testmodels.TestModels1;

import java.util.Collection;

import static junit.framework.Assert.*;
import static osmo.tester.TestUtils.getResource;
import static osmo.tester.TestUtils.unifyLineSeparators;

/**
 * @author Teemu Kanstren
 */
public class ValueRangeMinerTests {
  private MainMiner main;

  @Before
  public void setup() {
    ValueRangeMiner vrMiner = new ValueRangeMiner();
    main = new MainMiner();
    main.addMiner(vrMiner);
  }

  @Test
  public void invariantsForModel1() {
    Suite suite = TestModels1.model1();
    main.mine(suite);
    InvariantCollection invariants = main.getInvariants();

    assertEquals("Total number of invariants", 18, invariants.size());

    assertInvalidInvariantFound(invariants, "v1", "global");
    assertInvariantCount(invariants, "s1v1", "global", 1);
    assertInvariantCount(invariants, "s1v2", "global", 1);
    assertInvariantCount(invariants, "s2v1", "global", 1);
    assertInvariantCount(invariants, "s2v2", "global", 1);
    assertInvariantCount(invariants, "s3v1", "global", 1);
    assertInvariantCount(invariants, "s3v2", "global", 1);
    assertInvariantCount(invariants, "s4v1", "global", 1);
    assertInvariantCount(invariants, "s4v2", "global", 1);

    assertInvalidInvariantFound(invariants, "v1", "TestProgram1");
    assertInvariantCount(invariants, "s1v1", "step1", 1);
    assertInvariantCount(invariants, "s1v2", "step1", 1);
    assertInvariantCount(invariants, "s2v1", "step2", 1);
    assertInvariantCount(invariants, "s2v2", "step2", 1);
    assertInvariantCount(invariants, "s3v1", "step3", 1);
    assertInvariantCount(invariants, "s3v2", "step3", 1);
    assertInvariantCount(invariants, "s4v1", "step4", 1);
    assertInvariantCount(invariants, "s4v2", "step4", 1);

    assertInvariantRange(invariants, "s1v1", "global", 1, 1);
    assertInvariantRange(invariants, "s1v2", "global", 2, 2);
    assertInvariantRange(invariants, "s2v1", "global", 4, 7);
    assertInvariantRange(invariants, "s2v2", "global", 2, 2);
    assertInvariantRange(invariants, "s3v1", "global", 3, 5);
    assertInvariantRange(invariants, "s3v2", "global", 4, 4);
    assertInvariantRange(invariants, "s4v1", "global", 1, 1);
    assertInvariantRange(invariants, "s4v2", "global", 2, 2);

    assertInvariantRange(invariants, "s1v1", "step1", 1, 1);
    assertInvariantRange(invariants, "s1v2", "step1", 2, 2);
    assertInvariantRange(invariants, "s2v1", "step2", 4, 7);
    assertInvariantRange(invariants, "s2v2", "step2", 2, 2);
    assertInvariantRange(invariants, "s3v1", "step3", 3, 5);
    assertInvariantRange(invariants, "s3v2", "step3", 4, 4);
    assertInvariantRange(invariants, "s4v1", "step4", 1, 1);
    assertInvariantRange(invariants, "s4v2", "step4", 2, 2);
  }

  @Test
  public void reportForModel1() {
    Suite suite = TestModels1.model1();
    main.mine(suite);
    InvariantCollection invariants = main.getInvariants();
    String expected = getResource(getClass(), "expected-vr-model1.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generated report for model1", expected, invariants.toString());
  }

  private void assertInvariantCount(InvariantCollection toCheck, String variable, String scope, int expectedCount) {
    VariableInvariants invariants = toCheck.getInvariants(scope, variable);
    for (DataFlowInvariant invariant : invariants.getInvariants()) {
      assertTrue("Invariant should be valid", invariant.isValid());
    }
    int actualCount = invariants.count();
    assertEquals("Number of invariants for variable:" + variable + " scope:" + scope, expectedCount, actualCount);
  }

  public void assertInvalidInvariantFound(InvariantCollection toCheck, String variable, String scope) {
    VariableInvariants invariants = toCheck.getInvariants(scope, variable);
    for (DataFlowInvariant invariant : invariants.getInvariants()) {
      assertFalse("Invariant should be invalid", invariant.isValid());
    }
    assertTrue("Could not find required value range invariant for variable:" + variable + " scope:" + scope, invariants.count() > 0);
    assertTrue("Should have only one value range invariant for variable:" + variable + " scope:" + scope, invariants.count() == 1);
  }

  public void assertInvariantRange(InvariantCollection toCheck, String variable, String scope, int min, int max) {
    VariableInvariants invariants = toCheck.getInvariants(scope, variable);
    for (DataFlowInvariant invariant : invariants.getInvariants()) {
      ValueRangeInt vr = (ValueRangeInt) invariant;
      assertTrue("Invariant for variable:" + variable + " scope:" + scope + " should be valid", vr.isValid());
      assertEquals("Min value for value range", min, vr.getMin());
      assertEquals("Max value for value range", max, vr.getMax());
    }
    assertEquals("Should only have one value range for:" + variable + " scope:" + scope, 1, invariants.count());
  }
}

package osmo.tester.endconditions;

import org.junit.Test;
import osmo.tester.generator.endcondition.ProbabilityCondition;

import static junit.framework.Assert.fail;

/**
 * @author Teemu Kanstren
 */
public class ProbabilityTests {
  @Test
  public void testTooLowThreshold() {
    new ProbabilityCondition(0);
    new ProbabilityCondition(0.1);
    assertThresholdOutOfBounds(-1);
    assertThresholdOutOfBounds(-0.01);
  }

  @Test
  public void testTooHighThreshold() {
    new ProbabilityCondition(1);
    new ProbabilityCondition(0.99);
    assertThresholdOutOfBounds(1.01);
    assertThresholdOutOfBounds(2);
  }

  private void assertThresholdOutOfBounds(double value) {
    try {
      new ProbabilityCondition(value);
      fail("Creation of "+ProbabilityCondition.class.getSimpleName()+" with threshold of "+value+" should fail.");
    } catch (IllegalArgumentException e) {
      //expected
    }
  }
}

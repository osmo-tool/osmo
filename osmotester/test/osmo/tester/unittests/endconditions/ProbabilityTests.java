package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.tester.generator.endcondition.Probability;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ProbabilityTests {
  @Test
  public void testTooLowThreshold() {
    new Probability(0);
    new Probability(0.1);
    assertThresholdOutOfBounds(-1);
    assertThresholdOutOfBounds(-0.01);
  }

  @Test
  public void testTooHighThreshold() {
    new Probability(1);
    new Probability(0.99);
    assertThresholdOutOfBounds(1.01);
    assertThresholdOutOfBounds(2);
  }

  private void assertThresholdOutOfBounds(double value) {
    try {
      new Probability(value);
      fail("Creation of " + Probability.class.getSimpleName() + " with threshold of " + value + " should fail.");
    } catch (IllegalArgumentException e) {
      //expected
    }
  }
}

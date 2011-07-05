package osmo.tester.strategies;

import org.junit.Test;
import osmo.tester.generator.strategy.ProbabilityStrategy;

import static junit.framework.Assert.fail;

/**
 * @author Teemu Kanstren
 */
public class ProbabilityTests {
  @Test
  public void testTooLowThreshold() {
    new ProbabilityStrategy(0);
    new ProbabilityStrategy(0.1);
    assertThresholdOutOfBounds(-1);
    assertThresholdOutOfBounds(-0.01);
  }

  @Test
  public void testTooHighThreshold() {
    new ProbabilityStrategy(1);
    new ProbabilityStrategy(0.99);
    assertThresholdOutOfBounds(1.01);
    assertThresholdOutOfBounds(2);
  }

  private void assertThresholdOutOfBounds(double value) {
    try {
      new ProbabilityStrategy(value);
      fail("Creation of "+ProbabilityStrategy.class.getSimpleName()+" with threshold of "+value+" should fail.");
    } catch (IllegalArgumentException e) {
      //expected
    }
  }
}

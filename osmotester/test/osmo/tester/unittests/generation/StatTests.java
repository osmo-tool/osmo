package osmo.tester.unittests.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestStatistics;

import static org.junit.Assert.assertEquals;

public class StatTests {
  TestStatistics stats = null;

  @Before
  public void testSetup() {
    stats = new TestStatistics();
  }

  @Test
  public void all5() {
    stats.addValue(5);
    stats.addValue(5);
    stats.addValue(5);
    stats.addValue(5);
    double mean = stats.getMean();
    assertEquals(5.0d, mean, 0.0);
    double std = stats.getStandardDeviation();
    assertEquals(0.0d, std, 0.0);
  }

  @Test
  public void all123() {
    stats.addValue(123);
    stats.addValue(123);
    stats.addValue(123);
    stats.addValue(123);
    double mean = stats.getMean();
    assertEquals(123.0d, mean, 0.0);
    double std = stats.getStandardDeviation();
    assertEquals(0.0d, std, 0.0);
  }

  @Test
  public void twoNumbers() {
    stats.addValue(10);
    stats.addValue(5);
    double mean = stats.getMean();
    assertEquals(7.5d, mean, 0.0);
    double std = stats.getStandardDeviation();
    assertEquals(2.5d, std, 0.0);
  }

  @Test
  public void tenRandoms() {
    stats.addValue(9989997774L);
    stats.addValue(3464795982L);
    stats.addValue(7769888817L);
    stats.addValue(8429858296L);
    stats.addValue(989509514L);
    stats.addValue(8355532705L);
    stats.addValue(6348129923L);
    stats.addValue(1777518929L);
    stats.addValue(2906498622L);
    stats.addValue(9946116546L);
    double mean = stats.getMean();
    assertEquals(5997784710.8d, mean, 0.001);
    double std = stats.getStandardDeviation();
    assertEquals(3242370845.2411d, std, 0.001);
  }
}

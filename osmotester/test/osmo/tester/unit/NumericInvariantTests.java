package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.model.dataflow.DoubleInvariant;
import osmo.tester.model.dataflow.InputStrategy;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class NumericInvariantTests {
  @Test
  public void minMaxTest() {
    DoubleInvariant ni = new DoubleInvariant();
    try {
      ni.addPartition(10d, 0d);
      fail("Wrong order of min/max should throw exception.");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void separatePositivePartitionsWithLoop() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.setStrategy(InputStrategy.ORDERED_LOOP);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was "+d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was "+d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was "+d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was "+d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingPositivePartitionsWithLoop() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(50d, 200d);
    ni.addPartition(150d, 300d);
    ni.setStrategy(InputStrategy.ORDERED_LOOP);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was "+d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was "+d2, d2 >= 50 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was "+d3, d3 >= 150 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was "+d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingNegativePartitionsWithLoop() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    ni.setStrategy(InputStrategy.ORDERED_LOOP);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    assertTrue("Generated value 1 should be in partition 1 (-10-100) was "+d1, d1 >= -10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (-200-(-50)) was "+d2, d2 <= -50 && d2 >= -200);
    assertTrue("Generated value 3 should be in partition 3 (-150-0) was "+d3, d3 >= -150 && d3 <= 0);
    assertTrue("Generated value 4 should be in partition 4 (-10-100) was "+d4, d4 >= -10 && d4 <= 100);
    assertFalse("Number generation should be random, appears not (repeat to be sure): generated value was the same twice ("+d4+")", d1 == d4);
  }

  @Test
  public void randomInput() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    ni.setStrategy(InputStrategy.RANDOM);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    assertTrue("Generated value 1 should be in partitions 1-3 (-10-100, -50-(-200) was "+d1, (d1 >= -10 && d1 <= 100) || (d1 <= 0 && d1 >= -200));
    assertTrue("Generated value 2 should be in partitions 1-3 (-10-100, -50-(-200) was "+d2, (d2 >= -10 && d2 <= 100) || (d2 <= 0 && d2 >= -200));
    assertTrue("Generated value 3 should be in partitions 1-3 (-10-100, -50-(-200) was "+d3, (d3 >= -10 && d3 <= 100) || (d3 <= 0 && d3 >= -200));
    assertTrue("Generated value 4 should be in partitions 1-3 (-10-100, -50-(-200) was "+d4, (d4 >= -10 && d4 <= 100) || (d4 <= 0 && d4 >= -200));
  }

  @Test
  public void optimizedRandomInput() {
    OSMOTester osmo = new OSMOTester();
    osmo.setDebug(true);
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.setStrategy(InputStrategy.OPTIMIZED_RANDOM);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    int p1 = findInPartitions(d1, 10d, 100d, 150d, 200d, 250d, 300d);
    int p2 = findInPartitions(d2, 10d, 100d, 150d, 200d, 250d, 300d);
    int p3 = findInPartitions(d3, 10d, 100d, 150d, 200d, 250d, 300d);
    int p4 = findInPartitions(d4, 10d, 100d, 150d, 200d, 250d, 300d);
    assertTrue("Generated values should cover all partitions now: "+p1+", "+p2+", "+p3, p1 != p2 && p1 != p3 && p2 != p3);
    assertTrue("Overflowing generated value should be in one of the already covered partitions.", p4 == p1 || p4 == p2 || p4 == p3);

  }

  private int findInPartitions(double value, double min1, double max1, double min2, double max2, double min3, double max3) {
    if (value >= min1 && value <= max1) {
      return 1;
    }
    if (value >= min2 && value <= max2) {
      return 2;
    }
    if (value >= min3 && value <= max3) {
      return 3;
    }
    fail("Generated value is not in any given partition.");
    //this is in practice never executed
    return -1;
  }

  @Test
  public void addRemovePartitions() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.addPartition(101d, 120d);
    ni.setStrategy(InputStrategy.ORDERED_LOOP);
    double d1 = ni.input();
    double d2 = ni.input();
    double d3 = ni.input();
    double d4 = ni.input();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was "+d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was "+d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was "+d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 4 (101-120) was "+d4, d4 >= 101 && d4 <= 120);
    ni.removePartition(101d, 120d);

    d1 = ni.input();
    d2 = ni.input();
    d3 = ni.input();
    d4 = ni.input();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was "+d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was "+d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was "+d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was "+d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void zeroPartitions() {
    DoubleInvariant ni = new DoubleInvariant();
    try {
      double d1 = ni.input();
      fail("Zero partitions should fail generation.");
    } catch (IllegalStateException e) {
      assertTrue("Zero partitions should fail generation.", e.getMessage().startsWith("No partitions defined"));
    }
  }

  @Test
  public void evaluationWithOnePartition() {
    OSMOTester osmo = new OSMOTester();
    osmo.setDebug(true);
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    //TODO: test data generation with boundaries for partitions
    assertInvariantDoesNotContain(1d, ni);
    assertInvariantDoesNotContain(9d, ni);
    assertInvariantContains(10d, ni);
    assertInvariantContains(11d, ni);
    assertInvariantContains(50d, ni);
    assertInvariantContains(99d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantDoesNotContain(101d, ni);
  }

  public void assertInvariantContains(double value, DoubleInvariant di) {
    assertTrue("Value "+value+" should be contained in partition(s):"+di, di.evaluate(value));
  }

  public void assertInvariantDoesNotContain(double value, DoubleInvariant di) {
    assertFalse("Value "+value+" should not be contained in partition(s):"+di, di.evaluate(value));
  }

  @Test
  public void evaluationWithThreePartitions() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    assertInvariantContains(10d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantContains(150d, ni);
    assertInvariantContains(200d, ni);
    assertInvariantContains(250d, ni);
    assertInvariantContains(275d, ni);
    assertInvariantContains(300d, ni);

    assertInvariantDoesNotContain(9.9d, ni);
    assertInvariantDoesNotContain(0d, ni);
    assertInvariantDoesNotContain(100.1d, ni);
    assertInvariantDoesNotContain(149.9d, ni);
    assertInvariantDoesNotContain(145d, ni);
    assertInvariantDoesNotContain(200.1d, ni);
    assertInvariantDoesNotContain(249.9d, ni);
    assertInvariantDoesNotContain(300.1d, ni);
    assertInvariantDoesNotContain(400d, ni);
    assertInvariantDoesNotContain(-400d, ni);
  }

  @Test
  public void evaluationWithOverlappingPartitions() {
    DoubleInvariant ni = new DoubleInvariant();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.addPartition(50d, 120d);
    assertInvariantContains(10d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantContains(119.9d, ni);
    assertInvariantContains(120d, ni);
    assertInvariantContains(150d, ni);
    assertInvariantContains(200d, ni);
    assertInvariantContains(250d, ni);
    assertInvariantContains(275d, ni);
    assertInvariantContains(300d, ni);

    assertInvariantDoesNotContain(9.9d, ni);
    assertInvariantDoesNotContain(0d, ni);
    assertInvariantDoesNotContain(120.1d, ni);
    assertInvariantDoesNotContain(149.9d, ni);
    assertInvariantDoesNotContain(145d, ni);
    assertInvariantDoesNotContain(200.1d, ni);
    assertInvariantDoesNotContain(249.9d, ni);
    assertInvariantDoesNotContain(300.1d, ni);
    assertInvariantDoesNotContain(400d, ni);
    assertInvariantDoesNotContain(-400d, ni);
  }
}

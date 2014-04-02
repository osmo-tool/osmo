package osmo.tester.unittests.model.data;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.model.data.ValueRangeSet;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValueRangeSetTests {
  private ValueRangeSet<Double> ni = null;

  @Before
  public void setUp() throws Exception {
    ni = new ValueRangeSet<>();
    ni.setSeed(333);
  }

  @Test
  public void minMaxTest() {
    try {
      ni.addPartition(10d, 0d);
      fail("Wrong order of min/max should throw exception.");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void separatePositivePartitionsWithLoop() {
    ni.setStrategy(ValueRangeSet.LOOP);
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    double d1 = ni.random();
    double d2 = ni.random();
    double d3 = ni.random();
    double d4 = ni.random();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingPositivePartitionsWithLoop() {
    ni.setStrategy(ValueRangeSet.LOOP);
    ni.addPartition(10d, 100d);
    ni.addPartition(50d, 200d);
    ni.addPartition(150d, 300d);
    double d1 = ni.random();
    double d2 = ni.random();
    double d3 = ni.random();
    double d4 = ni.random();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 50 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 150 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingNegativePartitionsWithLoop() {
    ni.setStrategy(ValueRangeSet.LOOP);
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    double d1 = ni.random();
    double d2 = ni.random();
    double d3 = ni.random();
    double d4 = ni.random();
    assertTrue("Generated value 1 should be in partition 1 (-10-100) was " + d1, d1 >= -10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (-200-(-50)) was " + d2, d2 <= -50 && d2 >= -200);
    assertTrue("Generated value 3 should be in partition 3 (-150-0) was " + d3, d3 >= -150 && d3 <= 0);
    assertTrue("Generated value 4 should be in partition 4 (-10-100) was " + d4, d4 >= -10 && d4 <= 100);
    assertFalse("Number generation should be random, appears not (repeat to be sure): generated value was the same twice (" + d4 + ")", d1 == d4);
  }

  @Test
  public void randomInput() {
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    double d1 = ni.random();
    double d2 = ni.random();
    double d3 = ni.random();
    double d4 = ni.random();
    assertTrue("Generated value 1 should be in partitions 1-3 (-10-100, -50-(-200) was " + d1, (d1 >= -10 && d1 <= 100) || (d1 <= 0 && d1 >= -200));
    assertTrue("Generated value 2 should be in partitions 1-3 (-10-100, -50-(-200) was " + d2, (d2 >= -10 && d2 <= 100) || (d2 <= 0 && d2 >= -200));
    assertTrue("Generated value 3 should be in partitions 1-3 (-10-100, -50-(-200) was " + d3, (d3 >= -10 && d3 <= 100) || (d3 <= 0 && d3 >= -200));
    assertTrue("Generated value 4 should be in partitions 1-3 (-10-100, -50-(-200) was " + d4, (d4 >= -10 && d4 <= 100) || (d4 <= 0 && d4 >= -200));
  }

  @Test
  public void balancingInput() {
    ni.setStrategy(ValueRangeSet.BALANCED);
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    double d1 = ni.balanced();
    double d2 = ni.balanced();
    double d3 = ni.balanced();
    double d4 = ni.balanced();
    int p1 = findInPartitions(d1, 10d, 100d, 150d, 200d, 250d, 300d);
    int p2 = findInPartitions(d2, 10d, 100d, 150d, 200d, 250d, 300d);
    int p3 = findInPartitions(d3, 10d, 100d, 150d, 200d, 250d, 300d);
    int p4 = findInPartitions(d4, 10d, 100d, 150d, 200d, 250d, 300d);
    assertTrue("Generated values should cover all partitions now: " + p1 + ", " + p2 + ", " + p3, p1 != p2 && p1 != p3 && p2 != p3);
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
    ni.setStrategy(ValueRangeSet.LOOP);
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.addPartition(101d, 120d);
    double d1 = ni.ordered();
    double d2 = ni.ordered();
    double d3 = ni.ordered();
    double d4 = ni.ordered();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 4 (101-120) was " + d4, d4 >= 101 && d4 <= 120);
    ni.removePartition(101d, 120d);

    d1 = ni.ordered();
    d2 = ni.ordered();
    d3 = ni.ordered();
    d4 = ni.ordered();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void zeroPartitions() {
    try {
      double d1 = ni.random();
      fail("Zero partitions should fail generation.");
    } catch (IllegalStateException e) {
      assertTrue("Zero partitions should fail generation.", e.getMessage().startsWith("No partitions defined"));
    }
  }

  @Test
  public void intGenerationWithTwoValues() {
    ValueRangeSet<Integer> ni = new ValueRangeSet<>();
    ni.setSeed(333);
    ni.addPartition(1, 2);
    boolean b1 = false;
    boolean b2 = false;
    for (int i = 0 ; i < 100 ; i++) {
      int n = (int)ni.ordered();
      if (n == 1) {
        b1 = true;
      }
      if (n == 2) {
        b2 = true;
      }
    }
    assertTrue("Should generate value 1", b1);
    assertTrue("Should generate value 2", b2);
  }

  @Test
  public void generics() {
    ValueRangeSet<Integer> vrs1 = new ValueRangeSet<>();
    vrs1.setSeed(333);
    vrs1.addPartition(1, 2);
    Integer o1 = vrs1.next();
    assertEquals("Integer value range should produce integers..", Integer.class, o1.getClass());

    ValueRangeSet<Long> vrs2 = new ValueRangeSet<>();
    vrs2.setSeed(333);
    vrs2.addPartition(Long.class, 1, 2);
    Long o2 = vrs2.next();
    assertEquals("Long value range should produce longs..", Long.class, o2.getClass());

    ValueRangeSet<Long> vrs3 = new ValueRangeSet<>();
    vrs3.setSeed(333);
    vrs3.addPartition(1l, 2l);
    Long o3 = vrs3.next();
    assertEquals("Long value range should produce longs..", Long.class, o3.getClass());

    ValueRangeSet<Double> vrs4 = new ValueRangeSet<>();
    vrs4.setSeed(333);
    vrs4.addPartition(Double.class, 1, 2);
    Double o4 = vrs4.next();
    assertEquals("Double value range should produce doubles..", Double.class, o4.getClass());

    ValueRangeSet<Double> vrs5 = new ValueRangeSet<>();
    vrs5.setSeed(333);
    vrs5.addPartition(1d, 2d);
    Double o5 = vrs5.next();
    assertEquals("Double value range should produce doubles..", Double.class, o5.getClass());
  }

  @Test
  public void boundaryScanningInteger() {
    ValueRangeSet<Integer> vr = new ValueRangeSet<>();
    vr.setSeed(333);
    vr.setStrategy(ValueRangeSet.LOOP);
    vr.addPartition(0, 100);
    vr.addPartition(-100, -50);
    vr.addPartition(200, 300);
    vr.addPartition(-300, -200);
    Collection<Integer> actual = new ArrayList<>();
    vr.setSeed(111);
    for (int i = 0 ; i < 30 ; i++) {
      actual.add(vr.boundaryIn());
    }
    String expected = "[0, -100, 200, -300, 100, -50, 300, -200, 1, -99, 201, -299, 99, -51, 299, -201, 2, -98, 202, -298, 98, -52, 298, -202, 3, -97, 203, -297, 97, -53]";
    assertEquals("Generated integers for value range with boundary scan", expected, actual.toString());
  }

  @Test
  public void boundaryScanningInvalidInteger() {
    ValueRangeSet<Integer> vr = new ValueRangeSet<>();
    vr.setSeed(333);
    vr.setStrategy(ValueRangeSet.LOOP);
    vr.addPartition(0, 100);
    vr.addPartition(-100, -50);
    vr.addPartition(200, 300);
    vr.addPartition(-300, -200);
    vr.setSeed(111);
    Collection<Integer> actual = new ArrayList<>();
    for (int i = 0 ; i < 30 ; i++) {
      actual.add(vr.boundaryOut());
    }
    String expected = "[101, -49, 301, -199, -1, -101, 199, -301, 102, -48, 302, -198, -2, -102, 198, -302, 103, -47, 303, -197, -3, -103, 197, -303, 104, -46, 304, -196, -4, -104]";
    assertEquals("Generated integers for value range with boundary scan", expected, actual.toString());
  }

  @Test
  public void boundaryScanningFloat() {
    ValueRangeSet<Double> vr = new ValueRangeSet<>();
    vr.setSeed(333);
    vr.setStrategy(ValueRangeSet.LOOP);
    vr.addPartition(0f, 100f);
    vr.addPartition(-100f, -50f);
    vr.addPartition(200f, 300f);
    vr.addPartition(-300f, -200f);
    vr.setIncrement(0.1f);
    vr.setSeed(111);
    //the valuerangeset actually converts float to double
    double[] expected = new double[]{0, -100, 200, -300, 100, -50, 300, -200, 0.1, -99.9, 200.1, -299.9, 99.9, -50.1, 299.9, -200.1, 0.2, -99.8, 200.2, -299.8, 99.8, -50.2, 299.8, -200.2, 0.3, -99.7, 200.3, -299.7, 99.7, -50.3};
    for (int i = 0 ; i < 30 ; i++) {
      Double actual = vr.boundaryIn();
      assertEquals("Generated integers for value range with boundary scan (index " + i + ")", expected[i], actual, 0.01d);
    }
  }


  private void assertValues(ValueRangeSet<Double> range, double... expected) {
    for (double i : expected) {
      assertEquals(i, range.next());
    }
  }
}

package osmo.tester.model.dataflow;

import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValueRangeTests {
  @Test
  public void optimizedRandomValueRange() {
    ValueRange vr = new ValueRange(5, 7);
    vr.setStrategy(DataGenerationStrategy.BALANCING);
    boolean b5 = false;
    boolean b6 = false;
    boolean b7 = false;
    for (int a = 0; a < 10; a++) {
      for (int i = 0; i < 3; i++) {
        int n = vr.nextInt();
        if (n == 5) {
          b5 = true;
        }
        if (n == 6) {
          b6 = true;
        }
        if (n == 7) {
          b7 = true;
        }
      }
      assertTrue("Should generate value 5 (loop " + a + ")", b5);
      assertTrue("Should generate value 6 (loop \"+a+\")", b6);
      assertTrue("Should generate value 7 (loop \"+a+\")", b7);
    }
  }

  @Test
  public void orderedLoopValueRange() {
    ValueRange vr = new ValueRange(5, 7);
    vr.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    assertEquals("First item", 5, vr.nextInt());
    assertEquals("Second item", 6, vr.nextInt());
    assertEquals("Third item", 7, vr.nextInt());
    assertEquals("Fourth item", 5, vr.nextInt());
  }

  @Test
  public void generics() {
    ValueRange<Integer> vr = new ValueRange<>(1, 5);
    Object o = vr.next();
    assertEquals("Integer value range should produce integers..", Integer.class, o.getClass());

    ValueRange<Long> vr2 = new ValueRange<>(1l, 5l);
    Object o2 = vr2.next();
    assertEquals("Long value range should produce longs..", Long.class, o2.getClass());

    ValueRange<Double> vr3 = new ValueRange<>(1d, 5d);
    Object o3 = vr3.next();
    assertEquals("Double value range should produce doubles..", Double.class, o3.getClass());

    ValueRange<Integer> vr4 = new ValueRange<>(Integer.class, 1, 5);
    Object o4 = vr4.next();
    assertEquals("Integer value range should produce integers..", Integer.class, o4.getClass());

    ValueRange<Long> vr5 = new ValueRange<>(Long.class, 1, 5);
    Object o5 = vr5.next();
    assertEquals("Long value range should produce longs..", Long.class, o5.getClass());

    ValueRange<Double> vr6 = new ValueRange<>(Double.class, 1, 5);
    Object o6 = vr6.next();
    assertEquals("Double value range should produce doubles..", Double.class, o6.getClass());
  }

  @Test
  public void boundaryScanDefault() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    assertValues(range, 10, 20, 11, 19, 12, 18, 13, 17, 14, 16, 15, 15, 10, 20, 11, 19);
  }

  @Test
  public void boundaryScanRange0() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    range.setCount(0);
    assertValues(range, 10, 20, 10, 20);
  }

  @Test
  public void boundaryScanRange0Invalid() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN_INVALID);
    range.setCount(0);
    try {
      range.next();
      fail("Invalid scan with 0 size boundary count should fail");
    } catch (IllegalStateException e) {
      //there is nothing to provide with 0 configuration for an invalid boundary, since the boundary itself is considered valid
    }
  }

  @Test
  public void boundaryScanRange1() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    range.setCount(1);
    assertValues(range, 10, 20, 11, 19, 10, 20, 11, 19, 10, 20);
  }

  @Test
  public void boundaryScanNegativeRange() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    try {
      range.setCount(-1);
      fail("Boundary scan with negative range should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void boundaryScanDefaultInvalid() {
    ValueRange<Integer> range = new ValueRange<>(10, 20);
    range.setStrategy(DataGenerationStrategy.BOUNDARY_SCAN_INVALID);
    assertValues(range, 21, 9, 22, 8, 23, 7, 24, 6, 25, 5, 21, 9);
  }

  private void assertValues(ValueRange<Integer> range, int... expected) {
    for (int i : expected) {
      assertEquals(i, range.next().intValue());
    }
  }

  @Test
  public void getOptions() {
    ValueRange<Integer> range = new ValueRange<>(1, 5);
    Collection<Integer> options = range.getOptions();
    assertEquals("Options for range 1-5", "[1, 2, 3, 4, 5]", options.toString());
  }

  @Test
  public void deSerializeLong() {
    ValueRange<Long> range = new ValueRange<>(946677600000l, 1293832799000l);
    assertTrue("Should be in range", range.evaluateSerialized("1121959683153"));
  }
}

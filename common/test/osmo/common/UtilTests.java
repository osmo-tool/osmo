package osmo.common;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static osmo.common.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class UtilTests {
  @Test
  public void objectArrayOneOf() {
    String[] array = {"s1", "s2", "s3"};
    boolean s1 = false;
    boolean s2 = false;
    boolean s3 = false;
    for (int i = 0 ; i < 50 ; i++) {
      String s = oneOf(array);
      switch (s) {
        case "s1":
          s1 = true;
          break;
        case "s2":
          s2 = true;
          break;
        case "s3":
          s3 = true;
          break;
        default:
          fail("Unexpected value generated (should be one of 's1', 's2', 's3'):" + s);
          break;
      }
    }
    assertTrue("String 's1' should be generated", s1);
    assertTrue("String 's2' should be generated", s2);
    assertTrue("String 's3' should be generated", s3);
  }

  @Test
  public void intArrayOneOf() {
    int[] array = {-1, 5, 10};
    boolean i1 = false;
    boolean i2 = false;
    boolean i3 = false;
    for (int a = 0 ; a < 50 ; a++) {
      int i = oneOf(array);
      if (i == -1) {
        i1 = true;
      } else if (i == 5) {
        i2 = true;
      } else if (i == 10) {
        i3 = true;
      } else {
        Assert.fail("Unexpected value generated (should be one of -1, 5, 10):" + i);
      }
    }
    assertTrue("Integer -1 should be generated", i1);
    assertTrue("Integer 5 should be generated", i2);
    assertTrue("Integer 10 should be generated", i3);
  }

  @Test
  public void createInt() {
    Map<Integer, Integer> values = new HashMap<>();
    for (int x = 0 ; x < 1000 ; x++) {
      int i = cInt(0, 9);
      Integer count = values.get(i);
      if (count == null) {
        count = 0;
      }
      values.put(i, count + 1);
    }
    assertEquals("10 values should be generated for 0-9", 10, values.values().size());
    for (int i = 0 ; i < 10 ; i++) {
      Integer count = values.get(i);
      assertNotNull("All values between 0-9 should be generated (" + i + ")", count);
      assertTrue("Generated integers for " + i + " should be in 25% range (75-125), was:" + count, count > 75 && count < 125);
    }
  }

  @Test
  public void createLong() {
    Map<Long, Integer> values = new HashMap<>();
    for (int x = 0; x < 1000; x++) {
      long i = cLong(11111111110l, 11111111119l);
      Integer count = values.get(i);
      if (count == null) {
        count = 0;
      }
      values.put(i, count + 1);
    }
    assertEquals("10 values should be generated for 11111111110l-11111111119l", 10, values.values().size());
    for (long i = 11111111110l; i < 11111111120l; i++) {
      Integer count = values.get(i);
      assertNotNull("All values between 0-9 should be generated (" + i + ")", count);
      assertTrue("Generated longs for " + i + " should be in 25% range (75-125), was:" + count, count > 75 && count < 125);
    }
  }

  @Test
  public void createByte() {
    Map<Byte, Integer> values = new HashMap<>();
    for (int x = 0; x < 1000; x++) {
      byte i = cByte((byte) -15, (byte) -6);
      Integer count = values.get(i);
      if (count == null) {
        count = 0;
      }
      values.put(i, count + 1);
    }
    assertEquals("4 values should be generated for -15 to -6", 10, values.values().size());
    for (byte i = -15; i < -6; i++) {
      Integer count = values.get(i);
      assertNotNull("All values between -15 and -6 should be generated (" + i + ")", count);
      assertTrue("Generated bytes for " + i + " should be in 25% range (75-125), was:" + count, count > 75 && count < 125);
    }
  }

  @Test
  public void createDouble() {
    Collection<Double> values = new ArrayList<>();
    for (int x = 0; x < 1000; x++) {
      double i = cDouble(5d, 6d);
      values.add(i);
      assertTrue("Value should be between 5-6", i > 5 && i < 6);
    }
    assertTrue("Over 500 different values expected (" + values.size() + ")", values.size() > 500);
  }

  @Test
  public void createFloat() {
    Collection<Float> values = new ArrayList<>();
    for (int x = 0; x < 1000; x++) {
      float i = cFloat(5f, 6f);
      values.add(i);
      assertTrue("Value should be between 5-6", i > 5 && i < 6);
    }
    assertTrue("Over 500 different values expected (" + values.size() + ")", values.size() > 500);
  }

  @Test
  public void weightedRandomChoice() {
    List<Integer> weights = new ArrayList<>();
    weights.add(1);
    weights.add(2);
    weights.add(3);
    int zeroes = 0;
    int ones = 0;
    int twos = 0;
    for (int i = 0 ; i < 1000 ; i++) {
      int index = rawWeightedRandomFrom(weights);
      switch (index) {
        case 0:
          zeroes++;
          break;
        case 1:
          ones++;
          break;
        case 2:
          twos++;
          break;
        default:
          fail("Invalid index from weighted random (0-2 allowed):"+index);
      }
    }
    assertEquals("Should have created equal number of instances to loop size", 1000, zeroes + ones + twos);
    double zp = zeroes/1000d;
    double op = ones/1000d;
    double tp = twos/1000d;
    assertEquals("Proportion of zeroes generated", 0.165d, zp, 0.02d);
    assertEquals("Proportion of ones generated", 0.333d, op, 0.03d);
    assertEquals("Proportion of twos generated", 0.500d, tp, 0.05d);
  }

  @Test
  public void weightedRandomChoiceWithDuplicates() {
    List<Integer> weights = new ArrayList<>();
    weights.add(1);
    weights.add(2);
    weights.add(2);
    weights.add(3);
    int zeroes = 0;
    int ones = 0;
    int twos = 0;
    int threes = 0;
    for (int i = 0 ; i < 1000 ; i++) {
      int index = rawWeightedRandomFrom(weights);
      switch (index) {
        case 0:
          zeroes++;
          break;
        case 1:
          ones++;
          break;
        case 2:
          twos++;
          break;
        case 3:
          threes++;
          break;
        default:
          fail("Invalid index from weighted random (0-2 allowed):" + index);
      }
    }
    assertEquals("Should have created equal number of instances to loop size", 1000, zeroes + ones + twos + threes);
    double zp = zeroes / 1000d;
    double op = ones / 1000d;
    double tp = twos / 1000d;
    double thp = threes / 1000d;
    assertEquals("Proportion of zeroes generated", 0.125d, zp, 0.02d);
    assertEquals("Proportion of ones generated", 0.250d, op, 0.04d);
    assertEquals("Proportion of twos generated", 0.250d, tp, 0.04d);
    assertEquals("Proportion of threes generated", 0.375d, thp, 0.04d);
  }

  @Test
  public void weightedRandomChoiceAllZeros() {
    List<Integer> weights = new ArrayList<>();
    weights.add(0);
    weights.add(0);
    weights.add(0);
    try {
      rawWeightedRandomFrom(weights);
      fail("Weight 0 should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Weight must be > 0. Was 0.", e.getMessage());
    }
  }

  @Test
  public void weightedRandomChoiceAllZeroes() {
    List<Integer> weights = new ArrayList<>();
    weights.add(0);
    weights.add(0);
    weights.add(0);
    try {
      int index = rawWeightedRandomFrom(weights);
      fail("Weight 0 should not be allowed");
    } catch (Exception e) {
      //expected
    }
  }
}

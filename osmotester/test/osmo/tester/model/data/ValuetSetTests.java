package osmo.tester.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValuetSetTests {
  private ValueSet<String> set = null;

  @Before
  public void setup() {
    set = new ValueSet<>();
    set.setSeed(333);
    set.add("one");
    set.add("two");
    set.add("three");
  }

  @Test
  public void orderedTest() {
    set.add("one");
    set.add("two");
    set.add("three");
    assertEquals("one", set.ordered());
    assertEquals("two", set.ordered());
    assertEquals("three", set.ordered());
    assertEquals("one", set.ordered());
    assertEquals("two", set.ordered());
    assertEquals("three", set.ordered());
  }

  @Test
  public void randomizedTest() {
    String v1 = set.random();
    String v2 = set.random();
    String v3 = set.random();
    assertTrue(v1.equals("one") || v1.equals("two") || v1.equals("three"));
    assertTrue(v2.equals("one") || v2.equals("two") || v2.equals("three"));
    assertTrue(v3.equals("one") || v3.equals("two") || v3.equals("three"));
    boolean fail = true;
    for (int i = 0 ; i < 10 ; i++) {
      String v1_2 = set.random();
      String v2_2 = set.random();
      String v3_2 = set.random();
      if (!v1_2.equals(v1) || !v2_2.equals(v2) || !v3_2.equals(v3)) {
        fail = false;
        break;
      }
    }
    assertFalse("Random generation should be random, now it seems not to be.", fail);
  }

  @Test
  public void balancingTestOneLoop() {
    String v6 = null;
    boolean diff = false;
    Random seed = new Random();
    for (int i = 0 ; i < 10 ; i++) {
      set = new ValueSet<>();
      set.setSeed(seed.nextLong());
      set.add("one");
      set.add("two");
      set.add("three");
      set.add("four");
      set.add("five");
      String reference = generateAndCheck();
      //make sure the generated values are in different order at least in once case
      if (v6 == null) {
        v6 = reference;
      } else if (!v6.equals(reference)) {
        diff = true;
      }
    }
    assertTrue("10 optimized random generations should produce different results.", diff);
  }

  @Test
  public void balancingTestSeveralLoops() {
    String v6 = null;
    boolean diff = false;
    set = new ValueSet<>();
    set.setSeed(333);
    set.add("one");
    set.add("two");
    set.add("three");
    set.add("four");
    set.add("five");
    for (int i = 0 ; i < 10 ; i++) {
      String reference = generateAndCheck();
      //make sure the generated values are in different order at least in once case
      if (v6 == null) {
        v6 = reference;
      } else if (!v6.equals(reference)) {
        diff = true;
      }
    }
    assertTrue("10 balancing generations should produce different results.", diff);
  }

  private String generateAndCheck() {
    String v1 = set.balanced();
    String v2 = set.balanced();
    String v3 = set.balanced();
    String v4 = set.balanced();
    String v5 = set.balanced();
    boolean match = v1.equals(v2) || v1.equals(v3) || v1.equals(v4) || v1.equals(v5);
    match = match || v2.equals(v3) || v2.equals(v4) || v2.equals(v5) || v3.equals(v4) || v3.equals(v5) || v4.equals(v5);
    assertFalse("Balanced should generate random values but always different when uncovered options exist:" + v1 + v2 + v3 + v4 + v5, match);
    return v1 + v2 + v3 + v4 + v5;
  }

  @Test
  public void addAndRemoveOrderedTest() {
    assertEquals("one", set.ordered());
    assertEquals("two", set.ordered());
    set.remove("one");
    assertEquals("three", set.ordered());
    set.add("four");
    set.add("five");
    assertEquals("four", set.ordered());
    set.remove("three");
    assertEquals("five", set.ordered());
    assertEquals("two", set.ordered());
  }

  @Test
  public void weightedAddWithRandom() {
    set.setSeed(333);
    set.add("four", 3);
    int[] freqs = frequencyCount();
    assertEquals("Number of 'ones' generated", 97, freqs[1]);
    assertEquals("Number of 'twos' generated", 106, freqs[2]);
    assertEquals("Number of 'threes' generated", 109, freqs[3]);
    assertEquals("Number of 'fours' generated", 288, freqs[4]);
  }
  
  private int[] frequencyCount() {
    int[] result = new int[5];
    for (int i = 0 ; i < 600 ; i++) {
      String next = set.random();
      switch (next) {
        case "one":
          result[1]++;
          break;
        case "two":
          result[2]++;
          break;
        case "three":
          result[3]++;
          break;
        case "four":
          result[4]++;
          break;
        default:
          throw new IllegalArgumentException("Invalid value received from valueset (allowed 'one', 'two', 'three', 'four'):" + next);
      }
    }
    return result;
  }

  @Test
  public void weightedAddWithLoop() {
    set.add("four", 3);
    assertEquals("one", set.ordered());
    assertEquals("two", set.ordered());
    assertEquals("three", set.ordered());
    assertEquals("four", set.ordered());
    assertEquals("four", set.ordered());
    assertEquals("four", set.ordered());
    assertEquals("one", set.ordered());
  }
  
  @Test
  public void bookRandom() {
    String booked = set.bookRandom();
    assertEquals("Random booked", "three", booked);
    int[] freqs = frequencyCount();
    assertEquals("Number of 'ones' generated", 294, freqs[1]);
    assertEquals("Number of 'twos' generated", 306, freqs[2]);
    assertEquals("Number of 'threes' generated", 0, freqs[3]);
    assertEquals("Number of 'fours' generated", 0, freqs[4]);
  }
}

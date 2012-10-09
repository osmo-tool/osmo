package osmo.tester.model.dataflow;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValuetSetTests {
  private ValueSet<String> set = null;

  @Before
  public void setup() {
    set = new ValueSet<>();
    set.add("one");
    set.add("two");
    set.add("three");
  }

  @Test
  public void orderedTest() {
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.add("one");
    set.add("two");
    set.add("three");
    assertEquals("one", set.next());
    assertEquals("two", set.next());
    assertEquals("three", set.next());
    assertEquals("one", set.next());
    assertEquals("two", set.next());
    assertEquals("three", set.next());
  }

  @Test
  public void randomizedTest() {
    //leaving this out also tests the default behaviour and expectations for that
//    set.setStrategy(GenerationStrategy.RANDOM);
    String v1 = set.next();
    String v2 = set.next();
    String v3 = set.next();
    assertTrue(v1.equals("one") || v1.equals("two") || v1.equals("three"));
    assertTrue(v2.equals("one") || v2.equals("two") || v2.equals("three"));
    assertTrue(v3.equals("one") || v3.equals("two") || v3.equals("three"));
    boolean fail = true;
    for (int i = 0; i < 10; i++) {
      String v1_2 = set.next();
      String v2_2 = set.next();
      String v3_2 = set.next();
      if (!v1_2.equals(v1) || !v2_2.equals(v2) || !v3_2.equals(v3)) {
        fail = false;
        break;
      }
    }
    assertFalse("Random generation should be random, now it seems not to be.", fail);
  }

  @Test
  public void optimizedRandomTestOneLoop() {
    String v6 = null;
    boolean diff = false;
    for (int i = 0; i < 10; i++) {
      set = new ValueSet<>();
      set.setStrategy(DataGenerationStrategy.BALANCING);
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
  public void optimizedRandomTestSeveralLoops() {
    String v6 = null;
    boolean diff = false;
    set = new ValueSet<>();
    set.setStrategy(DataGenerationStrategy.BALANCING);
    set.add("one");
    set.add("two");
    set.add("three");
    set.add("four");
    set.add("five");
    for (int i = 0; i < 10; i++) {
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

  private String generateAndCheck() {
    String v1 = set.next();
    String v2 = set.next();
    String v3 = set.next();
    String v4 = set.next();
    String v5 = set.next();
    boolean match = v1.equals(v2) || v1.equals(v3) || v1.equals(v4) || v1.equals(v5);
    match = match || v2.equals(v3) || v2.equals(v4) || v2.equals(v5) || v3.equals(v4) || v3.equals(v5) || v4.equals(v5);
    assertFalse("Optimized random should generate random values but always different when uncovered options exist:" + v1 + v2 + v3 + v4 + v5, match);
    return v1 + v2 + v3 + v4 + v5;
  }

  @Test
  public void evaluationTest() {
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    assertTrue("Should find \"one\" in the set of objects.", set.evaluate("one"));
    assertTrue("Should find \"two\" in the set of objects.", set.evaluate("two"));
    assertTrue("Should find \"three\" in the set of objects.", set.evaluate("three"));
    assertFalse("Should not find \"four\" in the set of objects.", set.evaluate("four"));
  }

  @Test
  public void addAndRemoveOrderedTest() {
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    assertEquals("one", set.next());
    assertEquals("two", set.next());
    set.remove("one");
    assertEquals("three", set.next());
    set.add("four");
    set.add("five");
    assertEquals("four", set.next());
    set.remove("three");
    assertEquals("five", set.next());
    assertEquals("two", set.next());
  }
  
  @Test
  public void weightedAddWithRandom() {
    TestUtils.setSeed(333);
    set.setStrategy(DataGenerationStrategy.RANDOM);
    int ones = 0;
    int twos = 0;
    int threes = 0;
    int fours = 0;
    set.add("four", 3);
    for (int i = 0 ; i < 600 ; i++) {
      String next = set.next();
      switch (next) {
        case "one":
          ones++;
          break;
        case "two":
          twos++;
          break;
        case "three":
          threes++;
          break;
        case "four":
          fours++;
          break;
        default:
          throw new IllegalArgumentException("Invalid value received from valueset (allowed 'one', 'two', 'three', 'four'):"+next);
      }
    }
    assertEquals("Number of 'ones' generated", 98, ones);
    assertEquals("Number of 'twos' generated", 107, twos);
    assertEquals("Number of 'threes' generated", 106, threes);
    assertEquals("Number of 'fours' generated", 289, fours);
  }

  @Test
  public void weightedAddWithLoop() {
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.add("four", 3);
    assertEquals("one", set.next());
    assertEquals("two", set.next());
    assertEquals("three", set.next());
    assertEquals("four", set.next());
    assertEquals("four", set.next());
    assertEquals("four", set.next());
    assertEquals("one", set.next());
  }
}

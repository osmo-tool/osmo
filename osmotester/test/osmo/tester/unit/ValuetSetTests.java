package osmo.tester.unit;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueSet;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValuetSetTests {
  private ValueSet<String> inv = null;

  @Before
  public void setup() {
    inv = new ValueSet<String>();
    inv.add("one");
    inv.add("two");
    inv.add("three");
  }

  @Test
  public void orderedTest() {
    inv.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    inv.add("one");
    inv.add("two");
    inv.add("three");
    assertEquals("one", inv.next());
    assertEquals("two", inv.next());
    assertEquals("three", inv.next());
    assertEquals("one", inv.next());
    assertEquals("two", inv.next());
    assertEquals("three", inv.next());
  }

  @Test
  public void randomizedTest() {
    //leaving this out also tests the default behaviour and expectations for that
//    inv.setStrategy(GenerationStrategy.RANDOM);
    String v1 = inv.next();
    String v2 = inv.next();
    String v3 = inv.next();
    assertTrue(v1.equals("one") || v1.equals("two") || v1.equals("three"));
    assertTrue(v2.equals("one") || v2.equals("two") || v2.equals("three"));
    assertTrue(v3.equals("one") || v3.equals("two") || v3.equals("three"));
    boolean fail = true;
    for (int i = 0 ; i < 10 ; i++) {
      String v1_2 = inv.next();
      String v2_2 = inv.next();
      String v3_2 = inv.next();
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
    for (int i = 0 ; i < 10 ; i++) {
      inv = new ValueSet<String>();
      inv.setStrategy(DataGenerationStrategy.LESS_RANDOM);
      inv.add("one");
      inv.add("two");
      inv.add("three");
      inv.add("four");
      inv.add("five");
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
    inv = new ValueSet<String>();
    inv.setStrategy(DataGenerationStrategy.LESS_RANDOM);
    inv.add("one");
    inv.add("two");
    inv.add("three");
    inv.add("four");
    inv.add("five");
    for (int i = 0 ; i < 10 ; i++) {
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
    String v1 = inv.next();
    String v2 = inv.next();
    String v3 = inv.next();
    String v4 = inv.next();
    String v5 = inv.next();
    boolean match = v1.equals(v2) || v1.equals(v3) || v1.equals(v4) || v1.equals(v5);
    match = match || v2.equals(v3) || v2.equals(v4) || v2.equals(v5) || v3.equals(v4) || v3.equals(v5) || v4.equals(v5);
    assertFalse("Optimized random should generate random values but always different when uncovered options exist:" + v1 + v2 + v3 + v4 + v5, match);
    return v1 + v2 + v3 + v4 + v5;
  }

  @Test
  public void evaluationTest() {
    inv.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    assertTrue("Should find \"one\" in the set of objects.", inv.evaluate("one"));
    assertTrue("Should find \"two\" in the set of objects.", inv.evaluate("two"));
    assertTrue("Should find \"three\" in the set of objects.", inv.evaluate("three"));
    assertFalse("Should not find \"four\" in the set of objects.", inv.evaluate("four"));
  }

  @Test
  public void addAndRemoveOrderedTest() {
    inv.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    assertEquals("one", inv.next());
    assertEquals("two", inv.next());
    inv.remove("one");
    assertEquals("three", inv.next());
    inv.add("four");
    inv.add("five");
    assertEquals("four", inv.next());
    inv.remove("three");
    assertEquals("five", inv.next());
    assertEquals("two", inv.next());
  }

}

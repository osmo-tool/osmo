package osmo.tester.unit;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.model.dataflow.GenerationStrategy;
import osmo.tester.model.dataflow.ObjectSet;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ObjectSetTests {
  private ObjectSet<String> inv = null;

  @Before
  public void setup() {
    inv = new ObjectSet<String>();
    inv.addOption("one");
    inv.addOption("two");
    inv.addOption("three");
  }

  @Test
  public void orderedTest() {
    inv.setStrategy(GenerationStrategy.ORDERED_LOOP);
    inv.addOption("one");
    inv.addOption("two");
    inv.addOption("three");
    assertEquals("one", inv.input());
    assertEquals("two", inv.input());
    assertEquals("three", inv.input());
    assertEquals("one", inv.input());
    assertEquals("two", inv.input());
    assertEquals("three", inv.input());
  }

  @Test
  public void randomizedTest() {
    //leaving this out also tests the default behaviour and expectations for that
//    inv.setStrategy(GenerationStrategy.RANDOM);
    String v1 = inv.input();
    String v2 = inv.input();
    String v3 = inv.input();
    assertTrue(v1.equals("one") || v1.equals("two") || v1.equals("three"));
    assertTrue(v2.equals("one") || v2.equals("two") || v2.equals("three"));
    assertTrue(v3.equals("one") || v3.equals("two") || v3.equals("three"));
    boolean fail = true;
    for (int i = 0 ; i < 10 ; i++) {
      String v1_2 = inv.input();
      String v2_2 = inv.input();
      String v3_2 = inv.input();
      if (!v1_2.equals(v1) || !v2_2.equals(v2) || !v3_2.equals(v3)) {
        fail = false;
        break;
      }
    }
    assertFalse("Random generation should be random, now it seems not to be.", fail);
  }

  @Test
  public void optimizedRandomTest() {
    String v6 = null;
    boolean diff = false;
    for (int i = 0 ; i < 10 ; i++) {
      inv = new ObjectSet<String>();
      inv.setStrategy(GenerationStrategy.OPTIMIZED_RANDOM);
      inv.addOption("one");
      inv.addOption("two");
      inv.addOption("three");
      inv.addOption("four");
      inv.addOption("five");
      String v1 = inv.input();
      String v2 = inv.input();
      String v3 = inv.input();
      String v4 = inv.input();
      String v5 = inv.input();
      boolean match = v1.equals(v2) || v1.equals(v3) || v1.equals(v4) || v1.equals(v5);
      match = match || v2.equals(v3) || v2.equals(v4) || v2.equals(v5) || v3.equals(v4) || v3.equals(v5) || v4.equals(v5);
      assertFalse("Optimized random should generate random values but always different when uncovered options exist:"+v1+v2+v3+v4+v5, match);
      String reference = v1+v2+v3+v4+v5;
      if (v6 == null) {
        v6 = reference;
      } else if (!v6.equals(reference)) {
        diff = true;
      }
    }
    assertTrue("10 optimized random generations should produce different results.", diff);
  }

  @Test
  public void evaluationTest() {
    inv.setStrategy(GenerationStrategy.ORDERED_LOOP);
    assertTrue("Should find \"one\" in the set of objects.", inv.evaluate("one"));
    assertTrue("Should find \"two\" in the set of objects.", inv.evaluate("two"));
    assertTrue("Should find \"three\" in the set of objects.", inv.evaluate("three"));
    assertFalse("Should not find \"four\" in the set of objects.", inv.evaluate("four"));
  }

  @Test
  public void addAndRemoveOrderedTest() {
    inv.setStrategy(GenerationStrategy.ORDERED_LOOP);
    assertEquals("one", inv.input());
    assertEquals("two", inv.input());
    inv.removeOption("one");
    assertEquals("three", inv.input());
    inv.addOption("four");
    inv.addOption("five");
    assertEquals("four", inv.input());
    inv.removeOption("three");
    assertEquals("five", inv.input());
    assertEquals("two", inv.input());
  }

}

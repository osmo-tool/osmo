package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Teemu Kanstren
 */
public class ValueRangeTests {
  @Test
  public void optimizedRandomValueRange() {
    ValueRange vr = new ValueRange(5, 7);
    vr.setAlgorithm(DataGenerationStrategy.OPTIMIZED_RANDOM);
    boolean b5 = false;
    boolean b6 = false;
    boolean b7 = false;
    for (int a = 0 ; a < 10 ; a++) {
      for (int i = 0 ; i < 3 ; i++) {
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
      assertTrue("Should generate value 5 (loop "+a+")", b5);
      assertTrue("Should generate value 6 (loop \"+a+\")", b6);
      assertTrue("Should generate value 7 (loop \"+a+\")", b7);
    }
  }

  @Test
  public void orderedLoopValueRange() {
    ValueRange vr = new ValueRange(5, 7);
    vr.setAlgorithm(DataGenerationStrategy.ORDERED_LOOP);
    assertEquals("First item", 5, vr.nextInt());
    assertEquals("Second item", 6, vr.nextInt());
    assertEquals("Third item", 7, vr.nextInt());
    assertEquals("Fourth item", 5, vr.nextInt());
  }
}

package osmo.tester.unit;

import org.junit.Test;

import static org.junit.Assert.*;
import static osmo.tester.TestUtils.*;

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
      if (s.equals("s1")) {
        s1 = true;
      } else if (s.equals("s2")) {
        s2 = true;
      } else if (s.equals("s3")) {
        s3 = true;
      } else {
        fail("Unexpected value generated (should be one of 's1', 's2', 's3'):"+s);
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
        fail("Unexpected value generated (should be one of -1, 5, 10):"+i);
      }
    }
    assertTrue("Integer -1 should be generated", i1);
    assertTrue("Integer 5 should be generated", i2);
    assertTrue("Integer 10 should be generated", i3);
  }
}

package osmo.common;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren.
 */
public class StringReplacementTests {
  @Test
  public void untilOne() {
    String[] result = TestUtils.replace("##", "hello [##UNTIL]##", "hello [teemu]");
    assertEquals("Replace one string", "hello []", result[0]);
    assertEquals("Replace one string", "hello []", result[1]);
  }

  @Test
  public void untilNone() {
    String[] result = TestUtils.replace("##", "hello teemu", "hello [teemu]");
    assertEquals("Replace string with no match", "hello [teemu]", result[0]);
    assertEquals("Replace string with no match", "hello teemu", result[1]);
  }

  @Test
  public void mismatch() {
    String[] result = TestUtils.replace("##", "hello ##UNTILteemu", "hello [teemu]");
    assertEquals("Replace mismatching string", "hello [teemu]", result[0]);
    assertEquals("Replace mismatching string", "hello ##UNTILteemu", result[1]);
  }

  @Test
  public void several() {
    String[] result = TestUtils.replace("##", "hello [##UNTIL]## [##UNTIL]##", "hello [teemu] [terve]");
    assertEquals("Replace several strings", "hello [] []", result[0]);
    assertEquals("Replace several strings", "hello [] []", result[1]);
  }
}

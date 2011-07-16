package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.model.dataflow.ReadableCharSet;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ReadableCharSetTests {
  @Test
  public void samplesFound() {
    ReadableCharSet set = new ReadableCharSet();
    boolean lower = false;
    boolean upper = false;
    boolean special = false;
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.next();
      lower = Character.isLowerCase(c) || lower;
      upper = Character.isUpperCase(c) || upper;
      special = special || c == ' ' || c == '-' || c == '<' || c == '>' || c == '=' || c == '?' || c == '[' || c == ']';
    }
    assertTrue("Should have generated lower case character", lower);
    assertTrue("Should have generated upper case character", upper);
    assertTrue("Should have generated special character", special);
  }

  @Test
  public void evaluation() {
    ReadableCharSet set = new ReadableCharSet();
    assertTrue("'a' should be in the set.", set.evaluate('a'));
    assertTrue("'A' should be in the set.", set.evaluate('A'));
    assertTrue("'-' should be in the set.", set.evaluate('-'));
    assertTrue("'=' should be in the set.", set.evaluate('='));
  }
}

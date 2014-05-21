package osmo.tester.unittests.model.data;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.model.data.CharSet;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CharSetTests {
  private CharSet set = null;

  @Before
  public void setUp() throws Exception {
    set = new CharSet();
    set.setSeed(444);
  }

  @Test
  public void charTypesGenerated() {
    boolean lower = false;
    boolean upper = false;
    boolean special = false;
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.random();
      lower = Character.isLowerCase(c) || lower;
      upper = Character.isUpperCase(c) || upper;
      special = special || c == ' ' || c == '-' || c == '<' || c == '>' || c == '=' || c == '?' || c == '[' || c == ']';
    }
    assertTrue("Should have generated lower case character", lower);
    assertTrue("Should have generated upper case character", upper);
    assertTrue("Should have generated special character", special);
  }

  @Test
  public void charEvaluation() {
    assertTrue("'a' should be in the set.", set.evaluate('a'));
    assertTrue("'A' should be in the set.", set.evaluate('A'));
    assertTrue("'-' should be in the set.", set.evaluate('-'));
    assertTrue("'=' should be in the set.", set.evaluate('='));
  }

  @Test
  public void reduceWithWhiteSpace() {
    set.reduceBy(" \t");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.random();
      assertFalse("Should not contain removed chars, has " + c, c == ' ' || c == '\t');
    }
  }

  @Test
  public void reduceWithXml() {
    set.reduceBy("<>");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.random();
      assertFalse("Should not contain removed chars, has " + c, c == '<' || c == '>');
    }
  }

  @Test
  public void invalidLoop() {
    Collection<Integer> expected = invalidExpected();
    for (int i = 0 ; i < 200 ; i++) {
      char c = set.invalidLoop();
      expected.remove((int) c);
    }
    assertEquals("Number of uncovered char codes from invalid loop", 0, expected.size());
  }

  @Test
  public void invalidRandom() {
    Collection<Integer> expected = invalidExpected();
    for (int i = 0 ; i < 2000 ; i++) {
      char c = set.invalidRandom();
      expected.remove((int) c);
    }
    assertEquals("Number of uncovered char codes from invalid random", 0, expected.size());
  }

  private Collection<Integer> invalidExpected() {
    Collection<Integer> expected = new ArrayList<>();
    for (int i = 0 ; i <= 32 ; i++) {
      expected.add(i);
    }
    for (int i = 127 ; i <= 258 ; i++) {
      expected.add(i);
    }
    return expected;
  }

  @Test
  public void loopingValid() {
    set.asciiLettersAndNumbersOnly();
    assertEquals("First loop char", 'a', (char) set.loop());
    assertEquals("Second loop char", 'b', (char) set.loop());
    for (int i = 0 ; i < 58 ; i++) {
      char c = set.loop();
    }
    assertEquals("60th loop char", '8', (char) set.loop());
    assertEquals("61st loop char", '9', (char) set.loop());
    assertEquals("62nd loop char", 'a', (char) set.loop());
  }

  @Test
  public void asciiInvalid() {
    set.asciiLettersAndNumbersOnly();
    Collection<Integer> expected = invalidExpected();
    String added = "åäöÅÄÖ,.<>!\"#%&/()=?´`{[]}\\¨^~';:|-_*-+= ";
    char[] chars = added.toCharArray();
    for (char c : chars) {
      expected.add((int) c);
    }
    for (int i = 0 ; i < 300 ; i++) {
      char c = set.invalidLoop();
      expected.remove((int) c);
    }
    assertEquals("Number of uncovered char codes from invalid random with ascii set", 0, expected.size());
  }

  @Test
  public void reduction() {
    set.asciiLettersAndNumbersOnly();
    set.reduceBy("abcde");
    for (int i = 0 ; i < 100 ; i++) {
      char c = set.loop();
      String msg = "Letters 'abcde' should not be generated after reduction, was: " + c;
      assertTrue(msg, 'a' != c && 'b' != c && 'c' != c && 'd' != c && 'e' != c);
    }
    boolean a = false;
    boolean b = false;
    boolean c = false;
    boolean d = false;
    boolean e = false;
    for (int i = 0 ; i < 300 ; i++) {
      char ch = set.invalidLoop();
      a = a || ch == 'a';
      b = b || ch == 'b' || b;
      c = c || ch == 'c' || c;
      d = d || ch == 'd' || d;
      e = e || ch == 'e' || e;
    }
    assertTrue("'a' should be genered by invalid after reduction", a);
    assertTrue("'b' should be genered by invalid after reduction", b);
    assertTrue("'c' should be genered by invalid after reduction", c);
    assertTrue("'d' should be genered by invalid after reduction", d);
    assertTrue("'e' should be genered by invalid after reduction", e);
  }
}

package osmo.tester.model.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CharSetTests {
  @Test
  public void charTypesGenerated() {
    CharSet set = new CharSet();
    set.setStrategy(DataGenerationStrategy.RANDOM);
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
  public void charEvaluation() {
    CharSet set = new CharSet();
    assertTrue("'a' should be in the set.", set.evaluate('a'));
    assertTrue("'A' should be in the set.", set.evaluate('A'));
    assertTrue("'-' should be in the set.", set.evaluate('-'));
    assertTrue("'=' should be in the set.", set.evaluate('='));
  }

  @Test
  public void reduceWithWhiteSpace() {
    CharSet set = new CharSet();
    set.reduceBy(" \t");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.next();
      assertFalse("Should not contain removed chars, has " + c, c == ' ' || c == '\t');
    }
  }

  @Test
  public void reduceWithXml() {
    CharSet set = new CharSet();
    set.reduceBy("<>");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.next();
      assertFalse("Should not contain removed chars, has " + c, c == '<' || c == '>');
    }
  }

  @Test
  public void invalidLoop() {
    CharSet set = new CharSet();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP_INVALID);
    Collection<Integer> expected = invalidExpected();
    for (int i = 0 ; i < 200 ; i++) {
      char c = set.next();
      expected.remove((int) c);
    }
    assertEquals("Number of uncovered char codes from invalid loop", 0, expected.size());
  }

  @Test
  public void invalidRandom() {
    CharSet set = new CharSet();
    set.setStrategy(DataGenerationStrategy.RANDOM_INVALID);
    Collection<Integer> expected = invalidExpected();
    for (int i = 0 ; i < 2000 ; i++) {
      char c = set.next();
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
    CharSet set = new CharSet();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    set.asciiLettersAndNumbersOnly();
    assertEquals("First loop char", 'a', (char) set.next());
    assertEquals("Second loop char", 'b', (char) set.next());
    for (int i = 0 ; i < 58 ; i++) {
      char c = set.next();
    }
    assertEquals("60th loop char", '8', (char) set.next());
    assertEquals("61st loop char", '9', (char) set.next());
    assertEquals("62nd loop char", 'a', (char) set.next());
  }

  @Test
  public void asciiInvalid() {
    CharSet set = new CharSet();
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP_INVALID);
    set.asciiLettersAndNumbersOnly();
    Collection<Integer> expected = invalidExpected();
    String added = "åäöÅÄÖ,.<>!\"#%&/()=?´`{[]}\\¨^~';:|-_*-+= ";
    char[] chars = added.toCharArray();
    for (char c : chars) {
      expected.add((int) c);
    }
    for (int i = 0 ; i < 300 ; i++) {
      char c = set.next();
      expected.remove((int) c);
    }
    assertEquals("Number of uncovered char codes from invalid random with ascii set", 0, expected.size());
  }

  @Test
  public void reduction() {
    CharSet set = new CharSet();
    set.asciiLettersAndNumbersOnly();
    set.reduceBy("abcde");
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    for (int i = 0 ; i < 100 ; i++) {
      char c = set.next();
      String msg = "Letters 'abcde' should not be generated after reduction, was: " + c;
      assertTrue(msg, 'a' != c && 'b' != c && 'c' != c && 'd' != c && 'e' != c);
    }
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP_INVALID);
    boolean a = false;
    boolean b = false;
    boolean c = false;
    boolean d = false;
    boolean e = false;
    for (int i = 0 ; i < 300 ; i++) {
      char ch = set.next();
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

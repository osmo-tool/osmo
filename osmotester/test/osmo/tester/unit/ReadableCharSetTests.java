package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.model.dataflow.ReadableChars;
import osmo.tester.model.dataflow.ReadableWords;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ReadableCharSetTests {
  @Test
  public void charTypesGenerated() {
    ReadableChars set = new ReadableChars();
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
    ReadableChars set = new ReadableChars();
    assertTrue("'a' should be in the set.", set.evaluate('a'));
    assertTrue("'A' should be in the set.", set.evaluate('A'));
    assertTrue("'-' should be in the set.", set.evaluate('-'));
    assertTrue("'=' should be in the set.", set.evaluate('='));
  }

  @Test
  public void generateNegativeSequence() {
    try {
      ReadableWords words = new ReadableWords(-1, -2);
      fail("Negative length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Minimum or maximum length are not allowed to be negative (was -1, -2)", e.getMessage());
    }
  }

  @Test
  public void minLengthGreaterThanMax() {
    try {
      ReadableWords words = new ReadableWords(2, 1);
      fail("Min greater than max length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Maximum length is not allowed to be less than minimum length.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf0() {
    try {
      ReadableWords words = new ReadableWords(0, 0);
      fail("Zero length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Min and max are equal - generating/evaluating empty strings makes no sense.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf1() {
    ReadableWords words = new ReadableWords(1, 1);
    String word = words.next();
    assertEquals("Generated sequence length should match requested", 1, word.length());
  }

  @Test
  public void generateSequenceOf2() {
    ReadableWords words = new ReadableWords(2, 2);
    String word = words.next();
    assertEquals("Generated sequence length should match requested", 2, word.length());
  }

  @Test
  public void generateSequenceOf3To5() {
    ReadableWords words = new ReadableWords(3, 5);
    boolean three = false;
    boolean four = false;
    boolean five = false;
    for (int i = 0 ; i < 1000 ; i++) {
      String word = words.next();
      int length = word.length();
      assertTrue("Generated sequence length should be between 3-5, was " + length, length >= 3 && length <= 5);
      three = three || length == 3;
      four = four || length == 4;
      five = five || length == 5;
    }
    assertTrue("Should have generated length 3 char sequence.", three);
    assertTrue("Should have generated length 4 char sequence.", four);
    assertTrue("Should have generated length 5 char sequence.", five);
  }

  @Test
  public void evaluateEmptySequence() {
    ReadableWords words = new ReadableWords(0, 5);
    assertTrue("Evaluating empty string should work.", words.evaluate(""));
  }

  @Test
  public void evaluateSequenceOf20NotMax() {
    ReadableWords words = new ReadableWords(10, 30);
    assertTrue("Evaluating empty string should work.", words.evaluate("12345678901234567890"));
  }

  @Test
  public void evaluateBelowMinLength() {
    ReadableWords words = new ReadableWords(10, 20);
    assertTrue("Evaluating empty string should work.", words.evaluate("123456789"));
  }

  @Test
  public void evaluateAboveMaxLength() {
    ReadableWords words = new ReadableWords(10, 20);
    assertTrue("Evaluating empty string should work.", words.evaluate("123456789012345678901"));
  }

  @Test
  public void evaluateMaxLength() {
    ReadableWords words = new ReadableWords(10, 20);
    assertTrue("Evaluating empty string should work.", words.evaluate("12345678901234567890"));
  }

  @Test
  public void evaluateMinLength() {
    ReadableWords words = new ReadableWords(10, 20);
    assertTrue("Evaluating empty string should work.", words.evaluate("1234567890"));
  }

  @Test
  public void reduceWithWhiteSpace() {
    ReadableChars set = new ReadableChars();
    set.reduceBy(" \t");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.next();
      assertFalse("Should not contain removed chars, has " + c, c == ' ' || c == '\t');
    }
  }

  @Test
  public void reduceWithXml() {
    ReadableChars set = new ReadableChars();
    set.reduceBy("<>");
    for (int i = 0 ; i < 1000 ; i++) {
      char c = set.next();
      assertFalse("Should not contain removed chars, has " + c, c == '<' || c == '>');
    }
  }
}

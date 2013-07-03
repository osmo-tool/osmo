package osmo.tester.model.data;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class TextTests {

  @Test
  public void generateNegativeSequence() {
    try {
      Text text = new Text(-1, -2);
      fail("Negative length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Minimum and maximum length are not allowed to be negative (was -1, -2)", e.getMessage());
    }
  }

  @Test
  public void minLengthGreaterThanMax() {
    try {
      Text text = new Text(2, 1);
      fail("Min greater than max length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Maximum length is not allowed to be less than minimum length.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf0() {
    try {
      Text text = new Text(0, 0);
      fail("Zero length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Min and max are zero - generating/evaluating empty strings makes no sense.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf1() {
    Text text = new Text(1, 1);
    text.setSeed(333);
    String word = text.next();
    assertEquals("Generated sequence length should match requested", 1, word.length());
  }

  @Test
  public void generateSequenceOf2() {
    Text text = new Text(2, 2);
    text.setSeed(333);
    String word = text.next();
    assertEquals("Generated sequence length should match requested", 2, word.length());
  }

  @Test
  public void generateSequenceOf3To5() {
    Text text = new Text(3, 5);
    text.setSeed(333);
    boolean three = false;
    boolean four = false;
    boolean five = false;
    for (int i = 0 ; i < 1000 ; i++) {
      String word = text.next();
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
  public void invalidLength() {
    testLength(DataGenerationStrategy.ORDERED_LOOP_INVALID);
    testLength(DataGenerationStrategy.RANDOM_INVALID);
    testLength(DataGenerationStrategy.RANDOM);
  }

  private void testLength(DataGenerationStrategy strategy) {
    Text text = new Text(10, 20);
    text.setSeed(333);
    text.setStrategy(strategy);
    text.enableInvalidLength(true);
    Set<Integer> lengths = new HashSet<>();
    for (int i = 0 ; i < 100 ; i++) {
      String word = text.next();
      int length = word.length();
      lengths.add(length);
      assertTrue("Invalid length for 10-20+5 should first be 21-25, was:" + length, length > 20 && length <= 25);

      word = text.next();
      length = word.length();
      lengths.add(length);
      assertTrue("Invalid length for 10-20+5 should second be 5-9, was:" + length, length >= 5 && length < 10);
    }
    assertEquals("Number of different lengths of invalid values", 10, lengths.size());
  }

  @Test
  public void invalidRandom() {
    Text text = new Text(10, 20);
    text.setSeed(333);
    text.setStrategy(DataGenerationStrategy.RANDOM_INVALID);
    text.asciiLettersAndNumbersOnly();
    String word = text.next();
    int invalid = countInvalidAsciiCharsIn(word);
    int valid = word.length() - invalid;
    assertTrue("Number of valid chars in random invalid should be > 0, was " + valid, valid > 0);
    assertTrue("Number of invalid chars in random invalid should be > 0, was " + invalid, invalid > 0);
  }

  private int countInvalidAsciiCharsIn(String word) {
    String expected = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    char[] chars = word.toCharArray();
    int invalid = 0;
    for (char c : chars) {
      if (expected.indexOf(c) < 0) {
        invalid++;
      }
    }
    return invalid;
  }

  @Test
  public void invalidZeroLength() {
    Text text = new Text(10, 20);
    text.setSeed(333);
    text.enableZeroSize(true);
    String word = text.next();
    int length = word.length();
    assertTrue("Zero size enabled with invalid length disabled should produce no effect", length >= 10 && length <= 20);

    text = new Text(10, 20);
    text.setSeed(333);
    text.enableZeroSize(true);
    text.enableInvalidLength(true);
    word = text.next();
    assertEquals("Zero size enabled with invalid length enabled should produce zero size as first item.", 0, word.length());
    word = text.next();
    length = word.length();
    assertTrue("Zero size enabled with invalid length enabled should produce >max size as second item.", length > 20);
    word = text.next();
    length = word.length();
    assertTrue("Zero size enabled with invalid length enabled should produce <min size as second item.", length < 10);
  }

  @Test
  public void invalidLoop() {
    Text text = new Text(10, 10);
    text.setSeed(333);
    text.setStrategy(DataGenerationStrategy.ORDERED_LOOP_INVALID);
    text.asciiLettersAndNumbersOnly();
    assertOneAndTwoInvalidChars(text);
    //roll over until the full string should be invalid
    for (int i = 0 ; i < 8 + 7 + 6 + 5 + 4 + 3 + 2 ; i++) {
      String word = text.next();
    }
    String word = text.next();
    assertInvalidCharsAt(word, 0, 10);
    //we have now rolled through it all so we expect a restart...
    assertOneAndTwoInvalidChars(text);
  }

  private void assertOneAndTwoInvalidChars(Text text) {
    for (int i = 0 ; i < 10 ; i++) {
      String word = text.next();
      assertInvalidCharsAt(word, i, 1);
    }
    for (int i = 0 ; i < 9 ; i++) {
      String word = text.next();
      assertInvalidCharsAt(word, i, 2);
    }
  }

  private void assertInvalidCharsAt(String word, int index, int count) {
    int invalids = countInvalidAsciiCharsIn(word);
    assertEquals("Expected number of invalid chars (index the check: " + index + ") in generated word", count, invalids);
  }

  @Test
  public void invalidConfig() {
    Text text = new Text(10, 20);
    text.setSeed(333);
    try {
      text.setInvalidProbability(-1);
      fail("Negative values should not be allowed for invalid probability.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative invalid probability", "Probability must be between 0-1, was -1.0", e.getMessage());
    }
    try {
      text.setInvalidProbability(1.01f);
      fail("Values >1 should not be allowed for invalid probability.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative invalid probability", "Probability must be between 0-1, was 1.01", e.getMessage());
    }

    try {
      text.setOffset(-1, 5);
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative invalid offset", "Minimum and maximum length are not allowed to be negative (was -1, 5)", e.getMessage());
    }
    try {
      text.setOffset(9, 5);
    } catch (IllegalArgumentException e) {
      assertEquals("Error for min invalid offset larger than max", "Maximum length is not allowed to be less than minimum length.", e.getMessage());
    }
  }

  @Test
  public void invalidProbabilityZero() {
    Text text = new Text(10, 10);
    text.setSeed(333);
    text.asciiLettersAndNumbersOnly();
    text.setStrategy(DataGenerationStrategy.RANDOM_INVALID);
    text.setInvalidProbability(0);
    String word = text.next();
    int count = countInvalidAsciiCharsIn(word);
    assertEquals("Number of invalid chars with zero invalid probability", 0, count);
  }

  @Test
  public void invalidProbabilityFull() {
    Text text = new Text(10, 10);
    text.setSeed(333);
    text.asciiLettersAndNumbersOnly();
    text.setStrategy(DataGenerationStrategy.RANDOM_INVALID);
    text.setInvalidProbability(1);
    String word = text.next();
    int count = countInvalidAsciiCharsIn(word);
    assertEquals("All chars should be invalid with full invalid probability", 10, count);
  }

  @Test
  public void invalidLengthToNegative() {
    Text text = new Text(1, 2);
    text.setSeed(333);
    text.asciiLettersAndNumbersOnly();
    text.setOffset(1, 5);
    text.enableInvalidLength(true);
    for (int i = 0 ; i < 100 ; i++) {
      String word = text.next();
//      System.out.println(word.length()+":"+word);
      assertNotNull("Invalid generation should always produce a word or exception, not null", word);
      assertTrue("Invalid should never go over max size + max offset", word.length() < 8);
    }
  }
}

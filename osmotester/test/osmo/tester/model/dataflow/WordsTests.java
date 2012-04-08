package osmo.tester.model.dataflow;

import org.junit.Test;
import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class WordsTests {
  @Test
  public void generateNegativeSequence() {
    try {
      Words words = new Words(-1, -2);
      fail("Negative length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Minimum and maximum length are not allowed to be negative (was -1, -2)", e.getMessage());
    }
  }

  @Test
  public void minLengthGreaterThanMax() {
    try {
      Words words = new Words(2, 1);
      fail("Min greater than max length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Maximum length is not allowed to be less than minimum length.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf0() {
    try {
      Words words = new Words(0, 0);
      fail("Zero length should throw Exception.");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Min and max are zero - generating/evaluating empty strings makes no sense.", e.getMessage());
    }
  }

  @Test
  public void generateSequenceOf1() {
    Words words = new Words(1, 1);
    String word = words.next();
    assertEquals("Generated sequence length should match requested", 1, word.length());
  }

  @Test
  public void generateSequenceOf2() {
    Words words = new Words(2, 2);
    String word = words.next();
    assertEquals("Generated sequence length should match requested", 2, word.length());
  }

  @Test
  public void generateSequenceOf3To5() {
    Words words = new Words(3, 5);
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
    Words words = new Words(0, 5);
    assertTrue("Evaluating empty string should work.", words.evaluate(""));
  }

  @Test
  public void evaluateSequenceOf20NotMax() {
    Words words = new Words(10, 30);
    assertTrue("Evaluating 20 length string should work for 10-30.", words.evaluate("12345678901234567890"));
  }

  @Test
  public void evaluateBelowMinLength() {
    Words words = new Words(10, 20);
    assertFalse("Evaluating below minimum length should fail.", words.evaluate("123456789"));
  }

  @Test
  public void evaluateAboveMaxLength() {
    Words words = new Words(10, 20);
    assertFalse("Evaluating above maximum length should fail.", words.evaluate("123456789012345678901"));
  }

  @Test
  public void evaluateMaxLength() {
    Words words = new Words(10, 20);
    assertTrue("Evaluating at maximum length should work.", words.evaluate("12345678901234567890"));
  }

  @Test
  public void evaluateMinLength() {
    Words words = new Words(10, 20);
    assertTrue("Evaluating at minimum length should work.", words.evaluate("1234567890"));
  }

  @Test
  public void fuzzLength() {
    testLength(DataGenerationStrategy.FUZZY_LOOP);
    testLength(DataGenerationStrategy.FUZZY_RANDOM);
    testLength(DataGenerationStrategy.RANDOM);
  }

  private void testLength(DataGenerationStrategy strategy) {
    Words words = new Words(10, 20);
    words.setStrategy(strategy);
    words.fuzzLength(true);
    Set<Integer> lengths = new HashSet<>();
    for (int i = 0 ; i < 100 ; i++) {
      String word = words.next();
      int length = word.length();
      lengths.add(length);
      assertTrue("Fuzzed length for 10-20+5 should first be 21-25, was:" + length, length > 20 && length <= 25);

      word = words.next();
      length = word.length();
      lengths.add(length);
      assertTrue("Fuzzed length for 10-20+5 should second be 5-9, was:" + length, length >= 5 && length < 10);
    }
    assertEquals("Number of different lengths of fuzziness", 10, lengths.size());
  }

  @Test
  public void fuzzRandom() {
    Words words = new Words(10, 20);
    words.setStrategy(DataGenerationStrategy.FUZZY_RANDOM);
    words.asciiLettersAndNumbersOnly();
    String word = words.next();
    int invalid = countInvalidAsciiCharsIn(word);
    int valid = word.length() - invalid;
    assertTrue("Number of valid chars in random fuzz should be > 0, was " + valid, valid > 0);
    assertTrue("Number of invalid chars in random fuzz should be > 0, was " + invalid, invalid > 0);
  }

  private int countInvalidAsciiCharsIn(String word) {
    String expected = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    char[] chars = word.toCharArray();
//    int valid = 0;
    int invalid = 0;
    for (char c : chars) {
      if (expected.indexOf(c) < 0) {
        invalid++;
        //     } else {
        //       valid++;
      }
    }
    return invalid;
  }

  @Test
  public void fuzzZeroLength() {
    Words words = new Words(10, 20);
    words.setZeroSize(true);
    String word = words.next();
    int length = word.length();
    assertTrue("Zero size enabled with length fuzzing disabled should produce no effect", length >= 10 && length <= 20);

    words = new Words(10, 20);
    words.setZeroSize(true);
    words.fuzzLength(true);
    word = words.next();
    assertEquals("Zero size enabled with length fuzzing enabled should produce zero size as first item.", 0, word.length());
    word = words.next();
    length = word.length();
    assertTrue("Zero size enabled with length fuzzing enabled should produce >max size as second item.", length > 20);
    word = words.next();
    length = word.length();
    assertTrue("Zero size enabled with length fuzzing enabled should produce <min size as second item.", length < 10);
  }

  @Test
  public void fuzzLoop() {
    Words words = new Words(10, 10);
    words.setStrategy(DataGenerationStrategy.FUZZY_LOOP);
    words.asciiLettersAndNumbersOnly();
    assertOneAndTwoInvalidChars(words);
    //roll over until the full string should be fuzzed
    for (int i = 0 ; i < 8 + 7 + 6 + 5 + 4 + 3 + 2 ; i++) {
      String word = words.next();
    }
    String word = words.next();
    assertInvalidCharsAt(word, 0, 10);
    //we have now rolled through it all so we expect a restart...
    assertOneAndTwoInvalidChars(words);
  }

  private void assertOneAndTwoInvalidChars(Words words) {
    for (int i = 0 ; i < 10 ; i++) {
      String word = words.next();
      assertInvalidCharsAt(word, i, 1);
    }
    for (int i = 0 ; i < 9 ; i++) {
      String word = words.next();
      assertInvalidCharsAt(word, i, 2);
    }
  }

  private void assertInvalidCharsAt(String word, int index, int count) {
    int invalids = countInvalidAsciiCharsIn(word);
    assertEquals("Expected number of invalid chars (index the check: " + index + ") in generated word", count, invalids);
  }

  @Test
  public void invalidConfig() {
    Words words = new Words(10, 20);
    try {
      words.setFuzzProbability(-1);
      fail("Negative values should not be allowed for fuzz probability.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative fuzz probability", "Probability must be between 0-1, was -1.0", e.getMessage());
    }
    try {
      words.setFuzzProbability(1.01f);
      fail("Values >1 should not be allowed for fuzz probability.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative fuzz probability", "Probability must be between 0-1, was 1.01", e.getMessage());
    }

    try {
      words.setOffset(-1, 5);
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative fuzz offset", "Minimum and maximum length are not allowed to be negative (was -1, 5)", e.getMessage());
    }
    try {
      words.setOffset(9, 5);
    } catch (IllegalArgumentException e) {
      assertEquals("Error for min fuzz offset larger than max", "Maximum length is not allowed to be less than minimum length.", e.getMessage());
    }
  }

  @Test
  public void fuzzProbabilityZero() {
    Words words = new Words(10, 10);
    words.asciiLettersAndNumbersOnly();
    words.setStrategy(DataGenerationStrategy.FUZZY_RANDOM);
    words.setFuzzProbability(0);
    String word = words.next();
    int count = countInvalidAsciiCharsIn(word);
    assertEquals("Number of invalid chars with zero fuzz probability", 0, count);
  }

  @Test
  public void fuzzProbabilityFull() {
    Words words = new Words(10, 10);
    words.asciiLettersAndNumbersOnly();
    words.setStrategy(DataGenerationStrategy.FUZZY_RANDOM);
    words.setFuzzProbability(1);
    String word = words.next();
    int count = countInvalidAsciiCharsIn(word);
    assertEquals("All chars should be fuzzed with full fuzz probability", 10, count);
  }

  @Test
  public void fuzzLengthToNegative() {
    Words words = new Words(1, 2);
    words.asciiLettersAndNumbersOnly();
    words.setOffset(1, 5);
    words.fuzzLength(true);
    for (int i = 0 ; i < 100 ; i++) {
      String word = words.next();
//      System.out.println(word.length()+":"+word);
      assertNotNull("Fuzzing should always produce a word or exception, not null", word);
      assertTrue("Fuzzing should never go over max size + max offset", word.length() < 8);
    }
  }
}

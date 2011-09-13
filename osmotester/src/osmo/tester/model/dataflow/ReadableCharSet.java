package osmo.tester.model.dataflow;

import static osmo.common.TestUtils.cInt;

/**
 * Input/Output in form of human readable characters.
 *
 * @author Teemu Kanstren
 */
public class ReadableCharSet {
  /** Defines the list of characters that are considered as human-readable. */
  public static final String chars = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789,.<>!\"#¤%&/()=?´`{[]}\\¨^~';:|-_*-+= \t";
  /** Minimum length of generated word. */
  private int min = 5;
  /** Maximum length of generated word. */
  private int max = 10;

  /**
   * Constructor for default values (min=5, max=10).
   */
  public ReadableCharSet() {
  }

  /**
   * Constructor for setting minimum and maximum char sequence size.
   *
   * @param min The minimum length.
   * @param max The maximum length.
   */
  public ReadableCharSet(int min, int max) {
    this.min = min;
    this.max = max;
    if (min < 0 || max < 0) {
      throw new IllegalArgumentException("Minimum or maximum length are not allowed to be negative (was "+min+", "+max+")");
    }
    if (max < min) {
      throw new IllegalArgumentException("Maximum length is not allowed to be less than minimum length.");
    }
    if (max == 0) {
      throw new IllegalArgumentException("Min and max are equal - generating/evaluating empty strings makes no sense.");
    }
  }

  /**
   * @return A human readable character.
   */
  public char next() {
    int min = 0;
    int max = chars.length() - 1;
    int index = cInt(min, max);
    return chars.charAt(index);
  }

  /**
   * @param c Character to test.
   * @return True if given character is in the set defined as human readable.
   */
  public boolean evaluate(char c) {
    return chars.indexOf(c) >= 0;
  }

  /**
   * Generates a sequence of readable characters, in length between the configured min and max values.
   * 
   * @return The generated character sequence.
   */
  public String nextWord() {
    int length = cInt(min, max);
    char[] chars = new char[length];
    for (int i = 0 ; i < length ; i++) {
      chars[i] = next();
    }
    return new String(chars);
  }

  /**
   * Evaluate that all characters in the given char sequence are in the readable set.
   *
   * @param word The char sequence to evaluate.
   * @return True if all characters are in the readable set, false otherwise.
   */
  public boolean evaluateWord(String word) {
    char[] wordChars = word.toCharArray();
    boolean result = true;
    for (char c : wordChars) {
      boolean b = evaluate(c);
      result = result && b;
    }
    return result;
  }
}

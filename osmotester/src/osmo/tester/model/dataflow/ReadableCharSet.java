package osmo.tester.model.dataflow;

import static osmo.tester.TestUtils.*;

/**
 * Input/Output in form of human readable characters.
 *
 * @author Teemu Kanstren
 */
public class ReadableCharSet {
  /** Defines the list of characters that are considered as human-readable. */
  private static final String chars = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789,.<>!\"#¤%&/()=?´`{[]}\\¨^~';:|-_*-+= \t";

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
}

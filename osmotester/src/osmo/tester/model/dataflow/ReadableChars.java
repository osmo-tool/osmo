package osmo.tester.model.dataflow;

import static osmo.common.TestUtils.cInt;

/**
 * Input/Output in form of human readable characters.
 *
 * @author Teemu Kanstren
 */
public class ReadableChars extends SearchableInput<Character> {
  /** Defines the list of characters that are considered as human-readable. */
  private String chars = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789,.<>!\"#%&/()=?´`{[]}\\¨^~';:|-_*-+= \t";

  @Override
  public void setStrategy(DataGenerationStrategy algorithm) {
    throw new UnsupportedOperationException(ReadableChars.class.getSimpleName() + " does not support setting data generation strategy.");
  }

  /**
   * Removes the characters in the given string from the potential characters to generate.
   *
   * @param charsToRemove The characters to remove.
   */
  public void reduceBy(String charsToRemove) {
    char[] r = charsToRemove.toCharArray();
    String result = "";
    for (int ci = 0; ci < chars.toCharArray().length; ci++) {
      char c = chars.toCharArray()[ci];
      boolean found = false;
      for (char rc : r) {
        if (c == rc) {
          found = true;
          break;
        }
      }
      if (!found) {
        result += c;
      }
    }
    chars = result;
  }

  /** @return A human readable character. */
  public Character next() {
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
   * Removes the XML tag start and end characters from generation ("<" and ">") to allow for the
   * generation of data that can be embedded in XML files.
   */
  public void enableXml() {
    reduceBy("<>");
  }

  /** Sets the set of generated characters to contain only a-z,A-Z,0-9. */
  public void asciiLettersAndNumbersOnly() {
    chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  }

  @Override
  public boolean evaluate(Character item) {
    return chars.contains(item.toString());
  }

  @Override
  public boolean evaluateSerialized(String item) {
    if (item.length() != 1) {
      return false;
    }
    return evaluate(item.charAt(0));
  }
}

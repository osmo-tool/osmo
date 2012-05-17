package osmo.tester.model.dataflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static osmo.common.TestUtils.cInt;

/**
 * Input/Output in form of characters.
 *
 * @author Teemu Kanstren
 */
public class CharSet extends SearchableInput<Character> {
  /** The input strategy to choose a char. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Defines the list of characters for generating valid items. */
  private String validChars = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789,.<>!\"#%&/()=?´`{[]}\\¨^~';:|-_*-+= ";
  /** Defines the list of characters for generating invalid items. */
  private String invalidChars = "";
  /** Index when looped strategy is used. */
  private int loopIndex = 0;

  public CharSet() {
    for (int i = 0; i <= 32; i++) {
      invalidChars += (char) i;
    }
    for (int i = 127; i <= 258; i++) {
      invalidChars += (char) i;
    }
  }

  @Override
  public CharSet setStrategy(DataGenerationStrategy strategy) {
    switch (strategy) {
      case RANDOM:
      case ORDERED_LOOP:
      case ORDERED_LOOP_INVALID:
      case RANDOM_INVALID:
        this.strategy = strategy;
        loopIndex = 0;
        return this;
      default:
        String name = CharSet.class.getSimpleName();
        String msg = name + " only supports Random, Looping, and Invalid generation strategies. Given:" + strategy;
        throw new UnsupportedOperationException(msg);
    }
  }

  /**
   * Removes the characters in the given string from the potential valid characters to generate, and adds them
   * to the invalid set.
   *
   * @param charsToRemove The characters to remove.
   */
  public void reduceBy(String charsToRemove) {
    char[] r = charsToRemove.toCharArray();
    String result = "";
    String removed = "";
    char[] array = validChars.toCharArray();
    for (char c : array) {
      boolean found = false;
      for (char rc : r) {
        if (c == rc) {
          found = true;
          break;
        }
      }
      if (!found) {
        result += c;
      } else {
        removed += c;
      }
    }
    validChars = result;
    invalidChars += removed;
  }

  /** @return A human readable character. */
  public Character next() {
    switch (strategy) {
      case RANDOM:
        return nextRandom();
      case ORDERED_LOOP:
        return nextLoop();
      case RANDOM_INVALID:
        return nextInvalidRandom();
      case ORDERED_LOOP_INVALID:
        return nextInvalidLoop();
      default:
        String name = CharSet.class.getSimpleName();
        throw new IllegalArgumentException("DataGenerationStrategy " + strategy + " not supported by " + name + ".");
    }
  }

  private Character nextRandom() {
    int min = 0;
    int max = validChars.length() - 1;
    int index = cInt(min, max);
    return validChars.charAt(index);
  }

  private Character nextLoop() {
    char c = validChars.charAt(loopIndex);
    loopIndex++;
    if (loopIndex >= validChars.length()) {
      loopIndex = 0;
    }
    return c;
  }

  public Character nextInvalidRandom() {
    int min = 0;
    int max = invalidChars.length() - 1;
    int index = cInt(min, max);
    return invalidChars.charAt(index);
  }

  public Character nextInvalidLoop() {
    loopIndex++;
    if (loopIndex >= invalidChars.length()) {
      loopIndex = 0;
    }
    return invalidChars.charAt(loopIndex);
  }

  /**
   * @param c Character to test.
   * @return True if given character is in the set defined as human readable.
   */
  public boolean evaluate(char c) {
    return validChars.indexOf(c) >= 0;
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
    validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    invalidChars += "åäöÅÄÖ,.<>!\"#%&/()=?´`{[]}\\¨^~';:|-_*-+= ";
  }

  @Override
  public boolean evaluate(Character item) {
    return validChars.contains(item.toString());
  }

  @Override
  public boolean evaluateSerialized(String item) {
    if (item.length() != 1) {
      return false;
    }
    return evaluate(item.charAt(0));
  }

  @Override
  public Collection<Character> getOptions() {
    List<Character> result = new ArrayList<>();
    char[] temp = validChars.toCharArray();
    if (strategy == DataGenerationStrategy.ORDERED_LOOP_INVALID || strategy == DataGenerationStrategy.RANDOM_INVALID) {
      temp = invalidChars.toCharArray();
    }
    for (char c : temp) {
      result.add(c);
    }
    return result;
  }

  @Override
  public void enableGUI() {
  }
}

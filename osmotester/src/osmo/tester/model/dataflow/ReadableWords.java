package osmo.tester.model.dataflow;

import osmo.tester.gui.manualdrive.WordGUI;

import java.util.ArrayList;
import java.util.Collection;

import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class ReadableWords extends SearchableInput<String> {
  /** Minimum length of generated word. */
  private int min = 5;
  /** Maximum length of generated word. */
  private int max = 10;
  /** Used to create characters for words. */
  private ReadableChars chars = new ReadableChars();
  /** History of generated words. */
  private Collection<String> history = new ArrayList<String>();
  /** How are the characters generated? */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;

  /** Constructor for default values (min=5, max=10). */
  public ReadableWords() {
  }

  /**
   * Constructor for setting minimum and maximum char sequence size.
   *
   * @param min The minimum length.
   * @param max The maximum length.
   */
  public ReadableWords(int min, int max) {
    this.min = min;
    this.max = max;
    if (min < 0 || max < 0) {
      throw new IllegalArgumentException("Minimum or maximum length are not allowed to be negative (was " + min + ", " + max + ")");
    }
    if (max < min) {
      throw new IllegalArgumentException("Maximum length is not allowed to be less than minimum length.");
    }
    if (max == 0) {
      throw new IllegalArgumentException("Min and max are equal - generating/evaluating empty strings makes no sense.");
    }
  }

  /**
   * Not supported by this class.
   *
   * @param algorithm The new algorithm.
   */
  @Override
  public void setStrategy(DataGenerationStrategy algorithm) {
    switch (algorithm) {
      case RANDOM:
      case SCRIPTED:
        this.strategy = algorithm;
        return;
      default:
        throw new UnsupportedOperationException(ReadableChars.class.getSimpleName() + " supports only scripted and random data generation strategy.");
    }
  }

  /**
   * Generates a sequence of readable characters, in length between the configured min and max values.
   *
   * @return The generated character sequence.
   */
  @Override
  public String next() {
    if (gui != null) {
      return (String) gui.next();
    }
    switch (strategy) {
      case RANDOM:
        return randomNext();
      case SCRIPTED:
        return scriptedNext();
      default:
        throw new IllegalStateException("Unsupported data generation strategy for " + ReadableWords.class.getName() + " (random and scripted only supported): " + strategy.getClass().getName());
    }
  }

  private String randomNext() {
    int length = cInt(min, max);
    char[] c = new char[length];
    for (int i = 0; i < length; i++) {
      c[i] = chars.next();
    }
    String next = new String(c);
    history.add(next);
    observe(next);
    return next;
  }

  private String scriptedNext() {
    String next = scriptNextSerialized();
    if (!evaluate(next)) {
      throw new IllegalArgumentException("Requested invalid scripted value for variable '" + getName() + "' (must be valid set of chars and length in defined bounds of length " + min + "-" + max + "): " + next);
    }
    return next;
  }

  /** Set to only generate XML compliant characters. */
  public void enableXml() {
    chars.enableXml();
  }

  /** Only a-z, A-Z, 0-9. */
  public void asciiLettersAndNumbersOnly() {
    chars.asciiLettersAndNumbersOnly();
  }

  /**
   * Reduce the set of generated chars by given set of chars.
   *
   * @param charsToRemove The string of chars to be removed.
   */
  public void reduceBy(String charsToRemove) {
    chars.reduceBy(charsToRemove);
  }

  @Override
  /**
   * Evaluate that all characters in the given char sequence are in the readable set and if the length of the word
   * is withing the bounds defined in this object.
   *
   * @param word The char sequence to evaluate.
   * @return True if all characters are in the readable set and length is ok, false otherwise.
   */
  public boolean evaluate(String word) {
    char[] wordChars = word.toCharArray();
    if (wordChars.length < min || wordChars.length > max) {
      return false;
    }
    boolean result = true;
    for (char c : wordChars) {
      boolean b = chars.evaluate(c);
      result = result && b;
    }
    return result;
  }

  @Override
  public boolean evaluateSerialized(String item) {
    return evaluate(item);
  }

  public Collection<String> getHistory() {
    return history;
  }

  @Override
  public void enableGUI() {
    gui = new WordGUI(this);
  }
}

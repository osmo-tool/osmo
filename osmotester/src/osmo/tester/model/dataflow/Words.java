package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.gui.manualdrive.WordGUI;

import java.util.ArrayList;
import java.util.Collection;

import static osmo.common.TestUtils.*;

/**
 * For generating words, meaning strings of characters matching the given specification.
 * 
 * @author Teemu Kanstren 
 */
public class Words extends SearchableInput<String> {
  private static final Logger log = new Logger(Words.class);
  /** Minimum length of generated word. */
  private int min = 5;
  /** Maximum length of generated word. */
  private int max = 10;
  /** Used to create valid characters for words. */
  private CharSet chars = new CharSet();
  /** History of generated words. */
  private Collection<String> history = new ArrayList<>();
  /** How are the characters generated? */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Produce words of invalid length? Invalid is what we call length fuzziness here.. */
  private boolean invalidLength = false;
  /** Was it below or above min/max length when length fuzziness is applied? */
  private int previousLength = -1;
  /** Min number to go over size if length fuzziness is applied. */
  private int minOffset = 1;
  /** Max number to go over size if length fuzziness is applied. */
  private int maxOffset = 5;
  /** Should we create one input of size zero? */
  private boolean zeroSize = false;
  /** Did we already provide zero size? */
  private boolean zeroDone = false;
  /** The probability that we will provide an invalid character in the generated string (between 0-1). */
  private float fuzzProbability = 0.5f;
  /** Index in result that we are fuzzing when in fuzz loop. */
  private int fuzzIndex = 0;
  /** Number of chars we fuzz at index while in fuzz loop. */
  private int fuzzLength = 1;

  /** Constructor for default values (min=5, max=10). */
  public Words() {
  }

  /**
   * Constructor for setting minimum and maximum char sequence size.
   *
   * @param min The minimum length.
   * @param max The maximum length.
   */
  public Words(int min, int max) {
    this.min = min;
    this.max = max;
    evaluateMinMax(min, max);
  }

  /**
   * Set the probability that a character for a string will be "fuzzed" meaning an invalid value will be
   * inserted instead of valid value.
   * 
   * @param fuzzProbability Value between 0 (0%) and 1 (100%).
   */
  public void setFuzzProbability(float fuzzProbability) {
    if (fuzzProbability < 0 || fuzzProbability > 1) {
      throw new IllegalArgumentException("Probability must be between 0-1, was "+fuzzProbability);
    }
    this.fuzzProbability = fuzzProbability;
  }

  /**
   * Enable or disable production of zero size strings in fuzzing. By default this is disabled.
   * 
   * @param zeroSize Enable/disable flag.
   */
  public void setZeroSize(boolean zeroSize) {
    this.zeroSize = zeroSize;
  }

  private void evaluateMinMax(int min, int max) {
    if (min < 0 || max < 0) {
      throw new IllegalArgumentException("Minimum and maximum length are not allowed to be negative (was " + min + ", " + max + ")");
    }
    if (max < min) {
      throw new IllegalArgumentException("Maximum length is not allowed to be less than minimum length.");
    }
    if (max == 0) {
      throw new IllegalArgumentException("Min and max are zero - generating/evaluating empty strings makes no sense.");
    }
  }

  /**
   * Set the min and max offsets for how much smaller/bigger strings will be generated when length fuzziness is applied.
   * Note that strings of less that 0 size will never be generated (doh!). To exclude/include 0 size always, enable
   * the zeroSize attribute.
   * 
   * @param min Minimum of more/less.
   * @param max Maximum of more/less.
   */
  public void setOffset(int min, int max) {
    this.minOffset = min;
    this.maxOffset = max;
    evaluateMinMax(min, max);
  }

  /**
   * Not supported by this class.
   *
   * @param algorithm The new algorithm.
   */
  @Override
  public Words setStrategy(DataGenerationStrategy algorithm) {
    switch (algorithm) {
      case RANDOM:
      case SCRIPTED:
      case FUZZY_LOOP:
      case FUZZY_RANDOM:
        this.strategy = algorithm;
        return this;
      default:
        throw new UnsupportedOperationException(CharSet.class.getSimpleName() + " supports only Scripted, RAndom and Fuzzy data generation strategy.");
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
      case FUZZY_RANDOM:
        return fuzzyRandomNext();
      case FUZZY_LOOP:
        return fuzzyLoopNext();
      default:
        throw new IllegalStateException("Unsupported data generation strategy for " + Words.class.getName() + " (random and scripted only supported): " + strategy.getClass().getName());
    }
  }

  private int length() {
    int length = -1;
    if (!invalidLength) {
      length = cInt(min, max);
    } else {
      if (zeroSize && !zeroDone) {
        log.debug("Giving zero length");
        zeroDone = true;
        previousLength = 0;
        return 0;
      }
      int offset = cInt(minOffset, maxOffset);
      if (previousLength < min) {
        length = max+offset;
      } else {
        length = min-offset;
      }
      if (length < 0) {
        length = 0;
      }
    }
    previousLength = length;
    return length;
  }
  
  private String randomNext() {
    int length = length();
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
    return scriptNextSerialized();
  }

  /**
   * Creates values that are different from expected, replacing chars with invalid ones using some heuristics and 
   * random probability.
   * Relates to data, length is controlled with the invalidLength variable.
   * 
   * @return the next generated value.
   */
  private String fuzzyRandomNext() {
    int length = length();
    char[] c = new char[length];
    for (int i = 0; i < length; i++) {
      float f = cFloat(0,1);
      if (f > fuzzProbability) {
        c[i] = chars.next();
      } else {
        c[i] = chars.nextFuzzyRandom();
      }
    }
    String next = new String(c);
    history.add(next);
    observe(next);
    return next;
  }

  /**
   * Creates values that are different from expected, replacing with invalid ones using some heuristics and by looping
   * through the chars in the word one at a time.
   * Relates to data, length is controlled with the invalidLength variable.
   *
   * @return the next generated value.
   */
  private String fuzzyLoopNext() {
    int length = length();
    log.debug("Fuzzy loop length:"+length);
    char[] c = new char[length];
    for (int i = 0; i < length; i++) {
      if (i >= fuzzIndex && i < fuzzIndex+fuzzLength) {
        c[i] = chars.nextFuzzyLoop();
      } else {
        c[i] = chars.next();
      }
    }
    fuzzIndex++;
    //rotate loop and size of change until it overflows and restart from beginning
    if (fuzzIndex+fuzzLength > length) {
      fuzzIndex = 0;
      fuzzLength++;
      if (fuzzIndex+fuzzLength > length) {
        fuzzLength = 1;
      }
    }
    String next = new String(c);
    history.add(next);
    observe(next);
    return next;
    // illegal characters: for ascii, base64, xml, json, url-encoded, custom
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

  public void fuzzLength(boolean fuzz) {
    this.invalidLength = fuzz;
  }

}

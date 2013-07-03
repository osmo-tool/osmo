package osmo.tester.model.data;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.TextGUI;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For generating words, meaning strings of characters matching the given specification.
 *
 * @author Teemu Kanstren
 */
public class Text extends SearchableInput<String> {
  private static final Logger log = new Logger(Text.class);
  /** Minimum length of generated word. */
  private int min = 5;
  /** Maximum length of generated word. */
  private int max = 10;
  /** Used to create valid characters for words. */
  private final CharSet chars = new CharSet();
  /** History of generated words. */
  private Collection<String> history = new ArrayList<>();
  /** How are the characters generated? */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Produce words of invalid length? Invalid is what we call length outside configured bounds.. */
  private boolean invalid = false;
  /** Was it below or above min/max length when invalid length is applied? */
  private int previousLength = -1;
  /** Min number to go over size if invalid length is applied. */
  private int minOffset = 1;
  /** Max number to go over size if invalid length is applied. */
  private int maxOffset = 5;
  /** Should we create one input of size zero? */
  private boolean zeroSize = false;
  /** Did we already provide zero size? */
  private boolean zeroDone = false;
  /** The probability that we will provide an invalid character in the generated string (between 0-1). */
  private float invalidProbability = 0.5f;
  /** Index in result for invalid loop. */
  private int invalidIndex = 0;
  /** Number of chars we create at index while in invalid loop mode. */
  private int invalidSize = 1;

  /** Constructor for default values. */
  public Text() {
  }

  /**
   * @param min The minimum length.
   * @param max The maximum length.
   */
  public Text(int min, int max) {
    this.min = min;
    this.max = max;
    evaluateMinMax(min, max);
  }

  @Override
  public void setSeed(long seed) {
    super.setSeed(seed);
    chars.setSeed(seed);
  }

  /**
   * Set the probability that an invalid character for a string will be
   * inserted instead of valid value.
   *
   * @param invalidProbability Value between 0 (0%) and 1 (100%).
   */
  public Text setInvalidProbability(float invalidProbability) {
    if (invalidProbability < 0 || invalidProbability > 1) {
      throw new IllegalArgumentException("Probability must be between 0-1, was " + invalidProbability);
    }
    this.invalidProbability = invalidProbability;
    return this;
  }

  /**
   * Enable or disable production of zero size strings in invalid data generation. By default this is disabled.
   *
   * @param zeroSize Enable/disable flag.
   */
  public Text enableZeroSize(boolean zeroSize) {
    this.zeroSize = zeroSize;
    return this;
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
   * Set the min and max offsets for how much smaller/bigger strings will be generated when invalid length is applied.
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
  public Text setStrategy(DataGenerationStrategy algorithm) {
    switch (algorithm) {
      case RANDOM:
      case ORDERED_LOOP_INVALID:
      case RANDOM_INVALID:
        this.strategy = algorithm;
        return this;
      default:
        throw new UnsupportedOperationException(Text.class.getSimpleName() + " supports only Scripted, Random and Invalid data generation strategy, given: " + algorithm.name() + ".");
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
    OSMOConfiguration.check(this);
    switch (strategy) {
      case RANDOM:
        return randomNext();
      case RANDOM_INVALID:
        return invalidRandomNext();
      case ORDERED_LOOP_INVALID:
        return invalidLoopNext();
      default:
        throw new IllegalStateException("Unsupported data generation strategy for " + Text.class.getName() + " (random and scripted only supported): " + strategy.getClass().getName());
    }
  }

  private int length() {
    int length = -1;
    if (!invalid) {
      length = rand.nextInt(min, max);
    } else {
      if (zeroSize && !zeroDone) {
        log.debug("Giving zero length");
        zeroDone = true;
        previousLength = 0;
        return 0;
      }
      int offset = rand.nextInt(minOffset, maxOffset);
      if (previousLength < min) {
        length = max + offset;
      } else {
        length = min - offset;
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
    for (int i = 0 ; i < length ; i++) {
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
   * Relates to data, length is controlled with the invalid variable.
   *
   * @return the next generated value.
   */
  private String invalidRandomNext() {
    int length = length();
    char[] c = new char[length];
    for (int i = 0 ; i < length ; i++) {
      float f = rand.nextFloat(0, 1);
      if (f > invalidProbability) {
        c[i] = chars.next();
      } else {
        c[i] = chars.nextInvalidRandom();
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
   * Relates to data, length is controlled with the invalid variable.
   *
   * @return the next generated value.
   */
  private String invalidLoopNext() {
    int length = length();
    log.debug("Invalid loop length:" + length);
    char[] c = new char[length];
    for (int i = 0 ; i < length ; i++) {
      if (i >= invalidIndex && i < invalidIndex + invalidSize) {
        c[i] = chars.nextInvalidLoop();
      } else {
        c[i] = chars.next();
      }
    }
    invalidIndex++;
    //rotate loop and size of change until it overflows and restart from beginning
    if (invalidIndex + invalidSize > length) {
      invalidIndex = 0;
      invalidSize++;
      if (invalidIndex + invalidSize > length) {
        invalidSize = 1;
      }
    }
    String next = new String(c);
    history.add(next);
    observe(next);
    return next;
    // illegal characters: for ascii, base64, xml, json, url-encoded, custom
  }

  /** Set to only generate XML compliant characters. */
  public Text enableXml() {
    chars.enableXml();
    return this;
  }

  /** Only a-z, A-Z, 0-9. */
  public Text asciiLettersAndNumbersOnly() {
    chars.asciiLettersAndNumbersOnly();
    return this;
  }

  /**
   * Reduce the set of generated chars by given set of chars.
   *
   * @param charsToRemove The string of chars to be removed.
   */
  public Text reduceBy(String charsToRemove) {
    chars.reduceBy(charsToRemove);
    return this;
  }
  public Collection<String> getHistory() {
    return history;
  }

  @Override
  public void enableGUI() {
    if (guiEnabled) return;
    guiEnabled = true;
    gui = new TextGUI(this);
  }

  public Text enableInvalidLength(boolean invalid) {
    this.invalid = invalid;
    return this;
  }

}

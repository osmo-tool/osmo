package osmo.tester.model.data;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.TextGUI;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For generating strings of characters matching the given specification.
 *
 * @author Teemu Kanstren
 */
public class Text extends SearchableInput<String> {
  private static final Logger log = new Logger(Text.class);
  /** Minimum length of generated text. */
  private int min = 5;
  /** Maximum length of generated text. */
  private int max = 10;
  /** Used to create valid characters for text. */
  private final CharSet chars = new CharSet();
  /** History of generated text for this object. */
  private Collection<String> history = new ArrayList<>();
  /** How are the characters generated? */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Produce words of invalid length? Invalid is what we call length outside configured bounds.. */
  private boolean invalid = false;
  /** Length of previously generated value. Used to track upper/lower bounds violations when generating invalid data. */
  private int previousLength = -1;
  /** Min number to go over size if invalid length is applied. */
  private int minOffset = 1;
  /** Max number to go over size if invalid length is applied. */
  private int maxOffset = 5;
  /** Should we create one input of size zero? */
  private boolean zeroSize = false;
  /** Did we already provide zero size? */
  private boolean zeroDone = false;
  /** Probability for invalid characters to appear in the generated string if using the invalidX generation methods. 
   * Between 0-1. Default 0.5 (half are invalid). */
  private float invalidProbability = 0.5f;
  /** Do we generate a random string for toString() call or not? */
  private boolean randomToString = false;

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
    checkMinMax(min, max);
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

  private void checkMinMax(int min, int max) {
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
    checkMinMax(min, max);
  }

  /**
   * Not supported by this class.
   */
  @Override
  public Text setStrategy(DataGenerationStrategy algorithm) {
    switch (algorithm) {
      case RANDOM:
      case RANDOM_INVALID:
        this.strategy = algorithm;
        return this;
      default:
        throw new UnsupportedOperationException(Text.class.getSimpleName() + " supports only Random data generation strategy, given: " + algorithm.name() + ".");
    }
  }

  /**
   * Generates a sequence of characters, in length between the configured min and max values.
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
        return random();
      case RANDOM_INVALID:
        return randomInvalid();
      default:
        throw new IllegalStateException("Unsupported data generation strategy for " + Text.class.getName() + " (random and scripted only supported): " + strategy.getClass().getName());
    }
  }

  /**
   * Calculate length of text to generate next.
   * 
   * @return Length defined.
   */
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

  /**
   * Generate random valid characters.
   * 
   * @return Generated data.
   */
  public String random() {
    int length = length();
    char[] c = new char[length];
    for (int i = 0 ; i < length ; i++) {
      c[i] = chars.random();
    }
    String next = new String(c);
    history.add(next);
    record(next);
    return next;
  }

  /**
   * Creates values that are different from expected, replacing chars with invalid ones using some heuristics and
   * random probability.
   * Relates to generated charactgers, length is controlled with the "invalid" variable.
   *
   * @return the next generated value.
   */
  public String randomInvalid() {
    int length = length();
    char[] c = new char[length];
    for (int i = 0 ; i < length ; i++) {
      float f = rand.nextFloat(0, 1);
      if (f > invalidProbability) {
        c[i] = chars.next();
      } else {
        c[i] = chars.invalidRandom();
      }
    }
    String next = new String(c);
    history.add(next);
    record(next);
    return next;
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

  /**
   * Gives the list of previously generated texts.
   * 
   * @return The generation history.
   */
  public Collection<String> getHistory() {
    return history;
  }

  @Override
  public void enableGUI() {
    if (gui != null) return;
    gui = new TextGUI(this);
  }

  /**
   * Enables generation of text of invalid length.
   * 
   * @param invalid To enable it or not..
   * @return self for chaining.
   */
  public Text enableInvalidLength(boolean invalid) {
    this.invalid = invalid;
    return this;
  }

  public Text numbersOnly() {
    chars.numbersOnly();
    return this;
  }

  public boolean isRandomToString() {
    return randomToString;
  }

  public Text setRandomToString(boolean randomToString) {
    this.randomToString = randomToString;
    return this;
  }

  @Override
  public String toString() {
    if (randomToString) {
      return random();
    }
    return super.toString();
  }
}

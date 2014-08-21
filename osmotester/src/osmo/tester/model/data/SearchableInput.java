package osmo.tester.model.data;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ValueGUI;
import osmo.tester.model.VariableValue;

import java.util.Collection;

/**
 * A superclass for data variable classes whose value is to be recorded during test generation.
 *
 * @author Teemu Kanstren
 * @param <T> The type of input.
 */
public abstract class SearchableInput<T> implements VariableValue<T> {
  private static final Logger log = new Logger(SearchableInput.class);
  /** Variable name. */
  private String name;
  /** For providing values manually through a GUI. Enabled if non-null. */
  protected ValueGUI gui = null;
  /** The latest value that was generated by this object. */
  private T latestValue = null;
  /** Test suite access. */
  private TestSuite suite = null;
  /** Has the manual GUI definition been checked already? */
  private boolean checked = false;
  /** Should the values be tracked and stored in test steps? */
  private boolean stored = false;
  /** Provides for instance specific randomization. */
  protected Randomizer rand = null;

  protected SearchableInput() {
  }

  public void setSeed(long seed) {
    rand = new Randomizer(seed);
  }

  public boolean isChecked() {
    return checked;
  }

  public void setStored(boolean stored) {
    this.stored = stored;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public void setSuite(TestSuite suite) {
    this.suite = suite;
  }

  @Override
  public T value() {
    return getLatestValue();
  }

  public T getLatestValue() {
    return latestValue;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Records the given value into test generation history, including current test and step.
   * 
   * @param value To record.
   */
  public void record(T value) {
    latestValue = value;
    if (name == null) {
      return;
    }
    if (suite == null) {
      //this can happen if the variables are used to initialize model before test generation
      return;
    }
    if (!stored) return;
    suite.addValue(name, ""+value);
  }

  public Collection<?> getOptions() {
    throw new UnsupportedOperationException("This variable type does not support defining options");
  }

  public abstract void enableGUI();

  public void disableGUI() {
    gui = null;
  }
}

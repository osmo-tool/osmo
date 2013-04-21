package osmo.tester.model.data;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ValueGUI;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.VariableValue;

import java.util.Collection;

/**
 * A superclass for data variable classes that can be observed.
 *
 * @author Teemu Kanstren
 */
public abstract class SearchableInput<T> implements Input<T>, Output<T>, VariableValue {
  private static Logger log = new Logger(SearchableInput.class);
  /** Variable name. */
  private String name;
  /** Does the variable support "all" values need to be covered mode. */
  protected boolean allSupported = false;
  /** Provides values for this variable in playback mode. */
  private ScriptedValueProvider scripter = null;
  /** For providing values manually through a GUI. Enabled if non-null. */
  protected ValueGUI gui = null;
  private ValueSet<String> slices = null;
  /** The latest value that was generated. */
  private T latestValue = null;
  protected boolean guiEnabled = false;
  private TestSuite suite = null;
  /** Has the manual GUI definition been checked already? */
  private boolean checked = false;
  /** Should the values be tracked and stored in test steps? */
  private boolean stored = false;

  protected SearchableInput() {
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

  public ValueSet<String> getSlices() {
    if (slices == null) {
      slices = OSMOConfiguration.getSlicesFor(getName());
    }
    return slices;
  }

  public void setSuite(TestSuite suite) {
    this.suite = suite;
  }

  @Override
  public Object value() {
    return getLatestValue();
  }

  public T getLatestValue() {
    return latestValue;
  }

  public boolean isAllSupported() {
    return allSupported;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void observe(T value) {
    latestValue = value;
    if (name == null) {
      return;
    }
    if (suite == null) {
      //this can happen if the variables are used to initialize model before test generation
      return;
    }
    if (!stored) return;
    suite.getCurrentTest().addVariableValue(name, value, false);
  }

  public String scriptNextSerialized() {
    if (scripter == null) {
      scripter = OSMOConfiguration.getScripter();
    }
    return scripter.next(name);
  }

  public Collection<?> getOptions() {
    throw new UnsupportedOperationException("This variable type does not support defining options");
  }

  public abstract void enableGUI();

  public void disableGUI() {
    gui = null;
    guiEnabled = false;
  }
}

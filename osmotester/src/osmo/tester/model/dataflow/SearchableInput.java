package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ValueGUI;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.VariableValue;
import osmo.tester.model.dataflow.wrappers.ToStringWrapper;

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

  protected SearchableInput() {
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
  }

  public ToStringWrapper wrapper() {
    return new ToStringWrapper(this);
  }
}

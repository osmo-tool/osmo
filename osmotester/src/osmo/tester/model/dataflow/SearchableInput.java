package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.Observer;
import osmo.tester.gui.manualdrive.ValueGUI;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.dataflow.serialization.Deserializer;
import osmo.tester.model.dataflow.wrappers.ToStringWrapper;

import java.util.Collection;

/**
 * A superclass for data variable classes that can be observed.
 *
 * @author Teemu Kanstren
 */
public abstract class SearchableInput<T> implements Input<T>, Output<T> {
  private static Logger log = new Logger(SearchableInput.class);
  /** Variable name. */
  private String name;
  /** Does the variable support "all" values need to be covered mode. */
  protected boolean allSupported = false;
  /** Provides values for this variable in playback mode. */
  private ScriptedValueProvider scripter = null;
  /** For providing values manually through a GUI. Enabled if non-null. */
  protected ValueGUI gui = null;
  private ValueSet<T> slices = null;
  protected Deserializer<T> deserializer;
  /** The latest value that was generated. */
  private T latestValue = null;
  protected boolean guiEnabled = false;

  protected SearchableInput() {
  }

  public ValueSet<T> getSlices() {
    if (slices == null) {
      slices = OSMOConfiguration.getSlicesFor(getName(), deserializer);
    }
    return slices;
  }

  protected ValueSet<T> checkSlicing() {
    ValueSet<T> slices = getSlices();
    if (slices != null) {
      setStrategy(DataGenerationStrategy.SLICED);
    }
    return slices;
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
    Observer.observe(name, value);
  }

//  public void setScripter(ScriptedValueProvider scripter) {
//    this.scripter = scripter;
//  }

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

//  public abstract void addSlice(String serialized);

}

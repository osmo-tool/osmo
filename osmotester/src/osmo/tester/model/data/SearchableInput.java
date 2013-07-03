package osmo.tester.model.data;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ValueGUI;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.VariableValue;

import java.security.spec.EllipticCurve;
import java.util.Collection;

/**
 * A superclass for data variable classes that can be observed.
 *
 * @author Teemu Kanstren
 */
public abstract class SearchableInput<T> implements Input<T>, VariableValue<T> {
  private static Logger log = new Logger(SearchableInput.class);
  /** Variable name. */
  private String name;
  /** Provides values for this variable in playback mode. */
  private ScriptedValueProvider scripter = null;
  /** For providing values manually through a GUI. Enabled if non-null. */
  protected ValueGUI gui = null;
  /** The latest value that was generated. */
  private T latestValue = null;
  protected boolean guiEnabled = false;
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

  public Randomizer getRandomizer() {
    return rand;
  }
}

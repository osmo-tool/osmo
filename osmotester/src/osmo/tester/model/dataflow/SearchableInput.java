package osmo.tester.model.dataflow;

import java.util.Collection;

/**
 * A superclass for data variable classes that can be observed.
 *
 * @author Teemu Kanstren
 */
public abstract class SearchableInput<T> implements Input<T>, Output<T> {
  /** Variable name. */
  private String name;
  /** The observer to be notified when variable data is generated. */
  private InputObserver<T> observer = null;
  /** Does the variable support "all" values need to be covered mode. */
  protected boolean allSupported = false;
  private ScriptedValueProvider scripter = null;

  protected SearchableInput() {
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

  public void setObserver(InputObserver<T> observer) {
    this.observer = observer;
  }

  public void observe(T value) {
    if (name == null) {
      return;
    }
    observer.value(name, value);
  }

  public void setScripter(ScriptedValueProvider scripter) {
    this.scripter = scripter;
    setStrategy(DataGenerationStrategy.SCRIPTED);
  }

  public String scriptNextSerialized() {
    return scripter.next(name);
  }

  public Collection<?> getOptions() {
    throw new UnsupportedOperationException("This variable type does not support defining options");
  }
}

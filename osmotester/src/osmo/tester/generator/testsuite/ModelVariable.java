package osmo.tester.generator.testsuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/** @author Teemu Kanstren */
public class ModelVariable {
  private final String name;
  private Collection<Object> values = new ArrayList<Object>();

  public ModelVariable(String name) {
    this.name = name;
  }

  public void addValue(Object value) {
    values.add(value);
  }

  public String getName() {
    return name;
  }

  public Collection<Object> getValues() {
    return values;
  }

  public void addAll(ModelVariable testVar) {
    values.addAll(testVar.values);
  }

  public boolean contains(Object value) {
    return values.contains(value);
  }

  public void enableMerging() {
    values = new HashSet<Object>();
  }
}

package osmo.tester.generator.testsuite;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class ModelVariable {
  private final String name;
  private final Collection<Object> values = new ArrayList<Object>();

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
}

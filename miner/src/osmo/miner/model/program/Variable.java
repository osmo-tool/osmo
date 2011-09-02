package osmo.miner.model.program;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class Variable implements Comparable<Variable> {
  private final String name;
  private final Collection<String> values = new ArrayList<String>();

  public Variable(String name) {
    this.name = name;
  }

  public void addValue(String value) {
    values.add(value);
  }

  public String getName() {
    return name;
  }

  public Collection<String> getValues() {
    return values;
  }

  @Override
  public int compareTo(Variable o) {
    return name.compareTo(o.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Variable variable = (Variable) o;

    if (name != null ? !name.equals(variable.name) : variable.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}

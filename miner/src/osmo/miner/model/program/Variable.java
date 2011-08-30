package osmo.miner.model.program;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class Variable {
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
}

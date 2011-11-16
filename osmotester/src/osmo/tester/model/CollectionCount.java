package osmo.tester.model;

import java.util.Collection;

/**
 * This can be used to provide a @Variable value that always is recorded before and after
 * a step (transition) as a variable value.
 * <p/>
 * Example:
 * private Collection<String> names = new ArrayList<String>();
 *
 * @author Teemu Kanstren
 * @Variable private CollectionCount nameCount = new CollectionCount(names);
 * <p/>
 * This will result in one  variable being stored named "nameCount". At each point the size of "names" is
 * modified, it will be stored with the size as value between steps (transitions).
 */
public class CollectionCount implements VariableValue {
  /** The collection whose size should be counted. */
  private final Collection counted;

  /** @param counted The collection to be counted. */
  public CollectionCount(Collection counted) {
    this.counted = counted;
  }

  /**
   * Gives the value to be stored (collection size).
   *
   * @return The size of the collection.
   */
  @Override
  public Object value() {
    return counted.size();
  }
}

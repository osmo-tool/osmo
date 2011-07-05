package osmo.tester;

import java.util.HashMap;

/**
 * Extends the standard HashMap with a default value that is given when get(key) is invoked and there is
 * no value stored for that key (normal HashMap would return null).
 *
 * @author Teemu Kanstren
 */
public class HashMapWithDefaultValue<K, V> extends HashMap<K, V> {
  private final V defaultValue;

  public HashMapWithDefaultValue(V defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public V get(Object key) {
    V value = super.get(key);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}

package osmo.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 
 * For keeping a track of a set of values for a key.
 * 
 * @author Teemu Kanstren 
 */
public class SetMap<K, V> {
  private Map<K, Set<V>> map = new HashMap<>();
  
  public void addValue(K key, V value) {
    Set<V> values = map.get(key);
    if (values == null) {
      values = new HashSet<>();
      map.put(key, values);
    }
    values.add(value);
  }
  
  public void addValues(K key, Collection<V> values) {
    for (V value : values) {
      addValue(key, value);
    }
  }

  public int count() {
    int count = 0;
    for (Set<V> vs : map.values()) {
      count += vs.size();
    }
    return count;
  }
}

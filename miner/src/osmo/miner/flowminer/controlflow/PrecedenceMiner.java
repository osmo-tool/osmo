package osmo.miner.flowminer.controlflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Teemu Kanstren */
public class PrecedenceMiner {
  private Collection<String> invalidated = new HashSet<String>();
  private Map<String, Collection<String>> previous = new HashMap<String, Collection<String>>();

  public void process(String name) {
//    System.out.println("name:"+name);
    Collection<String> previouses = previous.get(name);
    if (previouses == null) {
      previouses = new ArrayList<String>();
      previous.put(name, previouses);
      for (String huuh : previous.keySet()) {
        String invalid = name + "->" + huuh;
        invalidated.add(invalid);
//        System.out.println("inv:"+invalid);
      }
      return;
    }
    for (String huuh : previous.keySet()) {
      if (!previouses.contains(huuh)) {
        String invalid = huuh + "->" + name;
        invalidated.add(invalid);
//        System.out.println("inv2:" + invalid);
      }
    }
    for (String huuh : previous.keySet()) {
      Collection<String> toCheck = previous.get(huuh);
      if (!huuh.equals(name)) {
        toCheck.add(name);
      }
    }
  }

  public Collection<String> getPatterns() {
    Collection<String> result = new ArrayList<String>();
    for (String name : previous.keySet()) {
      Collection<String> values = previous.get(name);
      for (String value : values) {
        result.add(value+"->"+name);
      }
    }
//    System.out.println("invalidated:"+invalidated);
    result.removeAll(invalidated);
    return result;
  }
}

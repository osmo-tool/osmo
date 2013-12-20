package osmo.tester.optimizer.reducer.invariants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Precedence {
  private Collection<String> invalidated = new HashSet<>();
  private Map<String, Collection<String>> previousMap = new HashMap<>();
  private String previous = null;
  public void process(String name) {
    Collection<String> previouses = previousMap.get(name);
    if (previouses == null) {
      previouses = new ArrayList<>();
      previousMap.put(name, previouses);
//      for (String huuh : previous.keySet()) {
//        String invalid = name + "->" + huuh;
//        invalidated.add(invalid);
//      }
    }
//TODO: lista testin edellisistä stepeistä ja näistä tarkastus onko ok, jos ei ole niin invalidi se on
    
//    for (String huuh : previous.keySet()) {
//      if (!previouses.contains(huuh)) {
//        String invalid = huuh + "->" + name;
//        invalidated.add(invalid);
//      }
//    }
//    for (String huuh : previous.keySet()) {
//      Collection<String> toCheck = previous.get(huuh);
//      if (!huuh.equals(name)) {
//        toCheck.add(name);
//      }
//    }
  }

  public Collection<String> getPatterns() {
    Collection<String> result = new ArrayList<>();
    for (String name : previousMap.keySet()) {
      Collection<String> values = previousMap.get(name);
      for (String value : values) {
        result.add(value+"->"+name);
      }
    }
    result.removeAll(invalidated);
    return result;
  }
}

package osmo.tester.scripter.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * For holding a set of test scripts (test suite).
 * 
 * @author Teemu Kanstren
 */
public class TestScripts {
  /** The scripts to hold. */
  private List<TestScript> scripts = new ArrayList<>();
  /** For iterating over the scripts. */
  private Iterator<TestScript> i = null;

  public void addScript(TestScript script) {
    scripts.add(script);
  }
  
  public void init() {
    i = scripts.iterator();
  }
  
  public TestScript next() {
    if (i == null) init();
    return i.next();
  }
  
  public boolean hasNext() {
    return i.hasNext();
  }
}

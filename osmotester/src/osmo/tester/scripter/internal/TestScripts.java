package osmo.tester.scripter.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TestScripts {
  private List<TestScript> scripts = new ArrayList<>();
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

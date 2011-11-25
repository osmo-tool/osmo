package osmo.tester.scripting.manual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** @author Teemu Kanstren */
public class TestScript implements Iterable<ScriptStep> {
  private List<ScriptStep> steps = new ArrayList<ScriptStep>();
  private ScriptStep lastAdded = null;

  public void addStep(String name) {
    ScriptStep step = new ScriptStep(name);
    this.lastAdded = step;
    steps.add(step);
  }

  public void addValue(String variable, String value) {
    lastAdded.addValue(variable, value);
  }

  @Override
  public Iterator<ScriptStep> iterator() {
    return steps.iterator();
  }

  public List<ScriptStep> getSteps() {
    return steps;
  }
}

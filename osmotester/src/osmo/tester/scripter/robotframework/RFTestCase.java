package osmo.tester.scripter.robotframework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class RFTestCase {
  private Collection<RFTestStep> steps = new ArrayList<RFTestStep>();
  private final String name;
  private boolean read = false;
  private final int cellCount;

  public RFTestCase(String name, int cellCount) {
    this.name = name;
    this.cellCount = cellCount;
  }

  public String getName() {
    if (read) {
      return "";
    }
    read = true;
    return name;
  }

  public Collection<RFTestStep> getSteps() {
    return steps;
  }

  public void addStep(String keyword, String[] params) {
    RFTestStep step = new RFTestStep(keyword, cellCount, params);
    steps.add(step);
  }

  public void addStepWithResult(String keyword, String variableName, String[] params) {
    RFTestStep step = new RFTestStep(keyword, cellCount, variableName, params);
    steps.add(step);
  }
}

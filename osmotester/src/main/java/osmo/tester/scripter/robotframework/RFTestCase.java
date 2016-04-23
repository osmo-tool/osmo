package osmo.tester.scripter.robotframework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a test case for the robot framework scripter.
 *
 * @author Teemu Kanstren
 */
public class RFTestCase {
  /** The test steps in this test case. */
  private Collection<RFTestStep> steps = new ArrayList<>();
  /** The name of the test case. */
  private final String name;
  /** Defines is the name has been queried or not, used to only print the name once in the table, while having a simple template. */
  private boolean read = false;
  /** Number of cells in the RF test case HTML table, used for pretty printing. */
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

  public void addStep(String keyword, RFParameter[] params) {
    RFTestStep step = new RFTestStep(keyword, cellCount, params);
    steps.add(step);
  }

  public void addStepWithResult(String keyword, String variableName, RFParameter[] params) {
    RFTestStep step = new RFTestStep(keyword, cellCount, variableName, params);
    steps.add(step);
  }
}

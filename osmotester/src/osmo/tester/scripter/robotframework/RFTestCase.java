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

  public RFTestCase(String name) {
    this.name = name;
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

  public void addStep(String keyword, String p1, String p2) {
    RFTestStep step = new RFTestStep(keyword, p1, p2);
    steps.add(step);
  }
}

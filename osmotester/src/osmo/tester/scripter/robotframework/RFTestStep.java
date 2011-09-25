package osmo.tester.scripter.robotframework;

/**
 * @author Teemu Kanstren
 */
public class RFTestStep {
  private final String action;
  private final String p1;
  private final String p2;

  public RFTestStep(String action, String p1, String p2) {
    this.action = action;
    this.p1 = p1;
    this.p2 = p2;
  }

  public String getAction() {
    return action;
  }

  public String getP1() {
    return p1;
  }

  public String getP2() {
    return p2;
  }
}

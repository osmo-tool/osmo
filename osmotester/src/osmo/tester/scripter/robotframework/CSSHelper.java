package osmo.tester.scripter.robotframework;

/**
 * @author Teemu Kanstren
 */
public class CSSHelper {
  private boolean alt = false;

  public void reset() {
    alt = false;
  }

  public String getTableClass() {
    if (alt) {
      alt = false;
      return " class='alt'";
    }
    alt = true;
    return "";
  }
}

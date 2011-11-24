package osmo.tester.scripter.robotframework;

/**
 * A helper class to pretty-format the velocity templates creating the robot framework test scripts.
 *
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

  @Override
  public String toString() {
    return getTableClass();
  }
}

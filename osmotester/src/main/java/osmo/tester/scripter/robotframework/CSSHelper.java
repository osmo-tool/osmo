package osmo.tester.scripter.robotframework;

/**
 * A helper class to pretty-format the velocity templates creating the robot framework test scripts.
 *
 * @author Teemu Kanstren
 */
public class CSSHelper {
  /** We alter between different colors/ui classes for HTML rows. */
  private boolean alt = false;

  public void reset() {
    alt = false;
  }

  /**
   * Creates CSS style tags to make every second row of table look different.
   * 
   * @return alt for odds, none for evens (or is it the other way around?)
   */
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

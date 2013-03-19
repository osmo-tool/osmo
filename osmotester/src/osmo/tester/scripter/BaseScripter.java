package osmo.tester.scripter;

/**
 * A class to be extended for scripter implementation if you do not wish to implement all the methods yourself.
 *
 * @author Teemu Kanstren
 */
public class BaseScripter implements OSMOScripter {
  @Override
  public void startSuite() {
  }

  @Override
  public void startTest() {
  }

  @Override
  public void step(String name, Object... params) {
  }

  @Override
  public void endTest() {
  }

  @Override
  public void endSuite() {
  }
}

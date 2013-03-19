package osmo.tester.scripter;

/**
 * Defines a general scripting interface.
 * Currently a first draft for future extension.
 *
 * @author Teemu Kanstren
 */
public interface OSMOScripter {
  public void startSuite();

  public void startTest();

  public void step(String name, Object... params);

  public void endTest();

  public void endSuite();
}

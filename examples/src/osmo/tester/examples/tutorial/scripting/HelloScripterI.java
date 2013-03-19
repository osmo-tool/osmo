package osmo.tester.examples.tutorial.scripting;

/** @author Teemu Kanstren */
public interface HelloScripterI {
  public void hello(String name, double size);
  public void world(String name, double range);
  public void startTest();
  public void endTest();
  public void write();
}

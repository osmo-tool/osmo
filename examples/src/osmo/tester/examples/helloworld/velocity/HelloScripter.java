package osmo.tester.examples.helloworld.velocity;

/** @author Teemu Kanstren */
public interface HelloScripter {
  public void hello(String name, double size);

  public void world(String name, double range);

  public void startTest();

  public void endTest();

  public void write();
}

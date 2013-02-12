package osmo.tester.examples.tutorial.scripting;

/** @author Teemu Kanstren */
public class HelloConsoleScripter implements HelloScripterI {
  public void hello(String name, double size) {
    System.out.println("HELLO "+name+" ("+size+")");
  }

  public void world(String name, double range) {
    System.out.println("WORLD "+name+" ("+range+")");
  }

  public void startTest() {
    System.out.println("TEST START");
  }

  public void endTest() {
    System.out.println("TEST END");
  }

  @Override
  public void write() {
  }
}

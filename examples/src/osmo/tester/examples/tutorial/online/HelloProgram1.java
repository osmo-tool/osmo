package osmo.tester.examples.tutorial.online;

/** @author Teemu Kanstren */
public class HelloProgram1 {
  private int counter = 0;

  public String hello(String name, int size) {
    counter++;
    return "hi dude, "+name;
  }

  public String world(String world, double range) {
    counter++;
    return range + "? swweeet, dude";
  }
}

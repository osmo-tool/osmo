package osmo.tester.examples.tutorial.online;

/** @author Teemu Kanstren */
public class HelloProgram2 {
  private int counter = 0;

  public String hello(String name, int size) {
    counter++;
    if (counter % 15 == 10) {
      return "whoopsidaisy";
    }
    return "hi dude, "+name;
  }

  public String world(String world, double range) {
    counter++;
    return range + "? swweeet, dude";
  }
}

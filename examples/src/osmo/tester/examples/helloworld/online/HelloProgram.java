package osmo.tester.examples.helloworld.online;

/** @author Teemu Kanstren */
public class HelloProgram {
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
    return range+"? thats pretty mighty, dude";
  }
}

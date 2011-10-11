package osmo.tester.examples.vendingmachine;

import java.io.PrintStream;

/**
 * A  very simple example of a "scripter" used to write test scripts from a test model.
 * 
 * @author Teemu Kanstren
 */
public class Scripter {
  private final PrintStream out;

  public Scripter(PrintStream out) {
    this.out = out;
  }

  public void step(String step) {
    out.print(step+"\n");
  }
}

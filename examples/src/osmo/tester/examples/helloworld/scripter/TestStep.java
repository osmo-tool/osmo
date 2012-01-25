package osmo.tester.examples.helloworld.scripter;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestStep {
  private final String name;
  private Collection<StepArg> args = new ArrayList<StepArg>();

  public TestStep(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addArg(String name, String value) {
    args.add(new StepArg(name, value));
  }

  public Collection<StepArg> getArgs() {
    return args;
  }
}

package osmo.tester.examples.helloworld.modular;

import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

/** @author Teemu Kanstren */
public class SeparateState {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<String>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<String>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<Integer>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<Double>(0.1d, 5.2d);

  public SeparateState() {
    names.setStrategy(DataGenerationStrategy.BALANCING);
  }

  public void reset() {
    helloCount = 0;
    worldCount = 0;
  }

  public boolean canHello() {
    return helloCount == worldCount;
  }

  public String nextName() {
    return names.next();
  }

  public int nextSize() {
    return sizes.next();
  }

  public void didHello() {
    helloCount++;
  }

  public boolean canWorld() {
    return helloCount > worldCount;
  }

  public void didWorld() {
    worldCount++;
  }

  public String nextWorld() {
    return worlds.next();
  }

  public double nextRange() {
    return ranges.next();
  }
}

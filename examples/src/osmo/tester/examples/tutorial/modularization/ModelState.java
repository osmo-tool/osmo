package osmo.tester.examples.tutorial.modularization;

import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class ModelState {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);

  public ModelState() {
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

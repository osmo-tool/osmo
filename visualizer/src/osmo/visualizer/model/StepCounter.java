package osmo.visualizer.model;

/** @author Teemu Kanstren */
public class StepCounter {
  private int count = 0;

  public StepCounter() {
    this.count = 1;
  }

  public void increment() {
    count++;
  }
  
  @Override
  public String toString() {
    return ""+count;
  }
}

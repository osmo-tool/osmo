package osmo.tester.optimizer.online;

/**
 * Ends search after there is no improvement observed on fitness for given number of iterations.
 *
 * @author Teemu Kanstren
 */
public class PeakEndCondition implements SearchEndCondition {
  private final int threshold;

  public PeakEndCondition(int threshold) {
    this.threshold = threshold;
  }

  @Override
  public boolean shouldEnd(SearchState state) {
    int diff = state.getIterationCount() - state.getBestIteration();
    return diff >= threshold;
  }
}

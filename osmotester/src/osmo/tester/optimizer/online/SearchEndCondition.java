package osmo.tester.optimizer.online;

/**
 * Defines when {@link SearchingOptimizer} stops searching for a more optimal solution.
 *
 * @author Teemu Kanstren
 */
public interface SearchEndCondition {
  /**
   * @param state The current state of search.
   * @return True if search should end.
   */
  public boolean shouldEnd(SearchState state);
}

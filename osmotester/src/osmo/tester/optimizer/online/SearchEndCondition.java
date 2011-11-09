package osmo.tester.optimizer.online;

/** @author Teemu Kanstren */
public interface SearchEndCondition {
  public boolean shouldEnd(SearchState state);
}

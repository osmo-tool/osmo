package osmo.tester.optimizer.online;

/** @author Teemu Kanstren */
public interface SearchListener {
  public void updateBest(Candidate candidate);
}

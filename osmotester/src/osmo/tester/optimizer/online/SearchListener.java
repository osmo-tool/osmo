package osmo.tester.optimizer.online;

import osmo.tester.optimizer.Candidate;

/** @author Teemu Kanstren */
public interface SearchListener {
  public void updateBest(Candidate candidate);
}

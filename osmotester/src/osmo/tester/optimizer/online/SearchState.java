package osmo.tester.optimizer.online;

import osmo.common.log.Logger;

/** @author Teemu Kanstren */
public class SearchState {
  private static final Logger log = new Logger(SearchState.class);
  private int iterationCount = 0;
  private Candidate best = null;
  private int bestIteration = 0;

  public void incrementIterationCount() {
    iterationCount++;
  }

  public int getIterationCount() {
    return iterationCount;
  }

  public Candidate getBest() {
    return best;
  }

  public int getBestIteration() {
    return bestIteration;
  }

  public void checkCandidate(Candidate current) {
    if (best == null || best.getFitness() < current.getFitness()) {
      best = current;
      bestIteration = iterationCount;
      log.debug("updated best fitness, iteration " + iterationCount + " best:" + best.getFitness());
      System.out.println("updated best fitness, iteration " + iterationCount + " best:" + best.getFitness());
    }
  }
}

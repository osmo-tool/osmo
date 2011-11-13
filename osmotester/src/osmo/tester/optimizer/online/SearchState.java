package osmo.tester.optimizer.online;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class SearchState {
  private static final Logger log = new Logger(SearchState.class);
  private Candidate best = null;
  private int bestIteration = 0;
  private int totalIterationCount = 0;
  private SearchListener listener = null;
  private SearchPhase phase = new SearchPhase();
  private Collection<SearchPhase> phases = new ArrayList<SearchPhase>();

  public SearchState() {
    startPhase();
  }

  public void incrementIterationCount() {
    totalIterationCount++;
    phase.incrementIterationCount();
  }

  public int getIterationCount() {
    return phase.getIterationCount();
  }

  public Candidate getBest() {
    return best;
  }

  public int getBestIteration() {
    return phase.getBestIteration();
  }

  public void startPhase() {
    System.out.println("next phase:"+phase.getId()+", "+phase.getIterationCount());
    phase.end();
    phase = new SearchPhase();
    phases.add(phase);
  }

  public void checkCandidate(Candidate current) {
    if (best == null || best.getFitness() < current.getFitness()) {
      best = current;
      bestIteration = totalIterationCount;
      phase.setBestIteration();
      log.debug("updated best fitness, phase "+phase.getId()+", iteration " + phase.getIterationCount() + " best:" + best.getFitness());
      System.out.println("updated best fitness, phase "+phase.getId()+", iteration " + phase.getIterationCount() + " best:" + best.getFitness());
      if (listener != null) {
        listener.updateBest(best);
      }
    }
  }

  public void setListener(SearchListener listener) {
    this.listener = listener;
  }

  private static class SearchPhase {
    private long startTime;
    private long endTime;
    private int iterationCount = 0;
    private int bestIteration = 0;
    private int id = 0;
    private static int nextId = 0;

    private SearchPhase() {
      startTime = System.currentTimeMillis();
      id = nextId++;
    }

    public void incrementIterationCount() {
      iterationCount++;
    }

    public int getBestIteration() {
      return bestIteration;
    }

    public void setBestIteration() {
      this.bestIteration = iterationCount;
    }

    public long getStartTime() {
      return startTime;
    }

    public long getEndTime() {
      return endTime;
    }

    public void end() {
      endTime = System.currentTimeMillis();
    }

    public int getIterationCount() {
      return iterationCount;
    }

    public int getId() {
      return id;
    }
  }
}

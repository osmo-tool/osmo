package osmo.tester.optimizer.online;

import osmo.common.log.Logger;
import osmo.tester.optimizer.Candidate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Maintains overall state for the genetic algorithm based test suite optimization search.
 *
 * @author Teemu Kanstren
 */
public class SearchState {
  private static final Logger log = new Logger(SearchState.class);
  /** The best candidate so far. */
  private Candidate best = null;
  /** The overall iteration where it was found (not phase internal). */
  private int bestIteration = 0;
  /** Total count of iterations done, including all phases. */
  private int totalIterationCount = 0;
  /** A listener that is notified when the "best" value is updated. */
  private SearchListener listener = null;
  /** The search phase running, used to run several iterations of iterations with different mutation values. */
  private SearchPhase phase = new SearchPhase();
  /** The iterations that have been run. */
  private Collection<SearchPhase> phases = new ArrayList<>();

  public SearchState() {
    SearchPhase.nextId = 0;
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

  /** Starts a new phase, with different values to use by end conditions and so on. */
  public void startPhase() {
    System.out.println("next phase:" + phase.getId() + ", " + phase.getIterationCount());
    phase.end();
    phase = new SearchPhase();
    phases.add(phase);
  }

  /**
   * Check if the given candidate is better than the one currently marked as best.
   * If the given candidate has higher fitness than previous best candidate it is used as the new one.
   *
   * @param current The candidate to be checked.
   */
  public void checkCandidate(Candidate current) {
    if (best == null || best.getFitness() < current.getFitness()) {
      best = current;
      bestIteration = totalIterationCount;
      phase.setBestIteration();
      log.debug("updated best fitness, phase " + phase.getId() + ", iteration " + phase.getIterationCount() + " best:" + best.getFitness());
      System.out.println("updated best fitness, phase " + phase.getId() + ", iteration " + phase.getIterationCount() + " best:" + best.getFitness());
      if (listener != null) {
        listener.updateBest(best);
      }
    }
  }

  /**
   * Sets the listener to be notified when the best one chances.
   *
   * @param listener The listener. Only one is supported.
   */
  public void setListener(SearchListener listener) {
    this.listener = listener;
  }

  /** Describes a phase of search. A phase is one of several iterations run with different mutation parameters. */
  private static class SearchPhase {
    /** The time when the phase was started. */
    private long startTime;
    /** The time when the phase ended. */
    private long endTime;
    /** Number of iterations run in the phase (so far). */
    private int iterationCount = 0;
    /** If the best value is updated in this phase, this shows the last iteration inside phase when best was updated. */
    private int bestIteration = 0;
    /** The id of this phase, starts from one and increases as it goes.. */
    private int id = 0;
    /** The next identifier for next phase. */
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

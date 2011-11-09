package osmo.tester.optimizer.online;

import java.util.Comparator;

/** @author Teemu Kanstren */
public class FitnessComparator implements Comparator<Candidate> {
  @Override
  public int compare(Candidate c1, Candidate c2) {
    return c1.getFitness()-c2.getFitness();
  }
}

package osmo.tester.optimizer;

import java.util.Comparator;

/**
 * Used to sort the candidates according to their fitness value.
 *
 * @author Teemu Kanstren
 */
public class FitnessComparator implements Comparator<Candidate> {
  @Override
  public int compare(Candidate c1, Candidate c2) {
    return c1.getFitness() - c2.getFitness();
  }
}

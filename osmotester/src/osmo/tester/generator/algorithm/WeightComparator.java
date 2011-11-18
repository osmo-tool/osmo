package osmo.tester.generator.algorithm;

import osmo.tester.model.FSMTransition;

import java.util.Comparator;

/** @author Teemu Kanstren */
public class WeightComparator implements Comparator<FSMTransition> {
  @Override
  public int compare(FSMTransition o1, FSMTransition o2) {
    return o1.getWeight() - o2.getWeight();
  }
}

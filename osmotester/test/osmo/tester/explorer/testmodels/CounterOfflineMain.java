package osmo.tester.explorer.testmodels;

import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.GreedyOptimizer;

import java.util.List;

/** @author Teemu Kanstren */
public class CounterOfflineMain {
  public static void main(String[] args) {
    ExplorationConfiguration gc = new ExplorationConfiguration(null, 1, 52);
    gc.setStepWeight(30);
    gc.setStepPairWeight(20);
    gc.setDefaultValueWeight(7);
    gc.setVariableCountWeight(5);
    gc.setRequirementWeight(20);
    gc.setMaxTestLength(10);
    gc.setMinSuiteScore(50);
    gc.setMaxSuiteLength(10);
    gc.setSuitePlateauThreshold(50);
    GreedyOptimizer greedy = new GreedyOptimizer(gc, 200, new Probability(0.2), 52);
    greedy.addModelClass(CounterModel.class);
    List<TestCase> cases = greedy.search();
    for (TestCase test : cases) {
      System.out.println(test.getAttribute("test-script"));
    }
  }
}

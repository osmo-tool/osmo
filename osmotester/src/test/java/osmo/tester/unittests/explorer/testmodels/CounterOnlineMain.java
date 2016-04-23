package osmo.tester.unittests.explorer.testmodels;

import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;

import java.util.List;

/** @author Teemu Kanstren */
public class CounterOnlineMain {
  public static void main(String[] args) {
    ModelFactory factory = new CounterFactory();
    OSMOExplorer osmo = new OSMOExplorer();
    ExplorationConfiguration config = new ExplorationConfiguration(factory, 3, 55);
    config.setStepWeight(30);
    config.setStepPairWeight(20);
    config.setDefaultValueWeight(7);
    config.setVariableCountWeight(5);
    config.setRequirementWeight(20);
    config.setMaxTestLength(10);
    config.setMinSuiteScore(50);
    config.setMaxSuiteLength(10);
    config.setSuitePlateauThreshold(50);
    osmo.explore(config);
    List<TestCase> cases = osmo.getSuite().getAllTestCases();
    for (TestCase test : cases) {
      System.out.println(test.getAttribute("test-script"));
    }

  }
}

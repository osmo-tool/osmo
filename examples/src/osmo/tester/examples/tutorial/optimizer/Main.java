package osmo.tester.examples.tutorial.optimizer;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.endcondition.WeightedEndConditionSet;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.greedy.MultiGreedy;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class Main {
  public static void main(String[] args) {
    long seed = System.currentTimeMillis();
    if (args.length > 0) {
      seed = Long.parseLong(args[0]);
    }
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new CalendarFactory());
    EndCondition ec1 = new LengthProbability(1, 0.5);
    EndCondition ec2 = new LengthProbability(10, 0.2);
    EndCondition ec3 = new LengthProbability(50, 0.5);
    WeightedEndConditionSet set = new WeightedEndConditionSet(10, ec1, 100, ec2);
    set.addEndCondition(20, ec3);
    oc.setTestEndCondition(set);
    ScoreConfiguration sc = new ScoreConfiguration();
    MultiGreedy greedy = new MultiGreedy(oc, sc, seed);
    greedy.setTimeout(10);
    List<TestCase> tests = greedy.search();
    int i = 1;
    for (TestCase test : tests) {
      TestUtils.write((String)test.getAttribute("script"), "calendar-example/test"+i+".html");
      i++;
    }
    HTMLCoverageReporter html = new HTMLCoverageReporter(greedy.getFinalCoverage(), tests, greedy.getFsm());
    String report = html.getTraceabilityMatrix();
    TestUtils.write(report, "calendar-example/coverage.html");
  }
}

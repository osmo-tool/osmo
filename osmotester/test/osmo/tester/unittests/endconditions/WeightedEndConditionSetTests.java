package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.common.Randomizer;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.endcondition.WeightedEndConditionSet;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.explorer.testmodels.CounterFactory;
import osmo.tester.unittests.testmodels.CalculatorModel;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren.
 */
public class WeightedEndConditionSetTests {
  @Test
  public void twoLengths() {
    List<Integer> twoList = new ArrayList<>();
    List<Integer> twentyLIst = new ArrayList<>();
    Randomizer rand = new Randomizer(100);
    for (int i = 0 ; i < 1000 ; i++) {
      OSMOTester tester = new OSMOTester();
      tester.setPrintCoverage(false);
      tester.setModelFactory(new CounterFactory());
      WeightedEndConditionSet set = new WeightedEndConditionSet(10, new Length(2), 90, new Length(20));
      tester.setTestEndCondition(set);
      tester.setSuiteEndCondition(new Length(20));
      tester.generate(rand.nextLong());
      TestSuite suite = tester.getSuite();
      List<TestCase> history = suite.getAllTestCases();
      assertEquals("Number of tests generated", 20, history.size());
      int twos = 0;
      int twenties = 0;
      for (TestCase test : history) {
        if (test.getLength() == 2) twos++;
        if (test.getLength() == 20) twenties++;
      }
      twoList.add(twos);
      twentyLIst.add(twenties);
    }
//    System.out.println(twoList);
//    System.out.println(twentyLIst);
    double sum2 = 0;
    double sum20 = 0;
    for (Integer two : twoList) {
      sum2 += two;

    }
    double avg2 = sum2 / twoList.size();
    for (Integer twenty : twentyLIst) {
      sum20 += twenty;
    }
    double avg20 = sum20 / twentyLIst.size();
    assertEquals("Average number of tests of length 2", 2f, avg2, 0.05);
    assertEquals("Average number of tests of length 20", 18f, avg20, 0.05);
  }

  @Test
  public void twoProbabilities() {
    List<Integer> lengths = new ArrayList<>();
    Randomizer rand = new Randomizer(1111);
    int sum = 0;
    for (int i = 0 ; i < 100 ; i++) {
      OSMOTester tester = new OSMOTester();
      tester.setPrintCoverage(false);
      tester.setModelFactory(new CounterFactory());
      WeightedEndConditionSet set = new WeightedEndConditionSet(10, new Probability(0.5), 90, new Probability(0.1));
      tester.setTestEndCondition(set);
      tester.setSuiteEndCondition(new Length(20));
      tester.generate(rand.nextLong());
      TestSuite suite = tester.getSuite();
      List<TestCase> history = suite.getAllTestCases();
      assertEquals("Number of tests generated", 20, history.size());
      for (TestCase test : history) {
        lengths.add(test.getLength());
        sum += test.getLength();
      }
    }
    double avgLength = sum/lengths.size();
    System.out.println(lengths);
    assertEquals("Average test length", 10f, avgLength, 0);
  }
}

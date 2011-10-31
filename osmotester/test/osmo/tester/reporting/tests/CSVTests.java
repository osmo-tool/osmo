package osmo.tester.reporting.tests;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ParameterModel1;

import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.getResource;
import static osmo.common.TestUtils.unifyLineSeparators;

/** @author Teemu Kanstren */
public class CSVTests {
  @Test
  public void csvSteps() {
    OSMOTester osmo = new OSMOTester();
    ParameterModel1 model = new ParameterModel1();
    osmo.addModelObject(model);
    osmo.addTestEndCondition(new Length(10));
    osmo.addSuiteEndCondition(new Length(2));
    osmo.setRandom(new Random(1));
    osmo.generate();
    List<String> report = model.getResult();

    assertEquals("Should generate 2 reports for 2 tests", 2, report.size());
    String report1 = unifyLineSeparators(report.get(0), "\n");
    String report2 = unifyLineSeparators(report.get(1), "\n");
    String expected = getResource(getClass(), "expected-test1.csv");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generated report for test1", expected, report1);
    expected = getResource(getClass(), "expected-test2.csv");
    assertEquals("Generated report for test2", expected, report2);
  }
}

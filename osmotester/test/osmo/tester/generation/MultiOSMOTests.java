package osmo.tester.generation;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.multi.MOSMOConfiguration;
import osmo.tester.generator.multi.MultiOSMO;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.parser.ModelObject;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class MultiOSMOTests {
  @Test
  public void noFactoryError() {
    TestUtils.recursiveDelete("osmo-output");
    MultiOSMO mosmo = new MultiOSMO(4, 444);
    OSMOConfiguration config = mosmo.getConfig();
    try {
      config.addModelObject(new ValidTestModel1());
      fail("Trying to generate using MOSMO with no factory should fail.");
    } catch (Exception e) {
      assertEquals("Error msg for no factory", MOSMOConfiguration.ERROR_MSG, e.getMessage());
    }
  }

  @Test
  public void generate4() {
    TestUtils.recursiveDelete("osmo-output");
    MultiOSMO mosmo = new MultiOSMO(4, 444);
    OSMOConfiguration config = mosmo.getConfig();
    config.setFactory(new MyModelFactory());
    config.setTestEndCondition(new Length(10));
    config.setSuiteEndCondition(new Length(5));
    List<TestCase> tests = mosmo.generate();
    assertEquals("Number of tests for 4 generators with 5 in suite", 20, tests.size());
    List<String> reports = TestUtils.listFiles("osmo-output", ".csv", false);
    assertEquals("Number of reports generated", 5, reports.size());
    List<String> traces = TestUtils.listFiles("osmo-output", ".html", false);
    assertEquals("Number of traces generated", 4, traces.size());
  }
  
  private static class MyModelFactory implements ModelFactory {
    @Override
    public Collection<ModelObject> createModelObjects() {
      Collection<ModelObject> result = new ArrayList<>();
      result.add(new ModelObject(new ValidTestModel2(new Requirements())));
      return result;
    }
  }
}

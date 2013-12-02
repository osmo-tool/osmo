package osmo.tester.unittests.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.unittests.testmodels.CoverageValueModel1;
import osmo.tester.unittests.testmodels.CoverageValueModel2;
import osmo.tester.unittests.testmodels.PartialModel1;
import osmo.tester.unittests.testmodels.PartialModel2;
import osmo.tester.unittests.testmodels.ValidTestModel4;
import osmo.tester.unittests.testmodels.ValidTestModel6;

import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CoverageValueMethodTests {
  private OSMOTester osmo = null;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
  }

  @Test
  public void noState() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    ValidTestModel6 model = new ValidTestModel6();
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);
    TestSuite suite = osmo.getSuite();
    assertEquals("State over generation with no state defined", "{}", suite.getCoverage().getStates().toString());
    assertEquals("Sequence of steps", "[t2, t1, t1]", suite.getAllTestCases().get(0).getSteps().toString());
  }

  @Test
  public void twoStatesNoVariables() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    Requirements reqs = new Requirements();
    PartialModel1 pm1 = new PartialModel1(reqs);
    factory.add(pm1);
    factory.add(new PartialModel2(reqs));
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);
    TestSuite suite = osmo.getSuite();

    String expected = "{state1=[Hello-hello1, world-hello1, epixx-hello1], state2=[Hello-hello2, world-hello2, epixx-hello2]}";
    assertEquals("State over generation with two states defined", expected, suite.getCoverage().getStates().toString());

    assertEquals("Number of test variables", 0, suite.getCoverage().getVariables().size());
  }

  @Test
  public void multipleStatesCustomVariable() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    ValidTestModel4 model = new ValidTestModel4(NullPrintStream.stream);
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);
    TestCase testCase = osmo.getSuite().getAllTestCases().get(0);

    TestSuite suite = osmo.getSuite();
    String expected = "{my-state=[1, 2, 3]}";
    assertEquals("State when generating", expected, suite.getCoverage().getStates().toString());

    Map<String, Collection<String>> testVariables = suite.getCoverage().getVariableValues();
    assertEquals("Number of test variables", 2, testVariables.size());
    expected = "{my_item=[hello, world], your_item=[foobar]}";
    assertEquals("Test variables", expected, testVariables.toString());
  }

  @Test
  public void stateAndVariables() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    CoverageValueModel1 model = new CoverageValueModel1();
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);

    TestSuite suite = osmo.getSuite();
    String expected = "{my-state=[1, 2, 3]}";
    assertEquals("State when generating", expected, suite.getCoverage().getStates().toString());

    Map<String, Collection<String>> testVariables = suite.getCoverage().getVariableValues();
    assertEquals("Number of test variables", 3, testVariables.size());
    expected = "{firstName=[hello], lastName=[world], global=[bad]}";
    assertEquals("Test variables", expected, testVariables.toString());
  }

  @Test
  public void oneStateSeveralValues() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    CoverageValueModel2 model = new CoverageValueModel2();
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(222);

    TestSuite suite = osmo.getSuite();
    String expected = "{my-state=[4, 1, 3]}";
    assertEquals("State when generating", expected, suite.getCoverage().getStates().toString());

    Map<String, Collection<String>> testVariables = suite.getCoverage().getVariableValues();
    assertEquals("Number of test variables", 1, testVariables.size());
    expected = "{range=[1]}";
    assertEquals("Test variables", expected, testVariables.toString());
  }
}

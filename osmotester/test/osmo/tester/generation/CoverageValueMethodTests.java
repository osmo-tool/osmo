package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.TestModels;
import osmo.tester.testmodels.CoverageValueModel1;
import osmo.tester.testmodels.CoverageValueModel2;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;
import osmo.tester.testmodels.ValidTestModel4;
import osmo.tester.testmodels.ValidTestModel6;

import java.util.Collection;
import java.util.List;
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
    ValidTestModel6 model = new ValidTestModel6(":null:-null-:null:-null-:null:-null-");
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);
    TestSuite suite = osmo.getSuite();
    assertEquals("State over generation with no state defined", ":null:-null-:null:-null-:null:-null-", model.getStates());
    assertEquals("Sequence of steps", "[t2, t1, t1]", suite.getAllTestCases().get(0).getSteps().toString());

    Map<String, ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 1, testVariables.size());
    ModelVariable mv = testVariables.values().iterator().next();
    assertEquals("Test variables", "[2, 1]", mv.getValues().toString());

    Map<String, ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 1, stepVariables.size());
    mv = stepVariables.values().iterator().next();
    assertEquals("Step variables", "[2, 1, 1]", mv.getValues().toString());
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

    String expected = ":null::null::state1([Hello-hello1])::state2([Hello-hello2])::null::null::state1([world-hello1])::state2([world-hello2])::null::null::state1([epixx-hello1])::state2([epixx-hello2]):";
    assertEquals("State over generation with two states defined", expected, pm1.getStates());

    Map<String, ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 0, testVariables.size());
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
    assertEquals("State over generation with no state defined", ":null:-my-state([1])-:null:-my-state([2])-:null:-my-state([3])-", model.getStates());
    TestCase testCase = osmo.getSuite().getAllTestCases().get(0);

    List<TestCaseStep> steps = testCase.getSteps();

    TestCaseStep step = steps.get(0);
    assertEquals("Step name", "hello", step.getName());
    assertEquals("State in step", "my-state([1])", step.getStatesFor("my-state").toString());
    Collection<ModelVariable> values = step.getValues();
    assertEquals("Number of values in step", 1, values.size());

    step = steps.get(1);
    values = step.getValues();
    assertEquals("Step name", "world", step.getName());
    assertEquals("State in step", "my-state([2])", step.getStatesFor("my-state").toString());
    assertEquals("Number of values in step", 2, values.size());

    step = steps.get(2);
    values = step.getValues();
    assertEquals("Step name", "epixx", step.getName());
    assertEquals("State in step", "my-state([3])", step.getStatesFor("my-state").toString());
    assertEquals("Number of values in step", 2, values.size());

    TestSuite suite = osmo.getSuite();
    Map<String, ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 2, testVariables.size());
    String expected = "{your_item=your_item([foobar]), my_item=my_item([hello, world])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String, ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 2, stepVariables.size());
    expected = "{your_item=your_item([foobar, foobar]), my_item=my_item([hello, world, world])}";
    assertEquals("Step variables", expected, stepVariables.toString());
  }

  @Test
  public void multipleStatesAndVariables() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    CoverageValueModel1 model = new CoverageValueModel1();
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(111);

    assertEquals("State over generation with no state defined", "-my-state([1])--my-state([2])--my-state([3])-", model.getStates());

    TestSuite suite = osmo.getSuite();
    Map<String, ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 3, testVariables.size());
    String expected = "{lastName=lastName([world]), global=global([bad]), firstName=firstName([hello])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String, ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 3, stepVariables.size());
    expected = "{lastName=lastName([world, world, world]), global=global([bad]), firstName=firstName([hello, hello, hello])}";
    assertEquals("Step variables", expected, stepVariables.toString());
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

    assertEquals("State", ":null:-my-state([4])-:null:-my-state([1])-:null:-my-state([3])-", model.getStates());

    TestSuite suite = osmo.getSuite();
    Map<String, ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 1, testVariables.size());
    String expected = "{range=range([1])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String, ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 1, stepVariables.size());
    expected = "{range=range([1])}";
    assertEquals("Step variables", expected, stepVariables.toString());
  }
}

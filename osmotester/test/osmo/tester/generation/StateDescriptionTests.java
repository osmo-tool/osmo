package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;
import osmo.tester.testmodels.StateDescriptionModel;
import osmo.tester.testmodels.StateDescriptionModel2;
import osmo.tester.testmodels.ValidTestModel4;
import osmo.tester.testmodels.ValidTestModel6;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class StateDescriptionTests {
  private OSMOTester osmo = null;

  @Before
  public void testSetup() {
    OSMOConfiguration.setSeed(111);
    osmo = new OSMOTester();
  }

  @Test
  public void noState() {
    ValidTestModel6 model = new ValidTestModel6();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = osmo.getSuite();
    assertEquals("Sequence of steps", "[t2, t1, t1]", suite.getAllTestCases().get(0).getSteps().toString());
    assertEquals("State over generation with no state defined", ":null:-null-:null:-null-:null:-null-", model.getStates());

    Map<String,ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 1, testVariables.size());
    ModelVariable mv = testVariables.values().iterator().next();
    assertEquals("Test variables", "[2, 1]", mv.getValues().toString());

    Map<String,ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 1, stepVariables.size());
    mv = stepVariables.values().iterator().next();
    assertEquals("Step variables", "[2, 1, 1]", mv.getValues().toString());
  }
  
  @Test
  public void twoStatesNoVariables() {
    Requirements reqs = new Requirements();
    PartialModel1 model1 = new PartialModel1(reqs);
    PartialModel2 model2 = new PartialModel2(reqs);
    osmo.addModelObject(model1);
    osmo.addModelObject(model2);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();
    String expected = ":null:-hello-hello1-:null:-world-hello2-:null:-epixx-hello2-";
    assertEquals("State over generation with two states defined", expected, model1.getStates());

    TestSuite suite = osmo.getSuite();
    Map<String,ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 0, testVariables.size());
  }

  @Test
  public void multipleStatesCustomVariable() {
    ValidTestModel4 model = new ValidTestModel4(NullPrintStream.stream);
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();
    assertEquals("State over generation with no state defined", ":null:-1-:null:-2-:null:-3-", model.getStates());
    TestCase testCase = osmo.getSuite().getAllTestCases().get(0);

    List<TestCaseStep> steps = testCase.getSteps();

    TestCaseStep step = steps.get(0);
    assertEquals("Step name", "hello", step.getName());
    assertEquals("State in step", "1", step.getState());
    Collection<ModelVariable> values = step.getValues();
    assertEquals("Number of values in step", 1, values.size());

    step = steps.get(1);
    values = step.getValues();
    assertEquals("Step name", "world", step.getName());
    assertEquals("State in step", "2", step.getState());
    assertEquals("Number of values in step", 2, values.size());

    step = steps.get(2);
    values = step.getValues();
    assertEquals("Step name", "epixx", step.getName());
    assertEquals("State in step", "3", step.getState());
    assertEquals("Number of values in step", 2, values.size());

    TestSuite suite = osmo.getSuite();
    Map<String,ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 2, testVariables.size());
    String expected = "{your_item=your_item([foobar]), my_item=my_item([hello, world])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String,ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 2, stepVariables.size());
    expected = "{your_item=your_item([foobar, foobar]), my_item=my_item([hello, world, world])}";
    assertEquals("Step variables", expected, stepVariables.toString());
  }

  @Test
  public void multipleStatesAndVariables() {
    StateDescriptionModel model = new StateDescriptionModel();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();

    assertEquals("State over generation with no state defined", "-1--2--3-", model.getStates());

    TestSuite suite = osmo.getSuite();
    Map<String,ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 3, testVariables.size());
    String expected = "{lastName=lastName([world]), global=global([bad]), firstName=firstName([hello])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String,ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 3, stepVariables.size());
    expected = "{lastName=lastName([world, world, world]), global=global([bad]), firstName=firstName([hello, hello, hello])}";
    assertEquals("Step variables", expected, stepVariables.toString());
  }

  @Test
  public void oneStateSeveralValues() {
    OSMOConfiguration.setSeed(222);
    StateDescriptionModel2 model = new StateDescriptionModel2();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();

    assertEquals("State", ":null:-4-:null:-1-:null:-3-", model.getStates());

    TestSuite suite = osmo.getSuite();
    Map<String,ModelVariable> testVariables = suite.getTestVariables();
    assertEquals("Number of test variables", 1, testVariables.size());
    String expected = "{range=range([1])}";
    assertEquals("Test variables", expected, testVariables.toString());

    Map<String,ModelVariable> stepVariables = suite.getStepVariables();
    assertEquals("Number of step variables", 1, stepVariables.size());
    expected = "{range=range([1])}";
    assertEquals("Step variables", expected, stepVariables.toString());

    Map<String,ModelVariable> stateVariables = suite.getStateVariables();
    assertEquals("Number of step variables", 1, stateVariables.size());
    expected = "{range=range([1])}";
    assertEquals("Step variables", expected, stateVariables.toString());
  }
}

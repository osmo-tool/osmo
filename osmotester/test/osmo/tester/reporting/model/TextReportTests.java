package osmo.tester.reporting.model;

import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.ValidTestModel3;
import osmo.tester.testmodels.ValidTestModel4;
import osmo.tester.testmodels.ValidTestModel5;
import osmo.tester.testmodels.ValidTestModel6;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class TextReportTests {
  @Test
  public void validModel1() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel1());
    String actual = mv.write();
    String expected = "BeforeSuites: \n" +
            "AfterSuites: \n" +
            "BeforeTests: \n" +
            "AfterTests: \n" +
            "Model EndConditions: \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: \n" +
            "Requirements: null\n" +
            "Variables: \n" +
            "\n" +
            "STEP: epixx, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel1.kitted(), \n" +
            "GROUP: \n" +
            "POST: \n" +
            "PRE: \n" +
            "\n" +
            "STEP: world, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel1.listCheck(), \n" +
            "GROUP: \n" +
            "POST: \n" +
            "PRE: \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }

  @Test
  public void validModel2() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel2(new Requirements(), NullPrintStream.stream));
    String actual = mv.write();
    String expected = "BeforeSuites: osmo.tester.testmodels.ValidTestModel2.firstOfAll(), \n" +
            "AfterSuites: osmo.tester.testmodels.ValidTestModel2.lastOfAll(), \n" +
            "BeforeTests: osmo.tester.testmodels.ValidTestModel2.setup(), \n" +
            "AfterTests: osmo.tester.testmodels.ValidTestModel2.bob(), \n" +
            "Model EndConditions: \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: \n" +
            "Requirements: null\n" +
            "Variables: \n" +
            "\n" +
            "STEP: hello, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel2.helloCheck(), \n" +
            "GROUP: \n" +
            "POST: \n" +
            "PRE: \n" +
            "\n" +
            "STEP: epixx, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel2.kitted(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel2.epixxO(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel2.epixxPre(), \n" +
            "\n" +
            "STEP: world, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel2.worldCheck(), \n" +
            "GROUP: \n" +
            "POST: \n" +
            "PRE: \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }

  @Test
  public void validModel3() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel3(NullPrintStream.stream));
    String actual = mv.write();
    String expected = "BeforeSuites: osmo.tester.testmodels.ValidTestModel3.empty(), \n" +
            "AfterSuites: osmo.tester.testmodels.ValidTestModel3.empty(), \n" +
            "BeforeTests: osmo.tester.testmodels.ValidTestModel3.reset(), \n" +
            "AfterTests: osmo.tester.testmodels.ValidTestModel3.empty(), \n" +
            "Model EndConditions: \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: \n" +
            "Requirements: null\n" +
            "Variables: \n" +
            "\n" +
            "STEP: hello, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel3.helloCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel3.stateCheck(), \n" +
            "PRE: \n" +
            "\n" +
            "STEP: epixx, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel3.kitted(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel3.epixxO(), osmo.tester.testmodels.ValidTestModel3.stateCheck(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel3.epixxPre(), \n" +
            "\n" +
            "STEP: world, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel3.worldCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel3.stateCheck(), \n" +
            "PRE: \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }

  @Test
  public void validModel4() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel4(NullPrintStream.stream));
    String actual = mv.write();
    String expected = "BeforeSuites: \n" +
            "AfterSuites: \n" +
            "BeforeTests: osmo.tester.testmodels.ValidTestModel4.reset(), \n" +
            "AfterTests: \n" +
            "Model EndConditions: \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: my-state[osmo.tester.testmodels.ValidTestModel4.state1(osmo.tester.generator.testsuite.TestCaseStep)], \n" +
            "Requirements: null\n" +
            "Variables: \n" +
            "\n" +
            "STEP: hello, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel4.helloCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel4.savePostState(), osmo.tester.testmodels.ValidTestModel4.sharedCheck(), osmo.tester.testmodels.ValidTestModel4.stateCheck(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel4.savePreState(), \n" +
            "\n" +
            "STEP: epixx, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel4.kitted(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel4.epixxO(), osmo.tester.testmodels.ValidTestModel4.savePostState(), osmo.tester.testmodels.ValidTestModel4.stateCheck(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel4.savePreState(), \n" +
            "\n" +
            "STEP: world, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel4.worldCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel4.savePostState(), osmo.tester.testmodels.ValidTestModel4.sharedCheck(), osmo.tester.testmodels.ValidTestModel4.stateCheck(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel4.savePreState(), \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }

  @Test
  public void validModel5() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel5(NullPrintStream.stream));
    String actual = mv.write();
    String expected = "BeforeSuites: \n" +
            "AfterSuites: \n" +
            "BeforeTests: osmo.tester.testmodels.ValidTestModel5.reset(), \n" +
            "AfterTests: \n" +
            "Model EndConditions: osmo.tester.testmodels.ValidTestModel5.end(), \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: \n" +
            "Requirements: null\n" +
            "Variables: \n" +
            "\n" +
            "STEP: hello, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel5.helloCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel5.sharedCheck(), osmo.tester.testmodels.ValidTestModel5.stateCheck(), \n" +
            "PRE: \n" +
            "\n" +
            "STEP: epixx, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel5.kitted(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel5.epixxO(), osmo.tester.testmodels.ValidTestModel5.stateCheck(), \n" +
            "PRE: \n" +
            "\n" +
            "STEP: world, WEIGHT=10\n" +
            "GUARDS: osmo.tester.testmodels.ValidTestModel5.worldCheck(), \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel5.sharedCheck(), osmo.tester.testmodels.ValidTestModel5.stateCheck(), \n" +
            "PRE: \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }

  @Test
  public void validModel6() {
    FSMVisualizer mv = new FSMVisualizer();
    mv.addModelObject(new ValidTestModel6());
    String actual = mv.write();
    String expected = "BeforeSuites: \n" +
            "AfterSuites: \n" +
            "BeforeTests: \n" +
            "AfterTests: \n" +
            "Model EndConditions: \n" +
            "Exploration Enablers: \n" +
            "Generation Enablers: \n" +
            "Coverage Value Methods: \n" +
            "Requirements: null\n" +
            "Variables: index, \n" +
            "\n" +
            "STEP: t3, WEIGHT=10\n" +
            "GUARDS: \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel6.savePostState(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel6.savePreState(), \n" +
            "\n" +
            "STEP: t2, WEIGHT=10\n" +
            "GUARDS: \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel6.savePostState(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel6.savePreState(), \n" +
            "\n" +
            "STEP: t1, WEIGHT=10\n" +
            "GUARDS: \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel6.savePostState(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel6.savePreState(), \n" +
            "\n" +
            "STEP: t4, WEIGHT=10\n" +
            "GUARDS: \n" +
            "GROUP: \n" +
            "POST: osmo.tester.testmodels.ValidTestModel6.savePostState(), \n" +
            "PRE: osmo.tester.testmodels.ValidTestModel6.savePreState(), \n" +
            "\n";
    assertEquals("Text report for model", expected, actual);
  }
}

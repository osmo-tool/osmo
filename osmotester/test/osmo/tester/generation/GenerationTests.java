package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.BaseModelExtension;
import osmo.tester.testmodels.GroupModel2;
import osmo.tester.testmodels.GuardianModel;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;
import osmo.tester.testmodels.StateDescriptionModel;
import osmo.tester.testmodels.TestStepModel;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.ValidTestModel3;
import osmo.tester.testmodels.ValidTestModel4;
import osmo.tester.testmodels.ValidTestModel5;
import osmo.tester.testmodels.VariableModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/**
 * Test cases that exercise the model generator, checking the output for the given test models.
 *
 * @author Teemu Kanstren
 */
public class GenerationTests {
  private OSMOTester osmo = null;

  @Before
  public void testSetup() {
    OSMOConfiguration.setSeed(111);
    osmo = new OSMOTester();
  }

  @After
  public void endAssertion() {
  }

  @Test
  public void noEnabledTransition() {
    osmo.addModelObject(new ValidTestModel1());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Generation without available transitions should fail.");
    } catch (IllegalStateException e) {
      assertEquals("No transition available.", e.getMessage());
    }
  }

  @Test
  public void generateTestModel2() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel2(new Requirements(), ps));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();
    String expected = ":hello:world:epixx_pre:epixx:epixx_oracle";
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void generateTestModel3() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel3(ps));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate();
    String expected = ":hello:gen_oracle:world:gen_oracle:epixx_pre:epixx:epixx_oracle:gen_oracle";
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void generateTestModel3Times4() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel3(ps));
    Length length3 = new Length(3);
    Length length4 = new Length(4);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length4);
    osmo.generate();
    String one = ":hello:gen_oracle:world:gen_oracle:epixx_pre:epixx:epixx_oracle:gen_oracle";
    String four = one;
    four += one;
    four += one;
    four += one;
    String actual = out.toString();
    assertEquals(four, actual);
  }

  @Test
  public void generateTestModel4Times2() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel4(ps));
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle:epixx:epixx_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void generateTestModel5Times2() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel5(ps));
    Length length5 = new Length(5);
    Length length2 = new Length(2);
    osmo.setTestEndCondition(length5);
    osmo.setSuiteEndCondition(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void generateWithStateDescription() {
    StateDescriptionModel model = new StateDescriptionModel();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length3);
    osmo.generate();
    List<TestCase> tests = osmo.getSuite().getAllTestCases();
    TestCase test1 = tests.get(0);
    test1.getSteps();
  }
  
  @Test
  public void generatePartialModelsTimes2() {
    Requirements reqs = new Requirements();
    reqs.add(PartialModel1.REQ_HELLO);
    reqs.add(PartialModel1.REQ_WORLD);
    reqs.add(PartialModel1.REQ_EPIX);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    PartialModel1 model1 = new PartialModel1(reqs, ps);
    PartialModel2 model2 = new PartialModel2(reqs, ps);
    osmo.addModelObject(model1);
    osmo.addModelObject(model2);
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle:epixx:epixx_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void generateBaseModelExtension() {
    BaseModelExtension model = new BaseModelExtension();
    osmo.addModelObject(model);
    Length length5 = new Length(5);
    osmo.setTestEndCondition(length5);
    osmo.setSuiteEndCondition(length5);
    osmo.generate();
    assertEquals("Number of times generic @Post performed", 10, model.checkCount);
    assertTrue("Should have performed generic @Post for first transition", model.firstChecked);
    assertTrue("Should have performed generic @Post for second transition", model.secondChecked);
  }

  @Test
  public void thresholdBreak() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    Endless endless = new Endless();
    Length length = new Length(100);
    Or combo = new Or(endless, length);
    osmo.setTestEndCondition(combo);
    osmo.setSuiteEndCondition(combo);
    osmo.generate();
    assertEquals("Number of tests", 100, osmo.getSuite().getFinishedTestCases().size());
    assertEquals("Test length", 100, osmo.getSuite().getFinishedTestCases().get(0).getSteps().size());
  }

  @Test
  public void flow() {
    OSMOConfiguration.setSeed(145);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    ValidTestModel2 modelObject = new ValidTestModel2(new Requirements(), ps);
    modelObject.setPrintFlow(true);
    osmo.addModelObject(modelObject);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate();
    String actual = out.toString();
    assertEquals(":beforesuite::beforetest::hello:world:epixx_pre:epixx:epixx_oracle:aftertest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftersuite:", actual);
  }

  @Test
  public void prefixAndNoPrefixFlow() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    ValidTestModel2 mo = new ValidTestModel2(new Requirements(), ps);
    mo.setPrintFlow(true);
    osmo.addModelObject("p1", mo);
    osmo.addModelObject("p2", mo);
    osmo.addModelObject(mo);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate();
    String actual = out.toString();
    assertEquals(":beforesuite::beforesuite::beforesuite::beforetest::beforetest::beforetest::hello:world:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::beforetest::beforetest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::beforetest::beforetest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::aftersuite::aftersuite::aftersuite:", actual);
  }

  @Test
  public void lastSteps() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    TestStepModel mo = new TestStepModel(ps);
    osmo.addModelObject("p1", mo);
    osmo.addModelObject("p2", mo);
    osmo.addModelObject(mo);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate();
    String actual = out.toString();
    String expected = ":hello:gen_oracle:gen_oracle:gen_oracle:world:gen_oracle:gen_oracle:gen_oracle:epixx_pre:epixx:epixx_oracle:gen_oracle:gen_oracle:gen_oracle:last_step1:last_step1:last_step1:last_step2:last_step2:last_step2:hello:gen_oracle:gen_oracle:gen_oracle:world:gen_oracle:gen_oracle:gen_oracle:epixx_pre:epixx:epixx_oracle:gen_oracle:gen_oracle:gen_oracle:last_step1:last_step1:last_step1:last_step2:last_step2:last_step2:hello:gen_oracle:gen_oracle:gen_oracle:world:gen_oracle:gen_oracle:gen_oracle:epixx_pre:epixx:epixx_oracle:gen_oracle:gen_oracle:gen_oracle:last_step1:last_step1:last_step1:last_step2:last_step2:last_step2";
    assertEquals(expected, actual);
    assertEquals("Last step 1 in a test case", 3, mo.getLastStep1TestCount());
    assertEquals("Last step 2 in a test case", 3, mo.getLastStep2TestCount());
    assertEquals("Last step 1 in a test suite", 9, mo.getLastStep1SuiteCount());
    assertEquals("Last step 2 in a test suite", 9, mo.getLastStep2SuiteCount());
  }

  @Test
  public void negatedGuard() {
    OSMOConfiguration.setSeed(65);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    GuardianModel mo = new GuardianModel(ps);
    osmo.addModelObject(mo);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate();

    String expected = getResource(getClass(), "expected-negated-guard.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }

  @Test
  public void negatedGuardWithPrefix() {
    OSMOConfiguration.setSeed(650);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    GuardianModel mo1 = new GuardianModel(ps, "m1");
    GuardianModel mo2 = new GuardianModel(ps, "m2");
    GuardianModel mo3 = new GuardianModel(ps, "m3");
    osmo.addModelObject(mo1);
    osmo.addModelObject("p2", mo2);
    osmo.addModelObject("p3", mo3);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate();

    String expected = getResource(getClass(), "expected-negated-guard-prefix.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }
  
  @Test
  public void groups() {
    OSMOConfiguration.setSeed(650);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new GroupModel2(ps));
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(15));
    osmo.generate();
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    String expected = getResource(getClass(), "expected-group.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generation with groups", expected, actual);
  }
}


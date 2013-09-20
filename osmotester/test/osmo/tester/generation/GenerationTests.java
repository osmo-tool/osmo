package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.BaseModelExtension;
import osmo.tester.testmodels.CoverageValueModel1;
import osmo.tester.testmodels.GroupModel2;
import osmo.tester.testmodels.GuardianModel;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;
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
    osmo = new OSMOTester();
  }

  @Test
  public void noEnabledTransition() {
    osmo.addModelObject(new ValidTestModel1());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(111);
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
    osmo.generate(111);
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
    osmo.generate(111);
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
    osmo.generate(111);
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
    osmo.generate(111);
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
    osmo.generate(111);
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void generateWithStateDescription() {
    CoverageValueModel1 model = new CoverageValueModel1();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length3);
    osmo.generate(111);
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
    osmo.generate(111);
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
    osmo.generate(111);
    assertEquals("Number of times generic @Post performed", 10, model.checkCount);
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
    osmo.generate(111);
    assertEquals("Number of tests", 100, osmo.getSuite().getFinishedTestCases().size());
    assertEquals("Test length", 100, osmo.getSuite().getFinishedTestCases().get(0).getSteps().size());
  }

  @Test
  public void flow() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    ValidTestModel2 modelObject = new ValidTestModel2(new Requirements(), ps);
    modelObject.setPrintFlow(true);
    osmo.addModelObject(modelObject);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate(145);
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
    osmo.generate(111);
    String actual = out.toString();
    assertEquals(":beforesuite::beforesuite::beforesuite::beforetest::beforetest::beforetest::hello:world:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::beforetest::beforetest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::beforetest::beforetest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftertest::aftertest::aftersuite::aftersuite::aftersuite:", actual);
  }

  @Test
  public void negatedGuard() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    GuardianModel mo = new GuardianModel(ps);
    osmo.addModelObject(mo);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(3));
    osmo.generate(65);

    String expected = getResource(getClass(), "expected-negated-guard.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }

  @Test
  public void negatedGuardWithPrefix() {
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
    osmo.generate(650);

    String expected = getResource(getClass(), "expected-negated-guard-prefix.txt");
    expected = unifyLineSeparators(expected, "\n");
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Print from model", expected, actual);
  }
  
  @Test
  public void groups() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new GroupModel2(ps));
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(15));
    osmo.generate(650);
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    String expected = getResource(getClass(), "expected-group.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generation with groups", expected, actual);
  }
}


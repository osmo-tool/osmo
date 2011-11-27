package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.filter.MaxTransitionFilter;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.BaseModelExtension;
import osmo.tester.testmodels.EndStateModel;
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

import static junit.framework.Assert.*;

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

  @After
  public void endAssertion() {
  }

  @Test
  public void noEnabledTransition() {
    osmo.addModelObject(new ValidTestModel1());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
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
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
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
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
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
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length4);
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
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length2);
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
    osmo.addTestEndCondition(length5);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void generateEndStateModelLength1() {
    EndStateModel model = new EndStateModel();
    osmo.addModelObject(model);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length1);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    assertEquals("Number of covered requirements", 3, model.getRequirements().getUniqueCoverage().size());
  }

  @Test
  public void generateEndStateModelLength2() {
    EndStateModel model = new EndStateModel();
    osmo.addModelObject(model);
    Length length1 = new Length(2);
    osmo.addTestEndCondition(length1);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    assertEquals("Number of covered requirements", 3, model.getRequirements().getUniqueCoverage().size());
  }

  @Test
  public void generatePartialModelsTimes2() {
    Requirements req = new Requirements();
    req.add(PartialModel1.REQ_HELLO);
    req.add(PartialModel1.REQ_WORLD);
    req.add(PartialModel1.REQ_EPIX);
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    PartialModel1 model1 = new PartialModel1(req, ps);
    PartialModel2 model2 = new PartialModel2(req, ps);
    osmo.addModelObject(model1);
    osmo.addModelObject(model2);
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length2);
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
//    osmo.setDebug(true);
    osmo.addModelObject(model);
    Length length5 = new Length(5);
    osmo.addTestEndCondition(length5);
    osmo.addSuiteEndCondition(length5);
    osmo.generate();
    assertEquals("Number of times generic @Post performed", 10, model.checkCount);
    assertTrue("Should have performed generic @Post for first transition", model.firstChecked);
    assertTrue("Should have performed generic @Post for second transition", model.secondChecked);
  }

  @Test
  public void thresholdBreak() {
    VariableModel2 model = new VariableModel2();
//    osmo.setDebug(true);
    osmo.addModelObject(model);
    Endless endless = new Endless();
    osmo.addTestEndCondition(endless);
    osmo.addSuiteEndCondition(endless);
    Length length = new Length(100);
    length.setStrict(true);
    osmo.addTestEndCondition(length);
    osmo.addSuiteEndCondition(length);
    osmo.generate();
    assertEquals("Number of tests", 100, osmo.getSuite().getFinishedTestCases().size());
    assertEquals("Test length", 100, osmo.getSuite().getFinishedTestCases().get(0).getSteps().size());
  }

  @Test
  public void flow() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.setSeed(145);
    ValidTestModel2 modelObject = new ValidTestModel2(new Requirements(), ps);
    modelObject.setPrintFlow(true);
    osmo.addModelObject(modelObject);
    osmo.addTestEndCondition(new Length(3));
    osmo.addSuiteEndCondition(new Length(3));
    osmo.generate();
    String actual = out.toString();
    assertEquals(":beforesuite::beforetest::hello:world:epixx_pre:epixx:epixx_oracle:aftertest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::beforetest::epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:aftertest::aftersuite:", actual);
  }
}

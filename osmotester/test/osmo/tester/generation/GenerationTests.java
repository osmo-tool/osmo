package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.strategy.LengthStrategy;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.PartialModel1;
import osmo.tester.testmodels.PartialModel2;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.ValidTestModel3;
import osmo.tester.testmodels.ValidTestModel4;
import osmo.tester.testmodels.ValidTestModel5;

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
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length1);
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
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length1);
    osmo.generate();
    String expected = ":hello:world:epixx:epixx_oracle";
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void generateTestModel3() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel3(ps));
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length1);
    osmo.generate();
    String expected = ":hello:gen_oracle:world:gen_oracle:epixx:epixx_oracle:gen_oracle";
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void generateTestModel3Times4() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel3(ps));
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length4 = new LengthStrategy(4);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length4);
    osmo.generate();
    String one = ":hello:gen_oracle:world:gen_oracle:epixx:epixx_oracle:gen_oracle";
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
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length2 = new LengthStrategy(2);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length2);
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
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length2 = new LengthStrategy(2);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

  @Test
  public void GeneratePartialModelsTimes2() {
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
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length2 = new LengthStrategy(2);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length2);
    osmo.generate();
    String one = ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle:epixx:epixx_oracle:gen_oracle";
    String two = one;
    two += one;
    String actual = out.toString();
    assertEquals(two, actual);
  }

}

package osmo.tester.filter;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.filter.MaxStepFilter;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.ValidTestModel6;
import osmo.tester.testmodels.VariableModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/** @author Teemu Kanstren */
public class MaxFilterTests {
  private OSMOTester osmo = null;
  private OSMOConfiguration config;
  private ByteArrayOutputStream out;
  private PrintStream ps;
  private ValidTestModel2 validTestModel2;

  @Before
  public void testSetup() {
    out = new ByteArrayOutputStream(1000);
    ps = new PrintStream(out);
    osmo = new OSMOTester();
    config = osmo.getConfig();
    validTestModel2 = new ValidTestModel2(new Requirements(), ps);
  }

  @Test
  public void noSuchTransition() {
    osmo.setModelFactory(new ReflectiveModelFactory(VariableModel2.class));
    MaxStepFilter filter = new MaxStepFilter();
    filter.setMax("no-such-transition", 1);
    osmo.addFilter(filter);
    try {
      osmo.generate(123);
      fail("Generation with non-existing transition should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for non-existing transition", "Specified steps do not exist in the model:[no-such-transition]", e.getMessage());
    }
  }

  @Test
  public void zero() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(validTestModel2);
    osmo.setModelFactory(factory);
    MaxStepFilter filter = new MaxStepFilter();
    filter.setMax("world", 0);
    osmo.addFilter(filter);
    config.setFailWhenNoWayForward(false);
    osmo.generate(123);
    assertSteps(":hello:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last:last");
  }

  private void assertSteps(String expected) {
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void negative() {
    MaxStepFilter filter = new MaxStepFilter();
    try {
      filter.setMax("bob", -1);
      fail("Negative filter value should fail");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for negative filter value", "Step max count is now allowed to be negative. Was -1 for 'bob'.", e.getMessage());
    }
  }

  @Test
  public void onceWithNoAlternative() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(validTestModel2);
    osmo.setModelFactory(factory);
    MaxStepFilter filter = new MaxStepFilter();
    filter.setMax("epixx", 1);
    osmo.addFilter(filter);
    osmo.setSuiteEndCondition(new Length(1));
    config.setFailWhenNoWayForward(false);
    osmo.generate(123);
    assertSteps(":hello:world:epixx_pre:epixx:epixx_oracle:last");
  }

  @Test
  public void onceWithAlternative() {
    TestSequenceListener listener = new TestSequenceListener();
    osmo.addListener(listener);
    osmo.addModelObject(new ValidTestModel6(":null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-:null:-null-"));
    MaxStepFilter filter = new MaxStepFilter();
    filter.setMax("t1", 1);
    filter.setMax("t2", 2);
    filter.setMax("t3", 2);
    osmo.addFilter(filter);
    osmo.setTestEndCondition(new Length(20));
    osmo.setSuiteEndCondition(new Length(1));
    osmo.generate(123);
    listener.addExpected("suite-start", "start", "t:t3", "t:t2", "t:t4", "t:t3", "t:t4", "t:t1", "t:t4", "t:t2", "t:t4",
            "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "t:t4", "ls:check", "end", "suite-end");
    listener.validate("1 allowed transition with alternatives");
  }

  @Test
  public void twiceWithNoAlternative() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(validTestModel2);
    osmo.setModelFactory(factory);
    MaxStepFilter filter = new MaxStepFilter();
    filter.setMax("epixx", 2);
    osmo.addFilter(filter);
    osmo.setSuiteEndCondition(new Length(1));
    config.setFailWhenNoWayForward(false);
    osmo.generate(123);
    assertSteps(":hello:world:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:last");
  }
}

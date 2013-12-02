package osmo.tester.unittests.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.unittests.testmodels.ErrorModel1;
import osmo.tester.unittests.testmodels.ErrorModel2;
import osmo.tester.unittests.testmodels.ErrorModel3;
import osmo.tester.unittests.testmodels.ErrorModel4;
import osmo.tester.unittests.testmodels.ErrorModel5;
import osmo.tester.unittests.testmodels.ErrorModel6;
import osmo.tester.unittests.testmodels.ErrorModel7;
import osmo.tester.unittests.testmodels.ErrorModel8;
import osmo.tester.unittests.testmodels.ErrorModel9;
import osmo.tester.unittests.testmodels.StrictTestModel;

import static junit.framework.Assert.*;

/**
 * TRAP refers to catching an exception in the generator (not in the test cases here) and so on.
 *
 * @author Teemu Kanstren
 */
public class ErrorHandlingTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;
  private OSMOConfiguration config;

  @Before
  public void testSetup() {
//    Logger.consoleLevel = Level.ALL;
    osmo = new OSMOTester();
    config = osmo.getConfig();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @Test
  public void beforeTestWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel1.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@BeforeTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeTestWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel1.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Trap should not catch @BeforeTest");
    } catch (RuntimeException e) {
      assertEquals("@BeforeTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeSuiteWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel3.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@BeforeSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeSuiteWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel3.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Trap should not catch @BeforeSuite");
    } catch (RuntimeException e) {
      assertEquals("@BeforeSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterTestWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel2.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@AfterTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterTestWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel2.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Trap should not catch @AfterTest");
    } catch (RuntimeException e) {
      assertEquals("@AfterTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterSuiteWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel4.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@AfterSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterSuiteWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel4.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Trap should not catch @AfterSuite");
    } catch (RuntimeException e) {
      assertEquals("@AfterSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void guardWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel6.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Guard fail", e.getMessage());
    }
  }

  @Test
  public void guardWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel6.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Runtime exception with trap should propagate.");
    } catch (Exception e) {
      assertEquals("@Guard fail", e.getMessage());
    }
    listener.validate("@Guard with trap");
  }

  @Test
  public void transitionWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel5.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("Error in test generation:@Transition assert fail", e.getMessage());
    }
  }

  @Test
  public void transitionWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello");
    listener.addExpected("g:hello", "pre:hello", "t:hello", "post:hello");
    listener.addExpected("g:hello", "pre:hello", "t:hello", "post:hello", "ls:lastStepStanding", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel5.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("@TestStep with trap");
  }

  @Test
  public void preWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel8.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Pre fail", e.getMessage());
    }
  }

  @Test
  public void preWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel8.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Runtime exception with trap should propagate.");
    } catch (Exception e) {
      assertEquals("@Pre fail", e.getMessage());
    }
    listener.validate("@Pre with trap");
  }

  @Test
  public void postWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel7.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("Error in test generation:@Post fail", e.getMessage());
    }
  }

  @Test
  public void postWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "g:hello", "pre:hello", "t:hello", "post:hello", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel7.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("@Post with trap");
  }

  @Test
  public void endConditionWithoutTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel9.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@EndCondition fail", e.getMessage());
    }
  }

  @Test
  public void endConditionWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel9.class));
    config.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("Throwing an exception without trap should propagate.");
    } catch (Exception e) {
      assertEquals("@EndCondition fail", e.getMessage());
    }
    listener.validate("@EndCondition with trap");
  }

  @Test
  public void nonStrictStepFails() {
    listener.addExpected("suite-start", "start", "e:a non-strict one 2", "t:a non-strict one 2", "e:a non-strict one", "t:a non-strict one", "e:a strict one", "end", "suite-end");
    listener.setTraceGuards(false);
    listener.setTraceErrors(true);
    osmo.setModelFactory(new ReflectiveModelFactory(StrictTestModel.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
      fail("The final strict test step failure should stop the generator");
    } catch (RuntimeException e) {
      assertEquals("t2 fail", e.getMessage());
    }
    listener.validate("Strict/non-strict generation combination.");
    
  }
}

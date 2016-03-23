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
    String error = (String) osmo.getSuite().getCurrentTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @BeforeTest should not be invoked", null, error);
  }

  @Test
  public void beforeTestWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel1.class));
    config.setStopTestOnError(false);
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
    String error = (String) osmo.getSuite().getCurrentTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @BeforeTest should not be invoked", null, error);
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
    //this should not pass first test creation, so ignore it for @OnError
  }

  @Test
  public void beforeSuiteWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel3.class));
    config.setStopTestOnError(false);
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
    //this should not pass first test creation, so ignore it for @OnError
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
      assertEquals("@AfterTest fail", e.getMessage());
    }
    String error = (String) osmo.getSuite().getCurrentTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @AfterTest should be invoked", "hello", error);
  }

  @Test
  public void afterTestWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel2.class));
    config.setStopTestOnError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(100);
    } catch (RuntimeException e) {
      fail("Trap should catch @AfterTest");
    }
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @AfterTest should be invoked", "hello", error);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @AfterSuite should not be invoked", null, error);
  }

  @Test
  public void afterSuiteWithTrap() {
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel4.class));
    config.setStopTestOnError(false);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError for a test failing on @AfterSuite should not be invoked", null, error);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError for a guard", "guard", error);
  }

  @Test
  public void guardWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel6.class));
//    config.setStopGenerationOnError(false);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError for a guard", "guard", error);
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
      assertEquals("Error in test generation:@TestStep assert fail", e.getMessage());
    }
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @TestStep failure", "hello", error);
  }

  @Test
  public void transitionWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello");
    listener.addExpected("g:hello", "pre:hello", "t:hello", "post:hello");
    listener.addExpected("g:hello", "pre:hello", "t:hello", "post:hello", "ls:lastStepStanding", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel5.class));
    config.setStopTestOnError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("@TestStep with trap");
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @TestStep failure", "hello", error);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @Pre failure", "hello", error);
  }

  @Test
  public void preWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel8.class));
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @Pre failure", "hello", error);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @Post failure", "hello", error);
  }

  @Test
  public void postWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "g:hello", "pre:hello", "t:hello", "post:hello", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel7.class));
    config.setStopTestOnError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("@Post with trap");
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @Post failure", "hello", error);
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @EndCondition failure", "hello", error);
  }

  @Test
  public void endConditionWithTrap() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ErrorModel9.class));
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
    String error = (String) osmo.getSuite().getLastTest().getAttribute("error");
    assertEquals("@OnError should be invoked on @EndCondition failure", "hello", error);
  }
}

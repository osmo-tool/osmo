package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ErrorModel1;
import osmo.tester.testmodels.ErrorModel2;
import osmo.tester.testmodels.ErrorModel3;
import osmo.tester.testmodels.ErrorModel4;
import osmo.tester.testmodels.ErrorModel5;
import osmo.tester.testmodels.ErrorModel6;
import osmo.tester.testmodels.ErrorModel7;
import osmo.tester.testmodels.ErrorModel8;
import osmo.tester.testmodels.ErrorModel9;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * TRAP refers to catching an exception in the generator (not in the test cases here) and so on.
 *
 * @author Teemu Kanstren
 */
public class ErrorHandlingTests {
  private OSMOTester osmo = null;
  private TestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(100);
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @Test
  public void beforeTestWithoutTrap() {
    osmo.addModelObject(new ErrorModel1());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@BeforeTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeTestWithTrap() {
    osmo.addModelObject(new ErrorModel1());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Trap should not catch @BeforeTest");
    } catch (RuntimeException e) {
      assertEquals("@BeforeTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeSuiteWithoutTrap() {
    osmo.addModelObject(new ErrorModel3());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@BeforeSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void beforeSuiteWithTrap() {
    osmo.addModelObject(new ErrorModel3());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Trap should not catch @BeforeSuite");
    } catch (RuntimeException e) {
      assertEquals("@BeforeSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterTestWithoutTrap() {
    osmo.addModelObject(new ErrorModel2());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@AfterTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterTestWithTrap() {
    osmo.addModelObject(new ErrorModel2());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Trap should not catch @AfterTest");
    } catch (RuntimeException e) {
      assertEquals("@AfterTest fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterSuiteWithoutTrap() {
    osmo.addModelObject(new ErrorModel4());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@AfterSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void afterSuiteWithTrap() {
    osmo.addModelObject(new ErrorModel4());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Trap should not catch @AfterSuite");
    } catch (RuntimeException e) {
      assertEquals("@AfterSuite fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void guardWithoutTrap() {
    osmo.addModelObject(new ErrorModel6());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Guard fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void guardWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "end", "suite-end");
    osmo.addModelObject(new ErrorModel6());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("@Guard with trap");
  }

  @Test
  public void transitionWithoutTrap() {
    osmo.addModelObject(new ErrorModel5());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Transition fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void transitionWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "end", "suite-end");
    osmo.addModelObject(new ErrorModel5());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("@Transition with trap");
  }

  @Test
  public void preWithoutTrap() {
    osmo.addModelObject(new ErrorModel8());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Pre fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void preWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "end", "suite-end");
    osmo.addModelObject(new ErrorModel8());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("@Pre with trap");
  }

  @Test
  public void postWithoutTrap() {
    osmo.addModelObject(new ErrorModel7());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@Post fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void postWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.addModelObject(new ErrorModel7());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("@Post with trap");
  }

  @Test
  public void endConditionWithoutTrap() {
    osmo.addModelObject(new ErrorModel9());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Throwing an exception without trap should propagate.");
    } catch (RuntimeException e) {
      assertEquals("@EndCondition fail", e.getCause().getCause().getMessage());
    }
  }

  @Test
  public void endConditionWithTrap() {
    listener.addExpected("suite-start", "start", "g:hello", "pre:hello", "t:hello", "post:hello", "end", "suite-end");
    osmo.addModelObject(new ErrorModel9());
    osmo.setFailWhenError(false);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("@EndCondition with trap");
  }
}

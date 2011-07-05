package osmo.tester.testmodels;

import osmo.tester.annotation.After;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.Before;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Oracle;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/**
 *
 * 
 * @author Teemu Kanstren
 */
public class EmptyTestModel1 {
  @RequirementsField
  private Requirements requirements = new Requirements();
  @TestSuiteField
  private TestSuite history = null;

  public TestSuite getHistory() {
    return history;
  }

  @Before
  public void start1() {

  }

  @Before
  public void start2() {

  }

  @After
  public void end() {

  }

  @BeforeSuite
  public void beforeAll() {

  }

  @AfterSuite
  public void endAll() {

  }

  @Transition("hello")
  public void transition1() {

  }

  @Transition("world")
  public void epix() {

  }

  @Guard("world")
  public boolean listCheck() {
    return false;
  }

  @Guard("world")
  public boolean listCheck2() {
    return false;
  }

  @Transition("epixx")
  public void epixx() {

  }

  @Guard("epixx")
  public boolean kitted() {
    return false;
  }

  @Guard({"epixx", "world"})
  public boolean gaagaa() {
    return false;
  }

  @Oracle
  public void stateOracle() {
  }

  @Oracle("epixx")
  public void epixxOracle() {
  }

  @Oracle({"hello", "epixx"})
  public void commonOracle() {
  }

  @EndCondition
  public boolean end1() {
    return false;
  }

  @EndCondition
  public boolean end2() {
    return false;
  }
}

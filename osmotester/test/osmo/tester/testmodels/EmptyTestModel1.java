package osmo.tester.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.util.Map;

/** @author Teemu Kanstren */
public class EmptyTestModel1 {
  @RequirementsField
  private Requirements requirements = new Requirements();
  @TestSuiteField
  private TestSuite history = new TestSuite();

  public TestSuite getHistory() {
    return history;
  }

  @BeforeTest
  public void start1() {

  }

  @BeforeTest
  public void start2() {

  }

  @AfterTest
  public void end() {

  }

  @BeforeSuite
  public void beforeAll() {

  }

  @AfterSuite
  public void endAll() {

  }
  
  @Guard("!world")
  public boolean negato() {
    return true;
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

  @Post
  public void stateOracle() {
  }

  @Pre("epixx")
  public void beforeEpixx() {
  }

  @Post("epixx")
  public void epixxOracle() {
  }

  @Pre({"hello", "epixx"})
  public void commonPre(Map<String, Object> p) {
  }

  @Post({"hello", "epixx"})
  public void commonOracle(Map<String, Object> p) {
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

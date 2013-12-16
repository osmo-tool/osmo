package osmo.tester.unittests.generation;

import org.junit.Test;
import osmo.common.OSMOException;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.model.Requirements;
import osmo.tester.unittests.testmodels.ErrorModel5;
import osmo.tester.unittests.testmodels.ValidTestModel2;
import osmo.tester.unittests.testmodels.ValidTestModel5;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class LastStepTests {
  @Test
  public void modelEnds() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    ValidTestModel5 model = new ValidTestModel5(ps);
    OSMOTester osmo = new OSMOTester();
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(1));
    osmo.addModelObject(model);
    osmo.generate(123);
    String actual = out.toString();
    assertEquals("Set of executed steps", ":hello:two_oracle:gen_oracle:world:two_oracle:gen_oracle:last", actual);
  }

  @Test
  public void modelThrowsAndFails() {
    TestSequenceListener listener = new TestSequenceListener(false);
    //we expect nothing, therefor we add nothing
//    listener.addExpected();
    ErrorModel5 model = new ErrorModel5();
    OSMOTester osmo = new OSMOTester();
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.addModelObject(model);
    try {
      osmo.generate(123);
      fail("This should have failed..");
    } catch (OSMOException e) {
      //expected
    }
    listener.validate("Sets of steps with last step and no catch for throw");
  }

  @Test
  public void modelThrowsAndDoesNotFail() {
    TestSequenceListener listener = new TestSequenceListener(false);
    listener.addExpected("suite-start", "start", "t:hello", "t:hello", "ls:lastStepStanding", "end", "start", "t:hello", "t:hello", "ls:lastStepStanding", "end", "suite-end");
    ErrorModel5 model = new ErrorModel5();
    OSMOTester osmo = new OSMOTester();
    osmo.addListener(listener);
    osmo.setTestEndCondition(new Length(2));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.getConfig().setStopTestOnError(false);
    osmo.addModelObject(model);
    osmo.generate(123);
    listener.validate("Sets of steps with last step and catch for throw");
  }

  @Test
  public void endConditionEnds() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    ValidTestModel2 model = new ValidTestModel2(new Requirements(), ps);
    OSMOTester osmo = new OSMOTester();
    osmo.setTestEndCondition(new Length(5));
    osmo.setSuiteEndCondition(new Length(1));
    osmo.addModelObject(model);
    osmo.generate(123);
    String actual = out.toString();
    assertEquals("Set of executed steps", ":hello:world:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:epixx_pre:epixx:epixx_oracle:last", actual);
  }
}

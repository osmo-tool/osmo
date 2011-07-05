package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.strategy.LengthStrategy;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Teemu Kanstren
 */
public class ListenerTests {
  private OSMOTester osmo = null;
  private TestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @Test
  public void noEnabledTransition() {
    listener.addExpected("suite-start", "start", "g:kitted(epixx)", "g:listCheck(world)");
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
    listener.validate("No enabled transitions, generated sequence");
  }

  @Test
  public void generateTestModel2() {
    listener.addExpected("suite-start");
    listener.addExpected("start", "g:helloCheck(hello)", "g:kitted(epixx)", "g:worldCheck(world)", "t:hello");
    listener.addExpected("g:helloCheck(hello)", "g:kitted(epixx)", "g:worldCheck(world)", "t:world");
    listener.addExpected("g:helloCheck(hello)", "g:kitted(epixx)", "g:worldCheck(world)", "t:epixx", "end");
    listener.addExpected("suite-end");
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel2(new Requirements(), ps));
    LengthStrategy length3 = new LengthStrategy(3);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length3);
    osmo.setSuiteStrategy(length1);
    osmo.generate();
    listener.validate("Generated sequence for test model 2");
  }
}

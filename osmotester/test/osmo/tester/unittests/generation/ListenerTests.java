package osmo.tester.unittests.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.listener.TracePrinter;
import osmo.tester.model.Requirements;
import osmo.tester.unittests.testmodels.ValidTestModel1;
import osmo.tester.unittests.testmodels.ValidTestModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class ListenerTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    TestUtils.startOutputCapture();
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @After
  public void down() {
    TestUtils.endOutputCapture();
  }

  @Test
  public void noEnabledTransition() {
    listener.addExpected("suite-start", "start", "g:epixx", "g:world", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(ValidTestModel1.class));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(555);
      fail("Generation without available transitions should fail.");
    } catch (IllegalStateException e) {
      assertEquals("No test step available.", e.getMessage());
    }
    listener.validate("No enabled transitions, generated sequence");
  }

  @Test
  public void generateTestModel2() {
    listener.setTracePrePost(true);
    listener.addExpected("suite-start");
    listener.addExpected("start", "g:epixx", "g:hello", "g:world", "t:hello");
    listener.addExpected("g:epixx", "g:hello", "g:world", "t:world");
    listener.addExpected("g:epixx", "g:hello", "g:world", "pre:epixx", "t:epixx", "post:epixx", "ls:last", "end");
    listener.addExpected("suite-end");
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(new ValidTestModel2(new Requirements(), ps));
    osmo.setModelFactory(factory);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(555);
    listener.validate("Generated sequence for test model 2");
  }
  
  @Test
  public void tracePrinter() {
    osmo.addModelObject(new ValidTestModel2(new Requirements()));
    TracePrinter printer = new TracePrinter();
    osmo.addListener(printer);
    osmo.setTestEndCondition(new Length(3));
    osmo.setSuiteEndCondition(new Length(2));
    osmo.generate(555);
    String output = TestUtils.getOutput();
    String expected = "1.1.STEP:HELLO\n" +
            "1.2.STEP:WORLD\n" +
            "1.3.STEP:EPIXX\n" +
            "1.4.LASTSTEP:LAST\n" +
            "2.1.STEP:EPIXX\n" +
            "2.2.STEP:EPIXX\n" +
            "2.3.STEP:EPIXX\n" +
            "2.4.LASTSTEP:LAST\n" +
            "generated 2 tests.\n" +
            "\n" +
            "Covered elements:\n" +
            "Total steps: 6\n" +
            "Unique steps: 3 (of 3)\n" +
            "Unique step-pairs: 5 (of 5)\n" +
            "Unique requirements: 3\n" +
            "Variable values: 0\n" +
            "Unique coverage-values: 0\n" +
            "Unique coverage-value-pairs: 0\n" +
            "\n" +
            "Requirements:[]\n" +
            "Covered:[epix, hello, world]\n" +
            "Not covered:[]\n" +
            "\n";
    expected = unifyLineSeparators(expected, "\n");
    output = unifyLineSeparators(output, "\n");
    assertEquals("Captured output but TracePrinter", expected, output);
  }
}

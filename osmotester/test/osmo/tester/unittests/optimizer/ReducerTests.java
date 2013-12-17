package osmo.tester.unittests.optimizer;

import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerState;
import osmo.tester.unittests.testmodels.ErrorModelProbability;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ReducerTests {
  @Test
  public void probableModel() {
    Reducer reducer = new Reducer(1, 111);
    OSMOConfiguration config = reducer.getConfig();
    config.setFactory(new ReflectiveModelFactory(ErrorModelProbability.class));
    config.setTestEndCondition(new Length(50));
    config.setSuiteEndCondition(new Length(20));
    ReducerState state = reducer.search(5, TimeUnit.SECONDS, 50, 10);
    List<TestCase> tests = state.getTests();
    assertEquals("Number of tests", 3, tests.size());
    TestCase test1 = tests.get(0);
    TestCase test2 = tests.get(1);
    TestCase test3 = tests.get(2);
    assertEquals("First test length", 5, test1.getAllStepNames().size());
    assertEquals("Second test length", 3, test2.getAllStepNames().size());
    assertEquals("Final test length", 1, test3.getAllStepNames().size());
  } 
}

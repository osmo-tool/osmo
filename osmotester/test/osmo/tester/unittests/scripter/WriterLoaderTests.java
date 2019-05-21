package osmo.tester.unittests.scripter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.scripter.internal.TestLoader;
import osmo.tester.scripter.internal.TestScript;
import osmo.tester.scripter.internal.TestWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class WriterLoaderTests {
  private static final String TEMP_DIR = "osmo-unit-test-temp";
  private TestWriter writer;
  private TestLoader loader;
  
  @Before
  public void setup() {
    writer = new TestWriter(TEMP_DIR);
    loader = new TestLoader();
  }
  
  @After
  public void cleanup() throws Exception {
    //strangely it seems we need to run garbage collector or the file cannot be deleted..?
    //http://stackoverflow.com/questions/991489/i-cant-delete-a-file-in-java
    System.gc();
    Thread.sleep(100);
    TestUtils.recursiveDelete(TEMP_DIR);
    Thread.sleep(100);
  }
  
  @Test
  public void writeAndLoadEmptyTest() {
    TestCase test = new TestCase(777);
    writer.write(test);
    List<TestScript> scripts = loader.loadTests(TEMP_DIR);
    assertEquals("Number of loaded scripts", 1, scripts.size());
    assertEquals("Scripts", "[TestScript{seed=777, steps=[]}]", scripts.toString());
  }

  @Test
  public void writeAndLoadTestWithSteps() {
    TestCase test = new TestCase(1911);
    test.addStep(new FSMTransition("login"));
    test.addStep(new FSMTransition("send message"));
    test.addStep(new FSMTransition("logout"));
    writer.write(test);
    List<TestScript> scripts = loader.loadTests(TEMP_DIR);
    assertEquals("Number of loaded scripts", 1, scripts.size());
    assertEquals("Scripts", "[TestScript{seed=1911, steps=[login, send message, logout]}]", scripts.toString());
  }

  @Test
  public void writeAndLoadTwoTests() {
    TestCase test1 = new TestCase(5555555555l);
    test1.addStep(new FSMTransition("login"));
    test1.addStep(new FSMTransition("send message"));
    test1.addStep(new FSMTransition("logout"));
    writer.write(test1);

    TestCase test2 = new TestCase(-99);
    test2.addStep(new FSMTransition("tweet"));
    test2.addStep(new FSMTransition("tweet"));
    test2.addStep(new FSMTransition("insta"));
    test2.addStep(new FSMTransition("cave"));
    writer.write(test2);

    List<TestScript> scripts = loader.loadTests(TEMP_DIR);
    List<String> scriptTxts = new ArrayList<>();
    scripts.forEach(ts -> scriptTxts.add(ts.toString()));
    Collections.sort(scriptTxts, Collections.reverseOrder());
    assertEquals("Number of loaded scripts", 2, scripts.size());
    assertEquals("Scripts", "[TestScript{seed=5555555555, steps=[login, send message, logout]}, TestScript{seed=-99, steps=[tweet, tweet, insta, cave]}]", scriptTxts.toString());
  }

  @Test
  public void writeAndLoadSuite() {
    TestCase test1 = new TestCase(5555555555L);
    test1.addStep(new FSMTransition("login"));
    test1.addStep(new FSMTransition("send message"));
    test1.addStep(new FSMTransition("logout"));

    TestCase test2 = new TestCase(-99);
    test2.addStep(new FSMTransition("tweet"));
    test2.addStep(new FSMTransition("tweet"));
    test2.addStep(new FSMTransition("insta"));
    test2.addStep(new FSMTransition("cave"));
    
    List<TestCase> suite = new ArrayList<>();
    suite.add(test1);
    suite.add(test2);
    writer.write(suite);

    List<TestScript> scripts = loader.loadTests(TEMP_DIR);
    List<String> scriptTxts = new ArrayList<>();
    scripts.forEach(ts -> scriptTxts.add(ts.toString()));
    Collections.sort(scriptTxts, Collections.reverseOrder());
    assertEquals("Number of loaded scripts", 2, scripts.size());
    assertEquals("Scripts", "[TestScript{seed=5555555555, steps=[login, send message, logout]}, TestScript{seed=-99, steps=[tweet, tweet, insta, cave]}]", scriptTxts.toString());
  }
}

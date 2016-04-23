package osmo.tester.unittests.scripter;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.scripter.internal.TestLoader;
import osmo.tester.scripter.internal.TestScript;
import osmo.tester.scripter.internal.TestScripts;
import osmo.tester.unittests.testmodels.RandomValueModel2;
import osmo.tester.unittests.testmodels.VariableModel2;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ScriptParserTests {
  @Test
  public void parseValidScript1() {
    String input = "";
    input += "#hello\n";
    input += "seed: 1911\n";
    input += "login\n";
    input += "sent message\n";
    input += "logout\n\n";
    TestLoader loader = new TestLoader();
    TestScript script = loader.parse(input);
    assertEquals("Seed value", 1911, script.getSeed().longValue());
    List<String> steps = script.getSteps();
    assertEquals("Steps", "[login, sent message, logout]", steps.toString());
  }
  
  @Test
  public void parseValidScript2() {
    String input = "";
    input += "#hello\n";
    input += "seed:777 \n";
    input += "#steps start\n";
    input += "login   \n";
    input += "#should we trim like someone might write these by hand?\n";
    input += "   sent message\n";
    input += "#in any case the linefeed will mess up on systems if hand written\n";
    input += " logout \n";
    input += "#which would require unifying line separators\n";
    input += "#and so we do not trim the files as someone might put whitespace in a step name\n";
    TestLoader loader = new TestLoader();
    TestScript script = loader.parse(input);
    assertEquals("Seed value", 777, script.getSeed().longValue());
    List<String> steps = script.getSteps();
    assertEquals("Steps", "[login   ,    sent message,  logout ]", steps.toString());
  }
  
  @Test
  public void brokenNoSeed() {
    String input = "";
    input += "login\n";
    input += "sent message\n";
    input += "logout\n";
    TestLoader loader = new TestLoader();
    try {
      TestScript script = loader.parse(input);
      fail("Missing seed in test script should throw Exception");
    } catch (Exception e) {
      assertEquals("Failure message for broken test script", "Test script has no seed defined.", e.getMessage());
    }
  }
  
  @Test
  public void executeTest() {
    List<TestScript> scripts = new ArrayList<>();
    TestScript script = new TestScript();
    script.setSeed(1);
    script.addStep("Step1");
    scripts.add(script);
    OSMOTester tester = new OSMOTester();
    OSMOConfiguration config = tester.getConfig();
    config.addModelObject(new RandomValueModel2());
    config.setScripts(scripts);
    tester.generate(1);
    assertEquals("[TestCase:[Step1]]", tester.getSuite().getAllTestCases().toString());
  }

  @Test
  public void executeTests() {
    List<TestScript> scripts = new ArrayList<>();
    TestScript script1 = new TestScript();
    script1.setSeed(777);
    script1.addStep("Step1");
    script1.addStep("Step1");
    script1.addStep("Step1");
    scripts.add(script1);
    TestScript script2 = new TestScript();
    script2.setSeed(1911);
    script2.addStep("Step2");
    script2.addStep("Step1");
    script2.addStep("Step3");
    scripts.add(script2);
    TestScript script3 = new TestScript();
    script3.setSeed(555);
    script3.addStep("Step2");
    script3.addStep("Step1");
    script3.addStep("Step3");
    script3.addStep("Step3");
    script3.addStep("Step2");
    script3.addStep("Step2");
    scripts.add(script3);
    OSMOTester tester = new OSMOTester();
    OSMOConfiguration config = tester.getConfig();
    config.addModelObject(new RandomValueModel2());
    config.setScripts(scripts);
    tester.generate(1);
    List<TestCase> tests = tester.getSuite().getAllTestCases();
    assertEquals("[TestCase:[Step1, Step1, Step1], TestCase:[Step2, Step1, Step3], TestCase:[Step2, Step1, Step3, Step3, Step2, Step2]]", tests.toString());
    TestCase test1 = tests.get(0);
    assertEquals("Test seed in scripting", 777, test1.getSeed());
    TestCase test2 = tests.get(1);
    assertEquals("Test seed in scripting", 1911, test2.getSeed());
    TestCase test3 = tests.get(2);
    assertEquals("Test seed in scripting", 555, test3.getSeed());
  }
}

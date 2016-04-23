package osmo.tester.unittests;

import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;
import osmo.tester.scripter.internal.TestScript;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class ScriptBuilder {
  public final List<String> steps = new ArrayList<>();

  public void add(int count, String step) {
    for (int i = 0 ; i < count ; i++) {
      steps.add(step);
    }
  }

  public void shuffle() {
    List<String> shuffled = new ArrayList<>();
    Randomizer rand = new Randomizer(555);
    while (steps.size() > 0) {
      String step = rand.oneOf(steps);
      shuffled.add(step);
      steps.remove(step);
    }
    steps.addAll(shuffled);
  }

  public TestCase create(long seed, ModelFactory mf) {
    List<TestScript> scripts = new ArrayList<>();

    TestScript script1 = new TestScript();
    script1.setSeed(seed);
    steps.forEach(script1::addStep);

    scripts.add(script1);
    OSMOTester tester = new OSMOTester();
    OSMOConfiguration config = tester.getConfig();
    config.setFactory(mf);
    config.setScripts(scripts);
    config.setStopGenerationOnError(false);
    tester.generate(1);
    List<TestCase> tests = tester.getSuite().getAllTestCases();
    return tests.get(0);
  }
}

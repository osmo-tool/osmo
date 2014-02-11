package osmo.tester.scripter.internal;

import osmo.common.TestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TestLoader {
  public List<TestScript> loadTests(String dir) {
    List<TestScript> scripts = new ArrayList<>();
    List<String> files = TestUtils.listFiles(dir, "tc", true);
    for (String file : files) {
      String scriptText = TestUtils.readFile(file, "UTF8");
      TestScript script = parse(scriptText);
      scripts.add(script);
    }
    return scripts;
  }

  public TestScript parse(String scriptText) {
    TestScript script = new TestScript();
    String[] lines = scriptText.split("\n");
    for (String line : lines) {
      //comments start with #
      if (line.startsWith("#")) continue;
      if (line.startsWith("seed:")) {
        parseSeed(script, line);
        continue;
      }
      //if we get here it is not a comment and it is not a seed definition, so it must be a step
      script.addStep(line);
    }
    if (script.getSeed() == null) throw new IllegalArgumentException("Test script has no seed defined.");
    return script;
  }

  private void parseSeed(TestScript script, String line) {
    int start = "seed:".length();
    String seedText = line.substring(start, line.length()).trim();
    long seed = Long.parseLong(seedText);
    script.setSeed(seed);
  }
}

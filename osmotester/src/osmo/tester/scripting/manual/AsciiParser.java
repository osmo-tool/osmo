package osmo.tester.scripting.manual;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.scripting.AbstractAsciiParser;

import java.util.ArrayList;
import java.util.List;

/** @author Teemu Kanstren */
public class AsciiParser extends AbstractAsciiParser {
  private static Logger log = new Logger(AsciiParser.class);

  public AsciiParser() {
    super(log);
  }

  public List<TestScript> parse(String input) {
    String[] lines = parseLines(input);
    String[] script = parseTable(lines, "action", "name", "value");
    return parseTests(script);
  }

  private List<TestScript> parseTests(String[] script) {
    List<TestScript> tests = new ArrayList<TestScript>();
    TestScript test = null;
    for (int i = 0; i < script.length; i += 3) {
      String action = script[i];
      String name = script[i+ 1];
      String parameter = script[i + 2];
      log.debug("Action found: [" + action + ", " + name + ", "+parameter+"]");
      if (action.equalsIgnoreCase("new test")) {
        log.debug("new test");
        test = new TestScript();
        tests.add(test);
      }
      if (action.equalsIgnoreCase("step")) {
        log.debug("Step:"+name);
        if (test == null) {
          throw new IllegalArgumentException("Script step must be preceded by test start definition (step:"+name+")");
        }
        test.addStep(name);
      }
      if (action.equalsIgnoreCase("variable")) {
        log.debug("variable");
        if (test == null) {
          throw new IllegalArgumentException("Script test value must be preceded by test definition (variable:"+name+")");
        }
        test.addValue(name, parameter);
      }
    }
    return tests;
  }
}

package osmo.tester.examples.tutorial.optimizer;

import osmo.tester.annotation.AfterTest;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.generator.testsuite.TestSuite;

/**
 * @author Teemu Kanstren.
 */
public class ScriptMob {
  private final OfflineScripter scripter;
  private TestSuite suite;

  public ScriptMob(OfflineScripter scripter) {
    this.scripter = scripter;
  }

  @AfterTest
  public void storeScript() {
    suite.getCurrentTest().setAttribute("script", scripter.getScript());
  }
}

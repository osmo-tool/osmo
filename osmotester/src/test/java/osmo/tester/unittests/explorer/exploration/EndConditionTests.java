package osmo.tester.unittests.explorer.exploration;

import org.junit.Test;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.ExplorationEndCondition;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class EndConditionTests {
  private String ln = System.lineSeparator();

  @Test
  public void noMinDefinedForSuite() {
    ExplorationConfiguration config = new ExplorationConfiguration(null, 5, 1);
    config.setMinSuiteLength(0);
    config.setMinSuiteScore(0);
    try {
      ExplorationEndCondition ec = new ExplorationEndCondition(config, null, true);
    } catch (IllegalArgumentException e) {
      String expected = "Invalid exploration configuration:" + ln;
      expected += "Exploration requires defining either minimum suite length or minimum suite score." + ln;
      assertEquals("Invalid e for no suite minimum", expected, e.getMessage());
    }
  }

  @Test
  public void noMinDefinedForTestAndSuite() {
    ExplorationConfiguration config = new ExplorationConfiguration(null, 5, 1);
    config.setMinTestLength(0);
    config.setMinTestScore(0);
    try {
      ExplorationEndCondition ec = new ExplorationEndCondition(config, null, true);
    } catch (IllegalArgumentException e) {
      String expected = "Invalid exploration configuration:" + ln;
      expected += "Exploration requires defining either minimum suite length or minimum suite score." + ln;
      expected += "Exploration requires defining either minimum test length or minimum test score." + ln;
      assertEquals("Invalid e for no test and suite minimum", expected, e.getMessage());
    }
  }
}

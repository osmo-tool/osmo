package osmo.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** @author Teemu Kanstren */
public class SetMapTests {
  @Test
  public void oneVariableOneValue() {
    SetMap<String, String> sm = new SetMap<>();
    sm.addValue("bob", "hi");
    assertEquals("Number of values", 1, sm.count());
  }

  @Test
  public void fiveVariablesOneValue() {
    SetMap<String, String> sm = new SetMap<>();
    sm.addValue("bob", "hi");
    sm.addValue("joe", "hi");
    sm.addValue("moe", "hi");
    sm.addValue("aa", "hi");
    sm.addValue("not god", "hi");
    assertEquals("Number of values", 5, sm.count());
  }

  @Test
  public void twoVariablesFiveValuesAndDuplicate() {
    SetMap<String, String> sm = new SetMap<>();
    sm.addValue("bob", "hi1");
    sm.addValue("bob", "hi2");
    sm.addValue("bob", "hi3");
    sm.addValue("bob", "hi4");
    sm.addValue("bob", "hi5");
    sm.addValue("joe", "hi");
    sm.addValue("joe", "hi");
    sm.addValue("joe", "hi");
    sm.addValue("joe", "hi");
    sm.addValue("joe", "hi2");
    assertEquals("Number of values", 7, sm.count());
  }
}

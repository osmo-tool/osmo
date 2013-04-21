package osmo.tester.coverage;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class IntegerRangeTests {
  @Test
  public void nullName() {
    try {
      RangeCategory2 range = new RangeCategory2().addCategory(0,0,null);
      fail("Null range input should fail");
    } catch (NullPointerException e) {
      assertEquals("Error msg for null range input name.", "Range name cannot be null.", e.getMessage());
    }
  }

  @Test
  public void minIsMax() {
    try {
      RangeCategory2 range = new RangeCategory2();
      range.addCategory(5, 1, "many");
      fail("Min and max in wrong order should fail");
    } catch (Exception e) {
      assertEquals("Error msg for min and max in wrong order.", "Minimum cannot be bigger than maximum:5 > 1", e.getMessage());
    }
  }

  @Test
  public void outOfRangeData() {
    RangeCategory2 range = new RangeCategory2();
    range.addCategory(1, 5, "many");
    range.process(6);
    range.process(4);
    range.process(0);
    assertEquals("Invalid ranges should give no value", null, range.value());
  }

  @Test
  public void oneRangeMaxValue() {
    RangeCategory2 range = new RangeCategory2();
    range.addCategory(1, 5, "many");
    range.process(5);
    assertEquals("Value in range", "many", range.value());
  }

  @Test
  public void oneRangeMinValue() {
    RangeCategory2 range = new RangeCategory2();
    range.addCategory(1, 5, "many");
    range.process(1);
    assertEquals("Value in range", "many", range.value());
  }
}
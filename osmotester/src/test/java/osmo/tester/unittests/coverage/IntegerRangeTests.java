package osmo.tester.unittests.coverage;
import org.junit.Test;
import osmo.tester.coverage.RangeCategory;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class IntegerRangeTests {
  @Test
  public void nullName() {
    try {
      RangeCategory range = new RangeCategory().addCategory(0,0,null);
      fail("Null range input should fail");
    } catch (NullPointerException e) {
      assertEquals("Error msg for null range input name.", "Range name cannot be null.", e.getMessage());
    }
  }

  @Test
  public void minIsMax() {
    try {
      RangeCategory range = new RangeCategory();
      range.addCategory(5, 1, "many");
      fail("Min and max in wrong order should fail");
    } catch (Exception e) {
      assertEquals("Error msg for min and max in wrong order.", "Minimum cannot be bigger than maximum:5 > 1", e.getMessage());
    }
  }

  @Test
  public void outOfRangeData() {
    RangeCategory range = new RangeCategory();
    range.addCategory(1, 5, "many");
    range.process(6);
    range.process(4);
    range.process(0);
    assertEquals("Invalid ranges should give no value", null, range.value());
  }

  @Test
  public void oneRangeMaxValue() {
    RangeCategory range = new RangeCategory();
    range.addCategory(1, 5, "many");
    range.process(5);
    assertEquals("Value in range", "many", range.value());
  }

  @Test
  public void oneRangeMinValue() {
    RangeCategory range = new RangeCategory();
    range.addCategory(1, 5, "many");
    range.process(1);
    assertEquals("Value in range", "many", range.value());
  }
}
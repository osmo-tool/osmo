package osmo.tester.unittests.coverage;

import org.junit.Test;
import static org.junit.Assert.*;
import osmo.tester.coverage.NumberCategoryPartitioner;

/**
 * @author Teemu Kanstren.
 */
public class NumberCategoryPartitionTests {
  @Test
  public void zeroOneMany() {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.zeroOneMany();
    assertEquals("Zero category", "zero", ncp.categoryFor(0));
    assertEquals("One category", "one", ncp.categoryFor(1));
    assertEquals("Many category for 2", "N", ncp.categoryFor(2));
    assertEquals("Many category for 2000", "N", ncp.categoryFor(2000));
    assertEquals("Out of bounds category for -1", "OutOfBounds(-1)", ncp.categoryFor(-1));
    assertEquals("Out of bounds category for -0.1", "OutOfBounds(-0.1)", ncp.categoryFor(-0.1));
    assertEquals("Out of bounds category for 0.1", "OutOfBounds(0.1)", ncp.categoryFor(0.1));
  }

  @Test
  public void oneTwoMany() {
    NumberCategoryPartitioner ncp = NumberCategoryPartitioner.oneTwoMany();
    assertEquals("One category", "one", ncp.categoryFor(1));
    assertEquals("Two category", "two", ncp.categoryFor(2));
    assertEquals("Many category for 3", "N", ncp.categoryFor(3));
    assertEquals("Many category for 2000", "N", ncp.categoryFor(2000));
    assertEquals("Out of bounds category for 0", "OutOfBounds(0)", ncp.categoryFor(0));
    assertEquals("Out of bounds category for -1", "OutOfBounds(-1)", ncp.categoryFor(-1));
    assertEquals("Out of bounds category for -0.1", "OutOfBounds(-0.1)", ncp.categoryFor(-0.1));
    assertEquals("Out of bounds category for 0.1", "OutOfBounds(0.1)", ncp.categoryFor(0.1));
  }

  @Test
  public void fractions() {
    NumberCategoryPartitioner ncp = new NumberCategoryPartitioner();
    ncp.addCategory(1, 5, "a few");
    ncp.addOCCategory(5.1, 5.2, "bit over 5");
    ncp.addCCCategory(5.2, 6, "over 5 under 6");
    ncp.addCOCategory(6, Integer.MAX_VALUE, "N");
    assertEquals("Few category(1)", "a few", ncp.categoryFor(1));
    assertEquals("Few category(5)", "a few", ncp.categoryFor(5));
    assertEquals("Out of bounds category for 0.999", "OutOfBounds(0.999)", ncp.categoryFor(0.999));
    assertEquals("Out of bounds category for 5.2", "OutOfBounds(5.2)", ncp.categoryFor(5.2));
    assertEquals("Out of bounds category for 6", "OutOfBounds(6)", ncp.categoryFor(6));
    assertEquals("Many category for 6.001", "N", ncp.categoryFor(6.001));
    assertEquals("Many category for Integer.MAX_VALUE", "N", ncp.categoryFor(Integer.MAX_VALUE));
  }

  @Test
  public void invalidInputs() {
    NumberCategoryPartitioner ncp = new NumberCategoryPartitioner();
    try {
      ncp.addCategory(-1, -2, "Min larger than max");
      fail("Should not accept min larger than max category");
    } catch (IllegalArgumentException e) {
      assertEquals("Category min cannot be larger than max:cat(-1,-2)", e.getMessage());
    }
    ncp.addCategory(1, 2, "One to Two");
    try {
      ncp.addCategory(2, 3, "Overlap");
      fail("Should not accept overlapping categories");
    } catch (Exception e) {
      assertEquals("Overlapping categories: cat(2,3) and cat(1,2)", e.getMessage());
    }
  }
}

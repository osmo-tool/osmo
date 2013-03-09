package osmo.tester.scripting.slicing;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.data.CharSet;
import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.Text;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueRangeSet;
import osmo.tester.model.data.ValueSet;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class DataObjectSliceTests {
  private TestSuite suite = null;

  @Before
  public void setup() {
    OSMOConfiguration.reset();
    OSMOConfiguration.setSeed(1);
    suite = new TestSuite();
    TestCase test = suite.startTest();
    test.addStep(new FSMTransition("test"));
  }

  @Test
  public void charSetSlice() {
    CharSet set = new CharSet();
    set.setSuite(suite);
    try {
      set.setStrategy(DataGenerationStrategy.SLICED);
    } catch (UnsupportedOperationException e) {
      assertEquals("Error message for unsupported slicing for " + CharSet.class.getSimpleName(), "CharSet only supports Random, Looping, and Invalid generation strategies. Given:SLICED", e.getMessage());
    }
  }

  @Test
  public void valueRangeSlice() {
    ValueRange<Integer> range = new ValueRange<>(0, 10);
    range.setSuite(suite);
    range.setName("range");
    OSMOConfiguration.addSlice("range", "1");
    OSMOConfiguration.addSlice("range", "5");
    range.setStrategy(DataGenerationStrategy.SLICED);
    int ones = 0;
    int fives = 0;
    for (int i = 0 ; i < 100 ; i++) {
      int value = range.next();
      switch (value) {
        case 1:
          ones++;
          break;
        case 5:
          fives++;
          break;
        default:
          fail("Sliced " + ValueRange.class.getSimpleName() + " should only produce sliced values");
          break;
      }
    }
    assertTrue("Number of ones should be > 40, was " + ones, ones >= 40);
    assertTrue("Number of fives should be > 40, was " + fives, fives >= 40);
  }

  @Test
  public void invalidValueRangeSlice() {
    ValueRange<Integer> range = new ValueRange<>(0, 10);
    range.setStrategy(DataGenerationStrategy.SLICED);
    range.setName("range");
    range.setSuite(suite);
    try {
      OSMOConfiguration.addSlice("range", "sdfs");
      range.next();
      fail("Invalid serialized value for value range should throw NumberFormatException.");
    } catch (NumberFormatException e) {
      //
    }
  }

  @Test
  public void valueRangeSetSlice() {
    ValueRangeSet<Integer> range = new ValueRangeSet<>();
    range.setSuite(suite);
    range.setName("range");
    range.addPartition(0, 10);
    range.addPartition(100, 200);
    OSMOConfiguration.addSlice("range", "55");
    OSMOConfiguration.addSlice("range", "555");
    range.setStrategy(DataGenerationStrategy.SLICED);
    int count55 = 0;
    int count555 = 0;
    for (int i = 0 ; i < 100 ; i++) {
      int value = range.next();
      switch (value) {
        case 55:
          count55++;
          break;
        case 555:
          count555++;
          break;
        default:
          fail("Sliced " + ValueRange.class.getSimpleName() + " should only produce sliced values (55, 555), got: " + value);
          break;
      }
    }
    assertTrue("Number of ones should be > 40, was " + count55, count55 >= 40);
    assertTrue("Number of fives should be > 40, was " + count555, count555 >= 40);
  }

  @Test
  public void valueSetSlice() {
    ValueSet<String> set = new ValueSet<>();
    set.setSuite(suite);
    set.setName("set");
    set.add("bob");
    set.add("job");
    set.add("hop");
    set.add("lop");
    set.setStrategy(DataGenerationStrategy.SLICED);
    OSMOConfiguration.addSlice("set", "whut");
    OSMOConfiguration.addSlice("set", "dhut");
    int whuts = 0;
    int dhuts = 0;
    for (int i = 0 ; i < 100 ; i++) {
      String value = set.next();
      switch (value) {
        case "whut":
          whuts++;
          break;
        case "dhut":
          dhuts++;
          break;
        default:
          fail("Sliced " + ValueRange.class.getSimpleName() + " should only produce sliced values ('whut', 'dhut'), got: " + value);
          break;
      }
    }
    assertTrue("Number of ones should be > 40, was " + whuts, whuts >= 40);
    assertTrue("Number of fives should be > 40, was " + dhuts, dhuts >= 40);
  }

  @Test
  public void wordsSlice() {
    Text text = new Text();
    text.setSuite(suite);
    try {
      text.setStrategy(DataGenerationStrategy.SLICED);
    } catch (UnsupportedOperationException e) {
      assertEquals("Error message for unsupported slicing for " + Text.class.getSimpleName(), "Text supports only Scripted, Random and Invalid data generation strategy, given: SLICED.", e.getMessage());
    }
  }
}

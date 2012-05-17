package osmo.tester.scripting.slicing;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.Observer;
import osmo.tester.model.dataflow.Boundary;
import osmo.tester.model.dataflow.CharSet;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.DataType;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueRangeSet;
import osmo.tester.model.dataflow.ValueSet;
import osmo.tester.model.dataflow.Words;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class DataObjectSliceTests {
  @Before
  public void setup() {
    OSMOConfiguration.reset();
  }

  @Test
  public void charSetSlice() {
    CharSet set = new CharSet();
    try {
      set.setStrategy(DataGenerationStrategy.SLICED);
    } catch (UnsupportedOperationException e) {
      assertEquals("Error message for unsupported slicing for "+CharSet.class.getSimpleName(), "CharSet only supports Random, Looping, and Invalid generation strategies. Given:SLICED", e.getMessage());
    }
  }

  @Test
  public void valueRangeSlice() {
    ValueRange<Integer> range = new ValueRange<>(0, 10);
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
    assertTrue("Number of ones should be > 40, was "+ones, ones >= 40);
    assertTrue("Number of fives should be > 40, was "+fives, fives >= 40);
  }

  @Test
  public void invalidValueRangeSlice() {
    ValueRange<Integer> range = new ValueRange<>(0, 10);
    range.setName("range");
    try {
      OSMOConfiguration.addSlice("range", "sdfs");
      range.getSlices();
      fail("Invalid serialized value for value range should throw NumberFormatException.");
    } catch (NumberFormatException e) {
      //
    }
  }

  @Test
  public void valueRangeSetSlice() {
    ValueRangeSet<Integer> range = new ValueRangeSet<>();
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
    assertTrue("Number of ones should be > 40, was "+count55, count55 >= 40);
    assertTrue("Number of fives should be > 40, was "+count555, count555 >= 40);
  }

  @Test
  public void valueSetSlice() {
    ValueSet<String> set = new ValueSet<>();
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
    assertTrue("Number of ones should be > 40, was "+whuts, whuts >= 40);
    assertTrue("Number of fives should be > 40, was "+dhuts, dhuts >= 40);
  }

  @Test
  public void wordsSlice() {
    Words words = new Words();
    try {
      words.setStrategy(DataGenerationStrategy.SLICED);
    } catch (UnsupportedOperationException e) {
      assertEquals("Error message for unsupported slicing for "+Words.class.getSimpleName(), "Words supports only Scripted, Random and Invalid data generation strategy, given: SLICED.", e.getMessage());
    }
  }
}

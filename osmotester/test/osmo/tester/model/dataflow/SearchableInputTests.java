package osmo.tester.model.dataflow;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.Observer;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class SearchableInputTests {
  @Before
  public void reset() {
    OSMOConfiguration.reset();
  }
  
  @Test
  public void valueRange() {
    ValueRange<Integer> range = new ValueRange<>(1, 10);
    range.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-range";
    range.setName(name);
    Collection<Integer> expected = new ArrayList<>();
    for (int i = 0; i < 11; i++) {
      range.next();
      expected.add(i % 10 + 1);
    }
    Collection actual = Observer.getObservations();
    assertEquals("Observed generated values", expected, actual);
  }

  @Test
  public void valueSet() {
    ValueSet<String> set = new ValueSet<>("v1", "v2", "v3");
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-set";
    set.setName(name);
    Collection<String> expected = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      set.next();
      expected.add("v" + (i % 3 + 1));
    }
    Collection actual = Observer.getObservations();
    assertEquals("Observed generated values", expected, actual);
  }
}

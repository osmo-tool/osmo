package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.InputObserver;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class SearchableInputTests {
  @Test
  public void valueRange() {
    ValueRange<Integer> range = new ValueRange<>(1, 10);
    range.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-range";
    range.setName(name);
    TestInputObserver<Integer> observer = new TestInputObserver<>(name);
    range.setObserver(observer);
    Collection<Integer> expected = new ArrayList<>();
    for (int i = 0; i < 11; i++) {
      range.next();
      expected.add(i % 10 + 1);
    }
    Collection<Integer> actual = observer.getObservations();
    assertEquals("Observed generated values", expected, actual);
  }

  @Test
  public void valueSet() {
    ValueSet<String> set = new ValueSet<>("v1", "v2", "v3");
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-set";
    set.setName(name);
    TestInputObserver<String> observer = new TestInputObserver<>(name);
    set.setObserver(observer);
    Collection<String> expected = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      set.next();
      expected.add("v" + (i % 3 + 1));
    }
    Collection<String> actual = observer.getObservations();
    assertEquals("Observed generated values", expected, actual);
  }

  private class TestInputObserver<T> implements InputObserver<T> {
    private Collection<T> observations = new ArrayList<>();
    private final String name;

    private TestInputObserver(String name) {
      this.name = name;
    }

    @Override
    public void value(String variable, T value) {
      assertEquals("Variable name", name, variable);
      observations.add(value);
    }

    public Collection<T> getObservations() {
      return observations;
    }
  }
}

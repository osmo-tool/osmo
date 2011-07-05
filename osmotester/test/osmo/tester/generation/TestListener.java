package osmo.tester.generation;

import org.junit.rules.ExpectedException;
import osmo.tester.generator.GenerationListener;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class TestListener implements GenerationListener {
  private Collection<String> steps = new ArrayList<String>();
  private Collection<String> expected = new ArrayList<String>();

  public void addExpected(String... items) {
    for (String item : items) {
      expected.add(item);
    }
  }

  @Override
  public void guard(String name) {
    steps.add("g:"+name);
  }

  @Override
  public void transition(String name) {
    steps.add("t:"+name);
  }

  @Override
  public void oracle(String name) {
    steps.add("o:"+name);
  }

  @Override
  public void testStarted() {
    steps.add("start");
  }

  @Override
  public void testEnded() {
    steps.add("end");
  }

  @Override
  public void suiteStarted() {
    steps.add("suite-start");
  }

  @Override
  public void suiteEnded() {
    steps.add("suite-end");
  }

  public void validate(String msg) {
    assertEquals(msg, expected, steps);
  }

  public Collection<String> getSteps() {
    return steps;
  }
}

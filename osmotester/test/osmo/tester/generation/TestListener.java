package osmo.tester.generation;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class TestListener implements GenerationListener {
  private Collection<String> steps = new ArrayList<String>();
  private Collection<String> expected = new ArrayList<String>();

  public void addExpected(String... items) {
    Collections.addAll(expected, items);
  }

  @Override
  public void guard(FSMTransition transition) {
    steps.add("g:" + transition.getName());
  }

  @Override
  public void transition(FSMTransition transition) {
    steps.add("t:" + transition.getName());
  }

  @Override
  public void pre(FSMTransition transition) {
    steps.add("pre:" + transition.getName());
  }

  @Override
  public void post(FSMTransition transition) {
    steps.add("post:" + transition.getName());
  }

  @Override
  public void testStarted(TestCase test) {
    steps.add("start");
  }

  @Override
  public void testEnded(TestCase test) {
    steps.add("end");
  }

  @Override
  public void suiteStarted(TestSuite suite) {
    steps.add("suite-start");
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    steps.add("suite-end");
  }

  public void validate(String msg) {
    assertEquals(msg, expected, steps);
  }

  public Collection<String> getSteps() {
    return steps;
  }
}

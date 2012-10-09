package osmo.tester.generation;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class TestSequenceListener implements GenerationListener {
  private Collection<String> steps = new ArrayList<>();
  private Collection<String> expected = new ArrayList<>();
  private List<String> temp = new ArrayList<>();
  private boolean previousWasGuard = false;
  private final boolean traceGuards;

  public TestSequenceListener() {
    this.traceGuards = true;
  }

  public TestSequenceListener(boolean traceGuards) {
    this.traceGuards = traceGuards;
  }

  public void addExpected(String... items) {
    Collections.addAll(expected, items);
  }

  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
    previousWasGuard = true;
    if (traceGuards) {
      temp.add("g:" + transition.getName());
    }
  }

  /**
   * This is used to sort the order of guards since reflection gives them to OSMO in different order depending
   * on the time of day, the sun spot locations, and other such attributes. We need deterministic order to get
   * a reliable test result.
   */
  private void storeGuards() {
    previousWasGuard = false;
    Collections.sort(temp);
    steps.addAll(temp);
    temp.clear();
  }

  @Override
  public void transition(FSMTransition transition) {
    storeGuards();
    steps.add("t:" + transition.getName());
  }

  @Override
  public void pre(FSMTransition transition) {
    storeGuards();
    steps.add("pre:" + transition.getName());
  }

  @Override
  public void post(FSMTransition transition) {
    storeGuards();
    steps.add("post:" + transition.getName());
  }

  @Override
  public void testStarted(TestCase test) {
    storeGuards();
    steps.add("start");
  }

  @Override
  public void testEnded(TestCase test) {
    storeGuards();
    steps.add("end");
  }

  @Override
  public void suiteStarted(TestSuite suite) {
    storeGuards();
    steps.add("suite-start");
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    storeGuards();
    steps.add("suite-end");
  }

  public void validate(String msg) {
    assertEquals(msg, expected, steps);
  }

  public Collection<String> getSteps() {
    return steps;
  }

  @Override
  public void testError(TestCase test, Exception error) {
  }
}

package osmo.tester.gui.jfx.executiontab.greedy;

import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class Iteration {
  private final int id;
  private static int nextId = 1;
  private final List<TestCase> tests;

  public Iteration(List<TestCase> tests) {
    this.id = nextId++;
    this.tests = tests;
  }

  public int getId() {
    return id;
  }

  public List<TestCase> getTests() {
    return tests;
  }

  @Override
  public String toString() {
    return "Iteration "+id;
  }
}

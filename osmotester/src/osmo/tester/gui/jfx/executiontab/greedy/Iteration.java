package osmo.tester.gui.jfx.executiontab.greedy;

import javafx.scene.chart.XYChart;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;

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
    this.tests = new ArrayList<>(tests);
  }

  public Iteration(Iteration cloneMe) {
    this.tests = new ArrayList<>(cloneMe.tests);
    this.id = cloneMe.id;
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

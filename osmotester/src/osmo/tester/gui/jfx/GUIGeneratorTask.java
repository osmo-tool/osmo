package osmo.tester.gui.jfx;

import osmo.tester.OSMOTester;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorTaskListener;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.greedy.GreedyOptimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class GUIGeneratorTask implements Runnable {
  private final OSMOTester tester;
  private final GreedyOptimizer greedy;
  private final OSMOExplorer explorer;
  private final long seed;
  private final int population;
  private final GeneratorTaskListener parent;
  private final GUIState state;

  public GUIGeneratorTask(GeneratorTaskListener parent, OSMOTester tester, long seed) {
    this.parent = parent;
    this.tester = tester;
    this.seed = seed;
    this.greedy = null;
    this.population = 0;
    this.explorer = null;
    this.state = null;
  }

  public GUIGeneratorTask(GeneratorTaskListener parent, GreedyOptimizer greedy, int population, long seed) {
    this.parent = parent;
    this.tester = null;
    this.seed = seed;
    this.greedy = greedy;
    this.population = population;
    this.explorer = null;
    this.state = null;
  }

  public GUIGeneratorTask(GeneratorTaskListener parent, OSMOExplorer explorer, GUIState state) {
    this.parent = parent;
    this.tester = null;
    this.seed = 0;
    this.greedy = null;
    this.population = 0;
    this.explorer = explorer;
    this.state = state;
  }

  @Override
  public void run() {
    List<TestCase> tests = new ArrayList<>();
    try {
      if (tester != null) tests = runTester();
      if (greedy != null) tests = runGreedy();
      if (explorer != null) tests = runExplorer();
    } catch (Exception e) {
      e.printStackTrace();
    }
    parent.taskFinished(tests);
  }

  private List<TestCase> runGreedy() {
    GenerationResults results = greedy.search(population, seed);
    return results.getTests();
  }

  private List<TestCase> runTester() {
    tester.generate(seed);
    return tester.getSuite().getAllTestCases();
  }

  private List<TestCase> runExplorer() {
    explorer.explore(state.getExplorationConfig());
    return explorer.getSuite().getAllTestCases();
  }
}

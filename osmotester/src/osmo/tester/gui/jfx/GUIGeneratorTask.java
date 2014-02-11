package osmo.tester.gui.jfx;

import osmo.tester.OSMOTester;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.optimizer.greedy.GreedyOptimizer;

/**
 * @author Teemu Kanstren
 */
public class GUIGeneratorTask implements Runnable {
  private final OSMOTester tester;
  private final GreedyOptimizer greedy;
  private final OSMOExplorer explorer;
  private final long seed;
  private final int population;
  private final GeneratorPane parent;
  private final GUIState state;

  public GUIGeneratorTask(GeneratorPane parent, OSMOTester tester, long seed) {
    this.parent = parent;
    this.tester = tester;
    this.seed = seed;
    this.greedy = null;
    this.population = 0;
    this.explorer = null;
    this.state = null;
  }

  public GUIGeneratorTask(GeneratorPane parent, GreedyOptimizer greedy, int population, long seed) {
    this.parent = parent;
    this.tester = null;
    this.seed = seed;
    this.greedy = greedy;
    this.population = population;
    this.explorer = null;
    this.state = null;
  }

  public GUIGeneratorTask(GeneratorPane parent, OSMOExplorer explorer, GUIState state) {
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
    try {
      if (tester != null) runTester();
      if (greedy != null) runGreedy();
      if (explorer != null) runExplorer();
    } catch (Exception e) {
      e.printStackTrace();
    }
    parent.taskFinished();
  }

  private void runGreedy() {
    greedy.search(population, seed);
  }

  private void runTester() {
    tester.generate(seed);
  }
  
  private void runExplorer() {
    explorer.explore(state.getExplorationConfig());
  }
}

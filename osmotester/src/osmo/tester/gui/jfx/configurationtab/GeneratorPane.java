package osmo.tester.gui.jfx.configurationtab;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.gui.jfx.GUIGeneratorTask;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.configurationtab.generator.Exploration;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorDescription;
import osmo.tester.gui.jfx.configurationtab.generator.Greedy;
import osmo.tester.gui.jfx.configurationtab.generator.GreedyParameters;
import osmo.tester.gui.jfx.configurationtab.generator.MultiCore;
import osmo.tester.gui.jfx.configurationtab.generator.MultiGreedy;
import osmo.tester.gui.jfx.configurationtab.generator.Requirements;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;
import osmo.tester.optimizer.greedy.GreedyOptimizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Teemu Kanstren
 */
public class GeneratorPane extends VBox {
  private final GUIState state;
  private Node old = null;
  private ComboBox<GeneratorDescription> generatorCombo;
  private GeneratorDescription generator = null;
  private GUIGeneratorTask task;

  public GeneratorPane(GUIState state) {
    super(10);
    this.state = state;
    createLabelPane();
  }

  private void createLabelPane() {
    HBox hbox = new HBox();
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.CENTER_LEFT);
    getChildren().add(hbox);
    ObservableList<Node> kids = hbox.getChildren();
    kids.add(new Label("Generator Settings"));
    generatorCombo = new ComboBox<>();
    generatorCombo.setOnAction((event) -> setGenerator(generatorCombo.getValue()));
    ObservableList<GeneratorDescription> items = generatorCombo.getItems();
    SingleCore singleCore = new SingleCore(state);
    items.addAll(singleCore, new MultiCore(state), new Greedy(state), new MultiGreedy(state), new Exploration(state), new Requirements(state));
    generatorCombo.setValue(singleCore);
    kids.add(generatorCombo);
    Button button = new Button("Generate");
    button.setOnAction((event) -> startGenerator());
    kids.add(button);
  }

  private void setGenerator(GeneratorDescription generator) {
    this.generator = generator;
    if (old != null) getChildren().remove(old);
    old = generator.createPane();
    getChildren().add(old);
  }

  private void startGenerator() {
    GeneratorDescription choice = generatorCombo.getValue();
    if (choice instanceof SingleCore) {
      startSingleCore();
    }
    if (choice instanceof MultiCore) {
      
    }
    if (choice instanceof Greedy) {
      startGreedy();
    }
    if (choice instanceof MultiGreedy) {

    }
    if (choice instanceof Exploration) {
      startExploration();
    }
    if (choice instanceof Requirements) {

    }
  }

  private void startSingleCore() {
    state.setOsmoConfig(new OSMOConfiguration());
    SingleCore sc = (SingleCore) generator;
    sc.storeParameters();
    state.openSingleCoreExecution();
    OSMOTester tester = new OSMOTester();
    tester.setConfig(state.getOsmoConfig());
    long seed = state.getSeed();
    task = new GUIGeneratorTask(this, tester, seed);
    new Thread(task).start();
//    tester.generate(seed);
  }
  
  private void startGreedy() {
    state.setOsmoConfig(new OSMOConfiguration());
    Greedy greedyDesc = (Greedy) generator;
    greedyDesc.storeParameters();
    state.openGreedyExecution();
    OSMOConfiguration config = state.getOsmoConfig();
    GreedyOptimizer greedy = new GreedyOptimizer(config, state.getScoreConfig());
    greedy.addIterationListener(state.getGreedyParameters().getListener());
    GreedyParameters gp = state.getGreedyParameters();
    greedy.setThreshold(gp.getThreshold());
    greedy.setTimeout(gp.getTimeoutInSeconds());
    greedy.setMax(gp.getMaxTests());
    config.setTestEndCondition(gp.getTestEndCondition());
    config.setAlgorithm(gp.getAlgorithm());
    task = new GUIGeneratorTask(this, greedy, gp.getPopulation(), state.getSeed());
    new Thread(task).start();
  }

  private void startExploration() {
    OSMOExplorer explorer = new OSMOExplorer();
    state.setOsmoConfig(explorer.getOSMOConfig());
    Exploration exploration = (Exploration) generator;
    exploration.storeParameters();
    state.openExplorationExecution();
    task = new GUIGeneratorTask(this, explorer, state);
    new Thread(task).start();
  }

  public void taskFinished() {
    task = null;
  }
}

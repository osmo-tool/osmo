package osmo.tester.gui.jfx;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.gui.jfx.configurationtab.generator.GreedyParameters;
import osmo.tester.gui.jfx.testinfo.ChartSettings;
import osmo.tester.model.ModelFactory;
import osmo.tester.optimizer.reducer.ReducerConfig;

/**
 * @author Teemu Kanstren
 */
public class GUIState {
  private ModelFactory factory = null;
  private OSMOConfiguration osmoConfig = new OSMOConfiguration();
  private final GreedyParameters greedyParameters = new GreedyParameters();
  private final ExplorationConfiguration explorationConfig = new ExplorationConfiguration(null, 1, 0);
  private final ScoreConfiguration scoreConfig = new ScoreConfiguration();
  private TextField seedField;
  private final MainWindow mainWindow;
  private ChartSettings chartSettings = new ChartSettings();
  private Stage stage;
  private boolean drawChart = true;
  private boolean onlyFailingTests;
  private ReducerConfig reducerConfig;

  public GUIState(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
  }

  public void setOsmoConfig(OSMOConfiguration osmoConfig) {
    this.osmoConfig = osmoConfig;
    osmoConfig.setFactory(factory);
    explorationConfig.setFactory(factory);
  }

  public OSMOConfiguration getOsmoConfig() {
    return osmoConfig;
  }

  public ExplorationConfiguration getExplorationConfig() {
    return explorationConfig;
  }

  public ScoreConfiguration getScoreConfig() {
    return scoreConfig;
  }

  public void setFactory(ModelFactory factory) {
    this.factory = factory;
  }

  public void setSeedField(TextField seedField) {
    this.seedField = seedField;
  }

  public long getSeed() {
    return Long.parseLong(seedField.getText());
  }

  public void openSingleCoreExecution() {
    mainWindow.openSingleCoreExecution();
  }

  public void openGreedyExecution() {
    mainWindow.openGreedyExecution();
  }

  public void openExplorationExecution() {
    mainWindow.openExplorationExecution();
  }

  public GreedyParameters getGreedyParameters() {
    return greedyParameters;
  }

  public ChartSettings getChartSettings() {
    return chartSettings;
  }

  public void setChartSettings(ChartSettings chartSettings) {
    this.chartSettings = chartSettings;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public Stage getStage() {
    return stage;
  }

  public boolean isDrawChart() {
    return drawChart;
  }

  public void setDrawChart(boolean drawChart) {
    this.drawChart = drawChart;
  }

  public boolean isOnlyFailingTests() {
    return onlyFailingTests;
  }

  public void setOnlyFailingTests(boolean onlyFailingTests) {
    this.onlyFailingTests = onlyFailingTests;
  }

  public ReducerConfig getReducerConfig() {
    reducerConfig = new ReducerConfig(getSeed());
    return reducerConfig;
  }

  public void setReducerConfig(ReducerConfig reducerConfig) {
    this.reducerConfig = reducerConfig;
  }
}

package osmo.tester.gui.jfx;

import javafx.scene.control.TextField;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.gui.jfx.configurationtab.generator.GreedyParameters;
import osmo.tester.gui.jfx.testinfo.ChartSettings;
import osmo.tester.model.ModelFactory;

/**
 * @author Teemu Kanstren
 */
public class GUIState {
  private final OSMOConfiguration osmoConfig = new OSMOConfiguration();
  private final GreedyParameters greedyParameters = new GreedyParameters();
  private final ExplorationConfiguration explorationConfig = new ExplorationConfiguration(null, 1, 0);
  private final ScoreConfiguration scoreConfig = new ScoreConfiguration();
  private TextField seedField;
  private final MainWindow mainWindow;
  private ChartSettings chartSettings = new ChartSettings();

  public GUIState(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
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
    osmoConfig.setFactory(factory);
    explorationConfig.setFactory(factory);
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

  public GreedyParameters getGreedyParameters() {
    return greedyParameters;
  }

  public ChartSettings getChartSettings() {
    return chartSettings;
  }

  public void setChartSettings(ChartSettings chartSettings) {
    this.chartSettings = chartSettings;
  }
}

package osmo.tester.gui.jfx;

import javafx.scene.control.TextField;
import osmo.tester.OSMOConfiguration;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.model.ModelFactory;

/**
 * @author Teemu Kanstren
 */
public class GUIState {
  private final OSMOConfiguration osmoConfig = new OSMOConfiguration();
  private final ExplorationConfiguration explorationConfig = new ExplorationConfiguration(null, 1, 0);
  private TextField seedField;
  private final MainWindow mainWindow;

  public GUIState(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
  }

  public OSMOConfiguration getOsmoConfig() {
    return osmoConfig;
  }

  public ExplorationConfiguration getExplorationConfig() {
    return explorationConfig;
  }

  public void setFactory(ModelFactory factory) {
    osmoConfig.setFactory(factory);
    explorationConfig.setFactory(factory);
  }

  public void setSeedField(TextField seedField) {
    this.seedField = seedField;
  }

  public TextField getSeedField() {
    return seedField;
  }

  public void openSingleCoreExecution() {
    mainWindow.openSingleCoreExecution();
  }

  public void openGreedyExecution() {
    mainWindow.openGreedyExecution();
  }
}

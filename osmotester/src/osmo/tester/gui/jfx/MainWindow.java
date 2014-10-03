package osmo.tester.gui.jfx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import osmo.tester.gui.jfx.configurationtab.ConfigurationTab;
import osmo.tester.gui.jfx.modeltab.ModelTab;
import osmo.tester.gui.jfx.tabs.CreditsTab;
import osmo.tester.gui.jfx.executiontab.ExecutionTab;
import osmo.tester.gui.jfx.tabs.ResultsTab;

/**
 * @author Teemu Kanstren
 */
public class MainWindow extends Application {
  private final GUIState state = new GUIState(this);
  private final ExecutionTab executionTab = new ExecutionTab(state);
  private final TabPane tabPane = new TabPane();
  private final ConfigurationTab configurationTab = new ConfigurationTab(state);

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("OSMO Tester v3.4.0");
    state.setStage(stage);

    Tab tab1 = new ModelTab(state);
    Tab tab4 = new ResultsTab();
    Tab tab5 = new CreditsTab();
    ObservableList<Tab> tabs = tabPane.getTabs();
    tabs.add(tab1);
    tabs.add(configurationTab);
    tabs.add(executionTab);
    tabs.add(tab4);
    tabs.add(tab5);

    Scene scene = new Scene(tabPane, 800, 600);
    scene.getStylesheets().add(ModelTab.class.getResource("modeltab-style.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  public void openSingleCoreExecution() {
    configurationTab.storeGeneralParameters();
    tabPane.getSelectionModel().select(executionTab);
    executionTab.showSingleCore();
  }

  public void openGreedyExecution() {
    configurationTab.storeScoreWeights();
    configurationTab.storeGeneralParameters();
    tabPane.getSelectionModel().select(executionTab);
    executionTab.showGreedy();
  }

  public void openExplorationExecution() {
    configurationTab.storeScoreWeights();
    configurationTab.storeGeneralParameters();
    tabPane.getSelectionModel().select(executionTab);
    executionTab.showSingleCore();
  }
}

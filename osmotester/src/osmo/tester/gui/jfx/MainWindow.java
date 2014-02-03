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
import osmo.tester.gui.jfx.tabs.ExecutionTab;
import osmo.tester.gui.jfx.tabs.ResultsTab;

/**
 * @author Teemu Kanstren
 */
public class MainWindow extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("OSMO Tester v4.0alpha");

    TabPane tabPane = new TabPane();
    Tab tab1 = new ModelTab();
    Tab tab2 = new ConfigurationTab();
    Tab tab3 = new ExecutionTab();
    Tab tab4 = new ResultsTab();
    Tab tab5 = new CreditsTab();
    ObservableList<Tab> tabs = tabPane.getTabs();
    tabs.add(tab1);
    tabs.add(tab2);
    tabs.add(tab3);
    tabs.add(tab4);
    tabs.add(tab5);

    Scene scene = new Scene(tabPane, 800, 600);
    scene.getStylesheets().add(ModelTab.class.getResource("modeltab-style.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }
}

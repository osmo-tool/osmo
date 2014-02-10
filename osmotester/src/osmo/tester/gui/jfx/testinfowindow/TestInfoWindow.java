package osmo.tester.gui.jfx.testinfowindow;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.configurationtab.ConfigurationTab;
import osmo.tester.gui.jfx.executiontab.ExecutionTab;
import osmo.tester.gui.jfx.executiontab.single.TestInfoPane;
import osmo.tester.gui.jfx.modeltab.ModelTab;
import osmo.tester.gui.jfx.tabs.CreditsTab;
import osmo.tester.gui.jfx.tabs.ResultsTab;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TestInfoWindow extends Stage {
  private final GUIState state;
  private final TestInfoPane tiPane;

  public TestInfoWindow(GUIState state) {
    this.state = state;
    setTitle("Test Information");
    initModality(Modality.WINDOW_MODAL);
    tiPane = new TestInfoPane(state);
    setScene(new Scene(tiPane));
  }
  
  public void addTests(List<TestCase> tests) {
    for (TestCase test : tests) {
      tiPane.addTest(test);
    }
  }
}

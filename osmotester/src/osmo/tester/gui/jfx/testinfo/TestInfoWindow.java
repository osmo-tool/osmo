package osmo.tester.gui.jfx.testinfo;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;

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
    initModality(Modality.APPLICATION_MODAL);
    tiPane = new TestInfoPane(state);
    ReducerPane reducer = new ReducerPane();
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.add(tiPane, 0, 0);
    grid.add(reducer, 1, 0);
    setScene(new Scene(grid));
  }
  
  public void addTests(List<TestCase> tests) {
    for (TestCase test : tests) {
      tiPane.addTest(test);
    }
  }
}

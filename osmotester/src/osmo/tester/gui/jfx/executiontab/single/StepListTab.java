package osmo.tester.gui.jfx.executiontab.single;

import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.executiontab.TestDescription;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class StepListTab extends Tab {
  private final ListView<String> steps = new ListView<>();

  public StepListTab() {
    super("Steps");
    setClosable(false);
    steps.setPrefHeight(150);
    setContent(steps);
  }

  public void update(TestDescription selected) {
    TestCase test = selected.getTest();
    List<String> steps = test.getAllStepNames();
    this.steps.getItems().setAll(steps);
  }
}

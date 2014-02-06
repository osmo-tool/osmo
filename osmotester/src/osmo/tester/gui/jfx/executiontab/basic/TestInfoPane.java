package osmo.tester.gui.jfx.executiontab.basic;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.TestDescription;

/**
 * @author Teemu Kanstren
 */
public class TestInfoPane extends VBox {
  private final GUIState state;
  private final StepListTab stepListTab;
  private final MetricsTab metricsTab;
  private TestDescription selected = null;
  private final ListView<TestDescription> tests = new ListView<>();
  private final TabPane tabs = new TabPane();

  public TestInfoPane(GUIState state) {
    super(10);
    this.state = state;

    ReadOnlyObjectProperty<TestDescription> selectedProperty = tests.getSelectionModel().selectedItemProperty();
    selectedProperty.addListener((observable, oldValue, newValue) -> choice(newValue));
    tests.setPrefHeight(150);
    
    ObservableList<Node> children = getChildren();
    children.add(new Label("Generated Tests"));
    children.add(tests);
    children.add(new Label("Test Info"));
    children.add(tabs);
    stepListTab = new StepListTab();
    metricsTab = new MetricsTab();
    tabs.getTabs().add(stepListTab);
    tabs.getTabs().add(metricsTab);
  }

  private void choice(TestDescription newValue) {
    selected = newValue;
    stepListTab.update(selected);
    metricsTab.update(selected);
  }

  public void addTest(TestCase test) {
    tests.getItems().add(new TestDescription(test));
  }
}

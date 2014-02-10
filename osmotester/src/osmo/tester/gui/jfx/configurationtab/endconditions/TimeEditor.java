package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorDescription;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class TimeEditor extends GridPane {
  public TimeEditor(GeneratorDescription parent, Stage stage, TimeDescription desc) {
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));

    Label labelDelay = new Label("Delay:");
    TextField fieldDelay = new TextField();
    fieldDelay.setPrefColumnCount(5);
    add(labelDelay, 0, 0);
    add(fieldDelay, 1, 0);

    ComboBox<TimeUnit> unitBox = new ComboBox<>();
    ObservableList<TimeUnit> items = unitBox.getItems();
    items.addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    unitBox.setValue(desc.getEndCondition().getTimeUnit());
    fieldDelay.setText(""+desc.getEndCondition().getDelay());
    add(new Label("TimeUnit"), 0, 1);
    add(unitBox, 1, 1);

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    add(box, 0, 2, 4, 1);
    Button save = new Button("Save");
    save.setOnAction(event -> {
      long delay = Long.parseLong(fieldDelay.getText());
      TimeUnit unit = unitBox.getValue();
      desc.setTime(new Time(delay, unit));
      parent.createTestECPane();
      parent.createSuiteECPane();
      stage.close();
    });
    Button cancel = new Button("Cancel");
    cancel.setOnAction(event -> {
      stage.close();
    });
    ObservableList<Node> kids = box.getChildren();
    kids.add(save);
    kids.add(cancel);
  }
}

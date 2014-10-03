package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorDescription;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public class LengthEditor extends GridPane {
  public LengthEditor(GeneratorDescription parent, Stage stage, LengthDescription desc) {
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));
    Text label = new Text("Length:");
    TextField field = new TextField("" + desc.getEndCondition().getLength());
    field.setPrefColumnCount(5);
    add(label, 0, 0);
    add(field, 1, 0);

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    add(box, 0, 1, 2, 1);
    Button save = new Button("Save");
    save.setOnAction(event -> {
      String value = field.getText();
      desc.setLength(new Length(Integer.parseInt(value)));
      stage.close();
      parent.createTestECPane();
      parent.createSuiteECPane();
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

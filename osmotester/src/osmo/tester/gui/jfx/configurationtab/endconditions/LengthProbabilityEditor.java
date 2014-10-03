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
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorDescription;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public class LengthProbabilityEditor extends GridPane {
  public LengthProbabilityEditor(GeneratorDescription parent, Stage stage, LengthProbabilityDescription desc) {
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));

    Text labelMin = new Text("Min:");
    TextField fieldMin = new TextField("" + desc.getEndCondition().getMin());
    fieldMin.setPrefColumnCount(5);
    add(labelMin, 0, 0);
    add(fieldMin, 1, 0);

    Text labelMax = new Text("Max:");
    TextField fieldMax = new TextField("" + desc.getEndCondition().getMax());
    fieldMax.setPrefColumnCount(5);
    add(labelMax, 2, 0);
    add(fieldMax, 3, 0);

    HBox pbox = new HBox(10);
    pbox.setAlignment(Pos.CENTER_LEFT);
    add(pbox, 0, 1, 4, 1);

    Text labelP = new Text("Probability:");
    TextField fieldP = new TextField(""+desc.getEndCondition().getProbability());
    fieldP.setPrefColumnCount(5);
    ObservableList<Node> pkids = pbox.getChildren();
    pkids.add(labelP);
    pkids.add(fieldP);
//    add(labelP, 0, 1, 2, 1);
//    add(fieldP, 2, 1, 2, 1);

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    add(box, 0, 2, 4, 1);
    Button save = new Button("Save");
    save.setOnAction(event -> {
      int min = Integer.parseInt(fieldMin.getText());
      int max = Integer.parseInt(fieldMax.getText());
      double probability = Double.parseDouble(fieldP.getText());
      desc.setLengthProbability(new LengthProbability(min, max, probability));
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

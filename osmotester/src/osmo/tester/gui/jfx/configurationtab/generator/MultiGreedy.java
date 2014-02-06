package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class MultiGreedy extends Greedy {
  private static final int parallelismY = 3;

  public MultiGreedy() {
    super();
  }

  @Override
  public String toString() {
    return "Multi Greedy";
  }
  
  @Override
  public Pane createPane() {
    createTimeOut();
    createThreshold();
    createMax();
    createParallelism();
    return grid;
  }

  private void createParallelism() {
    Label label = new Label("Parallelism:");
    HBox hbox = new HBox(10);
    hbox.setAlignment(Pos.CENTER_LEFT);
    TextField field = new TextField();
    hbox.getChildren().add(field);
    hbox.getChildren().add(new CheckBox("System"));
    field.setPrefColumnCount(2);
    grid.add(label, 0, parallelismY);
    grid.add(hbox, 1, parallelismY, 2, 1);
  }
}

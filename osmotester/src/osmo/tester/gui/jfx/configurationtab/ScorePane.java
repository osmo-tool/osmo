package osmo.tester.gui.jfx.configurationtab;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * @author Teemu Kanstren
 */
public class ScorePane extends GridPane {
  public ScorePane() {
    setHgap(10);
    setVgap(10);
    add(new Label("Coverage Score Weights"), 0, 0, 3, 1);
    int y = 1;
    int x = 0;
    createLabeledTextField("Length:", "Test length weight", x, y++);
    createLabeledTextField("Variable count:", "Variable count weight", x, y++);
    createLabeledTextField("Default for values:", "Default value weight", x, y++);
    createLabeledTextField("Steps:", "Step weight", x, y++);
    createLabeledTextField("Step pairs:", "Step pair weight", x, y++);
    createLabeledTextField("Requirements:", "Requirement weight", x, y++);
    createLabeledTextField("States:", "State weight", x, y++);
    createLabeledTextField("State pairs:", "State pair weight", x, y++);
    //TODO: read user model and add options for all variables
  }

  private void createLabeledTextField(String labelText, String promptText, int x, int y) {
    Label label = new Label(labelText);
    add(label, x, y);
    TextField field = new TextField();
    field.setPromptText(promptText);
    add(field, x + 1, y);
  }
}

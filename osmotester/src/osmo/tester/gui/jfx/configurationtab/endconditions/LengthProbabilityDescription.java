package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public class LengthProbabilityDescription implements ECDescription {
  private LengthProbability lengthProbability = new LengthProbability(10, 0.2);

  public void setLengthProbability(LengthProbability lengthProbability) {
    this.lengthProbability = lengthProbability;
  }

  @Override
  public LengthProbability getEndCondition() {
    return lengthProbability;
  }

  @Override
  public Pane createEditor(SingleCore parent, Stage stage) {
    return new LengthProbabilityEditor(parent, stage, this);
  }

  @Override
  public boolean supportsEditing() {
    return true;
  }

  @Override
  public String toString() {
    int min = lengthProbability.getMin();
    int max = lengthProbability.getMax();
    double probability = lengthProbability.getProbability();
    String desc = ""+min;
    if (max > 0) desc+="-"+max;
    desc += ", "+probability;
    return "Length&Probability("+desc+")";
  }
}

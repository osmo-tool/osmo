package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.GeneratorDescription;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public class ProbabilityDescription implements ECDescription {
  private Probability probability = new Probability(0.2);

  public void setProbability(Probability probability) {
    this.probability = probability;
  }

  @Override
  public Probability getEndCondition() {
    return probability;
  }

  @Override
  public Pane createEditor(GeneratorDescription parent, Stage stage) {
    return new ProbabilityEditor(parent, stage, this);
  }

  @Override
  public boolean supportsEditing() {
    return true;
  }

  @Override
  public String toString() {
    return "Probability("+probability.getThreshold()+")";
  }
}

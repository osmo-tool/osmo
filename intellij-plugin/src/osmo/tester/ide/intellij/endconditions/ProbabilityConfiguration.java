package osmo.tester.ide.intellij.endconditions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Probability;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class ProbabilityConfiguration implements EndConditionConfiguration {
  private Double threshold = 0.2d;
  public static final String KEY_THRESHOLD = "threshold";
  
  public EndCondition createEndCondition() {
    return new Probability(threshold);
  }

  @Override
  public String getType() {
    return Probability.class.getName();
  }

  @Override
  public void fillFrom(EndCondition endCondition) {
    Probability p = (Probability) endCondition;
    this.threshold = p.getThreshold();
  }

  public void showGUI(Project project) {
    String value = "";
    if (threshold != null) value = threshold.toString();
    String msg = "Give probability to stop (0-1)";
    String title = "Probability Configuration";
    Icon icon = Messages.getQuestionIcon();
    String newValue = Messages.showInputDialog(project, msg, title, icon, value, new ProbabilityValidator());
    if (newValue != null) {
      //null means user pressed cancel
      threshold = Double.parseDouble(newValue);
    }
  }

  @Override
  public Map<String, String> getMap() {
    HashMap<String, String> map = new HashMap<>();
    map.put(KEY_THRESHOLD, ""+threshold);
    return map;
  }

  @Override
  public void setMap(Map<String, String> map) {
    threshold = Double.parseDouble(map.get(KEY_THRESHOLD));
  }
}

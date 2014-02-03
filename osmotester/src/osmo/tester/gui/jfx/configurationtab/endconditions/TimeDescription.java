package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.gui.jfx.configurationtab.BasicsPane;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class TimeDescription implements ECDescription {
  private Time time = new Time(1, TimeUnit.HOURS);

  public void setTime(Time time) {
    this.time = time;
  }

  @Override
  public Time getEndCondition() {
    return time;
  }

  @Override
  public Pane createEditor(BasicsPane parent, Stage stage) {
    return new TimeEditor(parent, stage, this);
  }

  @Override
  public boolean supportsEditing() {
    return true;
  }

  @Override
  public String toString() {
    return "Time("+time.getDelay()+", "+time.getTimeUnit()+")";
  }
}

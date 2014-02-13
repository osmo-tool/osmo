package osmo.tester.gui.jfx.executiontab.single;

import javafx.scene.control.Tab;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.TestDescription;

/**
 * @author Teemu Kanstren
 */
public class MetricsTab extends Tab {
  private final MetricsPane metrics;

  public MetricsTab(GUIState state) {
    super("Metrics");
    this.metrics = new MetricsPane(state, false, false);
    setClosable(false);
    setContent(metrics);
  }

  public void update(TestDescription selected) {
    metrics.setCoverage(selected.getTest().getCoverage());
    metrics.refresh();
  }
}

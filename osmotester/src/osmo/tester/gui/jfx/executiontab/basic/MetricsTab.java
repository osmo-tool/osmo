package osmo.tester.gui.jfx.executiontab.basic;

import javafx.scene.control.Tab;
import osmo.tester.gui.jfx.executiontab.TestDescription;

/**
 * @author Teemu Kanstren
 */
public class MetricsTab extends Tab {
  private final MetricsPane metrics = new MetricsPane(false, false);

  public MetricsTab() {
    super("Metrics");
    setClosable(false);
    setContent(metrics);
  }

  public void update(TestDescription selected) {
    metrics.setCoverage(selected.getTest().getCoverage());
    metrics.refresh();
  }
}

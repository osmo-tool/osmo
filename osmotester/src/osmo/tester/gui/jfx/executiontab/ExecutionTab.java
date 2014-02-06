package osmo.tester.gui.jfx.executiontab;

import javafx.scene.control.Tab;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.basic.BasicExecutorPane;
import osmo.tester.gui.jfx.executiontab.greedy.GreedyInfoPane;

/**
 * @author Teemu Kanstren
 */
public class ExecutionTab extends Tab {
  private final GUIState state;
  
  public ExecutionTab(GUIState state) {
    super("Execution");
    this.state = state;
    setClosable(false);
  }
  
  public void showSingleCore() {
    BasicExecutorPane single = new BasicExecutorPane(state);
    setContent(single);
    state.getOsmoConfig().addListener(new CoverageListener(single));
  }
  
  public void showGreedy() {
    GreedyInfoPane greedy = new GreedyInfoPane(state);
    setContent(greedy);
  }
}

package osmo.tester.gui.jfx.executiontab;

import javafx.scene.control.Tab;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.single.BasicExecutorPane;
import osmo.tester.gui.jfx.executiontab.greedy.GreedyInfoPane;
import osmo.tester.gui.jfx.executiontab.greedy.IterationInfoListener;

import java.util.Collection;
import java.util.Iterator;

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
    OSMOConfiguration osmoConfig = state.getOsmoConfig();
    Collection<GenerationListener> listeners = osmoConfig.getListeners().getListeners();
    //if we get here many times as result of several runs in same session, we need to throw away the old stuff
    for (Iterator<GenerationListener> i = listeners.iterator() ; i.hasNext() ; ) {
      GenerationListener listener = i.next();
      if (listener instanceof CoverageListener) i.remove();
    }
    osmoConfig.addListener(new CoverageListener(single));
  }
  
  public void showGreedy() {
    GreedyInfoPane greedy = new GreedyInfoPane(state);
    setContent(greedy);
    state.getGreedyParameters().setListener(new IterationInfoListener(greedy));
  }
}

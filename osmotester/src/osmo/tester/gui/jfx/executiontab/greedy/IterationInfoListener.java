package osmo.tester.gui.jfx.executiontab.greedy;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.greedy.IterationListener;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class IterationInfoListener implements IterationListener {
  private final GreedyInfoPane info;

  public IterationInfoListener(GreedyInfoPane info) {
    this.info = info;
  }

  @Override
  public void iterationDone(List<TestCase> tests) {
    info.addIteration(tests);
  }

  @Override
  public void generationDone(List<TestCase> tests) {
    info.finished(tests);
  }
}

package osmo.tester.gui.jfx.configurationtab.generator;

import osmo.tester.generator.testsuite.TestCase;

import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public interface GeneratorTaskListener {
  public void taskFinished(List<TestCase> tests);
}

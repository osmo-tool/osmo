package osmo.visualizer.generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class TransitionsPieChart implements GenerationListener {
  private DefaultPieDataset data = new DefaultPieDataset();
  private Map<String, Integer> values = new HashMap<>();

  public void show() {
    JFrame frame = new JFrame("Bar Chart");
    JFreeChart chart = createChart("Transitions");
    ChartPanel chartPanel = new ChartPanel(chart);
    frame.setSize(new Dimension(500, 270));
    frame.setContentPane(chartPanel);
    frame.setVisible(true);
  }

  private JFreeChart createChart(String title) {
    JFreeChart chart = ChartFactory.createPieChart3D(title, data, true, true, false);
    PiePlot3D plot = (PiePlot3D) chart.getPlot();
    plot.setStartAngle(290);
    plot.setDirection(Rotation.CLOCKWISE);
    plot.setForegroundAlpha(0.5f);
    return chart;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition t : transitions) {
      values.put(t.getStringName(), 0);
    }
    updateData();
    show();
  }

  private void updateData() {
    for (String name : values.keySet()) {
      data.setValue(name, values.get(name));
    }
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void stepStarting(TestCaseStep step) {

  }

  @Override
  public void stepDone(TestCaseStep step) {
    String name = step.getName();
    int count = values.get(name);
    values.put(name, ++count);
    updateData();
  }

  @Override
  public void lastStep(String name) {
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
  }

  @Override
  public void testEnded(TestCase test) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }

  @Override
  public void testError(TestCase test, Throwable error) {
  }
}

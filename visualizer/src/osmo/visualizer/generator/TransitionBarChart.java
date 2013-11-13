package osmo.visualizer.generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.coverage.TestCoverage;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Map;

/** @author Teemu Kanstren */
public class TransitionBarChart implements GenerationListener {
  private DefaultCategoryDataset data = new DefaultCategoryDataset();
  private int nextId = 1;

  public void show() {
    JFrame frame = new JFrame("Bar Chart");
    JFreeChart chart = createChart("Transitions");
    ChartPanel chartPanel = new ChartPanel(chart);
    frame.setSize(new Dimension(500, 270));
    frame.setContentPane(chartPanel);
    frame.setVisible(true);
  }

  private JFreeChart createChart(String title) {
    JFreeChart chart = ChartFactory.createBarChart(title, "test", "transitions", data, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    return chart;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    show();
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestCaseStep step) {
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
    TestCoverage tc = new TestCoverage(test);
    String name = "Test"+nextId++;
    data.setValue(tc.getSteps().size(), "Transitions", name);
    data.setValue(tc.getStepPairs().size(), "Pairs", name);
    data.setValue(tc.getRequirements().size(), "Requirements", name);
    data.setValue(tc.getSingles().size(), "Singles", name);
    Map<String,Collection<String>> variables = tc.getVariableValues();
    data.setValue(variables.size(), "Variables", name);
    int values = 0;
    for (Collection<String> variable : variables.values()) {
      values += variable.size();
    }
    data.setValue(values, "Values", name);
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

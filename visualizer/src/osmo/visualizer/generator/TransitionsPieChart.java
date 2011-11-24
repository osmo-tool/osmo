package osmo.visualizer.generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.GenerationListener;
import osmo.tester.manualdrive.ManualAlgorithm;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class TransitionsPieChart implements GenerationListener {
  private DefaultPieDataset data = new DefaultPieDataset();
  private Map<String, Integer> values = new HashMap<String, Integer>();

  public static void main(String[] args) {
    TransitionsPieChart transitionsBarChart = new TransitionsPieChart();
    OSMOTester tester = new OSMOTester();
    tester.addTestEndCondition(new Endless());
    tester.addListener(transitionsBarChart);
//    tester.addListener(new TransitionsPieChart());
    tester.addModelObject(new CalculatorModel());
    tester.setAlgorithm(new ManualAlgorithm());
    tester.generate();
  }

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
  public void init(FSM fsm) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition t : transitions) {
      values.put(t.getName(), 0);
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
  public void transition(FSMTransition transition) {
    String name = transition.getName();
    int count = values.get(name);
    values.put(name, ++count);
    updateData();
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
}

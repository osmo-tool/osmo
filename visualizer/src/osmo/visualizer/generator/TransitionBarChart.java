package osmo.visualizer.generator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.GenerationListener;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ManualEndCondition;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.TestCoverage;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.Map;

/** @author Teemu Kanstren */
public class TransitionBarChart implements GenerationListener {
  private DefaultCategoryDataset data = new DefaultCategoryDataset();
  private int nextId = 1;

  public static void main(String[] args) {
    TransitionBarChart barGraph = new TransitionBarChart();
    OSMOTester tester = new OSMOTester();
    ManualEndCondition mec = new ManualEndCondition();
    tester.addTestEndCondition(mec);
    tester.addSuiteEndCondition(mec);
    tester.addListener(barGraph);
//    tester.addModelObject(new CalculatorModel());
    ModelState state = new ModelState();
    MockScripter scripter = new MockScripter();
//    PrintStream out = new OfflineScripter("tbc.html");
    PrintStream out = System.out;
//    PrintStream out = NullPrintStream.stream;
    tester.addModelObject(state);
    tester.addModelObject(new CalendarBaseModel(state, scripter, out));
    tester.addModelObject(new CalendarBaseModel(state, scripter, out));
    tester.addModelObject(new CalendarOracleModel(state, scripter, out));
    tester.addModelObject(new CalendarTaskModel(state, scripter, out));
    tester.addModelObject(new CalendarOverlappingModel(state, scripter, out));
    tester.addModelObject(new CalendarParticipantModel(state, scripter, out));
    tester.addModelObject(new CalendarErrorHandlingModel(state, scripter, out));
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
    JFreeChart chart = ChartFactory.createBarChart(title, "test", "transitions", data, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    return chart;
  }

  @Override
  public void init(FSM fsm) {
    show();
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void transition(FSMTransition transition) {
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
    data.setValue(tc.getTransitions().size(), "Transitions", name);
    data.setValue(tc.getPairs().size(), "Pairs", name);
    data.setValue(tc.getRequirements().size(), "Requirements", name);
    data.setValue(tc.getSingles().size(), "Singles", name);
    Map<String,ModelVariable> variables = tc.getVariables();
    data.setValue(variables.size(), "Variables", name);
    int values = 0;
    for (ModelVariable variable : variables.values()) {
      values += variable.getValues().size();
    }
    data.setValue(values, "Values", name);
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }
}

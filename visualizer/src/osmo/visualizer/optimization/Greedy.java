package osmo.visualizer.optimization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.Candidate;
import osmo.tester.optimizer.SearchConfiguration;
import osmo.tester.optimizer.TestCoverage;
import osmo.tester.optimizer.offline.GreedyOptimizer;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class Greedy {
  private DefaultCategoryDataset data = new DefaultCategoryDataset();
  private int nextId = 1;

  public static void main(String[] args) {
    Greedy barGraph = new Greedy();
    OSMOTester osmo = new OSMOTester();
    osmo.setSeed(333);
//    tester.addModelObject(new CalculatorModel());
    ModelState state = new ModelState();
    MockScripter scripter = new MockScripter();
//    PrintStream out = new OfflineScripter("tbc.html");
//    PrintStream out = System.out;
    PrintStream out = NullPrintStream.stream;
    osmo.addModelObject(state);
    osmo.addModelObject(new CalendarBaseModel(state, scripter, out));
    osmo.addModelObject(new CalendarOracleModel(state, scripter, out));
    osmo.addModelObject(new CalendarTaskModel(state, scripter, out));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter, out));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter, out));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter, out));
//    osmo.setAlgorithm(new ManualAlgorithm());
    osmo.setAlgorithm(new RandomAlgorithm());
    MainGenerator generator = osmo.initGenerator();
    SearchConfiguration sc = new SearchConfiguration(generator);
    sc.setLengthWeight(-20);
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
//    EndCondition length15 = new Length(15);
//    osmo.addTestEndCondition(length15);
    generator.initSuite();
    for (int i = 0 ; i < 50 ; i++) {
      barGraph.addTest(generator.nextTest());
    }
    barGraph.show();

//    if (true) return;
//    Candidate candidate = optimizer.search();
//    List<TestCase> tests = candidate.getTests();
//    for (TestCase test : tests) {
//      barGraph.addTest(test);
//    }
//    barGraph.show();
  }

  public void show() {
    JFrame frame = new JFrame("Bar Chart");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JFreeChart chart = createChart("Tests");
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

  public void addTest(TestCase test) {
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
}

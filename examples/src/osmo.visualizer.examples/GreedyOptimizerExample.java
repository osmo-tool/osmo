package osmo.visualizer.examples;

import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.testmodel.*;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.optimizer.SearchConfiguration;
import osmo.tester.optimizer.offline.GreedyOptimizer;
import osmo.visualizer.optimization.Greedy;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class GreedyOptimizerExample {
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
    osmo.addModelObject(new CalendarMeetingModel(state, scripter, out));
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
    for (int i = 0; i < 50; i++) {
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
}

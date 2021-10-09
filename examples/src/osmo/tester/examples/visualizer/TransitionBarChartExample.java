package osmo.tester.examples.visualizer;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarMeetingModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.visualizer.generator.TransitionBarChart;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class TransitionBarChartExample {
  public static void main(String[] args) {
    TransitionBarChart barGraph = new TransitionBarChart();
    OSMOTester tester = new OSMOTester();
//    ManualEndCondition mec = new ManualEndCondition();
    tester.setTestEndCondition(new Length(10));
    tester.setSuiteEndCondition(new Length(10));
    tester.addListener(barGraph);
//    tester.addModelObject(new CalculatorModel());
    ModelState state = new ModelState();
    MockScripter scripter = new MockScripter();
//    PrintStream out = new OfflineScripter("tbc.html");
    PrintStream out = System.out;
//    PrintStream out = NullPrintStream.stream;

    tester.addModelObject(state);
    tester.addModelObject(new CalendarMeetingModel(state, scripter, out));
    tester.addModelObject(new CalendarOracleModel(state, scripter, out));
    tester.addModelObject(new CalendarTaskModel(state, scripter, out));
    tester.addModelObject(new CalendarOverlappingModel(state, scripter, out));
    tester.addModelObject(new CalendarParticipantModel(state, scripter, out));
    tester.addModelObject(new CalendarErrorHandlingModel(state, scripter, out));
    tester.setAlgorithm(new ManualAlgorithm(tester));
    tester.generate(55);
  }
}

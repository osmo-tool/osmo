package osmo.visualizer.custom;

import osmo.common.NullPrintStream;
import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.Candidate;
import osmo.tester.optimizer.SearchConfiguration;
import osmo.tester.optimizer.offline.GreedyOptimizer;
import osmo.tester.optimizer.online.PeakEndCondition;
import osmo.tester.optimizer.online.SearchListener;
import osmo.tester.optimizer.online.SearchingOptimizer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class Experiments {
  private long start = 0;

  public static void main(String[] args) {
    for (int i = 0; i < 1; i++) {
      new Experiments().run();
    }
  }

  public void run() {
//    Logger.debug = true;
    TestUtils.setSeed(112);
    OSMOTester osmo = new OSMOTester();
    ModelState state = new ModelState();
    CalendarScripter scripter = new MockScripter();
    PrintStream nps = NullPrintStream.stream;
    osmo.addModelObject(new CalendarBaseModel(state, scripter, nps));
    osmo.addModelObject(new CalendarOracleModel(state, scripter, nps));
    osmo.addModelObject(new CalendarTaskModel(state, scripter, nps));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter, nps));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter, nps));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter, nps));
    osmo.addModelObject(new CalendarFailureModel(state, scripter, nps));
    osmo.addModelObject(state);
//    osmo.addModelObject(new ValidTestModel2(new Requirements()));
    MainGenerator generator = osmo.initGenerator();
    SearchConfiguration config = new SearchConfiguration(generator);
    config.setSeed(111);
    config.setNumberOfCandidates(100);
    config.setPopulationSize(50);
    config.setEndCondition(new PeakEndCondition(2500));
    SearchingOptimizer optimizer = new SearchingOptimizer(config);

    List<TestCase> sortedTests = greedySort(config);
    System.out.println("tests:" + sortedTests.size());

//    optimizer.searchFromTests(sortedTests);

//    greedy(config);

//    if (true) return;

    FitnessListener listener = new FitnessListener();
    optimizer.getState().setListener(listener);

    start = System.currentTimeMillis();
    Candidate solution = optimizer.searchFromTests(sortedTests);
//    Candidate solution = optimizer.search();

    System.out.println("generated tests:" + generator.getTestCount());
    System.out.println("totaltime:" + seconds());
    System.out.println("updates:\n" + listener.getUpdates());
    System.out.println("tests:\n" + solution.matrix());
  }

  private List<TestCase> greedySort(SearchConfiguration config) {
    GreedyOptimizer greedy = new GreedyOptimizer(config);
    List<TestCase> sortedTestSet = greedy.createSortedTestSet(config.getPopulationSize() * config.getNumberOfCandidates());
    List<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0; i < config.getPopulationSize(); i++) {
      tests.add(sortedTestSet.get(i));
    }
    Candidate candidate = new Candidate(config, tests);
    int fitness = candidate.getFitness();
    System.out.println("gf:" + fitness);
    return sortedTestSet;
  }

  private void greedy(SearchConfiguration config) {
    GreedyOptimizer greedy = new GreedyOptimizer(config);
    Candidate best = greedy.search();
//    List<TestCase> tests = greedy.createSortedTestSet(config.getPopulationSize());
    System.out.println("greedy:" + best.getFitness() + "\n" + best.matrix());
  }

  private long seconds() {
    long now = System.currentTimeMillis();
    long diff = now - start;
    long seconds = diff / 1000;
    return seconds;
  }

  private class FitnessListener implements SearchListener {
    private Collection<String> updates = new ArrayList<String>();

    @Override
    public void updateBest(Candidate candidate) {
      updates.add(seconds() + "s, " + candidate.getFitness() + ",");
    }

    public Collection<String> getUpdates() {
      return updates;
    }
  }
}

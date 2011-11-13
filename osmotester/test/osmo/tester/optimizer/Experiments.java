package osmo.tester.optimizer;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.MockScripter;
import osmo.tester.examples.calendar.scripter.online.OnlineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generation.NullPrintStream;
import osmo.tester.generator.MainGenerator;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.PeakEndCondition;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchListener;
import osmo.tester.optimizer.online.SearchingOptimizer;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.VariableModel2;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/** @author Teemu Kanstren */
public class Experiments {
  private long start = 0;

  public static void main(String[] args) {
    for (int i = 0 ; i < 1 ; i++) {
      new Experiments().run();
    }
  }

  public void run() {
//    Logger.debug = true;
    TestUtils.setRandom(new Random(112));
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
    config.setNumberOfCandidates(100);
    config.setEndCondition(new PeakEndCondition(250));
    SearchingOptimizer optimizer = new SearchingOptimizer(config);

    FitnessListener listener = new FitnessListener();
    optimizer.getState().setListener(listener);

    start = System.currentTimeMillis();
    Candidate solution = optimizer.search();

    System.out.println("totaltime:"+seconds());
    System.out.println("updates:\n"+listener.getUpdates());
    System.out.println("tests:\n"+solution.matrix());
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
      updates.add(seconds()+"s, "+candidate.getFitness()+",");
    }

    public Collection<String> getUpdates() {
      return updates;
    }
  }
}

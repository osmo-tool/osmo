package osmo.tester.examples.dsm;

import osmo.tester.dsm.ModelObjectFactory;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.scripter.online.OnlineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestModelFactory implements ModelObjectFactory {
  @Override
  public Collection<Object> createModelObjects() {
    Collection<Object> objects = new ArrayList<Object>();
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter("tests.html");
    objects.add(state);
    objects.add(new CalendarBaseModel(state, scripter));
    objects.add(new CalendarOracleModel(state, scripter));
    objects.add(new CalendarTaskModel(state, scripter));
    objects.add(new CalendarOverlappingModel(state, scripter));
    objects.add(new CalendarParticipantModel(state, scripter));
    objects.add(new CalendarErrorHandlingModel(state, scripter));
    objects.add(new CalendarFailureModel(state, scripter));
    return objects;
  }

  @Override
  public Collection<EndCondition> createTestEndConditions() {
    Collection<EndCondition> end = new ArrayList<EndCondition>();
    end.add(new Probability(0.05));
    return end;
  }

  @Override
  public Collection<EndCondition> createSuiteEndConditions() {
    Collection<EndCondition> end = new ArrayList<EndCondition>();
    end.add(new Length(3));
    return end;
  }

  public static void main(String[] args) {
    System.out.println("time:"+System.currentTimeMillis());
  }
}

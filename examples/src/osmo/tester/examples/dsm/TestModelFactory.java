package osmo.tester.examples.dsm;

import osmo.tester.scripting.dsm.ModelObjectFactory;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/** @author Teemu Kanstren */
public class TestModelFactory implements ModelObjectFactory {
  @Override
  public Collection<Object> createModelObjects() {
    Collection<Object> objects = new ArrayList<>();
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter("tests.html");
    objects.add(state);
    objects.add(new CalendarBaseModel(state, scripter));
//    objects.add(new CalendarOracleModel(state, scripter));
    objects.add(new CalendarTaskModel(state, scripter));
//    objects.add(new CalendarOverlappingModel(state, scripter));
    objects.add(new CalendarParticipantModel(state, scripter));
    objects.add(new CalendarErrorHandlingModel(state, scripter));
    objects.add(new CalendarFailureModel(state, scripter));
    return objects;
  }

  @Override
  public Collection<EndCondition> createTestEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Probability(0.05));
    return end;
  }

  @Override
  public Collection<EndCondition> createSuiteEndConditions() {
    Collection<EndCondition> end = new ArrayList<>();
    end.add(new Length(3));
    return end;
  }

  public static void main(String[] args) {
    System.out.println("time:"+System.currentTimeMillis());
    Date date = new Date(1321959683153l);
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);
    System.out.println("date:"+df.format(date));
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, 0, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, 11, 31, 23, 59, 59);
    System.out.println("start:"+start.getTimeInMillis());
    System.out.println("end:"+end.getTimeInMillis());
  }
}

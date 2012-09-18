package osmo.tester.examples.slicing;

import osmo.tester.OSMOConfiguration;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.*;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.scripting.OSMOConfigurationFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** @author Teemu Kanstren */
public class TestModelFactory implements OSMOConfigurationFactory {
  @Override
  public OSMOConfiguration createConfiguration() {
    OSMOConfiguration config = new OSMOConfiguration();
    config.addTestEndCondition(new Probability(0.05));
    config.addSuiteEndCondition(new Length(3));
    ModelState state = new ModelState();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    config.addModelObject(state);
    config.addModelObject(new CalendarMeetingModel(state, scripter));
    config.addModelObject(new CalendarTaskModel(state, scripter));
    config.addModelObject(new CalendarParticipantModel(state, scripter));
    config.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    config.addModelObject(new CalendarFailureModel(state, scripter));
    return config;
  }

  public static void main(String[] args) {
    System.out.println("time:" + System.currentTimeMillis());
    Date date = new Date(1321959683153l);
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);
    System.out.println("date:" + df.format(date));
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, 0, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, 11, 31, 23, 59, 59);
    System.out.println("start:" + start.getTimeInMillis());
    System.out.println("end:" + end.getTimeInMillis());
  }
}

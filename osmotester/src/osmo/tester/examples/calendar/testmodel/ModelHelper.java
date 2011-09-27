package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

import static osmo.common.TestUtils.cLong;

/**
 * Class to hold static helper methods for the model objects.
 *
 * @author Teemu Kanstren
 */
public class ModelHelper {
  public static Date calculateEndTime(Date start) {
    //1000 = second, 60=minute, 60=hour, 4=four hours max
    long max = 1000 * 60 * 60 * 4;
    //min 1 second, max 4 hours
    return new Date(start.getTime() + cLong(1000, max));
  }

}

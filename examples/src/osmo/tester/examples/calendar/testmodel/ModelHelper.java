package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

import static osmo.common.TestUtils.*;

/**
 * Class to hold static helper methods for the model objects.
 *
 * @author Teemu Kanstren
 */
public class ModelHelper {
  /**
   * Calculates a random end time for a given event start time. This time is between 1 second to 4 hours from start.
   *
   * @param start The start time of the event for which the end time is needed.
   * @return The calculated end time.
   */
  public static Date calculateEndTime(Date start) {
    //1000 = second, 60=minute, 60=hour, 4=four hours max
    long max = 1000 * 60 * 60 * 4;
    //min 1 second, max 4 hours
    return new Date(start.getTime() + cLong(1000, max));
  }

}

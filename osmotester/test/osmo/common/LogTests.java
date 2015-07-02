package osmo.common;

import org.junit.Test;
import osmo.common.log.Logger;

import static org.junit.Assert.assertEquals;

/**
 * @author Teemu Kanstren.
 */
public class LogTests {
  @Test
  public void roll() {
    Logger log = new Logger(LogTests.class);
    log.d("hello");
    String data = TestUtils.readFile("test0.log.0", "UTF8");
    assertEquals("Logged data", "", data);
  }
}

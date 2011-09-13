package osmo.common.log;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handles writing the log to an actual output stream.
 * Currently only System.out but others can also be easily supported.
 * Part of the required components for the JDK logging service.
 * 
 * @author Teemu Kanstren
 */
public class LogHandler extends Handler {
  /** This is where the log messages are written. */
  private PrintStream out = System.out;
  /** For formatting the log statements. */
  private LogFormatter formatter = new LogFormatter();

  @Override
  public void publish(LogRecord record) {
    out.print(formatter.format(record));
  }

  @Override
  public void flush() {
    //system.out handles itself
  }

  @Override
  public void close() throws SecurityException {
    //system.out handles itself
  }
}

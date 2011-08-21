package osmo.tester.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * The main class used to create log files describing OSMOTester behaviour.
 * It is intended to encapsulate logging functionality without requiring external configuration files or
 * external 
 * 
 * @author Teemu Kanstren
 */
public class Logger {
  /** We delegate to the JDK logging interface. */
  private java.util.logging.Logger logger;
  /** When set to true, debug information will be printed to log file/console. */
  public static boolean debug = false;
  /** Log file handler, shared to keep from creating numerous log files. */
  private static FileHandler file;
  /** Name of the log file to be written. */
  private final String filename = "osmotester.log";
  /** Used for synchronization. */
  private final Object lock = new Object();

  /**
   * Constructor.
   *
   * @param clazz The class for which logging is performed.
   */
  public Logger(Class clazz) {
    String name = "";
    String p = clazz.getPackage().getName();
    String[] ps = p.split("\\.");
    for (String s : ps) {
      name += s.charAt(0)+".";
    }
    name += clazz.getSimpleName();
    init(name);
  }

  private void init(String name) {
    logger = java.util.logging.Logger.getLogger(name);
    if (!debug) {
      logger.setLevel(Level.OFF);
      return;
    }
    Level level = Level.ALL;
    logger.setLevel(level);
    LogHandler console = new LogHandler();
    console.setFormatter(new LogFormatter());
    console.setLevel(level);
    logger.addHandler(console);
    try {
      synchronized (lock) {
        if (file == null) {
          file = new FileHandler(filename, false);
        }
      }
      logger.addHandler(file);
      file.setFormatter(new LogFormatter());
      file.setLevel(level);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize file '"+filename+"' for logging.");
    }
  }

  /**
   * Prints debug level messages. If debug level is not enabled, nothing is printed.
   *
   * @param msg The mesage to be printed.
   */
  public void debug(String msg) {
//    System.out.println("hello:"+msg);
    logger.fine(msg);
  }

  /**
   * Prints error messages, including exception stacktrace.
   *
   * @param msg The error message to print.
   * @param e The exception to print.
   */
  public void error(String msg, Exception e) {
    logger.log(Level.SEVERE, msg, e);
  }
}

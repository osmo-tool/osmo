package osmo.common;

import org.apache.logging.log4j.LogManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * The main entry point for writing logs. Create one in each class, just like many of the regular logging frameworks.
 * It is intended to encapsulate logging functionality with less complexity in configuration than general frameworks.
 *
 * @author Teemu Kanstren
 */
public class Logger {
  /** We delegate to the log4j logging interface. */
  private org.apache.logging.log4j.Logger logger;
  /** Maps levels to OSMO string definitions. */
  private final static Map<String, Level> levelMap = new HashMap<>();

  static {
    levelMap.put("off", Level.OFF);
    levelMap.put("debug", Level.FINE);
    levelMap.put("warn", Level.WARNING);
    levelMap.put("error", Level.SEVERE);
    levelMap.put("info", Level.INFO);
  }


  /**
   * Use this to create a logger for each class.
   * Takes the given class, compresses the package name and uses the result as the logging identifier.
   * For example, "foo.bar.X" becomes "f.b.X".
   *
   * @param clazz The class for which logging is performed.
   */
  public Logger(Class clazz) {
    logger = LogManager.getLogger(clazz);
  }

  /**
   * Prints debug level messages. If d level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void d(String msg) {
    logger.debug(msg);
  }

  public void d(String msg, Throwable e) {
    logger.debug(msg, e);
  }

  /**
   * Prints warning level messages. If warning level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void w(String msg) {
    logger.warn(msg);
  }

  /**
   * Prints warning level messages. If warning level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   * @param e Associated error/exception.
   */
  public void w(String msg, Throwable e) {
    logger.warn(msg, e);
  }

  /**
   * Prints information level messages. If information level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void i(String msg) {
    logger.info(msg);
  }

  /**
   * Prints error messages, including exception stacktrace.
   *
   * @param msg The e message to print.
   * @param e   The exception to print.
   */
  public void e(String msg, Throwable e) {
    logger.error(msg, e);
  }

  /**
   * Prints error messages.
   *
   * @param msg The error message to print.
   */
  public void e(String msg) {
    logger.error(msg);
  }
}

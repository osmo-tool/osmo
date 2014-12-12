package osmo.common.log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * The main class used to create log files describing OSMOTester behaviour.
 * It is intended to encapsulate logging functionality with less complexity in configuration than general frameworks.
 *
 * @author Teemu Kanstren
 */
public class Logger {
  /** We delegate to the JDK logging interface. */
  private java.util.logging.Logger logger;
  /** File logging level. */
  public static Level fileLevel = null;
  /** Console logging level. */
  public static Level consoleLevel = Level.SEVERE;
  /** Package filter, only this and subpackages are logged. */
  public static String packageName = "";
  /** Log file handler, shared to keep from creating numerous log files. */
  private static FileHandler file;
  private static LogHandler console;
  /** Maps levels to OSMO string definitions. */
  private final static Map<String, Level> levelMap = new HashMap<>();

  static {
    levelMap.put("off", Level.OFF);
    levelMap.put("debug", Level.FINE);
    levelMap.put("warn", Level.WARNING);
    levelMap.put("error", Level.SEVERE);
    levelMap.put("info", Level.INFO);
    initFromFile();
    Runtime runtime = Runtime.getRuntime();
    System.out.println("Runtime:"+runtime);
    if (file != null) runtime.addShutdownHook(new Thread(file::close));
  }


  /**
   * Constructor.
   *
   * @param clazz The class for which logging is performed.
   */
  public Logger(Class clazz) {
    String name = squeeze(clazz.getPackage().getName());
    name += clazz.getSimpleName();
    init(name);
  }
  
  private static String squeeze(String orange) {
    String juice = "";
    String[] ps = orange.split("\\.");
    for (String s : ps) {
      juice += s.charAt(0) + ".";
    }
    return juice;
  }

  private void init(String name) {
    if (!name.startsWith(packageName)) {
      logger = null;
      return;
    }
    logger = java.util.logging.Logger.getLogger(name);
    logger.setUseParentHandlers(false);
    logger.setLevel(Level.ALL);
    console = new LogHandler();
    console.setFormatter(new LogFormatter());
    console.setLevel(consoleLevel);
    logger.addHandler(console);
    if (file != null) {
      logger.addHandler(file);
    }
  }

  public static void initFromFile() {
    String configurationFile = "osmo-logging.properties";
    try {
      FileInputStream fis = new FileInputStream(configurationFile);
      Properties props = new Properties();
      props.load(fis);
      String logfile = props.getProperty("log.file.name", "osmo.log");
      String fileLogLevel = props.getProperty("log.file.level", "off");
      fileLevel = toLevel(fileLogLevel);

      file = new FileHandler(logfile, false);
      file.setFormatter(new LogFormatter());
      file.setLevel(fileLevel);

      String consoleLogLevel = props.getProperty("log.console.level", "off");
      consoleLevel = toLevel(consoleLogLevel);
      
      String packageFilter = props.getProperty("log.console.package", "");
      packageName = squeeze(packageFilter);
      
    } catch (IOException e) {
      file = null;
      new Logger(Logger.class).debug("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
//      System.err.println("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
//      e.printStackTrace(System.err);
    }
  }

  public static Level toLevel(String text) {
    text = text.toLowerCase();
    Level level = levelMap.get(text);
    if (level == null) {
      throw new IllegalArgumentException("Unknown (file) log level definition:" + text);
    }
    return level;
  }

  /**
   * Prints debug level messages. If debug level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void debug(String msg) {
    if (isOff()) {
      return;
    }
    logger.fine(msg);
  }

  private boolean isOff() {
    if (consoleLevel == Level.OFF && fileLevel == Level.OFF) {
      return true;
    }
    if (logger == null) {
      return true;
    }
    return false;
  }
  
  public void debug(String msg, Throwable e) {
    if (isOff()) {
      return;
    }
    logger.log(Level.FINE, msg, e);
  }

  /**
   * Prints warning level messages. If warning level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void warn(String msg) {
    if (isOff()) {
      return;
    }
    logger.warning(msg);
  }

  /**
   * Prints warning level messages. If warning level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void warn(String msg, Throwable e) {
    if (isOff()) {
      return;
    }
    logger.log(Level.WARNING, msg, e);
  }

  /**
   * Prints information level messages. If info level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void info(String msg) {
    if (isOff()) {
      return;
    }
    logger.info(msg);
  }

  /**
   * Prints error messages, including exception stacktrace.
   *
   * @param msg The error message to print.
   * @param e   The exception to print.
   */
  public void error(String msg, Throwable e) {
    if (isOff()) {
      return;
    }
    logger.log(Level.SEVERE, msg, e);
  }

  /**
   * Prints error messages.
   *
   * @param msg The error message to print.
   */
  public void error(String msg) {
    if (isOff()) {
      return;
    }
    logger.log(Level.SEVERE, msg);
  }
}

package osmo.common.log;

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
  /** We delegate to the JDK logging interface. */
  private java.util.logging.Logger logger;
  /** File logging level. Defaults to nothing being printed.*/
  public static Level fileLevel = null;
  /** Console logging level. Defaults to errors being printed. */
  public static Level consoleLevel = Level.SEVERE;
  /** Package filter, only this and subpackages are logged. */
  public static String packageName = "";
  /** Log file handler, shared to keep from creating numerous log files. */
  private static FileHandler file;
  /** Console file handler. Writes to system.out. */
  private static LogHandler console;
  /** Maps levels to OSMO string definitions. */
  private final static Map<String, Level> levelMap = new HashMap<>();
  /** If true, we use Java Util Logging configuration. */
  private static boolean useJul = false;

  static {
    levelMap.put("off", Level.OFF);
    levelMap.put("debug", Level.FINE);
    levelMap.put("warn", Level.WARNING);
    levelMap.put("error", Level.SEVERE);
    levelMap.put("info", Level.INFO);
    initFromFile();
    if (file != null) Runtime.getRuntime().addShutdownHook(new Thread(file::close));
  }


  /**
   * Use this to create a logger for each class.
   * Takes the given class, compresses the package name and uses the result as the logging identifier.
   * For example, "foo.bar.X" becomes "f.b.X".
   *
   * @param clazz The class for which logging is performed.
   */
  public Logger(Class clazz) {
    String name = squeeze(clazz.getPackage().getName());
    name += clazz.getSimpleName();
    if (useJul) initJUL(name);
    else init(name);
  }

  private void initJUL(String name) {
    logger = java.util.logging.Logger.getLogger(name);
  }

  /**
   * Turns package names into shortened version. For example, "foo.bar.X" becomes "f.b.".
   *
   * @param orange The package name to compress.
   * @return The compressed version.
   */
  private static String squeeze(String orange) {
    String juice = "";
    String[] ps = orange.split("\\.");
    for (String s : ps) {
      juice += s.charAt(0) + ".";
    }
    return juice;
  }

  private synchronized void init(String name) {
    //check if we are configured to log this package name
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
    String configurationFile = "osmo-tester.properties";
    try {
      FileInputStream fis = new FileInputStream(configurationFile);
      Properties props = new Properties();
      props.load(fis);
      useJul = Boolean.parseBoolean(props.getProperty("log.jdk", "false"));
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
      new Logger(Logger.class).d("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
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
   * Prints d level messages. If d level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void d(String msg) {
    if (isOff()) {
      return;
    }
    logger.fine(msg);
  }

  private boolean isOff() {
    if (useJul) return false;
    if (consoleLevel == Level.OFF && fileLevel == Level.OFF) {
      return true;
    }
    if (logger == null) {
      return true;
    }
    return false;
  }

  public void d(String msg, Throwable e) {
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
  public void w(String msg) {
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
  public void w(String msg, Throwable e) {
    if (isOff()) {
      return;
    }
    logger.log(Level.WARNING, msg, e);
  }

  /**
   * Prints information level messages. If i level is not enabled, nothing is printed.
   *
   * @param msg The message to be printed.
   */
  public void i(String msg) {
    if (isOff()) {
      return;
    }
    logger.info(msg);
  }

  /**
   * Prints e messages, including exception stacktrace.
   *
   * @param msg The e message to print.
   * @param e   The exception to print.
   */
  public void e(String msg, Throwable e) {
    if (isOff()) {
      return;
    }
    logger.log(Level.SEVERE, msg, e);
  }

  /**
   * Prints e messages.
   *
   * @param msg The e message to print.
   */
  public void e(String msg) {
    if (isOff()) {
      return;
    }
    logger.log(Level.SEVERE, msg);
  }
}

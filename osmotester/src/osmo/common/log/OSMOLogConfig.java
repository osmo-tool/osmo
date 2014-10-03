package osmo.common.log;

public class OSMOLogConfig {}

//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.Appender;
//import org.apache.logging.log4j.core.Layout;
//import org.apache.logging.log4j.core.appender.RollingFileAppender;
//import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
//import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
//import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
//import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
//import org.apache.logging.log4j.core.config.DefaultConfiguration;
//import org.apache.logging.log4j.core.layout.PatternLayout;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.Properties;
//import java.util.concurrent.TimeUnit;
//import java.util.zip.Deflater;
//
///**
// * @author Teemu Kanstren
// */
//public class OSMOLogConfig extends DefaultConfiguration {
//  public static final String PATTERN_LAYOUT = "[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n";
//  /** Name of the log file to be written. */
//  private static final String DEFAULT_LOG_FILE = "osmo.log";
//  
//  public OSMOLogConfig() {
//    setName("OSMO Tester");
//    // MARKER
//    Layout<? extends Serializable> layout = PatternLayout.createLayout(PATTERN_LAYOUT, null, null, null, true, false, null, null);
//
//    String oneDay = TimeUnit.DAYS.toMillis(1) + "";
//    String oneMB = (1024 * 1024) + "";
//    final TimeBasedTriggeringPolicy timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.createPolicy(oneDay, "true");
//    final SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = SizeBasedTriggeringPolicy.createPolicy(oneMB);
//    final CompositeTriggeringPolicy policy = CompositeTriggeringPolicy.createPolicy(timeBasedTriggeringPolicy,sizeBasedTriggeringPolicy);
//    final DefaultRolloverStrategy strategy = DefaultRolloverStrategy.createStrategy("7", "1", null,Deflater.DEFAULT_COMPRESSION + "", this);
//
//    Properties props = readProperties();
//    String filename = props.getProperty("log.file.name", "osmo.log");
//
//    Appender appender = RollingFileAppender.createAppender(filename, filename + "-yyyy.MM.dd", "true",
//            "OSMO-appender", "true", "8192", "true", policy, strategy, layout, null, null, null, null, null);
//    addAppender(appender);
//    getRootLogger().addAppender(appender, Level.INFO, null);  
//  }
//
//  private Properties readProperties() {
//    String configurationFile = "osmo-logging.properties";
//    Properties props = new Properties();
//    try {
//      FileInputStream fis = new FileInputStream(configurationFile);
//      props.load(fis);
//    } catch (IOException e) {
//      new Logger(Logger.class).debug("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
////      System.err.println("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
////      e.printStackTrace(System.err);
//    }
//    return props;
//  }
//  
////  public static void initFromFile() {
////    String configurationFile = "osmo-logging.properties";
////    try {
////      FileInputStream fis = new FileInputStream(configurationFile);
////      Properties props = new Properties();
////      props.load(fis);
////      logfile = props.getProperty("log.file.name", "osmo.log");
////      String fileLogLevel = props.getProperty("log.file.level", "off");
////      fileLevel = toLevel(fileLogLevel);
////
////      file = new FileHandler(logfile, false);
////      file.setFormatter(new LogFormatter());
////      file.setLevel(fileLevel);
////
////      String consoleLogLevel = props.getProperty("log.console.level", "off");
////      consoleLevel = toLevel(consoleLogLevel);
////
////      String packageFilter = props.getProperty("log.console.package", "");
////      packageName = squeeze(packageFilter);
////
////    } catch (IOException e) {
////      file = null;
////      new Logger(Logger.class).debug("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
//////      System.err.println("Unable to read logging configuration from file '" + configurationFile + "'. Using defaults.");
//////      e.printStackTrace(System.err);
////    }
////  }
//}

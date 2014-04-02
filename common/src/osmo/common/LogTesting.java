package osmo.common;

import osmo.common.log.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Teemu Kanstren */
public class LogTesting {
  public static void main(String[] args) {
    Logger logger = Logger.getLogger(LogTesting.class.getName());
    logger.setUseParentHandlers(false);
    logger.setLevel(Level.ALL);
    Handler console = new ConsoleHandler();
    console.setFormatter(new LogFormatter());
    console.setLevel(Level.WARNING);
    logger.addHandler(console);
    logger.warning("WARNING");
    logger.info("INFO");
    logger.fine("FINE");
  }
}

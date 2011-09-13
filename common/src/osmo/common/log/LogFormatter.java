package osmo.common.log;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Defines the format of the log file. Used by the JDK logging interface.
 * 
 * @author Teemu Kanstren
 */
public class LogFormatter extends Formatter {
  //this would append date to the logged lines
//  private final static String format = "{0,date} {0,time}";
  /** Defines the format of each log line. */
  private final static String format = "{0,time}";
  /** Used to create the log strings according to the format and given messages. */
  private MessageFormat formatter;
  /** We use constant object array to reduce runtime overhead. */
  private Object args[] = new Object[1];
  /** We also use a constant date object to reduce runtime overhead. */
  private Date date = new Date();

  public LogFormatter() {
    formatter = new MessageFormat(format);
    args[0] = date;
  }

  @Override
  public String format(LogRecord record) {
    StringBuffer line = new StringBuffer();
    try {
      long time = record.getMillis();
      date.setTime(time);
      formatter.format(args, line, null);
      line.append(" ");
      line.append(record.getLoggerName()).append(" - ");
      line.append(formatMessage(record)).append("\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return line.toString();
  }
}

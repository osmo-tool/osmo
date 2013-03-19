package osmo.common.log;

import java.io.PrintWriter;
import java.io.StringWriter;
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
  private StringWriter sw = new StringWriter();
  private PrintWriter pw = new PrintWriter(sw);

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
      Throwable thrown = record.getThrown();
      if (thrown != null) {
        thrown.printStackTrace(pw);
        line.append(sw.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return line.toString();
  }
}

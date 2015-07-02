package osmo.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * If you ever need a printstream that just sucks everything up into an endless black hole, this is it..
 *
 * @author Teemu Kanstren
 */
public class NullPrintStream {
  /** This is the singleton instance you should refer to when you need to gulp something down. */
  public static final PrintStream stream = new PrintStream(new NullOutputStream());

  /**
   * This is the actual class that eats the stream up.
   */
  private static class NullOutputStream extends OutputStream {
    /**
     * Takes the given data and "throws it away".
     *
     * @param b The data to write.
     * @throws IOException Never really..
     */
    @Override
    public void write(int b) throws IOException {
    }
  }

}

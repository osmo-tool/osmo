package osmo.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/** @author Teemu Kanstren */
public class NullPrintStream {
  public static final PrintStream stream = new PrintStream(new NullOutputStream());

  private static class NullOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {
    }
  }

}

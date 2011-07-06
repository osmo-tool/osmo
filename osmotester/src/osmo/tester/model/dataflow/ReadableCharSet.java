package osmo.tester.model.dataflow;

import static osmo.tester.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class ReadableCharSet {
  private static final String chars = "abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ0123456789,.<>!\"#¤%&/()=?´`{[]}\\¨^~';:|-_*-+= \t";

  public char next() {
    int min = 0;
    int max = chars.length() - 1;
    int index = cInt(min, max);
    return chars.charAt(index);
  }

  public boolean evaluate(char c) {
    return chars.indexOf(c) >= 0;
  }
}

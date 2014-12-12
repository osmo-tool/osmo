package osmo.tester.ide.intellij.filters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/** @author Teemu Kanstren */
public class LongOnlyDocument extends PlainDocument {
  @Override
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    if (str == null)
      return;
    String oldText = getText(0, getLength());
    String newText = oldText.substring(0, offset) + str + oldText.substring(offset);
    try {
      Long.parseLong(newText);
      super.insertString(offset, str, a);
    } catch (NumberFormatException e) {
    }
  }
}

package osmo.tester.ide.intellij.listeners;

import osmo.common.Randomizer;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class RandomizeSeedListener implements ActionListener {
  private final JTextField field;
  private Randomizer rand = new Randomizer();

  public RandomizeSeedListener(JTextField field) {
    this.field = field;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    field.setText(""+rand.nextLong());
  }
}

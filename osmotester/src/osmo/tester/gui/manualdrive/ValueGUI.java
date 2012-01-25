package osmo.tester.gui.manualdrive;

import osmo.tester.model.dataflow.SearchableInput;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Base GUI for variable values.
 *
 * @author Teemu Kanstren
 */
public abstract class ValueGUI extends JFrame {
  /** The input variable the this GUI is for. */
  protected final SearchableInput input;
  /** The value to provide next. */
  protected Object value = null;
  /** A hack to prevent calling SearchableInputObserver twice if automated (skip) option is chosen. */
  private boolean observed = false;

  public ValueGUI(SearchableInput input) throws HeadlessException {
    this.input = input;
    setTitle(input.getName());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    Container pane = getContentPane();
    pane.add(new JLabel(createValueLabel()), BorderLayout.NORTH);
    pane.add(createValueComponent(), BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        value = value();
        if (value == null) {
          //invalid input observed
          return;
        }
        setVisible(false);
        synchronized (ValueGUI.this) {
          ValueGUI.this.notify();
        }
      }
    });
    JButton skip = new JButton("Skip");
    skip.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ValueGUI.this.input.disableGUI();
        value = ValueGUI.this.input.next();
        ValueGUI.this.input.enableGUI();
        setVisible(false);
        observed = true;
        synchronized (ValueGUI.this) {
          ValueGUI.this.notify();
        }
      }
    });
    JButton auto = new JButton("Auto");
    auto.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ValueGUI.this.input.disableGUI();
        value = ValueGUI.this.input.next();
        setVisible(false);
        observed = true;
        synchronized (ValueGUI.this) {
          ValueGUI.this.notify();
        }
      }
    });
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(ok);
    panel.add(skip);
    panel.add(auto);
    pane.add(panel, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Should create the label for the window to describe what value is requested.
   *
   * @return The label.
   */
  protected abstract String createValueLabel();

  /**
   * Should create the JComponent to request the value from the user.
   *
   * @return The JComponent to get the value.
   */
  protected abstract JComponent createValueComponent();

  /**
   * Gives the value for variable as parsed from user input. If this returns null, it is considered an
   * invalid value and the GUI is left open to request a new valid value.
   *
   * @return The defined value.
   */
  protected abstract Object value();

  /**
   * Provides the next value for the associated input.
   *
   * @return The next defined input value.
   */
  public Object next() {
    observed = false;
    setVisible(true);
    synchronized (this) {
      try {
        wait();
      } catch (InterruptedException e) {
        //ignored
      }
    }
    if (!observed) {
      input.observe(value);
    }
    return value;
  }

  /** Enables the Nimbus look and feel. */
  public static void setNimbus() {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
      // If Nimbus is not available, you can set the GUI to another look and feel.
    }
  }
}

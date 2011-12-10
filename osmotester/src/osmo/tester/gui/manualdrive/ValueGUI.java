package osmo.tester.gui.manualdrive;

import osmo.tester.model.dataflow.SearchableInput;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Teemu Kanstren
 */
public abstract class ValueGUI extends JFrame {
  protected final SearchableInput input;
  protected Object value = null;

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
    build();
    pack();
    setLocationRelativeTo(null);
  }

  protected abstract String createValueLabel();

  protected abstract JComponent createValueComponent();

  protected abstract void build();

  protected abstract Object value();

  public Object next() {
    setVisible(true);
    synchronized (this) {
      try {
        wait();
      } catch (InterruptedException e) {
        //ignored
      }
    }
    input.observe(value);
    return value;
  }
}

package osmo.tester.manualdrive;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Teemu Kanstren
 */
public class EndConditionFrame extends JFrame {
  public EndConditionFrame(final ManualEndCondition mec) throws HeadlessException {
    super("Test Control");
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    JButton endTest = new JButton("End Test");
    endTest.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mec.endTest();
      }
    });
    panel.add(endTest);
    JButton endSuite = new JButton("End Suite");
    endSuite.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mec.endSuite();
      }
    });
    panel.add(endSuite);
    getContentPane().add(panel);
    pack();
  }
}

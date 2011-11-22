package osmo.tester.manualdrive;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.LessRandomAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provide manual interface for controlling test generation
 *
 * @author Olli-Pekka Puolitaival
 */
public class ManualAlgorithm extends JFrame implements FSMTraversalAlgorithm {

  private static final long serialVersionUID = 1L;
  private JPanel contentPane;
  //TODO: put available transitions list in scrollable panel (there was some problems)
  private static JList availableTransitionsList;
  private static JTextPane testLogPane = new JTextPane();
  private static JTextPane testMetricsPane = new JTextPane();
  private static String choiceFromList = null;
  private static boolean autoplay = false;
  private static boolean lockAutoplay = false;
  private static JButton autoPlayButton = new JButton("Start auto play");
  private static JTextPane autoPlayDelayTextPane = new JTextPane();
  private static JComboBox algorithmComboBox = new JComboBox();
  private static boolean running = false;

  /** Create the frame. */
  public ManualAlgorithm() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 600, 460);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    testLogPane.setEditable(false);
    testLogPane.setText("First Test Case starts");
    setContentPane(contentPane);
    JScrollPane scrollTestLog = new JScrollPane(testLogPane);
    JScrollPane testMetricsPaneScroll = new JScrollPane(testMetricsPane);
    JLabel lblTestLog = new JLabel("Test log");
    JLabel lblNextTransition = new JLabel("Next transition");
    JLabel lblTraceability = new JLabel("Metrics");

    autoPlayButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        //Auto play action handling
        try {
          Integer.parseInt(autoPlayDelayTextPane.getText());
        } catch (Exception e2) {
          return;
        }

        if (lockAutoplay) {
          lockAutoplay = false;
          return;
        }

        if (autoplay) {
          autoplay = false;
          lockAutoplay = true;
          autoPlayButton.setText("Start autoplay");
        } else {
          autoplay = true;
          lockAutoplay = true;
          autoPlayButton.setText("Stop autoplay");
        }
      }
    });
    autoPlayButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });

    testMetricsPane.setBackground(SystemColor.menu);
    testMetricsPane.setText("Test metrics");

    availableTransitionsList = new JList();
    availableTransitionsList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        choiceFromList = availableTransitionsList.getSelectedValue().toString();
      }
    });
    availableTransitionsList.setModel(new AbstractListModel() {
      private static final long serialVersionUID = 1L;
      String[] values = new String[]{"Empty!"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });
    availableTransitionsList.setAlignmentX(Component.RIGHT_ALIGNMENT);
    autoPlayDelayTextPane.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        try {
          Integer.parseInt(autoPlayDelayTextPane.getText());
        } catch (Exception e2) {
          autoPlayDelayTextPane.setBackground(Color.RED);
          return;
        }
        autoPlayDelayTextPane.setBackground(Color.WHITE);

      }
    });
    autoPlayDelayTextPane.setText("1000");

    JLabel lblAutoPlayDelay = new JLabel("Auto play delay (ms)");

    algorithmComboBox.setModel(new DefaultComboBoxModel(new String[]{"RandomAlgorithm", "LessRandomAlgorithm", "WeightedAlgorithm"}));
    GroupLayout gl_contentPane = new GroupLayout(contentPane);
    gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false).addComponent(scrollTestLog, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE).addComponent(lblNextTransition).addGroup(gl_contentPane.createSequentialGroup().addComponent(availableTransitionsList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addComponent(lblTestLog)).addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addGap(18).addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addComponent(lblTraceability, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE).addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(autoPlayDelayTextPane, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE).addComponent(autoPlayButton).addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false).addComponent(algorithmComboBox, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(lblAutoPlayDelay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))).addGroup(gl_contentPane.createSequentialGroup().addPreferredGap(ComponentPlacement.UNRELATED).addComponent(testMetricsPaneScroll, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))).addContainerGap()));
    gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblTestLog).addComponent(lblTraceability)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addComponent(scrollTestLog, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblNextTransition)).addComponent(testMetricsPaneScroll, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane.createSequentialGroup().addComponent(algorithmComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblAutoPlayDelay).addPreferredGap(ComponentPlacement.RELATED).addComponent(autoPlayDelayTextPane, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(autoPlayButton)).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(availableTransitionsList, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))).addContainerGap()));

    contentPane.setLayout(gl_contentPane);
  }

  public void run() {
    if (!running) {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            ManualAlgorithm frame = new ManualAlgorithm();
            frame.setVisible(true);
            running = true;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      //Little sleep makes it works better
      sleep(300);
    }
  }

  private String historyText(TestSuite history) {
    String ret = "";

    for (TestCase tc : history.getAllTestCases()) {
      for (TestStep ts : tc.getSteps()) {
        ret = ts.getId() + ". " + ts.getTransition().getName() + "\n" + ret;
      }
    }
    return ret;
  }

  private String getSpaces(int a) {
    String ret = "";
    for (int i = 0; i < a; i++) {
      ret += " ";
    }
    return ret;
  }

  private String coverageText(TestSuite history) {
    Map<FSMTransition, Integer> a = history.getTransitionCoverage();
    String ret = "";
    for (FSMTransition t : a.keySet()) {
      ret += t.getName() + getSpaces(30 - t.getName().length()) + "\t" + a.get(t) + "\n";
    }
    return ret;
  }

  /** Just waiting that user make the selection to the next transition */
  private void waitForSelection() {
    if (autoplay) {
      int temp = 0;
      try {
        temp = Integer.parseInt(autoPlayDelayTextPane.getText());
      } catch (Exception e) {
        return;
      }
      sleep(temp);
    } else {
      do {
        if (choiceFromList != null || autoplay) break;
        sleep(100);
      } while (true);
    }
  }

  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {

    }
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    run();

    //Make some updates
    testLogPane.setText(historyText(history));
    testMetricsPane.setText(coverageText(history));

    //Set available transitions to the UI
    availableTransitionsList.setModel(new ModelHelper(transitions));

    //Waiting for selection
    waitForSelection();

    //Make selection
    for (FSMTransition t : transitions) {
      if (t.getName().equals(choiceFromList)) {
        choiceFromList = null;
        return t;
      }
    }

    //Autorun mode
    switch (algorithmComboBox.getSelectedIndex()) {
      case 0:
        return new RandomAlgorithm().choose(history, transitions);
      case 1:
        return new LessRandomAlgorithm().choose(history, transitions);
      case 2:
        return new WeightedRandomAlgorithm().choose(history, transitions);
      default:
        throw new RuntimeException("Error in algrithm handler. The index was: " + algorithmComboBox.getSelectedIndex());
    }
  }
}


/**
 * Helps to add items to the list
 *
 * @author puolol
 */
class ModelHelper extends AbstractListModel {

  private static final long serialVersionUID = 1L;
  List<String> values = new ArrayList<String>();

  public ModelHelper() {
    this.values.add("Empty");
  }

  public ModelHelper(List<FSMTransition> transitions) {
    values = new ArrayList<String>();
    for (FSMTransition t : transitions) {
      values.add(t.getName());
    }
  }

  public int getSize() {
    return values.size();
  }

  public Object getElementAt(int index) {
    return values.get(index);
  }
}

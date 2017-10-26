package osmo.tester.gui.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.ModelHelper;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.reporting.trace.TraceReportWriter;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides a manual interface for controlling test generation. Works by attaching itself to the generator as
 * a test generation algorithm. Shows always the set of available steps that can be taken in the
 * current state and allows the user to choose which one to take. Also enables some data elements
 * to be scripted.
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class ManualAlgorithm extends JFrame implements FSMTraversalAlgorithm {
  /** Available steps to choose from. */
  private static JList availableStepsList;
  /** History of taken steps and variables. */
  private static JTextPane testLogPane = new JTextPane();
  /** Overall metrics for taken steps. */
  private static JTextPane metricsPane = new JTextPane();
  /** Used to pass the list choice between inner classes. */
  private static String choiceFromList = null;
  /** If the autoplay is enabled. */
  private static boolean autoplay = false;
  /** Some kind of hack used to stop user from performing multiple clicks on autoplay accidentally. */
  private static boolean lockAutoplay = false;
  /** Button for starting autoplay. */
  private static JButton autoPlayButton = new JButton("Start auto play");
  /** The delay between taking steps in autoplay. In milliseconds. */
  private static JTextPane autoPlayDelayTextPane = new JTextPane();
  /** For choosing which algorithm to use. */
  private static JComboBox algorithmComboBox = new JComboBox();
  /** Tells if we already started the GUI or not. */
  private static boolean running = false;
  /** The suite of created test cases. */
  private static TestSuite suite = null;
  /** Use this if user choosen "random" as algorithm in GUI. */
  private final RandomAlgorithm randomAlgorithm = new RandomAlgorithm();
  /** Use this if user choosen "balancing" as algorithm in GUI. */
  private final BalancingAlgorithm balancingAlgorithm = new BalancingAlgorithm();
  /** Use this if user choosen "weighted" as algorithm in GUI. */
  private final WeightedRandomAlgorithm weightedRandomAlgorithm = new WeightedRandomAlgorithm();
  /** The model we are using for generation. */
  private FSM fsm = null;
  /** Used as locking object to synchronize between GUI elements. */
  private static final Object lock = new Object();
  /** Button to end test case after next step. */
  private static final JButton btnEndTest = new JButton("End Test");
  /** Button to end test suite after next step. */
  private static final JButton btnEndSuite = new JButton("End Suite");
  /** Button to write script to disk. */
  private static final JButton btnWriteScript = new JButton("Write Script");
  /** This is given to generator as end condition to allow the end test/suite buttons to work. */
  private final ManualEndCondition mec = new ManualEndCondition();
  private String historyText = "";

  /**
   * Create the GUI.
   *
   * @param tester The test generator.
   */
  public ManualAlgorithm(OSMOTester tester) {
    OSMOConfiguration config = tester.getConfig();
    config.setManual(true);
    config.setSuiteEndCondition(mec);
    config.setTestEndCondition(mec);
    tester.addListener(new GUIGenerationListener(this));
    setNimbus();
    setTitle("OSMOTester Manual Script Generation");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setBounds(100, 100, 600, 460);
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    testLogPane.setEditable(false);
    testLogPane.setText("First Test Case starts");
    setContentPane(contentPane);
    JScrollPane scrollTestLog = new JScrollPane(testLogPane);
    JScrollPane testMetricsPaneScroll = new JScrollPane(metricsPane);
    JLabel lblTestLog = new JLabel("Test log");
    JLabel lblNextStep = new JLabel("Next Step");
    JLabel lblTraceability = new JLabel("Metrics");

    autoPlayButton.addActionListener(e -> {
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
    });

    metricsPane.setBackground(SystemColor.menu);
    metricsPane.setText("Test metrics");

    availableStepsList = new JList();
    availableStepsList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        synchronized (lock) {
          Object selectedValue = availableStepsList.getSelectedValue();
          //user may click outside possible selections
          if (selectedValue == null) return;
          choiceFromList = selectedValue.toString();
          lock.notifyAll();
        }
      }
    });
    availableStepsList.setModel(new AbstractListModel() {
      private static final long serialVersionUID = 1L;
      String[] values = new String[]{"Empty!"};

      public int getSize() {
        return values.length;
      }

      public Object getElementAt(int index) {
        return values[index];
      }
    });
    availableStepsList.setAlignmentX(Component.RIGHT_ALIGNMENT);
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

    algorithmComboBox.setModel(new DefaultComboBoxModel(new String[]{"RandomAlgorithm", "BalancingRandomAlgorithm", "WeightedAlgorithm"}));

    btnEndTest.addActionListener(e -> {
      System.out.println("end test pressed");
      mec.setEndTest(true);
    });

    btnEndSuite.addActionListener(e -> {
      mec.setEndSuite(true);
      mec.setEndTest(true);
    });

    btnWriteScript.addActionListener(e -> writeScript());
    GroupLayout gl_contentPane = new GroupLayout(contentPane);
    gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(scrollTestLog, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                                            .addComponent(lblNextStep)
                                            .addComponent(availableStepsList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(lblTestLog))
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_contentPane.createSequentialGroup()
                                            .addGap(18)
                                            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                    .addComponent(lblTraceability, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                                    .addGroup(gl_contentPane.createSequentialGroup()
                                                            .addGap(22)
                                                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                                    .addComponent(btnEndSuite)
                                                                    .addComponent(btnEndTest)
                                                                    .addComponent(btnWriteScript))
                                                            .addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                                    .addComponent(autoPlayDelayTextPane, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(autoPlayButton)
                                                                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                                                                            .addComponent(algorithmComboBox, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                            .addComponent(lblAutoPlayDelay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                                    .addGroup(gl_contentPane.createSequentialGroup()
                                            .addPreferredGap(ComponentPlacement.UNRELATED)
                                            .addComponent(testMetricsPaneScroll, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)))
                            .addContainerGap())
    );
    gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(lblTestLog)
                                    .addComponent(lblTraceability))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_contentPane.createSequentialGroup()
                                            .addComponent(scrollTestLog, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.UNRELATED)
                                            .addComponent(lblNextStep))
                                    .addComponent(testMetricsPaneScroll, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                    .addGroup(gl_contentPane.createSequentialGroup()
                                            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                    .addGroup(gl_contentPane.createSequentialGroup()
                                                            .addComponent(algorithmComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(lblAutoPlayDelay)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(autoPlayDelayTextPane, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(gl_contentPane.createSequentialGroup()
                                                            .addComponent(btnWriteScript)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(btnEndTest)))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(autoPlayButton)
                                                    .addComponent(btnEndSuite)))
                                    .addComponent(availableStepsList, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap())
    );

    contentPane.setLayout(gl_contentPane);
  }

  /**
   * Start the GUI if not yet started.
   *
   * @param suite Provides future access to tests that will be created/generated.
   */
  public void run(TestSuite suite) {
    if (!running) {
      ManualAlgorithm.suite = suite;
      EventQueue.invokeLater(() -> {
        try {
          setVisible(true);
          running = true;
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      //Little sleep makes it works better (yeah?)
      sleep(300);
    }
  }

  /**
   * For creating start padding for metrics etc.
   *
   * @param a How many spaces we want.
   * @return That many spaces.
   */
  private String getSpaces(int a) {
    String ret = "";
    for (int i = 0 ; i < a ; i++) {
      ret += " ";
    }
    return ret;
  }

  /**
   * Creates the data for the metrics pane.
   *
   * @param suite Created tests.
   * @return The text for metrics pane.
   */
  private String coverageText(TestSuite suite) {
    TestCoverage coverage = new TestCoverage();
    coverage.addCoverage(suite.getCoverage());
    coverage.addCoverage(suite.getCurrentTest().getCoverage());
    Map<String, Integer> a = coverage.getStepCoverage();
    String ret = "Number of times steps taken:\n";
    for (String t : a.keySet()) {
      ret += t + getSpaces(30 - t.length()) + "\t" + a.get(t) + "\n";
    }

    ret += "\nNumber of times variables used:\n";
    Map<String, Collection<String>> values = coverage.getVariableValues();
    for (String t : values.keySet()) {
      ret += t + getSpaces(30 - t.length()) + "\t" + values.get(t).size() + "\n";
    }
    
    return ret;
  }

  private String stateText() {
    String text = "";
    Collection<VariableField> variables = fsm.getModelVariables();
    for (VariableField variable : variables) {
      text += variable.getName() + ": " + variable.getValue() + "\n";
    }
    return text;
  }

  /** Just waiting that user make the selection to the next transition */
  private void waitForSelection() {
    synchronized (lock) {
      choiceFromList = null;
      if (autoplay) {
        int temp = 0;
        try {
          temp = Integer.parseInt(autoPlayDelayTextPane.getText());
          lock.wait(temp);
        } catch (Exception ignored) {
        }
      } else {
        do {
          if (choiceFromList != null || autoplay) {
            break;
          }
          if (mec.endSuite(null, null) || mec.endTest(null, null)) {
            break;
          }
          try {
            lock.wait(100);
          } catch (InterruptedException e) {
            //ignored
          }
        } while (true);
      }
    }
  }

  /**
   * To avoid clutter.
   *
   * @param ms How many millis to sleep.
   */
  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignored) {

    }
  }

  @Override
  public void init(long seed, FSM fsm) {
    this.randomAlgorithm.init(seed, fsm);
    this.balancingAlgorithm.init(seed, fsm);
    this.weightedRandomAlgorithm.init(seed, fsm);
    this.fsm = fsm;
    Collection<VariableField> variables = fsm.getModelVariables();
    for (VariableField variable : variables) {
      Object value = variable.getValue();
      if (value instanceof SearchableInput) {
        SearchableInput si = (SearchableInput) value;
        si.enableGUI();
        si.setChecked(true);
      }
    }
    historyText = "GENERATION START\n";
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    run(suite);

    testLogPane.setText(historyText);
    testLogPane.setCaretPosition(testLogPane.getText().length());
    metricsPane.setText(stateText());
    metricsPane.setText(coverageText(suite));

    //Set available transitions to the UI
    availableStepsList.setModel(new ModelHelper(choices));

    //Waiting for selection
    waitForSelection();
    if (mec.endSuite(null, null) || mec.endTest(null, null)) {
      return null;
    }

    //Make selection
    for (FSMTransition t : choices) {
      String stepName = t.getName().toString();
      if (stepName.equals(choiceFromList)) {
        choiceFromList = null;
        historyText += "STEP:"+stepName+"\n";
        return t;
      }
    }

    //Autorun mode
    switch (algorithmComboBox.getSelectedIndex()) {
      case 0:
        return randomAlgorithm.choose(suite, choices);
      case 1:
        return balancingAlgorithm.choose(suite, choices);
      case 2:
        return weightedRandomAlgorithm.choose(suite, choices);
      default:
        throw new RuntimeException("Error in algorithm handler. The index was: " + algorithmComboBox.getSelectedIndex());
    }
  }

  /** Enables the Nimbus look and feel. */
  public void setNimbus() {
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

  /** Writes the defined script to file in HTML format. */
  public void writeScript() {
    TraceReportWriter reporter = new TraceReportWriter();
    String filename = "osmo-output/manual-tests.html";
    try {
      reporter.write(suite.getAllTestCases(), filename);
      JOptionPane.showMessageDialog(this, "Wrote file to:" + filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void testEnded() {
    availableStepsList.setEnabled(false);
    mec.setEndTest(false);
  }
  
  public void testStarted() {
    availableStepsList.setEnabled(true);
    historyText += "--= NEW TEST =--\n";
  }
  
  public void suiteEnded() {
    availableStepsList.setEnabled(false);
    autoPlayButton.setEnabled(false);
    btnEndTest.setEnabled(false);
    System.out.println("end test disabled");
    btnEndSuite.setEnabled(false);
  }

  @Override
  public void initTest(long seed) {
  }

  @Override
  public FSMTraversalAlgorithm cloneMe() {
    return null;
  }
}
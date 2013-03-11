package osmo.tester.gui.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.ModelHelper;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.parser.ParserResult;

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
 * Provides a manual interface for controlling test generation. Works by attaching itself to OSMOTester as
 * a test generation algorithm. Shows always the set of available transitions that can be taken in the
 * current state and allows the user to choose which one to take. Also enables all SearchableInput elements
 * to be scripted.
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class ManualAlgorithm extends JFrame implements FSMTraversalAlgorithm {
  /** Available transitions to choose from. */
  private static JList availableTransitionsList;
  /** History of taken transitions and variables. */
  private static JTextPane testLogPane = new JTextPane();
  /** Overall metrics for taken transitions. */
  private static JTextPane statePane = new JTextPane();
  /** Used to pass the list choice between inner classes. */
  private static String choiceFromList = null;
  /** If the autoplay is enabled. */
  private static boolean autoplay = false;
  /** Some kind of hack used to stop user from performing multiple clicks on autoplay accidentally. */
  private static boolean lockAutoplay = false;
  /** Button for starting autoplay. */
  private static JButton autoPlayButton = new JButton("Start auto play");
  /** The delay between taking transitions in autoplay. In milliseconds. */
  private static JTextPane autoPlayDelayTextPane = new JTextPane();
  /** For choosing which algorithm to use. */
  private static JComboBox algorithmComboBox = new JComboBox();
  /** Tells if we already started the GUI or not. */
  private static boolean running = false;
  /** The suite of created test cases. */
  private static TestSuite suite = null;
  private final RandomAlgorithm randomAlgorithm = new RandomAlgorithm();
  private final BalancingAlgorithm balancingAlgorithm = new BalancingAlgorithm();
  private final WeightedRandomAlgorithm weightedRandomAlgorithm = new WeightedRandomAlgorithm();
  /** The model we are using for generation. */
  private FSM fsm = null;
  private static Object lock = new Object();
  private static final JButton btnEndTest = new JButton("End Test");
  private static final JButton btnEndSuite = new JButton("End Suite");
  private static final JButton btnWriteScript = new JButton("Write Script");

  /** Create the frame. */
  public ManualAlgorithm() {
    OSMOConfiguration.setManual(true);
    MainGenerator.addStaticListener(new GUIGenerationListener(this));
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
    JScrollPane testMetricsPaneScroll = new JScrollPane(statePane);
    JLabel lblTestLog = new JLabel("Test log");
    JLabel lblNextStep = new JLabel("Next Step");
    JLabel lblTraceability = new JLabel("Value History");

    autoPlayButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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

    statePane.setBackground(SystemColor.menu);
    statePane.setText("Test metrics");

    availableTransitionsList = new JList();
    availableTransitionsList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        synchronized (lock) {
          Object selectedValue = availableTransitionsList.getSelectedValue();
          choiceFromList = selectedValue.toString();
          lock.notifyAll();
        }
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

    algorithmComboBox.setModel(new DefaultComboBoxModel(new String[]{"RandomAlgorithm", "BalancingRandomAlgorithm", "WeightedAlgorithm"}));

    btnEndTest.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("end test pressed");
        suite.setShouldEndTest(true);
      }

    });

    btnEndSuite.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        suite.setShouldEndTest(true);
        suite.setShouldEndSuite(true);
      }
    });

    btnWriteScript.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        writeScript();
      }
    });
    GroupLayout gl_contentPane = new GroupLayout(contentPane);
    gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(scrollTestLog, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                                            .addComponent(lblNextStep)
                                            .addComponent(availableTransitionsList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                    .addComponent(availableTransitionsList, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))
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
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
//            ManualAlgorithm frame = new ManualAlgorithm();
//            frame.setVisible(true);
            setVisible(true);
            running = true;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      //Little sleep makes it works better (yeah?)
      sleep(300);
    }
  }

  /**
   * Builds the description of generated test cases.
   *
   * @param history Generated tests.
   * @return String to show to user.
   */
  private String historyText(TestSuite history) {
    String ret = "";

    int tcId = 1;
    List<TestCase> tests = history.getAllTestCases();
    for (TestCase tc : tests) {
      ret += "----== NEW TEST " + tcId + " ==----\n";
      tcId++;
      for (TestStep ts : tc.getSteps()) {
        int tsId = ts.getId();
        String added = tsId + ". " + ts.getName() + "\n";
        Collection<ModelVariable> values = ts.getValues();
        int i = 1;
        for (ModelVariable value : values) {
          added += tsId + "." + i + ". " + value.getName() + " = " + value.getValues() + "\n";
          i++;
        }
        ret += added;
        Collection<String> coveredRequirements = ts.getCoveredRequirements();
        for (String req : coveredRequirements) {
          ret += "(Covered requirement:"+req+")\n";
        }
      }
    }
    return ret;
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
   * @param history Created tests.
   * @return The text for metrics pane.
   */
  private String coverageText(TestSuite history) {
    Map<String, Integer> a = history.getTransitionCoverage();
    String ret = "";
    for (String t : a.keySet()) {
      ret += t + getSpaces(30 - t.length()) + "\t" + a.get(t) + "\n";
    }
    return ret;
  }

  private String stateText() {
    String text = "";
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    for (SearchableInput input : inputs) {
      text += input.getName() + ": " + input.getLatestValue() + "\n";
    }
    Collection<VariableField> variables = fsm.getStateVariables();
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
        } catch (Exception e) {
          return;
        }
      } else {
        do {
          if (choiceFromList != null || autoplay) {
            break;
          }
          if (suite.shouldEndSuite() || suite.shouldEndTest()) {
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
    } catch (InterruptedException e) {

    }
  }

  @Override
  public void init(ParserResult parserResult) {
    this.fsm = parserResult.getFsm();
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    for (SearchableInput input : inputs) {
      input.enableGUI();
      input.setChecked(true);
    }
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    run(history);

    //Make some updates
    testLogPane.setText(historyText(history));
    testLogPane.setCaretPosition(testLogPane.getText().length());
    statePane.setText(stateText());
//    statePane.setText(coverageText(history));

    //Set available transitions to the UI
    availableTransitionsList.setModel(new ModelHelper(choices));

    //Waiting for selection
    waitForSelection();
    if (suite.shouldEndTest() || suite.shouldEndSuite()) {
      return null;
    }

    //Make selection
    for (FSMTransition t : choices) {
      if (t.getName().equals(choiceFromList)) {
        choiceFromList = null;
        return t;
      }
    }

    //Autorun mode
    switch (algorithmComboBox.getSelectedIndex()) {
      case 0:
        return randomAlgorithm.choose(history, choices);
      case 1:
        return balancingAlgorithm.choose(history, choices);
      case 2:
        return weightedRandomAlgorithm.choose(history, choices);
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

  /** Writes the defined script to file in format scriptable by OSMOTester. */
  public void writeScript() {
    ManualScriptWriter writer = new ManualScriptWriter();
    writer.write(suite);
    String separator = System.getProperty("file.separator");
    String filename = System.getProperty("user.dir")+separator+ManualScriptWriter.FILENAME;
    JOptionPane.showMessageDialog(this, "Wrote file to:"+filename);
  }
  
  public void testEnded() {
    availableTransitionsList.setEnabled(false);
  }
  
  public void testStarted() {
    availableTransitionsList.setEnabled(true);
  }
  
  public void suiteEnded() {
    availableTransitionsList.setEnabled(false);
    autoPlayButton.setEnabled(false);
    btnEndTest.setEnabled(false);
    System.out.println("end test disabled");
//    btnEndTest.repaint();
    btnEndSuite.setEnabled(false);
//    btnEndSuite.repaint();
  }

  @Override
  public void initTest() {
  }
}
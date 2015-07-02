package osmo.mjexamples.gsm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import osmo.common.NullPrintStream;
import osmo.tester.OSMOConfiguration;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.TestStep;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.greedy.GreedyOptimizer;

/**
 * This is an EFSM model of the SIM card within a mobile phone.
 * It models just a small part of the functionality to do with
 * accessing files and directories within the SIM card, and the
 * PINs and PUKs used to make this secure.
 * <p/>
 * See Chapter 11 of the book "Practical Model-Based Testing:
 * A Tools Approach" for more discussion of this system and
 * a version of the model in B.
 * <p/>
 * This ModelJUnit version of the model was translated from a UML/OCL
 * model developed at LEIRIOS Technologies, which in turn was adapted
 * from an earlier and larger version of the B model published in
 * the above book.
 *
 * @author marku, ported by teemu kanstren
 */
public class SimCard {
  public enum E_Status {Enabled, Disabled}

  public enum B_Status {Blocked, Unblocked}

  public enum Status_Word {
    sw_9000, sw_9404, sw_9405, sw_9804,
    sw_9840, sw_9808, sw_9400
  }

  public enum File_Type {Type_DF, Type_EF}

  public enum Permission {Always, CHV, Never, Adm, None}

  public enum F_Name {MF, DF_GSM, EF_LP, EF_IMSI, DF_Roaming, EF_FR, EF_UK}

  // These variables model the attributes within each Sim Card.
  protected int PIN;   // can be 11 or 12
  protected static final int PUK = 22;
  protected E_Status status_en;
  protected B_Status status_PIN_block;
  protected B_Status status_block;
  protected int counter_PIN_try;
  protected int counter_PUK_try;
  protected boolean perm_session;
  public static final int Max_Pin_Try = 3;
  public static final int Max_Puk_Try = 10;
  protected String read_data;
  protected Status_Word result;
  public Requirements req = new Requirements();
  public PrintStream out = System.out;

  // These variables model what a SimCard knows about Files.
  protected File DF;  // the current directory (never null)
  protected File EF;  // the current elementary file, or null
  protected Map<F_Name, File> files = new HashMap<>();

  /**
   * If this is non-null, each action calls the corresponding adaptor
   * action, which tests the GSM11 implementation.
   */
  protected SimCardAdaptor sut = null;

  /**
   * If <code>sut0</code> is null, then the model runs without
   * testing any SUT.
   *
   * @param sut0 Can be null.
   */
  public SimCard(SimCardAdaptor sut0) {
    sut = sut0;
    //TODO: read these automatically
    req.add(Req.Change1);
    req.add(Req.Change2);
    req.add(Req.Change3);
    req.add(Req.Change4);
    req.add(Req.Change5);
    req.add(Req.Disable1);
    req.add(Req.Disable2);
    req.add(Req.Disable3);
    req.add(Req.Disable4);
    req.add(Req.Disable5);
    req.add(Req.Enable1);
    req.add(Req.Enable2);
    req.add(Req.Enable3);
    req.add(Req.Enable4);
    req.add(Req.Enable5);
    req.add(Req.ReadBin1);
    req.add(Req.ReadBin1_67);
    req.add(Req.ReadBin2);
    req.add(Req.ReadBin3);
    req.add(Req.ReadBin3_5);
    req.add(Req.ReadBin3_8);
    req.add(Req.SelectFile2);
    req.add(Req.SelectFile2_File3_File4);
    req.add(Req.SelectFile6);
    req.add(Req.SelectFile7);
    req.add(Req.Unblock3);
    req.add(Req.Unblock4);
    req.add(Req.Unblock5);
    req.add(Req.Unblock7Unblock2);
    req.add(Req.UnblockCHV1);
    req.add(Req.VerifyCHV1);
    req.add(Req.VerifyCHV2);
    req.add(Req.VerifyCHV3);
    req.add(Req.VerifyCHV4);
    req.add(Req.VerifyCHV5);
  }

  @GenerationEnabler
  public void realMode() {
    sut = new SimCardAdaptor();
  }

  @ExplorationEnabler
  public void simulation() {
    sut = null;
    out = NullPrintStream.stream;
  }

  /**
   * This sets up a hierarchy of files that is used for testing.
   * Their contents remain constant throughout the testing.
   */
  private void initFiles() {
    files.clear();
    File mf = new File(File_Type.Type_DF, F_Name.MF, "d1", Permission.None, null);
    File df_gsm = new File(File_Type.Type_DF, F_Name.DF_GSM, "d1", Permission.None, mf);
    File ef_imsi = new File(File_Type.Type_EF, F_Name.EF_IMSI, "d2", Permission.CHV, df_gsm);
    File ef_lp = new File(File_Type.Type_EF, F_Name.EF_LP, "d1", Permission.Always, df_gsm);
    File df_roam = new File(File_Type.Type_DF, F_Name.DF_Roaming, "d1", Permission.None, df_gsm);
    File ef_fr = new File(File_Type.Type_EF, F_Name.EF_FR, "d3", Permission.Adm, df_roam);
    File ef_uk = new File(File_Type.Type_EF, F_Name.EF_UK, "d4", Permission.Never, df_roam);
    files.put(F_Name.MF, mf);
    files.put(F_Name.DF_GSM, df_gsm);
    files.put(F_Name.EF_IMSI, ef_imsi);
    files.put(F_Name.EF_LP, ef_lp);
    files.put(F_Name.DF_Roaming, df_roam);
    files.put(F_Name.EF_FR, ef_fr);
    files.put(F_Name.EF_UK, ef_uk);
  }

  @CoverageValue
  public String getState(TestCaseStep step) {
    return DF.name.toString()
            + "," + (EF == null ? "null" : EF.name.toString())
            + ",PIN=" + PIN
            + "," + counter_PIN_try
            + "," + counter_PUK_try
            + "," + status_en
            + "," + (status_PIN_block == B_Status.Blocked ? "PINBLOCKED" : "")
            + "," + (status_block == B_Status.Blocked ? "PUKBLOCKED" : "")
            + "," + result.toString()
            ;
  }

  @BeforeTest
  public void reset() {
    PIN = 11;
    status_en = E_Status.Enabled;
    status_PIN_block = B_Status.Unblocked;
    status_block = B_Status.Unblocked;
    counter_PIN_try = 0;
    counter_PUK_try = 0;
    perm_session = false;
    read_data = "";  // None
    initFiles();
    DF = files.get(F_Name.MF);
    EF = null;
    result = Status_Word.sw_9000;  // Okay
    if (sut != null) {
      //cannot cover stuff in reset function as there is no test at that time...
//      req.covered("@REQ: RESET @");
      sut.reset();
    }
  }

  @TestStep("Verify PIN 11")
  public void verifyPIN11() {
    Verify_PIN(11);
  }

  @TestStep("Verify PIN 12")
  public void verifyPIN12() {
    Verify_PIN(12);
  }

  public void Verify_PIN(int Pin) {
    // Pre: Pin > 0 and Pin < 10000

    if (status_PIN_block == B_Status.Blocked) {
      result = Status_Word.sw_9840;
      req.covered(Req.VerifyCHV1);//"@REQ: VERIFY_CHV1 @"
    } else if (status_en == E_Status.Disabled) {
      result = Status_Word.sw_9808;
      req.covered(Req.VerifyCHV5); //"@REQ: VERIFY_CHV5 @"
    } else if (Pin == PIN) {
      counter_PIN_try = 0;
      perm_session = true;
      result = Status_Word.sw_9000;
      req.covered(Req.VerifyCHV2); //"@REQ: REQ6,VERIFY_CHV2 @"
    } else if (counter_PIN_try == Max_Pin_Try - 1) {
      counter_PIN_try = Max_Pin_Try;
      status_PIN_block = B_Status.Blocked;
      perm_session = false;
      result = Status_Word.sw_9840;
      req.covered(Req.VerifyCHV4); //"@REQ: REQ6, VERIFY_CHV4 @"
    } else {
      counter_PIN_try = counter_PIN_try + 1;
      result = Status_Word.sw_9804;
      req.covered(Req.VerifyCHV3); //"@REQ: VERIFY_CHV3 @"
    }

    if (sut != null) {
      sut.Verify_PIN(Pin, result);
    }
  }

  @TestStep("Unlock PIN OK 12")
  public void unblockPINGood12() {
    Unblock_PIN(22, 12);
  }

  @TestStep("Unlock PIN bad")
  public void unblockPINBad() {
    Unblock_PIN(11, 11);
  }

  public void Unblock_PIN(int Puk, int new_Pin) {
    // pre: Puk > 0 and Puk < 10000 and new_Pin > 0 and new_Pin < 10000

    if (status_block == B_Status.Blocked) {
      result = Status_Word.sw_9840;
      req.covered(Req.UnblockCHV1); //"@REQ: Unblock_CHV1 @"
    } else if (Puk == PUK) {
      PIN = new_Pin;
      counter_PIN_try = 0;
      counter_PUK_try = 0;
      perm_session = true;
      status_PIN_block = B_Status.Unblocked;
      result = Status_Word.sw_9000;
      if (status_en == E_Status.Disabled) {
        // Sep 2007: corrected this e from the B and UML/OCL models.
        // (The B model did not model enabled/disabled PINs, and the
        //  UML/OCL model set status_en to Disabled rather than Enabled.)
        // status_en = E_Status.Disabled; /*@REQ: Unblock5 @*/
        status_en = E_Status.Enabled;
        req.covered(Req.Unblock5); //"@REQ: Unblock5 @"
      } else {
        //unclear from line below if this belongs here or someplace else
        req.covered(Req.Unblock7Unblock2); //"@REQ: Unblock7,Unblock2 @"
        // leave status_en unchanged
      }/*@REQ: Unblock7,Unblock2 @*/
    } else if (counter_PUK_try == Max_Puk_Try - 1) {
      out.println("BLOCKED PUK!!! PUK try counter=" + counter_PUK_try);
      counter_PUK_try = Max_Puk_Try;
      status_block = B_Status.Blocked;
      perm_session = false;
      result = Status_Word.sw_9840;
      req.covered(Req.Unblock4); //"@REQ: REQ7, Unblock4 @"
    } else {
      counter_PUK_try = counter_PUK_try + 1;
      result = Status_Word.sw_9804;
      req.covered(Req.Unblock3); //"@REQ: Unblock3 @"
    }

    if (sut != null) {
      sut.Unblock_PIN(Puk, new_Pin, result);
    }
  }

  // When the correct PIN is 12, this will also test the invalid-PIN case
  @TestStep("Enable PIN 11")
  public void enablePIN11() {
    Enabled_PIN(11);
  }

  public void Enabled_PIN(int Pin) {
    // pre: Pin > 0 and Pin < 10000
    out.println("status_PIN_block:"+status_PIN_block+", status_en:"+status_en+" "+Pin+"="+PIN);
//    if (status_PIN_block != B_Status.Blocked && status_en != E_Status.Enabled && PIN != Pin) {
//      System.out.println("HERE WE ARE");
//    }

    if (status_PIN_block == B_Status.Blocked) {
      result = Status_Word.sw_9840;
      req.covered(Req.Enable2); //"@REQ: ENABLE2 @"
     // throw new RuntimeException("REQun etsintä päällä");
    } else if (status_en == E_Status.Enabled) {
      result = Status_Word.sw_9808;
      req.covered(Req.Enable3); //"@REQ: ENABLE3 @"
    } else if (Pin == PIN) {
      counter_PIN_try = 0;
      perm_session = true;
      status_en = E_Status.Enabled;
      result = Status_Word.sw_9000;
      req.covered(Req.Enable1); //"@REQ: ENABLE1 @"
    } else if (counter_PIN_try == Max_Pin_Try - 1) {
      counter_PIN_try = Max_Pin_Try;
      status_PIN_block = B_Status.Blocked;
      perm_session = false;
      result = Status_Word.sw_9840;
      //cannot be covered
      req.covered(Req.Enable4); //"@REQ: ENABLE4 @"
    } else {
      counter_PIN_try = counter_PIN_try + 1;
      result = Status_Word.sw_9804;
      //cannot be covered
      req.covered(Req.Enable5); //"@REQ: ENABLE5 @"
    }

    if (sut != null) {
      sut.Enabled_PIN(Pin, result);
    }
  }

  // When the correct PIN is 12, this will also test the invalid-PIN case
  @TestStep("Disable PIN OK")
  public void disablePINGood() {
    Disabled_PIN(11);
  }

  public void Disabled_PIN(int Pin) {
    // pre:     Pin > 0 and Pin < 10000

    if (status_PIN_block == B_Status.Blocked) {
      result = Status_Word.sw_9840;
      req.covered(Req.Disable2); //"@REQ: DISABLE2 @"
    } else if (status_en == E_Status.Disabled) {
      result = Status_Word.sw_9808;
      req.covered(Req.Disable3); //"@REQ: DISABLE3 @"
    } else if (Pin == PIN) {
      counter_PIN_try = 0;
      perm_session = true;
      status_en = E_Status.Disabled;
      result = Status_Word.sw_9000;
      req.covered(Req.Disable1); //"@REQ: DISABLE1 @"
    } else if (counter_PIN_try == Max_Pin_Try - 1) {
      counter_PIN_try = Max_Pin_Try;
      status_PIN_block = B_Status.Blocked;
      perm_session = false;
      result = Status_Word.sw_9840;
      req.covered(Req.Disable4); //"@REQ: DISABLE4 @"
    } else {
      counter_PIN_try = counter_PIN_try + 1;
      result = Status_Word.sw_9804; 
      req.covered(Req.Disable5); //"@REQ: DISABLE5 @"
    }

    if (sut != null) {
      sut.Disabled_PIN(Pin, result);
    }
  }

  // When the correct PIN is 12, this will also test the invalid-PIN case
  @TestStep("Change same PIN")
  public void changePinSame() {
    Change_PIN(11, 11);
  }  // ???

  @TestStep("Change new PIN")
  public void changePinNew() {
    Change_PIN(11, 12);
  }

  public void Change_PIN(int old_Pin, int new_Pin) {
    // pre: old_Pin > 0 and old_Pin < 10000 and new_Pin > 0 and new_Pin < 10000

    if (status_PIN_block == B_Status.Blocked) {
      result = Status_Word.sw_9840;
      req.covered(Req.Change2); //"@REQ: CHANGE2 @"
    } else if (status_en == E_Status.Disabled) {
      result = Status_Word.sw_9808;
      req.covered(Req.Change3); //"@REQ: CHANGE3 @"
    } else if (old_Pin == PIN) {
      PIN = new_Pin;
      counter_PIN_try = 0;
      perm_session = true;
      result = Status_Word.sw_9000;
      req.covered(Req.Change1); //"@REQ: CHANGE1 @"
    } else if (counter_PIN_try == Max_Pin_Try - 1) {
      counter_PIN_try = Max_Pin_Try;
      status_PIN_block = B_Status.Blocked;
      perm_session = false;
      result = Status_Word.sw_9840;
      req.covered(Req.Change4); //"@REQ: CHANGE4 @"
    } else {
      counter_PIN_try = counter_PIN_try + 1;
      result = Status_Word.sw_9804;
      req.covered(Req.Change5); //"@REQ: CHANGE5 @"
    }

    if (sut != null) {
      sut.Change_PIN(old_Pin, new_Pin, result);
    }
  }

  @TestStep("Select MF")
  public void selectMF() {
    Select_file(F_Name.MF);
  }

  @TestStep("Select DF GSM")
  public void selectDF_Gsm() {
    Select_file(F_Name.DF_GSM);
  }

  @TestStep("Select DF Roaming")
  public void selectDF_Roaming() {
    Select_file(F_Name.DF_Roaming);
  }

  @TestStep("Select EF IMSI")
  public void selectEF_IMSI() {
    Select_file(F_Name.EF_IMSI);
  }

  @TestStep("Select EF LP")
  public void selectEF_LP() {
    Select_file(F_Name.EF_LP);
  }

  @TestStep("Select EF FR")
  public void selectEF_FR() {
    Select_file(F_Name.EF_FR);
  }

  // We omit testing EF_UK, since its permissions are similar to EF_FR
  // @Action public void selectEF_UK() {Select_file(F_Name.EF_UK);}
  public void Select_file(F_Name file_name) {
    // pre: true

    File temp_file = files.get(file_name);
    if (temp_file.type == File_Type.Type_DF) {
      /* file_name is a directory */
      /* Check to see if file_name is reachable.
       * That is, if it refers to the master file or
       * to the parent of the current directory or
       * to a child of the current directory.
       * 
       * Sept 2007: Added the case where the requested directory is
       * the already-selected directory (and is not MF).  This case was
       * missing from the published B model in "Practical Model-Based
       * Testing", and was discovered when executing randomly-generated
       * tests from this model against the sample implementation.
       */
      if (file_name == F_Name.MF
              || file_name == DF.name
              || temp_file == DF.parent
              || (temp_file.parent != null && temp_file.parent == DF)) {
        result = Status_Word.sw_9000;
        DF = temp_file;
        EF = null;
        req.covered(Req.SelectFile2_File3_File4); //"@REQ: REQ1, REQ3, SELECT_FILE2, SELECT_FILE3, SELECT_FILE4 @"
      } else {
            /* the directory file_name cannot be selected */
        result = Status_Word.sw_9404;
        req.covered(Req.SelectFile6); //"@REQ: SELECT_FILE6 @"
      }
    } else {
      /* file_name is a elementary file */
      if (temp_file.parent == DF) {
        /*? file_name is a child of the current directory ?*/
        result = Status_Word.sw_9000;
        EF = temp_file;
        req.covered(Req.SelectFile2); //"@REQ: REQ2, SELECT_FILE2@"
      } else {
        /* file_name is not a child of the current directory 
         * and is not the current directory
         */
        result = Status_Word.sw_9405;
        req.covered(Req.SelectFile7); //"@REQ: SELECT_FILE7 @"
      }
    }

    if (sut != null) {
      sut.Select_file(file_name, result);
    }
  }

  @TestStep("Read Binary")
  public void Read_Binary() {
    // pre: true

    /*? No current file selected ?*/
    if (EF == null) {
      result = Status_Word.sw_9400;
      req.covered(Req.ReadBin2); //"@REQ: READ_BINARY2 @"
//      throw new RuntimeException("REQun etsintä päällä");
    } else if (EF.perm_read == Permission.Always) {
      result = Status_Word.sw_9000;
      read_data = EF.data;
      req.covered(Req.ReadBin1); //"@REQ: READ_BINARY1, REQ4 @"
    } else if (EF.perm_read == Permission.Never) {
      result = Status_Word.sw_9804;
      //TODO:impossible to cover
      req.covered(Req.ReadBin3_5); //"@REQ: READ_BINARY3, REQ5 @"
    } else if (EF.perm_read == Permission.Adm) {
      result = Status_Word.sw_9804;
      req.covered(Req.ReadBin3_8); //"@REQ: READ_BINARY3, REQ8 @"
    } else if (perm_session) {
      result = Status_Word.sw_9000;
      read_data = EF.data;
      req.covered(Req.ReadBin1_67); //"@REQ: READ_BINARY1, REQ6, REQ7 @"
    } else {
      result = Status_Word.sw_9804;
      req.covered(Req.ReadBin3); //"@REQ: READ_BINARY3 @"
    }

    if (sut != null) {
      sut.Read_Binary(result, read_data);
    }
  }

  //this should be preserved just as an example of how the greedy can provide smaller score with max limit between iterations
  public static void main(String[] args) {
    GSMModelFactory factory = new GSMModelFactory();
    OSMOConfiguration config = new OSMOConfiguration();
    config.setFactory(factory);
    ScoreConfiguration sc = new ScoreConfiguration();
    sc.setLengthWeight(0);
    GreedyOptimizer optimizer = new GreedyOptimizer(config, sc);
    
    optimizer.setTimeout(5); //128225
    optimizer.disableThreshold();
    optimizer.setMax(5);
    long start = System.currentTimeMillis();
    GenerationResults results = optimizer.search(100, 1);
    long end = System.currentTimeMillis();
    long diff = end-start;
    System.out.println("diff:"+diff);
    ScoreCalculator calculator = new ScoreCalculator(sc);
    int score = calculator.calculateScore(results.getCoverage());
    System.out.println("score:"+score+" tests:"+results.getTests().size()); //128225, 459 tests
    List<TestCase> trim = new ArrayList<>();
    for (int i = 0 ; i < 5 ; i++) {
      trim.add(results.getTests().get(i));
    }
    int score2 = calculator.calculateScore(new TestCoverage(trim));
    System.out.println("score2:"+score2); //24551, 18897
  }
}

package osmo.tester.tools.sip;

import osmo.common.log.Logger;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.TestStep;

import java.util.ArrayList;
import java.util.List;

/** @author Teemu Kanstren */
public class TestModel {
  private static Logger log = new Logger(TestModel.class);
  private List<SipUser> users = new ArrayList<>();
  private String uname;
  private String pw;

  public static void main(String[] args) throws Exception {
    TestModel model = new TestModel(args[0], args[1]);
    model.init();
  }

  public TestModel(String uname, String pw) {
    this.uname = uname;
    this.pw = pw;
  }

  @BeforeTest
  public void init() throws Exception {
    users.clear();
    createUser("192.168.0.80", "matti");
    createUser("192.168.0.22", "teppo");
    System.out.println("initialized model:");
    boolean ok = true;
    for (SipUser user : users) {
    }
    if (!ok) {
      System.out.println("Failed to start a client, exiting.");
      System.exit(1);
    }
//    createUser("192.168.0.82", "simo");
//    createUser("192.168.0.83", "timo");
//    createUser("192.168.0.84", "paavo");
//    createUser("192.168.0.85", "jorma");
//    createUser("192.168.0.86", "keijo");
//    createUser("192.168.0.87", "teijo");
  }
  
  private void createUser(String ip, String name) {
    users.add(new SipUser(ip, name));
  }

  @TestStep("Send Message OK")
  public void sendMsgOK() {
    
  }

  @TestStep("Send Message Unknown Recipient")
  public void sendMsgUR() {

  }

  @TestStep("Make Call OK")
  public void callOK() {

  }

  @TestStep("Make Call Busy")
  public void callBusy() {

  }
}

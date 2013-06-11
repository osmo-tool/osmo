package osmo.tester.tools.sip;

/** @author Teemu Kanstren */
public class TestClient {
  public static void main(String[] args) {
    JainScripter bob = new JainScripter("bob", "localhost", 5060);
    JainScripter alice = new JainScripter("alice", "localhost", 5061);
    bob.sendMessage("sip:alice@localhost:5061", "hello from bob");
    alice.sendMessage("sip:bob@localhost:5060", "hello from alice");
  }
}

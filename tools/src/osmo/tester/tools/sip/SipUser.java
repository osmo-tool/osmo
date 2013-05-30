package osmo.tester.tools.sip;

/** @author Teemu Kanstren */
public class SipUser {
  private final String ip;
  private final String name;

  public SipUser(String ip, String name) {
    this.ip = ip;
    this.name = name;
  }

  public String getIp() {
    return ip;
  }

  public String getName() {
    return name;
  }
}

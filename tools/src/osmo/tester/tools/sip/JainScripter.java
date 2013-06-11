package osmo.tester.tools.sip;

import osmo.common.log.Logger;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.util.ArrayList;
import java.util.Properties;

/** @author Teemu Kanstren */
public class JainScripter {
  private static Logger log = new Logger(JainScripter.class);
  private SipStack sipStack;
  private AddressFactory addressFactory;
  private HeaderFactory headerFactory;
  private MessageFactory messageFactory;
  private SipProvider sipProvider;
  private final String myUserName;
  private final String myHost;
  private final int myPort;
  public static final String HEADER_FROM_CLIENT = "OSMO_SIP_Tester_v0.1";

  public JainScripter(String userName, String ip, int port) {
    this.myUserName = userName;
    this.myHost = ip;
    this.myPort = port;
    SipFactory sipFactory = SipFactory.getInstance();
    sipFactory.setPathName("gov.nist");
    Properties properties = new Properties();
    //the stack name allows configuring different instances in the same VM
    properties.setProperty("javax.sip.STACK_NAME", userName+"-client");

    properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
    properties.setProperty("gov.nist.javax.sip.SERVER_LOG", userName+"-server-log.txt");
    properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", userName+"-client-debug.log");

    try {
      sipStack = sipFactory.createSipStack(properties);
      headerFactory = sipFactory.createHeaderFactory();
      addressFactory = sipFactory.createAddressFactory();
      messageFactory = sipFactory.createMessageFactory();

      ListeningPoint tcp = sipStack.createListeningPoint(ip, port, "tcp");
      ListeningPoint udp = sipStack.createListeningPoint(ip, port, "udp");

      JainListener listener = new JainListener(messageFactory);
      sipProvider = sipStack.createSipProvider(tcp);
      sipProvider.addSipListener(listener);
      sipProvider = sipStack.createSipProvider(udp);
      sipProvider.addSipListener(listener);
    } catch (Exception e) {
      log.error("Failed to create SIP scripter", e);
    }
  }

  /**
   * This method uses the SIP stack to send a message. 
   */
  public void sendMessage(String to, String msg) {
    try {
      sendMessage2(to, msg);
    } catch (Exception e) {
      log.error("Failed to send msg", e);
    }
  }
  
  private void sendMessage2(String to, String msg) throws Exception {
    String fromAddress = myHost + ":" + myPort;
    SipURI from = addressFactory.createSipURI(myUserName, fromAddress);
    Address fromNameAddress = addressFactory.createAddress(from);
    fromNameAddress.setDisplayName(myUserName);
    FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, HEADER_FROM_CLIENT);

    String toUserName = to.substring(to.indexOf(":") + 1, to.indexOf("@"));
    String toAddress = to.substring(to.indexOf("@") + 1);

    SipURI toUri = addressFactory.createSipURI(toUserName, toAddress);
    Address toNameAddress = addressFactory.createAddress(toUri);
    toNameAddress.setDisplayName(toUserName);
    ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

    SipURI requestURI = addressFactory.createSipURI(toUserName, toAddress);
    requestURI.setTransportParam("udp");

    ArrayList viaHeaders = new ArrayList();
    ViaHeader viaHeader = headerFactory.createViaHeader(myHost, myPort, "udp", "branch1");
    viaHeaders.add(viaHeader);

    CallIdHeader callIdHeader = sipProvider.getNewCallId();

    long sequence = 1;
    CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequence, Request.MESSAGE);

    MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

    Request request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader, 
            cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

    SipURI contactURI = addressFactory.createSipURI(myUserName, myHost);
    contactURI.setPort(myPort);
    Address contactAddress = addressFactory.createAddress(contactURI);
    contactAddress.setDisplayName(myUserName);
    ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
    request.addHeader(contactHeader);

    ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
    request.setContent(msg, contentTypeHeader);

    sipProvider.sendRequest(request);
  }
}

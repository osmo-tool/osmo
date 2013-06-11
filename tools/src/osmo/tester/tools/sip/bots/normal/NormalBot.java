package osmo.tester.tools.sip.bots.normal;

import osmo.common.log.Logger;
import osmo.tester.tools.sip.JainListener;

import javax.sip.ClientTransaction;
import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
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
import java.util.List;
import java.util.Properties;

/** @author Teemu Kanstren */
public class NormalBot {
  private static Logger log = new Logger(NormalBot.class);
  private AddressFactory addressFactory;
  private HeaderFactory headerFactory;
  private MessageFactory messageFactory;
  private SipProvider tcpProvider;
  private SipProvider udpProvider;
  private final String myName;
  private final String myHost;
  private final int myPort;
  public static final String HEADER_FROM_CLIENT = "OSMO_SIP_Tester_v0.1";
  private long seq = 1;
  private final ContactHeader contactHeader;
  private final MaxForwardsHeader maxForwards;
  private final FromHeader fromHeader;
  private final Address fromAddress;

  public NormalBot(String userName, String host, int port) {
    this.myName = userName;
    this.myHost = host;
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
      SipStack sipStack = sipFactory.createSipStack(properties);
      headerFactory = sipFactory.createHeaderFactory();
      addressFactory = sipFactory.createAddressFactory();
      messageFactory = sipFactory.createMessageFactory();

      ListeningPoint tcp = sipStack.createListeningPoint(host, port, "tcp");
      ListeningPoint udp = sipStack.createListeningPoint(host, port, "udp");

      JainListener listener = new JainListener(messageFactory);
      tcpProvider = sipStack.createSipProvider(tcp);
      tcpProvider.addSipListener(listener);
      udpProvider = sipStack.createSipProvider(udp);
      udpProvider.addSipListener(listener);

      SipURI fromUri = addressFactory.createSipURI(myName, myHost + ":" + myPort);
      this.fromAddress = addressFactory.createAddress(fromUri);
      fromAddress.setDisplayName(myName);
      this.fromHeader = headerFactory.createFromHeader(fromAddress, HEADER_FROM_CLIENT);

      SipURI contactURI = addressFactory.createSipURI(myName, myHost);
      contactURI.setPort(myPort);
      Address contactAddress = addressFactory.createAddress(contactURI);
      contactAddress.setDisplayName(myName);
      this.contactHeader = headerFactory.createContactHeader(contactAddress);

      this.maxForwards = headerFactory.createMaxForwardsHeader(5);

    } catch (Exception e) {
      log.error("Failed to create SIP stack", e);
      throw new RuntimeException("Failed to initialize SIP stack", e);
    }
  }
  
  public void register(String server) {
    try {
      register2(server);
    } catch (Exception e) {
      log.error("Failed to send register message", e);
      throw new RuntimeException("Failed to send register message", e);
    }
  }
  
  public void register2(String server) throws Exception {
    CallIdHeader callIdHeader = udpProvider.getNewCallId();
    CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(seq++, Request.REGISTER);
    ToHeader toHeader = this.headerFactory.createToHeader(fromAddress, null);
    List<ViaHeader> viaHeaders = new ArrayList<>();
    ViaHeader viaHeader = this.headerFactory.createViaHeader(myHost, myPort, "udp", null);
    viaHeaders.add(viaHeader);
    URI requestURI = addressFactory.createAddress(server).getURI();
    Request request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader,
            cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
    request.addHeader(contactHeader);
    ClientTransaction transaction = udpProvider.getNewClientTransaction(request);
    transaction.sendRequest();
  }
  
  public void invite() {
    try {
      invite2();
    } catch (Exception e) {
      log.error("Failed to send invite message", e);
      throw new RuntimeException("Failed to send invite message", e);
    }
  }
  
  public void invite2() throws Exception {
    
  }

  /**
   * This method uses the SIP stack to send a message. 
   */
  public void sendMessage(String to, String at, String msg) {
    try {
      sendMessageTo2(to, at, msg);
    } catch (Exception e) {
      log.error("Failed to send msg", e);
    }
  }

  private void sendMessageTo2(String toUser, String toHost, String msg) throws Exception {
    SipURI toUri = addressFactory.createSipURI(toUser, toHost);
    Address toNameAddress = addressFactory.createAddress(toUri);
    toNameAddress.setDisplayName(toUser);
    ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

    SipURI requestURI = addressFactory.createSipURI(toUser, toHost);
    requestURI.setTransportParam("udp");

    List<ViaHeader> viaHeaders = new ArrayList<>();
    ViaHeader viaHeader = headerFactory.createViaHeader(myHost, myPort, "udp", null);
    viaHeaders.add(viaHeader);

    CallIdHeader callIdHeader = udpProvider.getNewCallId();
    CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq++, Request.MESSAGE);

    Request request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader,
            cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

    request.addHeader(contactHeader);

    ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
    request.setContent(msg, contentTypeHeader);

    udpProvider.sendRequest(request);
  }

  public void bye() {
    try {
      bye2();
    } catch (Exception e) {
      log.error("Failed to send bye message", e);
      throw new RuntimeException("Failed to send bye message", e);
    }
  }

  public void bye2() throws Exception {

  }
}
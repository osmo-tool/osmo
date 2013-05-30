package osmo.tester.tools.sip;

import osmo.common.log.Logger;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/** @author Teemu Kanstren */
public class JainListener implements SipListener {
  private Logger log = new Logger(JainListener.class);
  
  /**
   * This method is called by the SIP stack when a new request arrives. 
   */
  @Override
  public void processRequest(RequestEvent event) {
    Request req = event.getRequest();

    String method = req.getMethod();
    if (!method.equals("MESSAGE")) { //bad request type.
//      messageProcessor.processError("Bad request type: " + method);
      return;
    }

    FromHeader from = (FromHeader) req.getHeader("From");
//    messageProcessor.processMessage(from.getAddress().toString(), new String(req.getRawContent()));
    Response response = null;
    try { //Reply with OK
//      response = messageFactory.createResponse(200, req);
      ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
      toHeader.setTag("888"); //This is mandatory as per the spec.
//      ServerTransaction st = sipProvider.getNewServerTransaction(req);
//      st.sendResponse(response);
    } catch (Throwable e) {
      log.error("Failed to send OK response to message", e);
    }
  }

  /** This method is called by the SIP stack when a response arrives. */
  @Override
  public void processResponse(ResponseEvent event) {
    Response response = event.getResponse();
    int status = response.getStatusCode();

    if ((status >= 200) && (status < 300)) { //Success!
//      messageProcessor.processInfo("--Sent");
      return;
    }

//    messageProcessor.processError("Previous message not sent: " + status);
  }

  /**
   * This method is called by the SIP stack when there's no answer 
   * to a message. Note that this is treated differently from an error
   * message. 
   */
  @Override
  public void processTimeout(TimeoutEvent event) {
//    messageProcessor
//            .processError("Previous message not sent: " + "timeout");
  }

  /**
   * This method is called by the SIP stack when there's an asynchronous
   * message transmission error.  
   */
  @Override
  public void processIOException(IOExceptionEvent event) {
//    messageProcessor.processError("Previous message not sent: "
//            + "I/O Exception");
  }

  /**
   * This method is called by the SIP stack when a dialog (session) ends. 
   */
  @Override
  public void processTransactionTerminated(TransactionTerminatedEvent event) {
  }

  /**
   * This method is called by the SIP stack when a transaction ends. 
   */
  @Override
  public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
  }
}

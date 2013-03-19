/**
 * MetaEditAPI.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package osmo.metaedit;

public interface MetaEditAPI extends javax.xml.rpc.Service {
    public java.lang.String getMetaEditAPIPortAddress();

    public osmo.metaedit.MetaEditAPIPortType getMetaEditAPIPort() throws javax.xml.rpc.ServiceException;

    public osmo.metaedit.MetaEditAPIPortType getMetaEditAPIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

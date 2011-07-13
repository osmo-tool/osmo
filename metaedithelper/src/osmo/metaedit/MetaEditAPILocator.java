/**
 * MetaEditAPILocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package osmo.metaedit;

public class MetaEditAPILocator extends org.apache.axis.client.Service implements osmo.metaedit.MetaEditAPI {

    public MetaEditAPILocator() {
    }


    public MetaEditAPILocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MetaEditAPILocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MetaEditAPIPort
    private java.lang.String MetaEditAPIPort_address = "http://localhost:6390/MetaEditAPI";

    public java.lang.String getMetaEditAPIPortAddress() {
        return MetaEditAPIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MetaEditAPIPortWSDDServiceName = "MetaEditAPIPort";

    public java.lang.String getMetaEditAPIPortWSDDServiceName() {
        return MetaEditAPIPortWSDDServiceName;
    }

    public void setMetaEditAPIPortWSDDServiceName(java.lang.String name) {
        MetaEditAPIPortWSDDServiceName = name;
    }

    public osmo.metaedit.MetaEditAPIPortType getMetaEditAPIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MetaEditAPIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMetaEditAPIPort(endpoint);
    }

    public osmo.metaedit.MetaEditAPIPortType getMetaEditAPIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            osmo.metaedit.MetaEditAPISoapBindingStub _stub = new osmo.metaedit.MetaEditAPISoapBindingStub(portAddress, this);
            _stub.setPortName(getMetaEditAPIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMetaEditAPIPortEndpointAddress(java.lang.String address) {
        MetaEditAPIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (osmo.metaedit.MetaEditAPIPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                osmo.metaedit.MetaEditAPISoapBindingStub _stub = new osmo.metaedit.MetaEditAPISoapBindingStub(new java.net.URL(MetaEditAPIPort_address), this);
                _stub.setPortName(getMetaEditAPIPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MetaEditAPIPort".equals(inputPortName)) {
            return getMetaEditAPIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://metacase.com/wsdl/", "MetaEditAPI");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://metacase.com/wsdl/", "MetaEditAPIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MetaEditAPIPort".equals(portName)) {
            setMetaEditAPIPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

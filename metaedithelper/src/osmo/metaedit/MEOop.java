/**
 * MEOop.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package osmo.metaedit;

public class MEOop  implements java.io.Serializable {
    private int areaID;

    private int objectID;

    public MEOop() {
    }

    public MEOop(
           int areaID,
           int objectID) {
           this.areaID = areaID;
           this.objectID = objectID;
    }


    /**
     * Gets the areaID value for this MEOop.
     * 
     * @return areaID
     */
    public int getAreaID() {
        return areaID;
    }


    /**
     * Sets the areaID value for this MEOop.
     * 
     * @param areaID
     */
    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }


    /**
     * Gets the objectID value for this MEOop.
     * 
     * @return objectID
     */
    public int getObjectID() {
        return objectID;
    }


    /**
     * Sets the objectID value for this MEOop.
     * 
     * @param objectID
     */
    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MEOop)) return false;
        MEOop other = (MEOop) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.areaID == other.getAreaID() &&
            this.objectID == other.getObjectID();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getAreaID();
        _hashCode += getObjectID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MEOop.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://metacase.com/type", "MEOop"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("areaID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "areaID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "objectID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

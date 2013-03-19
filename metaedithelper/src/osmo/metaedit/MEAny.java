/**
 * MEAny.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package osmo.metaedit;

public class MEAny  implements java.io.Serializable {
    private java.lang.String meType;

    private java.lang.String meValue;

    public MEAny() {
    }

    public MEAny(
           java.lang.String meType,
           java.lang.String meValue) {
           this.meType = meType;
           this.meValue = meValue;
    }


    /**
     * Gets the meType value for this MEAny.
     * 
     * @return meType
     */
    public java.lang.String getMeType() {
        return meType;
    }


    /**
     * Sets the meType value for this MEAny.
     * 
     * @param meType
     */
    public void setMeType(java.lang.String meType) {
        this.meType = meType;
    }


    /**
     * Gets the meValue value for this MEAny.
     * 
     * @return meValue
     */
    public java.lang.String getMeValue() {
        return meValue;
    }


    /**
     * Sets the meValue value for this MEAny.
     * 
     * @param meValue
     */
    public void setMeValue(java.lang.String meValue) {
        this.meValue = meValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MEAny)) return false;
        MEAny other = (MEAny) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meType==null && other.getMeType()==null) || 
             (this.meType!=null &&
              this.meType.equals(other.getMeType()))) &&
            ((this.meValue==null && other.getMeValue()==null) || 
             (this.meValue!=null &&
              this.meValue.equals(other.getMeValue())));
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
        if (getMeType() != null) {
            _hashCode += getMeType().hashCode();
        }
        if (getMeValue() != null) {
            _hashCode += getMeValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MEAny.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://metacase.com/type", "MEAny"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "meType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "meValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

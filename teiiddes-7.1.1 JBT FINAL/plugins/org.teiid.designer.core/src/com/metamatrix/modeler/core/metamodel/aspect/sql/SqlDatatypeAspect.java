/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;

/**
 * SqlDatatypeAspect is used to get the different properties on a datatype 
 * for runtime metadata.
 */
public interface SqlDatatypeAspect extends SqlAspect, SqlDatatypeCheckerAspect {
    
    /**
     * If the data type is numeric, the length is the total number of 
     * significant digits used to express the number. If it is a string, 
     * character array, or bit array it represents the maximum length of 
     * the value. For time and timestamp data types, the length is the number 
     * of positions that make up the fractional seconds.  The value of length
     * is a non-negative integer.
     * @param eObject The <code>EObject</code> to check 
     * @return int
     */
    int getLength(EObject eObject);

    /**
     * Returns an int indicating the precision length. Default to 
     * MetadataConstants.NOT_DEFINED_INT if not set.
     * @param eObject The <code>EObject</code> to check 
     * @return int
     */
    int getPrecisionLength(EObject eObject);

    /**
     * Returns the scale, which is the number of significant digits to the 
     * right of the decimal point. The scale cannot exceed the length, and the 
     * scale defaults to 0 (meaning it is an integer number and the decimal 
     * point is dropped).
     * @param eObject The <code>EObject</code> to check 
     * @return int
     */
    int getScale(EObject eObject);

    /**
     * Returns an int indicating the radix. Default to 
     * MetadataConstants.NOT_DEFINED_INT if not set.
     * @param eObject The <code>EObject</code> to check 
     * @return int
     */
    int getRadix(EObject eObject);

    /**
     * Returns a boolean indicating if the element data is signed.
     * @param eObject The <code>EObject</code> to check 
     * @return boolean
     */
    boolean isSigned(EObject eObject);

    /**
     * Returns a boolean indicating if the element is auto incremented by 
     * the database.  Therefore, this element value should not be provided 
     * on an insert statement.
     * @param eObject The <code>EObject</code> to check 
     * @return boolean
     */
    boolean isAutoIncrement(EObject eObject);

    /**
     * Returns a boolean indicating if the element data is case sensitive.
     * This value shall be false if the data type is not a character, 
     * character array or string type.
     * @param eObject The <code>EObject</code> to check 
     * @return boolean
     */
    boolean isCaseSensitive(EObject eObject);

    /**
     * Return short indicating the type.
     * @param eObject The <code>EObject</code> to check 
     * @return short
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.DATATYPE_TYPES
     */
    short getType(EObject eObject);

    /**
     * Returns a short indicating the serach typr.
     * @param eObject The <code>EObject</code> to check 
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.SEARCH_TYPES 
     */
    short getSearchType(EObject eObject);

    /**
     * Returns a short indicating if the element can be set to null.
     * @param eObject The <code>EObject</code> to check 
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.NULL_TYPES 
     */
    short getNullType(EObject eObject);
        
    /**
     * Return the UUID String for a given Datatype.
     * @param eObject The <code>EObject</code> to check 
     * @return the Stringified UUID for the given Datatype
     */
    String getUuidString(EObject eObject);

    /**
     * Returns the name of the Java class that represents this datatype
     * @param eObject The <code>EObject</code> to check 
     * @return String is the name of the Java class
     */
    String getJavaClassName(EObject eObject);

    /**
     * Returns the name of the runtime type that this datatype
     * is mapped to.
     * @param eObject The <code>EObject</code> to check 
     * @return runtime type name
     */
    String getRuntimeTypeName(EObject eObject);
    
    /**
     * Returns whether the runtime type is fixed or not
     * @param eObject The <code>EObject</code> to check 
     * @return Boolean
     */
    Boolean getRuntimeTypeFixed(EObject eObject);    

    /**
     * Return a string that uniquely identifies the datatype. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @param eObject The <code>EObject</code> to check 
     * @return String the identifier
     */
    String getDatatypeID(EObject eObject);

    /**
     * Return a string that uniquely identifies the datatype for
     * which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @param eObject The <code>EObject</code> to check 
     * @return String the identifier
     */
    String getBasetypeID(EObject eObject);

    /**
     * Return the object that is the datatype for
     * which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * @param eObject The <code>EObject</code> to check 
     * @return object the basetype
     */
    Object getBasetype(EObject eObject);

    /**
     * Return the object in the datatype hierarchy that is the 
     * built-in primitive datatype for which this datatype is 
     * an extension/restriction. If this datatype has no base or
     * supertype then null is returned. 
     * @param eObject The <code>EObject</code> to check 
     * @return object the basetype
     */
    Object getPrimitiveType(EObject eObject);

    /**
     * Return a string that uniquely identifies the built-in primitive datatype for
     * which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @param eObject The <code>EObject</code> to check 
     * @return String the identifier
     */
    String getPrimitiveTypeID(EObject eObject);

    /**
     * Return a map consisting of name value pairs defining 
     * extension properties for this datatype. If no extension
     * properties exist then an empty map is returned. 
     * @param eObject The <code>EObject</code> to check 
     * @return Map of extension properties
     */
    Map getEnterpriseExtensionsMap(EObject eObject);

    /**
     * Return the description string for this datatype
     * @param eObject The <code>EObject</code> to check 
     * @return the description string
     */
    String getDescription(EObject eObject);

    /**
     * Return whether the supplied object is a built-in datatype.
     * @param eObject The <code>EObject</code> to check 
     * @return true if the object is one of the built-in simple types, or
     * false otherwise
     */
    boolean isBuiltInDatatype(EObject eObject);

    /**
     * Return whether the supplied object represents a simple type
     * @param eObject The <code>EObject</code> to check 
     * @return true if the object represents a simple type
     */
    boolean isSimpleDatatype(EObject eObject);

    /**
     * Return whether the supplied object represents a complex type
     * @param eObject The <code>EObject</code> to check 
     * @return true if the object represents a complex type
     */
    boolean isComplexDatatype(EObject eObject);

    /**
     * Returns whether the type definition is one of the flavours of the ur-type, i.e., 
     * complex <a href="http://www.w3.org/TR/xmlschema-1/#ur-type-itself">anyType</a>,
     * simple <a href="http://www.w3.org/TR/xmlschema-2/#built-in-datatypes">anyType</a>, or
     * <a href="http://www.w3.org/TR/xmlschema-2/#dt-anySimpleType">anySimpleType</a>.
     * @param eObject The <code>EObject</code> to check 
     * @return whether the type definition is one of the flavours of the ur-type.
     */
    boolean isURType(EObject eObject);

    /**
     * Returns the variety used to characterize the 
     * @param eObject The <code>EObject</code> to check 
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.DATATYPES_VARIETIES 
     */
    short getVarietyType(EObject eObject);

    /**
     * Depending on the value of the variety type additional properties
     * may be defined.
     * @param eObject The <code>EObject</code> to check 
     * @return List
     */
    List getVarietyProps(EObject eObject);

    /**
     * Get a list of <code>EObject</code>s objects for the datatype 
     * elements defining this datatype
     * @param eObject The <code>EObject</code> for which datatype 
     * elements are obtained 
     * @return a list of <code>EObject</code>s
     */
    //  List getElements(EObject eObject);

    /**
     * Check this <code>EObject</code> to see if it contains the enterprise datatype
     * attributes 
     * @param type
     * @return success true if this is an enterprise datatype
     */
    public boolean isEnterpriseDataType(final EObject type);

    /**
     * Set the attribute contents of <code>EnterpriseDatatypeInfo</code> on the 
     * <code>XSDSimpleTypeDefinition</code>. If the schema for the enterprise datatype
     * is not marked as an enterprise schema, it will be now marked with the namespace uri import.
     * @param type
     * @param edtInfo
     */
    public void setEnterpriseDataAttributes(final XSDSimpleTypeDefinition type, final EnterpriseDatatypeInfo edtInfo);

   /**
    * Remove the enterprise datatype attributes from the given <code>XSDSimpleTypeDefinition</code>.
    * If there are no enterprise datatypes left in the schema, remove the enterprise namespace declaration.
    * @param type
    */    
    public void unSetEnterpriseDataAttributes(final XSDSimpleTypeDefinition type);
    
   /**
    * Obtain the enterprise datatype attributes values from the given
    *  <code>XSDSimpleDefinitionType</code> and wrap them in an 
    *  <code>EnterpriseDatatypeInfo</code>.  
    * @param type
    * @return edtInfo
    */   
   public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo (final XSDSimpleTypeDefinition type);
   
   /**
    * Set the basetype on the <code>XSDSimpleTypeDefinition</code>.
    * Add import and namespace declaration when necessary.
    * @param simpleType
    * @param baseType
    * @since 4.3
    */
   public void setBasetype(final XSDSimpleTypeDefinition simpleType, final XSDSimpleTypeDefinition baseType);
        
}

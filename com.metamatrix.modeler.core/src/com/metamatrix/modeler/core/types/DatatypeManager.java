/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.types;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * DatatypeManager
 */
public interface DatatypeManager {

    // /**
    // * Set/reset the reference to the <code>DatatypeManager</code> that
    // * is used as the parent manager to be that of the special "built-in"
    // * datatypes manager. The "built-in" datatypes manager allow access
    // * to the set of predefined datatypes. By calling this method
    // * all built-in datatypes now become "inherited" and accessible through
    // * this manager.
    // */
    // void setParentDatatypeManagerToBuiltInManager();
    //    
    // /**
    // * Set a reference to the <code>DatatypeManager</code> to be
    // * used as the parent datatype manager for this manager. All
    // * datatypes known by the parent manager are "inherited" and accessible
    // * through this manager. DatatypeManager instances can be chained
    // * together using this concept to create a hierarchy of datatypes.
    // * @param the DatatypeManager instance to use; may not be null
    // */
    // void setParentDatatypeManager(DatatypeManager parentMgr);

    /**
     * Return a reference to the DatatypeManager used exclusively for built-in datatypes or null if the implementation does not
     * support this concept.
     */
    DatatypeManager getBuiltInTypeManager();

    /**
     * Return the <code>EObject</code> instance representing the ur type of "anySimpleType" as defined in XML Schema.
     * 
     * @return
     */
    EObject getAnySimpleType() throws ModelerCoreException;

    /**
     * Return the <code>EObject</code> instance representing the ur type of "anyType" as defined in XML Schema.
     * 
     * @return
     */
    EObject getAnyType() throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all datatypes known to the manager.
     * 
     * @return
     */
    EObject[] getAllDatatypes() throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all built-in datatypes known to the manager.
     * 
     * @return
     */
    EObject[] getBuiltInDatatypes() throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all datatypes that are allowable values for the specified
     * <code>EStructuralFeature</code>.
     * 
     * @return
     */
    EObject[] getAllowableTypeValues( EObject eObject,
                                      EStructuralFeature feature ) throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all datatypes that are allowable values for the basetype
     * definition feature of an atomic simple type.
     * 
     * @return
     */
    EObject[] getAllowableBaseTypeValues( EObject datatype ) throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all datatypes that are allowable values for the item type
     * definition feature of an list simple type.
     * 
     * @return
     */
    EObject[] getAllowableItemTypeValues( EObject datatype ) throws ModelerCoreException;

    /**
     * Return the array of <code>EObject</code> instances representing all datatypes that are allowable values for the member type
     * definition feature of an union simple type.
     * 
     * @return
     */
    EObject[] getAllowableMemberTypeValues( EObject datatype ) throws ModelerCoreException;

    /**
     * Return the <code>EObject</code> instance representing the default built-in datatype for the specified runtime datatype name
     * or null if a datatype by that name does not exist.
     * 
     * @param the name of the runtime type; may not be null.
     * @return
     */
    EObject getDefaultDatatypeForRuntimeTypeName( String runtimeTypeName ) throws ModelerCoreException;

    /**
     * Return the <code>EObject</code> instance representing the built-in datatype with the specified name or null if a datatype
     * by that name does not exist.
     * 
     * @param the name of the built-in datatype; may not be null.
     * @return
     */
    EObject getBuiltInDatatype( String name ) throws ModelerCoreException;

    /**
     * Returns true if the <code>EObject</code> instance represents a Federate Designer built-in datatype (an extension of the XSD
     * built-in datatypes).
     * 
     * @param the EObject to check; may not be null.
     * @return
     */
    boolean isBuiltInDatatype( EObject datatype );

    /**
     * Return the <code>EObject</code> instance for the datatype with the specified identifier or null if none exists. The
     * identifier may be in the form of a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace 2. the fragment identifier defining the name of the datatype
     * </p>
     * or may represent the string form of an <code>ObjectID</code>
     * 
     * @param the unique identifier of the datatype to return; may not be null.
     * @return the datatype for the specified id or null if a datatype with that id cannot be found.
     */
    EObject findDatatype( String id ) throws ModelerCoreException;

    /**
     * Return the <code>EObject</code> instance for the datatype from the specified XSDSimpleTypeDefinition or AnySimpleType if
     * one can not be determined.
     */
    EObject getDatatypeForXsdType( final EObject xsdType ) throws ModelerCoreException;

    /**
     * Return the runtime type java class name associated with the datatype having the specified identifier or null if no datatype
     * with this identifier exists. The identifier may be in the form of a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace 2. the fragment identifier defining the name of the datatype
     * </p>
     * or may represent the string form of an <code>ObjectID</code>
     * 
     * @param the unique identifier of the datatype; may not be null.
     * @return the name of the java class or null if the it does not exist.
     */
    String getRuntimeTypeJavaClassName( String id ) throws ModelerCoreException;

    /**
     * Return the name of the runtime type associated with the datatype having the specified identifier or null if no datatype
     * with this identifier exists. The identifier may be in the form of a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace 2. the fragment identifier defining the name of the datatype
     * </p>
     * or may represent the string form of an <code>ObjectID</code>
     * 
     * @param the unique identifier of the datatype; may not be null.
     * @return the name of the runtime type or null if the runtime type does not exist.
     */
    String getRuntimeTypeName( String id ) throws ModelerCoreException;

    /**
     * Return the name of the runtime type associated with the specified datatype.
     * 
     * @param the datatype whose runtime type is requested; may not be null
     * @return the name of the runtime data access or null if the runtime type does not exist.
     */
    String getRuntimeTypeName( EObject datatype );

    /**
     * Return whether the runtime type can be changed by subclasses.
     * 
     * @param the datatype whose runtime type fixed value is requested; may not be null
     * @return the boolean wrapper for the runtime datatypr
     */
    Boolean getRuntimeTypeFixed( EObject datatype );

    /**
     * Get the <code>EObject</code> instance representing the data type that the specified simple data type is derived from. If
     * the specified simple data type is considered a built-in primative data type, then there is no base type and null is
     * returned.
     * 
     * @param the simple data type whose base type is requested
     * @return the base type reference or null if this a built-in primative type.
     */
    EObject getBaseType( EObject datatype );

    /**
     * Get the array <code>EObject</code> instances for all datatypes that extend/restrict the the specified datatype. If none
     * exist then an empty collection is returned.
     * 
     * @param the datatype
     * @return the collection of datatypes that extend/restrict this type
     */
    EObject[] getSubtypes( EObject datatype ) throws ModelerCoreException;

    /**
     * Get the array <code>EObject</code> instances for all datatypes that are built-in primative datatypes.
     * 
     * @return the collection of all primitive types
     */
    EObject[] getBuiltInPrimitiveTypes() throws ModelerCoreException;

    /**
     * Return true if the given EObject is and instanceof XSDSimpleTypeDefinition
     * 
     * @param type
     * @return true if the given EObject is and instanceof XSDSimpleTypeDefinition
     */
    boolean isSimpleDatatype( EObject type );

    /**
     * Return the UUID for a given Datatype.
     * 
     * @param type
     * @return the UUID for the given Datatype
     */
    ObjectID getUuid( EObject type );

    /**
     * Return the UUID String for a given Datatype.
     * 
     * @param type
     * @return the Stringified UUID for the given Datatype
     */
    String getUuidString( EObject type );

    /**
     * Return a map consisting of name value pairs defining extension properties for this datatype. If no extension properties
     * exist then an empty map is returned.
     * 
     * @param eObject The <code>EObject</code> to check
     * @return Map of extension properties
     */
    Map getEnterpriseExtensionsMap( EObject type );

    /**
     * Return the name for the given Type
     * 
     * @param type
     * @return the name for the given Type
     */
    String getName( EObject type );

    /**
     * Return the <code>EObject</code> instance representing the built-in primitive datatype from which this datatype extends or
     * restricts.
     * 
     * @param eObject The datatype whose primitive type we want to return; may not be null
     * @return
     */
    EObject getBuiltInPrimitiveType( EObject type );

    /**
     * Return true if, for the specified <code>EObject</code> instance representing a simple datatype, the datatype is numeric
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    boolean isNumeric( EObject type );

    /**
     * Return true if, for the specified <code>EObject</code> instance representing a simple datatype, the datatype is numeric
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    boolean isEnumeration( EObject type );

    /**
     * Return true if, for the specified <code>EObject</code> instance representing a simple datatype, extends or restricts the
     * built-in primitive type of "decimal"
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    boolean isBounded( EObject type );

    /**
     * Return true if, for the specified <code>EObject</code> instance representing a simple datatype, extends or restricts the
     * built-in primitive type of "string"
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    boolean isCharacter( EObject type );

    /**
     * Return true if, for the specified <code>EObject</code> instance representing a simple datatype, extends or restricts one of
     * the built-in types of "object", "blob", or "clob"
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    boolean isBinary( EObject type );

    /**
     * Return the description associated with the specified <code>EObject</code> instance representing a simple datatype. If a
     * description does not exist and empty string is returned.
     * 
     * @param eObject The <code>EObject</code> to check; may not be null
     * @return
     */
    String getDescription( EObject type );

    /**
     * Return whether or not this <code>EObject</code> is an enterprise datatype
     * 
     * @param simpleType The <code>EObject</code> to check; may not be null
     * @return boolean
     */
    boolean isEnterpriseDatatype( EObject simpleType );

    /**
     * Return the <code>EnterpriseDatatypeInfo</code> for this<code>XSDSimpleTypeDefinition</code>.
     * 
     * @param simpleType The <code>XSDSimpleTypeDefinition</code> to check; may not be null
     * @return the <code>EnterpriseDatatypeInfo</code> for the enterprise datatype; if this is not an enterprise datatype, null is
     *         returned.
     */
    EnterpriseDatatypeInfo getEnterpriseDatatypeInfo( XSDSimpleTypeDefinition simpleType );

    /**
     * Set the basetype definition on the <code>XSDSimpleTypeDefinition</code>. Add an import and namespace declaration if
     * necessary
     * 
     * @param simpleType
     * @param baseType
     */
    void setBasetypeDefinition( final XSDSimpleTypeDefinition simpleType,
                                final XSDSimpleTypeDefinition baseType );

    ResourceSet getContainer();

    EObject[] getTypeHierarchy( final EObject type );
}

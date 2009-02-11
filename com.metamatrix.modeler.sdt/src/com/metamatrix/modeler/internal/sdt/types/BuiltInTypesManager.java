/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.sdt.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

/**
 * DatatypeManagerImpl
 */
public class BuiltInTypesManager extends AbstractDatatypeManager {

    // http://www.w3.org/2001/XMLSchema
    protected static final String XSD_SCHEMA_URI_STRING = ModelerCore.XML_SCHEMA_GENERAL_URI;
    // http://www.w3.org/2001/MagicXMLSchema
    protected static final String MAGIC_SCHEMA_URI_STRING = ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI;

    // platform:/plugin/org.eclipse.xsd_1.1.1/cache/www.w3.org/2001/MagicXMLSchema.xsd#//anyType;XSDSimpleTypeDefinition
    protected static final String ANY_TYPE_NAME = "anyType"; //$NON-NLS-1$
    protected static final String ANY_TYPE_URI_STRING = ModelerCore.XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI
                                                        + "#//anyType;XSDSimpleTypeDefinition"; //$NON-NLS-1$
    protected static final URI ANY_TYPE_URI = URI.createURI(ANY_TYPE_URI_STRING);

    // platform:/plugin/org.eclipse.xsd_1.1.1/cache/www.w3.org/2001/MagicXMLSchema.xsd#//anySimpleType;XSDSimpleTypeDefinition=1
    protected static final String ANY_SIMPLE_TYPE_NAME = "anySimpleType"; //$NON-NLS-1$
    protected static final String ANY_SIMPLE_TYPE_URI_STRING = ModelerCore.XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI
                                                               + "#//anySimpleType;XSDSimpleTypeDefinition=1"; //$NON-NLS-1$
    protected static final URI ANY_SIMPLE_TYPE_URI = URI.createURI(ANY_SIMPLE_TYPE_URI_STRING);

    /**
     * Defines the expected built-in datatypes target namespace - must be consistent with the values found in
     * com.metamatrix.modeler.sdt plugin.xml
     */
    public static final String BUILTIN_DATATYPES_URI_STRING = DatatypeConstants.BUILTIN_DATATYPES_URI;
    public static final URI BUILTIN_DATATYPES_URI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);

    // Map, keyed on datatype name, of EMF's built-in datatypes as defined in the org.eclipse.xsd plugin.
    Map emfDatatypeMap;
    // Map, keyed on datatype name, of built-in datatypes as defined in the modeler.sdt plugin.
    Map mmDatatypeMap;
    // Map, keyed on uuid string, of built-in datatypes as defined in the modeler.sdt plugin.
    private Map uuidToMmTypeMap;

    // DEFECT 23839 - Maps keyed to mmType. Caching these types improves performance because getting the type from the SqlAspect
    // is more
    // expensive
    private Map mmTypeToUuidStringMap;
    private Map mmTypeToRuntimeTypeNameMap;

    // References to the ur-types
    private EObject anyType;
    private EObject anySimpleType;

    // The unmodifiable list of built-in primitive types
    private List primitiveTypes;

    // The Emf and Teiid Designer resources for the built-in datatype model
    private Resource emfResource;
    private Resource mmResource;
    private boolean hasEMFEnterpriseInfoInit;

    /**
     * Construct an instance of DatatypeManagerImpl.
     */
    public BuiltInTypesManager() {
        super();
    }

    @Override
    protected void doInitialize() {
        init();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInTypeManager()
     * @since 4.3
     */
    public DatatypeManager getBuiltInTypeManager() {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    public ObjectID getUuid( final EObject type ) {
        // Use the Teiid Designer built-in datatype reference to get the UUID
        final EObject mmType = this.getMmType(type);
        final SqlDatatypeAspect aspect = getSqlAspect(mmType);
        if (aspect != null) {
            return (ObjectID)aspect.getObjectID(mmType);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUuidString(org.eclipse.emf.ecore.EObject)
     */
    public String getUuidString( EObject type ) {
        String uuidString = null;

        // Use the Teiid Designer built-in datatype reference to get the UUID

        final EObject mmType = this.getMmType(type);
        // Defect 23839 - check for cached type-UUID
        uuidString = (String)mmTypeToUuidStringMap.get(mmType);
        // Defect 23839 - if NOT cached, get the aspect, then UUID and add to cache.
        if (uuidString == null) {
            final SqlDatatypeAspect aspect = getSqlAspect(mmType);
            if (aspect != null) {
                uuidString = aspect.getUuidString(mmType);
                mmTypeToUuidStringMap.put(mmType, uuidString);
            }
        }

        return uuidString;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeTypeName( final EObject type ) {
        // If the datatype is a ur-type return a predefined runtime type
        if (type == this.anySimpleType || type == this.anyType) {
            return DatatypeConstants.RuntimeTypeNames.OBJECT;
        }

        // Use the Teiid Designer built-in datatype reference to get the runtime type
        final EObject mmType = this.getMmType(type);
        // Defect 23839 - check for cached type-runtimeType
        String theRuntimeTypeName = (String)mmTypeToRuntimeTypeNameMap.get(mmType);
        // Defect 23839 - if NOT found, get the aspect, runtimeType name and put in map
        if (theRuntimeTypeName == null) {
            final SqlDatatypeAspect aspect = getSqlAspect(mmType);
            if (aspect != null) {
                theRuntimeTypeName = aspect.getRuntimeTypeName(mmType);
                mmTypeToRuntimeTypeNameMap.put(mmType, theRuntimeTypeName);
            }
        }

        return theRuntimeTypeName;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     */
    public Boolean getRuntimeTypeFixed( final EObject type ) {
        Boolean result = null;
        // If the datatype is a ur-type return a predefined runtime type
        if (type == this.anySimpleType || type == this.anyType) {
            result = Boolean.FALSE;
        }
        // Use the Teiid Designer built-in datatype reference to get the runtime type
        final EObject mmType = this.getMmType(type);
        final SqlDatatypeAspect aspect = getSqlAspect(mmType);
        if (aspect != null) {
            result = aspect.getRuntimeTypeFixed(mmType);
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseExtensionsMap(org.eclipse.emf.ecore.EObject)
     */
    public Map getEnterpriseExtensionsMap( EObject type ) {
        // Use the Teiid Designer built-in datatype reference to get the extension map
        final EObject mmType = this.getMmType(type);
        final SqlDatatypeAspect aspect = getSqlAspect(mmType);
        if (aspect != null) {
            return aspect.getEnterpriseExtensionsMap(mmType);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject type ) {
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            return aspect.getName(type);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isSimpleDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatype( EObject type ) {
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            return aspect.isSimpleDatatype(type);
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAnySimpleType()
     */
    public EObject getAnySimpleType() {
        if (this.anySimpleType == null) {
            this.anySimpleType = XSDSchemaImpl.getGlobalResourceSet().getEObject(ANY_SIMPLE_TYPE_URI, true);
        }
        return this.anySimpleType;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAnyType()
     */
    public EObject getAnyType() {
        if (this.anyType == null) {
            this.anyType = XSDSchemaImpl.getGlobalResourceSet().getEObject(ANY_TYPE_URI, true);
        }
        return this.anyType;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDefaultDatatypeForRuntimeTypeName(java.lang.String)
     */
    public EObject getDefaultDatatypeForRuntimeTypeName( final String runtimeTypeName ) {
        final String builtInTypeName = DatatypeConstants.getDatatypeNamefromRuntimeType(runtimeTypeName);
        if (builtInTypeName != null) {
            return this.getBuiltInDatatype(builtInTypeName);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBuiltInDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBuiltInDatatype( final EObject datatype ) {
        final SqlDatatypeAspect aspect = getSqlAspect(datatype);
        if (aspect != null) {
            return aspect.isBuiltInDatatype(datatype);
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBaseType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getBaseType( final EObject datatype ) {
        final SqlDatatypeAspect aspect = getSqlAspect(datatype);
        if (aspect != null) {

            // If the entity is either anyType or anySimpleType then return null
            if (aspect.isURType(datatype)) {
                return null;
            }

            // If the basetype has no identity then return anySimpleType
            XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition)datatype;
            XSDSimpleTypeDefinition basetype = (XSDSimpleTypeDefinition)aspect.getBasetype(entity);

            try {

                // If the datatype is of a list variety then return the itemtype as the basetype
                final XSDVariety variety = entity.getVariety();
                if (variety == XSDVariety.LIST_LITERAL) {
                    basetype = entity.getItemTypeDefinition();
                }
                // If the datatype is of a union variety then return "anySimpleType" as the basetype
                else if (variety == XSDVariety.UNION_LITERAL) {
                    basetype = (XSDSimpleTypeDefinition)this.getAnySimpleType();
                }

                // If the basetype is null then return "anySimpleType" as the basetype
                if (basetype == null) {
                    basetype = (XSDSimpleTypeDefinition)this.getAnySimpleType();
                }
                // If the basetype is anonymous then return "anySimpleType" as the basetype
                else if (basetype.getName() == null) {
                    basetype = (XSDSimpleTypeDefinition)this.getAnySimpleType();
                }
                if (XSDConstants.isAnySimpleType(basetype)) {
                    basetype = (XSDSimpleTypeDefinition)this.getAnySimpleType();
                }
            } catch (Throwable e) {
                ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                          e,
                                          ModelerSdtPlugin.Util.getString("DatatypeManagerImpl.Error_retrieving_the_basetype_for_datatype_1", datatype)); //$NON-NLS-1$
                basetype = null;
            }

            // If the basetype is a built-in datatype then return the EMF XSD built-in
            // instead of the Teiid Designer built-in type.
            final XSDSimpleTypeDefinition emfType = (XSDSimpleTypeDefinition)this.getEmfType(basetype);
            if (emfType != null) {
                basetype = emfType;
            }

            return basetype;
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDatatypeForXsdType
     */
    public EObject getDatatypeForXsdType( final EObject eObject ) {
        ArgCheck.isNotNull(eObject);

        // If the object is a simple type ...
        if (eObject instanceof XSDSimpleTypeDefinition) {
            return getDatatypeForXsdType((XSDSimpleTypeDefinition)eObject);
        }

        // Check if the object is a complex type ...
        if (eObject instanceof XSDComplexTypeDefinition) {
            return getDatatypeForXsdType((XSDComplexTypeDefinition)eObject);
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllDatatypes()
     */
    public EObject[] getAllDatatypes() {
        // Add "anySimpleType" to the top of the results
        List tmp = new ArrayList();
        tmp.add(this.getAnySimpleType());

        // Added in all the EMF XSD built-in datatypes and the Teiid Designer
        // extensions to the built-in dataytpes
        for (Iterator iter = this.mmDatatypeMap.values().iterator(); iter.hasNext();) {
            final XSDTypeDefinition mmType = (XSDTypeDefinition)iter.next();
            final String mmTypeName = mmType.getName().toLowerCase();
            final XSDTypeDefinition emfType = (XSDTypeDefinition)this.emfDatatypeMap.get(mmTypeName);
            if (emfType != null) {
                tmp.add(emfType);
            } else {
                tmp.add(mmType);
            }
        }

        // Remove any duplicates
        removeDuplicates(tmp);

        // Sort the results by name
        sortByName(tmp);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableBaseTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableBaseTypeValues( final EObject datatype ) {
        // Get the array of all possible datatypes
        List tmp = new ArrayList();
        tmp.addAll(Arrays.asList(this.getAllDatatypes()));

        // Remove the ur-type "anySimpleType" which is not allowed as a
        // basetype outside the schema of schemas
        tmp.remove(this.getAnySimpleType());
        // Remove the datatype itself as an allowable basetype
        tmp.remove(datatype);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableItemTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableItemTypeValues( final EObject datatype ) {
        // Get the array of all possible datatypes
        List tmp = new ArrayList();
        tmp.addAll(Arrays.asList(this.getAllDatatypes()));

        // Remove the datatype itself as an allowable basetype
        tmp.remove(datatype);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableMemberTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableMemberTypeValues( final EObject datatype ) {
        // Get the array of all possible datatypes
        List tmp = new ArrayList();
        tmp.addAll(Arrays.asList(this.getAllDatatypes()));

        // Remove the datatype itself as an allowable basetype
        tmp.remove(datatype);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableTypeValues(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    public EObject[] getAllowableTypeValues( final EObject eObject,
                                             final EStructuralFeature feature ) {
        if (feature == null || eObject == null) {
            return this.getAllDatatypes();
        } else if (eObject instanceof XSDSimpleTypeDefinition
                   && feature.getFeatureID() == XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__BASE_TYPE_DEFINITION) {
            return this.getAllowableBaseTypeValues(eObject);
        } else if (eObject instanceof XSDSimpleTypeDefinition
                   && feature.getFeatureID() == XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__ITEM_TYPE_DEFINITION) {
            return this.getAllowableItemTypeValues(eObject);
        } else if (eObject instanceof XSDSimpleTypeDefinition
                   && feature.getFeatureID() == XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__MEMBER_TYPE_DEFINITIONS) {
            return this.getAllowableMemberTypeValues(eObject);
        }
        return this.getAllDatatypes();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatype(java.lang.String)
     */
    public EObject getBuiltInDatatype( final String name ) {
        ArgCheck.isNotNull(name);
        String id = name;

        // If the name is the URI of an XMLSchema built-in datatype like
        // "http://www.w3.org/2001/XMLSchema#string" then extract the name
        // of the built-in datatype ...
        if (id.startsWith(XSD_SCHEMA_URI_STRING) && id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) >= 0) {
            int beginIndex = id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) + 1;
            id = id.substring(beginIndex);
            if (id.startsWith("//") && id.indexOf(';') > 0) { //$NON-NLS-1$
                id = id.substring(2, id.indexOf(';'));
            }
        }

        // If the name is the URI of an XMLSchema built-in datatype like
        // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance#string"
        // then extract the name of the built-in datatype ...
        if (id.startsWith(BUILTIN_DATATYPES_URI_STRING) && id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) >= 0) {
            int beginIndex = id.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) + 1;
            id = id.substring(beginIndex);
            if (id.startsWith("//") && id.indexOf(';') > 0) { //$NON-NLS-1$
                id = id.substring(2, id.indexOf(';'));
            }
        }

        // If the name is one of the ur-types, "anyType" or "anySimpleType" ...
        if (id.equalsIgnoreCase(ANY_TYPE_NAME)) {
            return this.getAnyType();
        }
        if (id.equalsIgnoreCase(ANY_SIMPLE_TYPE_NAME)) {
            return this.getAnySimpleType();
        }

        // Lookup the Teiid Designer built-in datatype by UUID
        EObject mmType = null;
        if (id.startsWith(UUID.PROTOCOL)) {
            mmType = (EObject)this.uuidToMmTypeMap.get(id);
        }
        // Lookup the Teiid Designer built-in datatype by name
        else {
            mmType = (EObject)this.mmDatatypeMap.get(id.toLowerCase());
        }

        // Get the corresponding EMF XSD built-in datatype ...
        if (mmType != null) {
            return this.getEmfType(mmType);
        }
        return mmType;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#findDatatype(java.lang.String)
     */
    public EObject findDatatype( final String id ) {
        // Check the built-in types manager first ...
        EObject result = this.getBuiltInDatatype(id);
        if (result != null) {
            return result;
        }

        // Try to find an EObject with this identifier in the built-in datatype resources
        EObject rv = this.findEObject(id);
        if (rv instanceof XSDSimpleTypeDefinition) {
            return rv;
        } // endif

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeJavaClassName(java.lang.String)
     */
    public String getRuntimeTypeJavaClassName( final String id ) {
        // Use the Teiid Designer built-in datatype reference to get the java class name
        final EObject type = this.getBuiltInDatatype(id);
        final EObject mmType = this.getMmType(type);
        final SqlDatatypeAspect aspect = getSqlAspect(mmType);
        if (aspect != null) {
            return aspect.getJavaClassName(mmType);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(java.lang.String)
     */
    public String getRuntimeTypeName( final String id ) {
        final EObject type = this.getBuiltInDatatype(id);
        return this.getRuntimeTypeName(type);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getSubtypes(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getSubtypes( final EObject datatype ) {
        List tmp = new ArrayList();

        final EObject[] allTypes = this.getAllDatatypes();
        for (int i = 0; i < allTypes.length; i++) {
            final EObject type = allTypes[i];
            final EObject baseType = this.getBaseType(type);
            if (baseType == datatype) {
                tmp.add(type);
            }
        }

        // If the basetype is "anyType" add in "anySimpleType" as a sub-type
        if (datatype == this.getAnyType()) {
            if (!tmp.contains(this.getAnySimpleType())) {
                tmp.add(this.getAnySimpleType());
            }
        }

        // Remove any duplicates
        removeDuplicates(tmp);

        // Sort the results by name
        sortByName(tmp);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveTypes()
     */
    public EObject[] getBuiltInPrimitiveTypes() {
        final List primTypes = this.getPrimitiveTypesList();
        return (EObject[])primTypes.toArray(new EObject[primTypes.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getBuiltInPrimitiveType( final EObject type ) {
        ArgCheck.isNotNull(type);

        // While the datatype type is not a built-in primitive type ...
        final List primTypes = this.getPrimitiveTypesList();
        EObject simpleType = type;
        while (!primTypes.contains(simpleType)) {
            final EObject baseType = this.getBaseType(simpleType);
            if (simpleType == baseType) {
                return null; // this would end up being recursion ...
            }
            simpleType = baseType;
        }

        return simpleType;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBinary( final EObject type ) {
        ArgCheck.isNotNull(type);
        XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)this.getExtendedBuiltInBaseType(type);
        if (simpleType != null) {
            final String typeName = simpleType.getName();
            if (DatatypeConstants.BuiltInNames.OBJECT.equals(typeName) || DatatypeConstants.BuiltInNames.BLOB.equals(typeName)
                || DatatypeConstants.BuiltInNames.CLOB.equals(typeName)) {
                return true;
            }
        }
        simpleType = (XSDSimpleTypeDefinition)this.getBuiltInPrimitiveType(type);
        if (simpleType != null) {
            final String typeName = simpleType.getName();
            if (DatatypeConstants.BuiltInNames.BASE64_BINARY.equals(typeName)
                || DatatypeConstants.BuiltInNames.HEX_BINARY.equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isCharacter(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCharacter( final EObject type ) {
        ArgCheck.isNotNull(type);
        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)this.getBuiltInPrimitiveType(type);
        if (simpleType != null && DatatypeConstants.BuiltInNames.STRING.equals(simpleType.getName())) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isNumeric( final EObject type ) {
        ArgCheck.isNotNull(type);
        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)this.getBuiltInPrimitiveType(type);
        if (simpleType != null && simpleType.getNumericFacet().isValue()) {
            return true;
        }
        return false;
    }

    public boolean isBounded( final EObject type ) {
        ArgCheck.isNotNull(type);
        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)this.getBuiltInPrimitiveType(type);
        if (simpleType != null && simpleType.getBoundedFacet().isValue()) {
            return true;
        }
        return false;
    }

    public boolean isEnumeration( final EObject type ) {
        ArgCheck.isNotNull(type);
        if (type instanceof XSDSimpleTypeDefinition) {
            final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)type;
            if (simpleType.getEnumerationFacets().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription( final EObject type ) {
        String description = null;
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            description = aspect.getDescription(type);
        }
        return (description == null ? StringUtil.Constants.EMPTY_STRING : description);
    }

    public EObject getDatatypeForXsdType( final XSDSimpleTypeDefinition simpleType ) {
        ArgCheck.isNotNull(simpleType);

        // While the simple type is not a built-in simple type (i.e., one in the schema of schemas) ...
        EObject builtInType = simpleType;
        while (!this.isBuiltInDatatype(builtInType)) {
            final EObject baseType = this.getBaseType(simpleType);
            if (builtInType == baseType) {
                builtInType = null;
                break; // this would end up being recursion ...
            }
            builtInType = baseType;
        }

        // Should only be null if its a simple type ...
        if (builtInType == null) {
            return getAnySimpleType();
        }

        // Here, the type is a built-in, so find the appropriate Datatype ...
        return this.getBuiltInDatatype(((XSDSimpleTypeDefinition)builtInType).getName());
    }

    public EObject getDatatypeForXsdType( final XSDComplexTypeDefinition complexType ) {
        ArgCheck.isNotNull(complexType);

        // See if this can have character content ...
        // mtkTODO: Implement getDatatypeForXsdType(XSDComplexTypeDefinition)

        // If not, then there is no corresponding SimpleDatatype for a ComplexType with no content ...
        return null;
    }

    /**
     * Return the EObject instance corresponding to the extended built-in type that the specified type restricts. If no extended
     * type exists anywhere in the type hierarchy for this datatype then null is returned.
     * 
     * @param type
     * @return
     */
    public EObject getExtendedBuiltInBaseType( final EObject type ) {
        ArgCheck.isNotNull(type);
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, type);

        // Check the type hierarchy for a extended built-in type ...
        final List mmExtendedTypes = this.getExtendedTypesList();
        final EObject[] typeHierarchy = this.getTypeHierarchy(type);
        for (int i = 0; i != typeHierarchy.length; ++i) {
            if (mmExtendedTypes.contains(typeHierarchy[i])) {
                return typeHierarchy[i];
            }
        }

        return null;
    }

    /**
     * Return the list of all Teiid Designer built-in types. These types represent extensions to the XML Schema of schema
     * built-in types.
     */
    public List getExtendedTypesList() {
        final List mmExtendedTypes = new ArrayList();
        final Collection mmExtendedTypeNames = DatatypeConstants.getMetaMatrixExtendedBuiltInTypeNames();
        for (Iterator iter = mmExtendedTypeNames.iterator(); iter.hasNext();) {
            String extendedTypeName = (String)iter.next();
            EObject extendedType = (EObject)this.mmDatatypeMap.get(extendedTypeName.toLowerCase());
            if (extendedType != null) {
                mmExtendedTypes.add(extendedType);
            }
        }
        return mmExtendedTypes;
    }

    /**
     * @see com.metamatrix.modeler.internal.sdt.types.AbstractDatatypeManager#getDatatypeResources()
     * @since 4.2
     */
    @Override
    protected List getDatatypeResources() {
        if (mmResource != null) {
            return Collections.singletonList(mmResource);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * If the specified XSDTypeDefinition is a built-in datatype then return the EMF XSD built-in datatype as defined in the
     * org.eclipse.xsd plugin. If specified type is not a built-in datatype then null is returned.
     * 
     * @param type
     * @return
     */
    protected EObject getEmfType( final EObject type ) {
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null && aspect.isBuiltInDatatype(type)) {
            // Lookup the EMF XSD built-in datatype by name
            final String typeName = ((XSDTypeDefinition)type).getName();
            final EObject emfType = (EObject)this.emfDatatypeMap.get(typeName.toLowerCase());
            // If found in the map then return the reference
            if (emfType != null) {
                return emfType;
            }
            // If not found in the EMF XSD built-in datatypes map then the
            // type is probably one of the extended built-in datatypes
            return type;
        }
        return null;
    }

    /**
     * If the specified XSDTypeDefinition is a built-in datatype then return the Teiid Designer built-in datatype as defined in
     * the modeler.sdt plugin. If specified type is not a built-in datatype then null is returned.
     * 
     * @param type
     * @return
     */
    protected EObject getMmType( final EObject type ) {
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null && aspect.isBuiltInDatatype(type)) {
            // Lookup the Teiid Designer built-in datatype by name
            final String typeName = ((XSDTypeDefinition)type).getName();
            final EObject emfType = (EObject)this.mmDatatypeMap.get(typeName.toLowerCase());
            // If found in the map then return the reference
            if (emfType != null) {
                return emfType;
            }
        }
        return null;
    }

    /**
     * Return the EObject instance corresponding to the specified identifier string
     * 
     * @param id
     * @return
     * @throws ModelerCoreException
     */
    protected EObject findEObject( final String id ) {
        if (id == null) {
            return null;
        }

        if (this.emfResource instanceof XSDResourceImpl && id.startsWith(UUID.PROTOCOL)) {
            return (EObject)this.uuidToMmTypeMap.get(id);
        }

        EObject result = this.emfResource.getEObject(id);
        if (result == null) {
            result = this.mmResource.getEObject(id);
        }
        return result;
    }

    protected void init() {
        this.primitiveTypes = null;
        this.emfResource = null;
        this.mmResource = null;
        this.emfDatatypeMap = new HashMap();
        this.mmDatatypeMap = new HashMap();
        this.uuidToMmTypeMap = new HashMap();
        this.mmTypeToUuidStringMap = new HashMap();
        this.mmTypeToRuntimeTypeNameMap = new HashMap();
        this.initializeEmfDatatypeMap();
        this.initializeMmDatatypeMap();
        this.initializeUuidToMmTypeMap();

    }

    /**
     * Return the list of built-in primitive types. A primitive type is one whose basetype is one of the ur-types.
     */
    private List getPrimitiveTypesList() {
        if (this.primitiveTypes == null) {
            this.primitiveTypes = new ArrayList();

            try {
                final EObject[] allTypes = this.getAllDatatypes();
                for (int i = 0; i < allTypes.length; i++) {
                    final EObject type = allTypes[i];
                    final EObject baseType = this.getBaseType(type);
                    if (baseType == this.getAnySimpleType() || baseType == this.getAnyType()) {
                        this.primitiveTypes.add(type);
                    }
                }
            } catch (Throwable e) {
                final String msg = ModelerSdtPlugin.Util.getString("BuiltInTypesManager.Error_constructing_the_list_of_built-in_primitive_types_1"); //$NON-NLS-1$
                ModelerSdtPlugin.Util.log(IStatus.ERROR, e, msg);
            }

            // Remove any duplicates
            removeDuplicates(this.primitiveTypes);

            // Sort the results by name
            sortByName(this.primitiveTypes);
        }
        return this.primitiveTypes;
    }

    private void initializeEmfDatatypeMap() {
        // Populate the xsdBuiltInDatatypeMap map with all XML Schema built-in datatypes
        final XSDSchema schema = XSDSchemaImpl.getSchemaForSchema(XSD_SCHEMA_URI_STRING);
        for (Iterator iterator = schema.getContents().iterator(); iterator.hasNext();) {
            final EObject eObject = (EObject)iterator.next();
            if (eObject instanceof XSDSimpleTypeDefinition) {
                final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObject;
                final String typeName = type.getName();
                if (DatatypeConstants.getBuiltInTypeNames().contains(typeName)) {
                    this.emfDatatypeMap.put(typeName.toLowerCase(), type);
                    // } else {
                    // System.out.println("name not found = "+typeName)
                }
            }
        }
        // Set the reference to the Emf resource
        this.emfResource = schema.eResource();
    }

    private void initializeMmDatatypeMap() {
        try {
            // Retrieve the built-in datatype resource from the model container
            final Resource resource = this.getContainer().getResource(BUILTIN_DATATYPES_URI, false);
            if (resource == null) {
                final Object[] params = new Object[] {BUILTIN_DATATYPES_URI};
                final String msg = ModelerSdtPlugin.Util.getString("BuiltInTypesManager.Error_obtain_the_built-in_datatypes_resource_from_the_container_using_URI_1", params); //$NON-NLS-1$
                ModelerSdtPlugin.Util.log(IStatus.ERROR, msg);
            }

            // Set the reference to the Emf resource
            this.mmResource = resource;

            // Populate the sdtBuiltInDatatypeMap map with all built-in datatypes
            // Populate the mmDatatypeMap map with all built-in datatypes
            // keyed by lower case name
            for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                if (eObject != null && eObject instanceof XSDSchema) {

                    // Found the schema now gather all the global simple types ...
                    for (Iterator iter2 = eObject.eContents().iterator(); iter2.hasNext();) {
                        eObject = (EObject)iter2.next();
                        if (eObject != null && eObject instanceof XSDSimpleTypeDefinition) {
                            final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObject;
                            final String typeName = type.getName();
                            this.mmDatatypeMap.put(typeName.toLowerCase(), type);
                        }
                    }

                }
            }
        } catch (Throwable e) {
            final Object[] params = new Object[] {BUILTIN_DATATYPES_URI};
            final String msg = ModelerSdtPlugin.Util.getString("BuiltInTypesManager.Error_obtain_the_built-in_datatypes_resource_from_the_container_using_URI_2", params); //$NON-NLS-1$
            ModelerSdtPlugin.Util.log(IStatus.ERROR, e, msg);
        }
    }

    private void initializeUuidToMmTypeMap() {
        for (Iterator iter = this.mmDatatypeMap.values().iterator(); iter.hasNext();) {
            final EObject type = (EObject)iter.next();
            final String uuid = this.getUuidString(type);
            if (uuid != null) {
                this.uuidToMmTypeMap.put(uuid, type);
            }
        }
    }

    private void initializeEnterpriseDataForEmfResource() {
        hasEMFEnterpriseInfoInit = true;
        // defect 19183 -- These changes are not undoable. Wrap them
        // in their own transaction to prevent 5000 undo entries from
        // getting created:
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) {
                for (Iterator iter = mmDatatypeMap.values().iterator(); iter.hasNext();) {
                    final XSDSimpleTypeDefinition mmType = (XSDSimpleTypeDefinition)iter.next();
                    final String typeName = mmType.getName().toLowerCase();
                    final XSDSimpleTypeDefinition emfType = (XSDSimpleTypeDefinition)emfDatatypeMap.get(typeName);

                    if (emfType != null) {
                        if (!getSqlAspect(mmType).isEnterpriseDataType(emfType)) {
                            EnterpriseDatatypeInfo edt = new EnterpriseDatatypeInfo(getUuidString(mmType),
                                                                                    getRuntimeTypeName(mmType),
                                                                                    getRuntimeTypeFixed(mmType));
                            getSqlAspect(mmType).setEnterpriseDataAttributes(emfType, edt);
                        } // endif -- is Enterprise
                    } // endif -- not null
                } // endfor -- over built-in types
                return null;
            }
        }; // endanon transaction

        try {
            ModelerCore.getModelEditor().executeAsTransaction(runnable,
                                                              "updating built-in type enterprise info", false, false, this); //$NON-NLS-1$ // not visible to users...
        } catch (ModelerCoreException mce) {
            ModelerSdtPlugin.Util.log(mce);
        } // endtry

    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatypes()
     * @since 4.3
     */
    public EObject[] getBuiltInDatatypes() {
        return getAllDatatypes();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isEnterpriseDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isEnterpriseDatatype( EObject simpleType ) {
        final SqlDatatypeAspect aspect = getSqlAspect(simpleType);
        if (aspect != null) {
            if (!hasEMFEnterpriseInfoInit) {
                initializeEnterpriseDataForEmfResource();
            } // endif
            return aspect.isEnterpriseDataType(simpleType);
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo( XSDSimpleTypeDefinition simpleType ) {
        if (!hasEMFEnterpriseInfoInit) {
            initializeEnterpriseDataForEmfResource();
        } // endif
        return getSqlAspect(simpleType).getEnterpriseDatatypeInfo(simpleType);
    }

    /**
     * Should not allow modification of built-in datatypes
     * 
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setBasetypeDefinition(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public void setBasetypeDefinition( final XSDSimpleTypeDefinition simpleType,
                                       final XSDSimpleTypeDefinition baseType ) {
        throw new UnsupportedOperationException();
    }
}

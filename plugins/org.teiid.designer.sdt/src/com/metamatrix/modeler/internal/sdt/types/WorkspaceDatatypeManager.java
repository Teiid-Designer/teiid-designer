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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.id.IDGenerator;
import org.teiid.core.id.InvalidIDException;
import org.teiid.core.id.ObjectID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

/**
 * WorkspaceDatatypeManager
 */
public class WorkspaceDatatypeManager extends AbstractDatatypeManager {

    private BuiltInTypesManager builtInTypesMgr;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of WorkspaceDatatypeManager.
     */
    public WorkspaceDatatypeManager() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.internal.sdt.types.AbstractDatatypeManager#doInitialize()
     * @since 4.2
     */
    @Override
    protected void doInitialize() throws ModelerCoreException {
        init();
    }

    private void init() throws ModelerCoreException {
        this.builtInTypesMgr = new BuiltInTypesManager();
        this.builtInTypesMgr.initialize(this.getContainer());
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInTypeManager()
     * @since 4.3
     */
    public DatatypeManager getBuiltInTypeManager() {
        return this.builtInTypesMgr;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatype(java.lang.String)
     */
    public EObject getBuiltInDatatype( final String name ) {
        return this.builtInTypesMgr.getBuiltInDatatype(name);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveTypes()
     */
    public EObject[] getBuiltInPrimitiveTypes() {
        return this.builtInTypesMgr.getBuiltInPrimitiveTypes();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getBuiltInPrimitiveType( EObject type ) {
        return this.builtInTypesMgr.getBuiltInPrimitiveType(type);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBinary( EObject type ) {
        return this.builtInTypesMgr.isBinary(type);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isCharacter(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCharacter( EObject type ) {
        return this.builtInTypesMgr.isCharacter(type);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isNumeric( EObject type ) {
        return this.builtInTypesMgr.isNumeric(type);
    }

    public boolean isBounded( EObject type ) {
        return this.builtInTypesMgr.isBounded(type);
    }

    public boolean isEnumeration( EObject type ) {
        return this.builtInTypesMgr.isEnumeration(type);
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    public ObjectID getUuid( final EObject type ) {
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(type)) {
            return this.builtInTypesMgr.getUuid(type);
        }

        // Return the UUID from the aspect
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            final Object uuid = aspect.getObjectID(type);
            if (uuid instanceof ObjectID) {
                return (ObjectID)uuid;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUuidString(org.eclipse.emf.ecore.EObject)
     */
    public String getUuidString( EObject type ) {
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(type)) {
            return this.builtInTypesMgr.getUuidString(type);
        }

        // Return the UUID string from the aspect
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            return aspect.getUuidString(type);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeTypeName( final EObject type ) {
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(type)) {
            return this.builtInTypesMgr.getRuntimeTypeName(type);
        }

        // Return the runtime type name from the aspect
        SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            EObject dtype = type;
            String result = aspect.getRuntimeTypeName(type);
            while (result == null || result.length() == 0) {
                // Go to the base type ...
                EObject basetype = (EObject)aspect.getBasetype(dtype);
                // If basetype is itself, break out of while loop
                if (basetype == dtype) {
                    break;
                }

                // If basetype is a ur-type... return string. Else if basetype is a built-in type ...
                if (aspect.isURType(basetype)) {
                    result = DatatypeConstants.RuntimeTypeNames.STRING;
                    break;
                } else if (aspect.isBuiltInDatatype(basetype)) {
                    result = aspect.getRuntimeTypeName(basetype);
                    break;
                }
                result = aspect.getRuntimeTypeName(basetype);
                dtype = basetype;
            }
            return result;

        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Boolean getRuntimeTypeFixed( EObject type ) {
        Boolean result = null;
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(type)) {
            result = this.builtInTypesMgr.getRuntimeTypeFixed(type);
        } else {
            // Return the runtime fixed value from the aspect
            SqlDatatypeAspect aspect = getSqlAspect(type);
            if (aspect != null) {
                result = aspect.getRuntimeTypeFixed(type);
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseExtensionsMap(org.eclipse.emf.ecore.EObject)
     */
    public Map getEnterpriseExtensionsMap( final EObject type ) {
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(type)) {
            return this.builtInTypesMgr.getEnterpriseExtensionsMap(type);
        }

        // Return the runtime type name from the aspect
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            return aspect.getEnterpriseExtensionsMap(type);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( final EObject type ) {
        final SqlDatatypeAspect aspect = getSqlAspect(type);
        if (aspect != null) {
            return aspect.getName(type);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isSimpleDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatype( final EObject type ) {
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
        return this.builtInTypesMgr.getAnySimpleType();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAnyType()
     */
    public EObject getAnyType() {
        return this.builtInTypesMgr.getAnyType();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDefaultDatatypeForRuntimeTypeName(java.lang.String)
     */
    public EObject getDefaultDatatypeForRuntimeTypeName( final String runtimeTypeName ) {
        return this.builtInTypesMgr.getDefaultDatatypeForRuntimeTypeName(runtimeTypeName);
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
        // If the datatype is a built-in type ...
        if (this.isBuiltInDatatype(datatype)) {
            return this.builtInTypesMgr.getBaseType(datatype);
        }

        // Get the type from the aspect ...
        final SqlDatatypeAspect aspect = getSqlAspect(datatype);
        if (aspect != null) {
            return (EObject)aspect.getBasetype(datatype);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDatatypeForXsdType
     */
    public EObject getDatatypeForXsdType( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);

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
        EObject[] result = this.getDatatypes();

        // Create a list of results
        List tmp = new ArrayList();
        tmp.addAll(Arrays.asList(result));

        // Add built-in datatypes to the list
        tmp.addAll(Arrays.asList(this.builtInTypesMgr.getAllDatatypes()));

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
     * @see com.metamatrix.modeler.core.types.DatatypeManager#findDatatype(java.lang.String)
     */
    public EObject findDatatype( final String id ) {
        // Check the built-in types manager first ...
        EObject result = this.builtInTypesMgr.findDatatype(id);
        if (result != null) {
            return result;
        }

        // Try to find an EObject with this identifier in the workspace
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
        // Check the built-in types manager first ...
        String result = this.builtInTypesMgr.getRuntimeTypeJavaClassName(id);
        if (result != null) {
            return result;
        }

        // Try to find an EObject with this identifier in the workspace
        EObject eObject = this.findDatatype(id);
        if (eObject != null) {
            final SqlDatatypeAspect aspect = getSqlAspect(eObject);
            if (aspect != null) {
                return aspect.getJavaClassName(eObject);
            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(java.lang.String)
     */
    public String getRuntimeTypeName( final String id ) {
        // Check the built-in types manager first ...
        String result = this.builtInTypesMgr.getRuntimeTypeName(id);
        if (result != null) {
            return result;
        }

        // Try to find an EObject with this identifier in the workspace
        EObject eObject = this.findDatatype(id);
        if (eObject != null) {
            final SqlDatatypeAspect aspect = getSqlAspect(eObject);
            if (aspect != null) {
                return aspect.getJavaClassName(eObject);
            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getSubtypes(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getSubtypes( final EObject datatype ) {
        CoreArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, datatype);

        // Create a collection of only the datatypes with the correct basetype
        List tmp = new ArrayList();

        // If the basetype is the ur-type of "anyType" or "anySimpleType"
        // then add all the built-in primitive types as subtypes
        SqlDatatypeAspect aspect = getSqlAspect(datatype);
        if (aspect != null && aspect.isURType(datatype)) {
            EObject[] subtypes = this.builtInTypesMgr.getBuiltInPrimitiveTypes();
            tmp.addAll(Arrays.asList(subtypes));
        }
        // Else if this basetype is a built-in type ...
        else if (aspect != null && aspect.isBuiltInDatatype(datatype)) {
            EObject[] subtypes = this.builtInTypesMgr.getSubtypes(datatype);
            tmp.addAll(Arrays.asList(subtypes));
        }

        // Check all user-defined datatypes within the workspace ...
        EObject[] datatypes = this.getDatatypes();
        for (int i = 0; i < datatypes.length; i++) {
            XSDSimpleTypeDefinition sdt = (XSDSimpleTypeDefinition)datatypes[i];
            if (sdt != null) {
                EObject basetype = sdt.getBaseTypeDefinition();
                aspect = getSqlAspect(basetype);

                // If the basetype is a built-in type then make sure
                // the EObject reference is to the EMF XSD built-in type
                if (aspect != null && aspect.isBuiltInDatatype(basetype)) {
                    basetype = this.builtInTypesMgr.getEmfType(basetype);
                }

                // If the datatype has the correct basetype ...
                if (datatype.equals(basetype)) {
                    tmp.add(sdt);
                }
            }
        }

        // Remove any duplicates
        removeDuplicates(tmp);

        // Sort the results by name
        sortByName(tmp);

        return (EObject[])tmp.toArray(new EObject[tmp.size()]);
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
        return (description == null ? CoreStringUtil.Constants.EMPTY_STRING : description);
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected EObject getDatatypeForXsdType( final XSDSimpleTypeDefinition simpleType ) {
        CoreArgCheck.isNotNull(simpleType);

        // While the simple type is not a built-in simple type (i.e., one in the schema of schemas) ...
        EObject builtInType = simpleType;
        while (!this.isBuiltInDatatype(builtInType)) {
            final EObject baseType = this.getBaseType(builtInType);
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

    protected EObject getDatatypeForXsdType( final XSDComplexTypeDefinition complexType ) {
        CoreArgCheck.isNotNull(complexType);

        // See if this can have character content ...
        // mtkTODO: Implement getDatatypeForXsdType(XSDComplexTypeDefinition)

        // If not, then there is no corresponding SimpleDatatype for a ComplexType with no content ...
        return null;
    }

    /**
     * Return an array of all SimpleDatatype instances found within the modeler workspace
     */
    protected EObject[] getDatatypes() {
        final List resources = this.getDatatypeResources();
        return this.getDatatypes(resources);
    }

    /**
     * Return an EObject array for all datatypes in the specified resource
     * 
     * @param emfResources the {@link Resource EMF Resources}
     * @return
     */
    protected EObject[] getDatatypes( final List emfResources ) {
        CoreArgCheck.isNotNull(emfResources);

        ArrayList tmp = new ArrayList();
        final Iterator iter = emfResources.iterator();
        while (iter.hasNext()) {
            final Resource resource = (Resource)iter.next();
            tmp.addAll(Arrays.asList(getDatatypes(resource)));
        }
        EObject[] result = new EObject[tmp.size()];
        tmp.toArray(result);

        return result;
    }

    /**
     * Return an EObject array for all datatypes in the specified resource. Only global datatypes will be returned.
     * 
     * @param resource
     * @return
     */
    protected EObject[] getDatatypes( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);

        // Iterate over the resource roots to find the schema instance ...
        ArrayList tmp = new ArrayList();
        for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
            EObject eObj = (EObject)iter.next();
            if (eObj != null && eObj instanceof XSDSchema) {
                XSDSchema schema = (XSDSchema)eObj;
                // Found the schema now gather all the global simple types ...
                for (Iterator iter2 = schema.eContents().iterator(); iter2.hasNext();) {
                    EObject eObj2 = (EObject)iter2.next();
                    if (eObj2 != null && eObj2 instanceof XSDSimpleTypeDefinition) {
                        XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)eObj2;
                        XsdUtil.checkForEnterpriseConversion(simpleType);
                        tmp.add(simpleType);
                    }
                }

            }
        }

        EObject[] result = new EObject[tmp.size()];
        tmp.toArray(result);

        return result;
    }

    /**
     * Return the EObject instance corresponding to the specified identifier string
     * 
     * @param uuidString
     * @return
     * @throws ModelerCoreException
     */
    protected EObject findEObject( final String id ) {
        if (id == null) {
            return null;
        }

        // The identifier is a UUID string, check the proxy manager first ...
        EObject result = null;
        if (containsUuidPattern(id)) {
            String uuidString = extractUuidString(id);
            final ResourceSet rsrcSet = this.getContainer();
            Container container = null;
            if (rsrcSet instanceof Container) {
                container = (Container)this.getContainer();
            }
            // final Container container = this.getContainer();
            if (container != null) {
                try {
                    ObjectID uuid = IDGenerator.getInstance().stringToObject(uuidString);
                    result = (EObject)container.getEObjectFinder().find(uuid);
                } catch (InvalidIDException e) {
                    ModelerSdtPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
            // If found an EObject with this UUID make sure it is a datatype else return null
            if (result != null) {
                return (result instanceof XSDSimpleTypeDefinition ? result : null);
            }
            // Else check all known datatypes for this uuid
            EObject[] allDatatypes = this.getAllDatatypes();
            for (int i = 0; i != allDatatypes.length; ++i) {
                final EObject datatype = allDatatypes[i];
                final SqlDatatypeAspect aspect = getSqlAspect(datatype);
                if (aspect != null && uuidString.equals(aspect.getUuidString(datatype))) {
                    return datatype;
                }
            }
        }

        // If not found, try to lookup the EObject instance in the resource set ...
        List resources = getDatatypeResources();
        final Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            final Resource emfResource = (Resource)iter.next();
            result = emfResource.getEObject(id);
            if (result != null) {
                return (result instanceof XSDSimpleTypeDefinition ? result : null);
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatypes()
     * @since 4.3
     */
    public EObject[] getBuiltInDatatypes() {
        return this.builtInTypesMgr.getBuiltInDatatypes();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isEnterpriseDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isEnterpriseDatatype( EObject simpleType ) {
        return this.builtInTypesMgr.isEnterpriseDatatype(resolveWhenProxy(simpleType));
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo( XSDSimpleTypeDefinition simpleType ) {
        return this.builtInTypesMgr.getEnterpriseDatatypeInfo((XSDSimpleTypeDefinition)resolveWhenProxy(simpleType));
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setBasetypeDefinition(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public void setBasetypeDefinition( final XSDSimpleTypeDefinition simpleType,
                                       final XSDSimpleTypeDefinition baseType ) {
        getSqlAspect(simpleType).setBasetype((XSDSimpleTypeDefinition)resolveWhenProxy(simpleType),
                                             (XSDSimpleTypeDefinition)resolveWhenProxy(baseType));
    }

    private EObject resolveWhenProxy( EObject e ) {
        EObject resolvedEObject = e;
        if (e.eIsProxy()) {
            resolvedEObject = EcoreUtil.resolve(e, getContainer());
            if (resolvedEObject.eIsProxy()) {
                throw new TeiidRuntimeException(
                                                     ModelerSdtPlugin.Util.getString("WorkspaceDatatypeManager.Error_EObject_can_not_be_a_proxy", resolvedEObject.toString())); //$NON-NLS-1$
            }
        }
        return resolvedEObject;
    }

}

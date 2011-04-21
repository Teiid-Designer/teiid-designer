/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.teiid.core.id.IDGenerator;
import org.teiid.core.id.InvalidIDException;
import org.teiid.core.id.ObjectID;
import com.metamatrix.modeler.core.container.Container;

/**
 * FakeDatatypeManager
 */
public class FakeDatatypeManager implements DatatypeManager {

    /**
     * Construct an instance of FakeDatatypeManager.
     */
    public FakeDatatypeManager() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInTypeManager()
     * @since 4.3
     */
    public DatatypeManager getBuiltInTypeManager() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setParentDatatypeManagerToBuiltInManager()
     */
    public void setParentDatatypeManagerToBuiltInManager() {

    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setParentDatatypeManager(com.metamatrix.modeler.core.types.DatatypeManager)
     */
    public void setParentDatatypeManager( DatatypeManager parentMgr ) {

    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isSimpleDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatype( EObject type ) {
        return type instanceof XSDSimpleTypeDefinition;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    public ObjectID getUuid( EObject type ) {
        if (type instanceof XSDSimpleTypeDefinition) {
            XSDAnnotation annotation = ((XSDSimpleTypeDefinition)type).getAnnotation();
            if (annotation == null) {
                return null;
            }

            final Iterator appInfos = annotation.getApplicationInformation().iterator();
            while (appInfos.hasNext()) {
                final Element appInfo = (Element)appInfos.next();
                final String uuid = appInfo.getAttribute("UUID"); //$NON-NLS-1$
                if (uuid != null && uuid.trim().length() > 0) {
                    try {
                        return IDGenerator.getInstance().stringToObject(uuid);
                    } catch (InvalidIDException e) {
                        return null;
                    }
                }

                return null;

            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    public String getUuidString( EObject type ) {
        if (type instanceof XSDSimpleTypeDefinition) {
            XSDAnnotation annotation = ((XSDSimpleTypeDefinition)type).getAnnotation();
            if (annotation == null) {
                return null;
            }

            final Iterator appInfos = annotation.getApplicationInformation().iterator();
            while (appInfos.hasNext()) {
                final Element appInfo = (Element)appInfos.next();
                final String uuid = appInfo.getAttribute("UUID"); //$NON-NLS-1$
                if (uuid != null && uuid.trim().length() > 0) {
                    return uuid;
                }
            }
            return null;
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseExtensionsMap(org.eclipse.emf.ecore.EObject)
     */
    public Map getEnterpriseExtensionsMap( EObject type ) {
        Map result = Collections.EMPTY_MAP;
        if (type instanceof XSDSimpleTypeDefinition) {
            // Get the annotation for the type
            XSDAnnotation annotation = ((XSDSimpleTypeDefinition)type).getAnnotation();
            if (annotation == null) {
                return null;
            }

            // Iterator over the appInfos and add any attributes to the result collection
            result = new HashMap();
            final Iterator appInfos = annotation.getApplicationInformation().iterator();
            while (appInfos.hasNext()) {
                final Element appInfo = (Element)appInfos.next();
                if (appInfo.getAttributes() != null && appInfo.getAttributes().getLength() > 0) {
                    final int length = appInfo.getAttributes().getLength();
                    final NamedNodeMap map = appInfo.getAttributes();
                    for (int i = 0; i < length; i++) {
                        final Node mapNode = map.item(i);
                        if (mapNode != null) {
                            result.put(mapNode.getNodeName(), mapNode.getNodeValue());
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject type ) {
        if (type instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition)type).getName();
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAnySimpleType()
     */
    public EObject getAnySimpleType() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAnyType()
     */
    public EObject getAnyType() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllDatatypes()
     */
    public EObject[] getAllDatatypes() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableBaseTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableBaseTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableItemTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableItemTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableMemberTypeValues(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getAllowableMemberTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getAllowableTypeValues(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    public EObject[] getAllowableTypeValues( EObject datatype,
                                             EStructuralFeature feature ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDefaultDatatypeForRuntimeTypeName(java.lang.String)
     */
    public EObject getDefaultDatatypeForRuntimeTypeName( String runtimeTypeName ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatype(java.lang.String)
     */
    public EObject getBuiltInDatatype( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBuiltInDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBuiltInDatatype( EObject datatype ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#findDatatype(java.lang.String)
     */
    public EObject findDatatype( String id ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDatatypeForXsdType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatypeForXsdType( EObject xsdType ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeJavaClassName(java.lang.String)
     */
    public String getRuntimeTypeJavaClassName( String id ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(java.lang.String)
     */
    public String getRuntimeTypeName( String id ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeTypeName( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBaseType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getBaseType( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getSubtypes(org.eclipse.emf.ecore.EObject)
     */
    public EObject[] getSubtypes( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveTypes()
     */
    public EObject[] getBuiltInPrimitiveTypes() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInPrimitiveType(org.eclipse.emf.ecore.EObject)
     */
    public EObject getBuiltInPrimitiveType( EObject type ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBinary( EObject type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isCharacter(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCharacter( EObject type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isNumeric( EObject type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isEnumeration( EObject type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isBounded(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBounded( EObject type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription( EObject type ) {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getContainer()
     * @since 4.2
     */
    public ResourceSet getContainer() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setContainer(com.metamatrix.modeler.core.container.Container)
     * @since 4.2
     */
    public void setContainer( Container container ) {
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getBuiltInDatatypes()
     * @since 4.3
     */
    public EObject[] getBuiltInDatatypes() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#isEnterpriseDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isEnterpriseDatatype( EObject simpleType ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo( XSDSimpleTypeDefinition simpleType ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Boolean getRuntimeTypeFixed( EObject datatype ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#setBasetypeDefinition(org.eclipse.xsd.XSDSimpleTypeDefinition,
     *      org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public void setBasetypeDefinition( XSDSimpleTypeDefinition simpleType,
                                       XSDSimpleTypeDefinition baseType ) {
    }

    /**
     * @see com.metamatrix.modeler.core.types.DatatypeManager#getTypeHierarchy(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public EObject[] getTypeHierarchy( EObject type ) {
        return new EObject[] {type};
    }

}

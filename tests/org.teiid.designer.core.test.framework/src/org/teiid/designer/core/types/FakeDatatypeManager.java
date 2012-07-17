/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.types;

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
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.types.EnterpriseDatatypeInfo;


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
     * @see org.teiid.designer.core.types.DatatypeManager#getBuiltInTypeManager()
     * @since 4.3
     */
    @Override
	public DatatypeManager getBuiltInTypeManager() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#setParentDatatypeManagerToBuiltInManager()
     */
    public void setParentDatatypeManagerToBuiltInManager() {

    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#setParentDatatypeManager(org.teiid.designer.core.types.DatatypeManager)
     */
    public void setParentDatatypeManager( DatatypeManager parentMgr ) {

    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isSimpleDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSimpleDatatype( EObject type ) {
        return type instanceof XSDSimpleTypeDefinition;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @see org.teiid.designer.core.types.DatatypeManager#getUUID(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @see org.teiid.designer.core.types.DatatypeManager#getEnterpriseExtensionsMap(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @see org.teiid.designer.core.types.DatatypeManager#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName( EObject type ) {
        if (type instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition)type).getName();
        }

        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAnySimpleType()
     */
    @Override
	public EObject getAnySimpleType() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAnyType()
     */
    @Override
	public EObject getAnyType() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAllDatatypes()
     */
    @Override
	public EObject[] getAllDatatypes() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAllowableBaseTypeValues(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject[] getAllowableBaseTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAllowableItemTypeValues(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject[] getAllowableItemTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAllowableMemberTypeValues(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject[] getAllowableMemberTypeValues( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getAllowableTypeValues(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public EObject[] getAllowableTypeValues( EObject datatype,
                                             EStructuralFeature feature ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getDefaultDatatypeForRuntimeTypeName(java.lang.String)
     */
    @Override
	public EObject getDefaultDatatypeForRuntimeTypeName( String runtimeTypeName ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getBuiltInDatatype(java.lang.String)
     */
    @Override
	public EObject getBuiltInDatatype( String name ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isBuiltInDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isBuiltInDatatype( EObject datatype ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#findDatatype(java.lang.String)
     */
    @Override
	public EObject findDatatype( String id ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getDatatypeForXsdType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatypeForXsdType( EObject xsdType ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getRuntimeTypeJavaClassName(java.lang.String)
     */
    @Override
	public String getRuntimeTypeJavaClassName( String id ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getRuntimeTypeName(java.lang.String)
     */
    @Override
	public String getRuntimeTypeName( String id ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getRuntimeTypeName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getRuntimeTypeName( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getBaseType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getBaseType( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getSubtypes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject[] getSubtypes( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getBuiltInPrimitiveTypes()
     */
    @Override
	public EObject[] getBuiltInPrimitiveTypes() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getBuiltInPrimitiveType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getBuiltInPrimitiveType( EObject type ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isBinary(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isBinary( EObject type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isCharacter(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCharacter( EObject type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isNumeric( EObject type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isNumeric(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isEnumeration( EObject type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isBounded(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isBounded( EObject type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getDescription(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDescription( EObject type ) {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getContainer()
     * @since 4.2
     */
    @Override
	public ResourceSet getContainer() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#setContainer(org.teiid.designer.core.container.Container)
     * @since 4.2
     */
    public void setContainer( Container container ) {
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getBuiltInDatatypes()
     * @since 4.3
     */
    @Override
	public EObject[] getBuiltInDatatypes() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#isEnterpriseDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isEnterpriseDatatype( EObject simpleType ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    @Override
	public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo( XSDSimpleTypeDefinition simpleType ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public Boolean getRuntimeTypeFixed( EObject datatype ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#setBasetypeDefinition(org.eclipse.xsd.XSDSimpleTypeDefinition,
     *      org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    @Override
	public void setBasetypeDefinition( XSDSimpleTypeDefinition simpleType,
                                       XSDSimpleTypeDefinition baseType ) {
    }

    /**
     * @see org.teiid.designer.core.types.DatatypeManager#getTypeHierarchy(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public EObject[] getTypeHierarchy( EObject type ) {
        return new EObject[] {type};
    }

}

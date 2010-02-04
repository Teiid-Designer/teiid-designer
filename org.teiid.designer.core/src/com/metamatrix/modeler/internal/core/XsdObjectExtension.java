/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * mmDefect_12555 - Created XsdObjectExtension to specialize the behavior of getting and setting extension properties for XSD
 * resource entities.
 * 
 * @since 4.2
 */
public class XsdObjectExtension extends ObjectExtension {

    public static final String EXTENSION_PACKAGE = "extensionPackage"; //$NON-NLS-1$

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * @since 4.2
     */
    public XsdObjectExtension() {
        super();
    }

    /**
     * @param extendedObject
     * @param xclass
     * @param editor
     * @since 4.2
     */
    public XsdObjectExtension( EObject extendedObject,
                               XClass xclass,
                               ModelEditor editor ) {
        super(extendedObject, xclass, editor);
    }

    // ==================================================================================
    // O V E R R I D D E N M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.internal.core.ObjectExtension#doDynamicSet(org.eclipse.emf.ecore.EStructuralFeature,
     *      java.lang.Object)
     * @since 4.2
     */
    @Override
    protected void doDynamicSet( EStructuralFeature eFeature,
                                 Object newValue ) {

        final EObject eObject = super.getExtendedObject();
        if (eObject != null && eObject instanceof XSDConcreteComponent) {
            final XSDConcreteComponent comp = (XSDConcreteComponent)eObject;
            if (XsdObjectExtension.canAnnotate(comp)) {

                // Get the stringified form of the value for this feature
                final EDataType dt = (EDataType)eFeature.getEType();
                final EPackage ePackage = dt.getEPackage();
                final EFactory fac = ePackage.getEFactoryInstance();
                String newStringValue = null;

                // Multivalued feature so create space delimited string
                if (eFeature.isMany()) {
                    final List values = (List)newValue;
                    final Iterator iter = values.iterator();
                    while (iter.hasNext()) {
                        final Object value = iter.next();
                        if (newStringValue == null) {
                            newStringValue = fac.convertToString(dt, value);
                        } else {
                            newStringValue = newStringValue + " " + fac.convertToString(dt, value); //$NON-NLS-1$
                        }
                    }

                }
                // Single valued feature
                else {
                    newStringValue = fac.convertToString(dt, newValue);
                }

                // Add/reset the appInfo attribute for the new value
                final String name = eFeature.getName();
                XsdObjectExtension.addAppInfoAttribute(comp, name, newStringValue);

                // Set and reset the XSD schema target namespace value so that the resource
                // gets marked as requiring save
                this.setAndResetTargetNamespace(comp.getSchema());
            }
        }
    }

    /**
     * Perform some get/set trickery on the xsd schema so that it the resource gets marked as requiring save
     * 
     * @param schema
     * @since 4.2
     */
    private void setAndResetTargetNamespace( final XSDSchema schema ) {
        if (schema != null) {
            Resource xsdResource = schema.eResource();
            if (xsdResource != null) {
                schema.eResource().setModified(true);
            }
            final EStructuralFeature feature = schema.eClass().getEStructuralFeature("targetNamespace"); //$NON-NLS-1$
            if (feature != null) {
                Object origValue = schema.eGet(feature);
                schema.eSet(feature, null);
                schema.eSet(feature, origValue);
            }
        }

    }

    /**
     * @see com.metamatrix.modeler.internal.core.ObjectExtension#doDynamicGet(org.eclipse.emf.ecore.EStructuralFeature,
     *      java.lang.Object)
     * @since 4.2
     */
    @Override
    protected Object doDynamicGet( EStructuralFeature eFeature,
                                   Object result ) {

        final EObject eObject = super.getExtendedObject();
        if (eObject != null && eObject instanceof XSDConcreteComponent) {
            final XSDConcreteComponent comp = (XSDConcreteComponent)eObject;
            if (XsdObjectExtension.canAnnotate(comp)) {
                final String name = eFeature.getName();
                final EDataType dt = (EDataType)eFeature.getEType();
                final EPackage ePackage = dt.getEPackage();
                final EFactory fac = ePackage.getEFactoryInstance();
                String value = XsdObjectExtension.getAppInfoAttributeValue(comp, name);
                // if the value is null, get default value, if that is null get the type default value
                if (value == null || StringUtil.Constants.EMPTY_STRING.equals(value)) {
                    Object defaultValue = eFeature.getDefaultValue();
                    if (defaultValue != null && !StringUtil.Constants.EMPTY_STRING.equals(defaultValue)) {
                        value = defaultValue.toString();
                    } else {
                        Object typeDefault = dt.getDefaultValue();
                        value = typeDefault != null ? typeDefault.toString() : null;
                    }
                }
                if (eFeature.isMany()) {
                    final List values = new BasicEList();
                    result = values;
                    final StringTokenizer stringTokenizer = new StringTokenizer(value, " "); //$NON-NLS-1$
                    while (stringTokenizer.hasMoreTokens()) {
                        String token = stringTokenizer.nextToken();
                        values.add(fac.createFromString(dt, token));
                    }
                } else {
                    result = fac.createFromString(dt, value);
                }
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.ObjectExtension#doDynamicIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.2
     */
    @Override
    protected boolean doDynamicIsSet( EStructuralFeature eFeature ) {
        final EObject eObject = super.getExtendedObject();
        if (eObject != null && eObject instanceof XSDConcreteComponent) {
            final XSDConcreteComponent comp = (XSDConcreteComponent)eObject;
            if (XsdObjectExtension.canAnnotate(comp)) {
                final String name = eFeature.getName();
                final String value = XsdObjectExtension.getAppInfoAttributeValue(comp, name);
                return value != null;
            }
        }
        return super.eDynamicIsSet(eFeature);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.ObjectExtension#doDynamicUnset(org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.2
     */
    @Override
    protected void doDynamicUnset( EStructuralFeature eFeature ) {
        final EObject eObject = super.getExtendedObject();
        if (eObject != null && eObject instanceof XSDConcreteComponent) {
            final XSDConcreteComponent comp = (XSDConcreteComponent)eObject;
            if (XsdObjectExtension.canAnnotate(comp)) {
                final String name = eFeature.getName();
                XsdObjectExtension.removeAppInfoAttribute(comp, name);
            }
        }
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================
    public static boolean isWritable( final XSDConcreteComponent component ) {
        if (component == null) {
            return false;
        }

        final XSDResourceImpl rsrc = (XSDResourceImpl)component.eResource();
        return isWritable(rsrc);
    }

    public static boolean isWritable( final XSDResourceImpl rsrc ) {
        if (rsrc == null) {
            return false;
        }

        URI uri = rsrc.getURI();
        if (uri != null && uri.toFileString() != null) {
            final File file = new File(uri.toFileString());
            if (file.exists()) {
                return file.canWrite();
            }
        }

        return true;
    }

    /**
     * Return true if the specified XSDConcreteComponent is an entity that can be contain an XSDAnnotation, otherwise return
     * false.
     */
    public static boolean canAnnotate( final XSDConcreteComponent comp ) {
        ArgCheck.isNotNull(comp);
        final boolean writable = XsdObjectExtension.isWritable(comp);
        if (!writable) {
            return false;
        }

        if (comp instanceof XSDSchema || comp instanceof XSDAttributeDeclaration || comp instanceof XSDAttributeGroupDefinition
            || comp instanceof XSDElementDeclaration || comp instanceof XSDNotationDeclaration || comp instanceof XSDModelGroup
            || comp instanceof XSDModelGroupDefinition || comp instanceof XSDIdentityConstraintDefinition
            || comp instanceof XSDTypeDefinition || comp instanceof XSDWildcard || comp instanceof XSDFacet) {
            return true;
        }
        return false;
    }

    /**
     * Return the XSDAnnotation instance associated with the XSDConcreteComponent or null if the component has no annotation.
     */
    public static XSDAnnotation getAnnotation( final XSDConcreteComponent comp ) {
        ArgCheck.isNotNull(comp);
        XSDAnnotation annotation = null;

        if (comp instanceof XSDSchema) {
            List annotations = ((XSDSchema)comp).getAnnotations();
            if (annotations != null) {
                for (Iterator iter = annotations.iterator(); iter.hasNext();) {
                    annotation = (XSDAnnotation)iter.next();
                    // Stop iterating if we find an annotation containing application info
                    // if (annotation != null && !annotation.getApplicationInformation().isEmpty()) {
                    if (annotation != null) {
                        break;
                    }
                }
            }

        } else if (comp instanceof XSDAttributeDeclaration) {
            annotation = ((XSDAttributeDeclaration)comp).getAnnotation();

        } else if (comp instanceof XSDAttributeGroupDefinition) {
            annotation = ((XSDAttributeGroupDefinition)comp).getAnnotation();

        } else if (comp instanceof XSDElementDeclaration) {
            annotation = ((XSDElementDeclaration)comp).getAnnotation();

        } else if (comp instanceof XSDNotationDeclaration) {
            annotation = ((XSDNotationDeclaration)comp).getAnnotation();

        } else if (comp instanceof XSDModelGroup) {
            annotation = ((XSDModelGroup)comp).getAnnotation();

        } else if (comp instanceof XSDModelGroupDefinition) {
            annotation = ((XSDModelGroupDefinition)comp).getAnnotation();

        } else if (comp instanceof XSDIdentityConstraintDefinition) {
            annotation = ((XSDIdentityConstraintDefinition)comp).getAnnotation();

        } else if (comp instanceof XSDTypeDefinition) {
            annotation = ((XSDTypeDefinition)comp).getAnnotation();

        } else if (comp instanceof XSDWildcard) {
            annotation = ((XSDWildcard)comp).getAnnotation();

        } else if (comp instanceof XSDFacet) {
            annotation = ((XSDFacet)comp).getAnnotation();

        }
        return annotation;
    }

    /**
     * Set the XSDAnnotation instance on the XSDConcreteComponent if the component can contain annotations otherwise do nothing.
     */
    public static void setAnnotation( final XSDConcreteComponent comp,
                                      final XSDAnnotation annotation ) {
        ArgCheck.isNotNull(comp);
        if (annotation == null) {
            return;
        }

        if (comp instanceof XSDSchema) {
            List annotations = ((XSDSchema)comp).getAnnotations();
            if (annotations != null) {
                annotations.add(annotations);
            }

        } else if (comp instanceof XSDAttributeDeclaration) {
            ((XSDAttributeDeclaration)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDAttributeGroupDefinition) {
            ((XSDAttributeGroupDefinition)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDElementDeclaration) {
            ((XSDElementDeclaration)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDNotationDeclaration) {
            ((XSDNotationDeclaration)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDModelGroup) {
            ((XSDModelGroup)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDModelGroupDefinition) {
            ((XSDModelGroupDefinition)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDIdentityConstraintDefinition) {
            ((XSDIdentityConstraintDefinition)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDTypeDefinition) {
            ((XSDTypeDefinition)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDWildcard) {
            ((XSDWildcard)comp).setAnnotation(annotation);

        } else if (comp instanceof XSDFacet) {
            ((XSDFacet)comp).setAnnotation(annotation);

        }
    }

    /**
     * Add an attribute with the specified name and value to the appInfo documentation of the XSDConcreteComponent. If the
     * XSDConcreteComponent cannot contain annotations then do nothing.
     */
    public static void addAppInfoAttribute( final XSDConcreteComponent comp,
                                            final String name,
                                            final String value ) {
        ArgCheck.isNotNull(comp);

        // If the XSDConcreteComponent is not a type that can be annotated then return immediately
        if (!canAnnotate(comp)) {
            return;
        }

        // Get or create the annotation for the specified component
        XSDAnnotation annotation = XsdObjectExtension.getAnnotation(comp);
        if (annotation == null) {
            annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
            setAnnotation(comp, annotation);
        }

        // Add the attribute to the appInfo
        addAppInfoAttribute(annotation, name, value);
    }

    /**
     * Add an attribute with the specified name and value to the appInfo documentation of the XSDAnnotation.
     */
    public static void addAppInfoAttribute( final XSDAnnotation annotation,
                                            final String name,
                                            final String value ) {
        ArgCheck.isNotNull(annotation);
        ArgCheck.isNotZeroLength(name);

        // Check if an attribute by this name already exists in the specified annotation
        List appInfos = annotation.getApplicationInformation();
        for (final Iterator iter = appInfos.iterator(); iter.hasNext();) {
            final Element appInfo = (Element)iter.next();

            // If one is found then reset the value and return
            if (appInfo != null && appInfo.getAttribute(name) != null) {
                if (value == null || StringUtil.Constants.EMPTY_STRING.equals(value)) {
                    appInfo.removeAttribute(name);
                } else {
                    appInfo.setAttribute(name, value);
                }
                return;
            }
        }

        // Get or create the appInfo instance for the specified annotation
        Element appInfo = null;
        if (annotation.getApplicationInformation().isEmpty()) {
            appInfo = annotation.createApplicationInformation(null);
            annotation.getElement().appendChild(appInfo);
        } else {
            appInfo = annotation.getApplicationInformation().iterator().next();
        }

        // Get or create the attribute for the appInfo
        appInfo.setAttribute(name, value);

    }

    /**
     * Remove the appInfo attribute with the specified name, if it exists.
     */
    public static void removeAppInfoAttribute( final XSDConcreteComponent comp,
                                               final String name ) {
        ArgCheck.isNotNull(comp);
        ArgCheck.isNotZeroLength(name);

        // If the XSDConcreteComponent is not a type that can be annotated then return immediately
        if (!canAnnotate(comp)) {
            return;
        }

        // Get the annotation, if it exists, for the specified component
        XSDAnnotation annotation = XsdObjectExtension.getAnnotation(comp);
        if (annotation == null) {
            return;
        }

        // Return the value for any appInfo attribute with the specified name
        List appInfos = annotation.getApplicationInformation();
        for (final Iterator iter = appInfos.iterator(); iter.hasNext();) {
            final Element appInfo = (Element)iter.next();

            // If one is found then return the value
            if (appInfo != null && appInfo.getAttribute(name) != null) {
                appInfo.removeAttribute(name);
            }
        }
    }

    /**
     * Return the value for the appInfo attribute with the specified name, if it exists, otherwise null is returned.
     */
    public static String getAppInfoAttributeValue( final XSDConcreteComponent comp,
                                                   final String name ) {
        ArgCheck.isNotNull(comp);
        ArgCheck.isNotZeroLength(name);
        return (String)getAppInfoAttributeMap(comp).get(name);
    }

    /**
     * Return a unmodifiable Map of the appInfo attribute names and values.
     */
    public static Map getAppInfoAttributeMap( final XSDConcreteComponent comp ) {
        ArgCheck.isNotNull(comp);

        // If the XSDConcreteComponent is not a type that can be annotated then return immediately
        if (!canAnnotate(comp)) {
            return Collections.EMPTY_MAP;
        }

        // Get the annotation for the specified component if it exists
        final XSDAnnotation annotation = XsdObjectExtension.getAnnotation(comp);
        if (annotation == null) {
            return Collections.EMPTY_MAP;
        }

        // Check if an attribute by this name already exists in the specified annotation
        final Map result = new HashMap();
        List appInfos = annotation.getApplicationInformation();
        for (final Iterator iter = appInfos.iterator(); iter.hasNext();) {
            final Element appInfo = (Element)iter.next();
            if (appInfo != null) {
                final NamedNodeMap map = appInfo.getAttributes();
                final int length = map.getLength();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        final Node mapNode = map.item(i);
                        if (mapNode != null) {
                            result.put(mapNode.getNodeName(), mapNode.getNodeValue());
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public static XPackage getExtensionPackage( final XSDResourceImpl xsdResource ) throws ModelerCoreException {
        ArgCheck.isNotNull(xsdResource);
        final XSDSchema schema = xsdResource.getSchema();
        if (schema != null) {
            return XsdObjectExtension.getExtensionPackage(schema);
        }
        return null;
    }

    public static XPackage getExtensionPackage( final XSDSchema schema ) throws ModelerCoreException {
        ArgCheck.isNotNull(schema);

        final Map extMap = XsdObjectExtension.getAppInfoAttributeMap(schema);
        final String extPkgLocation = (String)extMap.get(XsdObjectExtension.EXTENSION_PACKAGE);
        if (extPkgLocation != null && extPkgLocation.length() > 0) {
            XPackage extPackage = null;
            try {
                URI extPkgURI = URI.createURI(extPkgLocation);
                // Take the URI relative to the XSD resource location and make it absolute
                extPkgURI = extPkgURI.resolve(schema.eResource().getURI());

                // Lookup the XPackage in the Modeler's resource set ...
                extPackage = (XPackage)ModelerCore.getModelContainer().getEObject(extPkgURI, true);

                // If not found then try looking it up in the schema's resource set ...
                if (extPackage == null) {
                    extPackage = (XPackage)schema.eResource().getResourceSet().getEObject(extPkgURI, true);
                }

            } catch (CoreException err) {
                final String msg = ModelerCore.Util.getString("XsdObjectExtension.getExtension_1"); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR, err, msg);
            }

            // If found then return the XPackage otherwise throw an exception
            // because an XPackage with this URI could be found
            if (extPackage != null) {
                return extPackage;
            }
            final String msg = ModelerCore.Util.getString("XsdObjectExtension.getExtension_0"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        return null;
    }

    public static void removeExtensionPackage( final XSDResourceImpl xsdResource ) throws ModelerCoreException {
        ArgCheck.isNotNull(xsdResource);
        XPackage extPackage = XsdObjectExtension.getExtensionPackage(xsdResource);
        XSDSchema schema = xsdResource.getSchema();
        if (extPackage != null && schema != null) {
            // Set the incremental update state of the XSD resource to false to
            // improve performance while modifying the schema components
            final boolean incrementalUpdate = schema.isIncrementalUpdate();
            schema.setIncrementalUpdate(false);
            // For every XSDConcreteComponent in the resource check if remove any
            // appInfo attributes associated with the extension
            for (final Iterator iter = xsdResource.getAllContents(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof XSDConcreteComponent) {
                    final XSDConcreteComponent comp = (XSDConcreteComponent)obj;
                    final EClass eClass = comp.eClass();
                    final XClass xClass = extPackage.findXClass(eClass);
                    if (xClass != null) {
                        for (Iterator iter2 = xClass.eAllContents(); iter2.hasNext();) {
                            EObject eObj = (EObject)iter2.next();
                            if (eObj instanceof XAttribute) {
                                XsdObjectExtension.removeAppInfoAttribute(comp, ((XAttribute)eObj).getName());
                            }
                        }
                    }
                }
            }
            // Reset the incremental update state of the XSD resource to its original state
            schema.setIncrementalUpdate(incrementalUpdate);
        }
        if (schema != null) {
            XsdObjectExtension.removeAppInfoAttribute(schema, EXTENSION_PACKAGE);
        }
    }

    public static void setExtensionPackage( final XSDResourceImpl xsdResource,
                                            final XPackage extPackage ) {
        ArgCheck.isNotNull(xsdResource);
        XsdObjectExtension.setExtensionPackage(xsdResource.getSchema(), extPackage);
    }

    public static void setExtensionPackage( final XSDSchema schema,
                                            final XPackage extPackage ) {
        ArgCheck.isNotNull(schema);
        ArgCheck.isNotNull(extPackage);

        final Resource xsdResource = schema.eResource();
        final Resource extResource = extPackage.eResource();
        if (xsdResource != null && extResource != null) {
            URI xsdURI = xsdResource.getURI();
            URI extURI = extResource.getURI();
            extURI = extURI.appendFragment(extResource.getURIFragment(extPackage));
            // Make the URI relative to the XSD resource location
            URI relativeURI = extURI.deresolve(xsdURI);

            if (relativeURI != null) {
                XsdObjectExtension.addAppInfoAttribute(schema, EXTENSION_PACKAGE, relativeURI.toString());
            }
        } else {
            final String msg = ModelerCore.Util.getString("XsdObjectExtension.setExtension_2"); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, msg);
        }
    }

}

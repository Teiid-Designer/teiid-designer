/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.xsd.aspects.sql.XsdSimpleTypeDefinitionAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;

/**
 * XsdUtil
 */
public class XsdUtil {
    public static final String BUILT_IN_DATATYPE_NAMESPACE_URI = DatatypeConstants.BUILTIN_DATATYPES_URI;

    /**
     * Protected constructor to prevent instantiation.
     */
    protected XsdUtil() {
        super();
    }

    /**
     * Get the compositor object (i.e., the <code>sequence</code>, <code>choice</code> or <code>all</code>) under the supplied
     * complex type. The complex type definition contains a <code>complexContent</code> child, which contains a
     * <code>complexContent</code>, and which contains a <code>restriction</code> or <code>extension</code>, and finally the
     * compositor.
     * <p>
     * For example:
     * </p>
     * <p>
     * 
     * <pre>
     *     &lt;xsd:element name=&quot;internationalPrice&quot;&gt;
     *       &lt;xsd:complexType&gt;
     *         &lt;xsd:complexContent&gt;
     *           &lt;xsd:restriction base=&quot;xsd:anyType&quot;&gt;
     *             &lt;xsd:attribute name=&quot;currency&quot; type=&quot;xsd:string&quot;/&gt;
     *            &lt;xsd:attribute name=&quot;value&quot;    type=&quot;xsd:decimal&quot;/&gt;
     *           &lt;/xsd:restriction&gt;
     *         &lt;/xsd:complexContent&gt;
     *       &lt;/xsd:complexType&gt;
     * &lt;/xsd:element>
     * 
     * </p>
     * </pre>
     * 
     * @param complexType
     * @return the compositor
     */
    public static XSDModelGroup getCompositor( final XSDComplexTypeDefinition complexType ) {
        if (complexType != null) {
            // Get the complex content ...
            final XSDComplexTypeContent content = complexType.getContent();
            if (content instanceof XSDParticle) {
                // The complex content is a particle
                final XSDParticle particle = (XSDParticle)content;
                // Check the content of the particle ...
                final XSDParticleContent particleContent = particle.getContent();
                if (particleContent != null && particleContent instanceof XSDModelGroup) {
                    return (XSDModelGroup)particleContent;
                }
                // Check the term of the particle ...
                final XSDTerm particleTerm = particle.getTerm();
                if (particleTerm != null && particleTerm instanceof XSDModelGroup) {
                    return (XSDModelGroup)particleTerm;
                }
            }
        }
        return null;
    }

    /**
     * Return true if the specified resource is an instanceof an XSDResourceImpl which contains XSDSchemaDirective instances that
     * are not yet resolved. An XSDSchemaDirective is resolved because either the dependent resource was missing from the resource
     * set when this resource was loaded or because the location information in the XSDSchemaDirective is invalid. before the
     * dependent resource was added to the resource set.
     * 
     * @param eResource the resource to check
     * @return true if the resource contains unresolved directives
     * @since 4.3
     */
    public static boolean hasUnresolvedSchemaDirectives( final Resource eResource ) {
        if (eResource instanceof XSDResourceImpl) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)eResource;
            final XSDSchemaImpl schema = (XSDSchemaImpl)xsdResource.getSchema();

            // Iterate over the contents looking for SchemaDirective instances (import, include, redefine)
            for (Iterator iter = schema.eContents().iterator(); iter.hasNext();) {
                Object content = iter.next();
                if (content instanceof XSDSchemaDirective) {
                    final XSDSchemaDirective directive = (XSDSchemaDirective)content;

                    // If the directive is not yet resolved ...
                    if (directive.getResolvedSchema() == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * If the specified resource is an instanceof an XSDResourceImpl and it is found to have XSDSchemaDirective instances that are
     * not yet resolved, try to resolve them by reloading this resource and any other XSDResourceImpl instances in the resource
     * set with unresolved directives. Invoking this method will not resolve a directive if the dependent resource is missing or
     * if the location information in the XSDSchemaDirective is invalid.
     * 
     * @param eResource the resource to operate on
     * @return
     * @since 4.3
     */
    public static void resolveSchemaDirectives( final Resource eResource ) {
        if (eResource instanceof XSDResourceImpl && hasUnresolvedSchemaDirectives(eResource)) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)eResource;

            // The specified XSD resource has unresolved directives then make a list of
            // any other XSDs in the container with this same problem
            final ResourceSet rs = xsdResource.getResourceSet();
            if (rs != null) {
                final List xsds = new ArrayList(rs.getResources().size());
                for (Iterator iter = rs.getResources().iterator(); iter.hasNext();) {
                    final Resource r = (Resource)iter.next();
                    if (r instanceof XSDResourceImpl && hasUnresolvedSchemaDirectives(r)) {
                        xsds.add(r);
                    }
                }

                // Reload the XML Schema resources to ensure that the include, import, or redefine
                // definitions are resolved correctly.
                for (Iterator iter = xsds.iterator(); iter.hasNext();) {
                    final Resource r = (Resource)iter.next();
                    r.unload();
                    try {
                        r.load(rs.getLoadOptions());
                    } catch (IOException e) {
                        XsdPlugin.Util.log(e);
                    }
                }
            }
        }
    }

    public static String getUniqueQNamePrefix( final XSDSchema schema ) {
        if (schema == null) {
            return null;
        }

        final Map qNameMap = schema.getQNamePrefixToNamespaceMap();
        int increment = 1;
        final String start = "Z"; //$NON-NLS-1$
        String val = start + increment;
        boolean done = false;
        while (!done) {
            if (qNameMap.get(val) != null) {
                increment++;
                val = start + increment;
            } else {
                done = true;
            }
        }

        return val;
    }

    public static void removeNamespaceRef( final XSDSchema schema,
                                           final String namespace ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);
        ArgCheck.isNotNull(namespace);

        final Map map = schema.getQNamePrefixToNamespaceMap();
        final Set entrySet = map.entrySet();
        for (final Iterator it = new ArrayList(entrySet).iterator(); it.hasNext();) {
            Map.Entry entry = (Entry)it.next();
            if (namespace.equals(entry.getValue())) {
                try {
                    ModelerCore.getModelEditor().removeMapValue(schema, map, entry.getKey());
                } catch (ModelerCoreException mce) {
                    String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_remove_the_namespace_reference", namespace); //$NON-NLS-1$
                    XsdPlugin.Util.log(IStatus.ERROR, mce, msg);
                }
            }
        }
    }

    public static void removeImport( final XSDSchema schema,
                                     final String namespace ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);
        ArgCheck.isNotNull(namespace);

        for (final Iterator it = new ArrayList(schema.getContents()).iterator(); it.hasNext();) {
            final Object content = it.next();
            if (content instanceof XSDImport && namespace.equals(((XSDImport)content).getNamespace())) {
                try {
                    ModelerCore.getModelEditor().removeValue(schema, content, schema.getContents());
                } catch (ModelerCoreException mce) {
                    String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_remove_the_xsd_import", namespace); //$NON-NLS-1$
                    XsdPlugin.Util.log(IStatus.ERROR, mce, msg);
                }
            }
        }
    }

    public static void addImport( final XSDSimpleTypeDefinition simpleType,
                                  final XSDSimpleTypeDefinition baseType ) {
        ArgCheck.isNotNull(simpleType);
        XsdUtil.argCheckIsResolved(simpleType);
        ArgCheck.isNotNull(baseType);
        XsdUtil.argCheckIsResolved(baseType);

        final XSDSchema schema = simpleType.getSchema();
        final String referencedNamespace = baseType.getTargetNamespace();
        if (!XsdUtil.containsNamespaceDeclaration(schema, referencedNamespace)) {
            addNamespaceRef(schema, referencedNamespace);
        }
        final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
        xsdImport.setNamespace(referencedNamespace);
        // if we're adding an import for the MM built-in datatype schema, we do NOT
        // add the schema location; the mere presence of the namespace in an import
        // will cause the schema location to be set correctly
        if (!DatatypeConstants.BUILTIN_DATATYPES_URI.equals(referencedNamespace)) {
            final URI schemaLocation = baseType.eResource().getURI().deresolve(simpleType.eResource().getURI());
            xsdImport.setSchemaLocation(schemaLocation.toString());
        }
        try {
            ModelerCore.getModelEditor().addValue(schema, xsdImport, schema.getContents(), 0);
        } catch (ModelerCoreException mce) {
            Object[] params = new Object[] {referencedNamespace, xsdImport.getSchemaLocation()};
            String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_add_the_xsd_import", params); //$NON-NLS-1$
            XsdPlugin.Util.log(IStatus.ERROR, mce, msg);
        }
    }

    public static void addNamespaceRef( final XSDSchema schema,
                                        final String namespace ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);
        ArgCheck.isNotNull(namespace);

        final String prefix = XsdUtil.getUniqueQNamePrefix(schema);
        XsdUtil.addNamespaceRef(schema, prefix, namespace);
    }

    public static void addNamespaceRef( final XSDSchema schema,
                                        final String prefix,
                                        final String namespace ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);
        ArgCheck.isNotNull(namespace);

        final Map map = schema.getQNamePrefixToNamespaceMap();
        try {
            ModelerCore.getModelEditor().addMapValue(schema, map, prefix, namespace);
        } catch (ModelerCoreException mce) {
            Object[] params = new Object[] {prefix, namespace};
            String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_add_the_namespace_reference", params); //$NON-NLS-1$
            XsdPlugin.Util.log(IStatus.ERROR, mce, msg);
        }
    }

    public static boolean containsImport( final XSDSchema schema,
                                          final String namespace ) {
        boolean containsImport = false;
        if (schema != null) {
            if (namespace != null) {
                final List contents = schema.getContents();
                for (int i = 0; i < contents.size(); i++) {
                    final Object content = contents.get(i);
                    if (content instanceof XSDImport) {
                        if (namespace.equals(((XSDImport)content).getNamespace())) {
                            containsImport = true;
                            break;
                        }
                    }
                }
            }
        }
        return containsImport;
    }

    public static boolean containsReferenceToNamespace( final XSDSchema schema,
                                                        final String namespace ) {
        boolean containsReference = false;
        if (schema != null && !schema.eIsProxy() && namespace != null) {
            // we always will have a reference to the schema for schemas
            if (XSDConstants.isSchemaForSchemaNamespace(namespace)) {
                containsReference = true;
            } else {
                final List contents = schema.getContents();
                for (int i = 0; i < contents.size(); i++) {
                    final Object o = contents.get(i);
                    if (o instanceof XSDSimpleTypeDefinition) {
                        XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)o;
                        if (namespace.equals(simpleType.getBaseTypeDefinition().getTargetNamespace())) {
                            containsReference = true;
                            break;
                        }
                    }
                }
            }
        }
        return containsReference;
    }

    public static boolean containsNamespaceDeclaration( final XSDSchema schema,
                                                        final String namespace ) {
        boolean containsNamespaceDeclaration = false;
        if (schema != null) {
            final Map prefixesToNamespaces = schema.getQNamePrefixToNamespaceMap();
            containsNamespaceDeclaration = prefixesToNamespaces.containsValue(namespace);
        }
        return containsNamespaceDeclaration;
    }

    public static boolean isEnterpriseSchema( final XSDSchema schema ) {
        boolean success = false;
        if ((schema != null) && (!schema.eIsProxy())) {
            String namespace = schema.getQNamePrefixToNamespaceMap().get(XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005);
            success = XsdConstants.isSchemaEnterpriseDatatypeNamespace(namespace);
        }
        return success;
    }

    public static void setTargetNamespace( final XSDSchema schema,
                                           String namespace ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);
        ArgCheck.isNotNull(namespace);

        String defaultNamespaceReference = schema.getQNamePrefixToNamespaceMap().get(null);
        boolean assignDefaultReference = defaultNamespaceReference == null
                                         || schema.getTargetNamespace().equals(defaultNamespaceReference);
        if (assignDefaultReference) {
            addNamespaceRef(schema, null, namespace);
        } else {
            addNamespaceRef(schema, namespace);
        }
        schema.setTargetNamespace(namespace);
    }

    public static void setAsEnterpriseSchema( final XSDSchema schema ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);

        addNamespaceRef(schema,
                        XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005,
                        XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005);
        schema.eResource().setModified(true);
    }

    public static void unsetAsEnterpriseSchema( final XSDSchema schema ) {
        ArgCheck.isNotNull(schema);
        XsdUtil.argCheckIsResolved(schema);

        schema.getQNamePrefixToNamespaceMap().remove(XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005);
        schema.eResource().setModified(true);
    }

    public static void checkForEnterpriseConversion( final XSDSchema schema ) {
        if ((schema != null) && (!isEnterpriseSchema(schema))) {
            final List contents = schema.getContents();
            for (int i = 0; i < contents.size(); i++) {
                final Object o = contents.get(i);
                if (o instanceof XSDSimpleTypeDefinition) {
                    final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)o;
                    final XsdSimpleTypeDefinitionAspect aspect = (XsdSimpleTypeDefinitionAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(simpleType,
                                                                                                                                                      SqlAspect.class);
                    aspect.convertEnterpriseDatatype(simpleType);
                }
            }
        }
    }

    public static void checkForEnterpriseConversion( final XSDSimpleTypeDefinition simpleType ) {
        final XSDSchema schema = simpleType.getSchema();

        if ((schema != null) && (!isEnterpriseSchema(schema))) {
            final XsdSimpleTypeDefinitionAspect aspect = (XsdSimpleTypeDefinitionAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(simpleType,
                                                                                                                                              SqlAspect.class);
            EnterpriseDatatypeInfo edtInfo = aspect.getEnterpriseAttributesFromAppInfo(simpleType);
            if (edtInfo.isValid()) {
                checkForEnterpriseConversion(schema);
            }
        }
    }

    /**
     * Return true if the specified XSDConcreteComponent is an entity that can be contain an XSDAnnotation, otherwise return
     * false.
     */
    public static boolean canAnnotate( final XSDConcreteComponent comp ) {
        ArgCheck.isNotNull(comp);

        if (comp instanceof XSDSchema || comp instanceof XSDAnnotation || comp instanceof XSDAttributeDeclaration
            || comp instanceof XSDAttributeGroupDefinition || comp instanceof XSDElementDeclaration
            || comp instanceof XSDNotationDeclaration || comp instanceof XSDModelGroup || comp instanceof XSDModelGroupDefinition
            || comp instanceof XSDIdentityConstraintDefinition || comp instanceof XSDTypeDefinition
            || comp instanceof XSDWildcard || comp instanceof XSDFacet) {
            return true;
        }
        return false;
    }

    /**
     * Return the XSDAnnotation instance associated with the XSDConcreteComponent or null if the component has no annotation.
     */
    public static XSDAnnotation getAnnotation( final XSDConcreteComponent comp ) {
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

        } else if (comp instanceof XSDAnnotation) {
            annotation = (XSDAnnotation)comp;

        }

        return annotation;
    }

    /**
     * Return the XSDAnnotation instance associated with the XSDConcreteComponent or null if the component has no annotation.
     */
    public static XSDAnnotation getAnnotation( final XSDConcreteComponent comp,
                                               boolean createIfNull ) {
        ArgCheck.isNotNull(comp);

        if (!createIfNull) {
            return getAnnotation(comp);
        }

        XSDAnnotation annotation = null;
        if (comp instanceof XSDSchema) {
            List annotations = ((XSDSchema)comp).getAnnotations();
            if (annotations == null || annotations.isEmpty()) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDSchema)comp).getContents().add(annotation);
            } else {
                for (Iterator iter = annotations.iterator(); iter.hasNext();) {
                    annotation = (XSDAnnotation)iter.next();
                    if (annotation != null) {
                        break;
                    }
                }
            }

        } else if (comp instanceof XSDAttributeDeclaration) {
            annotation = ((XSDAttributeDeclaration)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDAttributeDeclaration)comp).setAnnotation(annotation);
            }
        } else if (comp instanceof XSDAttributeGroupDefinition) {
            annotation = ((XSDAttributeGroupDefinition)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDAttributeGroupDefinition)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDElementDeclaration) {
            annotation = ((XSDElementDeclaration)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDElementDeclaration)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDNotationDeclaration) {
            annotation = ((XSDNotationDeclaration)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDNotationDeclaration)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDModelGroup) {
            annotation = ((XSDModelGroup)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDModelGroup)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDModelGroupDefinition) {
            annotation = ((XSDModelGroupDefinition)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDModelGroupDefinition)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDIdentityConstraintDefinition) {
            annotation = ((XSDIdentityConstraintDefinition)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDIdentityConstraintDefinition)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDTypeDefinition) {
            annotation = ((XSDTypeDefinition)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDTypeDefinition)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDWildcard) {
            annotation = ((XSDWildcard)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDWildcard)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDFacet) {
            annotation = ((XSDFacet)comp).getAnnotation();
            if (annotation == null) {
                annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
                ((XSDFacet)comp).setAnnotation(annotation);
            }

        } else if (comp instanceof XSDAnnotation) {
            annotation = (XSDAnnotation)comp;

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
            ((XSDSchema)comp).getContents().add(annotation);
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
     * Add a documentation node with the specified text to the annotation of the XSDConcreteComponent. If the XSDConcreteComponent
     * cannot contain annotations then do nothing.
     * 
     * @param comp the XSDConcreteComponent to add the documentation node to; may not be null
     * @param text the documentation text; may not be null or zero length
     */
    public static void addUserInfoAttribute( final XSDConcreteComponent comp,
                                             final String text ) {
        ArgCheck.isNotNull(comp);
        ArgCheck.isNotZeroLength(text);

        // If the XSDConcreteComponent is not a type that can be annotated then return immediately
        if (!canAnnotate(comp)) {
            return;
        }

        // Get or create the annotation for the specified component
        XSDAnnotation annotation = getAnnotation(comp);
        if (annotation == null) {
            annotation = XSDFactory.eINSTANCE.createXSDAnnotation();
            setAnnotation(comp, annotation);
        }

        // Add the userInfo to the annotation
        addUserInfoAttribute(annotation, text);
    }

    /**
     * Add a documentation node with the specified text to this XSDAnnotation.
     * 
     * @param comp the XSDAnnotation to add the documentation node to; may not be null
     * @param text the documentation text; may not be null or zero length
     */
    public static void addUserInfoAttribute( final XSDAnnotation annotation,
                                             final String text ) {
        ArgCheck.isNotNull(annotation);
        ArgCheck.isNotZeroLength(text);

        // if there are any documentation nodes already, remove them
        if (!annotation.getUserInformation().isEmpty()) {
            ArrayList nodesToRemove = new ArrayList();
            final Iterator userInfos = annotation.getUserInformation().iterator();
            while (userInfos.hasNext()) {
                final Element userInfo = (Element)userInfos.next();
                Node child = userInfo.getFirstChild();
                if (child != null) {
                    accumulateTextNodes(child, nodesToRemove);
                } else {
                    nodesToRemove.add(userInfo);
                }
            }

            for (Iterator iter = nodesToRemove.iterator(); iter.hasNext();) {
                Node node = (Node)iter.next();
                try {
                    Node parent = node.getParentNode();
                    if (parent != null) {
                        parent.removeChild(node);
                        if (!parent.hasChildNodes()) {
                            Node infoNode = parent.getParentNode();
                            infoNode.removeChild(parent);
                        }
                    }
                } catch (Exception e) {
                    XsdPlugin.Util.log(e);
                }
            }

        }

        // create a new documentation node text node for the annotation
        final Element userInfo = annotation.createUserInformation(null);
        annotation.getElement().appendChild(userInfo);
        userInfo.appendChild(userInfo.getOwnerDocument().createTextNode(text));

        return;
    }

    /**
     * Add an Application Information attribute with the given name and value
     * 
     * @param comp
     * @param propName
     * @param propValue
     */
    public static void addApplicationInfoTag( final XSDConcreteComponent comp,
                                              final String propName,
                                              final String propValue ) {
        if (comp == null || propName == null) {
            return;
        }

        XSDAnnotation annotation = getAnnotation(comp, true);
        Element appinfo = null;
        if (annotation.getApplicationInformation().isEmpty()) {
            appinfo = annotation.createApplicationInformation(null);
            annotation.getElement().appendChild(appinfo);
        } else {
            appinfo = annotation.getApplicationInformation().iterator().next();
        }

        Attr attr = appinfo.getOwnerDocument().createAttribute(propName);
        attr.setNodeValue(propValue);
        appinfo.getAttributes().setNamedItem(attr);
    }

    /**
     * Return the string value of the App Info Attribute child with the given name.
     * 
     * @param comp
     * @param key
     * @return
     */
    public static String getAppInfoAttributeValue( final XSDConcreteComponent comp,
                                                   final String key ) {
        final XSDAnnotation annotation = getAnnotation(comp);
        if (annotation == null || annotation.getApplicationInformation().isEmpty()) {
            return null;
        }

        final Iterator elements = annotation.getApplicationInformation().iterator();
        while (elements.hasNext()) {
            final Element next = (Element)elements.next();
            final Attr attr = next.getAttributeNode(key);
            if (attr != null) {
                return attr.getValue();
            }
        }

        return null;
    }

    /**
     * Get the compositor object (i.e., the <code>sequence</code>, <code>choice</code> or <code>all</code>) under the supplied
     * <code>group</code>. The model group may directly contain a compositor.
     * <p>
     * For example:
     * </p>
     * <p>
     * 
     * <pre>
     *     &lt;xsd:group name=&quot;shipAndBill&quot;&gt;
     *       &lt;xsd:sequence&gt;
     *         &lt;xsd:element name=&quot;shipTo&quot; type=&quot;USAddress&quot;/&gt;
     *         &lt;xsd:element name=&quot;billTo&quot; type=&quot;USAddress&quot;/&gt;
     *       &lt;/xsd:sequence&gt;
     *     &lt;/xsd:group&gt;
     * </pre>
     * 
     * </p>
     * 
     * @param complexType
     * @return
     */
    public static XSDModelGroup getCompositor( final XSDModelGroupDefinition group ) {
        if (group != null) {
            // Get the content ...
            return group.getModelGroup();
        }
        return null;
    }

    /**
     * Determine whether the supplied component is an XSD attribute.
     * 
     * @param component the XSDComponent
     * @return true if the component is an attribute; false otherwise
     */
    public static boolean isAttribute( final XSDComponent component ) {
        if (component != null) {
            if (component instanceof XSDAttributeDeclaration) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether the supplied compositor is a <code>sequence</code>.
     * 
     * @param modelGroup the model group
     * @return true if the model group is not null and is a <code>sequence</code>; false otherwise
     */
    public static boolean isSequence( final XSDModelGroup modelGroup ) {
        if (modelGroup != null) {
            final XSDCompositor compositor = modelGroup.getCompositor();
            return XSDCompositor.SEQUENCE == compositor.getValue();
        }
        return false;
    }

    /**
     * Determine whether the supplied compositor is a <code>choice</code>.
     * 
     * @param modelGroup the model group
     * @return true if the model group is not null and is a <code>choice</code>; false otherwise
     */
    public static boolean isChoice( final XSDModelGroup modelGroup ) {
        if (modelGroup != null) {
            final XSDCompositor compositor = modelGroup.getCompositor();
            return XSDCompositor.CHOICE == compositor.getValue();
        }
        return false;
    }

    /**
     * Determine whether the supplied compositor is an <code>all</code>.
     * 
     * @param modelGroup the model group
     * @return true if the model group is not null and is an <code>all</code>; false otherwise
     */
    public static boolean isAll( final XSDModelGroup modelGroup ) {
        if (modelGroup != null) {
            final XSDCompositor compositor = modelGroup.getCompositor();
            return XSDCompositor.ALL == compositor.getValue();
        }
        return false;
    }

    public static boolean isWritable( final XSDComponent component ) {
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
     * Determine whether the supplied component is a global component. A global component is one that is immediately under the
     * schema node.
     * 
     * @param xsdComponent the component
     * @return true if the component is global, or false otherwise
     */
    public static boolean isGlobal( final XSDComponent xsdComponent ) {
        if (xsdComponent != null) {
            // Get the parent ...
            final XSDConcreteComponent parent = xsdComponent.getContainer();
            if (parent instanceof XSDSchema) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate the list of types and supertypes for the supplied schema component. The first type in the list will be the schema
     * component if it is an XSDTypeDefinition, or the type of the schema component otherwise. The last type in the list will be
     * the most root type (typically one of the built-ins).
     * 
     * @param xsdComponent the schema component
     * @return the list of types and supertypes; never null, but maybe empty if the supplied component doesn't have a type.
     */
    public static List getSupertypes( XSDComponent xsdComponent ) {
        final List superTypes = new ArrayList();// LinkedList();
        while (xsdComponent != null) {
            if (xsdComponent instanceof XSDTypeDefinition) {
                superTypes.add(xsdComponent);
            }

            final XSDTypeDefinition type = getTypeOrBaseType(xsdComponent);
            if (type == null || type == xsdComponent) { // base type of 'anyType' is 'anyType'
                // No more, so return ...
                return superTypes;
            }
            xsdComponent = type;
        }
        return superTypes;
    }

    /**
     * Return the type definition that is common to all <code>element</code>s able to be placed under the supplied schema
     * component.
     * 
     * @param schemaComponent the schema component for which the common base type for contained elements is to be obtained; may
     *        not be null
     * @return the common type definition, which may be the <code>anyType</code> type; never null
     */
    public static XSDTypeDefinition getCommonBaseTypeForContained( final XSDComponent schemaComponent ) {
        // Find all of the elements that can be contained by this component
        final List childElements = new LinkedList();
        addChildElements(schemaComponent, childElements, false);

        if (childElements.size() == 1) {
            // There is only one type, so return it ...
            return (XSDTypeDefinition)childElements.get(0);
        }

        // For each element, find its hierarchy of types ...
        final List supertypesLists = new ArrayList();
        final Iterator iter = childElements.iterator();
        while (iter.hasNext()) {
            final XSDComponent childElement = (XSDComponent)iter.next();
            final List supertypes = getSupertypes(childElement);
            supertypesLists.add(supertypes);
        }
        return (XSDTypeDefinition)findFirstCommonObject(supertypesLists);
    }

    /**
     * @param supertypesLists
     * @return
     */
    protected static Object findFirstCommonObject( final List supertypesLists ) {
        if (supertypesLists.size() == 0) {
            return null;
        }
        // if there is only one list of supertypes, then the common supertype is simply the first one
        if (supertypesLists.size() == 1) {
            final List supertypes = (List)supertypesLists.get(0);
            if (supertypes.size() != 0) {
                return supertypes.get(0);
            }
            return null;
        }

        // Find the most concrete supertype common to them all.
        // This algorithm loops through the first list to get the candidate type,
        // then loops over all of the remaining lists, and on each loops over the
        // supertypes. If we reach the end of one of the remaining lists without a match,
        // then break out of all but the outer loop (since that candidate type didn't match
        // any in the next list).
        final List candiateList = (List)supertypesLists.remove(0);
        final Iterator candidateIter = candiateList.iterator();
        while (candidateIter.hasNext()) {
            boolean failedToFindAnyMatch = false;
            final Object candidateType = candidateIter.next();
            final Iterator listIter = supertypesLists.iterator();
            while (listIter.hasNext()) {
                final List supertypesOfAnotherChild = (List)listIter.next();
                // Loop over this inner
                boolean foundMatch = false;
                final Iterator innerIter = supertypesOfAnotherChild.iterator();
                while (innerIter.hasNext()) {
                    Object superOfAnotherChild = innerIter.next();
                    if (candidateType.equals(superOfAnotherChild)) {
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    failedToFindAnyMatch = true;
                    break; // go on to the next list of supertypes
                }
                // Otherwise, we did find a match so continue with the next list
            }
            if (!failedToFindAnyMatch) {
                // Then we went all the way through the lists and found a match!
                return candidateType;
            }
        }
        return null;
    }

    /**
     * Build up the set of {@link XSDElementDeclaration element declarations}
     * 
     * @param xsdComponent
     * @param childElements
     * @param includeSchemaComponent
     */
    protected static void addChildElements( final XSDComponent xsdComponent,
                                            final List childElements,
                                            final boolean includeSchemaComponent ) {
        if (xsdComponent instanceof XSDSimpleTypeDefinition) {
            return;
        }
        if (xsdComponent instanceof XSDAnnotation) {
            return;
        }
        if (xsdComponent instanceof XSDAttributeGroupDefinition) {
            return;
        }
        if (xsdComponent instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle)xsdComponent;
            final XSDParticleContent content = particle.getContent();
            if (content instanceof XSDComponent) {
                addChildElements((XSDComponent)content, childElements, true);
            }
        }
        if (xsdComponent instanceof XSDElementDeclaration) {
            final XSDTypeDefinition type = getTypeOrBaseType(xsdComponent);
            // Get the type ...
            if (type != null) {
                if (includeSchemaComponent) {
                    childElements.add(type);
                } else {
                    addChildElements(type, childElements, true);
                }
            }
            return;
        }

        if (xsdComponent instanceof XSDComplexTypeDefinition) {
            final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)xsdComponent;
            final XSDModelGroup compositor = getCompositor(complexType);

            if (compositor != null) {
                // Get the children ...
                final List children = compositor.getContents();
                final Iterator iter = children.iterator();
                while (iter.hasNext()) {
                    final EObject child = (EObject)iter.next(); // may have XSDDiagnostics that are not XSDComponents!
                    if (child instanceof XSDComponent) {
                        addChildElements((XSDComponent)child, childElements, true);
                    }
                }
            }

            // Add everything from it's super, too
            final XSDTypeDefinition superType = getTypeOrBaseType(xsdComponent);
            if (superType != null && superType != xsdComponent) { // superType of 'anyType' is 'anyType'
                addChildElements(superType, childElements, true);
            }
        }

        if (xsdComponent instanceof XSDModelGroupDefinition) {
            final XSDModelGroupDefinition group = (XSDModelGroupDefinition)xsdComponent;
            final XSDModelGroup compositor = getCompositor(group);

            if (compositor != null) {
                // Get the children ...
                final List children = compositor.getContents();
                final Iterator iter = children.iterator();
                while (iter.hasNext()) {
                    final EObject child = (EObject)iter.next(); // may have XSDDiagnostics that are not XSDComponents!
                    if (child instanceof XSDComponent) {
                        addChildElements((XSDComponent)child, childElements, true);
                    }
                }
            }
        }
    }

    /**
     * Get the type of the supplied element declaration. Note that since the an <code>element</code> can be a reference to another
     * <code>element</code> (which could be a reference, etc.), this method resolves all references until it finds a non-null and
     * non-reference element declaration.
     * 
     * @param element the element declaration; may not be null
     * @return the type of the (resolved) element, or null if there is no type (i.e., the type or a reference could not be
     *         resolved).
     */
    public static XSDTypeDefinition getType( XSDElementDeclaration element ) {
        // Resolve, in case this is an element ref. Because refs and point to refs, we have
        // to recursively do this until the element ref is the element ...
        XSDElementDeclaration resolvedElement = element.getResolvedElementDeclaration();
        while (resolvedElement != null && resolvedElement != element) {
            element = resolvedElement; // set the element object to be the resolved
            resolvedElement = element.getResolvedElementDeclaration(); // resolve again
        }
        return resolvedElement == null ? null : resolvedElement.getTypeDefinition();
    }

    /**
     * Get the type of the supplied attribute declaration. Note that since the an <code>attribute</code> can be a reference to
     * another <code>attribute</code> (which could be a reference, etc.), this method resolves all references until it finds a
     * non-null and non-reference attribute declaration.
     * 
     * @param attrib the attribute declaration; may not be null
     * @return the type of the (resolved) attribute, or null if there is no type (i.e., the type or a reference could not be
     *         resolved).
     */
    public static XSDTypeDefinition getType( XSDAttributeDeclaration attrib ) {
        // Resolve, in case this is an attribute ref. Because refs and point to refs, we have
        // to recursively do this until the attribute ref is the attribute ...
        XSDAttributeDeclaration resolvedAttrib = attrib.getResolvedAttributeDeclaration();
        while (resolvedAttrib != null && resolvedAttrib != attrib) {
            attrib = resolvedAttrib; // set the attribute object to be the resolved
            resolvedAttrib = attrib.getResolvedAttributeDeclaration(); // resolve again
        }
        return resolvedAttrib == null ? null : resolvedAttrib.getTypeDefinition();
    }

    /**
     * If the supplied declaration is a reference to another declaration, resolve all references until it finds a non-null and
     * non-reference declaration.
     * 
     * @param feature the element or attribute declaration; may not be null
     * @return the resolved element, <code>feature</code> if it wasn't a reference, or null if a reference could not be resolved.
     */
    public static XSDFeature getResolved( XSDFeature feature ) {
        if (feature instanceof XSDElementDeclaration) {
            return getResolved((XSDElementDeclaration)feature);
        }
        if (feature instanceof XSDAttributeDeclaration) {
            return getResolved((XSDAttributeDeclaration)feature);
        }
        return null; // should never happen
    }

    /**
     * If the supplied declaration is a reference to another declaration, resolve all references until it finds a non-null and
     * non-reference declaration.
     * 
     * @param element the element declaration; may not be null
     * @return the resolved element, <code>element</code> if it wasn't a reference, or null if a reference could not be resolved.
     */
    public static XSDElementDeclaration getResolved( XSDElementDeclaration element ) {
        // Resolve, in case this is an element ref. Because refs and point to refs, we have
        // to recursively do this until the element ref is the element ...
        XSDElementDeclaration resolvedElement = element.getResolvedElementDeclaration();
        while (resolvedElement != null && resolvedElement != element) {
            element = resolvedElement; // set the element object to be the resolved
            resolvedElement = element.getResolvedElementDeclaration(); // resolve again
        }
        return resolvedElement;
    }

    /**
     * If the supplied declaration is a reference to another declaration, resolve all references until it finds a non-null and
     * non-reference declaration.
     * 
     * @param attribute the attribute declaration; may not be null
     * @return the resolved element, <code>attribute</code> if it wasn't a reference, or null if a reference could not be
     *         resolved.
     */
    public static XSDAttributeDeclaration getResolved( XSDAttributeDeclaration attribute ) {
        // Resolve, in case this is an element ref. Because refs and point to refs, we have
        // to recursively do this until the element ref is the element ...
        XSDAttributeDeclaration resolvedAttribute = attribute.getResolvedAttributeDeclaration();
        while (resolvedAttribute != null && resolvedAttribute != attribute) {
            attribute = resolvedAttribute; // set the element object to be the resolved
            resolvedAttribute = attribute.getResolvedAttributeDeclaration(); // resolve again
        }
        return resolvedAttribute;
    }

    /**
     * Get the XSDSimpleTypeDefinition for the supplied schema component.
     * 
     * @param xsdComponent the component for which the simple type definition is to be returned
     * @return the simple type definition, or null if there is none (or the component is not defined to have a type)
     */
    public static XSDSimpleTypeDefinition getSimpleType( final XSDComponent xsdComponent ) {
        XSDTypeDefinition xsdType = getType(xsdComponent);
        if (xsdType != null) {
            return getSimpleType(xsdType);
        }
        return null;
    }

    /**
     * Get the XSDSimpleTypeDefinition for the XSDTypeDefinition or null if the XSDTypeDefinition is not a XSDSimpleTypeDefinition
     * or does not extend or restrict a XSDSimpleTypeDefinition.
     * 
     * @param xsdType
     * @return the simple datatype or null if there is none.
     */
    public static XSDSimpleTypeDefinition getSimpleType( final XSDTypeDefinition xsdType ) {
        XSDSimpleTypeDefinition simpleType = null;
        if (xsdType != null && xsdType instanceof XSDSimpleTypeDefinition) {
            simpleType = (XSDSimpleTypeDefinition)xsdType;
            return simpleType;
        }
        if (xsdType != null && xsdType instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)xsdType;

            XSDTypeDefinition type = complexType;
            while (type != null) {
                if (isAnySimpleType(type)) {
                    simpleType = (XSDSimpleTypeDefinition)type;
                    break;
                } else if (isAnyType(type)) {
                    break;
                } else if (type instanceof XSDSimpleTypeDefinition) {
                    simpleType = (XSDSimpleTypeDefinition)type;
                    break;
                } else if (type instanceof XSDComplexTypeDefinition) {
                    XSDTypeDefinition baseType = ((XSDComplexTypeDefinition)type).getBaseTypeDefinition();
                    // Break any recursion
                    if (baseType == type) break;
                    type = baseType;
                } else break;
            }
            return simpleType;
        }
        return null;
    }

    /**
     * Get the type of the supplied schema component.
     * 
     * @param xsdComponent the component for which the type definition is to be returned
     * @return the type definition, or null if there is none (or the component is not defined to have a type)
     */
    public static XSDTypeDefinition getType( final XSDComponent xsdComponent ) {
        if (xsdComponent instanceof XSDElementDeclaration) {
            return getType((XSDElementDeclaration)xsdComponent);
        }
        if (xsdComponent instanceof XSDAttributeDeclaration) {
            return getType((XSDAttributeDeclaration)xsdComponent);
        }
        if (xsdComponent instanceof XSDTypeDefinition) {
            return (XSDTypeDefinition)xsdComponent;
        }
        return null;
    }

    /**
     * Get the Federate Designer built-in datatype of the supplied type.
     * 
     * @param type
     * @return the built-in datatype or null if there is none.
     */
    public static XSDSimpleTypeDefinition getBuiltInDatatype( final XSDTypeDefinition type ) {
        XSDSimpleTypeDefinition builtInType = null;
        if (type != null && type instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
            try {
                // Use the workspace datatype manager, since we're looking for a BUILT-IN TYPE
                builtInType = (XSDSimpleTypeDefinition)ModelerCore.getWorkspaceDatatypeManager().getDatatypeForXsdType(complexType);
            } catch (ModelerCoreException e) {
                Object[] params = new Object[] {type.getURI()};
                String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_get_the_MetaMatrix_built-in_datatype_for_1", params); //$NON-NLS-1$
                XsdPlugin.Util.log(IStatus.ERROR, e, msg);
            }
        }
        if (type != null && type instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)type;
            try {
                // Use the workspace datatype manager, since we're looking for a BUILT-IN TYPE
                builtInType = (XSDSimpleTypeDefinition)ModelerCore.getWorkspaceDatatypeManager().getDatatypeForXsdType(simpleType);
            } catch (ModelerCoreException e) {
                Object[] params = new Object[] {type.getURI()};
                String msg = XsdPlugin.Util.getString("XsdUtil.Unable_to_get_the_MetaMatrix_built-in_datatype_for_2", params); //$NON-NLS-1$
                XsdPlugin.Util.log(IStatus.ERROR, e, msg);
            }
        }
        return builtInType;
    }

    /**
     * Get the Federate Designer built-in datatype of the supplied element declaration
     * 
     * @param element the element declaration; may not be null
     * @return the built-in datatype of the (resolved) element, or null if there is no type.
     */
    public static XSDSimpleTypeDefinition getBuiltInDatatype( final XSDElementDeclaration element ) {
        XSDTypeDefinition type = getType(element);
        return getBuiltInDatatype(type);
    }

    /**
     * Get the Federate Designer built-in datatype of the supplied supplied schema component
     * 
     * @param xsdComponent the component for which the datatype is to be returned
     * @return the built-in datatype or null if there is none.
     */
    public static XSDSimpleTypeDefinition getBuiltInDatatype( final XSDComponent xsdComponent ) {
        XSDTypeDefinition type = getType(xsdComponent);
        return getBuiltInDatatype(type);
    }

    /**
     * Get the type or base type of the supplied schema component. For components like <code>element</code>, this returns the
     * {@link #getType(XSDElementDeclaration) type} of the element. For {@link XSDTypeDefinition type definitions}, this method
     * returns the {@link XSDTypeDefinition#getBaseType() base type}.
     * 
     * @param xsdComponent the component for which the type definition is to be returned
     * @return the type definition or base type definition, or null if there is none (or the component is not defined to have a
     *         type)
     */
    public static XSDTypeDefinition getTypeOrBaseType( final XSDComponent xsdComponent ) {
        if (xsdComponent instanceof XSDElementDeclaration) {
            return getType((XSDElementDeclaration)xsdComponent);
        }
        if (xsdComponent instanceof XSDTypeDefinition) {
            return ((XSDTypeDefinition)xsdComponent).getBaseType();
        }
        return null;
    }

    /**
     * Returns whether the type definition is one of the flavours of the ur-type, i.e., complex <a
     * href="http://www.w3.org/TR/xmlschema-1/#ur-type-itself">anyType</a>, simple <a
     * href="http://www.w3.org/TR/xmlschema-2/#built-in-datatypes">anyType</a>, or <a
     * href="http://www.w3.org/TR/xmlschema-2/#dt-anySimpleType">anySimpleType</a>.
     * 
     * @param xsdTypeDefinition a simple or complex type definition.
     * @return whether the type definition is one of the flavours of the ur-type.
     */
    public static boolean isURType( final XSDTypeDefinition xsdType ) {
        if (xsdType != null) {
            return XSDConstants.isURType(xsdType);
        }
        return false;
    }

    /**
     * Returns whether the type definition is one of the flavours of the anyType, i.e., complex <a
     * href="http://www.w3.org/TR/xmlschema-1/#ur-type-itself">anyType</a> or simple <a
     * href="http://www.w3.org/TR/xmlschema-2/#built-in-datatypes">anyType</a>.
     * 
     * @param xsdTypeDefinition a simple or complex type definition.
     * @return whether the type definition is one of the flavours of the anyType.
     */
    public static boolean isAnyType( final XSDTypeDefinition xsdType ) {
        if (xsdType != null) {
            return XSDConstants.isAnyType(xsdType);
        }
        return false;
    }

    /**
     * Returns whether the type definition is the <a href="http://www.w3.org/TR/xmlschema-2/#dt-anySimpleType">anySimpleType</a>.
     * 
     * @param xsdTypeDefinition a simple or complex type definition.
     * @return whether the type definition is the anySimpleType.
     */
    public static boolean isAnySimpleType( final XSDTypeDefinition xsdType ) {
        if (xsdType != null) {
            return XSDConstants.isAnySimpleType(xsdType);
        }
        return false;
    }

    /**
     * Return whether the supplied schema component is a built-in datatype.
     * 
     * @param xsdComponent the schema component
     * @return true if the supplied schema component is a simple type and is one of the built-in simple types, or false otherwise
     */
    public static boolean isBuiltInDatatype( final XSDComponent xsdComponent ) {
        if (xsdComponent != null && xsdComponent instanceof XSDSimpleTypeDefinition) {
            final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)xsdComponent;
            return isBuiltInDatatype(simpleType);
        }
        return false;
    }

    /**
     * Return whether the supplied simple type is a built-in datatype.
     * 
     * @param simpleType the simple type
     * @return true if the supplied simple type is one of the built-in simple types, or false otherwise
     */
    public static boolean isBuiltInDatatype( final XSDSimpleTypeDefinition simpleType ) {
        final String namespaceUri = simpleType.getTargetNamespace();
        if (BUILT_IN_DATATYPE_NAMESPACE_URI.equals(namespaceUri)) {
            return true;
        }
        if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespaceUri)) {
            return true;
        }
        if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10.equals(namespaceUri)) {
            return true;
        }
        if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999.equals(namespaceUri)) {
            return true;
        }
        return false;
    }

    public static boolean isNillable( final XSDComponent component ) {
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration)component;
            return element.isNillable();
        }
        return false;
    }

    /**
     * This method returns the literal minOccurs on the XSDComponent.
     * 
     * @param component
     * @return
     * @see #getMinOccurs(XSDComponent)
     * @see #getMaxOccursLiteral(XSDComponent)
     * @since 4.2
     */
    public static int getMinOccursLiteral( final XSDComponent component ) {
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration)component;
            // Get the container ...
            final XSDConcreteComponent container = element.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccursLiteral(particle);
            }
        }
        if (component instanceof XSDModelGroupDefinition) {
            final XSDModelGroupDefinition group = (XSDModelGroupDefinition)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccursLiteral(particle);
            }
        }
        if (component instanceof XSDModelGroup) {
            final XSDModelGroup group = (XSDModelGroup)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccursLiteral(particle);
            }
        }
        if (component instanceof XSDWildcard) {
            final XSDWildcard any = (XSDWildcard)component;
            // Get the container ...
            final XSDConcreteComponent container = any.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccursLiteral(particle);
            }
        }
        if (component instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle)component;
            int minOccurs = particle.getMinOccurs();
            return minOccurs;
        }
        return 0;
    }

    /**
     * This method returns the literal maxOccurs on the XSDComponent.
     * 
     * @param component
     * @return
     * @see #getMaxOccurs(XSDComponent)
     * @see #getMinOccursLiteral(XSDComponent)
     * @since 4.2
     */
    public static int getMaxOccursLiteral( final XSDComponent component ) {
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration)component;
            // Get the container ...
            final XSDConcreteComponent container = element.getContainer();
            if (container instanceof XSDSchema) {
                return -1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccursLiteral(particle);
            }
        }
        if (component instanceof XSDModelGroupDefinition) {
            final XSDModelGroupDefinition group = (XSDModelGroupDefinition)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return -1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccursLiteral(particle);
            }
        }
        if (component instanceof XSDModelGroup) {
            final XSDModelGroup group = (XSDModelGroup)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return -1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccursLiteral(particle);
            }
        }
        if (component instanceof XSDWildcard) {
            final XSDWildcard any = (XSDWildcard)component;
            // Get the container ...
            final XSDConcreteComponent container = any.getContainer();
            if (container instanceof XSDSchema) {
                return -1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccursLiteral(particle);
            }
        }
        if (component instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle)component;
            int maxOccurs = particle.getMaxOccurs();
            return maxOccurs;
        }
        return -1;
    }

    /**
     * This method computes the effective minimum occurs for an XSDComponent; that is, the minimum number of times that the
     * component may appear in an XML document, not just the minimum number of times the component may appear in the context of
     * it's parent (see {@link #getMaxOccursLiteral(XSDComponent)}. Note that the model groups (e.g., sequences, choices, and
     * alls) have the ability to compound the minimum occurs. For example, the "author" element must appear at least zero times
     * given the following fragment:
     * <p>
     * <code>
     *    &lt;xsd:complexType name="typeA">
     *      &lt;xsd:sequence minOccurs="0" maxOccurs="unbounded">
     *        &lt;xsd:element name="author" type="typeB" /> 
     *      &lt;/xsd:sequence>
     *    &lt;/xsd:complexType>
     * </code>
     * </p>
     * The following fragment actually requires the "author" element to appear at least 8 times (1x2x4) and the "publisher"
     * element at least 0 times (0x4):
     * <p>
     * <code>
     *    &lt;xsd:complexType name="typeA">
     *      &lt;xsd:sequence minOccurs="4" maxOccurs="40">
     *        &lt;xsd:sequence minOccurs="2" maxOccurs="20">
     *          &lt;xsd:element minOccurs="1" name="author" type="typeB" /> 
     *        &lt;/xsd:sequence>
     *        &lt;xsd:element minOccurs="0" name="publisher" type="typeC" /> 
     *      &lt;/xsd:sequence>
     *    &lt;/xsd:complexType>
     * </code>
     * </p>
     * 
     * @param component
     * @return
     * @see #getMaxOccurs(XSDComponent)
     * @see #getMinOccursLiteral(XSDComponent)
     * @since 4.2
     */
    public static int getMinOccurs( final XSDComponent component ) {
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration)component;
            // Get the container ...
            final XSDConcreteComponent container = element.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccurs(particle);
            }
        }
        if (component instanceof XSDModelGroupDefinition) {
            final XSDModelGroupDefinition group = (XSDModelGroupDefinition)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccurs(particle);
            }
        }
        if (component instanceof XSDModelGroup) {
            final XSDModelGroup group = (XSDModelGroup)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccurs(particle);
            }
        }
        if (component instanceof XSDWildcard) {
            final XSDWildcard any = (XSDWildcard)component;
            // Get the container ...
            final XSDConcreteComponent container = any.getContainer();
            if (container instanceof XSDSchema) {
                return 0;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMinOccurs(particle);
            }
        }
        if (component instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle)component;
            int minOccurs = particle.getMinOccurs();
            if (minOccurs == 0) {
                return minOccurs;
            }

            // It is not unlimited, so walk up the owners and compound (multiply) the minOccurs
            // from sequences
            XSDConcreteComponent particleParent = particle.getContainer();
            while (particleParent instanceof XSDModelGroup || particleParent instanceof XSDModelGroupDefinition) {
                // If the particleParent is a choice, then the minOccurs is 0 ...
                if (particleParent instanceof XSDModelGroup && isChoice((XSDModelGroup)particleParent)) {
                    return 0;
                }

                // Get the particle of the model group (compositor) or model group definition (group) ...
                final XSDConcreteComponent particleGrandparent = particleParent.getContainer();
                if (particleGrandparent instanceof XSDParticle) {
                    final XSDParticle modelGroupParticle = (XSDParticle)particleGrandparent;
                    // Return the model group's min occurs, regardless of whether it is set (see defect 11343).
                    final int groupOccurs = modelGroupParticle.getMinOccurs();
                    minOccurs *= groupOccurs;

                    // Keep walking up to see if this sequence is contained by another sequence ...
                    particleParent = modelGroupParticle.getContainer();
                } else {
                    // Stop
                    break;
                }
                if (minOccurs == 0) {
                    break;
                }
            }
            return minOccurs;
        }
        return 0;
    }

    /**
     * This method computes the effective maximum occurs for an XSDComponent; that is, the maximum number of times that the
     * component may appear in an XML document, not just the maximum number of times the component may appear in the context of
     * it's parent (see {@link #getMaxOccursLiteral(XSDComponent)}. Note that the model groups (e.g., sequences, choices, and
     * alls) have the ability to compound the maximum occurs. For example, the "author" element may appear an unlimited number of
     * times given the following fragment:
     * <p>
     * <code>
     *    &lt;xsd:complexType name="typeA">
     *      &lt;xsd:sequence minOccurs="0" maxOccurs="unbounded">
     *        &lt;xsd:element name="author" type="typeB" /> 
     *      &lt;/xsd:sequence>
     *    &lt;/xsd:complexType>
     * </code>
     * </p>
     * The following fragment actually allows the "author" element to appear up to 8 times (1x2x4) and the "publisher" element up
     * to 4 times (1x4):
     * <p>
     * <code>
     *    &lt;xsd:complexType name="typeA">
     *      &lt;xsd:sequence maxOccurs="4">
     *        &lt;xsd:sequence maxOccurs="2">
     *          &lt;xsd:element name="author" type="typeB" /> 
     *        &lt;/xsd:sequence>
     *        &lt;xsd:element name="publisher" type="typeC" /> 
     *      &lt;/xsd:sequence>
     *    &lt;/xsd:complexType>
     * </code>
     * </p>
     * 
     * @param component
     * @return
     * @see #getMinOccurs(XSDComponent)
     * @see #getMaxOccursLiteral(XSDComponent)
     * @since 4.2
     */
    public static int getMaxOccurs( final XSDComponent component ) {
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration element = (XSDElementDeclaration)component;
            // Get the container ...
            final XSDConcreteComponent container = element.getContainer();
            if (container instanceof XSDSchema) {
                return 1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccurs(particle);
            }
        }
        if (component instanceof XSDModelGroupDefinition) {
            final XSDModelGroupDefinition group = (XSDModelGroupDefinition)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccurs(particle);
            }
        }
        if (component instanceof XSDModelGroup) {
            final XSDModelGroup group = (XSDModelGroup)component;
            // Get the container ...
            final XSDConcreteComponent container = group.getContainer();
            if (container instanceof XSDSchema) {
                return 1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccurs(particle);
            }
        }
        if (component instanceof XSDWildcard) {
            final XSDWildcard any = (XSDWildcard)component;
            // Get the container ...
            final XSDConcreteComponent container = any.getContainer();
            if (container instanceof XSDSchema) {
                return 1;
            }
            if (container instanceof XSDParticle) {
                final XSDParticle particle = (XSDParticle)container;
                return getMaxOccurs(particle);
            }
        }
        if (component instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle)component;
            int maxOccurs = particle.getMaxOccurs();
            if (maxOccurs == -1) {
                return maxOccurs;
            }

            // It is not unlimited, so walk up the owners and compound (multiply) the maxOccurs
            // from sequences
            XSDConcreteComponent particleParent = particle.getContainer();
            while (particleParent instanceof XSDModelGroup || particleParent instanceof XSDModelGroupDefinition) {
                // Get the particle of the model group (compositor) or model group definition (group) ...
                final XSDConcreteComponent particleGrandparent = particleParent.getContainer();
                if (particleGrandparent instanceof XSDParticle) {
                    final XSDParticle modelGroupParticle = (XSDParticle)particleGrandparent;
                    // Return the model group's max occurs, regardless of whether it is set (see defect 11343).
                    final int groupOccurs = modelGroupParticle.getMaxOccurs();
                    if (groupOccurs == -1) {
                        return groupOccurs;
                    }
                    maxOccurs *= groupOccurs;

                    // Keep walking up to see if this sequence is contained by another sequence ...
                    particleParent = modelGroupParticle.getContainer();
                } else {
                    // Stop
                    break;
                }
                if (maxOccurs == -1) {
                    break;
                }
            }
            return maxOccurs;
        }
        return 1;
    }

    /**
     * This method computes the use for an XSD Attribute.
     * 
     * @param component
     * @return One of:
     *         <ul>
     *         <li>XSDAttributeUseCategory.OPTIONAL_LITERAL,</li>
     *         <li>XSDAttributeUseCategory.PROHIBITED_LITERAL, or</li>
     *         <li>XSDAttributeUseCategory.REQUIRED_LITERAL</li>
     *         </ul>
     * @since 4.2
     */
    public static XSDAttributeUseCategory getUse( final XSDComponent component ) {
        XSDAttributeUse use = null;
        if (component instanceof XSDAttributeUse) {
            use = (XSDAttributeUse)component;
        } else {
            final XSDConcreteComponent parent = component.getContainer();
            if (parent instanceof XSDAttributeUse) {
                use = (XSDAttributeUse)parent;
            }
        }
        return (use == null ? XSDAttributeUseCategory.OPTIONAL_LITERAL : use.getUse());
    }

    /**
     * Returns the concatenated child text of the specified node. This method only looks at the immediate children of type
     * <code>Node.TEXT_NODE</code> or the children of any child node that is of type <code>Node.CDATA_SECTION_NODE</code> for the
     * concatenation. This method was copied from the org.apache.xerces.util.DOMUtil class.
     * 
     * @param node The node to look at.
     */
    public static String getChildText( final Node node ) {

        // is there anything to do?
        if (node == null) {
            return null;
        }

        // concatenate children text
        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                str.append(child.getNodeValue());
            } else if (type == Node.CDATA_SECTION_NODE) {
                str.append(getChildText(child));
            }
            child = child.getNextSibling();
        }

        // return text value
        return str.toString();

    }

    /**
     * Given an AppInfo Node, recurse through all the children and build a Tag / value structure for all of the Element children.
     * 
     * @param node The node to look at.
     */
    public static String getAppInfoText( final Node node ) {

        // is there anything to do?
        if (node == null) {
            return null;
        }

        // concatenate children text
        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        str.append("<Application Information>"); //$NON-NLS-1$

        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                str.append("\n\t<"); //$NON-NLS-1$
                str.append(child.getNodeName());
                str.append(">"); //$NON-NLS-1$
                str.append(getChildText(child));
                str.append("</"); //$NON-NLS-1$
                str.append(child.getNodeName());
                str.append(">"); //$NON-NLS-1$
            }
            child = child.getNextSibling();
        }

        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            final Node next = node.getAttributes().item(i);
            str.append("\n\t<"); //$NON-NLS-1$
            str.append(next.getNodeName());
            str.append(">"); //$NON-NLS-1$
            str.append(getChildText(next));
            str.append("</"); //$NON-NLS-1$
            str.append(next.getNodeName());
        }

        str.append("\n</Application Information>"); //$NON-NLS-1$

        // return text value
        return str.toString();

    }

    /**
     * Get the description from the annotation, if one exists, on the specified XSD object.
     * 
     * @param eObject
     * @return
     * @since 4.2
     */
    public static String getDescription( XSDConcreteComponent eObject ) {
        XSDAnnotation annotation = null;
        if (eObject instanceof XSDAnnotation) {
            annotation = (XSDAnnotation)eObject;
        } else {
            annotation = getAnnotation(eObject);
        }

        if (annotation != null) {
            final Iterator userInfos = annotation.getUserInformation().iterator();
            while (userInfos.hasNext()) {
                final Element userInfo = (Element)userInfos.next();
                final String value = getChildText(userInfo);
                if (value != null) {
                    return value;
                }
            }
            final Iterator appInfos = annotation.getApplicationInformation().iterator();
            while (appInfos.hasNext()) {
                final Element appInfo = (Element)appInfos.next();
                final String value = getAppInfoText(appInfo);
                if (value != null) {
                    return value;
                }
            }
        }
        return StringUtil.Constants.EMPTY_STRING;
    }

    /**
     * Recursively accumulates all text and CDATA type nodes beneath the specified node.
     * 
     * @param node
     * @return
     * @since 4.2
     */
    private static void accumulateTextNodes( Node node,
                                             List collection ) {
        while (node != null) {
            short type = node.getNodeType();
            if (type == Node.TEXT_NODE) {
                collection.add(node);
            } else if (type == Node.CDATA_SECTION_NODE) {
                // recurse
                accumulateTextNodes(node.getFirstChild(), collection);
            }
            node = node.getNextSibling();
        }
    }

    private static void argCheckIsResolved( EObject e ) {
        if (e.eIsProxy()) {
            throw new IllegalArgumentException(
                                               XsdPlugin.Util.getString("XsdSimpleTypeDefinitionAspect.Error_EObject_can_not_be_a_proxy", e.toString())); //$NON-NLS-1$
        }
    }

}

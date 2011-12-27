/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpace;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.query.mapping.xml.MappingAllNode;
import org.teiid.query.mapping.xml.MappingAttribute;
import org.teiid.query.mapping.xml.MappingBaseNode;
import org.teiid.query.mapping.xml.MappingChoiceNode;
import org.teiid.query.mapping.xml.MappingCommentNode;
import org.teiid.query.mapping.xml.MappingCriteriaNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.mapping.xml.MappingNodeConstants;
import org.teiid.query.mapping.xml.MappingOutputter;
import org.teiid.query.mapping.xml.MappingRecursiveElement;
import org.teiid.query.mapping.xml.MappingSequenceNode;
import org.teiid.query.mapping.xml.Namespace;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.RecursionErrorMode;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.ChoiceErrorMode;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.SoapEncoding;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.metamodels.xml.XmlValueHolder;
import com.metamatrix.metamodels.xml.namespace.NamespaceContext;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.metamodels.xml.util.XmlNamespaceComparator;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.xml.aspects.sql.MappingContext;
import com.metamatrix.modeler.internal.xml.aspects.sql.XmlDocumentMappingHelper;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * MappingDocumentFormatter
 */
public class MappingDocumentFormatter {

    static final String XSI_TYPE_ATTRIBUTE_NAME = "type"; //$NON-NLS-1$

    /**
     * Construct an instance of MappingDocumentFormatter.
     */
    public static MappingDocumentFormatter create( final XmlDocument xmlDoc,
                                                   final MappingContext mappingContext ) {
        final List treeMappingRoots = findTreeMappingRoot(xmlDoc);
        // Return nothing if there is no tree mapping root ...
        if (treeMappingRoots != null && treeMappingRoots.size() != 0) {
            final MappingClassSet mappingClassSet = findMappingClassSet(xmlDoc);
            if (mappingClassSet != null) {
                final MappingDocumentFormatter formatter = new MappingDocumentFormatter(xmlDoc, treeMappingRoots, mappingClassSet,
                                                                                        mappingContext);
                return formatter;
            }
        }
        return null;
    }

    /**
     * @param xmlDoc
     * @return
     */
    protected static MappingClassSet findMappingClassSet( final XmlDocument xmlDoc ) {
        // Get the resource that contains this object ..
        final Resource resource = xmlDoc.eResource();
        if (resource == null) return null;
        final Iterator iter = resource.getContents().iterator();
        while (iter.hasNext()) {
            final Object rootObj = iter.next();
            if (rootObj instanceof MappingClassSetContainer) {
                final MappingClassSetContainer container = (MappingClassSetContainer)rootObj;
                final Iterator mcsetiter = container.getMappingClassSets().iterator();
                while (mcsetiter.hasNext()) {
                    final MappingClassSet mcSet = (MappingClassSet)mcsetiter.next();
                    final EObject target = mcSet.getTarget();
                    if (target == xmlDoc) return mcSet;
                }
            }
        }
        return null;
    }

    /**
     * @param xmlDoc
     * @return
     */
    protected static List findTreeMappingRoot( final XmlDocument xmlDoc ) {
        // Get the resource that contains this object ..
        final Resource resource = xmlDoc.eResource();
        if (resource == null) return Collections.EMPTY_LIST;
        final List treeMappingRoots = new LinkedList();
        final Iterator iter = resource.getContents().iterator();
        while (iter.hasNext()) {
            final Object rootObj = iter.next();
            if (rootObj instanceof TransformationContainer) {
                final TransformationContainer container = (TransformationContainer)rootObj;
                final Iterator transformIter = container.getTransformationMappings().iterator();
                while (transformIter.hasNext()) {
                    final TransformationMappingRoot tmroot = (TransformationMappingRoot)transformIter.next();
                    if (tmroot instanceof TreeMappingRoot) {
                        final EObject target = tmroot.getTarget();
                        if (target == xmlDoc) treeMappingRoots.add(tmroot);
                    }
                }
            }
        }
        return treeMappingRoots;
    }

    static String getNamespacePrefix( final XmlDocumentNode elementOrAttribute,
                                      final NamespaceContext context,
                                      final MappingContext mappingContext ) {
        if (elementOrAttribute == null) return null;

        // Get the XSDComponent mapped to this document node ...
        final XSDComponent xsdComponent = elementOrAttribute.getXsdComponent();

        // Find the default target namespace to pass into the getNamespacePrefix(...) method. The default target
        // namespace is the namespace associated with this XSDComponent. If one does not exist, then we need
        // to walk up the xml document nodes to find one mapping to a target namespace.
        final XSDSchema startingSchema = getTargetNamespaceSchema(xsdComponent, context, mappingContext);
        final String defaultTargetNamespace = getTargetNamespace(startingSchema, elementOrAttribute, context, mappingContext);

        // Get the namespace prefix to assign to this XmlDocumentNode
        final String prefix = getNamespacePrefix(xsdComponent, context, mappingContext, defaultTargetNamespace, true, true);

        return prefix;
    }

    /**
     * Utility to get a non-empty namespace prefix regardless of what the actual XmlNamespace has for it's prefix. This method does
     * not actually change the XmlNamespace object.
     * 
     * @param ns
     * @return
     */
    static String getNamespacePrefix( final XmlNamespace ns ) {
        String prefix = ns.getPrefix();
        if (prefix == null || prefix.trim().length() == 0) prefix = XmlDocumentUtil.createXmlPrefixFromUri(ns.getUri());
        return prefix;
    }

    private static String getNamespacePrefix( final XSDComponent xsdComponent,
                                              final NamespaceContext context,
                                              final MappingContext mappingContext,
                                              final String defaultTargetNamespace,
                                              final boolean checkWhetherPrefixIsRequired,
                                              final boolean addIfMissing ) {
        if (xsdComponent == null) return null;

        final XSDSchema schema = getTargetNamespaceSchema(xsdComponent, context, mappingContext);
        if (schema == null) return null;

        final String uri = (schema.getTargetNamespace() == null ? defaultTargetNamespace : schema.getTargetNamespace());
        XmlNamespace namespace = context.getBestNamespace(uri);

        if (namespace == null) {
            // There is no namespace in the context ...
            // First see if the schema component is one of the built-ins (in XMLSchema-instance)
            if (XsdUtil.isBuiltInDatatype(xsdComponent)) {
                final String prefix = getSchemaNamespacePrefix(context);
                if (!checkWhetherPrefixIsRequired || isPrefixRequired(xsdComponent, schema, uri)) return prefix;
                return null;
            }

            // It's not a built-in, so see if we should add it ...
            if (uri != null && addIfMissing) {
                final XmlNamespace newNs = XmlDocumentFactory.eINSTANCE.createXmlNamespace();
                String prefix = XmlDocumentUtil.createXmlPrefixFromUri(uri);
                if (prefix == null) prefix = "nspace"; //$NON-NLS-1$
                newNs.setPrefix(prefix);
                newNs.setUri(uri);
                int counter = 0;
                while (!context.addXmlNamespace(newNs)) {
                    ++counter;
                    newNs.setPrefix(prefix + counter);
                }
                namespace = newNs;
            }
        }
        if (namespace != null) {
            final String actualPrefix = namespace.getPrefix();
            if (actualPrefix == null || actualPrefix.trim().length() == 0) // This is the default target namespace declaration, so
            // return null (see defect 13428)
            return null;
            final String prefix = getNamespacePrefix(namespace);
            if (!checkWhetherPrefixIsRequired || isPrefixRequired(xsdComponent, schema, uri)) return prefix;
            return null;
        }
        return null;
    }

    private static String getSchemaInstanceNamespacePrefix( final NamespaceContext context ) {
        final Iterator iter = context.getAllXmlNamespaces().iterator();
        while (iter.hasNext()) {
            final XmlNamespace nsDecl = (XmlNamespace)iter.next();
            final String uri = nsDecl.getUri();
            if (uri != null && uri.startsWith("http://www.w3.org/") && uri.endsWith("/XMLSchema-instance")) return getNamespacePrefix(nsDecl); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Add the soap encoding namespace ...
        final String defaultPrefix = "xsi"; //$NON-NLS-1$
        final XmlNamespace newNsDecl = XmlDocumentFactory.eINSTANCE.createXmlNamespace();
        newNsDecl.setPrefix(defaultPrefix);
        newNsDecl.setUri("http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$
        context.addXmlNamespace(newNsDecl);
        return defaultPrefix;
    }

    public static String getSchemaNamespacePrefix( final NamespaceContext context ) {
        final Iterator iter = context.getAllXmlNamespaces().iterator();
        while (iter.hasNext()) {
            final XmlNamespace nsDecl = (XmlNamespace)iter.next();
            final String uri = nsDecl.getUri();
            if (uri != null && uri.startsWith("http://www.w3.org/") && uri.endsWith("/XMLSchema")) return getNamespacePrefix(nsDecl); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Add the soap encoding namespace ...
        final String defaultPrefix = "xsd"; //$NON-NLS-1$
        final XmlNamespace newNsDecl = XmlDocumentFactory.eINSTANCE.createXmlNamespace();
        newNsDecl.setPrefix(defaultPrefix);
        newNsDecl.setUri("http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$
        context.addXmlNamespace(newNsDecl);
        return defaultPrefix;
    }

    /**
     * Method to return the complete value for the "soap-enc:arrayType" attribute on the supplied element. This method checks the
     * schema component referenced by the element, and sees if that complex type has (eventually) a base type of the SOAP-ENC XSD's
     * "ArrayType".
     * 
     * @param element the XML element for which the array type is to be determined; never null
     * @param context the namespace context from which namespace prefixes should be determined; never null
     * @return the value of the "soap-enc:arrayType" attribute; may be null if there is no type
     */
    protected static String getSoapArrayType( final XmlElement element,
                                              final NamespaceContext context,
                                              final MappingContext mappingContext ) {
        XSDComponent xsdComponent = element.getXsdComponent();
        while (xsdComponent != null) {
            if (xsdComponent instanceof XSDComplexTypeDefinition) {
                final XSDComplexTypeDefinition complexTypeDefn = (XSDComplexTypeDefinition)xsdComponent;
                if (XSDConstants.isAnyType(complexTypeDefn)) return null;
                final String name = complexTypeDefn.getName();
                if (Soap.ARRAY_TYPE_NAME.equals(name)) {
                    // Check the namespace of the complext type to see if it is the
                    final XSDSchema schema = complexTypeDefn.getSchema();
                    final String targetNS = schema.getTargetNamespace();
                    if (Soap.TARGET_NAMESPACE_URI.equals(targetNS)) {
                        // Find the common base type of all containable children ...
                        final XSDTypeDefinition schemaCompOfChildren = XsdUtil.getCommonBaseTypeForContained(element.getXsdComponent());
                        if (schemaCompOfChildren == null) {
                            final String namespacePrefix = getSchemaNamespacePrefix(context);
                            return namespacePrefix + ":anyType[]"; //$NON-NLS-1$
                        }

                        String namespacePrefix = getNamespacePrefix(schemaCompOfChildren,
                                                                    context,
                                                                    mappingContext,
                                                                    null,
                                                                    false,
                                                                    true);
                        // If the namespace prefix is null
                        if (namespacePrefix == null) // See if the schema component is one of the built-ins (in XMLSchema-instance)
                        if (XsdUtil.isBuiltInDatatype(schemaCompOfChildren)) namespacePrefix = getSchemaNamespacePrefix(context);

                        String result = null;
                        if (namespacePrefix != null && namespacePrefix.trim().length() != 0) result = namespacePrefix
                                                                                                      + ":" + schemaCompOfChildren.getName() + "[]"; //$NON-NLS-1$ //$NON-NLS-2$
                        else result = schemaCompOfChildren.getName() + "[]"; //$NON-NLS-1$
                        return result;
                    }
                } else // This complex type is not the SOAP ArrayType, so go to the base type ...
                xsdComponent = complexTypeDefn.getBaseTypeDefinition();
            }
            if (xsdComponent instanceof XSDSimpleTypeDefinition) // ArrayType is a complex type, so this is definitely not a SOAP
            // ArrayType
            return null;
            if (xsdComponent instanceof XSDElementDeclaration) {
                final XSDElementDeclaration elmDeclaration = (XSDElementDeclaration)xsdComponent;
                xsdComponent = elmDeclaration.getTypeDefinition();
            }
            // Prevent the infinite loop if processing a type that this method cannot interpret
            if (!(xsdComponent instanceof XSDComplexTypeDefinition) && !(xsdComponent instanceof XSDSimpleTypeDefinition)
                && !(xsdComponent instanceof XSDElementDeclaration)) return null;

        }

        return null;
    }

    private static String getSoapEncodingNamespacePrefix( final NamespaceContext context ) {
        final XmlNamespace namespace = context.getBestNamespace(Soap.TARGET_NAMESPACE_URI);
        if (namespace != null) {
            final String prefix = getNamespacePrefix(namespace);
            return prefix;
        }
        // Add the soap encoding namespace ...
        final String defaultPrefix = Soap.DEFAULT_NAMESPACE_PREFIX;
        final XmlNamespace newNsDecl = XmlDocumentFactory.eINSTANCE.createXmlNamespace();
        newNsDecl.setPrefix(defaultPrefix);
        newNsDecl.setUri(Soap.TARGET_NAMESPACE_URI);
        context.addXmlNamespace(newNsDecl);
        return defaultPrefix;
    }

    /**
     * Return the target namespace string for the specified XmlDocumentNode. If the XmlDocumentNode has no XSDComponent reference or
     * is determined to reference a global schema then null is returned.
     * 
     * @param element
     * @param context
     * @param mappingContext
     * @return
     * @since 4.2
     */
    private static String getTargetNamespace( final XSDSchema startingSchema,
                                              final XmlDocumentNode element,
                                              final NamespaceContext context,
                                              final MappingContext mappingContext ) {
        if (element == null) return null;
        // Get the XSDComponent mapped to this document node ...
        final XSDComponent xsdComponent = element.getXsdComponent();

        // If the xsd component is a reference, follow the references looking for a target namespace
        if (xsdComponent instanceof XSDFeature) {
            final XSDComponent resolvedComponent = XsdUtil.getResolved((XSDFeature)xsdComponent);
            if (resolvedComponent != null && resolvedComponent != xsdComponent) {
                final String resolvedTNS = ((XSDFeature)resolvedComponent).getTargetNamespace();
                if (!CoreStringUtil.isEmpty(resolvedTNS)) return resolvedTNS;
            }
        }

        // Get the target namespace from the schema associated with this xsd component
        final XSDSchema schema = getTargetNamespaceSchema(xsdComponent, context, mappingContext);
        final String targetNS = (schema == null ? null : schema.getTargetNamespace());

        // If we find a target namespace ...
        if (!CoreStringUtil.isEmpty(targetNS)) {

            // If the starting schema associated with the initial call to this method has a null
            // target namespace then it may be either a chameleon schema or a global schema. If
            // it is a chameleon schema then the current schema reference, which has a target namespace,
            // must have an include declaration to the starting schema. We will check for that now.
            if (startingSchema != null && startingSchema.getTargetNamespace() == null) {

                // Check for an XSDInclude to the starting schema
                if (hasMatchingIncludeDirective(schema, startingSchema)) // The starting schema is a chameleon schema that has been
                // included in
                // this schema. Therefore we assume the target namespace of this schema.
                return targetNS;
                // An XSDInclude to the starting schema was not found so the starting schema must be
                // a global schema with no target namespace
                return null;
            }

            // If the starting schema associated with the initial call to this method has a target
            // namespace so we return it.
            return targetNS;
        }

        // If the target namespace is not defined for this schema, as in the case of a global
        // schema or a chameleon schema, go to the parent element in the document and try again.
        final XmlElement owner = getXmlElementContainer(element);
        return getTargetNamespace(startingSchema, owner, context, mappingContext);
    }

    /**
     * Return the XSDSchema reference for the specified XSDComponent or null if the XSDComponent is null. If the XSDComponent is an
     * eProxy the method will try to resolve the XSDResource. If the component is a reference the method will return the referenced
     * schema.
     * 
     * @param xsdComponent
     * @param context
     * @param mappingContext
     * @return
     * @since 4.2
     */
    private static XSDSchema getTargetNamespaceSchema( XSDComponent xsdComponent,
                                                       final NamespaceContext context,
                                                       final MappingContext mappingContext ) {
        if (xsdComponent == null) return null;
        XSDSchema schema = xsdComponent.getSchema();
        if (schema == null && xsdComponent.eIsProxy()) {
            xsdComponent = (XSDComponent)EcoreUtil.resolve(xsdComponent, mappingContext.getResourceSet());
            schema = xsdComponent.getSchema();
            if (schema == null) {
                final Object componentURi = ModelerCore.getModelEditor().getUri(xsdComponent);
                final String msg = PluginConstants.Util.getString("MappingDocumentFormatter.Unable_to_determine_schema_in_the_workspace_for_XsdComponent_{0}_when_deriving_Namespace_Prefix_1", componentURi); //$NON-NLS-1$
                PluginConstants.Util.log(IStatus.ERROR, msg);
                return null;
            }
        }

        // If the component is a reference, find out if the referenced schema has a target namespace ...
        if (xsdComponent instanceof XSDFeature) {
            final XSDComponent resolvedComponent = XsdUtil.getResolved((XSDFeature)xsdComponent);
            if (resolvedComponent != null && resolvedComponent != xsdComponent) {
                final XSDSchema resolvedSchema = resolvedComponent.getSchema();
                String resolvedTNS = ((XSDFeature)resolvedComponent).getTargetNamespace();
                if (CoreStringUtil.isEmpty(resolvedTNS)) resolvedTNS = resolvedSchema.getTargetNamespace();
                if (resolvedTNS != null && resolvedTNS.trim().length() != 0) // The referenced schema DOES have a target namespace,
                // so this is the
                // namespace that should be represented by the prefix (if there is one)
                if (resolvedSchema != null && resolvedSchema.eIsProxy()) {
                    final URI proxyURI = ((InternalEObject)resolvedSchema).eProxyURI();
                    if (proxyURI != null) {
                        final XSDResourceImpl resource = (XSDResourceImpl)mappingContext.getResourceSet().getResource(proxyURI.trimFragment(),
                                                                                                                      true);
                        if (resource != null) schema = resource.getSchema();
                    }
                } else schema = resolvedSchema;
            }
        }

        return schema;
    }

    private static XmlElement getXmlElementContainer( final XmlDocumentNode element ) {
        if (element == null) return null;
        EObject owner = element.eContainer();
        while ((owner != null) && !(owner instanceof XmlElement))
            owner = owner.eContainer();
        return (owner instanceof XmlElement ? (XmlElement)owner : null);
    }

    private static boolean hasMatchingIncludeDirective( final XSDSchema schemaWithDirectives,
                                                        final XSDSchema schemaToCheck ) {
        if (schemaWithDirectives != null && schemaToCheck != null) for (final Object content : schemaWithDirectives.eContents())
            if (content instanceof XSDInclude) {
                final XSDInclude includeDeclaration = (XSDInclude)content;
                final XSDSchema resolvedSchema = includeDeclaration.getResolvedSchema();
                final String includeSchemaLocation = resolvedSchema.getSchemaLocation();
                if (includeSchemaLocation != null && includeSchemaLocation.equals(schemaToCheck.getSchemaLocation())) return true;
            }
        return false;
    }

    /**
     * Return whether the XSD component should be prefixed in an XML document.
     * <p>
     * If the XSD component is an element and:
     * <ul>
     * <li>is global and the schema has a target namespace, then the element needs to be qualified</li>
     * <li>is global and the schema does not have a target namespace, then the element is unqualified</li>
     * <li>is a reference, then the result is this method called on the referenced element</li>
     * <li>is local, the value of the "form" attribute determines whether the element is qualified or unqualified. If the local
     * element has no "form" attribute, the value of the schema's "elementFormDefault" attribute determines whether the element is
     * qualified or unqualified</li>
     * </ul>
     * </p>
     * <p>
     * If the XSD component is an attribute and:
     * <ul>
     * <li>is global and the schema has a target namespace, then the attribute needs to be qualified</li>
     * <li>is global and the schema does not have a target namespace, then the attribute is unqualified</li>
     * <li>is a reference, then the result is this method called on the referenced attribute</li>
     * <li>is local, the value of the "form" attribute determines whether the attribute is qualified or unqualified. If the local
     * attribute has no "form" attribute, the value of the schema's "attributeFormDefault" attribute determines whether the
     * attribute is qualified or unqualified</li>
     * </ul>
     * </p>
     * <p>
     * Per the XSD specification, the form is determined completely by the element/attribute declaration and it's parent schema.
     * (This means that element/attribute refs nor their schema are used to determine whether an element/attribute should be
     * qualified.) Therefore, we always resolve any references.
     * </p>
     * 
     * @param component the XML Schema component
     * @return true if entity is to be unqualified.
     */
    protected static boolean isPrefixRequired( final XSDComponent component,
                                               final XSDSchema schema,
                                               final String schemaNamespaceUri ) {
        // If the component is global, then it must be qualified ...
        final boolean global = XsdUtil.isGlobal(component);
        if (global) {
            if (schemaNamespaceUri == null || schemaNamespaceUri.trim().length() == 0) // There is no schema namespace, so it is
            // unqualified
            return false;
            // There is a schema namespace, so it must be qualified
            return true;
        }

        // If an element or attribute ...
        if (component instanceof XSDFeature) { // supertype of both XSDAttributeDeclaration and XSDElementDeclaration
            final XSDFeature feature = (XSDFeature)component;

            // See if it is a reference ...
            final XSDFeature resolvedFeature = XsdUtil.getResolved(feature);
            if (resolvedFeature != null && resolvedFeature != feature) {
                // 'component' is a ref, so call this method with the resolved object ...
                final XSDSchema schemaForResolved = resolvedFeature.getSchema();
                final String uri = schemaForResolved.getTargetNamespace();
                // and use the resolved schema!!!
                return isPrefixRequired(resolvedFeature, resolvedFeature.getSchema(), uri);
            }

            // Otherwise, it is local and not a reference, so look at the "form" attribute ...
            XSDForm form = null;
            // If the form attribute is explicitly set on this declaration ...
            if (feature.isSetForm()) // It is set, so get it ...
            form = feature.getForm();
            else // There is no form attribute, so go to the schema and look for the default
            if (component instanceof XSDElementDeclaration) {
                if (schema.isSetElementFormDefault()) form = schema.getElementFormDefault();
                else form = XSDForm.UNQUALIFIED_LITERAL;
            } else if (component instanceof XSDAttributeDeclaration) if (schema.isSetAttributeFormDefault()) form = schema.getAttributeFormDefault();
            else form = XSDForm.UNQUALIFIED_LITERAL;
            if (form != null) switch (form.getValue()) {
                case XSDForm.QUALIFIED:
                    return true;
                case XSDForm.UNQUALIFIED:
                    return false;
            }
        }
        // By default, return true
        return true;
    }

    private boolean includeSoapDefaultEncoding = false;

    private final XmlDocument xmlDoc;

    private final MappingContext mappingContext;

    private final List treeMappingRoots; // instances of TreeMappingRoot

    private final XmlDocumentMappingHelper helper;

    private boolean indent;

    private boolean newlines;

    /**
     * Construct an instance of MappingDocumentFormatter.
     */
    public MappingDocumentFormatter( final XmlDocument xmlDoc,
                                     final List treeMappingRoots,
                                     final MappingClassSet mappingClassSet,
                                     final MappingContext mappingContext ) {
        super();
        CoreArgCheck.isNotNull(xmlDoc);
        CoreArgCheck.isNotNull(treeMappingRoots);
        CoreArgCheck.isNotNull(mappingClassSet);
        this.includeSoapDefaultEncoding = false;
        this.xmlDoc = xmlDoc;
        this.treeMappingRoots = treeMappingRoots;
        this.mappingContext = mappingContext;
        this.helper = new XmlDocumentMappingHelper(this.treeMappingRoots);
    }

    protected void createAttributeNode( final MappingElement parent,
                                        final XmlAttribute element,
                                        final ElementInfo elementInfo,
                                        final MappingContext mappingContext ) {
        final String name = element.getName();
        final String nsPrefix = getNamespacePrefix(element, elementInfo.getNamespaceContext(), mappingContext);
        MappingAttribute attribute = null;

        Namespace namespace = getNamespace(nsPrefix);
        // if the namespace equals to "xmlns" then we are defining a namespace attribute
        if (nsPrefix != null && nsPrefix.equalsIgnoreCase(MappingNodeConstants.NAMESPACE_DECLARATION_ATTRIBUTE_NAMESPACE)) {
            // this is default name space where only "xmlns" is defined. We do not need to global map
            // as there may be more(I guess..)
            namespace = new Namespace("", getFixedValue(element)); //$NON-NLS-1$
            parent.addNamespace(namespace);
        } else if (name != null && nsPrefix != null
                   && nsPrefix.equalsIgnoreCase(MappingNodeConstants.NAMESPACE_DECLARATION_ATTRIBUTE_NAMESPACE)) {
            namespace = new Namespace(name, getFixedValue(element));
            parent.addNamespace(namespace);
        } else {
            attribute = new MappingAttribute(name, namespace);
            attribute.setNameInSource(getNameInSource(element));
            attribute.setDefaultValue(getDefaultValue(element));
            attribute.setValue(getFixedValue(element));
            attribute.setExclude(element.isExcludeFromDocument());
            attribute.setNormalizeText(getXsiTypeTextNormalization(element, mappingContext));
            // attribute.setOptional(isOptional(element));
            if (parent != null) parent.addAttribute(attribute);
        }
    }

    protected MappingNode createChoiceNode( MappingBaseNode parent,
                                            final XmlChoice choice,
                                            final ElementInfo elementInfo ) {
        final MappingCriteriaNode criteria = createCriteriaNode(choice);
        if (parent != null && parent instanceof MappingChoiceNode && criteria != null) parent = ((MappingChoiceNode)parent).addCriteriaNode(criteria);

        final MappingChoiceNode choiceNode = new MappingChoiceNode(choice.getDefaultErrorMode().getValue() == ChoiceErrorMode.THROW);
        choiceNode.setExclude(choice.isExcludeFromDocument());
        choiceNode.setSource(getSource(choice));
        parent.addChoiceNode(choiceNode);

        choiceNode.addStagingTable(getStagingTable(choice));
        return choiceNode;
    }

    protected MappingNode createCommentNode( final MappingElement parent,
                                             final XmlComment comment,
                                             final ElementInfo elementInfo ) {
        final String text = comment.getText();
        if (text != null && text.trim().length() > 0 && parent != null) parent.addCommentNode(new MappingCommentNode(text));
        return null;
    }

    MappingCriteriaNode createCriteriaNode( final ChoiceOption element ) {
        String criteria = getCriteria(element);
        final boolean defalt = (element.getDefaultFor() != null);

        // if criteira node created with no default criteria, then assign some dummy
        // criteria so that we have some valid criteria.
        if (criteria == null && !defalt) criteria = "TRUE = FALSE"; //$NON-NLS-1$
        return new MappingCriteriaNode(criteria, defalt);
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingDocument createDocumentNode( final XmlRoot xmlRoot,
                                                  final ElementInfo rootElementInfo,
                                                  final MappingContext mappingContext ) {

        // Set the SOAP encoding information ...
        this.includeSoapDefaultEncoding = xmlDoc.getSoapEncoding().getValue() == SoapEncoding.DEFAULT;
        final MappingDocument doc = new MappingDocument(xmlDoc.getEncoding(), xmlDoc.isFormatted());
        final MappingElement rootNode = processElementNode(xmlRoot, rootElementInfo, mappingContext);
        doc.addChildElement(rootNode);
        return doc;
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingNode createElementNode( MappingBaseNode parent,
                                             final XmlElement element,
                                             final ElementInfo elementInfo,
                                             final MappingContext mappingContext ) {
        final MappingCriteriaNode criteria = createCriteriaNode(element);
        if (parent != null && parent instanceof MappingChoiceNode && criteria != null) parent = ((MappingChoiceNode)parent).addCriteriaNode(criteria);

        final MappingElement node = processElementNode(element, elementInfo, mappingContext);
        if (parent != null) parent.addChildElement(node);
        return node;
    }

    /**
     * Create the mapping document given the supplied information ...
     * 
     * @param xmlDoc
     * @param treeMappingRoot
     * @param mappingClassSet
     * @return
     */
    public MappingDocument createMapping() {
        // Initialize the helper (which contains the map from XML->MappingClassObject
        this.helper.initialize();

        final XmlRoot xmlRootElement = this.xmlDoc.getRoot();
        // final SchemaIncludeMap schemaIncludeMap = new SchemaIncludeMap(moe);
        final NamespaceContext nsContext = new NamespaceContext(xmlRootElement, null);
        final ElementInfo elementInfo = new ElementInfo(nsContext, null); // not null! see defect 11240

        // Create the root of the mapping node tree ...
        final MappingDocument docMappingNode = createDocumentNode(xmlRootElement, elementInfo, this.mappingContext);
        final MappingElement rootNode = (MappingElement)docMappingNode.getRootNode();

        // Create the rest of the mapping node tree.
        // When creating the mapping node tree the server assumes that attributes
        // of an element are added before its child elements. The order below must
        // not be changed.
        processNamespaces(xmlRootElement.getDeclaredNamespaces(), rootNode, nsContext, elementInfo);
        processChildren(xmlRootElement.getAttributes(), rootNode, nsContext, elementInfo);
        processChildren(xmlRootElement.getComments(), rootNode, nsContext, elementInfo);
        processChildren(xmlRootElement.getEntities(), rootNode, nsContext, elementInfo);
        processChildren(xmlRootElement.getProcessingInstructions(), rootNode, nsContext, elementInfo);

        return docMappingNode;
    }

    /**
     * Recursive method to generate Mapping Objects at and beneath the specified node.
     * 
     * @param entity the XmlDocumentEntity in the XML document.
     * @param parent parent MappingNode of the MappingNode to be created in this method call; this should never be null
     * @param context the namespace context for this entity ...
     */
    protected void createMapping( final XmlDocumentEntity entity,
                                  final MappingNode parent,
                                  final NamespaceContext namespaceContext,
                                  final MappingContext mappingContext,
                                  final ElementInfo parentInfo ) {
        // 
        final int classifierId = entity.eClass().getClassifierID();
        MappingNode entityMappingNode = null;
        ElementInfo entityInfo = parentInfo;
        NamespaceContext entityNamespaceContext = namespaceContext;
        switch (classifierId) {
            case XmlDocumentPackage.XML_ELEMENT:
                final XmlElement element = (XmlElement)entity;
                entityNamespaceContext = new NamespaceContext(element, namespaceContext);
                entityInfo = new ElementInfo(entityNamespaceContext, parentInfo);

                entityMappingNode = createElementNode((MappingBaseNode)parent, element, entityInfo, mappingContext);

                processNamespaces(element.getDeclaredNamespaces(), entityMappingNode, entityNamespaceContext, entityInfo);
                processChildren(element.getAttributes(), entityMappingNode, entityNamespaceContext, entityInfo);
                processChildren(element.getComments(), entityMappingNode, entityNamespaceContext, entityInfo);
                processChildren(element.getEntities(), entityMappingNode, entityNamespaceContext, entityInfo);
                processChildren(element.getProcessingInstructions(), entityMappingNode, entityNamespaceContext, entityInfo);
                break;
            case XmlDocumentPackage.XML_ATTRIBUTE:
                createAttributeNode((MappingElement)parent, (XmlAttribute)entity, entityInfo, mappingContext);
                break;
            case XmlDocumentPackage.XML_NAMESPACE:
                createNamespaceAttribute((MappingElement)parent, (XmlNamespace)entity, entityInfo);
                break;
            case XmlDocumentPackage.XML_ALL:
            case XmlDocumentPackage.XML_SEQUENCE:
                final XmlContainerNode container = (XmlContainerNode)entity;
                entityMappingNode = createSequenceNode((MappingBaseNode)parent, container, entityInfo);
                // Case 5069 - removed lines - replaced with eContents - was resulting in implied ordering of output doc.
                // processChildren(container.getContainers(),entityMappingNode,namespaceContext,parentInfo);
                // processChildren(container.getElements(),entityMappingNode,namespaceContext,parentInfo);
                final List kids = container.eContents();
                processChildren(kids, entityMappingNode, namespaceContext, parentInfo);
                break;
            case XmlDocumentPackage.XML_CHOICE:
                final XmlChoice choiceNode = (XmlChoice)entity;
                entityMappingNode = createChoiceNode((MappingBaseNode)parent, choiceNode, entityInfo);
                final List optionsInOrder = choiceNode.getOrderedChoiceOptions();
                processChildren(optionsInOrder, entityMappingNode, namespaceContext, parentInfo);
                break;
            case XmlDocumentPackage.XML_FRAGMENT:
                // entityMappingNode = createMappingNode(parentMappingNode,(ProcessingInstruction)entity,entityInfo);
                // processChildren = true;
                break;
            case XmlDocumentPackage.XML_COMMENT:
                createCommentNode((MappingElement)parent, (XmlComment)entity, entityInfo);
                break;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION:
                // entityMappingNode = createMappingNode(parentMappingNode,(ProcessingInstruction)entity,
                // namespaceContext,entityInfo);
                break;
        }

    }

    public String createMappingString() throws Exception {
        final MappingDocument mapping = createMapping();

        // Output the mapping objects to a stream
        String result = null;
        OutputStream moStream = null;
        try {
            moStream = new ByteArrayOutputStream();
            final PrintWriter pw = new PrintWriter(moStream, true);
            final MappingOutputter outputter = new MappingOutputter();
            outputter.write(mapping, pw); // TODO FIX/REPLACE??? , isNewlines(), isIndent());
            pw.flush();

            result = moStream.toString();
        } catch (final Exception e) {
            throw e;
        } finally {
            if (moStream != null) {
                try {
                    moStream.close();
                } catch (final IOException e1) {
                    PluginConstants.Util.log(e1);
                }
                moStream = null;
            }
        }
        return result;
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected void createNamespaceAttribute( final MappingElement parent,
                                             final XmlNamespace ns,
                                             final ElementInfo elementInfo ) {
        final Namespace namespace = new Namespace(ns.getPrefix(), ns.getUri());
        parent.addNamespace(namespace);
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingNode createSequenceNode( MappingBaseNode parent,
                                              final XmlContainerNode compositor,
                                              final ElementInfo elementInfo ) {
        final MappingCriteriaNode criteria = createCriteriaNode(compositor);
        if (parent != null && parent instanceof MappingChoiceNode && criteria != null) parent = ((MappingChoiceNode)parent).addCriteriaNode(criteria);

        MappingBaseNode seqNode = null;
        if (compositor instanceof XmlSequence) seqNode = parent.addSequenceNode(new MappingSequenceNode());
        else if (compositor instanceof XmlAll) seqNode = parent.addAllNode(new MappingAllNode());
        seqNode.setExclude(compositor.isExcludeFromDocument());
        seqNode.setSource(getSource(compositor));

        seqNode.addStagingTable(getStagingTable(compositor));
        return seqNode;
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingNode createSoapArrayTypeAttribute( final MappingElement parent,
                                                        final ElementInfo elementInfo ) {
        final String value = elementInfo.getSoapArrayType();
        if (value == null) return null;

        final NamespaceContext context = elementInfo.getNamespaceContext();
        final String prefix = getSoapEncodingNamespacePrefix(context);
        final Namespace namespace = getNamespace(prefix);

        final MappingAttribute attribute = new MappingAttribute(Soap.ARRAY_TYPE_XML_ATTRIBUTE_NAME, namespace);
        attribute.setValue(value);
        attribute.setOptional(true);
        attribute.setAlwaysInclude(true);

        parent.addAttribute(attribute);
        return attribute;
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingAttribute createXsiTypeAttribute( final MappingElement parent,
                                                       final ElementInfo elementInfo ) {
        final String value = elementInfo.getXsiType();
        if (value == null) return null;

        final NamespaceContext context = elementInfo.getNamespaceContext();
        final String prefix = getSchemaInstanceNamespacePrefix(context);
        final Namespace namespace = getNamespace(prefix);

        final MappingAttribute attribute = new MappingAttribute(XSI_TYPE_ATTRIBUTE_NAME, namespace);
        attribute.setValue(value);
        attribute.setOptional(true);
        attribute.setAlwaysInclude(true);

        parent.addAttribute(attribute);
        return attribute;
    }

    String getBuitInType( final XmlElement element ) {
        final MappingClassColumn mappingClassColumn = this.helper.getMappingClassColumn(element);
        if (mappingClassColumn != null) {
            final SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(mappingClassColumn);

            EObject dataType = columnAspect.getDatatype(mappingClassColumn);

            final DatatypeManager dtm = ModelerCore.getBuiltInTypesManager();

            CoreArgCheck.isNotNull(dtm);

            try {
                dataType = dtm.getDatatypeForXsdType(dataType);
            } catch (final ModelerCoreException err) {
                PluginConstants.Util.log(err);
            }
            if (dataType != null && dtm.isBuiltInDatatype(dataType)) return dtm.getName(dataType);
        }
        return MappingNodeConstants.Defaults.DEFAULT_BUILT_IN_TYPE;
    }

    String getCriteria( final ChoiceOption element ) {
        String choiceCriteria = element.getChoiceCriteria();
//        if (choiceCriteria != null && choiceCriteria.indexOf(UUID.PROTOCOL) >= 0) // Defect 23725 - changed call to get the
        // element's Container object. CANNOT ASSUME
        // RESOURCE SET is CONTAINER
//        choiceCriteria = SqlConverter.convertUUIDsToFullNames(choiceCriteria, ModelerCore.getContainer(element));
        return choiceCriteria;
    }

    String getDefaultValue( final XmlValueHolder valueHolder ) {
        final String value = valueHolder.getValue();
        if (value != null) {
            final ValueType valueType = valueHolder.getValueType();
            if (valueType.getValue() == ValueType.DEFAULT) return value;
        }
        return null;
    }

    String getFixedValue( final XmlValueHolder valueHolder ) {
        final String value = valueHolder.getValue();
        if (value != null) {
            final ValueType valueType = valueHolder.getValueType();
            if (valueType.getValue() == ValueType.FIXED) return value;
        }
        return null;
    }

    protected String getFullName( final EObject object ) {
        // find the SqlTableAspect for the mapping class ...
        final SqlAspect sqlAspect = (SqlAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(object, SqlAspect.class);
        if (sqlAspect != null) return sqlAspect.getFullName(object);
        return null;
    }

    int getMaxOccurrences( final XmlElement element ) {
        final XSDComponent xsdComponent = element.getXsdComponent();
        if (xsdComponent != null) return XsdUtil.getMaxOccurs(xsdComponent);
        return MappingNodeConstants.Defaults.DEFAULT_CARDINALITY_MAXIMUM_BOUND.intValue();
    }

    int getMinOccurrences( final XmlElement element ) {
        final XSDComponent xsdComponent = element.getXsdComponent();
        if (xsdComponent != null) return XsdUtil.getMinOccurs(xsdComponent);
        return MappingNodeConstants.Defaults.DEFAULT_CARDINALITY_MINIMUM_BOUND.intValue();
    }

    String getNameInSource( final XmlDocumentEntity element ) {
        final MappingClassColumn mappingClassColumn = this.helper.getMappingClassColumn(element);
        if (mappingClassColumn != null) return getFullName(mappingClassColumn);
        return null;
    }

    Namespace getNamespace( final String prefix ) {
        if (prefix != null) return new Namespace(prefix);
        return MappingNodeConstants.NO_NAMESPACE;
    }

    String getRecursionCriteria( final XmlElement element ) {
        String criteria = null;
        final MappingClass mappingClass = this.helper.getMappingClass(element);
        if (mappingClass != null && mappingClass.isRecursionAllowed() && mappingClass.isRecursive()) {
            criteria = mappingClass.getRecursionCriteria();
            // Rewrite the recursion criteria in terms of the root mapping class (defect 17576)
            if (!CoreStringUtil.isEmpty(criteria)) criteria = CoreStringUtil.replaceAll(criteria,
                                                                                        getFullName(mappingClass),
                                                                                        getRecursionMappingClass(element));
        }
        return criteria;
    }

    int getRecursionLimit( final XmlElement element ) {
        final MappingClass mappingClass = this.helper.getMappingClass(element);
        if (mappingClass != null && mappingClass.isRecursionAllowed() && mappingClass.isRecursive()) return mappingClass.getRecursionLimit();
        return MappingNodeConstants.Defaults.DEFAULT_RECURSION_LIMIT.intValue();
    }

    String getRecursionMappingClass( final XmlElement element ) {
        final MappingClass rootMappingClass = this.helper.getRecusionRootMappingClass(element);
        if (rootMappingClass != null) return getFullName(rootMappingClass);
        return null;
    }

    private String getSource( final XmlContainerNode compositor ) {
        String source = null;
        // Find the MappingClass that is bound to this element ...
        final MappingClass mappingClass = this.helper.getMappingClass(compositor);
        if (mappingClass != null) {
            final String fullName = getFullName(mappingClass);
            if (fullName != null) source = fullName;
        }
        return source;
    }

    String getSource( final XmlDocumentEntity element ) {
        final MappingClass mappingClass = this.helper.getMappingClass(element);
        if (mappingClass != null) return getFullName(mappingClass);
        return null;
    }

    private String getStagingTable( final XmlDocumentEntity compositor ) {
        String stagingTable = null;
        // Find the StagingTables that are bound to this element ...
        final StagingTable[] tempGroups = this.helper.getStagingTables(compositor);
        if (tempGroups != null && tempGroups.length != 0) for (final StagingTable st : tempGroups) {
            final String fullName = getFullName(st);
            if (fullName != null) stagingTable = fullName;
        }
        return stagingTable;
    }

    /**
     * Method to return the complete value for the "xsi:type" attribute on the supplied element.
     * 
     * @param element the XML element for which the type is to be determined; never null
     * @param context the namespace context from which namespace prefixes should be determined; never null
     * @return the value of the "xsi:type" attribute; may be null if there is no type
     */
    protected String getXsiType( final XmlElement element,
                                 final NamespaceContext context,
                                 final MappingContext mappingContext ) {
        // Get the type ...
        final XSDComponent xsdComponent = element.getXsdComponent();
        if (xsdComponent == null) return null;

        // Find the type name and the namespace URI ...
        final XSDTypeDefinition typeDefn = XsdUtil.getType(xsdComponent);
        final String typeName = typeDefn.getName();
        final String nsPrefix = getNamespacePrefix(typeDefn, context, mappingContext, null, false, true);

        // Compute what the xsi:type value would be ...
        return (nsPrefix == null ? typeName : nsPrefix + ":" + typeName); //$NON-NLS-1$
    }

    /**
     * Method to return the complete value for the "xsi:type" attribute on the supplied element.
     * 
     * @param element the XML element for which the type is to be determined; never null
     * @param mappingContext the mapping context
     * @return the mapping node property value for text normalization
     */
    protected String getXsiTypeTextNormalization( final XmlDocumentNode node,
                                                  final MappingContext mappingContext ) {
        // Get the type ...
        final XSDComponent xsdComponent = node.getXsdComponent();
        if (xsdComponent == null) return MappingNodeConstants.Defaults.DEFAULT_NORMALIZE_TEXT;

        // Find the type and its underlying whitespace normalization if available ...
        final XSDTypeDefinition typeDefn = XsdUtil.getType(xsdComponent);
        if (typeDefn instanceof XSDSimpleTypeDefinition) {
            final XSDSimpleTypeDefinition simpleTypeDefn = (XSDSimpleTypeDefinition)typeDefn;
            final XSDWhiteSpaceFacet facet = simpleTypeDefn.getEffectiveWhiteSpaceFacet();
            if (facet != null) {
                final XSDWhiteSpace whiteSpaceEnum = facet.getValue();
                if (whiteSpaceEnum != null) switch (whiteSpaceEnum.getValue()) {
                    case XSDWhiteSpace.PRESERVE:
                        return MappingNodeConstants.NORMALIZE_TEXT_PRESERVE;
                    case XSDWhiteSpace.REPLACE:
                        return MappingNodeConstants.NORMALIZE_TEXT_REPLACE;
                    case XSDWhiteSpace.COLLAPSE:
                        return MappingNodeConstants.NORMALIZE_TEXT_COLLAPSE;
                }
            }
        }

        // I don't think we need to do derive the text normalization for complex types with simple or empty content
        // } else if (typeDefn instanceof XSDComplexTypeDefinition) {
        // final XSDComplexTypeDefinition cmplxTypeDefn = (XSDComplexTypeDefinition)typeDefn;
        // XSDContentTypeCategory contentType = cmplxTypeDefn.getContentTypeCategory();
        // switch (contentType.getValue()) {
        // case XSDContentTypeCategory.EMPTY: return MappingNodeConstants.NORMALIZE_TEXT_PRESERVE;
        // case XSDContentTypeCategory.SIMPLE: return MappingNodeConstants.NORMALIZE_TEXT_PRESERVE;
        // default: return MappingNodeConstants.Defaults.DEFAULT_NORMALIZE_TEXT;
        // }
        // }

        return MappingNodeConstants.Defaults.DEFAULT_NORMALIZE_TEXT;
    }

    /**
     * @return
     */
    public boolean isIndent() {
        return indent;
    }

    /**
     * @return
     */
    public boolean isNewlines() {
        return newlines;
    }

    boolean isNillable( final XmlElement element ) {
        final XSDComponent xsdComponent = element.getXsdComponent();
        if (xsdComponent != null && xsdComponent instanceof XSDElementDeclaration) {
            final XSDElementDeclaration xsdElement = (XSDElementDeclaration)xsdComponent;
            return xsdElement.isNillable();
        }
        return MappingNodeConstants.Defaults.DEFAULT_IS_NILLABLE.booleanValue();
    }

    boolean isRecursive( final XmlElement element ) {
        final MappingClass mappingClass = this.helper.getMappingClass(element);
        if (mappingClass != null && mappingClass.isRecursionAllowed() && mappingClass.isRecursive()) return mappingClass.isRecursive();
        return false;
    }

    protected void processChildren( final List children,
                                    final MappingNode parentMappingNode,
                                    final NamespaceContext namespaceContext,
                                    final ElementInfo parentInfo ) {
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final Object child = iter.next();
            if (child instanceof XmlDocumentEntity) createMapping((XmlDocumentEntity)child,
                                                                  parentMappingNode,
                                                                  namespaceContext,
                                                                  mappingContext,
                                                                  parentInfo);
        }
    }

    /**
     * @param rootMappingNode
     * @param rootElement
     */
    protected MappingElement processElementNode( final XmlElement element,
                                                 final ElementInfo elementInfo,
                                                 final MappingContext mappingContext ) {
        MappingElement node = null;

        final String name = element.getName();
        final String nsPrefix = getNamespacePrefix(element, elementInfo.getNamespaceContext(), mappingContext);
        final Namespace namespace = getNamespace(nsPrefix);

        // There are effectively three types of elements, recursive, criteria and regular..
        if (isRecursive(element)) {
            // first check if this is a "recursive" element
            final MappingRecursiveElement elem = new MappingRecursiveElement(name, namespace, getRecursionMappingClass(element));
            elem.setCriteria(getRecursionCriteria(element));
            elem.setRecursionLimit(getRecursionLimit(element), throwExceptionOnRecursionLimit(element));
            node = elem;
        } else // this regular element
        node = new MappingElement(name, namespace);

        // now load all other common properties.
        node.setMinOccurrs(getMinOccurrences(element));
        node.setMaxOccurrs(getMaxOccurrences(element));
        node.setNameInSource(getNameInSource(element));
        node.setSource(getSource(element));

        node.setDefaultValue(getDefaultValue(element));
        node.setValue(getFixedValue(element));
        node.setNillable(isNillable(element));
        node.setExclude(element.isExcludeFromDocument());
        node.setType(getBuitInType(element));
        node.setNormalizeText(getXsiTypeTextNormalization(element, mappingContext));

        node.addStagingTable(getStagingTable(element));

        if (this.includeSoapDefaultEncoding) {
            // Determine if this element has a SOAP array type ...
            final String soapArrayType = getSoapArrayType(element, elementInfo.getNamespaceContext(), mappingContext);
            if (soapArrayType != null) {
                elementInfo.setSoapArrayType(soapArrayType);
                createSoapArrayTypeAttribute(node, elementInfo);
            }

            // Determine if this element should have an "xsi:type" attribute ...
            // Technically, xsi:type (e.g., ="xsd:string") is only required for SOAP encoding
            // only if it is a SUBTYPE of what is specified in the container's SOAP-enc:arrayType value
            // (e.g., "xsd:string[]"). If it is the same, then it is superfluous.
            // However, at this point we will always write it out (this is acceptable to the customer
            // requesting this feature).
            if (elementInfo.isXsiTypeRequired()) {
                final String xsiTypeValue = getXsiType(element, elementInfo.getNamespaceContext(), mappingContext);
                elementInfo.setXsiType(xsiTypeValue);
                createXsiTypeAttribute(node, elementInfo);
            }
        }
        return node;
    }

    protected void processNamespaces( final List children,
                                      final MappingNode parentMappingNode,
                                      final NamespaceContext namespaceContext,
                                      final ElementInfo parentInfo ) {
        final List orderedNamespaces = new LinkedList(children);
        final XmlNamespaceComparator comparator = new XmlNamespaceComparator();
        Collections.sort(orderedNamespaces, comparator);
        processChildren(orderedNamespaces, parentMappingNode, namespaceContext, parentInfo);
    }

    /**
     * @param b
     */
    public void setIndent( final boolean b ) {
        indent = b;
    }

    /**
     * @param b
     */
    public void setNewlines( final boolean b ) {
        newlines = b;
    }

    boolean throwExceptionOnRecursionLimit( final XmlElement element ) {
        final MappingClass mappingClass = this.helper.getMappingClass(element);
        if (mappingClass != null && mappingClass.isRecursionAllowed() && mappingClass.isRecursive()) return (mappingClass.getRecursionLimitErrorMode().getValue() == RecursionErrorMode.THROW);
        return MappingNodeConstants.Defaults.DEFAULT_EXCEPTION_ON_RECURSION_LIMIT.booleanValue();

    }

    protected class ElementInfo {
        private String soapArrayType;
        private String xsiType;
        private final ElementInfo parentInfo;
        private final NamespaceContext namespaceContext;

        protected ElementInfo( final NamespaceContext namespaceContext,
                               final ElementInfo parentInfo ) {
            this.parentInfo = parentInfo;
            this.namespaceContext = namespaceContext;
        }

        public NamespaceContext getNamespaceContext() {
            return this.namespaceContext;
        }

        public String getSoapArrayType() {
            return soapArrayType;
        }

        public XmlElement getXmlElement() {
            return this.namespaceContext.getXmlElement();
        }

        public String getXsiType() {
            return this.xsiType;
        }

        public boolean isXsiTypeRequired() {
            if (soapArrayType != null) return true;
            return (parentInfo != null && parentInfo.getSoapArrayType() != null);
        }

        public void setSoapArrayType( final String soapArrayType ) {
            this.soapArrayType = soapArrayType;
        }

        public void setXsiType( final String xsiType ) {
            this.xsiType = xsiType;
        }
    }

    static final class Soap {
        public static final String ARRAY_TYPE_XML_ATTRIBUTE_NAME = "arrayType"; //$NON-NLS-1$
        public static final String ARRAY_TYPE_NAME = "Array"; //$NON-NLS-1$
        public static final String TARGET_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/encoding/"; //$NON-NLS-1$
        public static final String DEFAULT_NAMESPACE_PREFIX = "soap-enc"; //$NON-NLS-1$
    }

}

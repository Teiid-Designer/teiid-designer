/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDParser;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.schema.tools.ToolsPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.AttributeColumn;
import com.metamatrix.modeler.schema.tools.model.schema.impl.ElementImpl;
import com.metamatrix.modeler.schema.tools.model.schema.impl.IdColumn;
import com.metamatrix.modeler.schema.tools.model.schema.impl.SchemaModelImpl;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TextColumn;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TypeDefinition;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;

public class SchemaProcessorImpl implements SchemaProcessor {

    private Map namespaces;

    private HashMap duplicateNamespaceFilter;

    private ArrayList elements;

    public ElementContentTraversalContext traverseCtx;

    private boolean representTypes = false;

    public Logger log;

    private String separator;

    public SchemaProcessorImpl( Logger log,
                                String separator ) {
        this.log = log;
        this.separator = separator;
        clear();
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#clear()
     */
    public void clear() {
        namespaces = new HashMap();
        duplicateNamespaceFilter = new HashMap();
        elements = new ArrayList();
        traverseCtx = new ElementContentTraversalContext(null, null);
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#processSchemas(org.eclipse.xsd.XSDSchema[])
     */
    public void processSchemas( XSDSchema[] schemas ) throws SchemaProcessingException {

        for (int i = 0; i < schemas.length; ++i) {
            XSDSchema schema = schemas[i];
            processSchemaRootElements(schema, traverseCtx);
        }

        for (int i = 0; i < schemas.length; ++i) {
            XSDSchema schema = schemas[i];
            processSchema(schema, traverseCtx);
        }
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#processSchemaURIs(java.util.List)
     */
    public void processSchemaURIs( List schemaURIs ) throws SchemaProcessingException {
        List schemas = new ArrayList();
        for (Iterator resourceIter = schemaURIs.iterator(); resourceIter.hasNext();) {
            Object o = resourceIter.next();
            URI uri = (URI)o;

            XSDSchema schema = getSchemaFromURI(uri);
            schemas.add(schema);
        }

        XSDSchema[] xsdSchemas = new XSDSchema[schemas.size()];
        schemas.toArray(xsdSchemas);
        processSchemas(xsdSchemas);
    }

    public static XSDSchema getSchemaFromURI( URI uri ) {
        XSDParser parser = new XSDParser(null);
        String path = null;
        // In non-ACS scenarios, the URI will be resolvable to a file on the local filesystem.
        // In that case, convert URI to path and parse from there.
        // In the ACS case, the schema is embedded in a custom URI as a string. It
        // must be extracted and parsed as a string.
        if (uri.scheme() == "ACSResponse" || uri.scheme() == "ACSRequest") { //$NON-NLS-1$ //$NON-NLS-2$
            String schemaString = uri.fragment();
            parser.parseString(schemaString);
        } else {
            path = uri.toFileString();
            parser.parse(path);
        }
        XSDSchema schema = parser.getSchema();
        if (schema.getSchemaLocation() == null) {
            schema.setSchemaLocation(path);
        }
        return schema;
    }

    private void processSchemaRootElements( XSDSchema schema,
                                            ElementContentTraversalContext traverseCtx ) {
        // Eclipse does not resolve references across schema files (i.e. imports)
        // unless the schemaLocation attribute is provided (in the schema file).
        // To work around this, we loop through the schemas making a list of the
        // top level elements and types, so that we can help with the cross
        // referencing.

        for (Iterator elemIter = schema.getElementDeclarations().iterator(); elemIter.hasNext();) {
            Object oelem = elemIter.next();
            XSDElementDeclaration elem = (XSDElementDeclaration)oelem;
            addRootElement(elem, traverseCtx);
        }
        for (Iterator typeIter = schema.getTypeDefinitions().iterator(); typeIter.hasNext();) {
            Object otype = typeIter.next();
            XSDTypeDefinition type = (XSDTypeDefinition)otype;
            addRootType(type, traverseCtx);
        }
    }

    private void processSchema( XSDSchema schema,
                                ElementContentTraversalContext traverseCtx ) throws SchemaProcessingException {
        processNamespaces(schema);
        for (Iterator elemiter = schema.getElementDeclarations().iterator(); elemiter.hasNext();) {
            Object oelem = elemiter.next();
            XSDElementDeclaration elem = (XSDElementDeclaration)oelem;
            processElement(elem, traverseCtx, schema);
        }
        if (representTypes) {
            for (Iterator typeiter = schema.getTypeDefinitions().iterator(); typeiter.hasNext();) {
                Object otype = typeiter.next();
                XSDTypeDefinition type = (XSDTypeDefinition)otype;
                processType(type, traverseCtx, schema);
            }
        }
    }

    private void processNamespaces( XSDSchema schema ) {
        Map schemaNamespaces = schema.getQNamePrefixToNamespaceMap();

        Iterator iter = schemaNamespaces.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (key == null || key.trim().equals("")) key = "mmn0"; //$NON-NLS-1$ //$NON-NLS-2$
            String value = (String)schemaNamespaces.get(key);
            if (null == value) continue;
            if(value.equals("http://www.w3.org/XML/1998/namespace")) {
            	key = "xml";
            }
            // ensure unique
            int i = 1;
            if ((namespaces.get(key) != null) && (value.equals(namespaces.get(key)))) {
                continue;
            }
            if (null != duplicateNamespaceFilter.get(value)) {
                String prefix = (String)duplicateNamespaceFilter.get(value);
                if (prefix.startsWith("mmn")) { //$NON-NLS-1$
                    prefix = key;
                }
                continue;
            }
            while (namespaces.get(key) != null) {
                key = key + i++;
            }
            namespaces.put(key, value);
            duplicateNamespaceFilter.put(value, key);
        }

        String namespace = schema.getTargetNamespace();
        if ((null != namespace) && (null == duplicateNamespaceFilter.get(namespace))) {
        	if(namespace.equals("http://www.w3.org/XML/1998/namespace")) {
        		namespaces.put("xml", namespace);;
            } else {
            	final String tns = "mmn"; //$NON-NLS-1$
            	int ctr = 0;
            	if (namespaces.get(tns + ctr) != null) ++ctr;
            	namespaces.put(tns + ctr, namespace);
            }
        }
    }

    private void addRootElement( XSDElementDeclaration elem,
                                 ElementContentTraversalContext traverseCtx ) {
        String name = elem.getName();
        String namespace = elem.getTargetNamespace();

        if (name == null) {
            return;
        }

        QName qname = SchemaUtil.getQName(namespace, name);
        if (traverseCtx.getGlobalElement(qname) != null) {
            return;
        }

        traverseCtx.putGlobalElement(qname, elem);
    }

    private void addRootType( XSDTypeDefinition type,
                              ElementContentTraversalContext traverseCtx ) {
        String name = type.getName();
        String namespace = type.getTargetNamespace();

        if (name == null) {
            return;
        }

        QName qname = SchemaUtil.getQName(namespace, name);
        if (traverseCtx.getGlobalType(qname) != null) {
            return;
        }

        traverseCtx.putGlobalType(qname, type);
    }

    public void processType( XSDTypeDefinition type,
                             ElementContentTraversalContext traverseCtx2,
                             XSDSchema schema ) throws SchemaProcessingException {
        String namespacePrefix = getNameSpacePrefix(type.getTargetNamespace());

        SchemaObject typeDecl = new TypeDefinition(type, namespacePrefix, schema);

        String fileName = SchemaUtil.shortenFileName(schema.getSchemaLocation());
        typeDecl.setFileName(fileName);
        addElement(typeDecl);
        processAttributes(typeDecl, traverseCtx);
        processElementText(typeDecl);
        processElementContents(typeDecl, traverseCtx);
    }

    public void processElement( XSDElementDeclaration elem,
                                ElementContentTraversalContext traverseCtx,
                                XSDSchema schema ) throws SchemaProcessingException {
        String name = elem.getName();
        String namespace = elem.getTargetNamespace();
        String fileName = SchemaUtil.shortenFileName(schema.getSchemaLocation());
        XSDElementDeclaration refElem = resolveElementRef(elem, traverseCtx);

        if (name == null) {
            if (refElem != elem) {
                processElement(refElem, traverseCtx, schema);
                return;
            }
            // How can the ref be null, or equal to the referencer if the name is null?
            log.log(IStatus.WARNING, ToolsPlugin.Util.getString("SchemaProcessorImpl.nullElement")); //$NON-NLS-1$
            return;
        }

        if (refElem != null && refElem != elem) {
            // This case represents an anonymous use of a known type.
        }

        XSDTypeDefinition type = resolveElementType(elem, traverseCtx);

        // Elements can have the same name and the same type and be different elements
        // (e.g. <xsd:element name="foo" type="bar"/> appearing in multiple places)
        // We consider these the same table
        SchemaObject element;
        QName qname = SchemaUtil.getQName(namespace, name);
        Map tablesForName = traverseCtx.getElementsByNameThenType(qname);
        element = (SchemaObject)tablesForName.get(type);

        if (element == null) {
            element = new ElementImpl(elem, getNameSpacePrefix(elem.getTargetNamespace()), type, schema);
            element.setFileName(fileName);
            // It's important to put the table in the map before we recurse down
            // the element's contents, to prevent infinite recursion of circular
            // references
            tablesForName.put(type, element);
            addElement(element);

            processAttributes(element, traverseCtx);
            processElementText(element);
            processElementContents(element, traverseCtx);
        }

        int minOccurs = traverseCtx.calculateMinOccurs(1);
        int maxOccurs = traverseCtx.calculateMaxOccurs(1);

        element.addParent(traverseCtx.getParentTable(), minOccurs, maxOccurs);
    }

    public void addElement( SchemaObject element ) {
        elements.add(element);
    }

    public XSDElementDeclaration resolveElementRef( XSDElementDeclaration ref,
                                                    ElementContentTraversalContext traverseCtx ) {
        XSDElementDeclaration elem = ref.getResolvedElementDeclaration();
        String name = elem.getName();
        String namespace = elem.getTargetNamespace();
        QName qname = SchemaUtil.getQName(namespace, name);
        Object oGlobal = traverseCtx.getGlobalElement(qname);
        XSDElementDeclaration retval;
        if (oGlobal != null) {
            retval = (XSDElementDeclaration)oGlobal;
        } else {
            retval = elem;
        }
        return retval;
    }

    public XSDTypeDefinition resolveElementType( XSDElementDeclaration elem,
                                                 ElementContentTraversalContext traverseCtx ) throws SchemaProcessingException {
        XSDTypeDefinition type = elem.getType();
        if (null == type) {
            throw new SchemaProcessingException(ToolsPlugin.Util.getString("SchemaProcessor.no.type.declaration", elem.getName())); //$NON-NLS-1$
        }
        return resolveType(type, traverseCtx);
    }

    private XSDTypeDefinition resolveType( XSDTypeDefinition type,
                                           ElementContentTraversalContext traverseCtx ) {
        String name = type.getName();
        String namespace = type.getTargetNamespace();
        QName qname = SchemaUtil.getQName(namespace, name);
        Object oGlobal = traverseCtx.getGlobalType(qname);
        XSDTypeDefinition retval;
        if (oGlobal != null) {
            retval = (XSDTypeDefinition)oGlobal;
        } else {
            retval = type;
        }
        return retval;
    }

    public void processAttributes( SchemaObject element,
                                   ElementContentTraversalContext traverseCtx ) throws SchemaProcessingException {
        Column idcol = new IdColumn(SchemaUtil.getStringType(element.getSchema()));
        element.addAttribute(idcol);

        List attrs = element.getAttributeList();
        for (Iterator iter = attrs.iterator(); iter.hasNext();) {
            Object o = iter.next();
            XSDAttributeUse attrUse = (XSDAttributeUse)o;
            XSDAttributeDeclaration attrDecl = attrUse.getAttributeDeclaration();
            Column col = new AttributeColumn(attrDecl, getNameSpacePrefix(attrDecl.getTargetNamespace()), false);
            element.addAttribute(col);
        }
    }

    public String getNameSpacePrefix( String targetNamespace ) {
        Map namespacePrefixes = getNamespacePrefixes();
        return (String)namespacePrefixes.get(targetNamespace);
    }

    public Map getNamespacePrefixes() {
        // reverse the m_namespaces map
        Map nsMap = getNamespaces();
        HashMap returnMap = new HashMap();
        Iterator nsIter = nsMap.keySet().iterator();
        while (nsIter.hasNext()) {
            String key = (String)nsIter.next();
            returnMap.put(nsMap.get(key), key);
        }
        return returnMap;
    }

    public void processElementText( SchemaObject element ) {
        XSDSimpleTypeDefinition textType = element.getTextType();
        Column col;

        if (textType != null) {
            col = new TextColumn(false, textType);
            element.addAttribute(col);
        }

    }

    public void processElementContents( SchemaObject element,
                                        ElementContentTraversalContext traverseCtx ) throws SchemaProcessingException {

        XSDComplexTypeContent content = element.getContent();
        if (null == content || !(content instanceof XSDParticle)) {
            return;
        }
        XSDParticle particle = (XSDParticle)content;
        ElementContentTraversalContext childTraverseCtx = new ElementContentTraversalContext(element, traverseCtx);
        processParticle(particle, childTraverseCtx, element.getSchema());
    }

    private void processParticle( XSDParticle particle,
                                  ElementContentTraversalContext traverseCtx,
                                  XSDSchema schema ) throws SchemaProcessingException {
        XSDParticleContent content = particle.getContent();
        int min = particle.getMinOccurs();
        int max = particle.getMaxOccurs();

        traverseCtx.addMinOccurs(new Integer(min));
        traverseCtx.addMaxOccurs(new Integer(max));
        if (content instanceof XSDElementDeclaration) {
            XSDElementDeclaration elem = (XSDElementDeclaration)content;
            processElement(elem, traverseCtx, schema);
        } else if (content instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup)content;
            processGroup(group, traverseCtx, schema);
        } else if (content instanceof XSDModelGroupDefinition) {
            XSDModelGroupDefinition groupDef = (XSDModelGroupDefinition)content;
            XSDModelGroupDefinition resolvedGroup = groupDef.getResolvedModelGroupDefinition();
            XSDModelGroup group = resolvedGroup == null ? null : resolvedGroup.getModelGroup();
            if (group != null) {
                processGroup(group, traverseCtx, schema);
            }
        } else if (content instanceof XSDTerm) {
            // XSDTerm term = (XSDTerm)content;
            // TODO: what are these?
        } else if (content instanceof XSDWildcard) {
            // XSDWildcard wildcard = (XSDWildcard)content;
            // I don't think we need to do anything with these
        }
        traverseCtx.removeMinOccurs(traverseCtx.minOccurs.size() - 1);
        traverseCtx.removeMaxOccurs(traverseCtx.maxOccurs.size() - 1);
    }

    private void processGroup( XSDModelGroup group,
                               ElementContentTraversalContext traverseCtx,
                               XSDSchema schema ) throws SchemaProcessingException {
        int min;
        int compositor = group.getCompositor().getValue();
        switch (compositor) {
            case XSDCompositor.ALL:
                min = 0;
                break;
            case XSDCompositor.CHOICE:
                min = 0; // technically if there was only one child this shoud be
                // 1
                // but I'm not sure if we can determine that here
                break;
            case XSDCompositor.SEQUENCE:
                min = 1;
                break;
            default:
                // error
                min = 1;
                break;
        }
        traverseCtx.addMinOccurs(new Integer(min));
        traverseCtx.addMaxOccurs(new Integer(1));
        EList particles = group.getParticles();
        for (Iterator iter = particles.iterator(); iter.hasNext();) {
            Object o = iter.next();
            XSDParticle particle = (XSDParticle)o;
            processParticle(particle, traverseCtx, schema);
        }
        traverseCtx.removeMinOccurs(traverseCtx.minOccurs.size() - 1);
        traverseCtx.removeMaxOccurs(traverseCtx.maxOccurs.size() - 1);
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#getNamespaces()
     */
    public Map getNamespaces() {
        return namespaces;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#getSchemaModel()
     */
    public SchemaModel getSchemaModel() {
        SchemaModelImpl model = new SchemaModelImpl(elements, namespaces, separator);
        model.setTypeAware(representTypes);
        return model;
    }

    public void representTypes( boolean representTypes ) {
        this.representTypes = representTypes;
    }

    public void setNamespaces( Map namespaces ) {
        this.namespaces = namespaces;
    }

}

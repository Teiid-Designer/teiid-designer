/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.ToolsPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;

public abstract class BaseSchemaObject implements SchemaObject {

    protected boolean doesNotHaveUniqueName;

    protected String fileName = new String();

    protected boolean availableRoot = false;

    boolean recursivityDetector = false;
    /**
     * Indicates if this table is, or falls under, a selected root element.
     */
    protected boolean withinSelectedHierarchy = false;

    private String prefix;

    protected List parents = new ArrayList();

    protected List attributes = new ArrayList();

    protected List children = new ArrayList();

    // Indicates if the SchemaObject should be represented as a Table in the final model (the xmi that is)
    protected boolean representAsTable = false;

    // User Selections
    boolean useAsRoot = false;

    protected XSDTypeDefinition type;

    protected XSDSchema schema;

    protected BaseSchemaObject( String namespacePrefix,
                                XSDTypeDefinition type,
                                XSDSchema schema ) {
        this.prefix = namespacePrefix;
        this.type = type;
        this.schema = schema;
    }

    public void setMustBeQualified() {
        doesNotHaveUniqueName = true;
    }

    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isWithinSelectedHierarchy() {
        return withinSelectedHierarchy;
    }

    public void setWithinSelectedHierarchy( boolean under ) {
        withinSelectedHierarchy = under;
    }

    public boolean isCanBeRoot() {
        if (!availableRoot || children.size() == 0 || parents.size() > 0) {
            return false;
        }
        return true;
    }

    protected ArrayList copyRelationshipArray( List relationships,
                                               SchemaModelCopyTraversalContext ctx ) {
        ArrayList relationshipsCopy = new ArrayList(relationships.size());
        Iterator iter = relationships.iterator();
        while (iter.hasNext()) {
            SimpleRelationship rel = (SimpleRelationship)iter.next();
            relationshipsCopy.add(rel.copy(ctx));
        }
        return relationshipsCopy;
    }

    protected ArrayList copyAttributesArray( List list,
                                             SchemaObject copiedElement ) {
        ArrayList attributesCopy = new ArrayList(list.size());
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Column column = (Column)iter.next();
            Column copy = column.copy();
            copy.setTable(copiedElement);
            attributesCopy.add(copy);
        }
        return attributesCopy;
    }

    public void addParent( SchemaObject parent,
                           int minOccurs,
                           int maxOccurs ) {
        if (parent == null) {
            availableRoot = true;
            return;
        }
        boolean alreadyThere = false;
        // We can cast to SimpleRelationship here, because during the initial
        // analysis
        // only SimpleTableRelationships are created. MergedTableRelationships
        // are not created
        // until after user options have been set.
        for (Iterator iter = parents.iterator(); iter.hasNext();) {
            Object o = iter.next();
            SimpleRelationship existingTableRelationship = (SimpleRelationship)o;
            SchemaObject existingTable = existingTableRelationship.getParent();
            if (existingTable == parent) {
                // this child appears more than once in the content model for
                // the parent
                // Instead of adding it again, we'll just make sure that the
                // cardinality is correct
                alreadyThere = true;
                existingTableRelationship.setMinOccurs(existingTableRelationship.getMinOccurs() + minOccurs);
                if ((existingTableRelationship.getMaxOccurs() != Relationship.UNBOUNDED) && (maxOccurs != Relationship.UNBOUNDED)) {
                    existingTableRelationship.setMaxOccurs(existingTableRelationship.getMaxOccurs() + maxOccurs);
                } else {
                    existingTableRelationship.setMaxOccurs(Relationship.UNBOUNDED);
                }
            }
        }
        if (!alreadyThere) {
            Relationship relationship = new SimpleRelationship(parent, this, minOccurs, maxOccurs);
            relationship.addNewRelationship();
        }
    }

    public void addAttribute( Column col ) {
        col.setTable(this);
        attributes.add(col);
    }

    public String recursiveGetXpath() {
        if (recursivityDetector) {
            return null;
        }
        recursivityDetector = true;
        try {
            HashSet paths = new HashSet();
            ArrayList xpaths = new ArrayList();
            for (Iterator iter = parents.iterator(); iter.hasNext();) {
                Object o = iter.next();
                Relationship tableRelationship = (Relationship)o;
                SchemaObject element = tableRelationship.getParent();
                String parentXpath = element.recursiveGetXpath();
                if (parentXpath == null) {
                    // recursive references. Abort! (all the way up)
                    return null;
                }
                String childRelXpath = tableRelationship.getChildRelativeXpath();
                String leafXpath = getRelativeXpath();

                if (!(childRelXpath.endsWith(leafXpath))) {
                    // Logic error
                    throw new IllegalStateException(ToolsPlugin.Util.getString("BaseSchemaObject.illegalState")); //$NON-NLS-1$
                }
                String xpathPlusLeaf;
                if (parentXpath.endsWith("/")) { //$NON-NLS-1$
                    xpathPlusLeaf = parentXpath + childRelXpath;
                } else {
                    xpathPlusLeaf = parentXpath + "/" + childRelXpath; //$NON-NLS-1$
                }
                String xpathWithoutLeaf = xpathPlusLeaf.substring(0, xpathPlusLeaf.length() - leafXpath.length() - 1);
                if (!paths.contains(xpathWithoutLeaf)) {
                    paths.add(xpathWithoutLeaf);
                    xpaths.add(xpathWithoutLeaf);
                }
            }
            // There are five possible configurations of parents. Here is how
            // they are formatted:
            // 1) No parents, just root: /elementname
            // 2) Single parent, plus root: (parent/elementname) | /elementname
            // 3) Single parent, no root: parent/elementname
            // 4) Multiple parents, plus root: (((parent1) |
            // (parentn))/elementname) | /elementname
            // 5) Multiple parents, no root: ((parent1) | (parentn))/elementname

            // Note
            // 1) Xpaths need parentheses around them if they contain an | at
            // the top level,
            // so that they can be included in bigger Xpaths.
            // 2) Xpaths do not have a trailing slash. if they are used as a
            // parent the slash will be added then.

            String qname = getRelativeXpath();

            StringBuffer xpath = new StringBuffer();
            boolean alsoRoot = this.isUseAsRoot();
            if (xpaths.size() == 0) {
                // configuration 1
                xpath.append("/"); //$NON-NLS-1$
                xpath.append(qname);
            } else if (xpaths.size() == 1 && alsoRoot) {
                // configuration 2
                xpath.append("(("); //$NON-NLS-1$
                xpath.append(xpaths.get(0));
                xpath.append('/');
                xpath.append(qname);
                xpath.append(") | (/"); //$NON-NLS-1$
                xpath.append(qname);
                xpath.append("))"); //$NON-NLS-1$
            } else if (xpaths.size() == 1 && !alsoRoot) {
                // configuration 3
                xpath.append(xpaths.get(0));
                xpath.append('/');
                xpath.append(qname);
            } else if (alsoRoot) {
                // configuration 4
                xpath.append("((("); //$NON-NLS-1$
                for (int i = 0; i < xpaths.size(); i++) {
                    Object o = xpaths.get(i);
                    String parentXpath = (String)o;
                    if (i > 0) {
                        xpath.append(" | "); //$NON-NLS-1$
                    }
                    xpath.append("("); //$NON-NLS-1$
                    xpath.append(parentXpath);
                    xpath.append(")"); //$NON-NLS-1$
                }
                xpath.append(")/"); //$NON-NLS-1$
                xpath.append(qname);
                xpath.append(") | (/"); //$NON-NLS-1$
                xpath.append(qname);
                xpath.append("))"); //$NON-NLS-1$
            } else {
                // configuration 5
                xpath.append("("); //$NON-NLS-1$
                for (int i = 0; i < xpaths.size(); i++) {
                    Object o = xpaths.get(i);
                    String parentXpath = (String)o;
                    if (i > 0) {
                        xpath.append(" | "); //$NON-NLS-1$
                    }
                    xpath.append("("); //$NON-NLS-1$
                    xpath.append(parentXpath);
                    xpath.append(")"); //$NON-NLS-1$
                }
                xpath.append(")/"); //$NON-NLS-1$
                xpath.append(qname);
            }
            return xpath.toString();
        } finally {
            recursivityDetector = false;
        }
    }

    public void cascadeRootSelection( boolean b ) {
        Set visitedElements = new HashSet();
        this.setUseAsRoot(b);
        this.setWithinSelectedHierarchy(b);
        visitedElements.add(this.getKey());
        cascadeRootSelectionImpl(this.getChildren(), b, visitedElements);
    }

    private void cascadeRootSelectionImpl( List children,
                                           boolean b,
                                           Set visitedElements ) {
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Relationship childRelationship = (Relationship)iter.next();
            SchemaObject child = childRelationship.getChild();
            if (!visitedElements.contains(child.getKey())) {
                visitedElements.add(child.getKey());
                child.setWithinSelectedHierarchy(b);
                cascadeRootSelectionImpl(child.getChildren(), b, visitedElements);
            }
        }
    }

    public String getInputXPath() {
        return GetXPath();
    }

    public String getOutputXPath() {
        return GetXPath();
    }

    private String GetXPath() {
        String xpath = recursiveGetXpath();
        if (xpath == null) {
            // recursiveness detected
            String name = getName();
            if (prefix != null && !prefix.equals("")) { //$NON-NLS-1$
                xpath = "//" + prefix + ':' + name; //$NON-NLS-1$
            } else {
                xpath = "//" + name; //$NON-NLS-1$
            }
        }
        return xpath;
    }

    public String getRelativeXpath() {
        String qname;
        String name = getName();
        if (prefix != null && !prefix.equals("")) { //$NON-NLS-1$
            qname = prefix + ':' + name;
        } else {
            qname = name;
        }
        return qname;
    }

    public List getParents() {
        return parents;
    }

    public List getChildren() {
        return children;
    }

    public List getAttributes() {
        return attributes;
    }

    void mergeChild( Relationship tableRelationship ) {
        SchemaObject child = tableRelationship.getChild();
        for (Iterator colIter = child.getAttributes().iterator(); colIter.hasNext();) {
            Object oCol = colIter.next();
            Column col = (Column)oCol;
            int maxOccurs = tableRelationship.getMaxOccurs();
            for (int iOccurrence = 1; iOccurrence <= maxOccurs; iOccurrence++) {
                int iOccurenceParam = maxOccurs > 1 ? iOccurrence : -1;
                Column mergedColumn = new MergedColumn(col, tableRelationship, iOccurenceParam);
                addAttribute(mergedColumn);
            }
        }
    }

    public void setAllParentRepresentations( int representation,
                                             RelationshipProcessor processor ) {
        // TODO: Setting the relationship type into the Relationship was a failed implementation, all vestiges should be removed.
        List parents = getParents();
        for (Iterator iter = parents.iterator(); iter.hasNext();) {
            Object o = iter.next();
            Relationship tableRelationship = (Relationship)o;
            tableRelationship.setType(representation);
            SchemaObject child = tableRelationship.getChild();
            String key = child.getSimpleName() + ':' + child.getNamespace();
            processor.addRelationship(key, new Integer(representation));
        }
    }

    public boolean isUseAsRoot() {
        return useAsRoot;
    }

    public void setUseAsRoot( boolean useAsRoot ) {
        this.useAsRoot = useAsRoot;
    }

    public boolean isSimpleElement( RelationshipProcessor processor ) {
        boolean isSimpleRequest = true;
        /*
         * for (Iterator iter = children.iterator(); iter.hasNext(); ){
         * Relationship child = (Relationship)iter.next(); int representation =
         * child.getRepresentation(processor); if(representation !=
         * Relationship.MERGE_IN_PARENT_SINGLE || representation !=
         * Relationship.MERGE_IN_PARENT_MULTIPLE) { isSimpleRequest = false;
         * break; } }
         */return isSimpleRequest;
    }

    public boolean representAsTable() {
        return representAsTable;
    }

    public void setRepresentAsTable( boolean table ) {
        this.representAsTable = table;
    }

    public List getAllModelColumns() {
        ArrayList columns = new ArrayList();
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
            Column column = (Column)iter.next();
            columns.add(column.getColumnImplementation());
        }
        return columns;
    }

    public List getAttributeList() {
        List result;
        if (type instanceof XSDComplexTypeDefinition) {
            result = ((XSDComplexTypeDefinition)type).getAttributeUses();
        } else {
            result = new ArrayList();
        }
        return result;
    }

    public String getNamespacePrefix() {
        return prefix;
    }

    protected void copy( BaseSchemaObject copy,
                         SchemaModelCopyTraversalContext ctx ) {
        ArrayList attributesCopy = copyAttributesArray(attributes, copy);
        copy.attributes = attributesCopy;

        ArrayList parentsCopy = copyRelationshipArray(parents, ctx);
        copy.parents = parentsCopy;

        ArrayList childrenCopy = copyRelationshipArray(children, ctx);
        copy.children = childrenCopy;

        copy.availableRoot = availableRoot;
        copy.doesNotHaveUniqueName = doesNotHaveUniqueName;
        copy.fileName = fileName;
        copy.useAsRoot = useAsRoot;
        copy.withinSelectedHierarchy = withinSelectedHierarchy;
    }

    public XSDSchema getSchema() {
        return schema;
    }

    public boolean hasComplexTypeDefinition() {
        return type instanceof XSDComplexTypeDefinition;
    }

    public boolean hasSimpleTypeDefinition() {
        return type instanceof XSDSimpleTypeDefinition;
    }

    public XSDComplexTypeContent getContent() {
        XSDComplexTypeContent result = null;
        if (type instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
            result = complexType.getContentType();
        }
        return result;
    }

    public XSDSimpleTypeDefinition getTextType() {
        XSDSimpleTypeDefinition textType = null;
        if (hasSimpleTypeDefinition()) {
            textType = (XSDSimpleTypeDefinition)getType();
        } else if (hasComplexTypeDefinition()) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)getType();
            XSDComplexTypeContent content = complexType.getContentType();
            if (content instanceof XSDSimpleTypeDefinition) {
                textType = (XSDSimpleTypeDefinition)content;
            } else if (complexType.isMixed()) {
                textType = getStringType(getSchema());
            }
        }
        return textType;
    }

    private static XSDSimpleTypeDefinition getStringType( XSDSchema schema ) {
        if (stringType == null) {
            XSDSchema schemaForSchema = schema.getSchemaForSchema();
            Map typeIdMap = schemaForSchema.getSimpleTypeIdMap();
            Object o = typeIdMap.get("string"); //$NON-NLS-1$
            stringType = (XSDSimpleTypeDefinition)o;
        }
        return stringType;
    }

    private static XSDSimpleTypeDefinition stringType = null;

}

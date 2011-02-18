/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;
import com.metamatrix.modeler.jdbc.metadata.JdbcProcedure;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;

/**
 * The JdbcModelStructure captures the structure of those JdbcNodes that are to be placed into the Relational model. This
 * structure is possibly different than the structure of the JdbcDatabase tree because this JdbcModelStructure excludes those
 * JdbcNode objects that:
 * <ul>
 * <li>are {@link JdbcNode#getSelectionMode() unselected}, OR</li>
 * <li>do not represent {@link JdbcNode#isDatabaseObject() physical database objects}, OR</li>
 * <li>are {@link JdbcCatalog JdbcCatalog} instances when the {@link JdbcImportSettings import settings} specify that
 * {@link JdbcImportSettings#isCreateCatalogsInModel() catalogs} are to be placed in the model, OR</li>
 * <li>are {@link JdbcSchema JdbcSchema} instances specify that {@link JdbcImportSettings#isCreateSchemasInModel() schemas} are to
 * be placed in the model</li> </li>
 * </ul>
 */
public class JdbcModelStructure {

    /**
     * Build and populate a new instance of the JdbcModelStructure using the supplied {@link JdbcDatabase} tree.
     * 
     * @param dbNode the database structure; may not be null
     * @return a new JdbcModelStructure; never null
     * @throws JdbcException if there is a problem navigating the JdbcDatabase tree
     */
    public static JdbcModelStructure build( final Context context ) throws JdbcException {
        final JdbcDatabase dbNode = context.getJdbcDatabase();
        final JdbcImportSettings settings = context.getJdbcImportSettings();
        final boolean includeCatalogs = settings.isCreateCatalogsInModel();
        final boolean includeSchemas = settings.isCreateSchemasInModel();
        return build(dbNode, includeCatalogs, includeSchemas);
    }

    /**
     * Build and populate a new instance of the JdbcModelStructure using the supplied {@link JdbcDatabase} tree.
     * 
     * @param dbNode the database structure; may not be null
     * @return a new JdbcModelStructure; never null
     * @throws JdbcException if there is a problem navigating the JdbcDatabase tree
     */
    protected static JdbcModelStructure build( final JdbcDatabase dbNode,
                                               final boolean includeCatalog,
                                               final boolean includeSchema ) throws JdbcException {
        final JdbcModelStructure model = new JdbcModelStructure();

        // Define the visitor
        final JdbcNodeVisitor visitor = new JdbcNodeVisitor() {
            public boolean visit( final JdbcNode node ) {
                // if the node is unselected, return and don't visit children
                if (node.getSelectionMode() == JdbcNode.UNSELECTED) {
                    return false;
                }

                // Don't process catalog or schema nodes if excluded, but always process their children
                if (node instanceof JdbcCatalog && !includeCatalog) {
                    return true;
                }
                if (node instanceof JdbcSchema && !includeSchema) {
                    return true;
                }

                // Get the parent database object for this node ...
                final JdbcNode parent = node.getParentDatabaseObject(includeCatalog, includeSchema);
                if (node.isDatabaseObject()) {
                    // Register the node under the parent; parent is null if node is a root-level object
                    model.addChild(parent, node);
                }
                model.incrementNodeCount();
                if (node instanceof JdbcTable) {
                    model.incrementTableAndProcedureCount();
                } else if (node instanceof JdbcProcedure) {
                    model.incrementTableAndProcedureCount();
                } else if (node instanceof JdbcCatalog) {
                    // model.incrementTableAndProcedureCount();
                } else if (node instanceof JdbcSchema) {
                    // model.incrementTableAndProcedureCount();
                }
                return true;
            }
        };

        // Navigate the JdbcDatabase tree using a visitor
        dbNode.accept(visitor, JdbcNode.DEPTH_INFINITE);

        return model;
    }

    private final Map childrenForParent;
    private final Map parentForChild;
    private int totalNodeCount;
    private int totalTablesAndProcedureCount;

    protected JdbcModelStructure() {
        this.childrenForParent = new HashMap();
        this.parentForChild = new HashMap();
    }

    /**
     * Get the children for the supplied parent node. Children should be added to parents using the
     * {@link #addChild(JdbcNode, JdbcNode) addChild} method, and not by modifying the result of this method.
     * 
     * @param parent the JdbcNode that is the parent; null if the result is to be the JdbcNode objects that are at the top level
     *        objects in the model structure
     * @return the list of JdbcNode objects that are to appear in the model below the <code>parent</code>; null if there are no
     *         children under the parent.
     */
    public List getChildren( final JdbcNode parent ) {
        return (List)this.childrenForParent.get(parent);
    }

    /**
     * Add the supplied JdbcNode as a child of the supplied parent JdbcNode.
     * 
     * @param parent the parent; null if <code>child</code> should be a root-level object in the model
     * @param child the child; may not be null
     * @return true if the child was successfully added under the parent, or false if the child already is a child under a
     *         different parent
     * @see #getParent(
     */
    public boolean addChild( final JdbcNode parent,
                             final JdbcNode child ) {
        CoreArgCheck.isNotNull(child);

        // Ensure the child is not already registered under another object ...
        final Object existingParent = this.parentForChild.get(child);
        if (existingParent != null) {
            return false;
        }

        // Get the existing children ...
        List children = (List)this.childrenForParent.get(parent);
        if (children == null) {
            // There were no children for the parent, so create and add the list
            children = new LinkedList();
            this.childrenForParent.put(parent, children);
        }

        // Put the child in the list of children ...
        children.add(child);
        this.parentForChild.put(child, parent);
        return true;
    }

    /**
     * Get the parent for the supplied child.
     * 
     * @param child the child node
     * @return the existing parent, or null if the child is not currently registered under the supplied parent
     */
    public JdbcNode getParent( final JdbcNode child ) {
        return (JdbcNode)this.parentForChild.get(child);
    }

    /**
     * Remove the supplied JdbcNode from the children of its current parent.
     * 
     * @param child the child to be removed from its parent; may not be null
     * @return the JdbcNode that was the parent of the <code>child</code>, or null if <code>child</code> was not a child of any
     *         parent.
     */
    public JdbcNode removeChild( final JdbcNode child ) {
        CoreArgCheck.isNotNull(child);
        final JdbcNode parent = (JdbcNode)this.parentForChild.remove(child);
        if (parent != null) {
            // Get the children for the parent and remove the child from the list
            final List children = this.getChildren(parent);
            CoreArgCheck.isNotNull(children);
            final boolean removed = children.remove(child);
            CoreArgCheck.isTrue(removed, "There were no children for child even though child had a parent"); //$NON-NLS-1$
        }
        return parent;
    }

    // /**
    // * Set the children for the supplied parent node.
    // * @param parent the JdbcNode that is the parent; null if the
    // * result is to be the JdbcNode objects that are at the top level
    // * objects in the model structure
    // * @param children the list of JdbcNode objects that are to appear
    // * in the model below the <code>parent</code>
    // * @return the previous children, if any; null if there were no
    // * children previously defined for the supplied parent
    // */
    // public List setChildren( final JdbcNode parent, final List children ) {
    // return (List) this.childrenForParent.put(parent,children);
    // }

    public void print( final PrintStream stream ) {
        printChildren(null, "  ", "/", stream); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void printChildren( final JdbcNode parent,
                               final String parentPath,
                               final String delim,
                               final PrintStream stream ) {
        final List children = this.getChildren(parent);
        if (children == null) {
            return;
        }
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final JdbcNode child = (JdbcNode)iter.next();
            final String childPath = parentPath + delim + child.getName();
            stream.println(childPath);
            printChildren(child, childPath, delim, stream);
        }

    }

    public void print( final StringBuffer sb ) {
        printChildren(null, "  ", "/", sb); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void printChildren( final JdbcNode parent,
                               final String parentPath,
                               final String delim,
                               final StringBuffer sb ) {
        final List children = this.getChildren(parent);
        if (children == null) {
            return;
        }
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final JdbcNode child = (JdbcNode)iter.next();
            final String childPath = parentPath + delim + child.getName();
            sb.append(childPath);
            sb.append("\n"); //$NON-NLS-1$
            printChildren(child, childPath, delim, sb);
        }

    }

    /**
     * @return
     */
    public int getTotalNodeCount() {
        return totalNodeCount;
    }

    /**
     * @return
     */
    public int getTotalTablesAndProceduresCount() {
        return totalTablesAndProcedureCount;
    }

    /**
     * @param i
     */
    public void setTotalNodeCount( int i ) {
        totalNodeCount = i;
    }

    protected void incrementNodeCount() {
        ++totalNodeCount;
    }

    protected void incrementTableAndProcedureCount() {
        ++totalTablesAndProcedureCount;
    }

}

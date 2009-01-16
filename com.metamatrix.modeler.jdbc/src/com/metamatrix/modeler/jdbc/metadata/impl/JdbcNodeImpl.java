/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Comparator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.core.util.IPathComparator;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;

/**
 * JdbcNodeImpl
 */
public abstract class JdbcNodeImpl implements JdbcNode, Comparable, InternalJdbcNode {

    public static final String EXCLUDED_PATTERN = null;
    public static final String WILDCARD_PATTERN = "%"; //$NON-NLS-1$
    public static final String NOT_APPLICABLE = ""; //$NON-NLS-1$
    public static final String DEFAULT_QUALIFIED_NAME_DELIMITER = "."; //$NON-NLS-1$

    /** Used as the value for {@link #getChildren()} when there are no children */
    private static final JdbcNode[] EMPTY_CHILDREN_ARRAY = new JdbcNodeImpl[] {};

    private IPath path;
    private final int type;
    private final String name;
    private final JdbcNode parent;
    private JdbcNode[] children;
    private Object childrenLock = new Object();
    private RequestContainer requests;
    private int selectionMode;
    private String qualifiedNameDelimiter;

    /**
     * Construct an instance of JdbcNodeImpl with the type, name and parent information.
     * 
     * @param type the type for this node
     * @param name the name for this node; may be null
     * @param parent the parent node; may be null if the node is to be a root node
     */
    protected JdbcNodeImpl( int type,
                            String name,
                            JdbcNode parent ) {
        super();
        this.type = type;
        // Remove path information from name (occurs for MS Access)
        if (name == null) {
            this.name = ""; //$NON-NLS-1$
        } else {
            name = name.substring(name.lastIndexOf('/') + 1);
            name = name.substring(name.lastIndexOf('\\') + 1);
            this.name = name;
        }
        this.parent = parent;
        this.children = null;

        // Set the path ...
        if (this.parent == null) {
            this.path = Path.ROOT;
        } else {
            this.path = this.parent.getPath().append(getName());
        }

        // Set the selection mode using the parent information ...
        this.selectionMode = getDefaultSelectionMode();
        if (parent != null) {
            final int parentSelectionMode = parent.getSelectionMode();
            if (parentSelectionMode == SELECTED) {
                doSetSelectionMode(SELECTED);
            } else if (parentSelectionMode == UNSELECTED) {
                doSetSelectionMode(UNSELECTED);
            } else {
                // parent mode is ambiguous, so look in the JdbcDatabase's list of selections ...
                final JdbcNodeSelections selections = ((InternalJdbcDatabase)this.getJdbcDatabase()).getJdbcNodeSelections();
                final int mode = selections.getSelectionMode(this.path);
                if (mode == JdbcNodeSelections.SELECTED) {
                    this.selectionMode = SELECTED;
                } else if (mode == JdbcNodeSelections.UNSELECTED) {
                    this.selectionMode = UNSELECTED;
                } else if (mode == JdbcNodeSelections.PARTIALLY_SELECTED) {
                    this.selectionMode = PARTIALLY_SELECTED;
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPath()
     */
    public IPath getPath() {
        return this.path;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource()
     */
    public abstract IPath getPathInSource();

    /**
     * This method implementation returns true by default.
     * 
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#isDatabaseObject()
     */
    public boolean isDatabaseObject() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
     */
    public abstract JdbcNode getParentDatabaseObject( boolean includeCatalog,
                                                      boolean includeSchema );

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#findChild(java.lang.String)
     */
    public JdbcNode findChild( String name ) {
        final JdbcDatabase dbNode = getJdbcDatabase();
        Assertion.isNotNull(dbNode);
        return dbNode.findJdbcNode(this.getPath().append(name));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getParent()
     */
    public JdbcNode getParent() {
        return this.parent;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getType()
     */
    public int getType() {
        return this.type;
    }

    /**
     * By default, this implementation returns true. Subclasses should override this method if they are considered leaf nodes and
     * never have children.
     * 
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#allowsChildren()
     */
    public boolean allowsChildren() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getChildren()
     */
    public JdbcNode[] getChildren() throws JdbcException {
        // Check if null; this is a check that works fast if non-null
        if (children == null) {
            // If null, then obtain the lock
            synchronized (childrenLock) {
                // And check again in case some other thread populated the children
                // while this thread was waiting for the lock in the previous line.
                if (children == null) {
                    // Compute the children for this node
                    children = computeChildren(); // may return null, may throw exception
                    // If null, the set to the empty array
                    if (children == null) {
                        children = EMPTY_CHILDREN_ARRAY;
                        // Nothing to register
                    } else {
                        // Register the children in the database cache
                        final InternalJdbcDatabase dbNode = (InternalJdbcDatabase)getJdbcDatabase();
                        final JdbcNodeCache cache = dbNode.getJdbcNodeCache();
                        for (int i = 0; i < children.length; ++i) {
                            cache.put(children[i]);
                        }
                    }
                }
            }
        }
        return children;
    }

    /**
     * Utility method (mostly for testing) that allows one to add a child node one at a time. This method does <i>not</i> check
     * whether the node already exists as a child of this node.
     */
    /*package*/void addChild( final JdbcNode node ) throws JdbcException {
        final JdbcNode[] currentChildren = this.getChildren(); // may throw exception
        final int currentNumChildren = currentChildren.length;
        if (currentNumChildren != 0) {
            final JdbcNode[] newChildren = new JdbcNode[currentNumChildren + 1];
            System.arraycopy(currentChildren, 0, newChildren, 0, currentNumChildren);
            newChildren[currentNumChildren] = node;
            children = newChildren;
        } else {
            children = new JdbcNode[] {node};
        }

        // Register the new node ...
        final InternalJdbcDatabase dbNode = (InternalJdbcDatabase)getJdbcDatabase();
        dbNode.getJdbcNodeCache().put(node);
    }

    /**
     * Refresh this node by clearing any cached information, including {@link #getChildren() children}.
     */
    public void refresh() {
        if (children != null) {
            synchronized (childrenLock) {
                // Remove existing children from the cache and call refresh on them ...
                final InternalJdbcDatabase dbNode = (InternalJdbcDatabase)getJdbcDatabase();
                final JdbcNodeCache cache = dbNode.getJdbcNodeCache();
                for (int i = 0; i < children.length; ++i) {
                    final JdbcNode child = children[i];
                    cache.remove(child);
                    child.refresh();
                }
                children = null;
            }
        }
    }

    /**
     * Compute the children for this node. This method is called the first time the children are needed (i.e., when the
     * {@link #getChildren()} method is called), and is called from within a synchronized method.
     * 
     * @return the array of child objects for this node; may be null if there are no children
     * @throws JdbcException if there is an error obtaining the children for this node
     */
    protected abstract JdbcNode[] computeChildren() throws JdbcException;

    /**
     * Return the stringified form of this node. By default, the string is of the form "<i>{@link #getTypeName() typeName}
     * {@link #getName() name}</i>". However, subclasses may override this behavior.
     * 
     * @return the string form of this node
     */
    @Override
    public String toString() {
        final String typeName = getTypeName();
        return (typeName == null ? "" : typeName) + name; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        // if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof JdbcNodeImpl) {
            final JdbcNodeImpl that = (JdbcNodeImpl)obj;

            // Check that the types are identical
            if (this.type != that.type) {
                return false;
            }

            // Check that the parent is the same
            if (this.parent != that.parent) {
                return false;
            }

            // Check that the names match (case INsensitive)
            if (this.name != null) {
                if (!this.name.equalsIgnoreCase(that.name)) {
                    return false;
                }
            } else {
                if (that.name != null) { // this.name is null
                    return false;
                }
                // else this.name == that.name == null
            }
        }

        // Otherwise not comparable ...
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = HashCodeUtil.hashCode(hash, this.type);
        hash = HashCodeUtil.hashCode(hash, this.parent);
        hash = HashCodeUtil.hashCode(hash, this.name);
        return hash;
    }

    /**
     * Compares this object to another. If the specified object is not an instance of the JdbcNodeImpl class, then this method
     * throws a ClassCastException (as instances are comparable only to instances of the same class). Note: this method <i>is</i>
     * consistent with <code>equals()</code>, meaning that <code>(compare(x, y)==0) == (x.equals(y))</code>.
     * <p>
     * 
     * @param obj the object that this instance is to be compared to
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
     *         specified object, respectively.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this instance.
     */
    public int compareTo( Object obj ) {
        if (obj == null) {
            return 1; // this is > null
        }
        final JdbcNodeImpl that = (JdbcNodeImpl)obj; // May throw ClassCastException
        Assertion.isNotNull(obj);

        // Check that the types are identical
        final int diffType = this.type - that.type;
        if (diffType != 0) {
            return diffType;
        }

        // Compare the paths ...
        final Comparator comparator = new IPathComparator();
        return comparator.compare(this.path, that.path);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#accept(com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor, int)
     */
    public void accept( final JdbcNodeVisitor visitor,
                        final int depth ) throws JdbcException {
        ArgCheck.isNotNull(visitor);
        ArgCheck.isTrue(depth == DEPTH_INFINITE || depth == DEPTH_ONE || depth == DEPTH_ZERO,
                        JdbcPlugin.Util.getString("JdbcNodeImpl.InvalidDepthValue")); //$NON-NLS-1$

        // visit this resource
        if (!visitor.visit(this) || depth == DEPTH_ZERO) return;

        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO);
        final JdbcNode[] children = this.getChildren();
        for (int i = 0; i < children.length; ++i) {
            children[i].accept(visitor, nextDepth);
        }
    }

    /**
     * Utility method to return the name of the catalog in which this node exists. If this node does not exist in a catalog, this
     * method returns the {@link #EXCLUDED_PATTERN} constant.
     * 
     * @return the catalog name, or EXCLUDED_PATTERN if <code>node</code> doesn't exist in a catalog.
     */
    public static String getCatalogName( final JdbcNode node ) {
        final JdbcDatabase dbNode = node.getJdbcDatabase();
        JdbcNode ancestor = node.getParent();
        // Stop when there is no ancestor or the ancestor is the database node
        while (ancestor != null && ancestor != dbNode) {
            // Then see if CatalogNode
            if (ancestor instanceof JdbcCatalog) {
                return ancestor.getName();
            }
            // Otherwise, keep going up
            ancestor = ancestor.getParent();
        }
        return EXCLUDED_PATTERN;
    }

    /**
     * Utility method to return the name of the catalog in which this node exists. If this node does not exist in a catalog, this
     * method returns the {@link #NOT_APPLICABLE} constant.
     * 
     * @return the catalog name, or NOT_APPLICABLE if <code>node</code> doesn't exist in a catalog.
     */
    public static String getSchemaName( final JdbcNode node ) {
        final JdbcDatabase dbNode = node.getJdbcDatabase();
        JdbcNode ancestor = node.getParent();
        // Stop when there is no ancestor or the ancestor is the database node
        while (ancestor != null && ancestor != dbNode) {
            // Then see if JdbcSchema
            if (ancestor instanceof JdbcSchema) {
                return ancestor.getName();
            }
            // Otherwise, keep going up
            ancestor = ancestor.getParent();
        }
        return EXCLUDED_PATTERN;
    }

    /**
     * Utility method to return the name of the catalog in which this node exists. If this node does not exist in a catalog, this
     * method returns the {@link #NOT_APPLICABLE} constant. However, if this database does not support schemas, this method
     * returns null.
     * 
     * @return the catalog name pattern, NOT_APPLICABLE if <code>node</code> doesn't exist in a catalog, or null if catalogs are
     *         not supported
     */
    public static String getCatalogPattern( final JdbcNode node ) {
        String catalogNamePattern = JdbcNodeImpl.getCatalogName(node);
        if (NOT_APPLICABLE.equals(catalogNamePattern)) {
            // See if catalogs are even supported ...
            boolean catalogsSupported = false;
            try {
                catalogsSupported = node.getJdbcDatabase().getCapabilities().supportsCatalogsInDataManipulation();
            } catch (JdbcException e) {
                JdbcPlugin.Util.log(e); // not expected, but log just in case
            } catch (SQLException e) {
                // ignore;
            }
            if (!catalogsSupported) {
                catalogNamePattern = EXCLUDED_PATTERN;
            }
        }
        return catalogNamePattern;
    }

    /**
     * Utility method to return the name of the schema in which this node exists. If this node does not exist in a schema, this
     * method returns the {@link #NOT_APPLICABLE} constant. However, if this database does not support schemas, this method
     * returns null.
     * 
     * @return the schema name pattern, NOT_APPLICABLE if <code>node</code> doesn't exist in a schema, or null if schemas are not
     *         supported
     */
    public static String getSchemaPattern( final JdbcNode node ) {
        String schemaNamePattern = JdbcNodeImpl.getSchemaName(node);
        if (NOT_APPLICABLE.equals(schemaNamePattern)) {
            // See if catalogs are even supported ...
            boolean schemaSupported = false;
            try {
                schemaSupported = node.getJdbcDatabase().getCapabilities().supportsCatalogsInDataManipulation();
            } catch (JdbcException e) {
                JdbcPlugin.Util.log(e); // not expected, but log just in case
            } catch (SQLException e) {
                // ignore;
            }
            if (!schemaSupported) {
                schemaNamePattern = EXCLUDED_PATTERN;
            }
        }
        return schemaNamePattern;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcTable#getNamesOfResults()
     */
    public String[] getNamesOfResults() throws JdbcException {
        return getRequestContainer().getNamesOfResults();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcTable#getResults(java.lang.String)
     */
    public Request getRequest( String name ) throws JdbcException {
        return getRequestContainer().getRequest(name);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcTable#getResults(java.lang.String)
     */
    public Request getRequest( String name,
                               final boolean includeMetadata ) throws JdbcException {
        return getRequestContainer().getRequest(name, includeMetadata);
    }

    protected synchronized RequestContainer getRequestContainer() throws JdbcException {
        if (requests == null) {
            requests = new RequestContainer(createRequests());
        }
        return requests;
    }

    /**
     * Override this method to add requests to the node.
     * 
     * @return
     * @throws JdbcException
     */
    @SuppressWarnings( "unused" )
    protected Request[] createRequests() throws JdbcException {
        return new Request[] {};
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getSelectionMode()
     */
    public int getSelectionMode() {
        return this.selectionMode;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#setSelectionMode(int)
     */
    public void setSelected( final boolean selected ) {
        final int newMode = (selected ? SELECTED : UNSELECTED);
        if (this.selectionMode == newMode) {
            // The value is the same, so simply return
            return;
        }

        // Set the current mode ...
        doSetSelectionMode(newMode);

        // -------------------------------------------------------------------------
        // Update the children ...
        // -------------------------------------------------------------------------

        // If the children haven't been loaded, simply return ...
        if (this.children != null) {

            // Process the children ...
            JdbcNode[] childrenCopy = null;
            synchronized (childrenLock) {
                // Get the children ...
                final int numChildren = this.children.length;
                childrenCopy = new JdbcNode[numChildren];
                System.arraycopy(this.children, 0, childrenCopy, 0, numChildren);
            }

            // If unselecting ...
            if (this.selectionMode == UNSELECTED) {
                // Go through all the children and unselect them ...
                for (int i = 0; i < childrenCopy.length; ++i) {
                    final JdbcNode child = childrenCopy[i];
                    child.setSelected(false);
                }
            } else { // if ( this.selectionMode == SELECTED )
                // Go through all the children and select them ...
                for (int i = 0; i < childrenCopy.length; ++i) {
                    final JdbcNode child = childrenCopy[i];
                    child.setSelected(true);
                }
            }
        }

        // -------------------------------------------------------------------------
        // Update the parent ...
        // -------------------------------------------------------------------------
        // This may cause the parent (or its ancestors) to each evaluate all of their children

        // If there is a parent ...
        if (this.parent != null && this.parent instanceof InternalJdbcNode) {
            ((InternalJdbcNode)this.parent).checkSelectionMode(this);
        }
    }

    /**
     * Return the default selection mode when the selection mode can't be determined any other way. For example, this method is
     * called when the parent selection mode is {@link JdbcNode#PARTIALLY_SELECTED}. This method returns
     * {@link JdbcNode#UNSELECTED} by default, and should be overridden by subclasses that wish to provide an alternative.
     * 
     * @return the default selection mode
     */
    protected int getDefaultSelectionMode() {
        return UNSELECTED;
    }

    protected void doSetSelectionMode( final int mode ) {
        this.selectionMode = mode;
        final InternalJdbcDatabase db = (InternalJdbcDatabase)this.getJdbcDatabase();
        final JdbcNodeSelections selections = db.getJdbcNodeSelections();
        selections.setSelected(this.getPath(), this.selectionMode);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.InternalJdbcNode#checkSelectionMode(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    public void checkSelectionMode( final JdbcNode childNodeWithChangedSelection ) {

        // If the current mode is that of the child ...
        if (this.selectionMode == childNodeWithChangedSelection.getSelectionMode()) {
            return;
        }

        // Otherwise, we always have to evaluate all children!

        // If the children haven't been loaded, then do nothing
        if (this.children == null) { // pathological case that should theoretically never happen
            return;
        }

        // Process the children ...
        JdbcNode[] childrenCopy = null;
        synchronized (childrenLock) {
            // Get the children ...
            final int numChildren = this.children.length;
            childrenCopy = new JdbcNode[numChildren];
            System.arraycopy(this.children, 0, childrenCopy, 0, numChildren);
        }

        // Go through all the children and see what their mode is ...
        final int previousMode = this.selectionMode;
        boolean hasUnselected = false;
        boolean hasSelected = false;
        boolean hasPartiallySelected = false;
        for (int i = 0; i < childrenCopy.length; ++i) {
            final JdbcNode child = childrenCopy[i];
            final int childMode = child.getSelectionMode();
            if (!hasSelected && childMode == SELECTED) {
                hasSelected = true;
            }
            if (!hasUnselected && childMode == UNSELECTED) {
                hasUnselected = true;
            }
            if (!hasPartiallySelected && childMode == PARTIALLY_SELECTED) {
                hasPartiallySelected = true;
            }

            // See if we know enough to set this node ...
            if (hasPartiallySelected || (hasSelected && hasUnselected)) {
                // A child is partially selected, or there are both selected & unselected children ...
                hasPartiallySelected = true;
                doSetSelectionMode(PARTIALLY_SELECTED);
                break;
            }
        }

        // We're through all the children, so they are all either SELECTED or UNSELECTED
        if (!hasPartiallySelected) {
            if (hasSelected) {
                doSetSelectionMode(SELECTED);
            }
            if (hasUnselected) {
                doSetSelectionMode(UNSELECTED);
            }
        }

        // If the value changed ...
        if (this.selectionMode != previousMode) {
            // call this method on the parent ...
            if (this.parent != null && this.parent instanceof InternalJdbcNode) {
                ((InternalJdbcNode)this.parent).checkSelectionMode(this);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getUnqualifiedName()
     */
    public String getUnqualifiedName() {
        return getUnqualifiedName(getName());
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getUnqualifiedName(java.lang.String)
     */
    public String getUnqualifiedName( final String originalName ) {
        // Get the identifier quote string ...
        String quoteString = null;
        try {
            quoteString = this.getJdbcDatabase().getCapabilities().getIdentifierQuoteString();
        } catch (JdbcException e) {
            JdbcPlugin.Util.log(e); // not expected, but log just in case
        } catch (SQLException e) {
            // ignore;
        }
        if (quoteString == null || quoteString.trim().length() == 0) {
            return originalName;
        }

        // See if the name even needs the quote string ...
        boolean extraCharsUsed = true; // assume they are ...
        try {
            final String extraChars = this.getJdbcDatabase().getCapabilities().getExtraNameCharacters();
            if (extraChars != null && extraChars.length() != 0) {
                extraCharsUsed = containsCharacters(originalName, extraChars);
            }
        } catch (JdbcException e) {
            JdbcPlugin.Util.log(e); // not expected, but log just in case
        } catch (SQLException e) {
            // ignore;
        }
        if (!extraCharsUsed && isValidName(originalName)) {
            // Case 3263: Regardless of result returned above, we should always consider
            // name with spaces as needing to be quoted.
            if (originalName.indexOf(" ") == -1) { //$NON-NLS-1$
                return originalName;
            }
        }

        final StringBuffer sb = new StringBuffer();
        sb.append(quoteString);
        sb.append(originalName);
        sb.append(quoteString);

        return sb.toString();
    }

    protected boolean containsCharacters( final String name,
                                          final String extraChars ) {
        final int numChars = extraChars.length();
        for (int i = 0; i < numChars; ++i) {
            final char extraChar = extraChars.charAt(i);
            if (name.indexOf(extraChar) != -1) {
                return true;
            }
        }
        return false;
    }

    protected String getQualifedNameDelimiter() {
        if (qualifiedNameDelimiter == null) {
            // Get the identifier quote string ...
            try {
                qualifiedNameDelimiter = this.getJdbcDatabase().getCapabilities().getCatalogSeparator();
                if (qualifiedNameDelimiter != null && qualifiedNameDelimiter.trim().length() == 0) {
                    qualifiedNameDelimiter = null;
                }
            } catch (JdbcException e) {
                JdbcPlugin.Util.log(e); // not expected, but log just in case
            } catch (SQLException e) {
                // ignore;
            }
            if (qualifiedNameDelimiter == null) {
                qualifiedNameDelimiter = DEFAULT_QUALIFIED_NAME_DELIMITER;
            }
        }
        return qualifiedNameDelimiter;
    }

    /**
     * Check whether the characters in the name are considered valid. The first character must be an alphabetic character.
     * 
     * @param name the name to be checked; may not be null
     * @return boolean true if name is valid, false othewise
     */
    public boolean isValidName( final String name ) {
        ArgCheck.isNotNull(name);
        boolean isValid = false;

        // Go through the string and ensure that each character is valid ...
        CharacterIterator charIter = new StringCharacterIterator(name);
        char c = charIter.first();

        // The first character must be an alphabetic character ...
        if (c != CharacterIterator.DONE) {
            if (Character.isLetter(c)) {
                isValid = true;
            }
        }
        return isValid;
    }

}

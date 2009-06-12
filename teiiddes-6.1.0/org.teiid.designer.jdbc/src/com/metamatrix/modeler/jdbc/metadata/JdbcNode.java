/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import org.eclipse.core.runtime.IPath;

import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.data.Request;

/**
 * JdbcNode
 */
public interface JdbcNode {
    
    /*====================================================================
     * Constants defining the depth of resource tree traversal:
     *====================================================================*/
    
    /**
     * Depth constant (value 0) indicating this JdbcNode, but not any of its members.
     */
    public static final int DEPTH_ZERO = 0;

    /**
     * Depth constant (value 1) indicating this JdbcNode and its direct children.
     */
    public static final int DEPTH_ONE = 1;

    /**
     * Depth constant (value 2) indicating this JdbcNode and its direct and
     * indirect children at any depth.
     */
    public static final int DEPTH_INFINITE = 2;

    /*====================================================================
     * Constants defining the selection modes:
     *====================================================================*/
    
    /**
     * Selection constant (value 0) indicating this JdbcNode is <i>not</i> selected, nor are any JdbcNodes
     * (directly or indirectly) below it.
     */
    public static final int UNSELECTED = 0;

    /**
     * Selection constant (value 1) indicating this JdbcNode <i>is</i> selected as are all of the JdbcNodes 
     * (directly or indirectly) below it.
     */
    public static final int SELECTED = 1;

    /**
     * Selection constant (value 2) indicating this JdbcNode is <i>not</i> selected, while some of the 
     * JdbcNodes (directly or indirectly) below it are selected and some are not selected.
     */
    public static final int PARTIALLY_SELECTED = 2;

    /**
     * Selection constant (value 3) indicating no objects selected.
     */
    public static final int NO_OBJS_SELECTED_CODE = 3;

    /*====================================================================
     * Constants defining the types:
     *====================================================================*/
    public int DATABASE         = 101;
    public int CATALOG          = 102;
    public int SCHEMA           = 103;
    public int TABLE            = 104;
    public int VIEW             = 105;
    public int PROCEDURE        = 106;
    public int TABLE_TYPE       = 107;
    public int PROCEDURE_TYPE   = 108;
    
    /**
     * Return the {@link JdbcDatabase} object that contains this node.
     * @return the database; may not be null
     */
    public JdbcDatabase getJdbcDatabase();

    /**
     * Get the type of this node.  Subclasses may add new types.
     * @return the integer constant for the type of this node.
     */
    public int getType();
    
    /**
     * Get the type name of this node.  Subclasses may add new types, and multiple
     * nodes with the same {@link #getType() type} may actually have different
     * {@link #getTypeName() type names}.
     * @return the string name for the type of this node.
     */
    public String getTypeName();
    
    /**
     * Get the name of this node.  The name is constant during the lifetime of the node.
     * @return the name of the node; never null but possibly zero-length.
     */
    public String getName();
    
    /**
     * Return the parent of this node.
     * @return the parent node; may be null if this node is a root node.
     */
    public JdbcNode getParent();

    /**
     * Return the {@link JdbcNode} objects that are the children of this node.
     * Calling this method may cause the children to be loaded for the first time.
     * @return the array of children; never null but possibly empty
     * @throws JdbcException if there is an error obtaining the children for this node
     */
    public JdbcNode[] getChildren() throws JdbcException;
    
    /**
     * Return whether this type of node may have children.  This is typically a constant
     * value for this type of node.  Note that this method does
     * not determine whether there are children (see {@link #getChildren()}) for this
     * node.
     * @return true if this node <i>may</i> have children, or false if this node
     * will never have children.
     */
    public boolean allowsChildren();
    
    /**
     * Return the fully-qualified name for this node, which may be DBMS-specific.
     * @return the fully-qualified name for this node
     */
    public String getFullyQualifiedName();
    
    /**
     * Return the unqualified name for this node, which may be DBMS-specific.
     * @return the unqualified name for this node
     */
    public String getUnqualifiedName();
    
    /**
     * Return the unqualified name given the supplied input name, using the same
     * algorithm to use when computing the unqualified name of this node, which may be DBMS-specific.
     * @param inputName the string that is to be converted to an unqualifed (potentially DBMS-specific) name;
     * may not be null
     * @return the unqualified name form of the supplied input
     */
    public String getUnqualifiedName( final String inputName );
    
    /**
     * Return the path of this object.  The path is defined to be the sequence of name segments that identify
     * the node below the {@link #getJdbcDatabase() JdbcDatabase} node.
     * @return the path
     */
    public IPath getPath();
    
    /**
     * Return the path of this object as known to its source.  The path is defined to be the sequence of 
     * name segments that identify the object (that the node represents) within the source database.
     * @return the path, or null if the node doesn't represent an object in the source.
     */
    public IPath getPathInSource();
    
    /**
     * Return the path of this object as known to its source, excluding or including as specified any 
     * catalog and schemas that may be ancestors.  The path is defined to be the sequence of 
     * name segments that identify the object (that the node represents) within the source database.
     * @param includeCatalog true if any catalogs above this node should be included in the path, or false
     * otherwise
     * @param includeSchema true if any schemas above this node should be included in the path, or false
     * otherwise
     * @return the path, or null if the node doesn't represent an object in the source.
     */
    public IPath getPathInSource( boolean includeCatalog, boolean includeSchema );
    
    /**
     * Return whether this JdbcNode represents one thing in the source database.  Examples of JdbcNodes
     * that are database objects are {@link JdbcCatalog}, {@link JdbcTable} and {@link JdbcProcedure}.
     * Examples of JdbcNodes that are not database objects are {@link JdbcProcedureType} and
     * {@link JdbcTableType}.
     * @return true if this object represents something actually in the data source, or false otherwise.
     */
    public boolean isDatabaseObject();
    
    /**
     * Return the JdbcNode that represents the {@link #isDatabaseObject() database object}
     * that is the parent of this database object.
     * @param includeCatalog true if any catalogs above this node should be included in the path, or false
     * otherwise
     * @param includeSchema true if any schemas above this node should be included in the path, or false
     * otherwise
     * @return the parent database object, which if non-null should return true for 
     * {@link #isDatabaseObject()}
     */
    public JdbcNode getParentDatabaseObject( boolean includeCatalog, boolean includeSchema );
    
    /**
     * Find and return the child with the supplied name.
     * @param name the name of the child
     * @return
     */
    public JdbcNode findChild( String name );
    
    /**
     * Refresh this node by clearing any cached information, including {@link #getChildren() children}.
     */
    public void refresh();
    
    /**
     * Accepts the given visitor.
     * The visitor's <code>visit</code> method is called with this node. 
     * If the visitor returns <code>false</code>, this {@link JdbcNode#getChildren() node's children}
     * are not visited.
     * <p>
     * The subtree under the given node is traversed to the supplied depth.
     * </p>
     * @param visitor the visitor; may not be null
     * @param depth the depth to which members of this resource should be
     *      visited.  One of <code>DEPTH_ZERO</code>, <code>DEPTH_ONE</code>,
     *      or <code>DEPTH_INFINITE</code>.
     * @throws IllegalArgumentException if the visitor is null or the depth is invalid
     * @throws JdbcException if the visitor failed with this exception
     */
    public void accept( JdbcNodeVisitor visitor, int depth ) throws JdbcException;

    /**
     * Get the names of the {@link #getRequests(String) results} that may be contained by this object.
     * @return the array of names for each request/result that may be contained by this object.
     * @throws JdbcException if there is an error determining the information
     */
    public String[] getNamesOfResults() throws JdbcException;
    
    /**
     * Get the named requests that may have results.  If the requested information has not yet been loaded 
     * when this method is called, the information will be fetched from the underlying JDBC datasource.
     * This method includes metadata (see {@link #getRequest(String, boolean)}).
     * @param name the name of the request/result; should be one of the names returned from 
     * {@link #getNamesOfResults()}.
     * @throws JdbcException if there is an error determining the information
     * @see JdbcNode#refresh()
     */
    public Request getRequest( String name ) throws JdbcException;
    
    /**
     * Get the named requests that may have results.  If the requested information has not yet been loaded 
     * when this method is called, the information will be fetched from the underlying JDBC datasource.
     * @param name the name of the request/result; should be one of the names returned from 
     * {@link #getNamesOfResults()}.
     * @param includeMetadata flag specifying whether to include metadata
     * @throws JdbcException if there is an error determining the information
     * @see JdbcNode#refresh()
     */
    public Request getRequest( String name, boolean includeMetadata ) throws JdbcException;
    
    /**
     * Set the selection mode on this node.  Depending upon the existing value and upon the new value,
     * invoking this method may cause the selection mode to change on other nodes below this node.  This
     * method does nothing if the new selection mode matches the current mode.  Note that it is not possible
     * to set the node as {@link #PARTIALLY_SELECTED}, since that is done as a by-product of selecting or
     * unselecting children.
     * @param selected true if the new selection mode of this node is to be {@link #SELECTED}, or false
     * if the selection mode is to be {@link #UNSELECTED}.
     */
    public void setSelected( boolean selected );
    
    /**
     * Return the selection mode on this node.
     * @return the current selection; one of {@link #SELECTED}, {@link #PARTIALLY_SELECTED}
     * or {@link #UNSELECTED} 
     */
    public int getSelectionMode();

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.modeler.jdbc.JdbcException;

/**
 * JdbcDatabase
 */
public interface JdbcDatabase extends JdbcNode {
    
    /**
     * Return the {@link java.sql.Connection connection} from which this node was created.
     * @return the Connection; may not be null
     */
    public Connection getConnection();

    /**
     * Return the {@link java.sql.DatabaseMetaData database metadata} for this database.
     * @return the Connection; may not be null
     */
    public DatabaseMetaData getDatabaseMetaData() throws JdbcException;

    /**
     * Get the capabilities of this database (or driver).  
     * Calling this method may cause the information to be loaded from the database connection.
     * @return the capabilities; never null
     * @throws JdbcException if there is an error obtaining the information from
     * the database connection
     */
    public Capabilities getCapabilities() throws JdbcException;
    
    /**
     * Get the general database (and driver) information.
     * Calling this method may cause the information to be loaded from the database connection.
     * @return the database information; never null
     * @throws JdbcException if there is an error obtaining the information from
     * the database connection
     */
    public DatabaseInfo getDatabaseInfo() throws JdbcException;

    /**
     * Get the set of metadata that is or will be accessible by this database.
     * @return the includes; never null
     */
    public Includes getIncludes();
    
    /**
     * Find the {@link JdbcNode} instance that is identified by the supplied path.
     * @param path the path of the node to find
     * @return the {@link JdbcNode} identified by the path, or null if no such node exists or has been
     * found (via {@link JdbcNode#getChildren() navigation}) yet.
     */
    public JdbcNode findJdbcNode( IPath path );
    
    /**
     * Find the {@link JdbcNode} instance that is identified by the supplied path.  This is a convenience
     * method that simply does the following:
     * <code>
     *     return findJdbcNode(new Path(path));
     * </code>
     * @param path the path of the node to find.
     * @return the {@link JdbcNode} identified by the path, or null if no such node exists or has been
     * found (via {@link JdbcNode#getChildren() navigation}) yet.
     */
    public JdbcNode findJdbcNode( String path );
    
    /**
     * return just the children that are checked. Used by importers/trees that don't want to expose the entire database schema
     * list
     * @return selected nodes array
     * @since 4.3
     */
    public JdbcNode[] getSelectedChildren() throws JdbcException;
}

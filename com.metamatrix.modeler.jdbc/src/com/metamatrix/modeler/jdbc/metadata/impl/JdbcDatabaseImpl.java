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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.metadata.Capabilities;
import com.metamatrix.modeler.jdbc.metadata.DatabaseInfo;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;
import com.metamatrix.modeler.jdbc.metadata.JdbcTableType;

/**
 * JdbcDatabaseImpl
 */
public class JdbcDatabaseImpl extends JdbcNodeImpl implements JdbcDatabase, InternalJdbcDatabase {

    private final Connection connection;
    private DatabaseMetaData metadata;
    private Capabilities capabilities;
    private DatabaseInfo databaseInfo;
    private final JdbcNodeCache cache;
    private final JdbcNodeSelections selections;
    private final IncludesImpl includes;
    private final Object capabilitiesLock = new Object();
    private final Object databaseInfoLock = new Object();

    /**
     * Construct an instance of JdbcDatabaseImpl.
     */
    public JdbcDatabaseImpl( final Connection connection,
                             final String name ) {
        this(connection, name, new JdbcNodeSelections());
    }

    /**
     * Construct an instance of JdbcDatabaseImpl.
     */
    public JdbcDatabaseImpl( final Connection connection,
                             final String name,
                             final JdbcNodeSelections selections ) {
        super(DATABASE, name, null);
        ArgCheck.isNotNull(connection);
        this.connection = connection;
        this.includes = new IncludesImpl(this);
        this.cache = new JdbcNodeCache();
        // Put this node into the cache
        this.cache.put(this);

        // Set up the selections
        this.selections = selections;
        doSetSelectionMode(PARTIALLY_SELECTED); // requires this.selections
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#accept(com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor,
     *      int)
     * @since 4.3
     */
    @Override
    public void accept( JdbcNodeVisitor theVisitor,
                        int theDepth ) throws JdbcException {
        if (theVisitor instanceof SelectSchemasAndCatalogs) {
            // need to search siblings before searching their children so that we find matches at the highest level first
            if (theDepth != DEPTH_ONE) {
                super.accept(theVisitor, DEPTH_ONE);
            }

            // search children's children now if no match found yet
            if (!((SelectSchemasAndCatalogs)theVisitor).foundMatch()) {
                super.accept(theVisitor, theDepth);
            }
        } else {
            super.accept(theVisitor, theDepth);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.InternalJdbcDatabase#getJdbcNodeSelections()
     */
    public JdbcNodeSelections getJdbcNodeSelections() {
        return this.selections;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    public IPath getPathInSource( final boolean includeCatalog,
                                  final boolean includeSchema ) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#isDatabaseObject()
     */
    @Override
    public boolean isDatabaseObject() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
     */
    @Override
    public JdbcNode getParentDatabaseObject( boolean includeCatalog,
                                             boolean includeSchema ) {
        return null; // there is never a parent of a database
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return this;
    }

    public JdbcNodeCache getJdbcNodeCache() {
        return this.cache;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#findJdbcNode(org.eclipse.core.runtime.IPath)
     */
    public JdbcNode findJdbcNode( IPath path ) {
        return this.cache.get(path);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#findJdbcNode(java.lang.String)
     */
    public JdbcNode findJdbcNode( String path ) {
        return findJdbcNode(new Path(path));
    }

    public JdbcNode[] getSelectedChildren() throws JdbcException {
        List selectedNodes = new ArrayList();

        final JdbcNode[] nodes = this.getChildren();
        for (int ndx = 0; ndx < nodes.length; ++ndx) {
            final JdbcNode node = nodes[ndx];
            if (node.getSelectionMode() != JdbcNode.UNSELECTED) {
                selectedNodes.add(node);
            }
        }
        // now that we've got the list
        if (selectedNodes.size() > 0) {
            JdbcNode[] nodeArray = new JdbcNode[selectedNodes.size()];
            int iNode = 0;
            for (Iterator iter = selectedNodes.iterator(); iter.hasNext();) {
                nodeArray[iNode] = (JdbcNode)iter.next();
                iNode++;
            }
            return nodeArray;
        }
        return new JdbcNode[0];
    }

    public Connection getConnection() {
        return this.connection;
    }

    public DatabaseMetaData getDatabaseMetaData() throws JdbcException {
        if (this.metadata == null) {
            try {
                this.metadata = this.connection.getMetaData();
            } catch (SQLException e) {
                throw new JdbcException(
                                        e,
                                        JdbcPlugin.Util.getString("JdbcDatabaseImpl.Error_while_getting_the_database_metadata_component")); //$NON-NLS-1$
            }
        }
        return this.metadata;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getCapabilities()
     */
    public Capabilities getCapabilities() throws JdbcException {
        if (capabilities == null) {
            synchronized (capabilitiesLock) {
                if (capabilities == null) {
                    capabilities = loadCapabilities(this.getDatabaseMetaData());
                }
            }
        }
        return capabilities;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getDatabaseInfo()
     */
    public DatabaseInfo getDatabaseInfo() throws JdbcException {
        if (databaseInfo == null) {
            synchronized (databaseInfoLock) {
                if (databaseInfo == null) {
                    databaseInfo = loadDatabaseInfo(this.getDatabaseMetaData());
                }
            }
        }
        return databaseInfo;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    public String getTypeName() {
        return JdbcPlugin.Util.getString("JdbcDatabaseImpl.DatabaseTypeName"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() throws JdbcException {
        final Capabilities capabilities = getCapabilities();

        // See if the driver/database supports catalogs ...
        boolean supportsCatalogs = false;
        try {
            // supportsCatalogs = md.supportsCatalogsInTableDefinitions();
            supportsCatalogs = capabilities.supportsCatalogs();
        } catch (Throwable t) {
            final Object[] params = new Object[] {this.getConnection()};
            final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_discovering_support_for_catalogs", params); //$NON-NLS-1$
            JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
        }

        // See if the driver/database supports schemas ...
        boolean supportsSchemas = false;
        try {
            // supportsSchemas = md.supportsSchemasInTableDefinitions();
            supportsSchemas = capabilities.supportsSchemas();
        } catch (Throwable t) {
            final Object[] params = new Object[] {this.getConnection()};
            final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_discovering_support_for_schemas", params); //$NON-NLS-1$
            JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
        }

        // Create the children ...
        final DatabaseMetaData metadata = getDatabaseMetaData();
        final List children = new ArrayList();
        if (supportsCatalogs) {
            ResultSet resultSet = null;
            try {
                resultSet = metadata.getCatalogs();
                while (resultSet.next()) {
                    final String catalogName = resultSet.getString(1);
                    if (catalogName.length() > 0) {
                        children.add(new JdbcCatalogImpl(this, catalogName));
                    }
                }
                if (children.isEmpty()) {
                    supportsCatalogs = false;
                }
            } catch (UnsupportedOperationException e) {
                supportsCatalogs = false;
            } catch (Throwable t) {
                final Object[] params = new Object[] {metadata.getClass().getName(), this.getConnection()};
                final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_calling_getCatalogs()_and_processing_results", params); //$NON-NLS-1$
                JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
                supportsCatalogs = false;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        JdbcPlugin.Util.log(e);
                    }
                }
            }
        }
        // If DatabaseMetadata supports schemas but not catalogs ...
        if (supportsSchemas && !supportsCatalogs) {
            ResultSet resultSet = null;
            try {
                resultSet = metadata.getSchemas();
                while (resultSet.next()) {
                    final String catalogName = resultSet.getString(1);
                    children.add(new JdbcSchemaImpl(this, catalogName));
                }
            } catch (UnsupportedOperationException e) {
                supportsSchemas = false;
            } catch (Throwable t) {
                final Object[] params = new Object[] {metadata.getClass().getName(), this.getConnection()};
                final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_calling_getSchemas()_and_processing_results", params); //$NON-NLS-1$
                JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
                supportsSchemas = false;
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        JdbcPlugin.Util.log(e);
                    }
                }
            }
        }
        // If DatabaseMetadata does not support schemas or catalogs then load the table metadata
        if (!supportsSchemas && !supportsCatalogs) {
            // Get the table types ...
            try {
                String[] tableTypes = getJdbcDatabase().getIncludes().getIncludedTableTypes();
                if (tableTypes == null) {
                    tableTypes = getJdbcDatabase().getCapabilities().getTableTypes();
                }
                for (int i = 0; i < tableTypes.length; ++i) {
                    children.add(new JdbcTableTypeImpl(this, tableTypes[i]));
                }
            } catch (Throwable t) {
                final Object[] params = new Object[] {metadata.getClass().getName(), this.getConnection()};
                final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_calling_getTableTypes()_and_processing_results", params); //$NON-NLS-1$
                JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
            }

            // Add the procedure type ...
            if (getIncludes().includeProcedures()) {
                String procTerm = null;
                try {
                    procTerm = getCapabilities().getProcedureTerm();
                } catch (Throwable t) {
                    procTerm = JdbcPlugin.Util.getString("JdbcDatabaseImpl.ProcedureTypeName"); //$NON-NLS-1$
                }
                children.add(new JdbcProcedureTypeImpl(this, procTerm));
            }

        }

        // Convert the list to an array and return ...
        return (JdbcNode[])children.toArray(new JdbcNode[children.size()]);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    public String getFullyQualifiedName() {
        return NOT_APPLICABLE;
    }

    /**
     * Method to load the values of a {@link DatabaseInfoImpl} instance given a {@link DatabaseMetaData} reference.
     * 
     * @param obj
     * @param metadata
     * @return
     */
    public Capabilities loadCapabilities( final DatabaseMetaData metadata ) {
        return new CapabilitiesImpl(metadata);
    }

    /**
     * Method to load the values of a {@link DatabaseInfoImpl} instance given a {@link DatabaseMetaData} reference.
     * 
     * @param obj
     * @param metadata
     * @return
     */
    public DatabaseInfo loadDatabaseInfo( final DatabaseMetaData metadata ) throws JdbcException {
        ArgCheck.isNotNull(metadata);
        final DatabaseInfoImpl obj = new DatabaseInfoImpl();
        final List statuses = new ArrayList();

        // Load the product name
        try {
            obj.setProductName(metadata.getDatabaseProductName());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_product_name", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the product version
        try {
            obj.setProductVersion(metadata.getDatabaseProductVersion());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_product_version", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the database major version
        obj.setDriverMajorVersion(metadata.getDriverMajorVersion());
        obj.setDriverMinorVersion(metadata.getDriverMinorVersion());

        // Load the driver name
        try {
            obj.setDriverName(metadata.getDriverName());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_driver_name", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the driver version
        try {
            obj.setDriverVersion(metadata.getDriverVersion());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_driver_version", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the database URL
        try {
            obj.setDatabaseURL(metadata.getURL());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_database_URL", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the database read-only status
        try {
            obj.setReadOnly(metadata.isReadOnly());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_database_read-only_mode", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Load the username
        try {
            obj.setUserName(metadata.getUserName());
        } catch (SQLException e) {
            final IStatus status = new Status(
                                              IStatus.ERROR,
                                              JdbcPlugin.PLUGIN_ID,
                                              0,
                                              JdbcPlugin.Util.getString("DatabaseInfoImpl.Unable_to_obtain_the_username", metadata), e); //$NON-NLS-1$
            statuses.add(status);
        }

        // Assemble all of the individual errors into the MultiStatus
        if (statuses.size() != 0) {
            final MultiStatus mstatus = new MultiStatus(
                                                        JdbcPlugin.PLUGIN_ID,
                                                        0,
                                                        JdbcPlugin.Util.getString("DatabaseInfoImpl.Error_while_processing_database_information", metadata), null); //$NON-NLS-1$
            final Iterator iter = statuses.iterator();
            while (iter.hasNext()) {
                final IStatus status = (IStatus)iter.next();
                mstatus.add(status);
            }
            throw new JdbcException(mstatus);
        }

        // Nothing went wront, so return null
        return obj;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#refresh()
     */
    @Override
    public void refresh() {
        super.refresh();
        if (capabilities != null) {
            synchronized (capabilitiesLock) {
                capabilities = null;
            }
        }
        if (databaseInfo != null) {
            synchronized (databaseInfoLock) {
                databaseInfo = null;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append(" - "); //$NON-NLS-1$
        try {
            final String url = this.connection.getMetaData().getURL();
            sb.append(url);
        } catch (SQLException e) {
            // do nothing
        }
        return super.toString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getIncludes()
     */
    public Includes getIncludes() {
        return this.includes;
    }

    /**
     * Find and select those nodes that should be selected by default.
     * 
     * @param username the username or (<code>null</code> if the metadata user name should be used to select nodes
     * @return the status of the operation
     */
    public IStatus selectDefaultNodes( String username ) {
        try {
            boolean isExcelOrAccess = false;
            final boolean ignoreCase = true;
            String productName = this.getDatabaseInfo().getProductName();
            if (productName != null && (productName.equalsIgnoreCase("excel") || productName.equalsIgnoreCase("access"))) { //$NON-NLS-1$ //$NON-NLS-2$
                isExcelOrAccess = true;
            }
            // Determine the username
            String user = (username == null ? getDatabaseInfo().getUserName() : username);
            SelectSchemasAndCatalogs visitor = new SelectSchemasAndCatalogs(user, ignoreCase, isExcelOrAccess);
            this.accept(visitor, DEPTH_INFINITE);

            // try metadata username if no matches found
            if (!visitor.foundMatch() && (username == null)) {
                String temp = getDatabaseInfo().getUserName();

                // only do second search if name is different
                if (!temp.equals(user)) {
                    user = temp;
                    visitor = new SelectSchemasAndCatalogs(user, ignoreCase, isExcelOrAccess);
                    this.accept(visitor, DEPTH_INFINITE);
                }
            }

            // If there were no new selections ...
            if (!visitor.foundMatch()) {
                final Object[] params = new Object[] {username == null ? user : username};
                final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unable_to_select_any_database_objects", params); //$NON-NLS-1$
                return new Status(IStatus.ERROR, JdbcPlugin.PLUGIN_ID, NO_OBJS_SELECTED_CODE, msg, null);
            }

            // Else we were able to find at least some selections ...
            final Object[] params = new Object[] {new Integer(1)};
            final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Selected_database_objects", params); //$NON-NLS-1$
            return new Status(IStatus.OK, JdbcPlugin.PLUGIN_ID, 0, msg, null);
        } catch (JdbcException e) {
            final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Error_while_selecting_default_database_objects"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, JdbcPlugin.PLUGIN_ID, 0, msg, e);
        }
    }

    /**
     * Find and select those nodes that should be selected by default. Uses the database metadata user name to select nodes.
     * 
     * @return the status of the operation
     */
    public IStatus selectDefaultNodes() {
        return selectDefaultNodes(null);
    }

    /**
     * The visitor that walks the JdbcNode tree, visiting only schemas and catalogs (never their children), and marks those that
     * match the supplied username. Excel is handled differently, since username is not required
     */
    protected class SelectSchemasAndCatalogs implements JdbcNodeVisitor {
        private final String matchName;
        private final boolean ignoreCase;
        private final boolean isExcelOrAccess;
        private boolean match = false;

        public SelectSchemasAndCatalogs( final String matchName,
                                         final boolean ignoreCase,
                                         final boolean isExcelOrAccess ) {
            ArgCheck.isNotNull(matchName);
            this.matchName = matchName;
            this.ignoreCase = ignoreCase;
            this.isExcelOrAccess = isExcelOrAccess;
        }

        public boolean foundMatch() {
            return this.match;
        }

        public boolean visit( final JdbcNode node ) {
            // return if we already found a match
            if (foundMatch()) {
                return false;
            }

            // If visiting the database, then we want to visit children (but nothing else to do)
            if (node instanceof JdbcDatabase) {
                return true;
            }

            // If visiting a schema or catalog don't visit children if match found
            if (node instanceof JdbcCatalog || node instanceof JdbcSchema
                || (this.isExcelOrAccess && (node instanceof JdbcTableType))) {
                // If excel then set match to true
                if (this.isExcelOrAccess) {
                    this.match = true;
                    // everything else, check name match
                } else {
                    // Compare the name ...
                    final String name = node.getName();
                    this.match = ignoreCase ? matchName.equalsIgnoreCase(name) : matchName.equals(name);
                }

                if (this.match) {
                    // Select the node ...
                    node.setSelected(true);
                    return false;
                }

                // visit children
                return true;
            }

            // don't walk to the children of any other
            return false;
        }
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.metadata.Capabilities;
import org.teiid.designer.jdbc.metadata.JdbcCatalog;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.metadata.JdbcNode;


/**
 * JdbcCatalogImpl
 *
 * @since 8.0
 */
public class JdbcCatalogImpl extends JdbcNodeImpl implements JdbcCatalog {
    
    static final String TABLE_SCHEM_STR = "TABLE_SCHEM"; //$NON-NLS-1$
    static final String TABLE_CATALOG_STR = "TABLE_CATALOG"; //$NON-NLS-1$
    static final String SQL_SERVER_DB_METADATA = "SQLServerDatabaseMetaData"; //$NON-NLS-1$
    static final String SQL_SERVER_EXCEPTION = "SQLServerException"; //$NON-NLS-1$

    /**
     * Construct an instance of JdbcCatalogImpl.
     * 
     */
    public JdbcCatalogImpl( final JdbcNode parent, final String name ) {
        super(CATALOG,name,parent);
        CoreArgCheck.isNotNull(parent);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    @Override
	public JdbcDatabase getJdbcDatabase() {
        return getParent().getJdbcDatabase();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() throws JdbcException {
        final DatabaseMetaData metadata = getJdbcDatabase().getDatabaseMetaData();
        final List children = new ArrayList();
        final JdbcDatabase database = getJdbcDatabase();
        final Capabilities capabilities = database.getCapabilities();

        // See if the driver/database supports schemas ...
        boolean supportsSchemas = false;
        try {
            // supportsSchemas = md.supportsSchemasInTableDefinitions();
            supportsSchemas = capabilities.supportsSchemas();
        } catch (Throwable t) {
            final Object[] params = new Object[] {database.getConnection()};
            final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_discovering_support_for_schemas", params); //$NON-NLS-1$
            JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
        }

        if (supportsSchemas) {
            supportsSchemas = addSchemaChildren(metadata, children);
        }
        // If DatabaseMetadata does not support schemas ...
        if (!supportsSchemas) {

            // Create the children ...
            // Get the table types ...
            try {
                String[] tableTypes = database.getIncludes().getIncludedTableTypes();
                if (tableTypes == null) {
                    tableTypes = database.getCapabilities().getTableTypes();
                }
                for (int i = 0; i < tableTypes.length; ++i) {
                    children.add(new JdbcTableTypeImpl(this, tableTypes[i]));
                }
            } catch (Throwable t) {
                final Object[] params = new Object[] {metadata.getClass().getName(), database};
                final String msg = JdbcPlugin.Util.getString("JdbcCatalogImpl.Unexpected_exception_while_calling_getTableTypes()_and_processing_results", params); //$NON-NLS-1$
                JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
            }

            // Add the procedure type ...
            if (database.getIncludes().includeProcedures()) {
                String procTerm = null;
                try {
                    procTerm = database.getCapabilities().getProcedureTerm();
                } catch (Throwable t) {
                    procTerm = JdbcPlugin.Util.getString("JdbcCatalogImpl.ProcedureTypeName"); //$NON-NLS-1$
                }
                children.add(new JdbcProcedureTypeImpl(this, procTerm));
            }
        }

        // Convert the list to an array and return ...
        return (JdbcNode[])children.toArray(new JdbcNode[children.size()]);
    }
    
    private boolean addSchemaChildren(DatabaseMetaData metadata, List children) {
        boolean supportsSchemas = true;
        // Load the schemas ...
        ResultSet resultSet = null;
        try {
            if( getName() != null ) {

                resultSet = metadata.getSchemas();
                while (resultSet.next()) {
                    final String catalogName = resultSet.getString(1);
                    children.add(new JdbcSchemaImpl(this, catalogName));
                }
            }
        } catch (UnsupportedOperationException e) {
            supportsSchemas = false;
        } catch (Throwable t) {
            // Note, if SQL Server database, filter out SQL Server "permission" exceptions 
            if( !(t.getClass().getName().endsWith(SQL_SERVER_EXCEPTION)) ) {
                 final Object[] params = new Object[] {metadata.getClass().getName(), getJdbcDatabase().getConnection()};
                                final String msg = JdbcPlugin.Util.getString("JdbcDatabaseImpl.Unexpected_exception_while_calling_getSchemas()_and_processing_results", params); //$NON-NLS-1$
                 JdbcPlugin.Util.log(IStatus.WARNING, t, msg);
            }
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
        
        return supportsSchemas;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    @Override
	public String getTypeName() {
        try {
            return getJdbcDatabase().getCapabilities().getCatalogTerm();
        } catch (Throwable t) {
            return JdbcPlugin.Util.getString("JdbcCatalogImpl.CatalogTypeName"); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    @Override
	public String getFullyQualifiedName() {
        // Determine whether catalogs are supported in qualified names ...
        boolean includeCatalogs = false;
        try {
            includeCatalogs = this.getJdbcDatabase().getCapabilities().supportsCatalogsInDataManipulation();
        } catch (JdbcException e) {
            JdbcPlugin.Util.log(e);     // not expected, but log just in case
        } catch (SQLException e) {
            //ignore;
        }
        if ( includeCatalogs ) {
            return getUnqualifiedName();
        }
        return NOT_APPLICABLE;  // empty string
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return this.getPath();
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    @Override
	public IPath getPathInSource( final boolean includeCatalog, final boolean includeSchema) {
        if ( includeCatalog ) {
            return this.getPath();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
     */
    @Override
    public JdbcNode getParentDatabaseObject(boolean includeCatalog, boolean includeSchema) {
        return null;    // there is never a parent of a catalog
    }


}

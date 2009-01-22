/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;

/**
 * JdbcSchemaImpl
 */
public class JdbcSchemaImpl extends JdbcNodeImpl implements JdbcSchema {

    /**
     * Construct an instance of JdbcSchemaImpl.
     * 
     */
    public JdbcSchemaImpl( final JdbcNode parent, final String name ) {
        super(SCHEMA,name,parent);
        ArgCheck.isNotNull(parent);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() throws JdbcException {
        // Create the children ...
        final DatabaseMetaData metadata = getJdbcDatabase().getDatabaseMetaData();
        final List children = new ArrayList();
        final JdbcDatabase database = getJdbcDatabase();
        
        // Get the table types ...
        try {
            String[] tableTypes = database.getIncludes().getIncludedTableTypes();
            if ( tableTypes == null ) {
                tableTypes = database.getCapabilities().getTableTypes();
            }
            for (int i = 0; i < tableTypes.length; ++i) {
                children.add(new JdbcTableTypeImpl(this,tableTypes[i]));
            }
        } catch (Throwable t) {
            final Object[] params = new Object[]{metadata.getClass().getName(),database};
            final String msg = JdbcPlugin.Util.getString("JdbcSchemaImpl.Unexpected_exception_while_calling_getTableTypes()_and_processing_results",params); //$NON-NLS-1$
            JdbcPlugin.Util.log(IStatus.WARNING,t,msg);
        }
        
        // Add the procedure type ...
        if ( database.getIncludes().includeProcedures() ) {
            String procTerm = null;
            try {
                procTerm = database.getCapabilities().getProcedureTerm();
            } catch (Throwable t) {
                procTerm = JdbcPlugin.Util.getString("JdbcSchemaImpl.ProcedureTypeName"); //$NON-NLS-1$
            }
            children.add(new JdbcProcedureTypeImpl(this,procTerm));
        }
        
        // Convert the list to an array and return ...
        return (JdbcNode[])children.toArray(new JdbcNode[children.size()]);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    public String getTypeName() {
        try {
            return getJdbcDatabase().getCapabilities().getSchemaTerm();
        } catch (Throwable t) {
            return JdbcPlugin.Util.getString("JdbcSchemaImpl.SchemaTypeName"); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    public String getFullyQualifiedName() {
        // Determine whether catalogs are supported in qualified names ...
        boolean includeSchemas = false;
        try {
            includeSchemas = this.getJdbcDatabase().getCapabilities().supportsSchemasInDataManipulation();
        } catch (JdbcException e) {
            JdbcPlugin.Util.log(e);     // not expected, but log just in case
        } catch (SQLException e) {
            //ignore;
        }
        final StringBuffer sb = new StringBuffer();
        final String prefix = this.getParent().getFullyQualifiedName();
        if ( prefix.length() != 0 ) {
            sb.append(prefix);
            sb.append(getQualifedNameDelimiter());
        }
        if ( includeSchemas ) {
            sb.append(getUnqualifiedName());
        }
        return sb.toString();  // empty string
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return getPath();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    public IPath getPathInSource( final boolean includeCatalog, final boolean includeSchema) {
        if ( includeSchema && includeCatalog ) {
            return this.getPath();
        }
        if ( includeSchema ) {
            // If the parent is the database ...
            if ( this.getParent() == this.getJdbcDatabase() ) {
                // Then there is no catalog and the path without a catalog is simply the path
                return this.getPath();
            }
            // There is a catalog, so the path is just the name
            return new Path(this.getName());
        }
        // No schema is to be included, so there is no path ...
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
     */
    @Override
    public JdbcNode getParentDatabaseObject(final boolean includeCatalog, final boolean includeSchema) {
        if ( this.getParent() instanceof JdbcCatalog ) {
            return ( includeCatalog ? this.getParent() : null );
        } 
        // else parent instanceof JdbcDatabase
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return getParent().getJdbcDatabase();
    }

}

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

import java.sql.DatabaseMetaData;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.data.MetadataRequest;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.metadata.JdbcTableType;

/**
 * JdbcTableImpl
 */
public class JdbcTableImpl extends JdbcNodeImpl implements JdbcTable {

    private String remarks;

    /**
     * Construct an instance of JdbcTableImpl.
     */
    public JdbcTableImpl( final JdbcTableType parent,
                          final String name ) {
        super(TABLE, name, parent);
        ArgCheck.isNotNull(parent);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() {
        // There are no children
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    public String getTypeName() {
        // Return the table type for this table
        return getParent().getName();
    }

    /**
     * This method is overridden to specify that JdbcTable instances never have children.
     * 
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#allowsChildren()
     */
    @Override
    public boolean allowsChildren() {
        return false;
    }

    /**
     * Return the default selection mode when the selection mode can't be determined any other way. For example, this method is
     * called when the parent selection mode is {@link JdbcNode#PARTIALLY_SELECTED}. This method returns {@link JdbcNode#SELECTED}
     * by default, since tables should be included for import whenever there is a question.
     * 
     * @return the default selection mode
     */
    @Override
    protected int getDefaultSelectionMode() {
        return SELECTED;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    public String getFullyQualifiedName() {
        final StringBuffer sb = new StringBuffer();
        final String prefix = this.getParent().getFullyQualifiedName();
        if (prefix.length() != 0) {
            sb.append(prefix);
            sb.append(getQualifedNameDelimiter());
        }
        final String unqualName = getUnqualifiedName();
        sb.append(unqualName);
        return sb.toString(); // empty string
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return getPathInSource(true, true);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    public IPath getPathInSource( final boolean includeCatalog,
                                  final boolean includeSchema ) {
        // Go up until we get to the schema, catalog or database that contains this object ...
        JdbcNode parent = getParent();
        while (true) {
            // Stop only if the node type is to be included in the path
            if ((parent instanceof JdbcSchema && includeSchema) || (parent instanceof JdbcCatalog && includeCatalog)
                || (parent instanceof JdbcDatabase)) {
                break;
            }
            parent = parent.getParent();
        }

        // If parent instanceof JdbcDatabase, then the path is just the path with this name ...
        if (parent instanceof JdbcDatabase) {
            return new Path(getName());
        }

        // Otherwise, just append the name of this node to that of the parent.
        return parent.getPathInSource().append(getName());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
     */
    @Override
    public JdbcNode getParentDatabaseObject( final boolean includeCatalog,
                                             final boolean includeSchema ) {
        JdbcNode parent = getParent();
        while (parent != null) {
            // Stop only if the node type is to be included in the path
            if ((parent instanceof JdbcSchema && includeSchema) || (parent instanceof JdbcCatalog && includeCatalog)
                || (parent instanceof JdbcDatabase)) {
                break;
            }
            parent = parent.getParent();
        }

        // If parent instanceof JdbcDatabase, then the path is just the path with this name ...
        if (parent != null && parent.isDatabaseObject()) {
            return parent;
        }

        return null;
    }

    /**
     * @return
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param string
     */
    public void setRemarks( String string ) {
        remarks = string;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return getParent().getJdbcDatabase();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#refresh()
     */
    @Override
    public void refresh() {
        super.refresh();
        // Remove any cached information ...
    }

    @Override
    protected Request[] createRequests() throws JdbcException {
        final DatabaseMetaData metadata = this.getJdbcDatabase().getDatabaseMetaData();
        final String catalogNamePattern = JdbcNodeImpl.getCatalogPattern(this);
        final String schemaNamePattern = JdbcNodeImpl.getSchemaPattern(this);
        final String tableNamePattern = this.getName();
        final String columnNamePattern = WILDCARD_PATTERN;

        final Request[] requests = new Request[7]; // 6 requests!

        // 1. Create the "Columns" request
        requests[0] = new GetColumnsRequest(metadata, catalogNamePattern, schemaNamePattern, tableNamePattern, columnNamePattern);

        // 2. Create the "Indexes" request
        final boolean uniqueOnly = getJdbcDatabase().getIncludes().getUniqueIndexesOnly();
        final boolean approxAllowed = getJdbcDatabase().getIncludes().getApproximateIndexes();
        requests[1] = new GetIndexesRequest(metadata, catalogNamePattern, schemaNamePattern, tableNamePattern, uniqueOnly,
                                            approxAllowed);

        // 3. Create the "Primary Key" request
        requests[2] = new GetPrimaryKeyRequest(metadata, catalogNamePattern, schemaNamePattern, tableNamePattern);

        // 4. Create the "Imported Foreign Key" request
        requests[3] = new GetImportedForeignKeysRequest(metadata, catalogNamePattern, schemaNamePattern, tableNamePattern);

        // 5. Create the "Exported Foreign Key" request
        requests[4] = new GetExportedForeignKeysRequest(metadata, catalogNamePattern, schemaNamePattern, tableNamePattern);

        // 6. Create the "Description" request
        requests[5] = new GetDescriptionRequest(this, "getRemarks"); //$NON-NLS-1$

        // Disable what is not to be loaded ...
        if (!this.getJdbcDatabase().getIncludes().includeIndexes()) {
            requests[1] = new DisabledRequest((MetadataRequest)requests[1]);
        }
        if (!this.getJdbcDatabase().getIncludes().includeForeignKeys()) {
            requests[3] = new DisabledRequest((MetadataRequest)requests[3]);
            requests[4] = new DisabledRequest((MetadataRequest)requests[4]);
        }

        // 7. Create the "Imported Foreign Key" request
        requests[6] = new GetAccessForeignKeysRequest(this.getJdbcDatabase().getConnection(), catalogNamePattern,
                                                      schemaNamePattern, tableNamePattern);

        // ?? 6. Create the "Preview" request?
        // ?? 7. Create the "Preview" request?
        return requests;
    }

}

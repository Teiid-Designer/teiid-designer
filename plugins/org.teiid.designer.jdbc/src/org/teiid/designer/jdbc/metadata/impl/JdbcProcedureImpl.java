/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.sql.DatabaseMetaData;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.data.MetadataRequest;
import org.teiid.designer.jdbc.data.Request;
import org.teiid.designer.jdbc.metadata.JdbcCatalog;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcProcedure;
import org.teiid.designer.jdbc.metadata.JdbcProcedureType;
import org.teiid.designer.jdbc.metadata.JdbcSchema;


/**
 * JdbcProcedureImpl
 *
 * @since 8.0
 */
public class JdbcProcedureImpl extends JdbcNodeImpl implements JdbcProcedure {

    private String remarks;
    private short procType;

    /**
     * Construct an instance of JdbcProcedureImpl.
     */
    public JdbcProcedureImpl( final JdbcProcedureType parent,
                              final String name ) {
        super(PROCEDURE, name, parent);
        CoreArgCheck.isNotNull(parent);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    @Override
	public String getTypeName() {
        // Return the table type for this table
        return getParent().getName();
    }

    /**
     * This method is overridden to specify that JdbcTable instances never have children.
     * 
     * @see org.teiid.designer.jdbc.metadata.JdbcNode#allowsChildren()
     */
    @Override
    public boolean allowsChildren() {
        return false;
    }

    /**
     * Return the default selection mode when the selection mode can't be determined any other way. For example, this method is
     * called when the parent selection mode is {@link JdbcNode#PARTIALLY_SELECTED}. This method returns {@link JdbcNode#SELECTED}
     * by default, since procedures should be included for import whenever there is a question.
     * 
     * @return the default selection mode
     */
    @Override
    protected int getDefaultSelectionMode() {
        return SELECTED;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    @Override
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
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return getPathInSource(true, true);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    @Override
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
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getParentDatabaseObject(boolean, boolean)
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

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    @Override
	public JdbcDatabase getJdbcDatabase() {
        return getParent().getJdbcDatabase();
    }

    /**
     * @return
     */
    @Override
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
     * @See org.teiid.designer.jdbc.metadata.JdbcProcedure#getProcedureType()
     */
    @Override
	public short getProcedureType() {
        return procType;
    }

    public void setProcedureType( final short type ) {
        CoreArgCheck.isTrue(type == RESULT_UNKNOWN || type == RETURNS_RESULT || type == NO_RESULT,
                        JdbcPlugin.Util.getString("JdbcProcedureImpl.Invalid_procedure_type")); //$NON-NLS-1$
        this.procType = type;
    }

    @Override
    protected Request[] createRequests() {
        DatabaseMetaData metadata = null;

        try {
            metadata = this.getJdbcDatabase().getDatabaseMetaData();
        } catch (JdbcException e) {
            JdbcPlugin.Util.log(e);
        }
        final DatabaseMetaData finalMetadata = metadata;
        final String catalogNamePattern = JdbcNodeImpl.getCatalogPattern(this);
        final String schemaNamePattern = JdbcNodeImpl.getSchemaPattern(this);
        final String procedureNamePattern = this.getName();
        final String procedureColumnNamePattern = WILDCARD_PATTERN;

        final Request[] requests = new Request[2]; // 2 requests!

        // 1. Create the "Columns" request
        requests[0] = new GetProcedureParametersRequest(finalMetadata, catalogNamePattern, schemaNamePattern,
                                                        procedureNamePattern, procedureColumnNamePattern);

        // 2. Create the "Description" request
        requests[1] = new GetDescriptionRequest(this, "getRemarks"); //$NON-NLS-1$

        // Disable what is not to be loaded ...
        if (!this.getJdbcDatabase().getIncludes().includeProcedures()) {
            requests[1] = new DisabledRequest((MetadataRequest)requests[1]);
        }

        return requests;
    }

}

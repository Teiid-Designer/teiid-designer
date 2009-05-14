/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.data.MetadataRequest;
import com.metamatrix.modeler.jdbc.data.Response;

/**
 * @since 5.5
 */
public class GetAccessForeignKeysRequest extends MetadataRequest {
    public static final String NAME = JdbcPlugin.Util.getString("GetAccessForeignKeysRequestName"); //$NON-NLS-1$

    /**
     * Construct an instance of GetImportedForeignKeysRequest.
     * 
     * @param name
     * @param target
     * @param methodName
     * @param params
     */
    public GetAccessForeignKeysRequest( final Connection connection,
                                        final String catalogNamePattern,
                                        final String schemaNamePattern,
                                        final String tableNamePattern ) {
        super(NAME, connection, "getAccessForeignKeys", //$NON-NLS-1$
              new Object[] {catalogNamePattern, schemaNamePattern, tableNamePattern});
    }

    /**
     * This method is overridden to optimize performance.
     * 
     * @see com.metamatrix.modeler.jdbc.data.MethodRequest#performInvocation(com.metamatrix.modeler.jdbc.data.Response)
     * @since 5.5
     */
    @Override
    protected IStatus performInvocation( final Response results ) {
        // Override to optimize ...
        final Connection connection = (Connection)this.getTarget();
        IStatus status = null;
        try {
            final String catalogPattern = (String)getParameters()[0];
            final String schemaPattern = (String)getParameters()[1];
            final String tablePattern = (String)getParameters()[2];
            Statement stmt = connection.createStatement();
            ResultSet foreignKeys = stmt.executeQuery("SELECT szRelationship, szReferencedObject, szColumn, szReferencedColumn FROM MSysRelationships WHERE szObject like '" + tablePattern + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            while (foreignKeys.next()) {
                String fkName = foreignKeys.getString(1);// FK_NAME
                // if FK has no name - make it up (use tablename instead)

                String pkTableName = foreignKeys.getString(2);// PKTABLE_NAME

                String fkey = foreignKeys.getString(3); // local column //FKCOLUMN_NAME
                String pkey = foreignKeys.getString(4); // foreign column //PKCOLUMN_NAME
                List row = new ArrayList();
                row.add(catalogPattern);
                row.add(schemaPattern);
                row.add(pkTableName);
                row.add(pkey);
                row.add(catalogPattern);
                row.add(schemaPattern);
                row.add(tablePattern);
                row.add(fkey);
                row.add(null);
                row.add(null);
                row.add(null);
                row.add(fkName);
                row.add("Primary key"); //$NON-NLS-1$
                row.add(null);
                results.addRecord(row);
            }

        } catch (SQLException e) {
            status = JdbcUtil.createIStatus(e, e.getLocalizedMessage());
        }
        return status;
    }
}

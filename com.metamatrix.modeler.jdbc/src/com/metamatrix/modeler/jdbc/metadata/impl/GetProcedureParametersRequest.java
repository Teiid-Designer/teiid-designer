/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.data.MetadataRequest;
import com.metamatrix.modeler.jdbc.data.Response;

/**
 * GetPrimaryKeyRequest
 */
public class GetProcedureParametersRequest extends MetadataRequest {
    
    public static final String NAME = JdbcPlugin.Util.getString("GetProcedureParametersRequest"); //$NON-NLS-1$
    private static final String METHOD_NAME = "getProcedureColumns"; //$NON-NLS-1$

    /**
     * Construct an instance of GetPrimaryKeyRequest.
     * @param name
     * @param target
     * @param methodName
     * @param params
     */
    public GetProcedureParametersRequest( final DatabaseMetaData metadata,
                                          final String catalogNamePattern, 
                                          final String schemaNamePattern,
                                          final String procedureNamePattern,
                                          final String procedureColumnNamePattern ) {
        super(NAME, metadata, METHOD_NAME, 
              new Object[]{catalogNamePattern,schemaNamePattern,
                           procedureNamePattern,procedureColumnNamePattern});
    }
    
    /** 
     * This method is overridden to optimize performance.
     * @see com.metamatrix.modeler.jdbc.data.MethodRequest#performInvocation(com.metamatrix.modeler.jdbc.data.Response)
     * @since 4.2
     */
    @Override
    protected IStatus performInvocation(final Response results) {
        // Override to optimize ...
        final DatabaseMetaData dbmd = this.getDatabaseMetaData();
        ResultSet resultSet = null;
        IStatus status = null;
        try {
            final String catalogPattern = (String)getParameters()[0];
            final String schemaPattern  = (String)getParameters()[1];
            final String tablePattern   = (String)getParameters()[2];
            final String paramPattern   = (String)getParameters()[3];
            resultSet = dbmd.getProcedureColumns(catalogPattern, schemaPattern, tablePattern, paramPattern);
            Response.addResults(results,resultSet,this.isMetadataRequested());
        } catch ( SQLException e ) {
            status = JdbcUtil.createIStatus(e,e.getLocalizedMessage());
        } finally {
            if ( resultSet != null ) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                }
            }
        }
        return status;
    }
    
}

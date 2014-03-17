/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.admin;

import java.sql.Driver;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.jdbc.TeiidDriver;
import org.teiid.runtime.client.TeiidRuntimePlugin;
import org.teiid.runtime.client.query.QueryService;

/**
 *
 */
public class ExecutionAdminFactory implements IExecutionAdminFactory {

    private DataTypeManagerService dataTypeManagerService;
    
    private QueryService queryService;

    @Override
    public IExecutionAdmin createExecutionAdmin(ITeiidServer teiidServer) throws Exception {
        return new ExecutionAdmin(teiidServer);
    }
    
    @Override
    public IDataTypeManagerService getDataTypeManagerService(ITeiidServerVersion teiidVersion) {
        if (dataTypeManagerService == null) {
            dataTypeManagerService = DataTypeManagerService.getInstance(teiidVersion);
        }
        
        return dataTypeManagerService;
    }

    @Override
    public Driver getTeiidDriver(ITeiidServerVersion teiidVersion) {
        TeiidDriver instance = TeiidDriver.getInstance();
        instance.setTeiidVersion(teiidVersion);
        return instance;
    }

    @Override
    public IQueryService getQueryService(ITeiidServerVersion teiidVersion) {
        if (queryService == null) {
            queryService = new QueryService(teiidVersion);
        }
        
        return queryService;
    }

    @Override
    public String getRuntimePluginPath() {
        return TeiidRuntimePlugin.getPluginPath();
    }
}
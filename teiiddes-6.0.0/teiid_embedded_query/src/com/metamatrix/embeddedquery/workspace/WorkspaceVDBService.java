/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.embeddedquery.workspace;

import java.util.List;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.dqp.embedded.services.EmbeddedVDBService;


/** 
 * VDB Service that is for the execution of the Workspace queries
 */
public class WorkspaceVDBService extends EmbeddedVDBService{
    private static String ADMIN = "admin"; //$NON-NLS-1$
    
    @Override
    public int getModelVisibility(String vdbName, String vdbVersion, String modelName) throws MetaMatrixComponentException {
        if (vdbName.equalsIgnoreCase(ADMIN)) {         
            return ModelInfo.PUBLIC;
        }
        return super.getModelVisibility(vdbName, vdbVersion, modelName);
    }
    
    @Override
    public List getConnectorBindingNames(String vdbName, String vdbVersion, String modelName) throws MetaMatrixComponentException {
    	WorkspaceInfo workspaceInfo = WorkspaceInfoHolder.getInfo();
        if (vdbName.equalsIgnoreCase(ADMIN) && workspaceInfo != null) { 
            return workspaceInfo.getBinding(modelName);
        }
        return super.getConnectorBindingNames(vdbName, vdbVersion, modelName);
    }
}

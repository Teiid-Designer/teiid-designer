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

package com.metamatrix.embeddedquery.workspace;

import java.util.List;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.dqp.embedded.services.EmbeddedVDBService;
import com.metamatrix.dqp.service.DQPServiceRegistry;


/** 
 * VDB Service that is for the execution of the Workspace queries
 */
public class WorkspaceVDBService extends EmbeddedVDBService{
    private static String ADMIN = "admin"; //$NON-NLS-1$
    
    public WorkspaceVDBService(DQPServiceRegistry svcRegistry) throws MetaMatrixComponentException{
        super(svcRegistry);
    } 
    
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

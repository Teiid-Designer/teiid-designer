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

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.dqp.embedded.services.EmbeddedMetadataService;
import com.metamatrix.dqp.service.DQPServiceRegistry;
import com.metamatrix.query.metadata.QueryMetadataInterface;


/** 
 * Metadata service which can take a injected Query Metadata Service from the modeler for execution in the DQP
 * for purposes of the DQP workspace execution.
 */
public class WorkspaceMetadataService extends EmbeddedMetadataService {
    private static String ADMIN = "admin"; //$NON-NLS-1$
            
    public WorkspaceMetadataService (DQPServiceRegistry svcRegistry) throws MetaMatrixComponentException{
        super(svcRegistry);
    }    

    @Override
    public QueryMetadataInterface lookupMetadata(String vdbName, String vdbVersion) throws MetaMatrixComponentException {
    	WorkspaceInfo workspaceInfo = WorkspaceInfoHolder.getInfo();
        if (vdbName.equalsIgnoreCase(ADMIN) && workspaceInfo != null) {
            Object metadata = workspaceInfo.getMetadata();
            return (QueryMetadataInterface)metadata;
        }
        return super.lookupMetadata(vdbName, vdbVersion);
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

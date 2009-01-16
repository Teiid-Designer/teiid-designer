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
package com.metamatrix.modeler.transformation.ui.workspace;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadata;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.query.metadata.QueryMetadataInterface;

public class WorkspaceExecutionUtil {

    public static QueryMetadataInterface getMetadata( final EObject eObject ) {
        QueryMetadataContext context = TransformationMetadataFactory.buildQueryMetadataContext(eObject, false);
        context.setIndexSelector(new ModelWorkspaceIndexSelector());
        QueryMetadataInterface metadata = new WorkspaceMetadata(context);
        return metadata;
    }

    /**
     * This class is for workspace execution. Query Engine does work well with mmuuids that Modeler Metadata object supplies and
     * Server Rutime Metadata works on VDB specific indexes. What we need metadata that works on modeler index files but does not
     * serve mmuuids
     */
    private static class WorkspaceMetadata extends TransformationMetadata {
        WorkspaceMetadata( QueryMetadataContext context ) {
            super(context);
        }
    }
}

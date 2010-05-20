/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.workspace;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadata;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import org.teiid.query.metadata.QueryMetadataInterface;

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

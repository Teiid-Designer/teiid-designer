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

package com.metamatrix.modeler.internal.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;


/** 
 * Class used to fine-tune the delete-related workspace search
 * In particular, it insures that if certain model types (i.e. xsd files) are out of scope for the search, they don't get 
 * included. This class can be expanded in the future to handle other cases to help reduce "time-to-delete"
 * see Defect 22774
 * @since 5.0.2
 */
public class DeleteRelatedWorkspaceSearch extends ModelWorkspaceSearch {

    boolean ignoreXsdResources = false;
    /** 
     * 
     * @since 5.0.2
     */
    public DeleteRelatedWorkspaceSearch() {
        super();
    }

    public DeleteRelatedWorkspaceSearch(boolean ignoreXsdResources) {
        this();
        this.ignoreXsdResources = ignoreXsdResources;
    }
    
    @Override
    protected IndexSelector createIndexSelector( IProgressMonitor monitor ) {
        return new ModelWorkspaceSearchIndexSelector(getApplicableModelResources(), monitor);
    }
    
    protected Collection getApplicableModelResources() {
        Collection modelResources = new ArrayList();
        ModelResource[] resources = null;
        try {
            resources = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
        } catch (CoreException theException) {
            ModelerCore.Util.log(IStatus.ERROR,theException,theException.getMessage());
        }
        if( !ignoreXsdResources ) {
            modelResources = Arrays.asList(resources);
        } else {
            if( resources != null && resources.length > 0 ) {
                // Look for XSD files, if NOT xsd, add to collection
                for( int i=0; i<resources.length; i++ ) {
                    if( !resources[i].isXsd()) {
                        modelResources.add(resources[i]);
                    }
                }
            }
        }
        
        return modelResources;
    }
    
}

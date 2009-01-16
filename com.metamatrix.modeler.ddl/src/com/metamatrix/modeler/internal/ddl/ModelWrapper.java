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

package com.metamatrix.modeler.internal.ddl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ModelWrapper
 */
public class ModelWrapper {

    private final Resource emfResource;
    private final ModelContents contents;
    private final String modelName;
    private final String modelFilename;
    private final ModelWorkspaceSelections selections;

    /**
     * Construct an instance of ModelWrapper.
     * @param emfResource the {@link com.metamatrix.metamodels.relational.RelationalPackage relational} 
     * EMF resource that contains the model to be written out; may not be null
     * @param modelContents the ModelContents object to use; may not be null
     * @param modelName the name of the model in the resource
     * @param modelFilename the filename of the model resource
     */
    public ModelWrapper( final Resource emfResource, final ModelContents contents,
                         final ModelWorkspaceSelections selections,
                         final String modelName, final String modelFilename ) {
        ArgCheck.isNotNull(emfResource);
        this.emfResource = emfResource;
        this.selections = selections;
        this.contents = contents != null ? contents : 
                        this.emfResource instanceof EmfResource ? 
                                ((EmfResource)this.emfResource).getModelContents() :
                                new ModelContents(this.emfResource);
        this.modelName = modelName != null ? modelName : ""; //$NON-NLS-1$
        this.modelFilename = modelFilename != null ? modelFilename : ""; //$NON-NLS-1$
    }

    public ModelContents getContents() {
        return contents;
    }
    public Resource getEmfResource() {
        return emfResource;
    }
    /**
     * @return
     */
    public String getModelFilename() {
        return modelFilename;
    }

    /**
     * @return
     */
    public String getModelName() {
        return modelName;
    }
    
    public boolean isSelected( final EObject obj ) {
        if ( this.selections == null ) {
            return true;
        }
        final int mode = this.selections.getSelectionMode(obj);
        return ( mode == ModelWorkspaceSelections.SELECTED || mode == ModelWorkspaceSelections.PARTIALLY_SELECTED );
    }

}

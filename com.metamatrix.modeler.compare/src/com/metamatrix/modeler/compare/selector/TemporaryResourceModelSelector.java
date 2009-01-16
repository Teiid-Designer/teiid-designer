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

package com.metamatrix.modeler.compare.selector;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;

/**
 * ModelResourceSelector
 */
public abstract class TemporaryResourceModelSelector extends AbstractModelSelector {

    private Container resourceSet;
    private String label;
    private static final String CONTAINER_NAME = "Container for difference analysis"; //$NON-NLS-1$

    /**
     * Construct an instance of ModelResourceSelector.
     * 
     */
    public TemporaryResourceModelSelector() {
        super();
        try {
            this.resourceSet = ModelerCore.createContainer(CONTAINER_NAME);
        } catch (CoreException e) {
            ModelerComparePlugin.Util.log(e);
        }
    }
    
    protected ResourceSet getResourceSet() {
        return this.resourceSet;
    }
    
    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getLabel()
     */
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel( final String label ) {
        this.label = label;
    }


}

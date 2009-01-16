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

package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.metamatrix.modeler.core.ModelerCore;

/**
 * ModelWorkspaceInfo
 */
public class ModelWorkspaceInfo extends OpenableModelWorkspaceItemInfo {

    /**
     * A array with all the non-model projects contained by this model
     */
    Object[] nonModelResources;

    /**
     * Constructs a new Model Workspace Info 
     */
    protected ModelWorkspaceInfo() {
    }
    /**
     * Compute the non-java resources contained in this java project.
     */
    private Object[] computeNonModelResources() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        int length = projects.length;
        Object[] nonModelResourcesTemp = null;
        int index = 0;
        for (int i = 0; i < length; i++) {
            IProject project = projects[i];
            if (!ModelerCore.hasModelNature(project)) {
                if (nonModelResourcesTemp == null) {
                    nonModelResourcesTemp = new Object[length];
                }
                nonModelResourcesTemp[index++] = project;
            }
        }
        if (index == 0) return NO_NON_MODEL_RESOURCES;
        if (index < length) {
            System.arraycopy(nonModelResources, 0, nonModelResources = new Object[index], 0, index);
        }
        return nonModelResourcesTemp;
    }
    
    /**
     * Returns an array of non-java resources contained in the receiver.
     */
    Object[] getNonModelResources() {
    
        Object[] nonModelResources = this.nonModelResources;
        if (nonModelResources == null) {
            nonModelResources = computeNonModelResources();
            this.nonModelResources = nonModelResources;
        }
        return nonModelResources;
    }

}

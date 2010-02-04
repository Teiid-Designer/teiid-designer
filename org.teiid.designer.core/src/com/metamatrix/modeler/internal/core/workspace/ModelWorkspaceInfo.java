/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

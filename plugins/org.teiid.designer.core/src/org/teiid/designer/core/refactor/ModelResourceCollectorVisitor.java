/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;


/**
 * ModelResourceCollectorVisitor is a collector visitor for finding ModelResource instances in the workspace.
 *
 * @since 8.0
 */
public class ModelResourceCollectorVisitor implements IResourceVisitor {

    private List<IFile> resources;
    private List<ModelResource> modelResources;

    /**
     * Construct an instance of ModelResourceCollectorVisitor.
     */
    public ModelResourceCollectorVisitor() {
        this.resources = new ArrayList<IFile>();
        this.modelResources = new ArrayList<ModelResource>();
    }

    @Override
	public boolean visit( IResource resource ) {
        if (resource instanceof IFile) {
            resources.add((IFile) resource);
            return false; // don't need to go deeper
        }
        return true;
    }

    public List<IFile> getResources() {
        return resources;
    }

    public List<ModelResource> getModelResources() throws CoreException {
        for (IFile resource : resources) {
            if (ModelUtil.isModelFile(resource)) {
                final ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(resource);
                if (modelResource != null) {
                    modelResources.add(modelResource);
                }
            }
        }
        return modelResources;
    }
}

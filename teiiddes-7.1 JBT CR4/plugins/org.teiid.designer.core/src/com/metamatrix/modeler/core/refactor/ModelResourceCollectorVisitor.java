/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelResourceCollectorVisitor is a collector visitor for finding ModelResource instances in the workspace.
 */
public class ModelResourceCollectorVisitor implements IResourceVisitor {

    private List resources;
    private List modelResources;

    /**
     * Construct an instance of ModelResourceCollectorVisitor.
     */
    public ModelResourceCollectorVisitor() {
        this.resources = new ArrayList();
        this.modelResources = new ArrayList();
    }

    public boolean visit( IResource resource ) {
        if (resource != null && resource instanceof IFile) {
            resources.add(resource);
            return false; // don't need to go deeper
        }
        return true;
    }

    public List getResources() {
        return resources;
    }

    public List getModelResources() throws CoreException {
        for (Iterator iter = resources.iterator(); iter.hasNext();) {
            IResource resource = (IResource)iter.next();
            if (ModelUtil.isModelFile(resource)) {
                final ModelResource modelResource = ModelerCore.getModelEditor().findModelResource((IFile)resource);
                if (modelResource != null) {
                    modelResources.add(modelResource);
                }
            }
        }
        return modelResources;
    }
}

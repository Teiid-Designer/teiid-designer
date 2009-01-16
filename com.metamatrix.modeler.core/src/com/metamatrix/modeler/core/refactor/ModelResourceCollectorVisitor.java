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

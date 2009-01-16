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

package com.metamatrix.modeler.internal.core.index;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * ModelWorkspaceIndexSelectorFactory
 */
public class ModelWorkspaceIndexSelectorFactory implements IndexSelectorFactory {

    /**
     * Construct an instance of ModelWorkspaceIndexSelectorFactory.
     * 
     */
    public ModelWorkspaceIndexSelectorFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.index.IndexSelectorFactory#createIndexSelector(java.util.List)
     */
    public IndexSelector createIndexSelector( final List modelWorkspaceItems ) throws CoreException {
        ArgCheck.isNotNull(modelWorkspaceItems);
        final int numItems = modelWorkspaceItems.size();
        if ( numItems == 0 ) {
            return new ModelWorkspaceIndexSelector();
        } else if ( numItems == 1 ) {
            final Object item = modelWorkspaceItems.get(0);
            if ( item instanceof ModelWorkspace ) {
                // It's the workspace ...
                return new ModelWorkspaceIndexSelector();
            }
        }
        final List modelResources = new LinkedList();
        final Iterator iter = modelWorkspaceItems.iterator();
        while (iter.hasNext()) {
            final ModelWorkspaceItem item = (ModelWorkspaceItem)iter.next();
            final ModelWorkspaceItem wsItem = item;
            doAddModelResources(wsItem,modelResources);
        }
        
        final List selectors = new LinkedList();
        final Iterator resourceIter = modelResources.iterator();
        while (resourceIter.hasNext()) {
            final ModelResource modelResource = (ModelResource)resourceIter.next();
            final IResource resource = modelResource.getCorrespondingResource();
            final String filePath = resource.getFullPath().toString();
            final IndexSelector selector = new ResourceFileIndexSelector(filePath);
            selectors.add(selector);
        }
        if ( selectors.size() == 0 ) {
            return new ModelWorkspaceIndexSelector();
        }
        if ( selectors.size() == 1 ) {
            return (IndexSelector)selectors.get(0);
        }
        return new CompositeIndexSelector(selectors);
    }

    protected void doAddModelResources( final ModelWorkspaceItem item, final List modelResources ) throws CoreException {
        if ( item instanceof ModelWorkspace ) {
            modelResources.add(item);
        } else if ( item instanceof ModelResource ) {
            modelResources.add(item);
        } else {
            // It's not a resource, so must be some container ...
            final ModelWorkspaceItem[] children = item.getChildren();
            for (int i = 0; i < children.length; ++i) {
                final ModelWorkspaceItem child = children[i];
                doAddModelResources(child,modelResources);
            }
        }
    }
    
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.index;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.index.CompositeIndexSelector;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspace;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;


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
     * @see org.teiid.designer.core.index.IndexSelectorFactory#createIndexSelector(java.util.List)
     */
    @Override
	public IndexSelector createIndexSelector( final List modelWorkspaceItems ) throws CoreException {
        CoreArgCheck.isNotNull(modelWorkspaceItems);
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

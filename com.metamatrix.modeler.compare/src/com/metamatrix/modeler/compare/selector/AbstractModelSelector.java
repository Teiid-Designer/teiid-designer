/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.refactor.OrganizeImportCommand;
import com.metamatrix.modeler.core.refactor.OrganizeImportHandler;

/**
 * AbstractModelSelector
 */
public abstract class AbstractModelSelector implements ModelSelector {

    /**
     * Construct an instance of AbstractModelSelector.
     * 
     */
    public AbstractModelSelector() {
        super();
    }

    /**
     */
    public abstract Resource getResource() throws ModelerCoreException;
    
    /**
     * Adds the supplied objects as new root-level objects.  The objects will be added immediately
     * after the last {@link #getRootObjects() exposed root} object.
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#addRootObjects(java.util.List)
     */
    public void addRootObjects(final List newRoots, final int startingIndex) throws ModelerCoreException {
        if ( newRoots == null || newRoots.isEmpty() ) {
            return;
        }
        final List roots = this.getResource().getContents();
        int index = startingIndex;
        
        // Add the new roots that haven't been added yet ...
        final Iterator iter = newRoots.iterator();
        while (iter.hasNext()) {
            final Object newRoot = iter.next();
            if ( !roots.contains(newRoot) ) {
                if ( index >= 0 && roots.size() >= index ) {
                    roots.add(index,newRoot);
                    ++index;
                } else {
                    roots.add(newRoot);
                }
            }
        }
    }

    /**
     * Adds the supplied objects as new root-level objects.  The objects will be added immediately
     * after the last {@link #getRootObjects() exposed root} object.
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#addRootObjects(java.util.List)
     */
    public void addRootObjects(final List newRoots) throws ModelerCoreException {
        if ( newRoots == null || newRoots.isEmpty() ) {
            return;
        }
        final List exposedRoots = this.getRootObjects();
        final int indexOfNextExposedRoot = exposedRoots.size();
        addRootObjects(newRoots,indexOfNextExposedRoot);
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#rebuildModelImports()
     */
    public void rebuildModelImports() throws ModelerCoreException {
        final OrganizeImportHandler handler = new OrganizeImportHandler() {
            public Object choose(List options) {
                return null;
            }
        };
        final OrganizeImportCommand command = new OrganizeImportCommand();
        command.setResource(this.getResource());
        command.setHandler(handler);
        command.execute(null);
    }

}

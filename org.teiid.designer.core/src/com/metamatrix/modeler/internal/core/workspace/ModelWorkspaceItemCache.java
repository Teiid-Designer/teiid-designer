/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.util.Map;

import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.Openable;
import com.metamatrix.modeler.internal.core.util.OverflowingLRUCache;

/**
 * An LRU cache of <code>JavaElements</code>.
 */
public class ModelWorkspaceItemCache extends OverflowingLRUCache {
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new element cache of the given size.
     */
    public ModelWorkspaceItemCache(int size) {
        super(size);
    }
        
    /**
     * Returns true if the element is successfully closed and
     * removed from the cache, otherwise false.
     *
     * <p>NOTE: this triggers an external removal of this element
     * by closing the element.
     */
    @Override
    protected boolean close(Map.Entry entry) {
        Openable element = (Openable) entry.getKey();
        try {
            if (element.hasUnsavedChanges()) {
                return false;
            }
            // We must close an entire JarPackageFragmentRoot at once.
//                if (element instanceof JarPackageFragment) {
//                    JarPackageFragment packageFragment= (JarPackageFragment) element;
//                    JarPackageFragmentRoot root = (JarPackageFragmentRoot) packageFragment.getParent();
//                    root.close();
//                } else {
                element.close();
//                }
            return true;
        } catch (ModelWorkspaceException npe) {
            return false;
        }
    }
    
}

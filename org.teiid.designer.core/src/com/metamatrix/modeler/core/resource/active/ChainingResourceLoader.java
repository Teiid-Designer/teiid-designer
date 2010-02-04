/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource.active;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * DefaultResourceLoader
 */
public class ChainingResourceLoader implements EmfResourceLoader {
    
    private final List loaders;

    /**
     * Construct an instance of DefaultResourceLoader.
     * 
     */
    public ChainingResourceLoader() {
        super();
        this.loaders = new LinkedList();
    }

    /**
     * Return the loaders that are in the chain.
     * @return the list of {@link EmfResourceLoader} instances; never null
     */
    public List getLoaders() {
        return loaders;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.ResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int)
     */
    public void loadFeature(final EList valueHolder, final Class dataClass,
                            final EObject owner, final int featureId) {
        final Iterator iter = this.loaders.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if ( obj != null && obj != this && obj instanceof EmfResourceLoader ) {
                final EmfResourceLoader loader = (EmfResourceLoader)obj;
                loader.loadFeature(valueHolder,dataClass,owner,featureId);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.ResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int)
     */
    public void loadFeature(final EList valueHolder, final Class dataClass,
                            final EObject owner, final int featureId,
                            final int reverseFeatureId) {
        final Iterator iter = this.loaders.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if ( obj != null && obj != this && obj instanceof EmfResourceLoader ) {
                final EmfResourceLoader loader = (EmfResourceLoader)obj;
                loader.loadFeature(valueHolder,dataClass,owner,featureId,reverseFeatureId);
            }
        }
    }

}

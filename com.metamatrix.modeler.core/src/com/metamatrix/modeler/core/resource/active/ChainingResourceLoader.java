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

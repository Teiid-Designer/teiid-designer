/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource.active;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;


/**
 * This implementation of {@link com.metamatrix.mtk.emf.resource.active.EmfResourceLoader}
 * does nothing.
 */
public class DefaultResourceLoader implements EmfResourceLoader {

    /**
     * Construct an instance of DefaultResourceLoader.
     * 
     */
    public DefaultResourceLoader() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.EmfResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int)
     */
    public void loadFeature(EList valueHolder, Class dataClass, EObject owner, int featureId) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.EmfResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int, int)
     */
    public void loadFeature(EList valueHolder, Class dataClass, EObject owner, int featureId, int reverseFeatureId) {
        // do nothing
    }

}

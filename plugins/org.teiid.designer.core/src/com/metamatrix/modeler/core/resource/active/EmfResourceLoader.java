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
 * ResourceLoader
 */
public interface EmfResourceLoader {

    /**
     * 
     * @param valueHolder
     * @param dataClass
     * @param owner
     * @param featureId
     */
    public void loadFeature(EList valueHolder, Class dataClass,
                            EObject owner,int featureId);

    /**
     * 
     * @param valueHolder
     * @param dataClass
     * @param owner
     * @param featureId
     * @param reverseFeatureId
     */
    public void loadFeature(EList valueHolder, Class dataClass,
                            EObject owner,int featureId, int reverseFeatureId);

}

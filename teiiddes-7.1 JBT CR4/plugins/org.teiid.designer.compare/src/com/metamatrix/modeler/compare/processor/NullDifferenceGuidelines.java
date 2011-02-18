/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.processor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.modeler.compare.DifferenceGuidelines;

/**
 * NullDifferenceGuidelines
 */
public class NullDifferenceGuidelines implements DifferenceGuidelines {

    public static final NullDifferenceGuidelines INSTANCE = new NullDifferenceGuidelines();

    /**
     * Construct an instance of NullDifferenceGuidelines.
     * 
     */
    public NullDifferenceGuidelines() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceGuidelines#includeMetamodel(java.lang.String)
     */
    public boolean includeMetamodel(String metamodelUri) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceGuidelines#includeFeature(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean includeFeature(EStructuralFeature feature) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceGuidelines#includeMetaclass(org.eclipse.emf.ecore.EClass)
     */
    public boolean includeMetaclass(EClass eclass) {
        return true;
    }

}

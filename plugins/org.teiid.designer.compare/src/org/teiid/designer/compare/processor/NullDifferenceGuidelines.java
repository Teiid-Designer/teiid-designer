/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.processor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.designer.compare.DifferenceGuidelines;

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
     * @see org.teiid.designer.compare.DifferenceGuidelines#includeMetamodel(java.lang.String)
     */
    public boolean includeMetamodel(String metamodelUri) {
        return true;
    }

    /**
     * @see org.teiid.designer.compare.DifferenceGuidelines#includeFeature(org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean includeFeature(EStructuralFeature feature) {
        return true;
    }

    /**
     * @see org.teiid.designer.compare.DifferenceGuidelines#includeMetaclass(org.eclipse.emf.ecore.EClass)
     */
    public boolean includeMetaclass(EClass eclass) {
        return true;
    }

}

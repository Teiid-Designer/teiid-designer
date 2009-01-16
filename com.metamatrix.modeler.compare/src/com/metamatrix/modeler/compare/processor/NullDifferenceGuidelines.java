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

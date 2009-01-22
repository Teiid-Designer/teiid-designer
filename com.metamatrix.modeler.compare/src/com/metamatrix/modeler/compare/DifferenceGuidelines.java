/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * DifferenceGuidelines
 */
public interface DifferenceGuidelines {

    public boolean includeMetamodel( String metamodelUri );

    public boolean includeFeature( EStructuralFeature feature );
    
    public boolean includeMetaclass( EClass eclass );

}

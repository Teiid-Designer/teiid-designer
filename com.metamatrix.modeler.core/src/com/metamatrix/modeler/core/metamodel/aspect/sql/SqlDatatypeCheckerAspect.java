/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/** 
 * @since 4.3
 */
public interface SqlDatatypeCheckerAspect {

    /**
     * Return true if the specified {@link org.eclipse.emf.ecore.EStructuralFeature} 
     * is a structural feature of this {@link org.eclipse.emf.ecore.EObject} <b>and</b>
     * and along accepts or returns a datatype as a value.
     * @param eObject The <code>EObject</code> to be checked 
     * @param eFeature The <code>EStructuralFeature</code> to be checked
     * @return true if the feature value is a datatype
     */
    boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature);
    
}

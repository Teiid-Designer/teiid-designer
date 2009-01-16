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

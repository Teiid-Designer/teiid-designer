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

package com.metamatrix.modeler.core.compare;

import java.util.List;

import org.eclipse.emf.ecore.EReference;

/**
 * This interface represents a factory for {@link EObjectMatcher} instances.  Extensions of the
 * {@link ModelerCore.EXTENSION_POINT.MODEL_OBJECT_RESOLVER#UNIQUE_ID
 */
public interface EObjectMatcherFactory {
    
    /**
     * Create the {@link EObjectMatcher} instances that should be used to find matches between
     * root-level objects in models.  Many implementations might return the same
     * matchers for all EReferences in a metaclass, and many implementations might return the same
     * matchers for all EReferences in a metamodel.  This method should return an empty list
     * if this factory has no matchers for root-level objects.
     * @return the list of EObjectMatcher instances; may not be null, but may be empty
     */
    List createEObjectMatchersForRoots();
    
    /**
     * Create the {@link EObjectMatcher} instances that should be used to find matches between
     * values of the supplied {@link EReference}.  Many implementations might return the same
     * matchers for all EReferences in a metaclass, and many implementations might return the same
     * matchers for all EReferences in a metamodel.  This method should return an empty list
     * if this factory has no matchers for the supplied feature.
     * @param reference the reference; never null
     * @return the list of EObjectMatcher instances; may not be null, but may be empty
     */
    List createEObjectMatchers( final EReference reference );

}

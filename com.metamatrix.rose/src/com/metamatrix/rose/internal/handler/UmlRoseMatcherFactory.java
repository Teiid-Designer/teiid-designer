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

package com.metamatrix.rose.internal.handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.uml2.uml.UMLPackage;

import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;

/**
 * UmlRoseMatcherFactory
 * 
 * @since 4.1
 */
public class UmlRoseMatcherFactory implements
                                  EObjectMatcherFactory {

    //============================================================================================================================
    // Variables
    
    private final List standardMatchers;

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of UmlRoseMatcherFactory.
     * @since 4.1
     */
    public UmlRoseMatcherFactory() {
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add(new UmlRoseIdMatcher());
        this.standardMatchers.add(new UmlRoseNameMatcher());
    }

    //============================================================================================================================
    // Implemented Methods

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     * @since 4.1
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if (!UMLPackage.eINSTANCE.equals(metamodel)) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }

        return this.standardMatchers;
    }
    
    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     * @since 4.1
     */
    public List createEObjectMatchersForRoots() {
        return this.standardMatchers;
    }
}

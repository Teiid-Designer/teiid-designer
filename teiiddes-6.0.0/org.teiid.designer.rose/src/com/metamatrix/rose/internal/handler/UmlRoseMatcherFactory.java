/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

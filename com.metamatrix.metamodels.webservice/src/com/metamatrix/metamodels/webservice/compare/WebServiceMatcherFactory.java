/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;


/** 
 * WebServiceMatcherFactory
 */
public class WebServiceMatcherFactory implements
                                     EObjectMatcherFactory {

    private final List standardMatchers;

    /**
     * Construct an instance of WebServiceMatcherFactory.
     * 
     */
    public WebServiceMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new WebServiceComponentNameToNameMatcher() );
        this.standardMatchers.add( new WebServiceComponentNameToNameIgnoreCaseMatcher() );
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        // webservice objects can be roots, so return the matchers 
        return this.standardMatchers;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !WebServicePackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }
        
        return this.standardMatchers;
    }

}

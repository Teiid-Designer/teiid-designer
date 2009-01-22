/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;

/**
 * UuidMatcherFactory
 */
public class RelationalMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;

    /**
     * Construct an instance of UuidMatcherFactory.
     * 
     */
    public RelationalMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new RelationalEntityNameToNameMatcher() );
        this.standardMatchers.add( new RelationalEntityNameInSourceToNameInSourceMatcher() );
        this.standardMatchers.add( new RelationalEntityNameToNameInSourceMatcher() );
        this.standardMatchers.add( new RelationalEntityNameInSourceToNameMatcher() );
        this.standardMatchers.add( new RelationalEntityNameToNameIgnoreCaseMatcher() );
        this.standardMatchers.add( new RelationalEntityNameInSourceToNameInSourceIgnoreCaseMatcher() );
        this.standardMatchers.add( new RelationalEntityNameToNameInSourceIgnoreCaseMatcher() );
        this.standardMatchers.add( new RelationalEntityNameInSourceToNameIgnoreCaseMatcher() );
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        // Relational objects can be roots, so return the matchers 
        return this.standardMatchers;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !RelationalPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }
        
        return this.standardMatchers;
    }

}

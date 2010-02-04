/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;
import com.metamatrix.modeler.jdbc.JdbcPackage;

/**
 * UuidMatcherFactory
 */
public class JdbcMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;

    /**
     * Construct an instance of UuidMatcherFactory.
     * 
     */
    public JdbcMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new JdbcMatcher() );
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        return this.standardMatchers;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Core metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !JdbcPackage.eINSTANCE.equals(metamodel) ) {
            return Collections.EMPTY_LIST;
        }
        
        return this.standardMatchers;
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.teiid.designer.core.compare.EObjectMatcherFactory;
import org.teiid.designer.jdbc.JdbcPackage;


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
     * @see org.teiid.designer.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    @Override
	public List createEObjectMatchersForRoots() {
        return this.standardMatchers;
    }

    /**
     * @see org.teiid.designer.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    @Override
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

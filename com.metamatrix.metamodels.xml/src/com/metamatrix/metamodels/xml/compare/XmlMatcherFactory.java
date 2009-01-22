/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;

/** 
 * XmlMatcherFactory
 */
public class XmlMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;

    /**
     * Construct an instance of XmlMatcherFactory.
     */
    public XmlMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new XmlPathInDocToPathInDocMatcher() );
        this.standardMatchers.add( new XmlXPathToXPathMatcher() );
        this.standardMatchers.add( new XmlDocumentNameToNameMatcher() );
        this.standardMatchers.add( new XmlDocumentNameToNameIgnoreCaseMatcher() );        
        this.standardMatchers.add( new XmlPathInDocToPathInDocIgnoreCaseMatcher() );
        this.standardMatchers.add( new XmlXPathToXPathIgnoreCaseMatcher() );
        this.standardMatchers.add( new XmlDocumentNameToNameIgnoreCaseMatcher() );
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
        // Make sure the reference is in the xml metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !XmlDocumentPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the xml metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }

        return this.standardMatchers;
    }

}

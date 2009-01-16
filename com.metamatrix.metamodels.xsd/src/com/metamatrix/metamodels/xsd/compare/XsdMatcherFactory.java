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

package com.metamatrix.metamodels.xsd.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xsd.XSDPackage;

import com.metamatrix.modeler.core.compare.EObjectMatcher;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;


/** 
 * @since 4.2
 */
public class XsdMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;
    private final EObjectMatcher facetMatcher = new XsdFacetToXsdFacetMatcher();

    /**
     * Construct an instance of XmlMatcherFactory.
     */
    public XsdMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        this.standardMatchers.add(new XsdQNameToQNameMatcher());
        this.standardMatchers.add(new XsdQNameToQNameIgnoreCaseMatcher());
        this.standardMatchers.add(new XsdTagNameToTagNameMatcher());
        this.standardMatchers.add(new XsdTagNameToTagNameIgnoreCaseMatcher());
        // XSD objects can be roots, so return the matchers 
        return this.standardMatchers;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the xsd metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !XSDPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the xsd metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }
        
        final int refID = reference.getFeatureID();
        List result = new ArrayList();
        switch (refID) {
            case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__FACET_CONTENTS:
            case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__FUNDAMENTAL_FACETS:
            case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__SYNTHETIC_FACETS: {
                result.add(facetMatcher);
                break;
            }

            default: {
                result = standardMatchers;
                break;
            }
        }
        
        return result;
    }

}

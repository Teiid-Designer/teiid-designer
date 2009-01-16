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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * EcoreMatcherFactory
 */
public class EcoreMatcherFactory implements EObjectMatcherFactory {

    private final EObjectMatcher eAnnotationMatcher                 = new EAnnotationMatcher();
    private final EObjectMatcher eNamedElementMatcher               = new ENamedElementMatcher();

    /**
     * Construct an instance of EcoreMatcherFactory.
     * 
     */
    public EcoreMatcherFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        final List results = new LinkedList();
        results.add(eAnnotationMatcher);
        results.add(eNamedElementMatcher);
        return results;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Check to see whether the reference has a type that is EStringToStringMapEntry
        
        // Create the appropriate matchers ...
        final List results = new LinkedList();
        final EClassifier refType = reference.getEType();
        if ( EcorePackage.eINSTANCE.getEStringToStringMapEntry().equals(refType) ) {
            results.add( new EcoreEStringToStringMapEntryMatcher() );
        }
        if ( refType instanceof EClass ) {
            final EClass eClass = (EClass)refType;
            if ( EcorePackage.eINSTANCE.getEAnnotation().equals(refType) || eClass.isSuperTypeOf(EcorePackage.eINSTANCE.getEAnnotation())  ) {
                results.add(eAnnotationMatcher);
            }
            if ( EcorePackage.eINSTANCE.getEPackage().equals(refType) || eClass.isSuperTypeOf(EcorePackage.eINSTANCE.getEPackage())  ) {
                results.add(eNamedElementMatcher);
            }
            if ( EcorePackage.eINSTANCE.getEAttribute().equals(refType) || eClass.isSuperTypeOf(EcorePackage.eINSTANCE.getEAttribute())  ) {
                results.add(eNamedElementMatcher);
            }
            if ( EcorePackage.eINSTANCE.getEClass().equals(refType) || eClass.isSuperTypeOf(EcorePackage.eINSTANCE.getEClass())  ) {
                results.add(eNamedElementMatcher);
            }
            if ( EcorePackage.eINSTANCE.getENamedElement().equals(refType) || EcorePackage.eINSTANCE.getEClass().isSuperTypeOf(eClass) ) {
                results.add(eNamedElementMatcher);
            }
        }

        if ( EcorePackage.eINSTANCE.getEAnnotation_Contents().equals(reference) ) {
            results.add(new EAnnotationContentsMatcher());
        }

        return results;
    }

}

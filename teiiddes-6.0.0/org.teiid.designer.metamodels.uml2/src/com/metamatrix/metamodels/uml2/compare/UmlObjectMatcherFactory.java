/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.uml2.uml.UMLPackage;

import com.metamatrix.modeler.core.compare.EObjectMatcher;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;

/**
 * UmlObjectMatcherFactory
 */
public class UmlObjectMatcherFactory implements EObjectMatcherFactory {

    private final EObjectMatcher namedElementMatcher                = new UmlNamedElementMatcher();
    private final EObjectMatcher namedElementCaseInsensitiveMatcher = new UmlNamedElementCaseInsensitiveMatcher();
    private final EObjectMatcher unNamedPropertyMatcher             = new UmlUnNamedPropertyMatcher();
    private final EObjectMatcher unNamedAssociationMatcher          = new UmlUnNamedAssociationMatcher();
    private final EObjectMatcher unNamedClassifierMatcher           = new UmlUnNamedClassifierMatcher();
    private final EObjectMatcher packageImportMatcher               = new UmlPackageImportMatcher();
    private final EObjectMatcher elementImportMatcher               = new UmlElementImportMatcher();
    private final EObjectMatcher valueSpecificationMatcher          = new UmlValueSpecificationMatcher();
    private final EObjectMatcher unNamedGeneralizationMatcher       = new UmlUnNamedGeneralizationMatcher();

    /**
     * Construct an instance of UmlObjectMatcherFactory.
     * 
     */
    public UmlObjectMatcherFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        final List result = new ArrayList();
        result.add(namedElementMatcher);
        result.add(namedElementCaseInsensitiveMatcher);
        result.add(unNamedPropertyMatcher);
        result.add(unNamedAssociationMatcher);
        result.add(unNamedClassifierMatcher);
        result.add(packageImportMatcher);
        result.add(elementImportMatcher);
        result.add(valueSpecificationMatcher);
        result.add(unNamedGeneralizationMatcher);
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !UMLPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }
        
        final List result = new ArrayList(10);
        final EClassifier type = reference.getEType();
        if ( type instanceof EClass ) {
            final EClass eClass = (EClass)type;
            if ( UMLPackage.eINSTANCE.getNamedElement().equals(type) || UMLPackage.eINSTANCE.getNamedElement().isSuperTypeOf(eClass) ) {
                result.add(namedElementMatcher);
                result.add(namedElementCaseInsensitiveMatcher);
            }
            if ( UMLPackage.eINSTANCE.getProperty().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getProperty()) ) {
                result.add(unNamedPropertyMatcher);
            }
            if ( UMLPackage.eINSTANCE.getClassifier().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getClassifier()) ) {
                result.add(unNamedClassifierMatcher);
            }
            if ( UMLPackage.eINSTANCE.getAssociation().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getAssociation()) ) {
                result.add(unNamedAssociationMatcher);
            }
            if ( UMLPackage.eINSTANCE.getElementImport().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getElementImport()) ) {
                result.add(elementImportMatcher);
            }
            if ( UMLPackage.eINSTANCE.getPackageImport().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getPackageImport()) ) {
                result.add(packageImportMatcher);
            }
            if ( UMLPackage.eINSTANCE.getValueSpecification().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getValueSpecification()) ) {
                result.add(valueSpecificationMatcher);
            }
            if( UMLPackage.eINSTANCE.getGeneralization().equals(type) || eClass.isSuperTypeOf(UMLPackage.eINSTANCE.getGeneralization()) )  {
                result.add(unNamedGeneralizationMatcher);
            }
        }
        
        return result;
    }

}

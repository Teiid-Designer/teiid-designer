/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.validation.rules;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLPackage;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * This is a name rule designed specifically for UML2 entities.
 */
public class Uml2StringNameRule implements
                               StructuralFeatureValidationRule {

    public void validate(final EStructuralFeature eStructuralFeature,
                         final EObject eObject,
                         final Object value,
                         final ValidationContext context) {
        /*
         * if passed in eobject is null, nothing to validate.
         */
        if (eObject == null) {
            return;
        }

        final String objType = eObject.eClass().getName();
        if (!context.hasRunRule(eObject, getRuleName() + objType)) {

            
            /*
             * check to see if the eStructural feature is null.
             */
            if (eStructuralFeature == null) {
                return;
            }

            /*
             * check if the feature matches the given feature
             */
            if (eStructuralFeature.getFeatureID() != UMLPackage.NAMED_ELEMENT__NAME) {
                return;
            }

            /*
             * if this eObject is not a named element, there is no reason to check to see if its name is valid.
             */
            if (!(eObject instanceof NamedElement)) {
                return;
            }
            
            final ValidationResult result = new ValidationResultImpl(eObject); 
            
            /*
             * if the name is 'null' we add a validation message to the context and return, no more validation to be done for this
             * 'null' feature value as its value is inherently not acceptable.
             */
            if (value == null) {

                /*
                 * create validation problem and add it to the results
                 */
                ValidationProblem problem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      ModelerCore.Util
                                                                                      .getString("StringNameRule.The_entity_name_may_not_be_null._1")); //$NON-NLS-1$
                result.addProblem(problem);

                return;

            }

            /*
             * validate the name
             */
            final String name = (String)value;

            /*
             * check that there are no invalid characters in the name. There are no 'special' invalid characters for UML2 entities
             * in general. The standard routine of checking for non-alpha/special characters is still run in the
             * validateStringNameChars method.
             */
            CoreValidationRulesUtil.validateStringNameChars(result, name, new char[0], IStatus.ERROR);

            /*
             * add the result to the context
             */
            context.addResult(result);

            final List siblings = getSiblingsOfSameType(eObject);
            
            /*
             * get delegates for proxys for performance the uniqueness rule should only be run once per container
             */
            CoreValidationRulesUtil.validateUniqueness(context, new StringNameValidator(true), siblings, UMLPackage.NAMED_ELEMENT__NAME);

            /*
             * set the rule has been run.
             */
            context.recordRuleRun(eObject, getRuleName() + objType);
        }

    }

    /**
     * This method will get only same-typed siblings.
     *  
     * @param eObject eObject to get siblings of
     * @return List of qualified siblings
     * @since 4.3
     */
    private List getSiblingsOfSameType(final EObject eObject) {

        final List qualifiedSiblings = new LinkedList();

        List list = null;
        final EObject parent = eObject.eContainer();
        if (parent == null) {
            final Resource resource = eObject.eResource();
            list = resource.getContents();
        } else {
            list = parent.eContents();
        }
        final Iterator iter = list.iterator();
        while (iter.hasNext()) {
            final Object object = iter.next();

            if (object instanceof EObject) {
                final EObject siblingEObject = (EObject)object;
                if (eObject.eClass() == siblingEObject.eClass()) {
                    qualifiedSiblings.add(siblingEObject);
                }
            }

        }

        return qualifiedSiblings;
    }

    private String getRuleName() {
        return this.getClass().getName();
    }

}

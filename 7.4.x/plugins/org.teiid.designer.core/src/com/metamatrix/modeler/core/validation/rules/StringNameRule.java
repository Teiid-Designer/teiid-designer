/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * StringNameRule, rule that validates the string name
 */
public class StringNameRule implements StructuralFeatureValidationRule {

    // maximum allowed length for the entity
    private char[] validChars;

    // id of the feature being validated
    private final int featureID;

    /**
     * Construct an instance of StringLengthRule.
     * 
     * @param maxStringLength Maximum length allowed for the entity
     * @param featureID ID of the feature to validate
     */
    public StringNameRule( char[] validChars,
                           int featureID ) {
        CoreArgCheck.isNotNull(validChars);
        this.validChars = validChars;
        this.featureID = featureID;
    }

    /**
     * Construct an instance of StringLengthRule.
     * 
     * @param maxStringLength Maximum length allowed for the entity
     * @param featureID ID of the feature to validate
     */
    public StringNameRule( int featureID ) {
        this.featureID = featureID;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ValidationRule#validate(java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate( EStructuralFeature eStructuralFeature,
                          EObject eObject,
                          Object value,
                          ValidationContext context ) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != this.featureID) {
            return;
        }

        // Check that the value is an instance of java.lang.String
        // otherwise we cannot apply this rule
        if (!(value instanceof String)) {
            return;
        }

        // validate the name
        final String name = (String)value;
        ValidationResult result = new ValidationResultImpl(eObject);
        if (validateCharacters()) {
            CoreValidationRulesUtil.validateStringNameChars(result, name, this.validChars, getInvalidCharactersSeverityCode());
        }
        // add the result to the context
        context.addResult(result);

        // type of object this rule is being run on.
        String objType = eObject.eClass().getName();
        // this rule is being run once per object type per parent
        if (!context.hasRunRule(eObject, getRuleName() + objType)) {
            if (validateUniqueness()) {
                List siblings = getSiblingsForUniquenessCheck(eObject);
                // get delegates for proxys for performance
                // the uniqueness rule should only be run once per container
                CoreValidationRulesUtil.validateUniqueness(context, siblings, this.featureID);
                // set the rule has been run
                context.recordRuleRun(eObject, getRuleName() + objType);
            }
        }
    }

    protected String getRuleName() {
        return this.getClass().getName();
    }

    protected boolean validateUniqueness() {
        return true;
    }

    protected boolean validateCharacters() {
        return true;
    }

    protected int getInvalidCharactersSeverityCode() {
        return IStatus.ERROR;
    }
    
    /**
     * @return Returns the featureID.
     * @since 4.2
     */
    protected int getFeatureID() {
        return this.featureID;
    }

    /**
     * Obtain those siblings of the supplied object that are to be considered the domain of objects for the uniqueness check.
     * 
     * @param eObject the object whose name is to be checked for uniqueness amongst its siblings
     * @return the siblings that should be used to check for uniqueness in the name; never null
     */
    protected List getSiblingsForUniquenessCheck( final EObject eObject ) {
        final EObject parent = eObject.eContainer();
        if (parent != null) {
            // eObject is not a parent ...
            return parent.eContents();
        }
        // eObject is one of the roots ...
        final Resource resource = eObject.eResource();
        if (resource != null) {
            return resource.getContents();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @param b
     */
    public void setNameCanBeNull( boolean b ) {
    }

}

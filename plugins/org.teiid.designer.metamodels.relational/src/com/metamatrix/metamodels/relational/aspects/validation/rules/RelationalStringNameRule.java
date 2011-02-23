/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * RelationalStringNameRule
 */
public class RelationalStringNameRule extends StringNameRule {

    /**
     * Construct an instance of RelationalStringNameRule.
     * 
     * @param invalidChars
     * @param featureID
     */
    public RelationalStringNameRule( char[] invalidChars, // NO_UCD
                                     int featureID ) { // NO_UCD
        super(invalidChars, featureID);
    }

    /**
     * Construct an instance of RelationalStringNameRule.
     * 
     * @param featureID
     */
    public RelationalStringNameRule( int featureID ) {
        super(featureID);
    }

    /**
     * This method groups siblings into the following domains, and chooses only those siblings that are in the same domain as the
     * supplied object.
     * <ul>
     * <li>{@link Catalog} and {@link Schema} instances</li>
     * <li>{@link BaseTable} and {@link View} instances</li>
     * <li>{@link Procedure} instances</li>
     * <li>{@link ProcedureColumn} instances</li>
     * <li>{@link AccessPattern} instances</li>
     * <li>{@link UniqueKey}, {@link ForeignKey} and {@link Index} instances</li>
     * <li></li>
     * <li></li>
     * </ul>
     * 
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public List getSiblingsForUniquenessCheck( final EObject eObject ) {
        Object parent = eObject.eContainer();
        if (parent == null) {
            parent = eObject.eResource();
        }
        if (eObject instanceof Table) {
            return RelationalUtil.findTables(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof Procedure) {
            return RelationalUtil.findProcedures(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof ProcedureParameter) {
            return RelationalUtil.findProcedureParameters(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof Index) {
            return RelationalUtil.findIndexes(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof ForeignKey || eObject instanceof UniqueKey) {
            return RelationalUtil.findKeys(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof Column) {
            return RelationalUtil.findColumns(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof AccessPattern) {
            if (parent instanceof Table) {
                return ((Table)parent).getAccessPatterns();
            }
            return new LinkedList();
        } else if (eObject instanceof LogicalRelationship) {
            return RelationalUtil.findLogicalRelationships(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof Schema) {
            return RelationalUtil.findSchemas(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof Catalog) {
            return RelationalUtil.findCatalogs(parent, ModelVisitorProcessor.DEPTH_ONE);
        }
        return super.getSiblingsForUniquenessCheck(eObject);
    }

    /**
     * This method overrides super method to intercept the StringNameValidator() with a Relational version. This allows
     * relaxing the valid character set.
     * 
     * 
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validate(EStructuralFeature, EObject, Object, ValidationContext)
     */
    @Override
    public void validate( EStructuralFeature eStructuralFeature,
                          EObject eObject,
                          Object value,
                          ValidationContext context ) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != getFeatureID()) {
            return;
        }

        // Check that the value is an instance of java.lang.String
        // otherwise we cannot apply this rule
        if (!(value instanceof String)) {
            return;
        }
        
	    final int status = getPreferenceStatus(context);
	    
	    boolean restrictChars = false;
        if (status != IStatus.OK ) {
            restrictChars = true;
        }

        // validate the name
        final String name = (String)value;
        ValidationResult result = new ValidationResultImpl(eObject);

        if (validateCharacters()) {

        	RelationalStringNameValidator validator = new RelationalStringNameValidator(getInvalidChars(), eObject instanceof Table, restrictChars);
        	
            CoreValidationRulesUtil.validateStringNameChars(result, name, validator, status);
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
                CoreValidationRulesUtil.validateUniqueness(context, siblings, getFeatureID());
                // set the rule has been run
                context.recordRuleRun(eObject, getRuleName() + objType);
            }
        }
    }
    
    protected int getPreferenceStatus(final ValidationContext context) {
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_NAME_CHARACTER_RESTRICTION, IStatus.OK);
    }
}


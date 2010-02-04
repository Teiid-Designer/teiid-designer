/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * SiblingNameInSourceRule
 */
public class SiblingNameInSourceRule implements ObjectValidationRule {

	private static final String nameInSourceFeatureName = RelationalPlugin.Util.getString("_UI_RelationalEntity_nameInSource_feature"); //$NON-NLS-1$
    private static final String RULE_NAME = SiblingNameInSourceRule.class.getName();

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final EObject eObject, final ValidationContext context) {
    	
        // nothing to validate if there are no siblings
        if(!shouldRun(eObject, context)) {
        	return;	
        }

		// get siblings to validate
		List siblings = getSiblingsForUniquenessCheck(eObject);
		if(siblings.isEmpty()) {        	
			return;
		}

	    final int statusForCaseSensitive = getPreferenceStatus(context);
        final int statusForCaseInsensitive = statusForCaseSensitive == IStatus.ERROR ? IStatus.WARNING : statusForCaseSensitive; 

        ValidationResults validationResults = new ValidationResults(siblings.size());
        for(int i=0; i < siblings.size(); i++) {
            EObject siblingI = (EObject)siblings.get(i);
            String nameInSourceA = getNameInSource(siblingI);
            final ValidationResult result = validationResults.get(i,siblingI);

            if(!StringUtil.isEmpty(nameInSourceA)) {
                for(int j=i+1; j < siblings.size(); j++) {
                    EObject siblingJ = (EObject)siblings.get(j);
                    String nameInSourceB = getNameInSource(siblings.get(j));
                    if(!StringUtil.isEmpty(nameInSourceB)) {                    
                        if(nameInSourceA.equals(nameInSourceB)) {
                            // create validation problem and add it to the result
                            final Object[] params = new Object[]{getName(siblingI),getName(siblingJ),nameInSourceFeatureName};
                            final String msg = RelationalPlugin.Util.getString("SiblingNameInSourceRule.failure_message_case_sensitive",params); //$NON-NLS-1$
                            ValidationProblem problem  = new ValidationProblemImpl(0, statusForCaseSensitive,msg);
                            problem.setHasPreference(context.hasPreferences());
                            result.addProblem(problem);
                            // creating additinal validation results on other sibling
                            // note this would create markers with duplicate messages but targets 
                            // will be differrent
                            final ValidationResult resultJ = validationResults.get(j,siblingJ);
                            ValidationProblem problemJ  = new ValidationProblemImpl(0, statusForCaseSensitive,msg);
                            problemJ.setHasPreference(context.hasPreferences());                            
                            resultJ.addProblem(problemJ);                            
                        } else if(nameInSourceA.equalsIgnoreCase(nameInSourceB)) {
                            // create validation problem and add it to the result
                            final Object[] params = new Object[]{getName(siblingI),getName(siblingJ),nameInSourceFeatureName};
                            final String msg = RelationalPlugin.Util.getString("SiblingNameInSourceRule.failure_message_case_insensitive",params); //$NON-NLS-1$
                            ValidationProblem problem  = new ValidationProblemImpl(0, statusForCaseInsensitive ,msg);
                            problem.setHasPreference(context.hasPreferences());
                            result.addProblem(problem);
                            // creating additinal validation results on other sibling
                            // note this would create markers with duplicate messages but targets 
                            // will be differrent
                            final ValidationResult resultJ = validationResults.get(j,siblingJ);
                            ValidationProblem problemJ  = new ValidationProblemImpl(0, statusForCaseInsensitive,msg);
                            problemJ.setHasPreference(context.hasPreferences());                                                        
                            resultJ.addProblem(problemJ);                            
                        }
                    }
                }
            }
			// add the result to the context
			context.addResult(result);          
        }
    }

	//############################################################################################################################
	//# 								P R O T E C T E D  M E T H O D S                                       					 #
	//############################################################################################################################

    protected class ValidationResults {
        private final ValidationResult[] results;
        public ValidationResults(final int size) {
            this.results = new ValidationResult[size];
        }
        public ValidationResult get(final int index, final EObject object ) {
            ValidationResult result = this.results[index];
            if ( result == null ) {
                result = new ValidationResultImpl(object);
                this.results[index] = result;
            }
            return result;
        }
    }

    protected int getPreferenceStatus(final ValidationContext context) {
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_SIBLING_NAME_IN_SOURCE, IStatus.OK);
    }

	/**
	 * This method groups siblings into the following domains, and chooses only those siblings that are in
	 * the same domain as the supplied object.
	 * <ul>
	 *  <li>{@link Catalog} and {@link Schema} instances</li>
	 *  <li>{@link BaseTable} and {@link View} instances</li>
	 *  <li>{@link Procedure} instances</li>
	 *  <li>{@link ProcedureColumn} instances</li>
	 *  <li>{@link AccessPattern} instances</li>
	 *  <li>{@link UniqueKey}, {@link ForeignKey} and {@link Index} instances</li>
	 *  <li></li>
	 *  <li></li>
	 * </ul>
	 * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
	 */
	protected List getSiblingsForUniquenessCheck(final EObject eObject) {
		Object parent = eObject.eContainer();
		if ( parent == null ) {
			parent = eObject.eResource();
		}
		if ( eObject instanceof Table ) {
			return RelationalUtil.findTables(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof Procedure ) {
			return RelationalUtil.findProcedures(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof ProcedureParameter ) {
			return RelationalUtil.findProcedureParameters(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof Column ) {
			return RelationalUtil.findColumns(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof Index ) {
			return RelationalUtil.findIndexes(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof ForeignKey || eObject instanceof UniqueKey ) {
			return RelationalUtil.findKeys(parent, ModelVisitorProcessor.DEPTH_ONE);
		} else if ( eObject instanceof AccessPattern ) {
			if ( parent instanceof Table ) {
				return ((Table)parent).getAccessPatterns();
			}
		} else if ( eObject instanceof LogicalRelationship ) {
			return RelationalUtil.findLogicalRelationships(parent, ModelVisitorProcessor.DEPTH_ONE);
		}
		return Collections.EMPTY_LIST;
	}

	//############################################################################################################################
	//# 								P R I V A T E  M E T H O D S                                       					 	 #
	//############################################################################################################################

    private String getNameInSource(final Object sibling) {
        if(sibling instanceof RelationalEntity) {
            RelationalEntity entity = (RelationalEntity) sibling;
            return entity.getNameInSource();
        }
        return null;
    }

    private String getName(final Object sibling) {
        if(sibling instanceof RelationalEntity) {
            RelationalEntity entity = (RelationalEntity) sibling;
            return entity.getName();
        }
        return ModelerCore.getModelEditor().getModelRelativePathIncludingModel((EObject) sibling).toString();
    }

	private boolean shouldRun(final EObject eObject, final ValidationContext context) {
		if(getPreferenceStatus(context) == IStatus.OK) {
			return false;
		}

		// run the rule only for physical models
		try {
			ModelAnnotation modelAnnotation = ModelerCore.getModelEditor().getModelAnnotation(eObject);
			int modelType = modelAnnotation.getModelType().getValue(); 
			if(modelType != ModelType.PHYSICAL) {
				return false;
			}
		} catch(Exception e) {
			RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
		}

		// type of object this rule is being run on.
		String objType = eObject.eClass().getName();        
		// this rule is being run once per object type per parent
		if(context.hasRunRule(eObject, RULE_NAME+objType)) {
			return false;
		}
		context.recordRuleRun(eObject, RULE_NAME+objType);			

		return true;    
	}

}

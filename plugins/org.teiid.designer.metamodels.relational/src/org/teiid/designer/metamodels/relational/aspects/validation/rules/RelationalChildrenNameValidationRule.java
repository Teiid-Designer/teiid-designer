/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;

/**
 * This rule is required to check sibling names of different metamodel types for uniqueness.
 * 
 * Example is an INDEX and a TABLE with same name. This may cause issues when exporting both to DDL and attempting to
 * execute that DDL in a DB.
 */
public class RelationalChildrenNameValidationRule implements ObjectValidationRule {

	private static final String nameFeatureName = RelationalPlugin.Util.getString("_UI_RelationalEntity_name_feature"); //$NON-NLS-1$
    private static final String RULE_NAME = RelationalChildrenNameValidationRule.class.getName();

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(final EObject eObject, final ValidationContext context) {
    	
        // nothing to validate if there are no siblings
        if(!shouldRun(eObject, context)) {
        	return;	
        }

		// get siblings to validate
		List siblings = getSiblingsForUniqueNameCheck(eObject);
		if(siblings.isEmpty()) {        	
			return;
		}

	    final int statusForCaseSensitive = getPreferenceStatus(context);
        final int statusForCaseInsensitive = statusForCaseSensitive == IStatus.ERROR ? IStatus.WARNING : statusForCaseSensitive; 

        ValidationResults validationResults = new ValidationResults(siblings.size());
        for(int i=0; i < siblings.size(); i++) {
            EObject siblingI = (EObject)siblings.get(i);
            String nameA = getName(siblingI);
            final ValidationResult result = validationResults.get(i,siblingI);

            if(!CoreStringUtil.isEmpty(nameA)) {
                for(int j=i+1; j < siblings.size(); j++) {
                    EObject siblingJ = (EObject)siblings.get(j);
                    String nameB = getName(siblings.get(j));
                    if(!CoreStringUtil.isEmpty(nameB)) {                    
                        if(nameA.equals(nameB)) {
                            // create validation problem and add it to the result
                            final Object[] params = new Object[]{nameA,nameB,nameFeatureName};
                            final String msg = RelationalPlugin.Util.getString("RelationalChildrenNameValidationRule.failure_message_case_sensitive",params); //$NON-NLS-1$
                            ValidationProblem problem  = new ValidationProblemImpl(0, statusForCaseSensitive,msg);
                            problem.setHasPreference(context.hasPreferences());
                            result.addProblem(problem);
                            // creating additional validation results on other sibling
                            // note this would create markers with duplicate messages but targets 
                            // will be different
                            final ValidationResult resultJ = validationResults.get(j,siblingJ);
                            ValidationProblem problemJ  = new ValidationProblemImpl(0, statusForCaseSensitive,msg);
                            problemJ.setHasPreference(context.hasPreferences());                            
                            resultJ.addProblem(problemJ);                            
                        } else if(nameA.equalsIgnoreCase(nameB)) {
                            // create validation problem and add it to the result
                            final Object[] params = new Object[]{nameA,nameB,nameFeatureName};
                            final String msg = RelationalPlugin.Util.getString("RelationalChildrenNameValidationRule.failure_message_case_insensitive",params); //$NON-NLS-1$
                            ValidationProblem problem  = new ValidationProblemImpl(0, statusForCaseInsensitive ,msg);
                            problem.setHasPreference(context.hasPreferences());
                            result.addProblem(problem);
                            // creating additional validation results on other sibling
                            // note this would create markers with duplicate messages but targets 
                            // will be different
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
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_SIBLING_NAME, IStatus.OK);
    }

	/**
	 * This method groups siblings into the following domains, and chooses only those siblings that are in
	 * the same domain as the supplied object.
	 * <ul>
	 *  <li>{@link Table} and {@link View} instances</li>
	 *  <li>{@link Procedure} instances</li>
	 *  <li>{@link Index} instances</li>
	 *  <li></li>
	 *  <li></li>
	 * </ul>
	 * @see org.teiid.designer.core.validation.rules.StringNameRule#getSiblingsForUniqueNameCheck(org.eclipse.emf.ecore.EObject)
	 */
	protected List getSiblingsForUniqueNameCheck(final EObject eObject) {
		Object parent = eObject.eContainer();
		if ( parent == null ) {
			parent = eObject.eResource();
		}

		if ( eObject instanceof Table ||
			 eObject instanceof Procedure ||
			 eObject instanceof Index ) {
			List siblings = new ArrayList();
			siblings.addAll(RelationalUtil.findTables(parent, ModelVisitorProcessor.DEPTH_ONE));
			siblings.addAll(RelationalUtil.findProcedures(parent, ModelVisitorProcessor.DEPTH_ONE));
			siblings.addAll(RelationalUtil.findIndexes(parent, ModelVisitorProcessor.DEPTH_ONE));
			return siblings;
		}
		return Collections.EMPTY_LIST;
	}

	//############################################################################################################################
	//# 								P R I V A T E  M E T H O D S                                       					 	 #
	//############################################################################################################################

//    private String getNameInSource(final Object sibling) {
//        if(sibling instanceof RelationalEntity) {
//            RelationalEntity entity = (RelationalEntity) sibling;
//            return entity.getNameInSource();
//        }
//        return null;
//    }

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

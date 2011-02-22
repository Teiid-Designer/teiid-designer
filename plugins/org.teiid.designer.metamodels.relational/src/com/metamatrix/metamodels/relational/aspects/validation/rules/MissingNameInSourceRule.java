package com.metamatrix.metamodels.relational.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

public class MissingNameInSourceRule implements ObjectValidationRule {

	private static final String nameInSourceFeatureName = RelationalPlugin.Util.getString("_UI_RelationalEntity_nameInSource_feature"); //$NON-NLS-1$
    private static final String RULE_NAME = MissingNameInSourceRule.class.getName();

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final EObject eObject, final ValidationContext context) {
    	
        // nothing to validate if there are no siblings
        if(!shouldRun(eObject, context)) {
        	return;	
        }

	    final int status = getPreferenceStatus(context);

        if (status == IStatus.OK) {
            return;
        }
        
        String nameInSourceA = getNameInSource(eObject);

        if(CoreStringUtil.isEmpty(nameInSourceA)) {

        	final ValidationResult result = new ValidationResultImpl(eObject);
            // create validation problem and add it to the result
        	final Object[] params = new Object[]{getName(eObject),nameInSourceFeatureName};
            final String msg = RelationalPlugin.Util.getString("MissingNameInSourceRule.warning_message", params); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, status, msg);
            problem.setHasPreference(context.hasPreferences());
            result.addProblem(problem);
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
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_TABLE_MISSING_NAME_IN_SOURCE, IStatus.OK);
    }
    
    private String getName(final Object sibling) {
        if(sibling instanceof RelationalEntity) {
            RelationalEntity entity = (RelationalEntity) sibling;
            return entity.getName();
        }
        return ModelerCore.getModelEditor().getModelRelativePathIncludingModel((EObject) sibling).toString();
    }
    
    private String getNameInSource(final Object sibling) {
        if(sibling instanceof RelationalEntity) {
            RelationalEntity entity = (RelationalEntity) sibling;
            return entity.getNameInSource();
        }
        return null;
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

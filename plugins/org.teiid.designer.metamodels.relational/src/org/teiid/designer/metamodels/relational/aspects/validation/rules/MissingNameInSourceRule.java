package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


public class MissingNameInSourceRule implements ObjectValidationRule {

	private static final String nameInSourceFeatureName = RelationalPlugin.Util.getString("_UI_RelationalEntity_nameInSource_feature"); //$NON-NLS-1$

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
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

		return true;    
	}

}

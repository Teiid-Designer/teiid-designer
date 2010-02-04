/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/**
 * @since 4.2
 */
public class EObjectUuidRule implements ObjectValidationRule {

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isNotNull(context);
        ArgCheck.isNotNull(eObject);

        // get uniqueness preference status
        final int status = context.getPreferenceStatus(ValidationPreferences.EOBJECT_UUID_UNIQUENESS, IStatus.ERROR);
	    if (status == IStatus.OK) {
	        return;
	    }

        // if the eObject is a model annotation then
        // is already being validation by ModelAnnoattionUuidRule
        if(eObject instanceof ModelAnnotation) {
            return;
        }

        final ModelEditor editor = ModelerCore.getModelEditor();
        final String uuidString = ModelerCore.getObjectIdString(eObject);
		if (uuidString == null) {
            final String pathInModel = editor.getModelRelativePathIncludingModel(eObject).toString();
			final ValidationResult result = new ValidationResultImpl(eObject);
			// create validation problem and add it to the result
			final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, ModelerCore.Util.getString("EObjectUuidRule.0", pathInModel)); //$NON-NLS-1$
			problem.setHasPreference(context.hasPreferences());
			result.addProblem(problem);
			context.addResult(result);
        } else {
	        boolean isDuplicate = context.containsUuid(uuidString);
	        if(isDuplicate) {
	            final String pathInModel = editor.getModelRelativePathIncludingModel(eObject).toString();
				final ValidationResult result = new ValidationResultImpl(eObject);
				String modelName = editor.getModelName(eObject);
				// create validation problem and addit to the result
				final ValidationProblem problem  = new ValidationProblemImpl(0, status, ModelerCore.Util.getString("EObjectUuidRule.1", pathInModel, uuidString, modelName)); //$NON-NLS-1$
				problem.setHasPreference(context.hasPreferences());
				result.addProblem(problem);
				context.addResult(result);
	        } else {
	            context.addUuidToContext(uuidString);
	        }
        }
    }
}

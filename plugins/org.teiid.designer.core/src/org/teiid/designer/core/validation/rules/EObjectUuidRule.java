/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;



/**
 * @since 4.2
 */
public class EObjectUuidRule implements ObjectValidationRule {

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isNotNull(context);
        CoreArgCheck.isNotNull(eObject);

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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/**
 * @since 4.2
 */
public class ModelAnnotationUuidRule implements ObjectValidationRule {


    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isNotNull(context);
        ArgCheck.isInstanceOf(ModelAnnotation.class, eObject);

        final ModelEditor editor = ModelerCore.getModelEditor();
        String uuidString = ModelerCore.getObjectIdString(eObject);
		if (uuidString == null) {
            final String pathInModel = editor.getModelRelativePathIncludingModel(eObject).toString();
			final ValidationResult result = new ValidationResultImpl(eObject);
			// create validation problem and add it to the result
			final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, ModelerCore.Util.getString("ModelAnnotationUuidRule.0", pathInModel)); //$NON-NLS-1$
			result.addProblem(problem);
			context.addResult(result);
        } else {
	        boolean isDuplicate = context.containsUuid(uuidString);
	        if(isDuplicate) {
	            final String pathInModel = editor.getModelRelativePathIncludingModel(eObject).toString();
				final ValidationResult result = new ValidationResultImpl(eObject);
				String modelName = editor.getModelName(eObject);
				// create validation problem and addit to the result
				final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, ModelerCore.Util.getString("ModelAnnotationUuidRule.1", pathInModel, uuidString, modelName)); //$NON-NLS-1$
				result.addProblem(problem);
				context.addResult(result);
	        } else {
	            context.addUuidToContext(uuidString);
	        }
        }
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ResourceAnnotationHelper;
import org.teiid.designer.metamodels.core.ModelAnnotation;



/**
 * @since 8.0
 */
public class ModelAnnotationUuidRule implements ObjectValidationRule {


    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isNotNull(context);
        CoreArgCheck.isInstanceOf(ModelAnnotation.class, eObject);

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
		
		// Add rule to check for "ENDPOINT" connection property?
		
		ResourceAnnotationHelper helper = new ResourceAnnotationHelper();
		
		try {
			ModelResource mr = ModelUtil.getModel(eObject);
			Properties modelProps = helper.getProperties(mr, "connection:");
			for( Object key : modelProps.keySet() ) {
				String keyStr = (String)key;
				if( keyStr.equalsIgnoreCase("connection:URL") || keyStr.equalsIgnoreCase("connection:FlatFileHomeUrl") ) {
					String value = (String)modelProps.get(key);
					if( value.contains(StringConstants.SPACE) ) {
						final ValidationResult result = new ValidationResultImpl(eObject);
						String modelName = editor.getModelName(eObject);
						// create validation problem and addit to the result
						final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING, ModelerCore.Util.getString("ModelAnnotationUuidRule.2",  value, modelName)); //$NON-NLS-1$
						result.addProblem(problem);
						context.addResult(result);
					}
				}
			}
		} catch (ModelWorkspaceException e) {
			e.printStackTrace();
		}
		
    }

}

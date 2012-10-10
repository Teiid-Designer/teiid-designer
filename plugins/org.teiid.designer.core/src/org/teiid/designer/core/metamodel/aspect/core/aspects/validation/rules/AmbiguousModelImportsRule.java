/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.core.ModelType;


/**
 * AmbiguousModelImportsRule
 *
 * @since 8.0
 */
public class AmbiguousModelImportsRule implements ObjectValidationRule {

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
	public void validate(EObject eObject, ValidationContext context) {
		CoreArgCheck.isInstanceOf(ModelAnnotation.class, eObject);

		final ModelAnnotation modelAnnotation = (ModelAnnotation) eObject;
		
		int modelType = modelAnnotation.getModelType().getValue();
		if (modelType != ModelType.PHYSICAL && modelType != ModelType.VIRTUAL) {
			return;
		}

		// model imports for the given model
		List imports = modelAnnotation.getModelImports();
		for(int i=0; i < imports.size(); i++) {
			ModelImport modelImport1 = (ModelImport) imports.get(i);
            
			String importName1 = modelImport1.getName();
			for (int j=i+1; j < imports.size(); j++) {
				ModelImport modelImport2 = (ModelImport) imports.get(j);
				String importName2 = modelImport2.getName();
                
				if(importName1.equalsIgnoreCase(importName2) && 
                                //MyDefect : 17829 added path check between two model imports to differentiate, if the names are same.
                                modelImport1.getModelLocation().equalsIgnoreCase(modelImport2.getModelLocation()) ) {
					// name of the model importing
					final String modelName = ModelerCore.getModelEditor().getModelName(eObject);					
					String msg = ModelerCore.Util.getString("AmbiguousModelImportsRule.Model_{0}_imports_two_models_of_the_same_name_{1}._1", modelName, importName1); //$NON-NLS-1$
					ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING ,msg);
					ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
					result.addProblem(problem);
					context.addResult(result);
				}
			}
		}
	}

}

/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * AmbiguousModelImportsRule
 */
public class AmbiguousModelImportsRule implements ObjectValidationRule {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	public void validate(EObject eObject, ValidationContext context) {
		ArgCheck.isInstanceOf(ModelAnnotation.class, eObject);

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

package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

public class DeletedXmlRelationalImportRule implements ResourceValidationRule {

	@Override
	public void validate(Resource resource, ValidationContext context) {
		CoreArgCheck.isNotNull(resource);
		CoreArgCheck.isNotNull(context);
		if (ModelUtil.isModelFile(resource)) {
			try {
				if (ModelUtil.isPhysical(resource)) {
					final List imports = ModelUtil.getModel(resource).getModelImports();
					for (final Iterator impIter = imports.iterator(); impIter
							.hasNext();) {
						final ModelImport imp = (ModelImport) impIter.next();
						if (imp.getName().equalsIgnoreCase("XMLFileConnectorExtensions")
								|| imp.getName().equalsIgnoreCase(
										"XMLHttpConnectorExtensions")
								|| imp.getName().equalsIgnoreCase("XMLSOAPConnectorExtensions")) {
							final ValidationResult result = new ValidationResultImpl(resource, context);
							final ValidationProblem problem = new ValidationProblemImpl(0,
									IStatus.ERROR, ModelerCore.Util.getString("DeletedXmlRelationalRule.invalidmodel", new String[]{imp.getName()})); //$NON-NLS-1$);
							result.addProblem(problem);
							context.addResult(result);
						}
					}
				}

			} catch (ModelWorkspaceException e) {
				ValidationResult result = new ValidationResultImpl(resource, resource);
	            ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, e.getLocalizedMessage());
	            result.addProblem(problem);
	            context.addResult(result);
			}
		}
	}

}

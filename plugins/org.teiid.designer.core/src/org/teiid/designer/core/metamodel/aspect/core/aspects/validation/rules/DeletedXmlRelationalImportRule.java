package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ResourceValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelImport;


/**
 * @since 8.0
 */
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
						if (imp.getName().equalsIgnoreCase("XMLFileConnectorExtensions") //$NON-NLS-1$
								|| imp.getName().equalsIgnoreCase(
										"XMLHttpConnectorExtensions") //$NON-NLS-1$
								|| imp.getName().equalsIgnoreCase("XMLSOAPConnectorExtensions")) { //$NON-NLS-1$
							final ValidationResult result = new ValidationResultImpl(resource, context);
							final ValidationProblem problem = new ValidationProblemImpl(0,
									IStatus.ERROR, ModelerCore.Util.getString("DeletedXmlRelationalRule.invalidmodel", new Object[]{imp.getName()})); //$NON-NLS-1$);
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

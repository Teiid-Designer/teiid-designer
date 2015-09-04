package org.teiid.designer.core.validation.rules;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;

public final class CircularDependenciesRule implements ResourceValidationRule {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
    public void validate(final Resource resource,
                         final ValidationContext context) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(context);

        try {
            final ModelResource modelResource = ModelUtil.getModel(resource);

            if (modelResource != null) {
                final IResource model = modelResource.getResource();

                if (model != null) {
                    final IFile circularDependency = WorkspaceResourceFinderUtil.getFirstResourceHavingCircularDependency(model);

                    if (circularDependency != null) {
                        final ValidationResult result = new ValidationResultImpl(resource, resource);
                        final String msg = ModelerCore.Util.getString("CircularDependenciesRule.errorMsg",
                                                                      model.getName(),
                                                                      circularDependency.getName());
                        final ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
                        result.addProblem(problem);
                        context.addResult(result);
                    }
                }
            }
        } catch (final Exception e) {
            final ValidationResult result = new ValidationResultImpl(resource, resource);
            final ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, e.getLocalizedMessage());
            result.addProblem(problem);
            context.addResult(result);
        }
    }

}

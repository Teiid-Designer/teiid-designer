/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.builder.ResourceValidator;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationRuleSetImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * SimpleXsdResourceValidator
 */
public class SimpleXsdResourceValidator implements ResourceValidator {

    private static final ValidationRuleSet RULE_SET = new ValidationRuleSetImpl();
    static {
        RULE_SET.addRule(new XsdResourceValidationRule());
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isValidatorForObject(final Object obj) {
        if (obj instanceof IResource) {
            return ModelUtil.isXsdFile((IResource)obj);
        } else if (obj instanceof XSDResourceImpl) {
            return true;

        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final IProgressMonitor theMonitor,
                         final Object obj,
                         final ValidationContext context) throws ModelerCoreException {

        if ( !isValidatorForObject(obj) ){
            final Object[] params = new Object[] {this.getClass().getName(),(obj != null ? obj.getClass().getName() : null)};
            final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.validator_cannot_be_used_to_validate_the_object",params); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        final IProgressMonitor monitor = (theMonitor != null ? theMonitor : new NullProgressMonitor());

        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;

            final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            final ModelResource mResource = workspace.findModelResource(iResource);

            final Resource eResource = mResource.getEmfResource();
            validate(monitor, eResource, iResource, context);

        } else if (obj instanceof XSDResourceImpl) {
            final XSDResourceImpl eResource = (XSDResourceImpl)obj;
            if (eResource.isLoaded()) {
                try {
                    validateResource(monitor, eResource, context);
                } catch (Throwable e) {
                    final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.Error_validating_resource",eResource.getURI().lastSegment()); //$NON-NLS-1$
                    XsdPlugin.Util.log(IStatus.ERROR,e,msg);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource, org.eclipse.core.resources.IResource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final IProgressMonitor theMonitor, final Resource resource,
                         final IResource iResource, final ValidationContext context) throws ModelerCoreException {

        final IProgressMonitor monitor = (theMonitor != null ? theMonitor : new NullProgressMonitor());

        if( !(resource instanceof EmfResource) ){
            final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.SimpleXsdResourceValidator_may_only_be_used_to_validate_instances_of_EmfResource_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }

        if(iResource == null){
            final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.IResource_may_not_be_null_during_validation_2"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }

        if(!isValidatorForResource(iResource) ){
            final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.Unexpected_IResource_type_encountered_during_EMF_Resource_Validation_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }

        final XSDResourceImpl eResource = (XSDResourceImpl)resource;
        if (eResource.isLoaded()) {
            try {
                validateResource(monitor, eResource, context);
            } catch (Throwable e) {
                final String msg = XsdPlugin.Util.getString("SimpleXsdResourceValidator.Error_validating_resource",eResource.getURI().lastSegment()); //$NON-NLS-1$
                XsdPlugin.Util.log(IStatus.ERROR,e,msg);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#addMarkers(com.metamatrix.modeler.core.validation.ValidationContext, org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    public void addMarkers(final ValidationContext context,
                           final IResource iResource) throws ModelerCoreException {

        if (context != null && context.hasResults()) {
            final List results = context.getValidationResults();
            try {
                for (final Iterator i = results.iterator(); i.hasNext();) {
                    final ValidationResult result = (ValidationResult)i.next();
                    if (result != null && result.hasProblems()) {
                        ValidationProblem[] problems = result.getProblems();
                        String locationPath = result.getLocationPath();
                        String locationUri  = result.getLocationUri();
                        String targetUri    = result.getTargetUri();
                        for (int probCnt=0; probCnt < problems.length; probCnt++) {
                            createProblemMarker(locationPath, locationUri, targetUri, problems[probCnt], iResource);
                        }
                        if(result.isFatalResource()) {
                            return;
                        }
                    }
                }
            } catch (CoreException err) {
                throw new ModelerCoreException(err);
            }
        }
    }

    /**
	 * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForResource(org.eclipse.core.resources.IResource)
	 */
	public boolean isValidatorForResource( final IResource iResource ) {
		return isValidatorForObject(iResource);
	}

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validationStarted(java.util.Collection, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validationStarted(final Collection resources,
                                  final ValidationContext context) {
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validationEnded(com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validationEnded(final ValidationContext context) {
    }

    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================

    /**
     * Create a marker given a validationProblem
     */
    private void createProblemMarker(final String locationPath,
                                     final String locationUri,
                                     final String targetUri,
                                     final ValidationProblem problem,
                                     final IResource resource) throws CoreException {

        IMarker marker = resource.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.LOCATION, locationPath);
        marker.setAttribute(ModelerCore.MARKER_URI_PROPERTY, locationUri);
        marker.setAttribute(ModelerCore.TARGET_MARKER_URI_PROPERTY, targetUri);
        marker.setAttribute(IMarker.MESSAGE, problem.getMessage());

        setMarkerSeverity(marker, problem);
    }

    /**
     * Get the set the severity on the marker given the validation problem.
     */
    private void setMarkerSeverity(final IMarker marker,
                                   final ValidationProblem problem) throws CoreException {

        switch(problem.getSeverity()) {
            case IStatus.ERROR:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                break;
            case IStatus.WARNING:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                break;
            case IStatus.INFO:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                break;
            default:
                return;
        }
    }

    private void validateResource(final IProgressMonitor monitor,
                                  final XSDResourceImpl eResource,
                                  final ValidationContext context) {

        ArgCheck.isNotNull(eResource);
        ArgCheck.isNotNull(context);

        // clear any existing results on the context
        context.clearResults();

        // Apply any resource validation rules
        RULE_SET.validate(monitor, eResource, context);

    }

}

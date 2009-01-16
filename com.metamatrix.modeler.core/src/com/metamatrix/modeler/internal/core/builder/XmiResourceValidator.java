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

package com.metamatrix.modeler.internal.core.builder;

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

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.builder.ResourceValidator;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.Validator;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * XmiResourceValidator
 */
public class XmiResourceValidator implements ResourceValidator {
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isValidatorForObject(final Object obj) {
        if (obj instanceof IResource) {
            return this.isValidatorForResource((IResource)obj);
            
        } else if (obj instanceof EmfResource) {
            return true;
            
        }
        
        return false;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final IProgressMonitor monitor, final Object obj, final ValidationContext context) throws ModelerCoreException {
        
        if(!isValidatorForObject(obj) ){
            final Object[] params = new Object[] {this.getClass().getName(),(obj != null ? obj.getClass().getName() : null)};
            final String msg = ModelerCore.Util.getString("XmiResourceValidator.validator_cannot_be_used_to_validate_the_object",params); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        final IProgressMonitor progressMonitor = (monitor != null ? monitor : new NullProgressMonitor());      
        
        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;
            
            final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            final ModelResource mResource = workspace.findModelResource(iResource);
            if (mResource != null) {
                final Resource eResource = mResource.getEmfResource();
                this.validate(progressMonitor, eResource, iResource, context);
            }
            
        } else if (obj instanceof EmfResource) {
            final EmfResource eResource = (EmfResource)obj;
            if (eResource.isLoaded()) {
                try {
                    Validator.validate(monitor, eResource, context);
                } catch (Throwable e) {
                    final String msg = ModelerCore.Util.getString("ModelBuilder.Error_validating_model_resource_2",eResource); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR,e,msg);
                }
            }
            
        }
    }
    
    /** 
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#addMarkers(com.metamatrix.modeler.core.validation.ValidationContext, org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    public void addMarkers(final ValidationContext context, final IResource iResource) throws ModelerCoreException {
        if (context != null && context.hasResults()) {
            final List results = context.getValidationResults();
            try {
                for (final Iterator iter = results.iterator(); iter.hasNext();) {
                    final ValidationResult result = (ValidationResult)iter.next();
                    if (result != null && result.hasProblems()) {
                        // defect 18922 - not jumping to the right entry in a mapping doc,
                        //  and showing what are apparently duplicates.
                        ValidationProblem[] problems = result.getProblems();
                        String rsltLocationPath = result.getLocationPath();
                        String rsltLocationUri  = result.getLocationUri();
                        String targetUri    = result.getTargetUri();
                        for (int probCnt=0; probCnt < problems.length; probCnt++) {
                            String probLocPath = problems[probCnt].getLocation();
                            if (probLocPath == null) {
                                // problem's location path not specified; use result's:
                                probLocPath = rsltLocationPath;
                            } // endif

                            String probURI = problems[probCnt].getURI();
                            if (probURI == null) {
                                // problem's location URI not specified; use result's:
                                probURI = rsltLocationUri;
                            } // endif
                            
                            createProblemMarker(probLocPath, probURI, targetUri,
                                    problems[probCnt], iResource);
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
    public boolean isValidatorForResource(IResource iResource) {
         return (ModelUtil.isModelFile(iResource) && !ModelUtil.isXsdFile(iResource) );
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource, org.eclipse.core.resources.IResource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final IProgressMonitor progressMonitor, final Resource resource, 
                         final IResource iResource, final ValidationContext context) throws ModelerCoreException {
        
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();        
    
        if( !(resource instanceof EmfResource) ){
            final String msg = ModelerCore.Util.getString("XmiResourceValidator.XmiResourceValidator_may_only_be_used_to_validate_instances_of_EmfResource_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        
        if(iResource == null){
            final String msg = ModelerCore.Util.getString("XmiResourceValidator.IResource_may_not_be_null_during_validation_2"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        
        if(!isValidatorForResource(iResource) ){
            final String msg = ModelerCore.Util.getString("XmiResourceValidator.Unexpected_IResource_type_encountered_during_EMF_Resource_Validation_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        
        final EmfResource emfResource = (EmfResource)resource;
        if (emfResource.isLoaded()) {
            try {
                Validator.validate(monitor, emfResource, context);
            } catch (Throwable e) {
                final String msg = ModelerCore.Util.getString("ModelBuilder.Error_validating_model_resource_2",emfResource); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR,e,msg);
            }
        }
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
    private void createProblemMarker(final String locationPath, final String locationUri, final String targetUri, 
                                     final ValidationProblem problem, final IResource resource) throws CoreException {
                                         
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
    private void setMarkerSeverity(final IMarker marker, final ValidationProblem problem) throws CoreException {
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

}

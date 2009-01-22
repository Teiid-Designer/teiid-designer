/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.vdb.edit.VdbContextValidator;
import com.metamatrix.vdb.edit.VdbContextValidatorResult;
import com.metamatrix.vdb.edit.VdbEditPlugin;


/** 
 * @since 5.0
 */
public class SharedWsVdbContextValidator implements VdbContextValidator {
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    public SharedWsVdbContextValidator() {
        super();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.vdb.edit.VdbContextValidator#validate(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.emf.ecore.resource.Resource[])
     * @since 5.0
     */
    public VdbContextValidatorResult validate(final IProgressMonitor theMonitor, final Resource[] models) {
        ArgCheck.isNotNull(models);
        
        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);
        
        // Set the task name and total work for the progress monitor
        int totalFileCount = models.length;
        String taskName = VdbEditPlugin.Util.getString("SharedWsVdbContextValidator.Validating"); //$NON-NLS-1$
        monitor.beginTask(taskName, totalFileCount);
        
        final VdbContextValidatorResultImpl result = new VdbContextValidatorResultImpl();
        
        // For each org.eclipse.emf.ecore.resource.Resource instance gather all the problem 
        // markers for the corresponding org.eclipse.core.resources.IResource instance
        for (int i = 0; i < models.length; i++) {
            Resource eResource = models[i];
            String name = (eResource.getURI().isFile() ? eResource.getURI().lastSegment() : URI.decode(eResource.getURI().toString()));
            
            try {
                monitor.setTaskName(VdbEditPlugin.Util.getString("SharedWsVdbContextValidator.Validating_model",name)); //$NON-NLS-1$

                // If we are not in Eclipse Runtime environment, there will be no workspace, so do check and continue
                if( ResourcesPlugin.getPlugin() == null ) {
                    continue;
                }
                ModelResource mResource = ModelerCore.getModelWorkspace().findModelResource(eResource);
                if (mResource != null) {
                    IResource iResource = mResource.getResource();
                    
                    IMarker[] markers = iResource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
                    for (int j = 0; j < markers.length; j++) {
                        IMarker marker = markers[j];
                        int severity = IStatus.OK;
                        int markerSeverity = marker.getAttribute(IMarker.SEVERITY, -1);
                        switch(markerSeverity) {
                            case IMarker.SEVERITY_ERROR:
                                severity = IStatus.ERROR;
                                break;
                            case IMarker.SEVERITY_WARNING:
                                severity = IStatus.WARNING;
                                break;
                            case IMarker.SEVERITY_INFO:
                                severity = IStatus.INFO;
                                break;
                            default:
                        }
                        String msg   = (String)marker.getAttribute(IMarker.MESSAGE);
                        Throwable t  = null;
                        result.addProblem(eResource, severity, msg, t);
                    }
                }
                
                monitor.worked(1);
            } catch (Throwable t) {
                String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextValidator.Error_validating_model",name); //$NON-NLS-1$
                result.addProblem(eResource, IStatus.ERROR, msg, t);
            }
        }
        
        return result;
    }

}

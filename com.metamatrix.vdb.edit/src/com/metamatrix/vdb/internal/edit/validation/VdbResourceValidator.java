/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit.validation;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.internal.core.xml.vdb.VdbHeaderReader;
import com.metamatrix.internal.core.xml.vdb.VdbModelInfo;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.builder.ResourceValidator;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.vdb.edit.VdbEditPlugin;

/**
 * VdbResourceValidator
 */
public class VdbResourceValidator implements ResourceValidator {
    
    //============================================================================================================================
    // Constants
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbResourceValidator.class);
    
    public static final int STALE_MODEL_CODE         = 5001;
    public static final int NULL_MANIFEST_MODEL_CODE = 5002;
    public static final int VDB_ARCHIVE_ERROR_CODE   = 5003;
    public static final int VDB_ARCHIVE_WARNING_CODE = 5004;
    public static final int VDB_ARCHIVE_INFO_CODE    = 5005;
    public static final int DUP_UUID_CODE            = 5006;
    
    private static final String DUP_UUID_MSG_ID = I18N_PREFIX + "duplicateUuidMessage"; //$NON-NLS-1$
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isValidatorForObject(final Object obj) {
        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;
            if (ModelUtil.isVdbArchiveFile(iResource)) {
                return true;
            }
        } else if (obj instanceof IPath) {
            final IPath path = (IPath)obj;
            if (ModelUtil.isVdbArchiveFile(path)) {
                return true;
            }
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
            final String msg = VdbEditPlugin.Util.getString(I18N_PREFIX + "validator_cannot_be_used_to_validate_the_object",params); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        // clear any existing results on the context
        context.clearResults();

        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;
            this.validate(iResource, context);
        } else if (obj instanceof IPath) {
            final IPath path = (IPath)obj;
            IResource vdbResource = ModelerCore.getWorkspace().getRoot().findMember(path);
            if(vdbResource != null) {
                this.validate(vdbResource, context);
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
                        ValidationProblem[] problems = result.getProblems();
                        for (int probCnt=0; probCnt < problems.length; probCnt++) {
                            createProblemMarker(iResource, problems[probCnt]);                  
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
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForResource(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean isValidatorForResource(final IResource iResource) {
        if (ModelUtil.isVdbArchiveFile(iResource)) {
            return true;
        }
        
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource, org.eclipse.core.resources.IResource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final IProgressMonitor monitor, final Resource resource, 
                         final IResource iResource, final ValidationContext context) throws ModelerCoreException {
        ArgCheck.isNotNull(iResource);
        
        if( !ModelUtil.isVdbArchiveFile(iResource) ){
            final String msg = VdbEditPlugin.Util.getString(I18N_PREFIX + "VdbResource_validator_may_only_be_used_to_validate_VDB_Resources_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        
        // clear any existing results on the context
        context.clearResults();

        this.validate(iResource, context);
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
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource, org.eclipse.core.resources.IResource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    private void validate(final IResource iResource, final ValidationContext context) throws ModelerCoreException {
        ArgCheck.isNotNull(iResource);
        
        if( !ModelUtil.isVdbArchiveFile(iResource) ){
            final String msg = VdbEditPlugin.Util.getString(I18N_PREFIX + "VdbResource_validator_may_only_be_used_to_validate_VDB_Resources_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        
        IPath vdbPath = iResource.getFullPath();
        
        try {
            // Make sure the file exists
            final File vdbFile = iResource.getLocation().toFile();
            if ( vdbFile == null || !vdbFile.exists()) {
                return;
            }
            
            VdbHeader header = VdbHeaderReader.readHeader(vdbFile);
            if(header == null) {
                final String msg = VdbEditPlugin.Util.getString(I18N_PREFIX + "vdbWithNoHeader",vdbPath); //$NON-NLS-1$
                this.addProblem(iResource, NULL_MANIFEST_MODEL_CODE, IStatus.WARNING, msg, context);
                return;
            }
            VdbModelInfo[] infos = header.getModelInfos();
            for(int i=0; i < infos.length; i++) {
                VdbModelInfo modelInfo = infos[i];
                long vdbModelCheckSum = modelInfo.getCheckSum();
                
                IResource modelResource = null;
                String location = modelInfo.getLocation();
                String path     = modelInfo.getPath();
                if (!StringUtil.isEmpty(path)) {
                    modelResource = ModelerCore.getWorkspace().getRoot().findMember(path);
                } else if (!StringUtil.isEmpty(location)) {
                    if (!location.startsWith("http")) { //$NON-NLS-1$
                        modelResource = ModelerCore.getWorkspace().getRoot().findMember(location);
                    }
                }
                
                if (modelResource != null) {
                    final File modelFile = modelResource.getLocation().toFile();
                    long workspaceModelChecksum = FileUtils.getCheckSum(modelFile);
                    if(vdbModelCheckSum != workspaceModelChecksum) {
                        final Object[] params = new Object[] {modelInfo.getName()};
                        final String msg = VdbEditPlugin.Util.getString(I18N_PREFIX + "staleModelWarningMsg",params); //$NON-NLS-1$
                        this.addProblem(iResource, STALE_MODEL_CODE, IStatus.WARNING, msg, context);
                    }
                }
            }
            // validates that there are no duplicate uuids in workspace
            final String uuid = header.getUUID();
            if (context.containsUuid(uuid)) {
                final String msg = VdbEditPlugin.Util.getString(DUP_UUID_MSG_ID, vdbPath.lastSegment());
                this.addProblem(iResource, DUP_UUID_CODE, IStatus.ERROR, msg, context);
            } else {
                context.addUuidToContext(uuid);
            }
            String problemMsg = null;
            String severity = header.getSeverity();            
            if(severity.equals(VdbHeader.SEVERITY_ERROR)) {
                problemMsg = VdbEditPlugin.Util.getString("VdbResourceValidator.0", header.getName()); //$NON-NLS-1$
                this.addProblem(iResource, VDB_ARCHIVE_ERROR_CODE, IStatus.ERROR, problemMsg, context);
            } else if(severity.equals(VdbHeader.SEVERITY_WARNING)) {
                problemMsg = VdbEditPlugin.Util.getString("VdbResourceValidator.1", header.getName()); //$NON-NLS-1$
                this.addProblem(iResource, VDB_ARCHIVE_WARNING_CODE, IStatus.WARNING, problemMsg, context);
            } else if(severity.equals(VdbHeader.SEVERITY_INFO)) {
                problemMsg = VdbEditPlugin.Util.getString("VdbResourceValidator.2", header.getName()); //$NON-NLS-1$
                this.addProblem(iResource, VDB_ARCHIVE_INFO_CODE, IStatus.INFO, problemMsg, context);
            }
        } catch (Exception e) {
            throw new ModelerCoreException(e, VdbEditPlugin.Util.getString("VdbResourceValidator.Unexpected_error_validating_VDB_1")); //$NON-NLS-1$
        }
    }
    
    private void addProblem(final Object object, final int code, final int severity, 
                            final String msg,  final ValidationContext context) {
        ValidationProblem problem = new ValidationProblemImpl(code, severity, msg);
        ValidationResult result = new ValidationResultImpl(object);        
        result.addProblem(problem);
        context.addResult(result); 
    }

    /**
     * Create a marker given a validationProblem
     */
    private void createProblemMarker(final IResource resource, final ValidationProblem problem) throws CoreException {
        IMarker marker = resource.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
        
        marker.setAttribute(ModelerCore.MARKER_URI_PROPERTY, resource.getFullPath().toString());
        if (problem.getCode() == STALE_MODEL_CODE) {
            final String text = VdbEditPlugin.Util.getString("VdbResourceValidator.out_of_synch"); //$NON-NLS-1$
            marker.setAttribute(ModelerCore.MARKER_PROBLEM_DECORATOR_TEXT, text);
        }
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

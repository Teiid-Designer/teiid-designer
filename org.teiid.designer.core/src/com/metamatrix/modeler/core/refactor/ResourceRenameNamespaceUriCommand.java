/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Map;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/** 
 * @since 4.3
 */
public final class ResourceRenameNamespaceUriCommand extends ResourceRefactorCommand {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ResourceRenameNamespaceUriCommand.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INTERFACES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public interface StatusCodes {
        /**
         * Error code indicating the new value is equal to the old value.
         */
        int NO_CHANGE = 10;
        
        /**
         * Error code indicating the resource is not a model.
         */
        int NOT_MODEL = 20;
        
        /**
         * Error code indicating the resource's project is closed.
         */
        int PROJECT_CLOSED = 30;
        
        /**
         * Error code indicating a problem with the model resource.
         */
        int MODEL_PROBLEM = 40;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String getString(String theKey) {
        return ModelerCore.Util.getStringOrKey(PREFIX + theKey);
    }
    
    private static final String getString(String theKey,
                                          Object theParam) {
        return ModelerCore.Util.getString(PREFIX + theKey, theParam);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private String currentValue = null;
    
    private String newValue = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ResourceRenameNamespaceUriCommand(IResource theModel) {
        super(getString("initLabel")); //$NON-NLS-1$)
        setResource(theModel);
    }
    
    public ResourceRenameNamespaceUriCommand(IResource theModel,
                                             String theNewNamespaceUri) {
        this(theModel);
        this.newValue = theNewNamespaceUri;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getCanExecuteStatus()
     * @since 4.3
     */
    @Override
    protected IStatus getCanExecuteStatus() {
        // assumption: super.canExecute() is the only method that calls this method
        // assumption: super.canExecute() checks that the resource is set and not readonly
        
        IStatus result = null; // leave null if status is OK
        int code = -1;
        Exception exception = null;
        IResource resource = getResource();
        String msg = null; // only set if an error
        
        // file must be a model in a model project
        if (!ModelUtil.isModelFile(resource)) {
            msg = getString("errorMsg.fileNotModel"); //$NON-NLS-1$
            code = StatusCodes.NOT_MODEL;
        }
        
        // model must be in an open project
        if (!resource.getProject().isOpen()) {
            msg = getString("errorMsg.projectNotOpen"); //$NON-NLS-1$
            code = StatusCodes.PROJECT_CLOSED;
        }
        
        try {
            ModelResource modelResource = ModelUtil.getModel(resource);
            
            // error if can't get the ModelResource
            if (modelResource == null) {
                msg = getString("errorMsg.modelResourceProblem", resource); //$NON-NLS-1$
                code = StatusCodes.MODEL_PROBLEM;
            } else {
                this.currentValue = modelResource.getModelAnnotation().getNamespaceUri();
                boolean different = false;
                
                // error if same as old value (don't want to perform refactoring if same value)
                if (this.currentValue == null) {
                    different = (this.newValue != null);
                } else {
                    different = (this.newValue == null) || !this.currentValue.equals(this.newValue);
                }
                
                if (!different) {
                    msg = getString("errorMsg.valueNotChanged"); //$NON-NLS-1$
                    code = StatusCodes.NO_CHANGE;
                }
            }
        } catch (ModelWorkspaceException theException) {
            msg = getString("errorMsg.modelResourceProblem", resource); //$NON-NLS-1$
            code = StatusCodes.MODEL_PROBLEM;
            exception = theException;
        }
        
        if (msg != null) {
            result = new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, code, msg, exception);
        }
        
        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getDescription()
     * @since 4.3
     */
    @Override
    public String getDescription() {
        return getString("description", getResource().getProjectRelativePath()); //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getLabel()
     * @since 4.3
     */
    @Override
    public String getLabel() {
        return getString("label", getResource().getProjectRelativePath()); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getMovedResourcePathMap(boolean)
     * @since 4.3
     */
    @Override
    protected Map getMovedResourcePathMap(boolean theIsUndo) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#modifyResource(org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
    protected IStatus modifyResource(IResource theResource,
                                     IProgressMonitor theMonitor) {
        // assumption: this will not be called if super.canExecute() is false
        return this.modifyResource(theResource, theMonitor, this.newValue);
    }
    
    private IStatus modifyResource(IResource theResource,
                                   IProgressMonitor theMonitor,
                                   String theUri) {
        IStatus result = null;
        
        try {
            ModelResource model = ModelUtil.getModel(theResource);
            
            if (model == null) {
                result = new Status(IStatus.ERROR,
                                    ModelerCore.PLUGIN_ID,
                                    StatusCodes.MODEL_PROBLEM,
                                    getString("errorMsg.modelResourceProblem", theResource), //$NON-NLS-1$
                                    null); // exception
            } else {
                ModelAnnotation annotation = model.getModelAnnotation();
                ModelerCore.getModelEditor().setPropertyValue(annotation,
                                                              theUri,
                                                              CorePackage.eINSTANCE.getModelAnnotation_NamespaceUri());
                setModifiedResource(theResource);
            }
        } catch (ModelWorkspaceException theException) {
            result = new Status(IStatus.ERROR,
                                ModelerCore.PLUGIN_ID,
                                StatusCodes.MODEL_PROBLEM,
                                getString("errorMsg.modelResourceProblem", theResource), theException); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#checkDependentResources(boolean)
     * @since 4.3
     */
    @Override
    protected int checkDependentResources(boolean theIsUndo) {
        return IStatus.OK;
    }

    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#redoResourceModification(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
    protected IStatus redoResourceModification(IProgressMonitor theMonitor) {
        return this.modifyResource(super.getModifiedResource(), theMonitor, this.newValue);
    }
    
    /**
     * Sets the new value to use for the model Namespace URI. 
     * @param theNewUri the URI
     * @since 4.3
     */
    public void setNamespaceUri(String theNewUri) {
        this.newValue = theNewUri;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#shouldRebuildImports()
     * @since 4.3
     */
    @Override
    protected boolean shouldRebuildImports() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#undoResourceModification(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
    protected IStatus undoResourceModification(IProgressMonitor theMonitor) {
        return this.modifyResource(super.getModifiedResource(), theMonitor, this.currentValue);
    }

}

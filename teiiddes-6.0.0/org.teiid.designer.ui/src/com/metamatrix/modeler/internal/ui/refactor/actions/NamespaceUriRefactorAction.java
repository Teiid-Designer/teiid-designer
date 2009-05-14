/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.refactor.ResourceRenameNamespaceUriCommand;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.refactor.NamespaceUriRenameDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;


/** 
 * @since 4.3
 */
public final class NamespaceUriRefactorAction extends RefactorAction
                                              implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(NamespaceUriRefactorAction.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.refactor.actions.RefactorAction#getUndoLabel()
     * @since 4.3
     */
    @Override
    protected String getUndoLabel() {
        return Util.getString(PREFIX + "undoTitle"); //$NON-NLS-1$;
    }
    
    
    /** 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.3
     */
    @Override
    public void run(IAction theAction) {
        try {
            ModelResource model = ModelUtilities.getModelResource((IFile)this.resSelectedResource, true);
            
            if (model == null) {
                MessageDialog.openError(getShell(),
                                        Util.getStringOrKey(PREFIX + "errorTitle"), //$NON-NLS-1$
                                        Util.getString(PREFIX + "msg.modelResourceNull", this.resSelectedResource)); //$NON-NLS-1$
            } else {
                IStatus preExecuteStatus = null;
                ModelAnnotation annotation = model.getModelAnnotation();
                NamespaceUriRenameDialog dialog = new NamespaceUriRenameDialog(getShell(),
                                                                               annotation.getNamespaceUri());
                // construct/display dialog
                dialog.create();
                dialog.open();
                
                // get URI value from dialog
                if (dialog.getReturnCode() == Window.OK) {
                    String newUri = dialog.getUri();
                    ResourceRenameNamespaceUriCommand cmd = new ResourceRenameNamespaceUriCommand(this.resSelectedResource, newUri);
                    preExecuteStatus = cmd.canExecute();
                    
                    if (preExecuteStatus.getSeverity() != IStatus.ERROR) {
                        executeCommand(cmd);
                        IStatus executionStatus = getStatus();
                        
                        if ((executionStatus != null) && (executionStatus.getSeverity() != IStatus.ERROR)) {
                            getRefactorUndoManager().addCommand(cmd);
                        } else if (executionStatus != null) {
                            throw new ModelWorkspaceException(executionStatus.getMessage());
                        }
                    } else {
                        throw new ModelWorkspaceException(preExecuteStatus.getMessage());
                    }
                }
            }
        } catch (ModelWorkspaceException theException) {
            Util.log(theException);
            ErrorDialog.openError(getShell(),
                                  Util.getStringOrKey(PREFIX + "errorTitle"), //$NON-NLS-1$
                                  Util.getString(PREFIX + "msg.executeProblem", this.resSelectedResource), //$NON-NLS-1$
                                  new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException));
        }
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.refactor.actions.RefactorAction#setEnabledState()
     * @since 4.3
     */
    @Override
    protected void setEnabledState() {
        super.setEnabledState();
        
        // make sure the resource is a model
        if (this.action.isEnabled() && !ModelUtil.isModelFile(this.resSelectedResource)) {
            this.action.setEnabled(false);
        }
    }

}

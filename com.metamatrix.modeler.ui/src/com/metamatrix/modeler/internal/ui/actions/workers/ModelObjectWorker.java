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

package com.metamatrix.modeler.internal.ui.actions.workers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.TransactionSettings;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.IActionWorker;
import com.metamatrix.ui.actions.WorkerProblem;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class ModelObjectWorker implements
                              IActionWorker {

    private Object currentSelection;
    private boolean enabled = false;
    private boolean enableAfterExecute = false;
    private WorkerProblem workerProblem;
    private Object workerSource;
    private TransactionSettings txnSettings;
    
    /** 
     * 
     * @since 4.2
     */
    public ModelObjectWorker(boolean enableAfterExecute) {
        super();
        this.enableAfterExecute = enableAfterExecute;
    }
    
    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableState()
     * @since 4.2
     */
    public boolean setEnabledState() {
        return false;
    }

    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#execute()
     * @since 4.2
     */
    public boolean execute() {
        return false;
    }
    
    /**
     * This method checks for licencing issues.
     * @return
     * @since 4.3
     */
    protected boolean canLegallyEditResource() {
        boolean canEdit = false;
        if( currentSelection instanceof ISelection ) {
            List allEObjects = SelectionUtilities.getSelectedEObjects((ISelection)currentSelection);
    
            if( allEObjects != null && !allEObjects.isEmpty() ) {
                canEdit = true;
            } else {
                // make sure only models selected and that they are licensed
                List selectedObjs = SelectionUtilities.getSelectedObjects((ISelection)currentSelection);
                
                if (!selectedObjs.isEmpty()) {
                    canEdit = true;
                
                    for (int size = selectedObjs.size(), i = 0; i < size; i++) {
                        Object obj = selectedObjs.get(i);
                        
                        if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                            
                            try {
                                if (ModelUtilities.getModelResource(((IFile)obj), false) == null) {
                                    canEdit = false;
                                    break;
                                }
                            } catch (ModelWorkspaceException theException) {
                                canEdit = false;
                                UiConstants.Util.log(theException);
                                break;
                            }
                        } else {
                            canEdit = false;
                            break;
                        }
                    }
                }
            }
        }
        
        return canEdit;
    }
    
    /**
     * This method provides modeler actions a quick way to determine whether the selected EObjects
     * are in a model that is read-only.  This is needed for enable/disable when selection changes.
     * @param theSelection
     * @return <code>true</code> if at least one of the selected EObjects or models is readonly;
     * <code>false</code> otherwise.
     */
    protected boolean isReadOnly() {
        boolean isreadonly = true;

        if( currentSelection instanceof ISelection ) {
            List allEObjects = SelectionUtilities.getSelectedEObjects((ISelection)currentSelection);
    
            if (!allEObjects.isEmpty()) {
                isreadonly = false;
                Iterator iter = allEObjects.iterator();
    
                while( iter.hasNext() ) {
                    if( ModelObjectUtilities.isReadOnly((EObject)iter.next()) ) {
                        isreadonly = true; 
                        break;
                    }
                }
            } else {
                List selectedObjs = SelectionUtilities.getSelectedObjects((ISelection)currentSelection);
                
                if (!selectedObjs.isEmpty()) {
                    isreadonly = false;
                    
                    for (int size = selectedObjs.size(), i = 0; i < size; i++) {
                        Object obj = selectedObjs.get(i);
                        
                        if (obj != null && (obj instanceof IResource) && 
                            ((IResource)obj).getResourceAttributes() != null && ((IResource)obj).getResourceAttributes().isReadOnly()) {
                            isreadonly = true;
                            break;
                        } else if (obj != null && !(obj instanceof IResource)) {
                            isreadonly = true;
                            break;
                        }
                    }
                }
            }
        }

        
        return isreadonly;
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.IActionWorker#getSelection()
     * @since 4.2
     */
    public Object getSelection() {
        return currentSelection;
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.IActionWorker#selectionChanged(java.lang.Object)
     * @since 4.2
     */
    public boolean selectionChanged(Object selection) {
        currentSelection = selection;
        enabled = setEnabledState();
        return enabled;
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.IActionWorker#isEnabled()
     * @since 4.2
     */
    public boolean isEnabled() {
        return enabled;
    }
    /**
     *  
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableAfterExecute()
     * @since 4.2
     */
    public boolean getEnableAfterExecute() {
        return this.enableAfterExecute;
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.IActionWorker#getWorkerProblem()
     * @since 4.2
     */
    public WorkerProblem getWorkerProblem() {
        return this.workerProblem;
    }
    
    /**
     *  
     * @param workerProblem
     * @since 4.2
     */
    public void setWorkerProblem(WorkerProblem workerProblem) {
        this.workerProblem = workerProblem;
    }
    
    public Object getWorkerSource() {
        return this.workerSource;
    }
    
    public void setWorkerSource(Object workerSource) {
        this.workerSource = workerSource;
    }
    
    public void setTransactionSettings( TransactionSettings txnSettings ) {
        this.txnSettings = txnSettings;
        
        // set source to this
        if ( this.txnSettings != null ) {
            this.txnSettings.setSource( this );
        }
    }

    public TransactionSettings getTransactionSettings() {
        return txnSettings;
    }

    protected ModelEditor getActiveEditor() {
        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        // see if active page is available:
        if (page == null) {
            // not available, see if we have any reference to a page:
            page = AbstractUiPlugin.getLastValidPage();
            
            if (page == null) {
                // still no page; exit:
                return null;
            } // endif
        } // endif
    
        IEditorPart editor = page.getActiveEditor();
    
        if (editor instanceof ModelEditor) {
            return (ModelEditor) editor;                   
        }
        return null;
    }

}

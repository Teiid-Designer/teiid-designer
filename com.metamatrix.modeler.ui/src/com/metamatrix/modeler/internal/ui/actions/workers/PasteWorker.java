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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.actions.WorkerProblem;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class PasteWorker extends ModelObjectWorker {
    private static final String PROBLEM = "PasteAction.problem"; //$NON-NLS-1$
    private boolean pastingDiagram = false;
    /** 
     * 
     * @since 4.2
     */
    public PasteWorker(boolean enableAfterExecute) {
        super(enableAfterExecute);
    }

    @Override
    public boolean execute() {
        boolean successful = false;
        Object selectedObject = SelectionUtilities.getSelectedObject((ISelection)getSelection());
        
        if( pastingDiagram ) {
            // Now we see if the clipboard contents contains a Diagram
            List cbContents = getClipboardContents(selectedObject);
            
            if( cbContents != null && cbContents.size() == 1 ) {
                EObject eObj = (EObject)cbContents.get(0);
                if( eObj instanceof Diagram) {
                    DiagramHelperManager.paste((Diagram)eObj, (EObject)selectedObject);
                    successful = true;
                }
            }
        } else {
            
            String pasteInName = null;

            if (selectedObject instanceof EObject) {
                pasteInName = ModelerCore.getModelEditor().getName((EObject)selectedObject);
            } else if ((selectedObject instanceof IResource) && ModelUtilities.isModelFile((IResource)selectedObject)) {
                try {
                    ModelResource modelResource = ModelUtilities.getModelResource((IFile)selectedObject, false);
                    
                    if (modelResource == null) {
                        // log exception
                        throw new ModelWorkspaceException(UiConstants.Util.getString("PasteWorker.msg.nullModelResource")); //$NON-NLS-1$
                    }
                    selectedObject = modelResource;        // reset the 'selection' to be the ModelResource
                    pasteInName = ModelerCore.getModelEditor().getModelName(modelResource);
                } catch (ModelWorkspaceException theException) {
                    UiConstants.Util.log(theException);
                }
            } else {
                Assertion.failed(PasteWorker.class + ".doRun: unexpected paste target <" + selectedObject + ">."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            // only do paste if we've assigned a transaction name
            if (pasteInName != null) {
                boolean started = ModelerCore.startTxn(true, UiConstants.Util.getString("PasteWorker.undoLabel", pasteInName), this); //$NON-NLS-1$
                boolean succeeded = false;  
    
                try {
                    ModelerCore.getModelEditor().pasteFromClipboard(selectedObject); 
                    succeeded = true;
                } catch (final Exception err) {
                    String title = UiConstants.Util.getString("PasteWorker.errorTitle"); //$NON-NLS-1$
                    String message = UiConstants.Util.getString("PasteWorker.errorMessage"); //$NON-NLS-1$
                    setWorkerProblem(new WorkerProblem(title, message));
                    
                    final String msg = UiConstants.Util.getString(PROBLEM, new Object[] {selectedObject}); 
                    UiConstants.Util.log(IStatus.ERROR, err, msg);
                } finally {
                    if ( started ) {
                        if ( succeeded ) {
                            successful = true;
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        return successful;
    }
    
    @Override
    public boolean setEnabledState() {
        boolean enable = false;
        pastingDiagram = false;

        Object selection = getSelection();
        if( selection instanceof ISelection ) {
            if( SelectionUtilities.isSingleSelection((ISelection)selection) ) {
                if(!isReadOnly() && canLegallyEditResource() ) {
                    enable = true;
                    final Object obj = SelectionUtilities.getSelectedObject((ISelection)selection);
                    
                    if (obj != null) {
                        enable = ModelerCore.getModelEditor().isValidPasteParent(obj);
                    }
    
                    if( !enable ) {
                        enable = isAndCanPasteDiagram((ISelection)selection);
                        if( enable )
                            pastingDiagram = true;
                    }
                }
            }
        }
        
        return enable;
    }
    
    /*
     * Assume the this is single selection
     */
    private boolean isAndCanPasteDiagram(ISelection iSingleSelection) {
        boolean canPaste = false;

        final EObject eObj = SelectionUtilities.getSelectedEObject(iSingleSelection);
        
        if (eObj != null) {
            // Now we see if the clipboard contents contains a Diagram
            List cbContents = getClipboardContents(eObj);
        
            if( cbContents != null && cbContents.size() == 1 ) {
                Object obj = cbContents.get(0);
                if( obj instanceof Diagram) {
                    canPaste = DiagramHelperManager.canPaste((Diagram)obj, eObj);
                }

            }
        }

        return canPaste;
    }
    
    private List getClipboardContents(Object targetForPaste) {
        List cbContents = null;
        try {
            cbContents = new ArrayList(ModelerCore.getModelEditor().getClipboardContents(targetForPaste));
        } catch (ModelerCoreException err) {
            final String msg = UiConstants.Util.getString(PROBLEM, new Object[] {targetForPaste}); 
            UiConstants.Util.log(IStatus.ERROR, err, msg);
        }
        if( cbContents == null )
            return Collections.EMPTY_LIST;
            
        return cbContents;
    }
}

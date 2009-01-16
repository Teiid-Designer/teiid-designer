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

package com.metamatrix.modeler.relationship.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * PasteAction
 */
public class PasteAction 
     extends RelationshipAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "PasteAction.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "PasteAction.undoText"; //$NON-NLS-1$
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of PasteAction.
     * 
     */
    public PasteAction() {
        super();
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
    }

    //============================================================================================================================
    // ISelectionListener Methods
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // sample code:
        super.selectionChanged(part, selection);
        boolean enable = false;
                
        List sourceEObjects = null;
        if (SelectionUtilities.isSingleSelection(selection)) {
            sourceEObjects = new ArrayList(1);
            Object o = SelectionUtilities.getSelectedObject(selection);
            sourceEObjects.add(o);
        } else if (SelectionUtilities.isMultiSelection(selection)) {
            sourceEObjects = SelectionUtilities.getSelectedEObjects(selection);
        }
             
		if( sourceEObjects != null && !sourceEObjects.isEmpty())      
        	enable = RelationshipGlobalActionsManager.canPaste( sourceEObjects );
        
        setEnabled(enable);
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject obj = SelectionUtilities.getSelectedEObject(getSelection());
        
        String objectString = null;

        String description = null;
        if (obj != null) {
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
            objectString = ilp.getText(obj);
            description = getPluginUtils().getString(UNDO_TEXT, objectString);
        }

        boolean requiredStart = ModelerCore.startTxn(true, true, description, this);
        boolean succeeded = false;
        try {
            ModelerCore.getModelEditor().pasteFromClipboard(obj);
            succeeded = true;
        } catch (ModelerCoreException theException) {
            String msg = getPluginUtils().getString(PROBLEM, objectString); 
            getPluginUtils().log(IStatus.ERROR, theException, msg);
            setEnabled(false);
        } finally {
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        determineEnablement();
    }
        
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        
        if( SelectionUtilities.isSingleSelection(getSelection()) &&
            !isReadOnly() ) {
            List sourceEObjects = null;
            EObject targetEObject = null;
            if (SelectionUtilities.isSingleSelection(getSelection())) {
                sourceEObjects = new ArrayList(1);
                targetEObject = SelectionUtilities.getSelectedEObject(getSelection());
                sourceEObjects.add(targetEObject);
            }
            if( targetEObject != null ) {
                enable = (RelationshipGlobalActionsManager.canPaste( sourceEObjects ) &&
                          ModelerCore.getModelEditor().isValidPasteParent(sourceEObjects));
            }
        }
        setEnabled(enable);
    }
}

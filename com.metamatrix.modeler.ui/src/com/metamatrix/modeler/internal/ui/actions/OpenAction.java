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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.internal.ui.views.DatatypeHierarchyView;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * OpenAction
 */
public class OpenAction extends AbstractAction {


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public OpenAction() {
        super(UiPlugin.getDefault());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        
        boolean enable = false;
        // Added the instanceof check for Defect 11152. This was enabling for a non-editable view
        // This was easiest way to fix.  Could check for model type, but.....
        if ( !(thePart instanceof DatatypeHierarchyView) && SelectionUtilities.isSingleSelection(theSelection) ) {
             EObject obj = SelectionUtilities.getSelectedEObject(theSelection);
             if ( obj != null ) {
                 enable = true;
             }
        }
        setEnabled(enable);
    }

	@Override
    protected void doRun() {
        EObject obj = SelectionUtilities.getSelectedEObject(getSelection());
        if ( obj != null ) {
        	// Changed 5/25/04 BML. Why not use the open command for any selection
        	// and let the editor decide (if already open) whether to change the focus of
        	// the editor.
        	// If working in diagrams, this works great. If user is working in table view
        	// the editor will change to diagram view. I guess this makes sense....??
        	if(!ModelEditorManager.isOpen(obj)) {
            	ModelEditorManager.open(obj, true);
        	} else
				ModelEditorManager.open(obj, false);
        }
	}
}

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

package com.metamatrix.modeler.mapping.ui.actions;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.mapping.ui.DebugConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/**
 * NewTempTableAction
 */
public class NewStagingTableAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "New Staging Table"; //$NON-NLS-1$
        
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public NewStagingTableAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.NEW_TEMP_TABLE));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
		UiConstants.Util.start(ACTION_DESCRIPTION, DebugConstants.METRICS_MAPPING_ACTION_SELECTION );
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
		UiConstants.Util.stop(ACTION_DESCRIPTION, DebugConstants.METRICS_MAPPING_ACTION_SELECTION );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if( getMappingClassFactory() != null ) {
            Object o = SelectionUtilities.getSelectedObject(getSelection());
            if( o instanceof EObject ) {
                EObject eObject = (EObject)o;
                boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_NEW_STAGING_TABLE;
                //start txn
                boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                boolean succeeded = false;
                try {
                    getMappingClassFactory().createStagingTable(eObject);
                    succeeded = true;
                } finally {
                    if(requiredStart){
                        if ( succeeded ) {
                            ModelerCore.commitTxn( );
                            if( !canUndo)
                                ModelerUndoManager.getInstance().clearAllEdits();
                        } else {
                            ModelerCore.rollbackTxn( );
                        }
                    }
                }
                
            } else {
                // LOG AN ERROR HERE!!
            }
        }
        
        setEnabled(false);
    }
    
    
    private void determineEnablement() {
        // This is an action that requires two things...
        // 1) Single Selection
        // 2) Selected object can allow mapping class
        boolean enable = false;

        if ( this.getPart() instanceof ModelEditor && SelectionUtilities.isSingleSelection(getSelection())) {
            EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            if( eObject != null && isWritable() && getMappingClassFactory() != null ) {
                if( getMappingClassFactory().canCreateStagingTable(eObject) )
                    enable = true;
            }
        }
        
        setEnabled(enable);
    }
}

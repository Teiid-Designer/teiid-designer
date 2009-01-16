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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.mapping.ui.DebugConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;


/**
 * SplitMappingClassAction
 */
public class SplitMappingClassAction extends MappingAction {
    private static final String ERROR_MESSAGE =
         "Cannot Split Mapping Class.\n\n" + //$NON-NLS-1$
         "Current mapping class multiplicity values are satisfied.\n" + //$NON-NLS-1$
         "Cannot automatically split.\n" + //$NON-NLS-1$
         "Split manually by selecting a node (i.e. sequence) in the mapping tree \n" + //$NON-NLS-1$
         "and selecting the \"New Mapping Class\" action."; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION = "Split Mapping Class"; //$NON-NLS-1$
         
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public SplitMappingClassAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SPLIT_MAPPING_CLASS));
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
            EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            
            boolean canSplit = getMappingClassFactory().canSplitMappingClass((MappingClass)eObject, MappingClassFactory.getDefaultStrategy());
            
            if( canSplit ) {
                boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_SPLIT_MAPPING_CLASS;
                //start txn
                boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                boolean succeeded = false;
                try {
                    getMappingClassFactory().splitMappingClass((MappingClass)eObject, MappingClassFactory.getDefaultStrategy(), false);
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
                // Throw up a dialog here to warn the user that split could not happen.
                WidgetUtil.showWarning(ERROR_MESSAGE);
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
            EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
            if( eObj != null && isWritable() && isMappingClass(eObj) && !isStagingTable(eObj)
                &&  getMappingClassFactory() != null ) {
                enable = true;
            }
        }

        setEnabled(enable);
    }
}

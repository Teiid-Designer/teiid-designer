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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.table.ModelTableEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelperManager;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class CopyWorker extends ModelObjectWorker {
    private static final String PROBLEM = "CopyWorker.problem"; //$NON-NLS-1$
    /** 
     * 
     * @since 4.2
     */
    public CopyWorker(boolean enableAfterExecute) {
        super(enableAfterExecute);
    }

    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableState()
     * @since 4.2
     */
    @Override
    public boolean setEnabledState() {
        boolean enable = false;
        Object selection = getSelection();
        if( selection instanceof ISelection && canLegallyEditResource() ) {
            ISelection iSelection = (ISelection)selection;
            if( SelectionUtilities.isSingleSelection(iSelection)) {
                final EObject obj = SelectionUtilities.getSelectedEObject(iSelection);
                if ( obj instanceof Diagram ) {
                    enable = DiagramHelperManager.canCopy((Diagram) obj);
                } else {
                    // Now we check if edit helpers will veto this copy
                    enable = (obj != null) && ModelObjectEditHelperManager.canCopy(obj); /* && ModelerCore.getModelEditor().isPasteable(obj)*/
                }
            } else if( SelectionUtilities.isMultiSelection(iSelection)) {
                final List eObjectList = SelectionUtilities.getSelectedEObjects(iSelection);
                enable = (eObjectList != null && !eObjectList.isEmpty());
                // Now we check if edit helpers will veto this copy
                if( enable ) {
                    EObject nextEObj = null;
                    for( Iterator iter = eObjectList.iterator(); iter.hasNext(); ) {
                        nextEObj = (EObject)iter.next();
                        if( !ModelObjectEditHelperManager.canCopy(nextEObj)) {
                            enable = false;
                            break;
                        }
                    }
                }
            }
        }
        
        return enable;
    }

    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#execute()
     * @since 4.2
     */
    @Override
    public boolean execute() {
        boolean successful = false;
        Object selection = getSelection();
        if( selection instanceof ISelection && canLegallyEditResource() ) {
            ISelection iSelection = (ISelection)selection;
            if( SelectionUtilities.isSingleSelection(iSelection)) {
                final EObject obj = SelectionUtilities.getSelectedEObject(iSelection);
                try {
                    ModelerCore.getModelEditor().copyToClipboard(obj);
                    successful = true;
                } catch (final ModelerCoreException err) {
                    final String msg = UiConstants.Util.getString(PROBLEM, new Object[] {obj}); 
                    UiConstants.Util.log(IStatus.ERROR, err, msg);
                }
                                
            } else if( SelectionUtilities.isMultiSelection(iSelection)) {
                final List eObjectList = SelectionUtilities.getSelectedEObjects(iSelection);
                try {
                    ModelerCore.getModelEditor().copyAllToClipboard(eObjectList);
                    successful = true;
                } catch (final ModelerCoreException err) {
                    final String msg = UiConstants.Util.getString(PROBLEM, new Object[] {eObjectList}); 
                    UiConstants.Util.log(IStatus.ERROR, err, msg);
                }
            }
            
            // jh Defect 19246: Add copy to System Clipboard
            doTableCopy();
        }
        return successful;
    }
    
    

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    protected void doTableCopy() {
        ModelTableEditor mte = getTableEditor();
        if ( mte != null ) {
            mte.copySelectedToSystemClipboard();
        }
    }

    private ModelTableEditor getTableEditor() {
        ModelTableEditor result = null;
        IEditorPart editor = getActiveEditor();

        if (editor instanceof ModelEditor) {
            ModelEditor modelEditor = (ModelEditor)editor;
            IEditorPart subEditor = modelEditor.getActiveEditor();
        
            if (subEditor instanceof ModelTableEditor) {
                result = (ModelTableEditor)subEditor;
            }
        }
        
        return result;
    }

}

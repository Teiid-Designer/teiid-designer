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

package com.metamatrix.modeler.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * CloneAction
 */
public class CloneAction 
     extends TransformationAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "CloneAction.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "CloneAction.undoText"; //$NON-NLS-1$
    private static final String PLURAL_UNDO_TEXT = "CloneAction.pluralUndoText"; //$NON-NLS-1$ 
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of CloneAction.
     * 
     */
    public CloneAction(EObject transformationEObject, Diagram diagram) {
        super( transformationEObject, diagram );
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
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
   
        String objectString = null;
        List lstObjs = SelectionUtilities.getSelectedEObjects(getSelection());
        String description;
        if (lstObjs.size() == 1) {
            EObject obj = (EObject)lstObjs.get(0);
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
            objectString = ilp.getText(obj);
            description = getPluginUtils().getString(UNDO_TEXT, objectString);
        } else {
            objectString = "" + lstObjs.size(); //$NON-NLS-1$
            description = getPluginUtils().getString(PLURAL_UNDO_TEXT, objectString);
        }

        boolean requiredStart = ModelerCore.startTxn(true, true, description, this);
        boolean succeeded = false;
        try {
            TransformationGlobalActionsManager.clone( lstObjs );
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
        setEnabled(false);
    }
    
    
    private void determineEnablement() {
        boolean enable = false;
        if (!isDependencyDiagram() && areEObjectsSelected() && !isReadOnly()) {
            List sourceEObjects = Collections.EMPTY_LIST;
            
            if (isEObjectSelected()) {
                Object o = SelectionUtilities.getSelectedEObject(getSelection());
                sourceEObjects = new ArrayList(1);
                sourceEObjects.add(o);
            } else if (areEObjectsSelected()) {
                sourceEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            }
            
            if( !sourceEObjects.isEmpty() )
                enable =TransformationGlobalActionsManager.canClone( getTransformation(), sourceEObjects );
            else
                enable = false;
        }
        setEnabled(enable);
    }
 }


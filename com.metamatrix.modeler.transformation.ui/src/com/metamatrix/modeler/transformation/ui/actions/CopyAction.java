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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * CopyAction
 */
public class CopyAction 
     extends TransformationAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "CopyAction.problem"; //$NON-NLS-1$
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of CopyAction.
     * 
     */
    public CopyAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
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
        if (lstObjs.size() == 1) {
            EObject obj = (EObject)lstObjs.get(0);
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
            objectString = ilp.getText(obj);
        } else {
            objectString = "" + lstObjs.size(); //$NON-NLS-1$
        }
        
        try {
            if( lstObjs.size() == 1 ) {
                EObject obj = (EObject)lstObjs.get(0);
                ModelerCore.getModelEditor().copyToClipboard(obj); 
            } else {
                ModelerCore.getModelEditor().copyAllToClipboard(lstObjs);
            }
        } catch (final ModelerCoreException err) {
            final String msg = Util.getString(PROBLEM, objectString); 
            getPluginUtils().log(IStatus.ERROR, err, msg);
        }
        
        determineEnablement();
    }
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        
        if( !isReadOnly() && areEObjectsSelected()) {
            List sourceEObjects = Collections.EMPTY_LIST;
            
            if (isEObjectSelected()) {
                sourceEObjects = new ArrayList(1);
                Object o = SelectionUtilities.getSelectedObject(getSelection());
                sourceEObjects.add(o);
            } else if (areEObjectsSelected()) {
                sourceEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            }
            
            if( !sourceEObjects.isEmpty() )
                enable = TransformationGlobalActionsManager.canCopy( getTransformation(), sourceEObjects );
            else
                enable = false;
        }
        setEnabled(enable);
    }
}

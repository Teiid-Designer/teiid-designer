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

package com.metamatrix.modeler.webservice.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0.2
 */
public class OperationEditorSelectionHandler extends SelectionProvider implements
                                                                      ISelectionChangedListener {

    // ===========================================================================================================================
    // Variables

    private TreeViewer viewer;
    private boolean isSelecting;

    // ===========================================================================================================================
    // Methods

    public void initialize(TreeViewer viewer) {
        if (this.viewer == null) {
            this.viewer = viewer;
            this.viewer.addSelectionChangedListener(this);
        }
    }

    /**
     * Called by the TableViewer when the selection changes, the content of the selection will be ModelRowElements. This method
     * responds by converting them to EObjects and re-firing the selection out to the ModelEditor.
     * 
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 4.0
     */
    public void selectionChanged(final SelectionChangedEvent event) {
        if (this.isSelecting) {
            return;
        }
        this.isSelecting = true;
        try {
            if (event.getSource() == this.viewer) {
                // event came from the tree. convert selection to EObjects and fire out to listeners
                setSelection(event.getSelection(), true, this);
            } else {
                // event came from the ModelEditor. Convert selection to the tree
                setSelection(event.getSelection());
            }
        } finally {
            this.isSelecting = false;
        }
    }

    /**
     * Overridden to allow calling setSelection from outside the table package, this method converts the selection from EObjects
     * to ModelRowElements and set it on the table.
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0.2
     */
    @Override
    public void setSelection(final ISelection selection) {
        // Now we need to auto-select the Tree. The selection can only be an interface or operation, so let's go searching.
        if (this.viewer != null && SelectionUtilities.isSingleSelection(selection) && SelectionUtilities.isAllEObjects(selection)) {
            EObject eObj = SelectionUtilities.getSelectedEObject(selection);
            if (eObj != null && ( eObj instanceof Operation || eObj instanceof Interface) ) {
                this.viewer.setSelection(new StructuredSelection(eObj), true);
            }
        }
    }
}

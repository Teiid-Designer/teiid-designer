/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import com.metamatrix.ui.internal.eventsupport.SelectionProvider;

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
    	// N/A
    }
}

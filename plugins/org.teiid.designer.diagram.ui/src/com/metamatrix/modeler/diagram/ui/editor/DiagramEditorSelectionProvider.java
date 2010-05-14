/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * DiagramEditorSelectionProvider is the ModelObjectSelectionProvider for the DiagramViewer.
 * It handles diagram selection for the viewer, plus an optional provider for the DiagramController.
 */
public class DiagramEditorSelectionProvider implements ISelectionProvider, ISelectionChangedListener {

    ISelection currentSelection;
    ArrayList listenerList = new ArrayList();

    /**
     * Construct an instance of DiagramEditorSelectionProvider.
     */
    public DiagramEditorSelectionProvider(DiagramViewer viewer) {
        viewer.addSelectionChangedListener(this);
    }

    public void setDiagramController(DiagramController controller) {
        ISelectionProvider provider = controller.getSelectionSource();
        if ( provider != null ) {
            provider.addSelectionChangedListener(this); 
        }
    }
    
    public void removeDiagramController(DiagramController controller) {
        ISelectionProvider provider = controller.getSelectionSource();
        if ( provider != null ) {
            provider.removeSelectionChangedListener(this);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public synchronized void addSelectionChangedListener(ISelectionChangedListener listener) {
        if ( ! listenerList.contains(listener) ) {
            listenerList.add(listener);
        }   
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public synchronized ISelection getSelection() {
        return currentSelection;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public synchronized void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listenerList.remove(listener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public synchronized void setSelection(ISelection selection) {
        currentSelection = selection;
        fireSelectionChanged(new SelectionChangedEvent(this, selection));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public synchronized void selectionChanged(SelectionChangedEvent event) {
        currentSelection = event.getSelection();
        fireSelectionChanged(event);
    }

    private void fireSelectionChanged(SelectionChangedEvent event) {
        for ( Iterator iter = listenerList.iterator() ; iter.hasNext() ; ) {
            ((ISelectionChangedListener) iter.next()).selectionChanged(event);
        }
    }

}

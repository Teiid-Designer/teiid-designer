/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;

/**
 * ModelEditorSelectionProvider is responsible for acting as the selection provider for the ModelEditor multi-page editor. It does
 * so by always registering as a SelectionChangeListener with the current ModelEditorPage's model object SelectionProvider. When
 * the currently active ModelEditorPage calls selectionChanged, that selection event is broadcast to whatever listeners have
 * registered with this object via the ISelectionProvider interface.
 */
public class ModelEditorSelectionProvider implements ISelectionProvider {

    private ModelEditor modelEditor;
    private ArrayList listeners = new ArrayList();
    private ISelection selection;
    ModelEditorPage sourcePage;

    /**
     * SelectionChangedListener to hook to the current ModelEditorPage
     */
    private ISelectionChangedListener theSelectionChangedListener = new ISelectionChangedListener() {
        /** Called by the ModelEditorPage to signal a selection change */
        public void selectionChanged( SelectionChangedEvent e ) {
            if (isVisible(sourcePage)) {

                // save the current selection state and broadcast to all listeners
                setSelection(e.getSelection());
            }
        }
    };

    /**
     * Creates a selection provider for the given multi-page editor.
     * 
     * @param multiPageEditor the multi-page editor
     */
    public ModelEditorSelectionProvider( ModelEditor editor ) {
        this.modelEditor = editor;
    }

    /**
     * Controls whether or not this SelectionProvider should fire SelectionChangedEvents to listeners
     * 
     * @enable true if this provider should fire events, false if events should be consumed.
     */
    void setChangeEventsEnabled( boolean enable ) {
    }

    /**
     * Set the current ModelEditorPage being displayed in the ModelEditor. This object responds by obtaining the ModelEditorPage's
     * SelectionProvider and hooking it up suchthat selection changes are fired to any listeners.
     * 
     * @param sourcePage
     */
    public void setSourcePage( ModelEditorPage sourcePage ) {
        this.sourcePage = sourcePage;
        if (this.sourcePage != null) {
            ISelectionProvider sourceProvider = sourcePage.getModelObjectSelectionProvider();
            if (sourceProvider != null) {
                sourceProvider.addSelectionChangedListener(theSelectionChangedListener);
            }
        }
    }

    public ModelEditorPage getSourcePage() {
        return sourcePage;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Return the current selection. If the active editor has a selection provider, return it's selection. Otherwise, return the
     * last selection that came through this object.
     */
    public ISelection getSelection() {
        if (sourcePage != null && isVisible(sourcePage)) {
            ISelectionProvider selectionProvider = sourcePage.getModelObjectSelectionProvider();
            if (selectionProvider != null) {
                return selectionProvider.getSelection();
            }
        }
        return selection;
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Called when events come in from the ModelEditorPage and need to get broadcast out to other Viewers.
     */
    public synchronized void setSelection( ISelection selection ) {
        this.selection = selection;
        // if the modelEditor is not the active part, then the selection originated outside the editor
        // and should be fired to the selection service. Otherwise, it should be consumed.
        if (isActivePart()) {
            fireSelectionChanged(new SelectionChangedEvent(this, selection));
        }
    }

    public boolean isVisible( ModelEditorPage page ) {
        return modelEditor.getCurrentPage() == page;
    }

    /**
     * Notifies all registered selection changed listeners that the editor's selection has changed. Only listeners registered at
     * the time this method is called are notified.
     * 
     * @param event the selection changed event
     */
    public void fireSelectionChanged( final SelectionChangedEvent event ) {
        // Defect 20052 Concurrent Modification problem
        // FIX = Copy the list before operating on it. Remove synchronize call.
        List copiedList = new ArrayList(listeners);

        final Iterator iter = copiedList.iterator();
        while (iter.hasNext()) {
            final ISelectionChangedListener l = (ISelectionChangedListener)iter.next();
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(event);
                }

                @Override
                public void handleException( Throwable e ) {
                    super.handleException(e);
                    // If an unexpected exception happens, remove it
                    // to make sure the workbench keeps running.
                    // use the iterator-safe remove to prevent ConcurrentModEx:
                    iter.remove();
                }
            });
        }
    }

    /**
     * Determine if this object's model editor is the active part in the workbench, indicating that the current selection event
     * came from it.
     * 
     * @return true if this object's ModelEditor is the active part in the workbench.
     */
    private boolean isActivePart() {
        return modelEditor.getEditorSite().getWorkbenchWindow().getPartService().getActivePart() == modelEditor;
    }
}

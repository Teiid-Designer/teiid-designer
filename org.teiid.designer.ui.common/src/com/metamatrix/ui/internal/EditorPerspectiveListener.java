/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * This class listens to the workbench and records the active editor for every deactivated perspective so the editor can be
 * re-activated when the perspective is re-opened.
 * 
 * @since 5.0.2
 */
public class EditorPerspectiveListener implements IPerspectiveListener3 {

    /** map of key=IPerspectiveDescriptor to value=IEditorInput */
    private Map<IPerspectiveDescriptor, IEditorInput> activeEditorMap = new HashMap<IPerspectiveDescriptor, IEditorInput>();

    IEditorReference currentEditor;

    /**
     * Create an EditorPerspectiveListener for the specified window
     * 
     * @param window
     * @since 5.0.2
     */
    public EditorPerspectiveListener( IWorkbenchWindow window ) {
        // register a part listener to keep track of the current editor
        window.getPartService().addPartListener(new IPartListener2() {
            public void partActivated( IWorkbenchPartReference ref ) {
                if (ref instanceof IEditorReference) {
                    currentEditor = (IEditorReference)ref;
                }
            }

            public void partBroughtToTop( IWorkbenchPartReference ref ) {
                if (ref instanceof IEditorReference) {
                    currentEditor = (IEditorReference)ref;
                }
            }

            public void partClosed( IWorkbenchPartReference ref ) {
                if (ref != null && ref.equals(currentEditor)) {
                    currentEditor = null;
                }
            }

            public void partDeactivated( IWorkbenchPartReference ref ) {
            }

            public void partOpened( IWorkbenchPartReference ref ) {
            }

            public void partHidden( IWorkbenchPartReference ref ) {
            }

            public void partVisible( IWorkbenchPartReference ref ) {
            }

            public void partInputChanged( IWorkbenchPartReference ref ) {
            }

        });
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener3#perspectiveClosed(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor)
     * @since 5.0.2
     */
    public void perspectiveClosed( IWorkbenchPage page,
                                   IPerspectiveDescriptor perspective ) {
        activeEditorMap.remove(perspective);
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener3#perspectiveDeactivated(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor)
     * @since 5.0.2
     */
    public void perspectiveDeactivated( IWorkbenchPage page,
                                        IPerspectiveDescriptor perspective ) {
        try {
            if (currentEditor != null && currentEditor.getEditorInput() != null) {
                activeEditorMap.put(perspective, currentEditor.getEditorInput());
            }
        } catch (PartInitException e) {
            // no need to log, if editor init fails, just proceed
        }
        currentEditor = null;
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor)
     * @since 5.0.2
     */
    public void perspectiveActivated( IWorkbenchPage page,
                                      IPerspectiveDescriptor perspective ) {
        if (activeEditorMap.keySet().contains(perspective)) {
            IEditorInput editorInput = activeEditorMap.get(perspective);
            if (editorInput != null) {
                IEditorReference[] editors = page.getEditorReferences();
                for (int i = 0; i < editors.length; ++i) {
                    try {
                        if (editors[i].getEditorInput().equals(editorInput)) {
                            page.openEditor(editorInput, editors[i].getId(), false);
                            break;
                        }
                    } catch (PartInitException e) {
                        // no need to log, we are just looking through the editors
                    }
                }
            }
        }
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener3#perspectiveSavedAs(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IPerspectiveDescriptor)
     * @since 5.0.2
     */
    public void perspectiveSavedAs( IWorkbenchPage page,
                                    IPerspectiveDescriptor oldPerspective,
                                    IPerspectiveDescriptor newPerspective ) {
        IEditorInput editor = activeEditorMap.get(oldPerspective);
        if (editor != null) {
            activeEditorMap.put(newPerspective, editor);
        }
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference, java.lang.String)
     * @since 5.0.2
     */
    public void perspectiveChanged( IWorkbenchPage page,
                                    IPerspectiveDescriptor perspective,
                                    IWorkbenchPartReference partRef,
                                    String changeId ) {
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
     * @since 5.0.2
     */
    public void perspectiveChanged( IWorkbenchPage page,
                                    IPerspectiveDescriptor perspective,
                                    String changeId ) {
    }

    /**
     * @see org.eclipse.ui.IPerspectiveListener3#perspectiveOpened(org.eclipse.ui.IWorkbenchPage,
     *      org.eclipse.ui.IPerspectiveDescriptor)
     * @since 5.0.2
     */
    public void perspectiveOpened( IWorkbenchPage page,
                                   IPerspectiveDescriptor perspective ) {
    }
}

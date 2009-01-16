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
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SaveAllAction implements IActionDelegate2, IWorkbenchWindowActionDelegate, IPartListener, IPropertyListener {

    private IAction action;

    private IWorkbenchWindow window;

    void addPropertyChangeListener( IEditorPart theEditor ) {
        // editor fire a property change when they are dirty
        theEditor.addPropertyListener(this);
        updateState();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
     */
    public void init( IAction theAction ) {
        action = theAction;
        action.setEnabled(false); // disable initially
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow theWindow ) {
        window = theWindow;

        // when Eclipse starts and there are editors open, a part event is not received for that
        // editor. no partActivated or partOpened event is received. this is a workaround for that.
        window.getWorkbench().addWindowListener(new IWindowListener() {
            public void windowActivated( IWorkbenchWindow window ) {
            }

            public void windowDeactivated( IWorkbenchWindow window ) {
            }

            public void windowClosed( IWorkbenchWindow window ) {
            }

            public void windowOpened( IWorkbenchWindow window ) {
                IEditorPart editorPart = window.getActivePage().getActiveEditor();

                if (editorPart != null) {
                    addPropertyChangeListener(editorPart);
                }
            }
        });

        // need to receive events on all parts. if part is an editor will register
        // to receive propery changes.
        window.getActivePage().addPartListener(this);
    }

    /**
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated( IWorkbenchPart thePart ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop( IWorkbenchPart thePart ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed( IWorkbenchPart thePart ) {
        if (thePart instanceof IEditorPart) {
            thePart.removePropertyListener(this);
            updateState();
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated( IWorkbenchPart thePart ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened( IWorkbenchPart thePart ) {
        if (thePart instanceof IEditorPart) {
            addPropertyChangeListener((IEditorPart)thePart);
        }
    }

    /**
     * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
     */
    public void propertyChanged( Object theSource,
                                 int thePropId ) {
        if ((theSource instanceof IEditorPart) && (thePropId == IEditorPart.PROP_DIRTY)) {
            updateState();
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction theAction,
                                  ISelection theSelection ) {
        // need to process these since the plugin manifest for this action may enable on selection.
        // this is a way to set the appropriate enabled state.
        updateState();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction theAction ) {
        IWorkbenchPage page = window.getActivePage();

        if (page != null) {
            page.saveAllEditors(false);
        }

        updateState();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
     */
    public void runWithEvent( IAction theAction,
                              Event theEvent ) {
        run(theAction);
    }

    /**
     * Updates availability depending on number of targets that need saving.
     */
    protected void updateState() {
        IWorkbenchPage page = window.getActivePage();
        action.setEnabled((page != null) && (page.getDirtyEditors().length > 0));
    }
}

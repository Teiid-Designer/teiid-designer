/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @since 5.0
 */
public class CloneProjectAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private final CloneProjectAction2 delegate;

    /**
     * 
     * @since 5.0
     */
    public CloneProjectAction() {
        this.delegate = new CloneProjectAction2();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        this.delegate.run();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        this.delegate.selectionChanged(null, selection);
        action.setEnabled(this.delegate.isEnabled());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    public void init( IWorkbenchWindow window ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    @Override
    public void init( IViewPart view ) {
        // nothing to do
    }

}

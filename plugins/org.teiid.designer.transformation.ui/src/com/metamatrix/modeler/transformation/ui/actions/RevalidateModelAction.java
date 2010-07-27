/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * RevalidateModelAction
 */
public class RevalidateModelAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private ModelResource modelResource;

    /**
     * Construct an instance of RevalidateModelAction.
     */
    public RevalidateModelAction() {
        super();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {
                revalidate();
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }
    }

    void revalidate() {
        // swjTODO: implement revalidate for the selected ModelResource
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        Object selectedObject = SelectionUtilities.getSelectedObject(selection);
        boolean enable = false;
        if (selectedObject instanceof IResource && ModelUtilities.isModelFile((IResource)selectedObject)) {
            try {
                this.modelResource = ModelUtil.getModelResource(((IFile)selectedObject), false);
                if (ModelUtilities.isVirtual(this.modelResource)) {
                    enable = true;
                }
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            }
        }
        action.setEnabled(enable);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
    }

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init( IViewPart view ) {
    }

}

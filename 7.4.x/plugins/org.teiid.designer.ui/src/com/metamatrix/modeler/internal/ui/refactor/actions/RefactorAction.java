/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor.actions;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.RefactorCommand;
import com.metamatrix.modeler.core.refactor.ResourceRefactorCommand;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoManager;
import com.metamatrix.modeler.internal.ui.refactor.SaveModifiedResourcesDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * MoveRefactorAction
 */
public abstract class RefactorAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private static final String READ_ONLY_TITLE = UiConstants.Util.getString("RefactorAction.readonlyTitle"); //$NON-NLS-1$

    protected ISelection selection;
    protected IResource resSelectedResource;
    protected IAction action;
    protected IWorkbenchWindow window;
    private IStatus status;

    /**
     * @return the Label to use for UNDO
     */
    protected abstract String getUndoLabel();

    protected boolean doResourceCleanup() {
        boolean bResult = false;

        if (ModelEditorManager.getDirtyResources().size() > 0) {

            SaveModifiedResourcesDialog pnlSave = new SaveModifiedResourcesDialog(getShell());
            pnlSave.open();

            bResult = (pnlSave.getReturnCode() == Window.OK);
        } else {
            bResult = true;
        }

        return bResult;
    }

    public RefactorUndoManager getRefactorUndoManager() {

        ModelerActionService mas = (ModelerActionService)UiPlugin.getDefault().getActionService(window.getActivePage());
        return mas.getRefactorUndoManager();
    }

    protected void setResult( IStatus status ) {
        this.status = status;
    }

    protected IStatus getStatus() {
        return status;
    }

    public void init( IViewPart view ) {
        window = view.getSite().getWorkbenchWindow();
    }

    public void init( IWorkbenchWindow iww ) {
        window = iww;
    }

    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        super.selectionChanged(action, selection);
        this.action = action;
        this.selection = selection;

        setEnabledState();
    }

    protected void setEnabledState() {
        boolean enable = false;

        if (selection != null && SelectionUtilities.isSingleSelection(selection)
            && SelectionUtilities.isAllIResourceObjects(selection)) {
            IResource resTemp = (IResource)SelectionUtilities.getSelectedIResourceObjects(selection).get(0);

            if (!ModelUtil.isIResourceReadOnly(resTemp) && !(resTemp instanceof IProject)
                && ModelUtilities.isModelProjectResource(resTemp)) {
                enable = true;
                resSelectedResource = resTemp;
            }
        }
        
        if (action.isEnabled() != enable) {
            action.setEnabled(enable);
        }
    }

    protected IProgressMonitor executeCommand( final RefactorCommand command ) {

        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {
                IStatus status = command.execute(monitor);
                setResult(status);
            }
        };

        IProgressMonitor monitor = new NullProgressMonitor();

        // start the txn
        ModelerCore.startTxn(true, false, getUndoLabel(), this);

        try {
            // run the operation
            operation.run(monitor);

        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());

        } finally {
            // commit the txn
            ModelerCore.commitTxn();
        }

        return monitor;
    }

    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    /**
     * @param rrCommand
     * @return true if there are no read-only dependents or user has authorized refactor
     */
    protected boolean checkDependentStatus( ResourceRefactorCommand rrCommand,
                                            IResource res ) {
        // make sure we can actually move the file:
        List readOnlys = rrCommand.getReadOnlyDependentResources();
        if (!readOnlys.isEmpty()) {
            String msg = UiConstants.Util.getString("RefactorAction.readonlyResources", res.getName()); //$NON-NLS-1$

            // prompt user to continue. If they do not want to, return, else
            // continue on.
            if (!ListMessageDialog.openWarningQuestion(getShell(),
                                                       READ_ONLY_TITLE,
                                                       null,
                                                       msg,
                                                       readOnlys,
                                                       new ResourceLabelProvider())) {
                return false;
            } // endif -- cancel or not
        } // endif -- anything read only

        return true;
    }

    static class ResourceLabelProvider extends ModelLabelProvider {
        @Override
        public String getText( Object element ) {
            // since we are in a list, not a tree, show full path:
            if (element instanceof IResource) {
                return ((IResource)element).getFullPath().toString();
            } // endif
            return super.getText(element);
        }
    } // endclass ResourceLabelProvider
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The <code>AbstractUndoRedoAction</code> class is the base class for the global undo and redo actions.
 * 
 * @since 5.3.3
 */
public abstract class AbstractUndoRedoAction extends AbstractAction implements IMenuListener {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private IUndoManager undoMgr;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    protected AbstractUndoRedoAction() {
        super(UiPlugin.getDefault());

        // initial state
        updateState(getUndoManager());
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     * @since 5.5
     */
    @Override
    protected void doRun() {
        // the undo mgr can be null if an accelerator key was used instead of a menu
        if (this.undoMgr == null) {
            this.undoMgr = getUndoManager();
        }
        
        // need to enable/disable menu here in case an accerlator key was used instead of a menu
        updateState(this.undoMgr);

        if (!isEnabled()) {
            this.undoMgr = null;
            return;
        }

        final IUndoManager finalUndoMgr = this.undoMgr;

        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    performAction(finalUndoMgr, monitor);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(runnable);

        try {
            IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            new ProgressMonitorDialog(window.getShell()).run(false, false, op);
        } catch (InvocationTargetException e) {
            String title = UiConstants.Util.getString("AbstractUndoRedoAction.errorTitle"); //$NON-NLS-1$
            String message = UiConstants.Util.getString("AbstractUndoRedoAction.errorMsg"); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
            MessageDialog.openError(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
        } catch (InterruptedException e) {
            // cancelled
        } finally {
            this.undoMgr = null;
        }

    }

    /**
     * Obtains the edit menu.
     * 
     * @return the edit menu or <code>null</code>
     * @since 5.5.3
     */
    protected IMenuManager getEditMenu() {
        IWorkbenchPage page = UiUtil.getWorkbenchPage();

        if (page != null) {
            ModelerActionService actionService = (ModelerActionService)UiPlugin.getDefault().getActionService(page);
            return actionService.getEditMenu();
        }

        return null;
    }

    /**
     * Obtains the current <code>IUndoManager</code>.
     * 
     * @return the undo manager or <code>null</code>
     * @since 5.5.3
     */
    private IUndoManager getUndoManager() {
        IUndoManager result = null;
        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();

        if (part != null) {
            result = (IUndoManager)part.getAdapter(IUndoManager.class);
        }

        if (result == null) {
            result = ModelerUndoManager.getInstance();
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     * @since 5.5
     */
    public void menuAboutToShow(IMenuManager manager) {
        // when the menu is shown the current part temporarily loses focus to the menu so we need to save undo mgr to use to
        // when we perform the action
        this.undoMgr = getUndoManager();
        updateState(this.undoMgr);
    }

    /**
     * Performs the undo or redo action.
     * 
     * @param undoMgr the current undo/redo manager
     * @param monitor the monitor to update progress
     * @since 5.5.3
     */
    protected abstract void performAction(IUndoManager undoMgr,
                                          IProgressMonitor monitor);

    /**
     * Updates the action text, tooltip, and enabled state.
     * 
     * @param undoMgr the current undo/redo manager or <code>null</code>
     * @since 5.5.3
     */
    protected abstract void updateState(IUndoManager undoMgr);
}

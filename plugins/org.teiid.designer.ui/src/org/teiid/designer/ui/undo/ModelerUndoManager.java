/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.undo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.transaction.Undoable;
import org.teiid.designer.core.transaction.UndoableListener;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ModelerUndoManager is the manager of all UndoableEdits that are generated from ModelerCore.
 *
 * @since 8.0
 */
public class ModelerUndoManager implements UndoableListener, IUndoManager {

    private class ModelerUndoOperation extends AbstractOperation {

        private final Undoable edit;

        /**
         * Create a new operation
         *
         * @param edit
         */
        public ModelerUndoOperation(Undoable edit) {
            super(edit.getPresentationName());
            this.edit = edit;
            addContext(undoContext);
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
            // Changes executed as modelling is processed, so executing one has no
            // effect.
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = null;

            // Start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Redo Edit", this); //$NON-NLS-1$

            try {
                // redo this edit and put it on the undo stack
                edit.redo();
                status = Status.OK_STATUS;
            }
            catch (Exception e) {
                String message = UiConstants.Util.getString("ModelerUndoManager.redoErrorMessage", edit.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
                status = IOperationHistory.OPERATION_INVALID_STATUS;

                throw new ExecutionException(message);
            }
            finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (status != null && status.isOK()) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
                fireEvent();
            }

            return status;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = null;

            // Start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Undo Edit", this); //$NON-NLS-1$

            try {
                edit.undo();
                // insure editors are opened for all changed resources
                openAllEditors(edit.getResources());
                status = Status.OK_STATUS;
            }
            catch (Exception e) {
                String message = UiConstants.Util.getString("ModelerUndoManager.undoErrorMessage", edit.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
                status = IOperationHistory.OPERATION_INVALID_STATUS;

                throw new ExecutionException(message);
            }
            finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (status != null && status.isOK()) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
                fireEvent();
            }

            return status;
        }
    }

    // ============================================
    // Static Variables

    private static ModelerUndoManager instance = new ModelerUndoManager();

    // ============================================
    // Static Methods

    /**
     * Get the singleton instance
     * 
     * @return instance
     */
    public static ModelerUndoManager getInstance() {
        return instance;
    }

    // ============================================
    // Instance Variables

    /**
     * The registered modeller listeners.
     */
    private ListenerList modellerUndoListeners;

    /**
     * The operation history being used to store the undo history.
     */
    private IOperationHistory operationHistory;

    /**
     * The undo context for this modeller undo manager.
     */ 
    private IUndoContext undoContext;

    // ============================================
    // Constructors

    /*
     * Create a ModelerUndoManager for the specified WorkspaceController
     * @param controller the WorkspaceController which provides the event channel for this class
     */
    private ModelerUndoManager() {

        operationHistory = OperationHistoryFactory.getOperationHistory();
        modellerUndoListeners = new ListenerList(ListenerList.IDENTITY);

        try {
            Container modelContainer = ModelerCore.getModelContainer();

            undoContext = (IUndoContext) ModelerCore.getWorkspace().getAdapter(IUndoContext.class);
            modelContainer.addUndoableEditListener(this);

        } catch (CoreException e) {
            UiConstants.Util.log(e);
        }
    }

    // ============================================
    // Methods

    /**
     * Add undo listener
     * 
     * @param listener
     */
    public void addUndoListener( ModelerUndoListener listener ) {
        modellerUndoListeners.add(listener);
    }

    /**
     * Remove undo listener
     * 
     * @param listener
     */
    public void removeUndoListener( ModelerUndoListener listener ) {
        modellerUndoListeners.remove(listener);
    }

    /**
     * @return undo context
     */
    public IUndoContext getUndoContext() {
        return undoContext;
    }

    @Override
    public boolean canRedo() {
        return OperationHistoryFactory.getOperationHistory().canRedo(undoContext);
    }

    @Override
    public boolean canUndo() {
        return OperationHistoryFactory.getOperationHistory().canUndo(undoContext);
    }

    @Override
    public void redo(IProgressMonitor monitor) throws ExecutionException {
        if (! canRedo())
            return;

        try {
            monitor.beginTask("Starting redo operation", 1); //$NON-NLS-1$
            OperationHistoryFactory.getOperationHistory().redo(getUndoContext(), null, null);    
        }
        finally {
            monitor.done();
        }
    }

    @Override
    public void undo(IProgressMonitor monitor) throws ExecutionException {
        if (! canUndo())
            return;

        try {
            monitor.beginTask("Starting redo operation", 1); //$NON-NLS-1$
            OperationHistoryFactory.getOperationHistory().undo(getUndoContext(), null, null);    
        }
        finally {
            monitor.done();
        }
    }

    @Override
    public void process( Undoable edit ) {
        if (!edit.canUndo()) {
            return;
        }

        ModelerUndoOperation operation = new ModelerUndoOperation(edit);
        operationHistory.add(operation);

        fireEvent();
    }

    private void fireEvent() {
        EventObject event = new EventObject(getInstance());

        for (Object listener : modellerUndoListeners.getListeners()) {
            ((ModelerUndoListener) listener).processEvent(event);
        }
    }

    private void openAllEditors( Collection resources ) {
        List changedResources = new ArrayList(resources);
        Resource nextRes = null;
        ModelResource mr = null;
        for (Iterator iter = changedResources.iterator(); iter.hasNext();) {
            nextRes = (Resource)iter.next();
            mr = ModelUtilities.getModelResource(nextRes, true);
            if (mr != null) {
                ModelEditorManager.activate(mr, true);
            }
        }
    }

    @Override
    public String getRedoLabel() {
        // Nothing to do
        return null;
    }

    @Override
    public String getUndoLabel() {
        // Nothing to do
        return null;
    }
}
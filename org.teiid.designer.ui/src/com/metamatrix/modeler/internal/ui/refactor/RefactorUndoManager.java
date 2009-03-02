/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

import com.metamatrix.modeler.core.refactor.RefactorCommand;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * RefactorUndoManager is the manager of all ModelRefactorCommands created in a session.
 */
public class RefactorUndoManager {

    private static final String UNDO_LABEL = UiConstants.Util.getString("RefactorUndoManager.undoLabel"); //$NON-NLS-1$
    private static final String UNDO_TOOLTIP = UiConstants.Util.getString("RefactorUndoManager.undoTooltip"); //$NON-NLS-1$
    private static final String REDO_LABEL = UiConstants.Util.getString("RefactorUndoManager.redoLabel"); //$NON-NLS-1$
    private static final String REDO_TOOLTIP = UiConstants.Util.getString("RefactorUndoManager.redoTooltip"); //$NON-NLS-1$
    
    private static final String PRESENTATION_NAME = "Modeler Refactor Undo Manager";  //$NON-NLS-1$
    
    private static RefactorUndoManager instance = new RefactorUndoManager();
    
    private Stack undoStack = new Stack();
    private Stack redoStack = new Stack();
    private ArrayList listeners = new ArrayList();
    
    // 
    // Defect 23140 - need to disable this manager because we don't allow Refactor actions (i.e. rename) in Dimension
    // So let's create a enabled boolean that checks for product characteristics
    private boolean enabled = !ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric();
    
    // ============================================
    // Static Methods

    public static RefactorUndoManager getInstance() {
        return instance;
    }
    
    /**
     * Construct an instance of RefactorUndoManager. Instances of the RefactorUndoManager are owned by the ModelerActionService.
     */
    private RefactorUndoManager() {
    }
    
    public void addCommand(RefactorCommand command) {
        if( enabled ) {
            undoStack.push(command);
            redoStack.clear();
            fireEvent();
            logStacks("addCommand");  //$NON-NLS-1$
        }
    }

    public String getUndoLabel() {
        if ( ! undoStack.isEmpty() ) {
            return UNDO_LABEL + ' ' + ((RefactorCommand) undoStack.peek()).getLabel();
        }
        return UNDO_LABEL;
    }

    public String getUndoTooltip() {
        String result = null;
        if ( ! undoStack.isEmpty() ) {
            result = UNDO_LABEL + ' ' + ((RefactorCommand) undoStack.peek()).getDescription();
        }
        if ( result != null && result.length() > 0 ) {
            return result;
        }
        return  UNDO_TOOLTIP;
    }

    public String getRedoLabel() {
        if ( ! redoStack.isEmpty() ) {
            return REDO_LABEL + ' ' + ((RefactorCommand) redoStack.peek()).getLabel();
        }
        return REDO_LABEL;
    }

    public String getRedoTooltip() {
        String result = null;
        if ( ! redoStack.isEmpty() ) {
            result = REDO_LABEL + ' ' + ((RefactorCommand) redoStack.peek()).getDescription();
        }
        if ( result != null && result.length() > 0 ) {
            return result;
        }
        return  REDO_TOOLTIP;
    }

    public boolean canUndo() {
        if( enabled ) {
            if ( ! undoStack.isEmpty() ) {
                logStacks("canUndo");  //$NON-NLS-1$
                return ((RefactorCommand) undoStack.peek()).canUndo();
            }
        }
        return false;
    }


    public boolean canRedo() {
        if( enabled ) {
            if ( ! redoStack.isEmpty() ) {
                logStacks("canRedo");  //$NON-NLS-1$
                return ((RefactorCommand) redoStack.peek()).canRedo();
            }
        }
        return false;
    }
    
    public Collection undo() {
        if( enabled ) {
            logStacks("undo");  //$NON-NLS-1$
            if ( ! undoStack.isEmpty() ) {
                RefactorCommand command = (RefactorCommand) undoStack.pop();
                undoCommand(command);
                redoStack.push(command);
                fireEvent();
                logStacks("Post-undo");  //$NON-NLS-1$
                return command.getResult();
            }
            logStacks("Post-undo");  //$NON-NLS-1$
        }
        return Collections.EMPTY_LIST;
    }
    
    public Collection redo() {
        if( enabled ) {
            logStacks("redo");  //$NON-NLS-1$
            if ( ! redoStack.isEmpty() ) {
                RefactorCommand command = (RefactorCommand) redoStack.pop();
                redoCommand(command);
                undoStack.push(command);
                fireEvent();
                logStacks("Post-redo");  //$NON-NLS-1$
                return command.getResult();
            }
            logStacks("Post-redo");  //$NON-NLS-1$
        }
        return Collections.EMPTY_LIST;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        fireEvent();
        logStacks("clear");  //$NON-NLS-1$
    }
    
    public void addListener(RefactorUndoListener listener) {
        if( enabled ) {
            if ( listener != null && ! listeners.contains(listener) ) {
                listeners.add(listener);
            }
        }
    }
    
    public void removeListener(RefactorUndoListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent() {
        if( enabled ) {
            for ( Iterator iter = listeners.iterator() ; iter.hasNext() ; ) {
                ((RefactorUndoListener) iter.next()).stateChanged();
            }
        }
    }
    
    private void undoCommand(final RefactorCommand command) {
        IRunnableWithProgress runnable= new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    command.undo(monitor);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } 
            }
        };
        
        IRunnableWithProgress op= new WorkspaceModifyDelegatingOperation(runnable);
        try {
            new ProgressMonitorDialog(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell()).run(false,
                                                                                                                      true,
                                                                                                                      op);
        } catch (InvocationTargetException e) {
            String title= UiConstants.Util.getString("RefactorUndoManager.undoError.title"); //$NON-NLS-1$
            String message= UiConstants.Util.getString("RefactorUndoManager.undoError.message"); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
            MessageDialog.openError(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
        } catch (InterruptedException e) {
            // cancelled
        }
    }

    private void redoCommand(final RefactorCommand command) {
        IRunnableWithProgress runnable= new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    command.redo(monitor);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } 
            }
        };
        
        IRunnableWithProgress op= new WorkspaceModifyDelegatingOperation(runnable);
        try {
            new ProgressMonitorDialog(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell()).run(false,
                                                                                                                      true,
                                                                                                                      op);
        } catch (InvocationTargetException e) {
            String title= UiConstants.Util.getString("RefactorUndoManager.redoError.title"); //$NON-NLS-1$
            String message= UiConstants.Util.getString("RefactorUndoManager.redoError.message"); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
            MessageDialog.openError(UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
        } catch (InterruptedException e) {
            // cancelled
        }
    }
    
    private void logStacks(String actionName) {
        if ( UiConstants.Util.isDebugEnabled("modelEditorUndoEvent") ) { //$NON-NLS-1$
            String message = "  >> RefactorUndoManager." + actionName + "] called\n" + this.toString(); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(message); 
        }
    }
    
    @Override
    public synchronized String toString() {

        StringBuffer buf = new StringBuffer(
                                            PRESENTATION_NAME
                                            + "\n  Undo stack size = " + undoStack.size() + "\n  Redo stack size = " + redoStack.size()); //$NON-NLS-1$  //$NON-NLS-2$
        buf.append("\n === Undo Stack === ");  //$NON-NLS-1$
        Iterator iter = undoStack.iterator();
        int i = 0;
        while ( iter.hasNext() ) {
            RefactorCommand command = (RefactorCommand) iter.next();
            buf.append("\n   " + (i++) + ":  ");  //$NON-NLS-1$  //$NON-NLS-2$
            buf.append(command.getLabel());
            buf.append("  -- isUndoable = " + command.canUndo());  //$NON-NLS-1$

        }
        buf.append("\n === Redo Stack === ");  //$NON-NLS-1$
        iter = redoStack.iterator();
        i = 0;
        while ( iter.hasNext() ) {
            RefactorCommand command = (RefactorCommand) iter.next();
            buf.append("\n   " + (i++) + ":  ");  //$NON-NLS-1$  //$NON-NLS-2$
            buf.append(command.getLabel());
            buf.append("  -- isRedoable = " + command.canRedo());  //$NON-NLS-1$

        }
        return buf.toString();
    }
}

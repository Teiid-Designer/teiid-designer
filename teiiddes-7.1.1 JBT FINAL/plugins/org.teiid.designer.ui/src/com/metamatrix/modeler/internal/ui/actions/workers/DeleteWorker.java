/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.ObjectDeleteCommand;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.TransactionSettings;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 4.2
 */
public class DeleteWorker extends ModelObjectWorker {

    private static final String UNDO_TEXT = "DeleteAction.undoText"; //$NON-NLS-1$
    private static final String PLURAL_UNDO_TEXT = "DeleteAction.pluralUndoText"; //$NON-NLS-1$
    private static final String DELETE_ONE_TITLE_KEY = "DeleteWorker.deleteOneTitle"; //$NON-NLS-1$
    private static final String DELETE_MANY_TITLE_KEY = "DeleteWorker.deleteManyTitle"; //$NON-NLS-1$

    /** The child type descriptor. */
    private ModelResource modelResource;
    private EObject focusedObject;
    private IStatus status;
    private ObjectDeleteCommand odcDeleteCommand;

    /**
     * @since 4.2
     */
    public DeleteWorker( boolean enableAfterExecute ) {
        super(enableAfterExecute);
    }

    /**
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableState()
     * @since 4.2
     */
    @Override
    public boolean setEnabledState() {
        boolean enable = false;
        Object selection = getSelection();
        boolean foundResource = false;
        if (selection instanceof ISelection) {
            ISelection iSelection = (ISelection)selection;
            if (!iSelection.isEmpty() && !isReadOnly() && canLegallyEditResource()) {
                if (SelectionUtilities.isAllEObjects(iSelection)) {
                    enable = true;
                    // check each object, break out if false
                    for (Iterator iter = SelectionUtilities.getSelectedEObjects(iSelection).iterator(); iter.hasNext() && enable;) {
                        EObject obj = (EObject)iter.next();
                        // Check Read-Only, Check if Diagram (ask diagram), Check Helper
                        if (obj == null || ModelObjectUtilities.isReadOnly(obj)) {
                            enable = false; // and quit checking
                        } else {
                            if (obj instanceof Diagram) {
                                enable = DiagramHelperManager.canDelete((Diagram)obj);
                            } else {
                                enable = ModelObjectEditHelperManager.canDelete(obj); // and keep checking
                            }
                            // We got this far, now we need to cache up a focused object for opening an editor
                            // so it reveals the proper container (i.e. package diagram)
                            // Only do this once for effiency
                            if (!foundResource && enable) {
                                focusedObject = obj;
                                foundResource = true;
                                modelResource = ModelUtilities.getModelResourceForModelObject(obj);
                            }
                        }
                        if (!enable) break;
                    }
                }
            }
        }

        if (!enable) {
            modelResource = null;
            focusedObject = null;
        }

        if (UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage() != null) {
            IWorkbenchPart theActivePart = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActivePart();
            if (theActivePart != null) {
                // System.out.println(" >>> DeleteWorker.setEnabledState()  Active Part = " + theActivePart);
            }
        }
        return enable;
    }

    /**
     * @see com.metamatrix.ui.actions.IActionWorker#execute()
     * @since 4.2
     */
    @Override
    public boolean execute() {
        boolean successful = false;
        Object selection = getSelection();
        if (selection instanceof ISelection && canStillDelete((ISelection)selection) && canLegallyEditResource()) {

            if (odcDeleteCommand == null) {
                createObjectDeleteCommand(); // why is this done here unconditionally? it was already called once
            }

            // get the selected objects
            EObject[] eobjects = getSelectedEObjects();

            try {
                // run it
                executeCommand(odcDeleteCommand, eobjects, getTransactionSettings());

            } finally {

                // reinit
                focusedObject = null;
                modelResource = null;
            }
        }

        return successful;
    }

    public boolean canStillDelete( ISelection iSelection ) {
        boolean okToDelete = true;
        for (Iterator iter = SelectionUtilities.getSelectedEObjects(iSelection).iterator(); iter.hasNext() && okToDelete;) {
            okToDelete = ModelObjectEditHelperManager.canDelete(iter.next());
        }
        return okToDelete;
    }

    public void createObjectDeleteCommand() {
        // get the selected objects
        EObject[] eobjects = getSelectedEObjects();

        // create the Delete command, set the objects on it
        odcDeleteCommand = new ObjectDeleteCommand();

        odcDeleteCommand.setObjectsToDelete(eobjects);
    }

    public EObject[] getSelectedEObjects() {

        ISelection selection = (ISelection)getSelection();
        List selectedObjects = SelectionUtilities.getSelectedEObjects(selection);

        EObject[] eobjects = new EObject[selectedObjects.size()];
        int iCount = 0;
        Iterator it = selectedObjects.iterator();

        while (it.hasNext()) {
            EObject eoTemp = (EObject)it.next();
            eobjects[iCount++] = eoTemp;
        }

        return eobjects;
    }

    private void executeCommand( final ObjectDeleteCommand odcDeleteCommand,
                                 final EObject[] eObjects,
                                 TransactionSettings ts ) {

        if (odcDeleteCommand == null) {
            createObjectDeleteCommand();
        }

        String tempString = UiConstants.Util.getString(DELETE_ONE_TITLE_KEY);
        if (eObjects.length > 1) {
            tempString = UiConstants.Util.getString(DELETE_MANY_TITLE_KEY, eObjects.length);
        }
        final String deleteTitle = tempString;

        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {

                if (monitor instanceof SubProgressMonitor) {
                    ((SubProgressMonitor)monitor).getWrappedProgressMonitor().setTaskName(deleteTitle);
                }

                monitor.beginTask(CoreStringUtil.Constants.EMPTY_STRING, 100);
                monitor.worked(5);
                // execute the command
                IStatus status = odcDeleteCommand.execute(monitor);

                setResult(status);
            }
        };

        // start the txn
        ts.setSource(this);
        boolean started = ModelerCore.startTxn(ts.isSignificant(), ts.isUndoable(), ts.getDescription(), ts.getSource());

        boolean succeeded = false;

        try {
            ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

            // run the operation
            progressDialog.run(true, true, operation);
            if (!progressDialog.getProgressMonitor().isCanceled()) {
                succeeded = true;
            }

            if (!getStatus().isOK()) {
                UiConstants.Util.log(getStatus());
            }
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    void setResult( IStatus status ) {
        this.status = status;

    }

    private IStatus getStatus() {
        return status;
    }

    public TransactionSettings initTransactionSettings() {

        TransactionSettings ts = getTransactionSettings();

        ts = processCanUndoDelete(ts);
        ts = processDescription(ts);

        return ts;
    }

    private TransactionSettings processDescription( TransactionSettings ts ) {

        ts.setDescription(getUndoText(getSelectedEObjects()));

        return ts;
    }

    private String getUndoText( EObject[] selectedObjects ) {

        String description = null;
        if (selectedObjects.length == 1) {
            EObject obj = selectedObjects[0];
            String path = ModelerCore.getModelEditor().getModelRelativePath(obj).toString();
            description = UiConstants.Util.getString(UNDO_TEXT, path);
        } else {
            description = UiConstants.Util.getString(PLURAL_UNDO_TEXT, selectedObjects.length);
        }
        return description;
    }

    private TransactionSettings processCanUndoDelete( TransactionSettings ts ) {

        boolean bCanUndoDelete = false;
        Object selection = getSelection();

        if (selection instanceof ISelection) {
            ISelection iSelection = (ISelection)selection;
            if (!iSelection.isEmpty() && !isReadOnly() && canLegallyEditResource()) {
                if (SelectionUtilities.isSingleSelection(iSelection)) {
                    Object o = SelectionUtilities.getSelectedEObject(iSelection);
                    bCanUndoDelete = (o != null && ModelObjectEditHelperManager.canUndoCut(o));
                } else if (SelectionUtilities.isMultiSelection(iSelection)) {

                    List sourceEObjects = SelectionUtilities.getSelectedEObjects(iSelection);
                    bCanUndoDelete = true;

                    if (sourceEObjects.size() > 0) {
                        bCanUndoDelete = ModelObjectEditHelperManager.canUndoCut(sourceEObjects);
                    }
                }

                ts.setIsUndoable(bCanUndoDelete);
            }
        }

        return ts;
    }

    public void setModelResource( ModelResource modelResource ) {
        this.modelResource = modelResource;
    }

    public EObject getFocusedObject() {
        return this.focusedObject;
    }

    public ModelResource getModelResource() {
        return this.modelResource;
    }

    public ObjectDeleteCommand getObjectDeleteCommand() {
        return this.odcDeleteCommand;
    }
}

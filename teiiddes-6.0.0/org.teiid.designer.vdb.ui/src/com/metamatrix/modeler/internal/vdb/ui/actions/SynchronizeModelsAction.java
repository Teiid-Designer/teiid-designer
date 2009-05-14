/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.util.VdbEditUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * Action to automatically synchronize and save any out-of-synch models in a VDB file.
 * 
 * @since 4.2
 */
public class SynchronizeModelsAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(SynchronizeModelsAction.class);
    private static final String FILE_DOES_NOT_EXIST_TITLE = getString("fileDoesNotExist.title"); //$NON-NLS-1$
    private static final String FILE_DOES_NOT_EXIST_MSG_KEY = "fileDoesNotExist.message"; //$NON-NLS-1$
    private static final String FAILED_SYNCHRONIZATION_MSG_KEY = getString("failedSynchronization.message"); //$NON-NLS-1$
    private static final String VDB_IN_SYNCH_TITLE = getString("alreadyInSynch.title"); //$NON-NLS-1$
    private static final String VDB_IN_SYNCH_MESSAGE = getString("alreadyInSynch.message"); //$NON-NLS-1$
    static final String EXCEPTION_TITLE = getString("exception.title"); //$NON-NLS-1$
    static final String EXCEPTION_MESSAGE = getString("exception.message"); //$NON-NLS-1$
    private static final String READ_ONLY_FILE_MESSAGE = getString("readOnlyFile.title"); //$NON-NLS-1$
    private static final String VDB_IS_READ_ONLY_MESSAGE = "vdbIsReadOnly.message"; //$NON-NLS-1$

    private static boolean ADD_DEPENDENT_MODELS_DEFAULT = true;

    /**
     * @since 4.2
     */
    private static String getString( final String id ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.2
     */
    private static String getString( final String id,
                                     String arg ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + id, arg);
    }

    IFile selectedVDB;
    VdbEditingContext context;
    IEditorPart currentVdbEditor;
    boolean contextIsLocal = false;
    boolean successfulRefresh = false;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public SynchronizeModelsAction() {
        super();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        if (ModelUtil.isIResourceReadOnly(selectedVDB)) {
            String message = getString(VDB_IS_READ_ONLY_MESSAGE, selectedVDB.getName());
            MessageDialog.openError(null, READ_ONLY_FILE_MESSAGE, message);
        } else {
            successfulRefresh = false;
            UiBusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    try {
                        context = getCurrentVdbContext();

                        if (context == null) {
                            context = VdbEditPlugin.createVdbEditingContext(selectedVDB.getRawLocation());
                            context.open();
                            contextIsLocal = true;
                        }

                        // check the models in the VDB to make sure we should proceed
                        if (checkModels()) {

                            // final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                            final IRunnableWithProgress op = new IRunnableWithProgress() {
                                // public void execute(IProgressMonitor theMonitor) throws InvocationTargetException {
                                public void run( final IProgressMonitor theMonitor ) {
                                    theMonitor.beginTask("Refreshing Out of Sync Models", 100); //$NON-NLS-1$
                                    theMonitor.worked(50);
                                    refreshStaleModels(theMonitor);
                                    theMonitor.done();
                                    successfulRefresh = true;
                                }
                            };
                            try {
                                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
                            } catch (InterruptedException e) {
                            } catch (InvocationTargetException e) {
                                VdbUiConstants.Util.log(e.getTargetException());
                            }
                        }

                    } catch (final Exception err) {
                        VdbUiConstants.Util.log(err);
                        MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
                    }
                }
            });
            // Broke this editor save logic out of the runnable, becaues it logs it's own progress into
            // the workspace status bar.
            if (successfulRefresh) {
                completeSave();
            }
        }
    }

    VdbEditingContext getCurrentVdbContext() {
        VdbEditingContext vdbContext = null;

        IEditorReference[] editors = UiUtil.getWorkbenchPage().getEditorReferences();

        IEditorPart nextEditor = null;
        for (int i = 0; i < editors.length; i++) {
            nextEditor = editors[i].getEditor(false);
            if (nextEditor instanceof VdbEditor) {
                VdbEditor editor = (VdbEditor)nextEditor;
                IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
                final IFile file = input.getFile();
                if (file.equals(selectedVDB)) {
                    currentVdbEditor = nextEditor;
                    vdbContext = editor.getContext();
                }
            }
            if (vdbContext != null) break;
        }

        return vdbContext;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    this.selectedVDB = (IFile)obj;
                    enable = true;
                }
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

    void refreshStaleModels( final IProgressMonitor monitor ) {

        // reload each model that is stale
        Collection modelList = new ArrayList(this.context.getVirtualDatabase().getModels());
        int workValue = 10;
        for (Iterator iter = modelList.iterator(); iter.hasNext();) {
            final ModelReference modelReference = (ModelReference)iter.next();
            if (context.isStale(modelReference)) {
                // save off visibility to reset on new model reference
                boolean visible = modelReference.isVisible();
                // get the path
                final IFile file = VdbEditUtil.getFile(modelReference, selectedVDB.getProject());
                // remove the stale model
                final IPath vdbRefPath = new Path(modelReference.getModelLocation()).makeRelative();
                context.removeModel(vdbRefPath);
                // add the model back again
                Object[] addedModels = addModels(monitor, new Object[] {file});
                // set the visibility
                for (int i = 0; i < addedModels.length; ++i) {
                    ModelReference newReference = (ModelReference)addedModels[i];
                    if (modelReference.getModelLocation().equals(newReference.getModelLocation())) {
                        newReference.setVisible(visible);
                    }
                }
                monitor.worked(workValue);
                workValue += 10;
                if (workValue > 100) workValue = 10;
            }
        }
    }

    private void completeSave() {
        if (contextIsLocal) {
            // Save the context

            // Defect 15772 - Update sync state when Synchronize Models action called and VDB not open
            // Refresh the file since it was modified by the context
            try {
                context.save(null);
                // pass in null for monitor because we don't want an InterruptedException thrown
                // if the monitor has been cancelled. we still want to refresh since the file has changed.
                selectedVDB.refreshLocal(IResource.DEPTH_ZERO, null);
            } catch (final CoreException err) {
                WidgetUtil.showError(err.getMessage());
                VdbUiConstants.Util.log(err);
            } // endtry

            try {
                this.context.close();
            } catch (IOException err) {
                VdbUiConstants.Util.log(err);
                MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
            }
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage page = currentVdbEditor.getSite().getPage();
                    page.saveEditor(currentVdbEditor, false);
                    currentVdbEditor = null;
                    context = null;
                    contextIsLocal = false;
                }
            });
        }

    }

    /**
     * Checks for the following conditions on all ModelResources that are stale: 1) Must exist in the workspace 2) Must have been
     * validated since last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also,
     * if no models are stale, the method returns false.
     * 
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     */
    boolean checkModels() {
        boolean result = true;
        boolean containsStaleModels = false;
        Collection modelList = this.context.getVirtualDatabase().getModels();
        // Collection dirtyModels = ModelEditorManager.getDirtyResources();
        Collection staleFiles = new ArrayList();
        // iterate through the models, as long as result remains TRUE
        for (Iterator iter = modelList.iterator(); iter.hasNext() && result;) {
            final ModelReference modelReference = (ModelReference)iter.next();
            if (context.isStale(modelReference)) {
                containsStaleModels = true;
                final IFile file = VdbEditUtil.getFile(modelReference, selectedVDB.getProject());
                if (file == null) {
                    // Cannot find file in the workspace - cannot continue
                    final String message = getString(FILE_DOES_NOT_EXIST_MSG_KEY, modelReference.toString());
                    MessageDialog.openError(null, FILE_DOES_NOT_EXIST_TITLE, message);
                    result = false;
                } else {
                    // Call Check models with file list
                    staleFiles.add(file);
                    result = ModelUtilities.verifyWorkspaceValidationState(file, this, FAILED_SYNCHRONIZATION_MSG_KEY);
                }

            }
        }

        if (!containsStaleModels) {
            MessageDialog.openInformation(null, VDB_IN_SYNCH_TITLE, VDB_IN_SYNCH_MESSAGE);
            result = false;
        }
        return result;
    }

    /**
     * Adds the specified array of model IFile instances to the VDB in this objects VdbEditingContext.
     * 
     * @param modelFiles
     * @return
     * @since 4.2
     */
    private Object[] addModels( final IProgressMonitor monitor,
                                Object[] modelFiles ) {
        final ArrayList addedModels = new ArrayList();
        if (modelFiles != null && modelFiles.length > 0) {

            for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
                final IFile model = (IFile)modelFiles[ndx];

                // model has been previously added don't try adding again
                boolean addedPreviously = false;
                final List modelList = context.getVirtualDatabase().getModels();

                for (int size = modelList.size(), i = 0; i < size; ++i) {
                    if (model.getFullPath().toString().equals(((ModelReference)modelList.get(i)).getModelLocation())) {
                        addedPreviously = true;
                        break;
                    }
                }

                // skip adding this model if added previously
                if (addedPreviously) {
                    continue;
                }

                try {
                    // Get the relative path to this model within the workspace
                    final IPath pathInWorkspace = model.getFullPath().makeRelative();
                    // Add the model to the vdb
                    // final ModelReference ref = this.context.addModel(pathInWorkspace);
                    // addedModels.add(ref);
                    final ModelReference[] refs = this.context.addModel(monitor, pathInWorkspace, ADD_DEPENDENT_MODELS_DEFAULT);
                    for (int i = 0; i < refs.length; i++) {
                        addedModels.add(refs[i]);
                    }

                    // // Add any models that the selected models depend upon ...
                    // if (ADD_DEPENDENT_MODELS_DEFAULT) {
                    // // Add any dependent models to the vdb
                    // final ModelReference[] refs = this.context.addDependentModels(pathInWorkspace);
                    //
                    // for (int i = 0; i < refs.length; i++ ) {
                    // addedModels.add(refs[i]);
                    // }
                    // }
                } catch (final Exception err) {
                    VdbUiConstants.Util.log(err);

                    if (err instanceof VdbEditException) {
                        IStatus status = ((VdbEditException)err).getStatus();
                        ErrorDialog.openError(null, null, null, status);
                    } else {
                        WidgetUtil.showError(err.getLocalizedMessage());
                    }
                }
            }
        }
        return addedModels.toArray();
    }

}

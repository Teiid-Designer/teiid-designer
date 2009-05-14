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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.modeler.vdb.ui.util.VdbEditUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;

public class SynchronizeVdbAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(SynchronizeVdbAction.class);
    protected static final String FILE_DOES_NOT_EXIST_TITLE = getString("fileDoesNotExist.title"); //$NON-NLS-1$
    protected static final String FAILED_SYNCHRONIZATION_MSG = getString("failedSynchronization.message"); //$NON-NLS-1$
    protected static final String VDB_IN_SYNCH_TITLE = getString("alreadyInSynch.title"); //$NON-NLS-1$
    protected static final String VDB_IN_SYNCH_MESSAGE = getString("alreadyInSynch.message"); //$NON-NLS-1$
    protected static final String EXCEPTION_TITLE = getString("exception.title"); //$NON-NLS-1$
    protected static final String EXCEPTION_MESSAGE = getString("exception.message"); //$NON-NLS-1$
    protected static final String READ_ONLY_FILE_MESSAGE = getString("readOnlyFile.title"); //$NON-NLS-1$

    protected static final String VDB_IS_READ_ONLY_MESSAGE_ID = "vdbIsReadOnly.message"; //$NON-NLS-1$
    protected static final String FILE_DOES_NOT_EXIST_MSG_ID = "fileDoesNotExist.message"; //$NON-NLS-1$
    protected static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$

    protected boolean successfulRefresh = false;

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

    public SynchronizeVdbAction() {
        super();
        setImageDescriptor(VdbUiPlugin.getDefault().getImageDescriptor(VdbUiConstants.Images.SYNCRONIZE_VDB_ICON));
    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals(VDB_EXTENSION)) {
                    this.selectedVDB = (IFile)obj;
                    enable = true;
                }
            }
        }
        setEnabled(enable);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        if (ModelUtil.isIResourceReadOnly(selectedVDB)) {
            String message = getString(VDB_IS_READ_ONLY_MESSAGE_ID, selectedVDB.getName());
            MessageDialog.openError(null, READ_ONLY_FILE_MESSAGE, message);
        } else {
            successfulRefresh = false;
            contextIsLocal = false;
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

                        if (contextIsLocal) {
                            if (checkModels()) {
                                final IRunnableWithProgress op = new IRunnableWithProgress() {
                                    public void run( final IProgressMonitor theMonitor ) {
                                        theMonitor.beginTask("Refreshing Out of Sync Models", 100); //$NON-NLS-1$
                                        theMonitor.worked(50);
                                        // ----------------------------------------------------------
                                        // Defect 22248 - Had to replace the refresh methods in this action with a new Utility
                                        // method
                                        // that mimiced what the VdbEditorModelComposite panel was doing.
                                        // ----------------------------------------------------------
                                        VdbEditUtil.refreshAllOutOfSyncModels(context, this, selectedVDB.getProject());

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
                        } else if (currentVdbEditor != null) {
                            // ---------------------------------------------------------------
                            // Defect 22305 was a result of this action performing the synching from outside
                            // the Vdb Editor. Needed to access the editor directly and call the same sync method
                            // behind the Synchronize All button action inside the editor.
                            // This prevents a possible IllegalStateExceptions
                            // ---------------------------------------------------------------
                            ((VdbEditor)currentVdbEditor).synchronizeVdb(true);
                        }

                    } catch (final Exception err) {
                        VdbUiConstants.Util.log(err);
                        MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
                    }
                }
            });
            // Broke this editor save logic out of the runnable, becaues it logs it's own progress into
            // the workspace status bar.
            // for Defect 22305, only do a save if this is a local context
            if (contextIsLocal && successfulRefresh) {
                completeSave();
            }
        }
    }

    protected VdbEditingContext getCurrentVdbContext() {
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

    protected void completeSave() {
        if (contextIsLocal) {
            // Save the context

            // Defect 15772 - Update sync state when Synchronize Models action called and VDB not open
            // Refresh the file since it was modified by the context
            try {
                context.setModified();
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
        Collection staleFiles = new ArrayList();
        // iterate through the models, as long as result remains TRUE
        for (Iterator iter = modelList.iterator(); iter.hasNext() && result;) {
            final ModelReference modelReference = (ModelReference)iter.next();
            if (context.isStale(modelReference)) {
                containsStaleModels = true;
                final IFile file = VdbEditUtil.getFile(modelReference, selectedVDB.getProject());
                if (file == null) {
                    // Cannot find file in the workspace - cannot continue
                    final String message = getString(FILE_DOES_NOT_EXIST_MSG_ID, modelReference.toString());
                    MessageDialog.openError(null, FILE_DOES_NOT_EXIST_TITLE, message);
                    result = false;
                } else {
                    // Call Check models with file list
                    staleFiles.add(file);
                    result = ModelUtilities.verifyWorkspaceValidationState(file, this, FAILED_SYNCHRONIZATION_MSG);
                }

            }
        }

        if (!containsStaleModels) {
            MessageDialog.openInformation(null, VDB_IN_SYNCH_TITLE, VDB_IN_SYNCH_MESSAGE);
            result = false;
        }
        return result;
    }

    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    /**
     * @param selection
     * @return
     */
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    result = true;
                }
            }
        }
        return result;
    }
}

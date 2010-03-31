/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

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

    protected boolean successfulRefresh = false;

    IFile selectedVDB;
    Vdb vdb;
    IEditorPart currentVdbEditor;
    boolean contextIsLocal = false;

    public SynchronizeVdbAction() {
        super();
        setImageDescriptor(VdbUiPlugin.getDefault().getImageDescriptor(VdbUiConstants.Images.SYNCRONIZE_VDB_ICON));
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

    protected void completeSave() {
        if (contextIsLocal) {
            // Save the context

            // Defect 15772 - Update sync state when Synchronize Models action called and VDB not open
            // Refresh the file since it was modified by the context
            try {
                vdb.save(null);
                // pass in null for monitor because we don't want an InterruptedException thrown
                // if the monitor has been cancelled. we still want to refresh since the file has changed.
                selectedVDB.refreshLocal(IResource.DEPTH_ZERO, null);
            } catch (final CoreException err) {
                WidgetUtil.showError(err.getMessage());
                VdbUiConstants.Util.log(err);
            } // endtry

            this.vdb.close();
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage page = currentVdbEditor.getSite().getPage();
                    page.saveEditor(currentVdbEditor, false);
                    currentVdbEditor = null;
                    vdb = null;
                    contextIsLocal = false;
                }
            });
        }

    }

    protected Vdb getCurrentVdb() {
        Vdb vdb = null;

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
                    vdb = editor.getVdb();
                }
            }
            if (vdb != null) break;
        }

        return vdb;
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
                        vdb = getCurrentVdb();
                        if (vdb == null) {
                            vdb = new Vdb(selectedVDB.getFullPath());
                            contextIsLocal = true;
                        }
                        if (contextIsLocal) vdb.synchronize();
                        else if (currentVdbEditor != null) ((VdbEditor)currentVdbEditor).synchronizeVdb(true);
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
}

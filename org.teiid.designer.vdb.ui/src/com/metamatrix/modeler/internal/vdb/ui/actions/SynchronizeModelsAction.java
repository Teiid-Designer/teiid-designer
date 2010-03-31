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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * Action to automatically synchronize and save any out-of-synch models in a VDB file.
 * 
 * @since 4.2
 */
public class SynchronizeModelsAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(SynchronizeModelsAction.class);
    static final String EXCEPTION_TITLE = getString("exception.title"); //$NON-NLS-1$
    static final String EXCEPTION_MESSAGE = getString("exception.message"); //$NON-NLS-1$
    private static final String READ_ONLY_FILE_MESSAGE = getString("readOnlyFile.title"); //$NON-NLS-1$
    private static final String VDB_IS_READ_ONLY_MESSAGE = "vdbIsReadOnly.message"; //$NON-NLS-1$

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
    Vdb vdb;
    IEditorPart currentVdbEditor;
    boolean contextIsLocal = false;
    boolean successfulRefresh = false;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public SynchronizeModelsAction() {
        super();
    }

    private void completeSave() {
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
            }

            vdb.close();
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

    Vdb getCurrentVdb() {
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
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init( IViewPart view ) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
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
                        vdb = getCurrentVdb();

                        if (vdb == null) {
                            vdb = new Vdb(selectedVDB.getFullPath());
                            contextIsLocal = true;
                        }
                        vdb.synchronize();
                        successfulRefresh = true;
                    } catch (final Exception err) {
                        VdbUiConstants.Util.log(err);
                        MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
                    }
                }
            });
            // Broke this editor save logic out of the runnable, because it logs it's own progress into
            // the workspace status bar.
            if (successfulRefresh) {
                completeSave();
            }
        }
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
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 4.2
 */
public class ExecuteVdbAction extends ActionDelegate implements DqpUiConstants, IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExecuteVdbAction.class);

    /**
     * @since 4.3
     */
    private static String getString( final String id ) {
        return UTIL.getStringOrKey(I18N_PREFIX + id);
    }

    private IFile selectedVDB;

    // ConnectorBindingsDialog dialog;

    private IStatus canExecute;

    private boolean allowUserInput = true;

    /**
     * @since 4.2
     */
    public ExecuteVdbAction() {
        // default behavior is to allow user interaction
        this(true);
    }

    /**
     * This contructor allows the user to disable user inputs
     * 
     * @param allowUserInteraction flag to allow or disallow user input
     */
    public ExecuteVdbAction( final boolean allowUserInteraction ) {
        super();
        this.allowUserInput = allowUserInteraction;
    }

    /**
     * This runs the default VdbExecutor.execute method
     */
    private void executeVdb() {
        // should be in an executable state here but check to be safe
    	// TODO: Replace
    }

    public IStatus getCanExecuteStatus() {
        return this.canExecute;
    }

    private Vdb getCurrentVdb() {
        Vdb result = null;
        final IEditorReference[] editors = UiUtil.getWorkbenchPage().getEditorReferences();
        IEditorPart nextEditor = null;

        for (int i = 0; i < editors.length; ++i) {
            nextEditor = editors[i].getEditor(false);

            if (nextEditor instanceof VdbEditor) {
                final VdbEditor editor = (VdbEditor)nextEditor;
                final IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
                final IFile file = input.getFile();

                if (file.equals(this.selectedVDB)) {
                    result = editor.getVdb();
                    break;
                }
            }
        }

        if (result == null) try {
            result = new Vdb(this.selectedVDB, new NullProgressMonitor());
        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            MessageDialog.openInformation(null, getString("editorContextError.title"), getString("editorContextError.message")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return result;
    }

    File getSelectedVdbFile() {
        return (this.selectedVDB == null) ? null : this.selectedVDB.getLocation().toFile();
    }

    private Shell getShell() {
        return DqpUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     * @since 4.2
     */
    public void init( final IViewPart view ) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.2
     */
    public void init( final IWorkbenchWindow window ) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.2
     */
    @Override
    public void run( final IAction action ) {
        setupVdbForExecution();

        executeVdb();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        boolean enable = false;

        if (!SelectionUtilities.isMultiSelection(selection)) {
            final Object obj = SelectionUtilities.getSelectedObject(selection);

            if (obj instanceof IFile) if (CoreStringUtil.endsWithIgnoreCase(((IFile)obj).getName(), ModelerCore.VDB_FILE_EXTENSION)) {
                setSelectedVdbFile((IFile)obj);
                enable = true;
            }
        }

        action.setEnabled(enable);
    }

    void setCanExecute( final IStatus theExecuteStatus ) {
        this.canExecute = theExecuteStatus;
    }

    public void setSelectedVdbFile( final IFile vdbFile ) {
        this.selectedVDB = vdbFile;
    }

    public void setupVdbForExecution() {
        final Vdb vdb = getCurrentVdb();

        CoreArgCheck.isNotNull(vdb);

        // if not saved show dialog and exit
        if (vdb.isModified()) {
            // show error dialog
            if (this.allowUserInput) MessageDialog.openError(getShell(), getString("unsavedVdbDialog.title"), //$NON-NLS-1$
                                                             getString("unsavedVdbDialog.msg") //$NON-NLS-1$
            );
            // Set error status
            setCanExecute(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, -1, getString("unsavedVdbDialog.msg"), null)); //$NON-NLS-1$
            return;
        }


        // TODO: REPLACE
    }
}

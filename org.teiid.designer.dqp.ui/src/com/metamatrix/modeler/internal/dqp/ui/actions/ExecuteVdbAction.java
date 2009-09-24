/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.dqp.internal.execution.VdbExecutionValidatorImpl;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.dialogs.ConnectorBindingsDialog;
import com.metamatrix.modeler.internal.vdb.ui.editor.VdbEditor;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.2
 */
public class ExecuteVdbAction extends ActionDelegate
    implements DqpUiConstants, IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExecuteVdbAction.class);

    /**
     * @since 4.3
     */
    private static String getString( final String id ) {
        return UTIL.getStringOrKey(I18N_PREFIX + id);
    }

    private IFile selectedVDB;

    ConnectorBindingsDialog dialog;

    private IStatus canExecute;

    private VdbExecutionValidator validator;

    VdbExecutor vdbExecutor;

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
    public ExecuteVdbAction( boolean allowUserInteraction ) {
        super();
        this.allowUserInput = allowUserInteraction;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.2
     */
    @Override
    public void run( IAction action ) {
        setupVdbForExecution();

        executeVdb();
    }

    public void setupVdbForExecution() {
        final VdbEditingContext vdbContext = getCurrentVdbContext();

        Assertion.isNotNull(vdbContext);
        Assertion.isInstanceOf(vdbContext, InternalVdbEditingContext.class, vdbContext.getClass().getName());

        // if not saved show dialog and exit
        if (vdbContext.isSaveRequired()) {
            // show error dialog
            if (this.allowUserInput) {
                MessageDialog.openError(getShell(), getString("unsavedVdbDialog.title"), //$NON-NLS-1$
                                        getString("unsavedVdbDialog.msg") //$NON-NLS-1$
                );
            }
            // Set error status
            setCanExecute(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, -1, getString("unsavedVdbDialog.msg"), null)); //$NON-NLS-1$
            return;
        }

        // executor for executing VDB and validating execution status
        vdbExecutor = new VdbExecutor(vdbContext, getVdbExecutionValidator());

        // check vdb execute status. exit if problem found.
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {
                setCanExecute(vdbExecutor.canExecute());
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, operation);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            UTIL.log(e.getTargetException());
            MessageDialog.openError(getShell(), getString("executorErrorDialog.title"), //$NON-NLS-1$
                                    getString("executorErrorDialog.msg") //$NON-NLS-1$
            );
            return;
        }

        // if not in an executable state, show dialog to allow user to fix any problems
        // (only if allowing user interaction)
        // dialog is shown for either error, warning, or info severity.
        if (!this.canExecute.isOK() && this.allowUserInput) {
            UiBusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    Shell shell = DqpUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                    dialog = new ConnectorBindingsDialog(shell, getSelectedVdbFile(), (InternalVdbEditingContext)vdbContext,
                                                         getVdbExecutionValidator());
                }
            });

            dialog.open();

            if (dialog.getReturnCode() == Window.OK) {
                // save VDB if necessary
                if (vdbContext.isSaveRequired()) {
                    vdbContext.save(new NullProgressMonitor());
                }

                // recheck the execution status. should be OK if the dialog OK button was clicked. but just make sure.
                try {
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, operation);
                } catch (InterruptedException e) {
                } catch (InvocationTargetException e) {
                    UTIL.log(e.getTargetException());
                    MessageDialog.openError(getShell(), getString("executorErrorDialog.title"), //$NON-NLS-1$
                                            getString("executorErrorDialog.msg") //$NON-NLS-1$
                    );
                    return;
                }
            }
        }
    }

    /**
     * This runs the default VdbExecutor.execute method
     */
    private void executeVdb() {
        // should be in an executable state here but check to be safe
        if (getCanExecuteStatus().getSeverity() != IStatus.ERROR) {
            vdbExecutor.execute(null);
        }
    }

    /**
     * run the VdbExecutor.execute method for initializing the connection only, and return a Vdb connection
     */
    public Connection getVdbConnection() {
        Connection connection = null;
        // should be in an executable state here but check to be safe
        if (getCanExecuteStatus().getSeverity() != IStatus.ERROR) {
            IStatus vdbConnectionStatus = this.vdbExecutor.execute(null, true);
            if (vdbConnectionStatus.isOK()) {
                connection = this.vdbExecutor.getSqlConnection().getConnection();
            }
        }
        return connection;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        boolean enable = false;

        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);

            if (obj instanceof IFile) {
                if (StringUtil.endsWithIgnoreCase(((IFile)obj).getName(), ModelerCore.VDB_FILE_EXTENSION)) {
                    setSelectedVdbFile((IFile)obj);
                    enable = true;
                }
            }
        }

        action.setEnabled(enable);
    }

    public void setSelectedVdbFile( IFile vdbFile ) {
        this.selectedVDB = vdbFile;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.2
     */
    public void init( IWorkbenchWindow window ) {
    }

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     * @since 4.2
     */
    public void init( IViewPart view ) {
    }

    File getSelectedVdbFile() {
        return (this.selectedVDB == null) ? null : this.selectedVDB.getLocation().toFile();
    }

    private VdbEditingContext getCurrentVdbContext() {
        VdbEditingContext result = null;
        IEditorReference[] editors = UiUtil.getWorkbenchPage().getEditorReferences();
        IEditorPart nextEditor = null;

        for (int i = 0; i < editors.length; ++i) {
            nextEditor = editors[i].getEditor(false);

            if (nextEditor instanceof VdbEditor) {
                VdbEditor editor = (VdbEditor)nextEditor;
                IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
                IFile file = input.getFile();

                if (file.equals(this.selectedVDB)) {
                    result = editor.getContext();
                    break;
                }
            }
        }

        if (result == null) {
            try {
                result = VdbEditPlugin.createVdbEditingContext(this.selectedVDB.getLocation());
                result.open();
            } catch (final Exception err) {
                VdbUiConstants.Util.log(err);
                MessageDialog.openInformation(null,
                                              getString("editorContextError.title"), getString("editorContextError.message")); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return result;
    }

    private Shell getShell() {
        return DqpUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    void setCanExecute( IStatus theExecuteStatus ) {
        this.canExecute = theExecuteStatus;
    }

    public IStatus getCanExecuteStatus() {
        return this.canExecute;
    }

    public void setVdbExecutionValidator( VdbExecutionValidator validator ) {
        this.validator = validator;
    }

    public VdbExecutionValidator getVdbExecutionValidator() {
        if (this.validator == null) {
            this.validator = new VdbExecutionValidatorImpl();
        }
        return this.validator;
    }
}

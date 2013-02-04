/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.undo.CreateFolderOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;

/**
 *  Utility for creating folder within a project
 */
public class FolderUtil {
    
    public static final String THIS_CLASS = "FolderUtil"; //$NON-NLS-1$

    private static String getString( String key ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key);
    }
    
    private static String getString( String key,
                                     String parameter ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key, parameter);
    }

    /**
     * Create a folder with the specified name within the supplied project
     * @param shell the supplied shell  
     * @param project the supplied project
     * @param name desired name of the folder
     */
    public static void createFolder(final Shell shell, IProject project, String name) {
        final IPath containerPath = project.getFullPath();
        IPath newFolderPath = containerPath.append(name);
        final IFolder newFolderHandle = ModelerCore.getWorkspace().getRoot().getFolder(newFolderPath);
        
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                AbstractOperation op;
                op = new CreateFolderOperation(
                    newFolderHandle, null, false, null,
                    getString("errorCreatingNewFolderTitle")); //$NON-NLS-1$
                try {
                    // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
                    // directly execute the operation so that the undo state is
                    // not preserved.  Making this undoable can result in accidental
                    // folder (and file) deletions.
                    op.execute(monitor, WorkspaceUndoUtil
                        .getUIInfoAdapter(shell));
                } catch (final ExecutionException e) {
                    shell.getDisplay().syncExec(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (e.getCause() instanceof CoreException) {
                                        ErrorDialog
                                                .openError(shell, // Was Utilities.getFocusShell()
                                                        getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
                                                        null, // no special message
                                                        ((CoreException) e
                                                                .getCause())
                                                                .getStatus());
                                    } else {
                                        UiConstants.Util.log(IStatus.ERROR, e, e.getCause().getMessage());
                                        MessageDialog
                                                .openError(shell,
                                                        getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
                                                        UiConstants.Util.getString("internalErrorMsg", //$NON-NLS-1$
                                                                        e
                                                                                .getCause()
                                                                                .getMessage()));
                                    }
                                }
                            });
                }
            }
        };

        try {
            new ProgressMonitorDialog(shell).run(true, true, op);
        } catch (InterruptedException e) {
            return;
        } catch (InvocationTargetException e) {
            // ExecutionExceptions are handled above, but unexpected runtime
            // exceptions and errors may still occur.
            UiConstants.Util.log(IStatus.ERROR, e, e.getTargetException().getMessage()); 
            MessageDialog
                    .open(MessageDialog.ERROR,shell,
                            getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
                            getString("internalErrorMsg", //$NON-NLS-1$,
                                            e.getTargetException().getMessage()), SWT.SHEET);
        }

    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * @since 5.0
 */
public class RebuildVdbAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RebuildVdbAction.class);
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

    List selectedVdbs;

    boolean runWasSuccessful = false;

    /**
     * @since 5.0
     */
    public RebuildVdbAction() {
        super();
        setImageDescriptor(VdbUiPlugin.getDefault().getImageDescriptor(VdbUiConstants.Images.REBUILD_VDB_ICON));
    }

    private boolean allVdbsSelected( ISelection selection ) {
        boolean allVdbs = false;
        if (!SelectionUtilities.isEmptySelection(selection)) {
            List objs = SelectionUtilities.getSelectedObjects(selection);
            this.selectedVdbs = new ArrayList(objs);
            Iterator iter = objs.iterator();
            Object nextObj = null;
            allVdbs = true;
            while (iter.hasNext() && allVdbs) {
                nextObj = iter.next();
                if (nextObj instanceof IFile) {
                    String extension = ((IFile)nextObj).getFileExtension();
                    if (extension != null && extension.equals(VDB_EXTENSION)) {
                        if (this.selectedVdbs.contains(selectedVdbs)) {
                            this.selectedVdbs.add(nextObj);
                        }
                    } else {
                        allVdbs = false;
                    }
                } else {
                    allVdbs = false;
                }
            }

        }
        return allVdbs;
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

    private boolean doReadOnlyCheck() {
        boolean allWriteable = true;
        if (!selectedVdbs.isEmpty()) {
            IFile nextVdb = null;
            for (Iterator iter = selectedVdbs.iterator(); iter.hasNext() && allWriteable;) {
                nextVdb = (IFile)iter.next();
                if (ModelUtil.isIResourceReadOnly(nextVdb)) {
                    String message = getString("vdbIsReadOnly.message", nextVdb.getName()); //$NON-NLS-1$
                    MessageDialog.openError(null, READ_ONLY_FILE_MESSAGE, message);
                    allWriteable = false;
                }
            }
        }
        return allWriteable;
    }

    private VdbEditor getOpenVdbEditor( IFile vdbFile ) {

        IEditorReference[] editors = UiUtil.getWorkbenchPage().getEditorReferences();

        IEditorPart nextEditor = null;
        for (int i = 0; i < editors.length; i++) {
            nextEditor = editors[i].getEditor(false);
            if (nextEditor instanceof VdbEditor) {
                VdbEditor editor = (VdbEditor)nextEditor;
                IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
                final IFile file = input.getFile();
                if (file.equals(vdbFile)) {
                    return editor;
                }
            }
        }

        return null;
    }

    /**
     * @param selection
     * @return
     */
    public boolean isApplicable( ISelection selection ) {
        return allVdbsSelected(selection);
    }

    boolean rebuildVdb( final IFile vdbFile,
                        final IProgressMonitor monitor,
                        final boolean refreshStaleModels ) {
        boolean successful = false;
        boolean thisContextIsLocal = false;
        Vdb vdb = null;
        VdbEditor editor = getOpenVdbEditor(vdbFile);

        try {
            if (editor != null) vdb = editor.getVdb();
            if (vdb == null) {
                vdb = new Vdb(vdbFile.getFullPath());
                thisContextIsLocal = true;
            }

            // check the models in the VDB to make sure we should proceed

            if (thisContextIsLocal) {
                // Don't refresh out of sync if there are none to prevent progress monitor dialog from popping up
                if (refreshStaleModels) {
                    vdb.synchronize();
                }
                // Save the context
                vdb.save(null);
                // Refresh the file since it was modified by the context
                ResourcesPlugin.getWorkspace().getRoot().findMember(vdb.getName()).refreshLocal(IResource.DEPTH_ZERO, null);

                vdb.close();
                successful = true;
            } else if (editor != null) {
                // Access the editor directly and call the same sync method
                // behind the Synchronize All button action inside the editor.
                // This prevents a possible IllegalStateExceptions
                if (refreshStaleModels) {
                    editor.synchronizeVdb(true);
                }
                editor.doSave(monitor);
                successful = true;
            }

        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            successful = false;
            MessageDialog.openError(null, EXCEPTION_TITLE, err.getMessage());
        }

        return successful;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        if (doReadOnlyCheck()) {
            runWasSuccessful = true;
            final boolean refreshStaleModels = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                                                                          getString("autoSyncDialog.title"), //$NON-NLS-1$
                                                                          getString("autoSyncDialog.message")); //$NON-NLS-1$
            final IProgressMonitor nullProgressMonitor = new NullProgressMonitor();
            UiBusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    if (!selectedVdbs.isEmpty()) {
                        IFile nextVdb = null;
                        for (Iterator iter = selectedVdbs.iterator(); iter.hasNext();) {
                            nextVdb = (IFile)iter.next();
                            boolean result = rebuildVdb(nextVdb, nullProgressMonitor, refreshStaleModels);
                            if (!result) {
                                runWasSuccessful = false;
                            }
                        }
                    }

                }
            });
        }

        if (runWasSuccessful) {
            String title = getString("successfulDialog.title"); //$NON-NLS-1$
            String msg = getString("successfulDialog.message"); //$NON-NLS-1$
            List vdbNames = new ArrayList(selectedVdbs.size());
            for (Iterator iter = selectedVdbs.iterator(); iter.hasNext();) {
                IFile nextVdb = (IFile)iter.next();
                String name = nextVdb.getName();
                vdbNames.add(name);
            }

            ListMessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, null, msg, vdbNames, null);
        }
        selectedVdbs = Collections.EMPTY_LIST;
    }

    protected void saveEditor( final VdbEditor editor ) {
        if (editor != null) {
            // This should only be called when
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage page = editor.getSite().getPage();
                    page.saveEditor(editor, false);
                }
            });
        }

    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        setEnabled(allVdbsSelected(selection));
    }
}

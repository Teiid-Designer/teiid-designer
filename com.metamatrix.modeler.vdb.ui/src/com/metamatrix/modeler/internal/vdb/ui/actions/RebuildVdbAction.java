/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.vdb.ui.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import com.metamatrix.ui.internal.widget.ListMessageDialog;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;

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
    List selectedVdbs;
    boolean runWasSuccessful = false;

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

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        setEnabled(allVdbsSelected(selection));
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

    private void saveContext( VdbEditingContext theContext,
                              IFile theVdbFile ) {
        if (theContext != null) {
            // Save the context

            // Defect 15772 - Update sync state when Synchronize Models action called and VDB not open
            // Refresh the file since it was modified by the context
            try {
                theContext.setModified();
                theContext.save(null);
                // pass in null for monitor because we don't want an InterruptedException thrown
                // if the monitor has been cancelled. we still want to refresh since the file has changed.
                theVdbFile.refreshLocal(IResource.DEPTH_ZERO, null);
            } catch (final CoreException err) {
                WidgetUtil.showError(err.getMessage());
                VdbUiConstants.Util.log(err);
            } // endtry

            try {
                theContext.close();
            } catch (IOException err) {
                VdbUiConstants.Util.log(err);
                MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
            }
        }
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

    boolean rebuildVdb( final IFile vdbFile,
                        final IProgressMonitor progressMonitor,
                        final boolean refreshStaleModels ) {
        boolean successful = false;
        boolean thisContextIsLocal = false;
        VdbEditingContext thisContext = null;
        VdbEditor editor = getOpenVdbEditor(vdbFile);

        try {
            if (editor != null) {
                thisContext = editor.getContext();
            }
            if (thisContext == null) {
                thisContext = VdbEditPlugin.createVdbEditingContext(vdbFile.getRawLocation());
                thisContext.open();
                thisContextIsLocal = true;
            }

            // check the models in the VDB to make sure we should proceed

            if (thisContextIsLocal) {
                // Don't refresh out of sync if there are none to prevent progress monitor dialog from popping up
                if (refreshStaleModels && hasStaleModels(thisContext, vdbFile)) {
                    if (checkModels(thisContext, vdbFile)) {
                        final VdbEditingContext finalContext = thisContext;
                        final IRunnableWithProgress op = new IRunnableWithProgress() {
                            public void run( final IProgressMonitor theMonitor ) {
                                theMonitor.beginTask("Refreshing Out of Sync Models", 100); //$NON-NLS-1$
                                theMonitor.worked(50);
                                // ----------------------------------------------------------
                                // Defect 22248 - Had to replace the refresh methods in this action with a new Utility method
                                // that mimiced what the VdbEditorModelComposite panel was doing.
                                // ----------------------------------------------------------
                                VdbEditUtil.refreshAllOutOfSyncModels(finalContext, this, vdbFile.getProject());

                                theMonitor.done();
                            }
                        };
                        try {
                            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
                        } catch (InterruptedException e) {
                        } catch (InvocationTargetException e) {
                            VdbUiConstants.Util.log(e.getTargetException());
                        }
                    }
                }
                // Go ahead and save this context.
                saveContext(thisContext, vdbFile);
                successful = true;
            } else if (editor != null) {
                // ---------------------------------------------------------------
                // Defect 22305 was a result of this action performing the synching from outside
                // the Vdb Editor. Needed to access the editor directly and call the same sync method
                // behind the Synchronize All button action inside the editor.
                // This prevents a possible IllegalStateExceptions
                // ---------------------------------------------------------------
                if (refreshStaleModels) {
                    editor.synchronizeVdb(true);
                }
                editor.getContext().setModified();
                editor.doSave(progressMonitor);
                successful = true;
            }

        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            successful = false;
            MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
        }

        return successful;
    }

    /**
     * Checks for the following conditions on all ModelResources that are stale: 1) Must exist in the workspace 2) Must have been
     * validated since last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also,
     * if no models are stale, the method returns false.
     * 
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     */
    private boolean checkModels( VdbEditingContext theContext,
                                 IFile theVdbFile ) {
        boolean result = true;

        Collection modelList = theContext.getVirtualDatabase().getModels();
        Collection staleFiles = new ArrayList();
        // iterate through the models, as long as result remains TRUE
        for (Iterator iter = modelList.iterator(); iter.hasNext() && result;) {
            final ModelReference modelReference = (ModelReference)iter.next();
            if (theContext.isStale(modelReference)) {
                final IFile file = VdbEditUtil.getFile(modelReference, theVdbFile.getProject());
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

        return result;
    }

    private boolean hasStaleModels( VdbEditingContext theContext,
                                    IFile theVdbFile ) {

        Collection modelList = theContext.getVirtualDatabase().getModels();

        // iterate through the models, as long as result remains TRUE
        for (Iterator iter = modelList.iterator(); iter.hasNext();) {
            final ModelReference modelReference = (ModelReference)iter.next();
            if (theContext.isStale(modelReference)) {
                return true;
            }
        }

        return false;
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
        return allVdbsSelected(selection);
    }
}

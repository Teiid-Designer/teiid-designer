/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.ModelResourceCollectorVisitor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * RebuildImportsAllAction rebuilds the model import declarations for all the models in the workspace. The models must be in open
 * model projects.
 */
public class BuildImportsAllAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public BuildImportsAllAction() {
        super();
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {
                // In order for the notifications caused by "opening models" for validation, to be swallowed, the validation
                // call needs to be wrapped in a transaction. This was discovered and relayed by Goutam on 2/14/05.
                boolean started = ModelerCore.startTxn(false, false, "Rebuild All Imports", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    rebuildImports(theMonitor);
                    succeeded = true;
                } catch (final Exception err) {
                    final String msg = UiConstants.Util.getString("RebuildImportsAllAction.errorMessage"); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, err, msg);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }
    }

    void rebuildImports( IProgressMonitor theMonitor ) {

        final ArrayList eventList = new ArrayList();
        final ArrayList modelsToSave = new ArrayList();
        final ArrayList readOnlyModels = new ArrayList();
        final ArrayList errorModels = new ArrayList();

        int size = getModelResourceList().size() + 1;

        theMonitor.beginTask(UiConstants.Util.getString("RebuildImportsAllAction.rebuildAllModels"), size); //$NON-NLS-1$
        // first, rebuild the models
        int count = 0;
        for (Iterator iter = getModelResourceList().iterator(); iter.hasNext();) {
            count++;
            try {
                theMonitor.setTaskName(UiConstants.Util.getString("RebuildImportsAllAction.progressMessage", new Object[] {new Integer(count), new Integer(size - 1)}));//$NON-NLS-1$
                ModelResource modelResource = (ModelResource)iter.next();
                if (modelResource != null && !modelResource.isReadOnly()) {
                    IFile modelFile = (IFile)modelResource.getResource();

                    boolean succeeded = false;
                    // Defect 23823 - switched to use a new Modeler Core utility.
                    try {
                        succeeded = ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), this, true);
                    } catch (ModelWorkspaceException theException) {
                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }

                    if (succeeded) {
                        eventList.add(modelResource);
                    } else {
                        errorModels.add(modelFile);
                    }
                    if (!ModelEditorManager.isOpen(modelFile)) {
                        modelsToSave.add(modelResource);
                    }
                } else if (modelResource.isReadOnly()) {
                    readOnlyModels.add(modelResource.getPath().makeRelative().toString());
                }
            } finally {
                theMonitor.worked(1);
            }
        }

        theMonitor.setTaskName(UiConstants.Util.getString("RebuildImportsAllAction.savingMessage", new Object[] {new Integer(count), new Integer(size - 1)}));//$NON-NLS-1$
        // second, save all the models that are not open in editors, or else they may never get saved.
        for (Iterator iter = modelsToSave.iterator(); iter.hasNext();) {
            try {
                ((ModelResource)iter.next()).save(null, true);
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        // fire events on all models so the gui can update their import lists
        for (Iterator iter = eventList.iterator(); iter.hasNext();) {
            ModelResourceEvent event = new ModelResourceEvent((ModelResource)iter.next(), ModelResourceEvent.REBUILD_IMPORTS,
                                                              this);
            UiPlugin.getDefault().getEventBroker().processEvent(event);
        }

        // if there were any readonly models, display a message to the user
        if (!readOnlyModels.isEmpty()) {
            final String title = UiConstants.Util.getString("RebuildImports.readOnlyTitle"); //$NON-NLS-1$
            final String message = UiConstants.Util.getString("RebuildImports.readOnlyMessage"); //$NON-NLS-1$
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    ListMessageDialog.openWarning(Display.getDefault().getActiveShell(),
                                                  title,
                                                  null,
                                                  message,
                                                  readOnlyModels,
                                                  null);
                }
            });
        }

        theMonitor.worked(1);

        // if there were any errors, display a message to the user
        if (!errorModels.isEmpty()) {
            final String title = UiConstants.Util.getString("RebuildImports.errorTitle"); //$NON-NLS-1$
            final String message = UiConstants.Util.getString("RebuildImports.errorMessage"); //$NON-NLS-1$
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    ListMessageDialog.openError(Display.getDefault().getActiveShell(), title, null, message, errorModels, null);
                }
            });
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        boolean enable = true;
        action.setEnabled(enable);
    }

    private Collection getModelResourceList() {

        ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].isOpen() && ModelerCore.hasModelNature(projects[i])) {
                try {
                    projects[i].accept(visitor);
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }
            }
        }

        try {
            return visitor.getModelResources();
        } catch (CoreException e) {
            UiConstants.Util.log(e);
        }

        return Collections.EMPTY_LIST;
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

}

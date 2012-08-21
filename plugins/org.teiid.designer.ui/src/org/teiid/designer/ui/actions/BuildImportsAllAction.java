/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.refactor.ModelResourceCollectorVisitor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.event.ModelResourceEvent;


/**
 * RebuildImportsAllAction rebuilds the model import declarations for all the models in the workspace. The models must be in open
 * model projects.
 *
 * @since 8.0
 */
public class BuildImportsAllAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public BuildImportsAllAction() {
        super();
    }

    private Collection getModelResourceList() {

        final ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        final IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < projects.length; ++i) {
            if (projects[i].isOpen() && ModelerCore.hasModelNature(projects[i])) {
                try {
                    projects[i].accept(visitor);
                } catch (final CoreException e) {
                    UiConstants.Util.log(e);
                }
            }
        }

        try {
            return visitor.getModelResources();
        } catch (final CoreException e) {
            UiConstants.Util.log(e);
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    @Override
	public void init( final IViewPart view ) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
	public void init( final IWorkbenchWindow window ) {
    }

    void rebuildImports( final IProgressMonitor theMonitor ) {

        final ArrayList eventList = new ArrayList();
        final ArrayList modelsToSave = new ArrayList();
        final ArrayList readOnlyModels = new ArrayList();
        final ArrayList errorModels = new ArrayList();

        final int size = getModelResourceList().size() + 1;

        theMonitor.beginTask(UiConstants.Util.getString("RebuildImportsAllAction.rebuildAllModels"), size); //$NON-NLS-1$
        // first, rebuild the models
        int count = 0;
        for (final Iterator iter = getModelResourceList().iterator(); iter.hasNext();) {
            count++;
            try {
                theMonitor.setTaskName(UiConstants.Util.getString("RebuildImportsAllAction.progressMessage", new Object[] {new Integer(count), new Integer(size - 1)}));//$NON-NLS-1$
                final ModelResource modelResource = (ModelResource)iter.next();
                if(modelResource!=null) {
                    if (!modelResource.isReadOnly()) {
                        final IFile modelFile = (IFile)modelResource.getResource();

                        boolean succeeded = false;
                        // Defect 23823 - switched to use a new Modeler Core utility.
                        try {
                            succeeded = ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), true);
                        } catch (final ModelWorkspaceException theException) {
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
                    } else {
                        readOnlyModels.add(modelResource.getPath().makeRelative().toString());
                    }
                }
            } finally {
                theMonitor.worked(1);
            }
        }

        theMonitor.setTaskName(UiConstants.Util.getString("RebuildImportsAllAction.savingMessage", new Object[] {new Integer(count), new Integer(size - 1)}));//$NON-NLS-1$
        // second, save all the models that are not open in editors, or else they may never get saved.
        for (final Iterator iter = modelsToSave.iterator(); iter.hasNext();) {
            try {
                ((ModelResource)iter.next()).save(null, true);
            } catch (final ModelWorkspaceException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        // fire events on all models so the gui can update their import lists
        for (final Iterator iter = eventList.iterator(); iter.hasNext();) {
            final ModelResourceEvent event = new ModelResourceEvent((ModelResource)iter.next(), ModelResourceEvent.REBUILD_IMPORTS,
                                                                    this);
            UiPlugin.getDefault().getEventBroker().processEvent(event);
        }

        // if there were any readonly models, display a message to the user
        if (!readOnlyModels.isEmpty()) {
            final String title = UiConstants.Util.getString("RebuildImports.readOnlyTitle"); //$NON-NLS-1$
            final String message = UiConstants.Util.getString("RebuildImports.readOnlyMessage"); //$NON-NLS-1$
            Display.getDefault().syncExec(new Runnable() {
                @Override
				public void run() {
                    ListMessageDialog.openWarning(Display.getDefault().getActiveShell(), title, null, message, readOnlyModels, null);
                }
            });
        }

        theMonitor.worked(1);

        // if there were any errors, display a message to the user
        if (!errorModels.isEmpty()) {
            final String title = UiConstants.Util.getString("RebuildImports.errorTitle"); //$NON-NLS-1$
            final String message = UiConstants.Util.getString("RebuildImports.errorMessage"); //$NON-NLS-1$
            Display.getDefault().syncExec(new Runnable() {
                @Override
				public void run() {
                    ListMessageDialog.openError(Display.getDefault().getActiveShell(), title, null, message, errorModels, null);
                }
            });
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( final IAction action ) {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {
                // In order for the notifications caused by "opening models" for validation, to be swallowed, the validation
                // call needs to be wrapped in a transaction. This was discovered and relayed by Goutam on 2/14/05.
                final boolean started = ModelerCore.startTxn(false, false, "Rebuild All Imports", this); //$NON-NLS-1$
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
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        final boolean enable = true;
        action.setEnabled(enable);
    }

}

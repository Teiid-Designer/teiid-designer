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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
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
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * RebuildImportsAction
 */
public class BuildImportsAction extends ActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private List selectedModels;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public BuildImportsAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init( final IViewPart view ) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( final IWorkbenchWindow window ) {

    }

    void rebuildImports() {
        if (selectedModels != null) {

            final ArrayList eventList = new ArrayList();
            final ArrayList modelsToSave = new ArrayList();

            // first, rebuild the models
            for (final Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                final IFile modelFile = (IFile)iter.next();
                try {
                    final ModelResource modelResource = ModelUtil.getModelResource(modelFile, true);
                    rebuildImports(modelResource);
                    eventList.add(modelResource);
                    if (!ModelEditorManager.isOpen(modelFile)) {
                        modelsToSave.add(modelResource);
                    } else {
                    }

                } catch (final ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // second, save all the models that are not open in editors, or else they may never get saved.
            for (final Iterator iter = modelsToSave.iterator(); iter.hasNext();) {
                try {
                    ((ModelResource)iter.next()).save(null, true);
                } catch (final ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // finally, fire events on all models so the gui can update their import lists
            for (final Iterator iter = eventList.iterator(); iter.hasNext();) {
                final ModelResourceEvent event = new ModelResourceEvent((ModelResource)iter.next(),
                                                                        ModelResourceEvent.REBUILD_IMPORTS, this);
                UiPlugin.getDefault().getEventBroker().processEvent(event);
            }

        }

    }

    private void rebuildImports( final ModelResource modelResource ) throws ModelWorkspaceException {
        if (modelResource != null && !modelResource.isReadOnly()) {
            // Defect 23823 - switched to use a new Modeler Core utility.
            ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), true);
        }
    }

    /* (non-Javadoc)
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
                    rebuildImports();
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

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        selectedModels = SelectionUtilities.getSelectedObjects(selection);
        boolean enable = true;
        if (selectedModels.isEmpty()) {
            enable = false;
        } else {
            for (final Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof IFile) {
                    if (!ModelUtilities.isModelFile((IFile)obj)) {
                        enable = false;
                        break;
                    }
                    try {
                        final ModelResource modelResource = ModelUtil.getModelResource((IFile)obj, true);
                        if (modelResource == null || modelResource.isReadOnly()) {
                            enable = false;
                            break;
                        }
                    } catch (final ModelWorkspaceException e) {
                        UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                } else {
                    enable = false;
                    break;
                }
            }
        }
        action.setEnabled(enable);
    }

    public void setSelectedModels( final List selectedModels ) {
        this.selectedModels = selectedModels;
    }

}

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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class BuildModelImportsAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private List selectedModels;

    public BuildModelImportsAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.BUILD_MODEL_IMPORTS_ICON));
        setId(ModelActionConstants.Resource.BUILD_MODEL_IMPORTS);
    }

    @Override
	public int compareTo( final Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    @Override
	public boolean isApplicable( final ISelection selection ) {
        boolean result = true;
        final List selectedObjs = SelectionUtilities.getSelectedObjects(selection);
        if (selectedObjs.isEmpty()) {
            result = false;
        }
        if (result) {
            for (final Iterator iter = selectedObjs.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof IFile) {
                    if (!ModelUtilities.isModelFile((IFile)obj)) {
                        result = false;
                    }
                } else {
                    result = false;
                }
                if (!result) break;
            }
        }

        return result;
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

                    // Defect 23823 - switched to use a new Modeler Core utility.
                    try {
                        ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), true);
                    } catch (final ModelWorkspaceException theException) {
                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }
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

    @Override
    public void run() {
        if (selectedModels != null) {
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
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
	public void selectionChanged( final IWorkbenchPart part,
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
        setEnabled(enable);
    }
}

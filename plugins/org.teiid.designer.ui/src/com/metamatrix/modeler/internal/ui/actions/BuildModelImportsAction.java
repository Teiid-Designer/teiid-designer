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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class BuildModelImportsAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private List selectedModels;

    public BuildModelImportsAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.BUILD_MODEL_IMPORTS_ICON));
    }

    void rebuildImports() {
        if (selectedModels != null) {

            ArrayList eventList = new ArrayList();
            ArrayList modelsToSave = new ArrayList();

            // first, rebuild the models
            for (Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                IFile modelFile = (IFile)iter.next();
                try {
                    ModelResource modelResource = ModelUtilities.getModelResource(modelFile, true);

                    // Defect 23823 - switched to use a new Modeler Core utility.
                    try {
                        ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), this, true);
                    } catch (ModelWorkspaceException theException) {
                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }
                    eventList.add(modelResource);
                    if (!ModelEditorManager.isOpen(modelFile)) {
                        modelsToSave.add(modelResource);
                    } else {
                    }

                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // second, save all the models that are not open in editors, or else they may never get saved.
            for (Iterator iter = modelsToSave.iterator(); iter.hasNext();) {
                try {
                    ((ModelResource)iter.next()).save(null, true);
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // finally, fire events on all models so the gui can update their import lists
            for (Iterator iter = eventList.iterator(); iter.hasNext();) {
                ModelResourceEvent event = new ModelResourceEvent((ModelResource)iter.next(), ModelResourceEvent.REBUILD_IMPORTS,
                                                                  this);
                UiPlugin.getDefault().getEventBroker().processEvent(event);
            }

        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        selectedModels = SelectionUtilities.getSelectedObjects(selection);
        boolean enable = true;
        if (selectedModels.isEmpty()) {
            enable = false;
        } else {
            for (Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof IFile) {
                    if (!ModelUtilities.isModelFile((IFile)obj)) {
                        enable = false;
                        break;
                    }
                    try {
                        ModelResource modelResource = ModelUtilities.getModelResource((IFile)obj, true);
                        if (modelResource == null || modelResource.isReadOnly()) {
                            enable = false;
                            break;
                        }
                    } catch (ModelWorkspaceException e) {
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

    @Override
    public void run() {
        if (selectedModels != null) {
            final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                @Override
                public void execute( IProgressMonitor theMonitor ) {
                    // In order for the notifications caused by "opening models" for validation, to be swallowed, the validation
                    // call needs to be wrapped in a transaction. This was discovered and relayed by Goutam on 2/14/05.
                    boolean started = ModelerCore.startTxn(false, false, "Rebuild All Imports", this); //$NON-NLS-1$
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
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                UiConstants.Util.log(e.getTargetException());
            }
        }
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

    public boolean isApplicable( ISelection selection ) {
        boolean result = true;
        List selectedObjs = SelectionUtilities.getSelectedObjects(selection);
        if (selectedObjs.isEmpty()) {
            result = false;
        }
        if (result) {
            for (Iterator iter = selectedObjs.iterator(); iter.hasNext();) {
                Object obj = iter.next();
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
}

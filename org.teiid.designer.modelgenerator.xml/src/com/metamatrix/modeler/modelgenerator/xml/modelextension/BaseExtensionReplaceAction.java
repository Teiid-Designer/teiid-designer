/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.modelextension;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public abstract class BaseExtensionReplaceAction extends Action
    implements ISelectionListener, Comparable, ISelectionAction, ExtensionReplaceAction {
    private List selectedModels;

    public BaseExtensionReplaceAction() {
        super();
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
    }

    public abstract String getExtensionName();

    public abstract ExtensionManager getExtensionManager();

    void replaceExtension( IProgressMonitor monitor ) {
        if (selectedModels != null) {

            ArrayList<ModelResource> eventList = new ArrayList<ModelResource>();
            ArrayList<ModelResource> modelsToSave = new ArrayList<ModelResource>();
            ExtensionManager manager = null;
            // first, rebuild the models
            for (Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                IFile modelFile = (IFile)iter.next();
                try {
                    ModelResource modelResource = ModelUtilities.getModelResource(modelFile, true);
                    try {
                        if (null == manager) {
                            manager = getExtensionManager();
                            manager.loadModelExtensions(modelFile.getParent(), monitor);
                        }
                        final ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
                        modelAnnotation.setExtensionPackage(manager.getPackage());
                        ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), this, true);
                    } catch (ModelWorkspaceException theException) {
                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    } catch (ModelerCoreException e) {
                        UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
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
            for (Iterator<ModelResource> iter = modelsToSave.iterator(); iter.hasNext();) {
                try {
                    iter.next().save(null, true);
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // finally, fire events on all models so the gui can update their import lists
            for (Iterator<ModelResource> iter = eventList.iterator(); iter.hasNext();) {
                ModelResourceEvent event = new ModelResourceEvent(iter.next(), ModelResourceEvent.REBUILD_IMPORTS, this);
                UiPlugin.getDefault().getEventBroker().processEvent(event);
            }

        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
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
                    String message = XmlImporterUiPlugin.getDefault().getPluginUtil().getString("ReplaceMissingExtensionsAction.TxnMessage"); //$NON-NLS-1$
                    boolean started = ModelerCore.startTxn(true, true, message, this);
                    boolean succeeded = false;
                    try {
                        replaceExtension(theMonitor);
                        succeeded = true;
                    } catch (final Exception err) {
                        final String msg = XmlImporterUiPlugin.getDefault().getPluginUtil().getString("ReplaceMissingExtensionsAction.errorMessage"); //$NON-NLS-1$
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
        boolean result = false;
        List selectedObjs = SelectionUtilities.getSelectedObjects(selection);
        if (!selectedObjs.isEmpty()) {
            for (Iterator iter = selectedObjs.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof IFile) {
                    IFile file = (IFile)obj;
                    if (ModelUtilities.isModelFile(file)) {
                        ModelResource resource = ModelUtilities.getModelResource(file);
                        try {
                            if (ModelUtilities.isPhysical(resource) && ModelUtilities.isRelationalModel(resource)) {
                                List imports = resource.getModelImports();
                                for (Iterator impIter = imports.iterator(); impIter.hasNext();) {
                                    ModelImport imp = (ModelImport)impIter.next();
                                    if (imp.getName().equals(getExtensionName())) {
                                        if (!ModelUtilities.isModelInWorkspace(imp.getUuid())) {
                                            result = true;
                                        }
                                    }
                                }
                            } else {
                                result = false;
                                break;
                            }
                        } catch (ModelWorkspaceException e) {
                            result = false;
                            break;
                        } catch (CoreException e) {
                            result = false;
                            break;
                        }
                    } else {
                        result = false;
                        break;
                    }
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}

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
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
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

    public int compareTo( final Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    public abstract ExtensionManager getExtensionManager();

    public abstract String getExtensionName();

    public boolean isApplicable( final ISelection selection ) {
        boolean result = false;
        final List selectedObjs = SelectionUtilities.getSelectedObjects(selection);
        if (!selectedObjs.isEmpty()) {
            for (final Iterator iter = selectedObjs.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof IFile) {
                    final IFile file = (IFile)obj;
                    if (ModelUtilities.isModelFile(file)) {
                        final ModelResource resource = ModelUtilities.getModelResource(file);
                        try {
                            if (ModelUtilities.isPhysical(resource) && ModelUtilities.isRelationalModel(resource)) {
                                final List imports = resource.getModelImports();
                                for (final Iterator impIter = imports.iterator(); impIter.hasNext();) {
                                    final ModelImport imp = (ModelImport)impIter.next();
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
                        } catch (final ModelWorkspaceException e) {
                            result = false;
                            break;
                        } catch (final CoreException e) {
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

    void replaceExtension( final IProgressMonitor monitor ) {
        if (selectedModels != null) {

            final ArrayList<ModelResource> eventList = new ArrayList<ModelResource>();
            final ArrayList<ModelResource> modelsToSave = new ArrayList<ModelResource>();
            ExtensionManager manager = null;
            // first, rebuild the models
            for (final Iterator iter = selectedModels.iterator(); iter.hasNext();) {
                final IFile modelFile = (IFile)iter.next();
                try {
                    final ModelResource modelResource = ModelUtil.getModelResource(modelFile, true);
                    try {
                        if (null == manager) {
                            manager = getExtensionManager();
                            manager.loadModelExtensions(modelFile.getParent(), monitor);
                        }
                        final ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
                        modelAnnotation.setExtensionPackage(manager.getPackage());
                        ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), true);
                    } catch (final ModelWorkspaceException theException) {
                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    } catch (final ModelerCoreException e) {
                        UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
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
            for (final ModelResource modelResource : modelsToSave) {
                try {
                    modelResource.save(null, true);
                } catch (final ModelWorkspaceException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            // finally, fire events on all models so the gui can update their import lists
            for (final ModelResource modelResource : eventList) {
                final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.REBUILD_IMPORTS, this);
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
                    final String message = XmlImporterUiPlugin.getDefault().getPluginUtil().getString("ReplaceMissingExtensionsAction.TxnMessage"); //$NON-NLS-1$
                    final boolean started = ModelerCore.startTxn(true, true, message, this);
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
            } catch (final InterruptedException e) {
            } catch (final InvocationTargetException e) {
                UiConstants.Util.log(e.getTargetException());
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
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

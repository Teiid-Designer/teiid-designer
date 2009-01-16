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
package com.metamatrix.modeler.vdb.ui.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * @since 4.3
 */
public class VdbEditUtil implements VdbUiConstants {
    private static final String I18N_PREFIX = "VdbEditUtil."; //$NON-NLS-1$
    private static final String FAILED_ADD_MSG_KEY = getString("failedAddMessage"); //$NON-NLS-1$
    static final String ADDING_MODELS_MESSAGE = getString("addingModelsMessage"); //$NON-NLS-1$
    static final String ADD_MODEL_ERROR_TITLE = getString("addModelErrorTitle"); //$NON-NLS-1$

    private static final boolean ADD_DEPENDENT_MODELS_DEFAULT = true;

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * Return the IFile in the workspace corresponding to the ModelReference found in the VDB.
     * 
     * @param reference The VDB ModelReference instance to find in the workspace
     * @param vdbProject The IProject containing this VDB
     * @return
     * @since 4.3
     */
    public static IFile getFile( final ModelReference reference,
                                 final IProject vdbProject ) {
        IFile localFile = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(reference.getModelLocation());
        if (localFile == null) {
            // If we cannot find the resoure in the workspace searching by path then
            // try to search by UUID - this should work if the resources is an XMI file
            if (!ModelUtil.isXsdFile(new Path(reference.getModelLocation()))) {
                final String refUUID = reference.getUuid();
                localFile = (IFile)WorkspaceResourceFinderUtil.findIResourceByUUID(refUUID);
            }
            // For an XSD file the best we can do is try to match the name
            else {
                // Get the model name including file extension from the path. Calling
                // ModelReference.getName() will return the name without the extension.
                final String name = (new Path(reference.getModelLocation())).lastSegment();
                IResource[] localFiles = WorkspaceResourceFinderUtil.findIResourceByName(name);
                if (localFiles.length == 1) {
                    localFile = (IFile)localFiles[0];
                } else if (localFiles.length > 1) {
                    // Return the local file within the same IProject as the vdb
                    for (int i = 0; i != localFiles.length; ++i) {
                        if (localFiles[i].getProject() == vdbProject) {
                            localFile = (IFile)localFiles[i];
                            break;
                        }
                    }
                    // If no IFile was found in the same IProject as the vdb, then
                    // return the first entry in the array
                    if (localFile == null) {
                        localFile = (IFile)localFiles[0];
                    }
                }
            }
        }
        return localFile;
    }

    public static boolean isVdbFile( final File theFile ) {
        boolean result = false;
        String name = theFile.getName();
        int index = name.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR);

        if ((index != -1) && ((index + 2) < name.length())) {
            result = isVdbFileExtension(name.substring(index + 1));
        }

        return result;
    }

    /**
     * Indicates if the specified extensions is a valid WSDL file extension.
     * 
     * @param theExtension the extension being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isVdbFileExtension( String theExtension ) {
        boolean result = false;

        if ((theExtension != null) && (theExtension.length() > 0)) {
            if (theExtension.equalsIgnoreCase(ModelUtilities.VDB_FILE_EXTENSION)) result = true;
        }

        return result;
    }

    /**
     * Method which synchronizes all out of sync models (i.e have changed) for a given VDB context
     * 
     * @param context
     * @param source
     * @param vdbProject
     */
    public static void refreshAllOutOfSyncModels( final VdbEditingContext context,
                                                  final Object source,
                                                  final IProject vdbProject ) {
        // Get list of out of sync models
        List outOfSyncModels = new ArrayList();
        List allModelRefs = new ArrayList(context.getVirtualDatabase().getModels());

        for (Iterator iter = allModelRefs.iterator(); iter.hasNext();) {
            final ModelReference modelReference = (ModelReference)iter.next();

            // test each model in the full list to see if it is out of sync
            if (context.isStale(modelReference)) {
                // see if this out of sync model is NOT in the selection; if so
                outOfSyncModels.add(modelReference);
            }
        }

        // if there are not out of sync models call refreshSelectedModels
        // return true
        if (!outOfSyncModels.isEmpty()) refreshSelectedModels(outOfSyncModels, context, vdbProject, source);
    }

    /**
     * method to directly refresh a set of models
     * 
     * @param selectedModels
     * @param context
     * @param vdbProject
     * @param source
     * @return true if any models were refreshed (i.e. removed then re-added)
     */
    public static boolean refreshSelectedModels( final List selectedModelReferences,
                                                 final VdbEditingContext context,
                                                 final IProject vdbProject,
                                                 final Object source ) {

        boolean modified = false;

        for (Iterator iter = selectedModelReferences.iterator(); iter.hasNext();) {
            ModelReference reference = (ModelReference)iter.next();
            boolean visible = reference.isVisible();

            final IFile file = VdbEditUtil.getFile(reference, vdbProject);

            if (file == null || !file.exists()) {
                // Cannot find file in the workspace - refresh failed
            } else {
                IFile[] modelArray = new IFile[] {file};
                if (validateModels(modelArray, source)) {
                    final IPath vdbRefPath = new Path(reference.getModelLocation()).makeRelative();
                    context.removeModel(vdbRefPath);
                    Object[] addedModels = addModels(modelArray, context, source);
                    for (int i = 0; i < addedModels.length; ++i) {
                        ModelReference newReference = (ModelReference)addedModels[i];
                        if (reference.getModelLocation().equals(newReference.getModelLocation())) {
                            newReference.setVisible(visible);
                        }
                    }
                    modified = true;
                }

            }
        }
        return modified;
    }

    /**
     * Convenience method to verify validation state of an array of models (IFiles)
     * 
     * @param modelFiles
     * @param source
     * @return
     */
    public static boolean validateModels( IFile[] modelFiles,
                                          Object source ) {
        // defect 15891 - scan entries and their dependencies before adding to
        // VDB
        // check the model, and its dependencies (the new 'true' at the end).

        boolean valid = true;
        for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
            final IFile model = modelFiles[ndx];
            if (!ModelUtilities.verifyWorkspaceValidationState(model, source, FAILED_ADD_MSG_KEY, true)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    /**
     * Broken out from addModels( ) so that it can be called by Refresh
     * 
     * @param modelFiles
     * @param context
     * @param source
     * @return
     * @since 5.0
     */
    public static Object[] addModels( final IFile[] modelFiles,
                                      final VdbEditingContext context,
                                      final Object source ) {
        final ArrayList addedModels = new ArrayList();
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {
                boolean allOK = true;

                if (modelFiles != null && modelFiles.length > 0) { // &&

                    // Get a crude estimate for the amount of work required to add
                    // the selected models and all their dependent models
                    int totalWork = 0;
                    for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
                        IFile model = modelFiles[ndx];
                        totalWork += WorkspaceResourceFinderUtil.getDependentResources(model).length;
                    }
                    theMonitor.beginTask(ADDING_MODELS_MESSAGE, totalWork);
                    theMonitor.setTaskName(ADDING_MODELS_MESSAGE);

                    // Add each selected model to the VDB along with their dependent models
                    for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
                        IFile model = modelFiles[ndx];

                        // model has been previously added don't try adding
                        // again
                        boolean addedPreviously = false;
                        List modelList = context.getVirtualDatabase().getModels();

                        for (int size = modelList.size(), i = 0; i < size; ++i) {
                            if (model.getFullPath().toString().equals(((ModelReference)modelList.get(i)).getModelLocation())) {
                                addedPreviously = true;
                                break;
                            }
                        }

                        // skip adding this model if added previously
                        if (addedPreviously) {
                            theMonitor.worked(1);
                            continue;
                        }

                        try {

                            // Get the relative path to this model within the
                            // workspace
                            final IPath pathInWorkspace = model.getFullPath().makeRelative();
                            // Add the model to the vdb
                            final ModelReference[] refs = addModelInTransaction(context,
                                                                                theMonitor,
                                                                                pathInWorkspace,
                                                                                ADD_DEPENDENT_MODELS_DEFAULT,
                                                                                source);
                            if (refs != null) {
                                for (int i = 0; i < refs.length; i++) {
                                    addedModels.add(refs[i]);
                                }
                            }

                        } catch (final Exception err) {
                            Util.log(err);

                            if (err instanceof VdbEditException) {
                                final IStatus status = ((VdbEditException)err).getStatus();
                                Display.getDefault().syncExec(new Runnable() {
                                    public void run() {
                                        ErrorDialog.openError(null, ADD_MODEL_ERROR_TITLE, null, status);
                                    }
                                });

                            } else {
                                WidgetUtil.showError(err.getLocalizedMessage());
                            }
                        }
                        if (!allOK) {
                            theMonitor.setCanceled(true);
                            break;
                        }

                    }
                }

                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            VdbUiConstants.Util.log(e.getTargetException());
        }

        return addedModels.toArray();
    }

    /**
     * @param context
     * @param monitor
     * @param pathInWorkspace
     * @param addDependentModels
     * @param source
     * @return
     * @throws VdbEditException
     */
    public static ModelReference[] addModelInTransaction( final VdbEditingContext context,
                                                          final IProgressMonitor monitor,
                                                          final IPath pathInWorkspace,
                                                          final boolean addDependentModels,
                                                          final Object source ) throws VdbEditException {
        ModelReference[] refs = null;

        boolean started = ModelerCore.startTxn(false, false, "Remove Models from VDB", source); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            refs = context.addModel(monitor, pathInWorkspace, ADD_DEPENDENT_MODELS_DEFAULT);
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                }
            }
        }
        return refs;
    }
}

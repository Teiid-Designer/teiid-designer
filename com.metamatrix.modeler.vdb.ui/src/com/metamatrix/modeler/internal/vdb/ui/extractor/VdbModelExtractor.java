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
package com.metamatrix.modeler.internal.vdb.ui.extractor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.internal.core.xml.vdb.VdbHeaderReader;
import com.metamatrix.internal.core.xml.vdb.VdbModelInfo;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.NewModelProjectWorker;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.ListMessageDialog;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * The purpose of this class is to provide a mechanism to extract models from a VDB
 * 
 * @since 4.3
 */
public class VdbModelExtractor {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbModelExtractor.class);
    static final String EXCEPTION_TITLE = getString("exception.title"); //$NON-NLS-1$
    static final String EXCEPTION_MESSAGE = getString("exception.message"); //$NON-NLS-1$
    private static final String OK_MESSAGE = getString("extractionOK"); //$NON-NLS-1$
    private static final String EXISTING_MODELS_MESSAGE = getString("existingModelsMessage"); //$NON-NLS-1$
    private static final String EMPTY_VDB_MESSAGE = getString("emptyVdbMessage"); //$NON-NLS-1$
    private static final String EXISTING_PROJECTS_MESSAGE = getString("existingProjectsMessage"); //$NON-NLS-1$

    private static final String FILE_SEPARATOR = "/"; //$NON-NLS-1$
    private static final String XMI_EXTENSION = ".xmi"; //$NON-NLS-1$
    private static final String XSD_EXTENSION = ".xsd"; //$NON-NLS-1$
    private static final String MANIFEST_MODEL_NAME = "VdbManifestModel"; //$NON-NLS-1$

    private static final int BUFFER = 2048;

    private static String getString( final String id ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + id);
    }

    private boolean canExtract = true;

    InternalVdbEditingContext vdbContext;
    IPath vdbFilePath;
    private File vdbFile;
    private List modelProjects;
    private List projectFolders;
    private ZipFile vdbArchive;
    private String errorMessage = OK_MESSAGE;

    public VdbModelExtractor( File vdbFile ) {
        super();

        this.vdbFile = vdbFile;
        vdbFilePath = new Path(this.vdbFile.getAbsolutePath());
        init(null);
    }

    public VdbModelExtractor( VdbEditingContext existingVdbContext ) {
        super();
        init(existingVdbContext);
    }

    private void init() {
        if (vdbFile != null && vdbFile.exists()) {
            VdbHeader vdbHeader = null;

            try {
                vdbHeader = VdbHeaderReader.readHeader(vdbFile);
            } catch (MetaMatrixCoreException e) {
                e.printStackTrace();
            }

            if (vdbHeader != null) {
                VdbModelInfo[] modelInfos = vdbHeader.getModelInfos();
                List existingModels = getExistingModels(modelInfos);
                if (existingModels.isEmpty()) {
                    // CONTINUE
                } else {
                    // Popup Dialog and set the CAN'T IMPORT FLAG
                    warnUserAboutExistingModels(existingModels);
                    errorMessage = EXISTING_MODELS_MESSAGE;
                    canExtract = false;
                }
            } else {
                String title = getString("emptyVdbDetected"); //$NON-NLS-1$
                String msg = getString("emptyVdbDetectedMessage"); //$NON-NLS-1$
                MessageDialog.openWarning(VdbUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), title, msg);
                errorMessage = EMPTY_VDB_MESSAGE;
                canExtract = false;
            }
        }
    }

    private List getExistingModels( VdbModelInfo[] modelInfos ) {
        List existingModelsList = new ArrayList(modelInfos.length);

        for (int i = 0; i < modelInfos.length; i++) {
            String mUUID = modelInfos[i].getUUID();
            String mPath = modelInfos[i].getPath();
            String lPath = modelInfos[i].getLocation();
            IResource pResource = null;

            // The ModelReference instances within older MetaMatrix-VdbManifestModel models
            // will have path information stored in the "path" feature. In 5.0 a new "modelLocation"
            // feature was added to ModelReference and the "path" feature became transient and volatile.
            // We need to test for the presence of either value ...
            if (!StringUtil.isEmpty(mPath) && WorkspaceResourceFinderUtil.isGlobalResource(mPath)) {
                continue;
            }
            if (!StringUtil.isEmpty(lPath) && WorkspaceResourceFinderUtil.isGlobalResource(lPath)) {
                continue;
            }
            if (!StringUtil.isEmpty(mUUID)) {
                pResource = WorkspaceResourceFinderUtil.findIResourceByUUID(mUUID);
            }
            if (pResource == null && !StringUtil.isEmpty(mPath)) {
                pResource = WorkspaceResourceFinderUtil.findIResourceByPath(new Path(mPath));
            }
            if (pResource == null && !StringUtil.isEmpty(lPath)) {
                pResource = WorkspaceResourceFinderUtil.findIResourceByPath(new Path(lPath));
            }
            if (pResource != null) {
                existingModelsList.add(lPath);
            }

        }

        if (existingModelsList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return existingModelsList;
    }

    private static void warnUserAboutExistingModels( List existingModelsList ) {
        String title = getString("existingModelsDetectedTitle"); //$NON-NLS-1$
        String msg = getString("existingModelsDetectedMessage"); //$NON-NLS-1$
        ListMessageDialog.openWarning(VdbUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                      title,
                                      null,
                                      msg,
                                      existingModelsList,
                                      null);
    }

    private void init( final VdbEditingContext existingContext ) {
        if (existingContext != null) {
            UiBusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    try {
                        vdbContext = (InternalVdbEditingContext)existingContext;
                        if (!vdbContext.isOpen()) {
                            vdbContext.open();
                        }
                    } catch (final Exception err) {
                        VdbUiConstants.Util.log(err);
                        MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
                    }
                }
            });
            this.vdbFile = vdbContext.getPathToVdb().toFile();
            this.vdbFilePath = vdbContext.getPathToVdb();
        } else {
            // We need to create a vdb context

            UiBusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    try {
                        vdbContext = (InternalVdbEditingContext)VdbEditPlugin.createVdbEditingContext(vdbFilePath);
                        vdbContext.open();
                    } catch (final Exception err) {
                        VdbUiConstants.Util.log(err);
                        MessageDialog.openInformation(null, EXCEPTION_TITLE, EXCEPTION_MESSAGE);
                    }
                }
            });
        }

        init();

        File vdbContentsFolder = vdbContext.getVdbContentsFolder();
        projectFolders = new ArrayList();
        if (vdbContentsFolder.isDirectory()) {
            File[] contents = vdbContentsFolder.listFiles();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isDirectory()) projectFolders.add(contents[i].getName());
            }
        }
    }

    /**
     * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist or is not
     * of valid format.
     */
    protected ZipFile getZipFile() {
        if (vdbArchive == null) {
            try {
                vdbArchive = new ZipFile(this.vdbFile);
            } catch (ZipException err) {
                // Don't log because it's probably EMPTY
                // VdbUiConstants.Util.log(err);
            } catch (IOException err) {
                VdbUiConstants.Util.log(err);
            }
        }
        return vdbArchive;
    }

    public void extractAll() {
        ZipFile zf = getZipFile();
        if (zf != null) {
            unZipFiles(getZipFile(), Platform.getLocation());

            loadProjects(projectFolders);

            for (Iterator iter = modelProjects.iterator(); iter.hasNext();) {
                IProject nextProj = (IProject)iter.next();
                refreshExistingProject(nextProj);
            }
        }
    }

    private void loadProjects( final List projectFolders ) {
        // create the new project operation
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            protected void execute( IProgressMonitor monitor ) {
                IProject[] addedProjects = getProjectsToAdd(projectFolders);
                createProjects(addedProjects, monitor);
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            VdbUiConstants.Util.log(e.getTargetException());
        }
    }

    void createProjects( IProject[] projects,
                         IProgressMonitor monitor ) {
        NewModelProjectWorker worker = new NewModelProjectWorker();
        modelProjects = new ArrayList(projects.length);
        for (int i = 0; i < projects.length; i++) {

            if (!projects[i].exists()) {
                IProject tempModelProject = worker.createNewProject(null, projects[i].getName(), monitor);
                if (tempModelProject != null) modelProjects.add(tempModelProject);
            } else {
                VdbUiConstants.Util.log(VdbUiConstants.Util.getString("projectExists", projects[i].getName()));//$NON-NLS-1$ 
            }
        }
    }

    // private void createProjects(List projectFolders, IProgressMonitor monitor) {
    // NewModelProjectWorker worker = new NewModelProjectWorker();
    // modelProjects = new ArrayList(projectFolders.size());
    // for( Iterator iter = projectFolders.iterator(); iter.hasNext(); ) {
    // String nextProjectName = (String)iter.next();
    // if( !projectExists(nextProjectName) ) {
    // IProject tempModelProject = worker.createNewProject(null, nextProjectName, monitor);
    // if( tempModelProject != null )
    // modelProjects.add(tempModelProject);
    // } else {
    // System.out.println("The following project already exists: " + nextProjectName);
    // }
    // }
    // }

    // private boolean projectExists(String name) {
    // final IProject newProjectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
    // return newProjectHandle.exists();
    // }

    /*
     * Utility which unzips the files and stores them on the file system.
     */
    private List unZipFiles( ZipFile zipFile,
                             final IPath workspacePath ) {
        List projectFolders = new ArrayList();
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(new File(zipFile.getName()));
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            String fullName = null;
            String containerPath = workspacePath.makeAbsolute().toString();
            while ((entry = zis.getNextEntry()) != null) {
                fullName = containerPath + FILE_SEPARATOR + entry.getName();
                if (isValidModelFile(fullName)) {
                    // may need to create folder(s)
                    String path = fullName.substring(0, fullName.lastIndexOf(FILE_SEPARATOR));
                    File folder = new File(path);
                    if (!folder.exists()) folder.mkdirs();

                    if (!entry.isDirectory()) {
                        int count;
                        byte data[] = new byte[BUFFER];
                        // write the files to the disk
                        FileOutputStream fos = new FileOutputStream(fullName);
                        // System.out.println("Saving File: " + fullName);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    }
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (projectFolders.isEmpty()) return Collections.EMPTY_LIST;

        return projectFolders;
    }

    private boolean isValidModelFile( String fullName ) {
        if (fullName.indexOf(XMI_EXTENSION) > 0) {
            if (fullName.indexOf(MANIFEST_MODEL_NAME) > -1) return false;
            return true;
        } else if (fullName.indexOf(XSD_EXTENSION) > 0) {
            return true;
        }

        return false;
    }

    /**
     * /** This was confiscated from the IProjectSetSerializer.addToWorkspace() method. It checks each input project, confirms
     * with the user to overwrite if it exists.
     * 
     * @param referenceStrings List
     * @param context Object
     * @return projectList IProject[]
     * @since 4.2
     */
    public IProject[] getProjectsToAdd( List referenceStrings ) {
        final int size = referenceStrings.size();
        final IProject[] allProjects = new IProject[size];
        IProject[] addedProjects = new IProject[size];
        for (int i = 0; i < size; i++) {
            String projectName = (String)referenceStrings.get(i);
            allProjects[i] = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        }
        // Check if any projects will be overwritten, and warn the user.
        int iAdded = 0;

        for (int i = 0; i < size; i++) {

            IProject project = allProjects[i];
            if (project.exists()) {
                errorMessage = EXISTING_PROJECTS_MESSAGE;
                canExtract = false;
                addedProjects[iAdded++] = project;
            } else {
                addedProjects[iAdded++] = project;
            }
        }

        return addedProjects;
    }

    /**
     * utility method for refreshing an existing project in the workspace.
     * 
     * @param existingProject
     * @since 4.2
     */
    private void refreshExistingProject( IProject existingProject ) {
        try {
            existingProject.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException err) {
            ErrorDialog.openError(getShell(), getString("refreshProjectProblemTitle"), //$NON-NLS-1$
                                  VdbUiConstants.Util.getString("refreshProjectProblemMessage", existingProject.getName()), //$NON-NLS-1$
                                  err.getStatus());
        }
    }

    Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

    public boolean canExtract() {
        return canExtract;
    }

    public void setCanExtract( boolean canExtract ) {
        this.canExtract = canExtract;
    }

    public List getProjectFolders() {
        return projectFolders;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void copyVdbToLocation( final IContainer targetContainer ) {
        // We've got the vdbArchive
        if (vdbArchive != null) {
            // System.out.println("VdbModelExtractor.copyVdbToLocation():  Location = " + targetContainer);
            final String[] names = new String[1];
            names[0] = vdbFile.getAbsolutePath();
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    getShell().forceActive();
                    CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(getShell());
                    operation.copyFiles(names, targetContainer);
                }
            });
        }
    }
}

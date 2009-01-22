/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards.unzipproject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;


public class ProjectImporter
{
    private static class ProjectRecord
    {
        File projectSystemFile;
        Object projectArchiveFile;
        String projectName;
        Object parent;
        int level;
        IProjectDescription description;
        ProjectZipImportStructureProvider provider;

        ProjectRecord(Object file, Object parent, int level,
                ProjectZipImportStructureProvider entryProvider) throws CoreException, IOException
        {
            this.projectArchiveFile = file;
            this.parent = parent;
            this.level = level;
            this.provider = entryProvider;
            setProjectName();
        }

        private void setProjectName() throws CoreException, IOException
        {
            IProjectDescription newDescription = null;
            InputStream stream = provider
                    .getContents(projectArchiveFile);
            newDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(stream);
            stream.close();

            if (newDescription == null) {
                this.description = null;
                projectName = ""; //$NON-NLS-1$
            } else {
                this.description = newDescription;
                projectName = this.description.getName();
            }
        }
    }

    public ProjectImporter()
    {
    }

    public IProject[] importFromZipFile(IWorkspace workspace, String path) throws Exception
    {
        ZipFile sourceFile = new ZipFile(path);
        ProjectZipImportStructureProvider provider = ArchiveFileUtils
                .getZipStructureProvider(sourceFile);
        Object child = provider.getRoot();

        Collection<ProjectRecord> files = new ArrayList<ProjectRecord>();
        collectProjectFilesFromProvider(files, provider, child, 0);

        ProjectRecord[] selectedProjects = new ProjectRecord[files.size()];
        files.toArray(selectedProjects);
        IProject[] retval = new IProject[selectedProjects.length];
        for (int i = 0; i < selectedProjects.length; i++) {
            ProjectRecord record = selectedProjects[i];
            retval[i] = importProject(record, workspace);
        }
        IProject[] projects = retval;
        return projects;
    }

    private void collectProjectFilesFromProvider(Collection<ProjectRecord> files,
            ProjectZipImportStructureProvider provider, Object entry, int level) throws CoreException, IOException
    {

        List children = provider.getChildren(entry); 
        if (children == null) {
            children = new ArrayList(1);
        }
        Iterator childrenEnum = children.iterator();
        while (childrenEnum.hasNext()) {
            Object child = childrenEnum.next();
            if (provider.isFolder(child)) {
                collectProjectFilesFromProvider(files, provider, child, level + 1);
            }
            String elementLabel = provider.getLabel(child);
            if (elementLabel.equals(IProjectDescription.DESCRIPTION_FILE_NAME)) {
                files.add(new ProjectRecord(child, entry, level, provider));
            }
        }
    }

    private IProject importProject(final ProjectRecord record,
                                           IWorkspace workspace) throws Exception
    {

        String projectName = record.projectName;
        IProject project = workspace.getRoot().getProject(projectName);
        if (record.description == null) {
            record.description = workspace.newProjectDescription(projectName);
            IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());
            record.description.setLocation(locationPath);
        } else {
            record.description.setName(projectName);
        }

        ArrayList<Object> fileSystemObjects = new ArrayList<Object>();
        getFilesForProject(fileSystemObjects, record.provider, record.parent);
        record.provider.setStripLevel(record.level);
        ImportOperation operation = new ImportOperation(project.getFullPath(),
                                                        record.provider.getRoot(),
                                                        record.provider,
                                                        YES,
                                                        fileSystemObjects);
        operation.run(null);
        
        IStatus status = operation.getStatus();
        if (!status.isOK()) {
            Throwable exception = status.getException();
            throw new Exception(exception.getMessage(), exception);
        }
        return project;
    }

    protected void getFilesForProject(Collection<Object> files, IImportStructureProvider provider, Object entry)
    {
        List children = provider.getChildren(entry);
        Iterator childrenEnum = children.iterator();

        while (childrenEnum.hasNext()) {
            Object child = childrenEnum.next();
            // Add the child, this way we get every files except the project
            // folder itself which we don't want
            files.add(child);
            // We don't have isDirectory for tar so must check for children
            // instead
            if (provider.isFolder(child)) {
                getFilesForProject(files, provider, child);
            }
        }
    }

    private IOverwriteQuery YES = new IOverwriteQuery()
    {
        public String queryOverwrite(String pathString)
        {
            return YES;
        }
    };
}

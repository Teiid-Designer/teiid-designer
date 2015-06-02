/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.refactor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.XmiVdb;

/**
 *
 */
public class VdbResourceChange extends ResourceChange {

    private final Set<PathPair> replacements = new HashSet<PathPair>();
    private String projectName;
    private String parentFolder;
    private String vdbName;

    /**
     * @param projectName
     * @param parentFolder
     * @param vdbName
     */
    public VdbResourceChange(String projectName, String parentFolder, String vdbName) {
        super();
        CoreArgCheck.isNotNull(projectName);
        CoreArgCheck.isNotNull(parentFolder);
        CoreArgCheck.isNotNull(vdbName);

        this.projectName = projectName;
        this.parentFolder = parentFolder;
        this.vdbName = vdbName;
    }

    /**
     * @param vdbFile
     */
    public VdbResourceChange(IFile vdbFile) {
        super();
        CoreArgCheck.isNotNull(vdbFile);

        this.projectName = vdbFile.getProject().getName();
        this.parentFolder = vdbFile.getParent().getName();
        this.vdbName = vdbFile.getName();
    }

    private IFile findVdb(IWorkspaceRoot workspaceRoot, IProject project) {
        if (! project.isAccessible())
            return null;

        Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(vdbName, project);
        if (resources.isEmpty())
            return null;

        for (IFile vdbFile : resources) {
            IContainer container = vdbFile.getParent();
            if (parentFolder.equals(container.getName()))
                return vdbFile;

            /*
             * We have a vdb file but its parent does not match so either
             * a) the parent folder is being renamed
             * b) there is genuinely 2 vdb files with the same name in the project
             *
             * Ask the replacement about the containing folder
             */
            for (PathPair pathPair : replacements) {
                IPath sourcePath = new Path(pathPair.getSourcePath());
                IPath targetPath = new Path(pathPair.getTargetPath());

                if (container.getFullPath().isPrefixOf(sourcePath) || container.getFullPath().isPrefixOf(targetPath))
                    return vdbFile;
            }
        }

        // Genuinely cannot find the vdb file
        return null;
    }

    private IFile findVdb() {
        IWorkspaceRoot workspaceRoot = ModelerCore.getWorkspace().getRoot();
        IProject vdbProject = workspaceRoot.getProject(projectName);
        if (! vdbProject.isAccessible()) {
            /*
             * The project has been renamed and this vdbFile needs a bit of work to be found.
             * Try the first segment of the target path in the replacements.
             */
            for (PathPair pathPair : replacements) {
                IPath sourcePath = new Path(pathPair.getSourcePath());
                IPath targetPath = new Path(pathPair.getTargetPath());

                vdbProject = workspaceRoot.getProject(sourcePath.segment(0));
                IFile vdbFile = findVdb(workspaceRoot, vdbProject);
                if (vdbFile != null)
                    return vdbFile;

                vdbProject = workspaceRoot.getProject(targetPath.segment(0));
                vdbFile = findVdb(workspaceRoot, vdbProject);
                if (vdbFile != null)
                    return vdbFile;
            }
        }

        IFile vdbFile = findVdb(workspaceRoot, vdbProject);
        return vdbFile;
    }

    /**
     * @return the parentFolder
     */
    public String getParentFolder() {
        return this.parentFolder;
    }

    /**
     * @return the vdbName
     */
    public String getVdbName() {
        return this.vdbName;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.resource.ResourceChange#getModifiedResource()
     */
    @Override
    protected IResource getModifiedResource() {
        return findVdb();
    }

    private Change getUndoChange() {
        VdbResourceChange change = new VdbResourceChange(projectName, parentFolder, vdbName);

        for (PathPair pathPair : replacements) {
            // Creates a reversed path pair
            change.addReplacement(pathPair.getTargetPath(), pathPair.getSourcePath());
        }

        return change;
    }

    /**
     * @param monitor
     * @return status
     */
    protected IStatus performTask(IProgressMonitor monitor) {
        // Refresh the project so that the new resource may be found
        // and a model resource extracted

        IFile vdbFile = findVdb();
        if (vdbFile == null) {
            /*
             * Cannot find the vdb file so cannot do any refreshing
             */
            String msg = VdbPlugin.UTIL.getString(getClass().getSimpleName() + ".syncVdbFailure", projectName, parentFolder, vdbName); //$NON-NLS-1$
            IStatus status = new Status(IStatus.WARNING, VdbPlugin.ID, msg);
            return status;
        }

        try {
            vdbFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

            // Synchronise will add the new resource
            VdbUtil.synchronizeVdb(vdbFile, false, true);

            // This should clean up and remove old resources
            Vdb actualVdb = new XmiVdb(vdbFile, monitor);
            for (VdbEntry entry : actualVdb.getModelEntries()) {
                for (PathPair pathPair : replacements) {
                    if (entry.getName().equals(pathPair.getSourcePath())) {
                        actualVdb.removeEntry(entry);
                        actualVdb.save(monitor);
                    }
                }
            }
        } catch (Exception ex) {
            return new Status(IStatus.ERROR, VdbPlugin.ID, ex.getMessage());
        }

        return Status.OK_STATUS;
    }

    /**
     * @param replacedResourcePaths
     */
    public void addReplacements(Set<PathPair> replacedResourcePaths) {
        replacements.addAll(replacedResourcePaths);
    }

    /**
     * @param invalidResourcePath
     * @param newResourcePath
     */
    public void addReplacement(String invalidResourcePath, String newResourcePath) {
        PathPair resourcePair = new PathPair(invalidResourcePath, newResourcePath);
        replacements.add(resourcePair);
    }

    /**
     * @return the resource replacement paths
     */
    public Set<PathPair> getReplacedResources() {
        return replacements;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.Change#getName()
     */
    @Override
    public String getName() {
        return VdbPlugin.UTIL.getString(getClass().getSimpleName() + ".name", vdbName); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public Change perform(IProgressMonitor pm) {
        Job job = new Job(getName()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return performTask(monitor);
            }
        };
    
        // Start the Job
        job.schedule();
        
        return getUndoChange();
    }
}

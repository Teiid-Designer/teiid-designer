/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.refactor;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.VdbUtil;

/**
 *
 */
public class VdbResourceChange extends ResourceChange {

    private final IFile vdbFile;
    
    private final Set<PathPair> replacements = new HashSet<PathPair>();

    public VdbResourceChange(IFile vdbFile) {
        super();
        CoreArgCheck.isNotNull(vdbFile);
        this.vdbFile = vdbFile;
    }
    
    public IFile getVdb() {
        return vdbFile;
    }

    @Override
    protected IResource getModifiedResource() {
        return vdbFile;
    }

    private Change getUndoChange() {
        VdbResourceChange change = new VdbResourceChange(vdbFile);

        for (PathPair pathPair : replacements) {
            // Creates a reversed path pair
            change.addReplacement(pathPair.getTargetPath(), pathPair.getSourcePath());
        }

        return change;
    }

    protected IStatus performTask(IProgressMonitor monitor) {
        // Refresh the project so that the new resource may be found
        // and a model resource extracted
        IProject project = vdbFile.getProject();
        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } catch (CoreException ex) {
            return new Status(IStatus.ERROR, VdbPlugin.ID, ex.getMessage());
        }

        // Synchronise will add the new resource
        VdbUtil.synchronizeVdb(vdbFile, false);

        // This should clean up and remove old resources
        Vdb actualVdb = new Vdb(vdbFile, monitor);
        for (VdbModelEntry entry : actualVdb.getModelEntries()) {
            for (PathPair pathPair : replacements) {
                if (entry.getName().equals(pathPair.getSourcePath())) {
                    actualVdb.removeEntry(entry);
                    actualVdb.save(monitor);
                }
            }
        }

        return Status.OK_STATUS;
    }

    public void addReplacements(Set<PathPair> replacedResourcePaths) {
        replacements.addAll(replacedResourcePaths);
    }

    public void addReplacement(String invalidResourcePath, String newResourcePath) {
        PathPair resourcePair = new PathPair(invalidResourcePath, newResourcePath);
        replacements.add(resourcePair);
    }

    public Set<PathPair> getReplacedResources() {
        return replacements;
    }

    @Override
    public String getName() {
        return VdbPlugin.UTIL.getString(getClass().getSimpleName() + ".name", vdbFile.getName()); //$NON-NLS-1$
    }

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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelFolder;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * ModelProjectImpl
 * 
 * @since 4.0
 */
public class ModelProjectImpl extends OpenableImpl implements IProjectNature, ModelProject {

    /**
     * Whether the underlying file system is case sensitive.
     */
    protected static final boolean IS_CASE_SENSITIVE = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Name of file containing custom project preferences
     */
    public static final String PREF_FILENAME = ".mprefs"; //$NON-NLS-1$

    /**
     * The platform project this <code>ModelProject</code> is based on
     * 
     * @since 4.0
     */
    protected IProject fProject;

    /**
     * The platform project this <code>ModelProject</code> is based on
     * 
     * @since 4.0
     */
    protected IPath path;

    /**
     * Constructor needed for {@link IProject#getNature(java.lang.String)} and {@link IProject#addNature()}.
     * 
     * @see #setProject
     * @since 4.0
     */
    public ModelProjectImpl() {
        super(MODEL_PROJECT, null, null);
    }

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelProjectImpl( final IProject project,
                             final ModelWorkspaceItem parent ) {
        super(MODEL_PROJECT, parent, project.getName());
        fProject = project;
    }

    /**
     * Returns a canonicalized path from the given external path. Note that the return path contains the same number of segments
     * and it contains a device only if the given path contained one.
     * 
     * @see java.io.File for the definition of a canonicalized path
     */
    public static IPath canonicalizedPath( IPath externalPath ) {

        if (externalPath == null) {
            return null;
        }

        if (IS_CASE_SENSITIVE) {
            return externalPath;
        }

        // if not external path, return original path
        IWorkspace workspace = ModelerCore.getWorkspace();
        if (workspace == null) {
            return externalPath; // protection during shutdown (30487)
        }
        if (workspace.getRoot().findMember(externalPath) != null) {
            return externalPath;
        }

        IPath canonicalPath = null;
        try {
            canonicalPath = new Path(new File(externalPath.toOSString()).getCanonicalPath());
        } catch (IOException e) {
            // default to original path
            return externalPath;
        }

        IPath result;
        int canonicalLength = canonicalPath.segmentCount();
        if (canonicalLength == 0) {
            // the java.io.File canonicalization failed
            return externalPath;
        } else if (externalPath.isAbsolute()) {
            result = canonicalPath;
        } else {
            // if path is relative, remove the first segments that were added by the java.io.File canonicalization
            // e.g. 'lib/classes.zip' was converted to 'd:/myfolder/lib/classes.zip'
            int externalLength = externalPath.segmentCount();
            if (canonicalLength >= externalLength) {
                result = canonicalPath.removeFirstSegments(canonicalLength - externalLength);
            } else {
                return externalPath;
            }
        }

        // keep device only if it was specified (this is because File.getCanonicalPath() converts '/lib/classed.zip' to
        // 'd:/lib/classes/zip')
        if (externalPath.getDevice() == null) {
            result = result.setDevice(null);
        }
        return result;
    }

    /**
     * @since 4.0
     */
    private void addToBuildSpec( final String builderId ) throws CoreException {
        final IProject project = getProject();
        final IProjectDescription desc = project.getDescription();
        ICommand cmd = getBuilderCommand(desc, builderId);
        if (cmd == null) {
            // Add a new build spec as the first build spec
            cmd = desc.newCommand();
            cmd.setBuilderName(builderId);
            final ICommand[] cmds = desc.getBuildSpec();
            final ICommand[] newCmds = new ICommand[cmds.length + 1];
            System.arraycopy(cmds, 0, newCmds, 1, cmds.length);
            newCmds[0] = cmd;
            desc.setBuildSpec(newCmds);
            project.setDescription(desc, null);
        }
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.core.resources.IProjectNature#configure()
     * @since 4.0
     */
    public void configure() throws CoreException {
        addToBuildSpec(ModelerCore.BUILDER_ID);
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     * @since 4.0
     */
    public void deconfigure() throws CoreException {
        removeFromBuildSpec(ModelerCore.BUILDER_ID);
    }

    /**
     * @see com.metamatrix.modeler.core.Openable
     * @since 4.0
     */
    @Override
    protected boolean generateInfos( final OpenableModelWorkspaceItemInfo info,
                                     final IProgressMonitor pm,
                                     final Map newElements,
                                     final IResource underlyingResource ) throws ModelWorkspaceException {
        boolean validInfo = false;
        try {
            if (getProject().isOpen()) {
                // put the info now, because computing the roots requires it
                ModelWorkspaceManager.getModelWorkspaceManager().putInfo(this, info);

                // compute the pkg fragment roots
                updatePackageFragmentRoots();

                // only valid if reaches here
                validInfo = true;
            }
        } finally {
            if (!validInfo) {
                ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
            }
        }
        return validInfo;
    }

    /**
     * @since 4.0
     */
    private ICommand getBuilderCommand( final IProjectDescription description,
                                        final String builderId ) {
        final ICommand[] cmds = description.getBuildSpec();
        for (int ndx = cmds.length; --ndx >= 0;) {
            final ICommand cmd = cmds[ndx];
            if (cmd.getBuilderName().equals(builderId)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelProject#getNonModelingResources()
     * @since 4.0
     */
    public Object[] getNonModelingResources() throws ModelWorkspaceException {
        return ((ModelProjectInfo)getItemInfo()).getNonModelResources(this);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem#getPath()
     * @since 4.0
     */
    public IPath getPath() {
        if (this.path == null) {
            return this.getProject().getFullPath();
        }
        return this.path;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem#getPath()
     * @since 4.0
     */
    public void setPath( IPath path ) {
        this.path = path;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelProject#getProject()
     * @since 4.0
     */
    public IProject getProject() {
        return fProject;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem#getResource()
     * @since 4.0
     */
    public IResource getResource() {
        return this.getProject();
    }

    /**
     * Removes the given builder from the build spec for the given project.
     * 
     * @since 4.0
     */
    private void removeFromBuildSpec( final String builderId ) throws CoreException {
        final IProject project = getProject();
        final IProjectDescription desc = project.getDescription();
        final ICommand[] cmds = desc.getBuildSpec();
        for (int ndx = cmds.length; --ndx >= 0;) {
            final ICommand cmd = cmds[ndx];
            if (cmd.getBuilderName().equals(builderId)) {
                final ICommand[] newCmds = new ICommand[cmds.length - 1];
                System.arraycopy(cmds, 0, newCmds, 0, ndx);
                System.arraycopy(cmds, ndx + 1, newCmds, ndx, cmds.length - ndx - 1);
                desc.setBuildSpec(newCmds);
                project.setDescription(desc, null);
                return;
            }
        }
    }

    /**
     * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
     * @since 4.0
     */
    public void setProject( final IProject project ) {
        this.fProject = project;
    }

    /**
     * Convenience method that returns the specific type of info for a ModelProject.
     */
    protected ModelProjectInfo getModelProjectInfo() throws ModelWorkspaceException {
        return (ModelProjectInfo)getItemInfo();
    }

    /**
     * Reset the collection of package fragment roots (local ones) - only if opened. Need to check *all* package fragment roots in
     * order to reset NameLookup
     * 
     * @since 4.0
     */
    public void updatePackageFragmentRoots() throws ModelWorkspaceException {
        if (this.isOpen()) {
            boolean failed = false;
            try {
                ModelProjectInfo info = getModelProjectInfo();
                info.setNonModelResources(null);
                info.setChildren(computeModelResources());
            } catch (ModelWorkspaceException e) {
                failed = true;
                throw e;
            } catch (RuntimeException e) {
                failed = true;
                throw e;
            } finally {
                if (failed) {
                    try {
                        close(); // could not do better
                    } catch (ModelWorkspaceException ex) {
                    }
                }
            }
        }
    }

    public ModelWorkspaceItem findModelWorkspaceItem( IResource resource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);
        return ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(resource);
    }

    /**
     * Used by the {@link #computeModelResources(IContainer, ArrayList)} method.
     * 
     * @param resource
     * @return
     */
    protected ModelResource createModelResource( final IFile resource ) {
        ModelWorkspaceItem parent = findParent(resource);
        if (!ModelUtil.isVdbArchiveFile(resource)) {
            return new ModelResourceImpl(parent, resource.getName(), false);
        }
        return new ModelResourceImpl(parent, resource.getName());
    }

    /**
     * Used by the {@link #computeModelResources(IContainer, ArrayList)} method.
     * 
     * @param resource
     * @return
     */
    protected ModelFolder createModelFolder( final IFolder folder ) {
        ModelWorkspaceItem parent = findParent(folder);
        return new ModelFolderImpl(folder, parent);
    }

    /**
     * @param resource
     * @return
     */
    private ModelWorkspaceItem findParent( IResource resource ) {
        final IContainer iParent = resource.getParent();
        if (iParent instanceof IProject) {
            return this;
        }

        return this.getModelWorkspace().getParent(resource);
    }

    public ModelWorkspaceItem[] computeModelResources() {
        final ArrayList accumulatedModelResources = new ArrayList();
        computeModelResources(this.getProject(), accumulatedModelResources);
        final ModelWorkspaceItem[] results = new ModelWorkspaceItem[accumulatedModelResources.size()];
        accumulatedModelResources.toArray(results);
        return results;
    }

    public void computeModelResources( final IContainer container,
                                       final ArrayList accumulatedModelResources ) {
        try {
            // Iterate through the container's immediate children and add them
            final IResource[] children = container.members();
            for (int i = 0; i < children.length; ++i) {
                final IResource child = children[i];
                if (IResource.FILE == child.getType()) {
                    // If the resource is a model file, then add it to the accumulated list
                    if (ModelUtil.isModelFile(child)) {
                        final ModelResource mdlResource = ModelProjectImpl.this.createModelResource((IFile)child);
                        accumulatedModelResources.add(mdlResource);

                        // If the IFile extension does not match the set of well-known model file extensions with
                        // a case-sensitive check but does match when the check is case-insensitive then error (defect 17709)
                    } else if (!ModelFileUtil.isModelFileExtension(child.getFileExtension(), true)
                               && ModelFileUtil.isModelFileExtension(child.getFileExtension(), false)) {
                        final String actualFileName = child.getName();
                        final String actualExtension = child.getFileExtension();
                        final int endIndex = actualFileName.length() - actualExtension.length();
                        final String expectedFileName = actualFileName.substring(0, endIndex) + actualExtension.toLowerCase();

                        final Object[] params = new Object[] {actualFileName, expectedFileName};
                        final String msg = ModelerCore.Util.getString("ModelerCore.file_extension_not_correct_case_please_rename_file", params); //$NON-NLS-1$
                        ModelerCore.Util.log(IStatus.ERROR, msg);
                    }
                } else if (IResource.FOLDER == child.getType()) {
                    final ModelFolder mdlFolder = ModelProjectImpl.this.createModelFolder((IFolder)child);
                    accumulatedModelResources.add(mdlFolder);
                }
            }
        } catch (CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ModelProjectImpl.Error_while_computing_ModelResource_instances_for_ModelProject", this)); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.workspace.OpenableImpl#createItemInfo()
     */
    @Override
    protected OpenableModelWorkspaceItemInfo createItemInfo() {
        return new ModelProjectInfo();
    }

}

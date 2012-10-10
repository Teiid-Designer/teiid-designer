/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.util.ArrayList;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelFileUtil;


/**
 * ModelFolderImpl
 *
 * @since 8.0
 */
public class ModelFolderImpl extends OpenableImpl implements ModelFolder {

    // ############################################################################################################################
    // # Variables #
    // ############################################################################################################################

    /**
     * The platform project this <code>ModelProject</code> is based on
     * 
     * @since 4.0
     */
    protected IFolder ifolder;

    /**
     * The platform project this <code>ModelProject</code> is based on
     * 
     * @since 4.0
     */
    protected IPath path;

    // ############################################################################################################################
    // # Constructors #
    // ############################################################################################################################

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelFolderImpl( final IFolder folder,
                            final ModelWorkspaceItem parent ) {
        super(MODEL_FOLDER, parent, folder.getName());
        ifolder = folder;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelProject#getNonModelingResources()
     * @since 4.0
     */
    @Override
	public Object[] getNonModelingResources() throws ModelWorkspaceException {
        return ((ModelFolderInfo)getItemInfo()).getNonModelResources(this);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     * @since 4.0
     */
    @Override
	public IPath getPath() {
        if (this.path == null) {
            return this.getFolder().getFullPath();
        }
        return this.path;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     * @since 4.0
     */
    public void setPath( IPath path ) {
        this.path = path;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelProject#getProject()
     * @since 4.0
     */
    @Override
	public IFolder getFolder() {
        return ifolder;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getResource()
     * @since 4.0
     */
    @Override
	public IResource getResource() {
        return this.getFolder();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
    public IResource getUnderlyingResource() {
        return getResource();
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable
     * @since 4.0
     */
    @Override
    protected boolean generateInfos( final OpenableModelWorkspaceItemInfo info,
                                     final IProgressMonitor pm,
                                     final Map newElements,
                                     final IResource underlyingResource ) throws ModelWorkspaceException {
        boolean validInfo = false;
        try {
            // put the info now, because computing the roots requires it
            ModelWorkspaceManager.getModelWorkspaceManager().putInfo(this, info);

            // compute the pkg fragment roots
            updateFolderContents();

            // only valid if reaches here
            validInfo = true;
        } finally {
            if (!validInfo) {
                ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
            }
        }
        return validInfo;
    }

    /**
     * Convenience method that returns the specific type of info for a ModelProject.
     */
    protected ModelFolderInfo getModelFolderInfo() throws ModelWorkspaceException {
        return (ModelFolderInfo)getItemInfo();
    }

    /**
     * Reset the collection of package fragment roots (local ones) - only if opened. Need to check *all* package fragment roots in
     * order to reset NameLookup
     * 
     * @since 4.0
     */
    public void updateFolderContents() throws ModelWorkspaceException {
        if (this.isOpen()) {
            boolean failed = false;
            try {
                ModelFolderInfo info = getModelFolderInfo();
                info.setNonModelResources(null);
                info.setChildren(computeModelFolderChildren());

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

    public ModelWorkspaceItem[] computeModelFolderChildren() {
        final ArrayList accumulatedModelResources = new ArrayList();
        computeModelFolderResources(this.getFolder(), accumulatedModelResources);
        final ModelWorkspaceItem[] results = new ModelWorkspaceItem[accumulatedModelResources.size()];
        accumulatedModelResources.toArray(results);
        return results;
    }

    public void computeModelFolderResources( final IContainer container,
                                             final ArrayList accumulatedModelResources ) {
        if (container.exists()) {
            try {
                // Iterate through the container's immediate children and add them
                final IResource[] children = container.members();
                for (int i = 0; i < children.length; ++i) {
                    final IResource child = children[i];
                    if (IResource.FILE == child.getType()) {
                        // If the resource is a model file, then add it to the accumulated list
                        if (ModelUtil.isModelFile(child)) {
                            final ModelResource mdlResource = ModelFolderImpl.this.createModelResource((IFile)child);
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
                        final ModelFolder mdlFolder = ModelFolderImpl.this.createModelFolder((IFolder)child);
                        accumulatedModelResources.add(mdlFolder);
                    }
                }
            } catch (CoreException e) {
                ModelerCore.Util.log(IStatus.ERROR,
                                     e,
                                     ModelerCore.Util.getString("ModelFolderImpl.Error_while_computing_ModelWorkspaceItem_instances_for_ModelFolder", this)); //$NON-NLS-1$
            }
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.core.workspace.OpenableImpl#createItemInfo()
     */
    @Override
    protected OpenableModelWorkspaceItemInfo createItemInfo() {
        return new ModelFolderInfo();
    }

    /**
     * Used by the {@link #computeModelResources(IContainer, ArrayList)} method.
     * 
     * @param resource
     * @return
     */
    protected ModelResource createModelResource( final IFile resource ) {
        return new ModelResourceImpl(this, resource.getName());
    }

    /**
     * Used by the {@link #computeModelResources(IContainer, ArrayList)} method.
     * 
     * @param resource
     * @return
     */
    protected ModelFolder createModelFolder( final IFolder folder ) {
        return new ModelFolderImpl(folder, this);
    }

    /**
     * Return the {@link ModelResource model resource} for the specified resource and contained by this project.
     * <p>
     * This method returns the same result as {@link #getChildren()}.
     * </p>
     * 
     * @return the {@link ModelWorkspaceItem} instance contained by this project item that represents the suppplied resource; may
     *         be null if the supplied resource doesn't represent a model or a folder
     * @throws ModelWorkspaceException
     */
    @Override
	public ModelWorkspaceItem getModelWorkspaceItem( IResource resource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);
        final ModelWorkspaceItem[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            final ModelWorkspaceItem child = children[i];
            final IResource thatResource = child.getCorrespondingResource();
            if (resource.equals(thatResource)) {
                return child;
            }
        }
        return null;
    }
}

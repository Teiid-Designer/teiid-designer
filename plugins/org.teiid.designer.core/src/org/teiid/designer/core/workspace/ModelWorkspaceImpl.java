/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.refactor.ModelResourceCollectorVisitor;


/**
 * ModelWorkspaceImpl
 *
 * @since 8.0
 */
public class ModelWorkspaceImpl extends OpenableImpl implements ModelWorkspace {
    // ============================================================================================================================
    // Constants

    private static final String[] MODEL_NATURES = new String[] {ModelerCore.NATURE_ID};

    /**
     * A set of java.io.Files used as a cache of external jars that are known to be existing. Note this cache is kept for the
     * whole session.
     */
    public static HashSet existingExternalFiles = new HashSet();

    /**
     * Flushes the cache of external files known to be existing.
     */
    public static void flushExternalFileCache() {
        existingExternalFiles = new HashSet();
    }

    /**
     * Helper method - returns the targeted item (IResource if internal or java.io.File if external), or null if unbound Internal
     * items must be referred to using container relative paths.
     */
    public static Object getTarget( IContainer container,
                                    IPath path,
                                    boolean checkResourceExistence ) {

        if (path == null) return null;

        // lookup - inside the container
        if (path.getDevice() == null) { // container relative paths should not contain a device
            // (see http://dev.eclipse.org/bugs/show_bug.cgi?id=18684)
            // (case of a workspace rooted at d:\ )
            IResource resource = container.findMember(path);
            if (resource != null) {
                if (!checkResourceExistence || resource.exists()) return resource;
                return null;
            }
        }

        // if path is relative, it cannot be an external path
        // (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
        if (!path.isAbsolute()) return null;

        // lookup - outside the container
        File externalFile = new File(path.toOSString());
        if (!checkResourceExistence) {
            return externalFile;
        } else if (existingExternalFiles.contains(externalFile)) {
            return externalFile;
        } else {
            if (ModelWorkspaceManager.ZIP_ACCESS_VERBOSE) {
                System.out.println("(" + Thread.currentThread() + ") [ModelWorkspaceImpl.getTarget(...)] Checking existence of " + path.toString()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (externalFile.exists()) {
                // cache external file
                existingExternalFiles.add(externalFile);
                return externalFile;
            }
        }
        return null;
    }

    /**
     * Construct an instance of ModelProjectImpl.
     */
    ModelWorkspaceImpl() {
        super(MODEL_WORKSPACE, null, "" /*workspace has empty name*/); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem
     */
    @Override
	public IPath getPath() {
        return Path.ROOT;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem
     */
    @Override
	public IResource getResource() {
        return getWorkspace().getRoot();
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable
     */
    @Override
    public IResource getUnderlyingResource() {
        return null;
    }

    /**
     * Returns the workbench associated with this object.
     */
    @Override
    public IWorkspace getWorkspace() {
        return ModelerCore.getWorkspace();
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable
     */
    @Override
    protected boolean generateInfos( final OpenableModelWorkspaceItemInfo info,
                                     final IProgressMonitor pm,
                                     final Map newElements,
                                     final IResource underlyingResource ) {

        ModelWorkspaceManager.getModelWorkspaceManager().putInfo(this, info);
        // determine my children
        IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (int i = 0, max = projects.length; i < max; i++) {
            IProject project = projects[i];
            if (ModelerCore.hasModelNature(project)) {
                info.addChild(getModelProject(project));
            }
        }
        return true;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelProject(java.lang.String)
     */
    @Override
	public ModelProject findModelProject( final String name ) {
        CoreArgCheck.isNotNull(name);
        try {
            final ModelProject[] projects = getModelProjects();
            for (int ndx = projects.length; --ndx >= 0;) {
                final ModelProject project = projects[ndx];
                if (name.equals(project.getItemName())) {
                    return project;
                }
            }
        } catch (ModelWorkspaceException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelProject(IResource)
     */
    @Override
	public ModelProject findModelProject( final IResource resource ) {
        CoreArgCheck.isNotNull(resource);

        // if(!ModelerCore.hasModelNature(resource.getProject())) {
        // return null;
        // }

        IProject rsrcProject = null;
        switch (resource.getType()) {
            case IResource.FOLDER:
                rsrcProject = ((IFolder)resource).getProject();
                break;
            case IResource.FILE:
                rsrcProject = ((IFile)resource).getProject();
                break;
            case IResource.PROJECT:
                rsrcProject = (IProject)resource;
                break;
            case IResource.ROOT:
            default:
                // throw new IllegalArgumentException("Invalid resource type, {0}, for IResource {1}");
        }

        try {
            final ModelProject[] projects = getModelProjects();
            for (int ndx = projects.length; --ndx >= 0;) {
                final ModelProject mdlProject = projects[ndx];
                if (rsrcProject != null && rsrcProject.equals(mdlProject.getResource())) {
                    return mdlProject;
                }
            }
        } catch (ModelWorkspaceException e) {
            // do nothing
        }
        return null;
    }

    @Override
	public ModelWorkspaceItem getParent( final IResource resource ) {
        CoreArgCheck.isNotNull(resource);

        IProject project = resource.getProject();
        if (project.isAccessible() && !ModelerCore.hasModelNature(project)) {
            return null;
        }

        // If the resource is an IProject... return the resource
        if (resource instanceof IProject) {
            return this;
        }

        // If the parent is null, return null
        final IResource parent = resource.getParent();
        if (parent == null) {
            return null;
        }

        // Calculate the parent path from the given resource
        final IPath path = resource.getFullPath();
        final IPath parentPath = path.removeLastSegments(1);

        // Find the workspaceItem for the parent path1
        return getWorkspaceItem(parentPath);
    }

    public ModelWorkspaceItem getWorkspaceItem( final IPath path,
                                                int resourceType ) {
        CoreArgCheck.isNotNull(path);
        try {
            // first get all the projects
            ModelProject[] projects = getModelProjects();
            for (int i = 0; i < projects.length; i++) {
                ModelProject project = projects[i];
                if (resourceType == IResource.PROJECT) {
                    if (project.getPath().equals(path)) {
                        return project;
                    }
                } else {
                    if (!project.isOpen()) {
                        continue;
                    }

                    // If the path only contains the project then we cannot match it
                    // to a non-project type so return null
                    if (path.segmentCount() < 2) {
                        return null;
                    }
                    // If the first segment is not this project's name then skip it
                    if (!path.segment(0).equals(project.getProject().getName())) {
                        continue;
                    }
                    // Iterate over all the path segments navigating to the child by name
                    ModelWorkspaceItem item = project;
                    final String[] segments = path.segments();
                    for (int j = 1; j < segments.length; j++) {
                        final String segment = segments[j];
                        if (!item.exists()) {
                            // Must be in the process of closing (see defect 10957) ...
                            return null;
                        }
                        item = item.getChild(segment);
                        if (item == null) {
                            break;
                        } else if (item.getPath().equals(path)) {
                            return item;
                        }
                    }
                    // ModelWorkspaceItem[] children = project.getChildren();
                    // return recursiveLookUp(children, path);
                }
            }
        } catch (ModelWorkspaceException e) {
            // do nothing
        }
        return null;
    }

    public ModelWorkspaceItem getWorkspaceItem( final IPath path ) {
        CoreArgCheck.isNotNull(path);
        try {
            // first get all the projects
            ModelProject[] projects = getModelProjects();
            for (int i = 0; i < projects.length; i++) {
                ModelProject project = projects[i];
                if (!project.exists()) {
                    continue;
                }
                if (!project.isOpen()) {
                    // See if the underlying project is open ...
                    final IProject iproj = (IProject)project.getResource();
                    if (!iproj.isOpen()) {
                        continue;
                    }
                    // Try to open the ModelProject, since the IProject is open ...
                    project.open(null);
                    if (!project.isOpen()) {
                        continue; // couldn't open it!
                    }
                }
                if (project.getPath().equals(path)) {
                    return project;
                }
                // Iterate over all the path segments navigating to the child by name
                ModelWorkspaceItem item = project;
                final String[] segments = path.segments();
                for (int j = 1; j < segments.length; j++) {
                    final String segment = segments[j];
                    if (!item.exists()) {
                        // Must be in the process of closing (see defect 10957) ...
                        return null;
                    }
                    final IResource itemResource = item.getResource();
                    item = item.getChild(segment);
                    if (item == null) {
                        // May be a newly created IResource for which there is yet no ModelWorkspaceItem
                        if (itemResource instanceof IContainer) {
                            final IContainer itemContainer = (IContainer)itemResource;
                            final IResource child = itemContainer.findMember(segment);
                            if (child != null) {
                                // Find the ModelWorkspaceItem ...
                                item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(child, true);
                            }
                        }
                    }
                    if (item == null) {
                        break;
                    } else if (item.getPath().equals(path)) {
                        return item;
                    }
                }
            }
        } catch (ModelWorkspaceException e) {
            // do nothing
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#createModelProject(java.lang.String, java.lang.String,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
	public ModelProject createModelProject( final String name,
                                            final IPath path,
                                            final IProgressMonitor monitor ) throws CoreException {
        CoreArgCheck.isNotNull(name);
        // Check if project already exists
        if (findModelProject(name) != null) {
            throw new ModelWorkspaceException(ModelerCore.Util.getString("ModelWorkspaceImpl.cannotCreateModelProject", name)); //$NON-NLS-1$
        }
        // Validate name
        final IWorkspace workspace = ModelerCore.getWorkspace();
        final IStatus status = workspace.validateName(name, IResource.PROJECT);
        if (!status.isOK()) {
            throw new ModelWorkspaceException(new ModelStatusImpl(status.getSeverity(), status.getCode(), status.getMessage()));
        }
        // Create new model project
        final IProject project = workspace.getRoot().getProject(name);
        final IProjectDescription desc = workspace.newProjectDescription(project.getName());
        desc.setLocation(path);
        desc.setNatureIds(MODEL_NATURES);
        final IWorkspaceRunnable op = new IWorkspaceRunnable() {
            @Override
			public void run( final IProgressMonitor monitor ) throws CoreException {
                project.create(desc, monitor);
                project.open(monitor);
            }
        };
        workspace.run(op, monitor);
        return new ModelProjectImpl(project, this);
    }

    /**
     * Returns the active Model project associated with the specified resource, or <code>null</code> if no Model project yet
     * exists for the resource.
     * 
     * @exception IllegalArgumentException if the given resource is not one of an IProject, IFolder, IRoot or IFile.
     * @see ModelWorkspace
     */
    @Override
	public ModelProject getModelProject( final IResource resource ) {
        if (!ModelerCore.hasModelNature(resource.getProject())) {
            return null;
        }
        ModelProject modelProject = findModelProject(resource);
        if (modelProject == null) {
            IProject project = resource.getProject();
            switch (resource.getType()) {
                case IResource.FOLDER:
                case IResource.FILE:
                case IResource.PROJECT:
                    return new ModelProjectImpl(project, this);
                case IResource.ROOT:
                    return null;
                default:
                    throw new IllegalArgumentException(
                                                       ModelerCore.Util.getString("ModelWorkspaceImpl.Invalid_resource_for_ModelProject", resource, this)); //$NON-NLS-1$
            }
        }

        return modelProject;
    }

    /**
     * @see ModelWorkspace
     */
    @Override
	public ModelProject[] getModelProjects() throws ModelWorkspaceException {
        ArrayList list = getChildrenOfType(MODEL_PROJECT);
        ModelProject[] array = new ModelProject[list.size()];
        list.toArray(array);
        return array;

    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getModelResources()
     * @since 4.2
     */
    @Override
	public ModelResource[] getModelResources() throws CoreException {
        List temp = new ArrayList();
        ModelProject[] projects = getModelProjects();

        for (int i = 0; i < projects.length; ++i) {
            ModelProject mProject = projects[i];
            ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
            if (mProject != null && mProject.isOpen()) {
                mProject.getProject().accept(visitor);
                temp.addAll(visitor.getModelResources());
            }
        }

        ModelResource[] modelResources = new ModelResource[temp.size()];
        temp.toArray(modelResources);

        return modelResources;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getEmfResources()
     * @since 4.2
     */
    @Override
	public Resource[] getEmfResources() throws CoreException {
        Resource[] result = null;
        ModelResource[] modelResources = getModelResources();

        if (modelResources.length == 0) {
            result = new Resource[0];
        } else {
            List temp = new ArrayList(modelResources.length);

            for (int i = 0; i < modelResources.length; ++i) {
                try {
                    Resource resource = modelResources[i].getEmfResource();

                    if (resource != null) {
                        temp.add(resource);
                    }
                } catch (ModelWorkspaceException theException) {
                    // unable to load. could be a duplicate model or a corrupted model.
                    ModelerCore.Util.log(theException);
                }
            }

            result = (Resource[])temp.toArray(new Resource[temp.size()]);
        }

        return result;
    }

    /**
     * @see ModelWorkspace
     */
    @Override
	public Object[] getNonModelingResources() throws ModelWorkspaceException {
        return ((ModelWorkspaceInfo)getItemInfo()).getNonModelResources();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.core.workspace.OpenableImpl#createItemInfo()
     */
    @Override
    protected OpenableModelWorkspaceItemInfo createItemInfo() {
        return new ModelWorkspaceInfo();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
	public ModelResource findModelResource( final IResource resource ) {
        CoreArgCheck.isNotNull(resource);
        if (!ModelUtil.isModelFile(resource)) {
            return null;
        }
        return (ModelResource)getWorkspaceItem(resource.getFullPath(), IResource.FILE);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
	public ModelResource findModelResource( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        return ModelWorkspaceManager.getModelWorkspaceManager().findModelResource(resource);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(IPath)
     */
    @Override
	public ModelResource findModelResource( final IPath pathInWorkspace ) {
        CoreArgCheck.isNotNull(pathInWorkspace);
        try {
            ModelWorkspaceItem item = this;
            final String[] segments = pathInWorkspace.segments();
            for (int i = 0; i < segments.length; ++i) {
                final String segment = segments[i];
                if (item == null) {
                    break;
                }
                item = item.getChild(segment);
            }
            if (item == null || item.getItemType() != ModelWorkspaceItem.MODEL_RESOURCE) {
                return null;
            }
            return (ModelResource)item;
        } catch (ModelWorkspaceException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public ModelResource findModelResource( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        try {
            final Container container = ModelerCore.getModelContainer();
            Resource resource = ModelerCore.getModelEditor().findResource(container, eObject, false);
            if (resource != null) {
                return findModelResource(resource);
            }
        } catch (CoreException err) {
            ModelerCore.Util.log(err);
        }
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspace#addNotificationListener(org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener)
     */
    @Override
	public void addNotificationListener( ModelWorkspaceNotificationListener listener ) {
        CoreArgCheck.isNotNull(listener);
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(listener);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspace#removeNotificationListener(org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener)
     */
    @Override
	public void removeNotificationListener( ModelWorkspaceNotificationListener listener ) {
        CoreArgCheck.isNotNull(listener);
        ModelWorkspaceManager.getModelWorkspaceManager().removeNotificationListener(listener);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#addModelResourceReloadVetoListener(org.teiid.designer.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    @Override
	public void addModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
        CoreArgCheck.isNotNull(listener);
        ModelWorkspaceManager.getModelWorkspaceManager().addModelResourceReloadVetoListener(listener);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#removeModelResourceReloadVetoListener(org.teiid.designer.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    @Override
	public void removeModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
        CoreArgCheck.isNotNull(listener);
        ModelWorkspaceManager.getModelWorkspaceManager().removeModelResourceReloadVetoListener(listener);
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.IOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.util.TransactionUtil;
import com.metamatrix.modeler.core.workspace.ModelFolder;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelResourceReloadVetoListener;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.core.workspace.Openable;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil.XmiHeaderCache;

/**
 * ModelWorkspaceManager
 */
public class ModelWorkspaceManager implements XmiHeaderCache {

    /**
     * Returns the singleton ModelWorkspaceManager
     */
    public final static ModelWorkspaceManager getModelWorkspaceManager() {
        if (manager == null) {
            manager = new ModelWorkspaceManager();

            final IWorkspace workspace;
            try {
                // Start up the ModelWorkspaceManager ...
                workspace = ResourcesPlugin.getWorkspace();
                workspace.addResourceChangeListener(manager.getDeltaProcessor(), IResourceChangeEvent.PRE_BUILD
                                                                                 | IResourceChangeEvent.POST_BUILD
                                                                                 | IResourceChangeEvent.POST_CHANGE
                                                                                 | IResourceChangeEvent.PRE_DELETE
                                                                                 | IResourceChangeEvent.PRE_CLOSE);

                manager.initModelWorkspace(workspace);
            } catch (Throwable t) {
                if (!ModelerCore.HEADLESS) {
                    t.printStackTrace();
                    ModelerCore.Util.log(IStatus.ERROR,
                                         ModelerCore.Util.getString("ModelWorkspaceManager.Error_encountered_starting_ModelWorkspaceManager_1")); //$NON-NLS-1$
                }
            }
        }
        return manager;
    }

    public final static void shutdown() throws CoreException {
        if (manager != null) {
            try {
                // Remove the delta processor as a listener of resource change events
                final IWorkspace workspace = ResourcesPlugin.getWorkspace();
                workspace.removeResourceChangeListener(manager.getDeltaProcessor());

                // Shutdown the manager
                manager.shutdownManager();
                manager.getIndexManager().disposeAll();
            } catch (CoreException e) {
                throw e;
            } catch (Throwable t) {
                throw new CoreException(
                                        new Status(
                                                   IStatus.ERROR,
                                                   ModelerCore.PLUGIN_ID,
                                                   1,
                                                   ModelerCore.Util.getString("ModelWorkspaceManager.Error_encountered_shutting_down_ModelWorkspaceManager_2"), t)); //$NON-NLS-1$
            } finally {
                manager = null;
            }
        }
    }

    public static boolean VERBOSE = false;
    public static boolean ZIP_ACCESS_VERBOSE = false;

    /**
     * The singleton manager
     */
    private static ModelWorkspaceManager manager;

    /**
     * Unique handle to the model workspace.
     */
    private ModelWorkspaceImpl modelWorkspace = new ModelWorkspaceImpl();

    /**
     * Used to convert <code>IResourceDelta</code>s into <code>ModelWorkspaceDelta</code>s.
     */
    private final DeltaProcessor deltaProcessor;

    /**
     * Infos cache.
     */
    protected ModelWorkspaceCache cache = new ModelWorkspaceCache();

    // public static HashSet OptionNames = new HashSet(20);

    protected Container container;

    /**
     * Collection of listeners for workspace change events
     */
    private final Collection workspaceListeners = new ArrayList();

    /**
     * Collection of veto listeners for resource change events; each listener may veto the unloading and reloading of the
     * in-memory model.
     */
    private final Collection resourceReloadVetoListeners = new ArrayList();

    /**
     * Table from IProject to PerProjectInfo. NOTE: this object itself is used as a lock to synchronize creation/removal of per
     * project infos
     */
    protected Map perProjectInfo = new HashMap(5);

    /**
     * Used to indicate that one or more ModelResource instances have been marked as indexed to the given type.
     */
    private int indexType = ModelResource.NOT_INDEXED;

    /**
     * Used to keep a cache of IMarker-EObject maps to eliminate repetitive UUID/EObject resolving. Part of Defect 22362 to
     * improve XML Document editing performance. Improves time to decoration tree & table view items.
     */
    private ModelMarkerManager markerManager = new ModelMarkerManager();

    private Map FileToxmlHeaderMap = Collections.synchronizedMap(new HashMap());

    private ModelWorkspaceSearch modelWorkspaceSearch = new ModelWorkspaceSearch();

    private ModelWorkspaceIndexManager indexManager = new ModelWorkspaceIndexManager();

    /**
     * Construct an instance of ModelWorkspaceManager.
     */
    private ModelWorkspaceManager() {
        super();

        // Create the delta processor for resource change event
        this.deltaProcessor = new DeltaProcessor(this);

        // Add a ChangeNotifier to the container so that the workspace
        // manager can listen for changes to the EMF Resource instances
        // and mark the associated ModelResource as requiring indexing.
        try {
            addChangeNotifier(ModelerCore.getModelContainer());
        } catch (CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ModelWorkspaceManager.Error_adding_ChangeNotifier_to_the_model_container_{0}_1", e.getMessage())); //$NON-NLS-1$
        }
    }

    private void initModelWorkspace( final IWorkspace workspace ) throws CoreException {
        ArgCheck.isNotNull(workspace);

        // collect all resources in open projects
        ModelIResourceCollectorVisitor resourceVisitor = new ModelIResourceCollectorVisitor();
        workspace.getRoot().accept(resourceVisitor);

        // sort the resources, in order (IRoot, IPRoject, IFolder and IFile)
        List resources = resourceVisitor.getResources();
        IResourceComparator comparator = new IResourceComparator();
        Collections.sort(resources, comparator);

        // create model workspaceitems for each of these resources
        Iterator resourceIter = resources.iterator();
        while (resourceIter.hasNext()) {
            IResource resource = (IResource)resourceIter.next();
            ModelWorkspaceItem workspaceItem = create(resource, null);
            if (resource.getType() == IResource.PROJECT || resource.getType() == IResource.ROOT) {
                if (workspaceItem instanceof Openable) {
                    ((Openable)workspaceItem).open(null);
                }
            }
        }
        ModelUtil.setModelWorkspaceManagerInitialized();
    }

    /**
     * Return the reference to the resource change listener used by the ModelWorkspace manager for processing events.
     * 
     * @return
     */
    public DeltaProcessor getDeltaProcessor() {
        return this.deltaProcessor;
    }

    /**
     * Add a listener that would listen to and possibly veto the reloading of a ModelResource from the underlying file.
     */
    public void addModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
        ArgCheck.isNotNull(listener);
        if (this.resourceReloadVetoListeners.contains(listener)) {
            return;
        }
        this.resourceReloadVetoListeners.add(listener);
    }

    /**
     * Remove a listener that would listen to and possibly veto the reloading of a ModelResource from the underlying file.
     */
    public void removeModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
        ArgCheck.isNotNull(listener);
        this.resourceReloadVetoListeners.remove(listener);
    }

    /**
     * Remove all listeners that would listen to and possibly veto the reloading of a ModelResource from the underlying file.
     */
    public void removeAllModelResourceReloadVetoListeners() {
        this.resourceReloadVetoListeners.clear();
    }

    /**
     * Notify all workspace listeners that a clean has been initiated on the provided project
     * 
     * @param project the project on which the clean has been initiated.
     */
    public void notifyClean( IProject project ) {
        Collection listeners = getNotificationListeners();
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ModelWorkspaceNotificationListener listener = (ModelWorkspaceNotificationListener)iter.next();
            listener.notifyClean(project);
        }
    }

    /**
     * Return the listeners
     */
    Collection getModelResourceReloadVetoListeners() {
        return new ArrayList(this.resourceReloadVetoListeners); // create a copy to prevent concurrent modifications
    }

    public boolean canReload( final ModelResource modelResource ) {
        final Collection listeners = getModelResourceReloadVetoListeners(); // makes copy
        if (listeners.size() != 0) {
            // notify all listners about the notification
            final Iterator iter = getModelResourceReloadVetoListeners().iterator(); // makes copy
            while (iter.hasNext()) {
                final ModelResourceReloadVetoListener listener = (ModelResourceReloadVetoListener)iter.next();
                final boolean canReload = listener.canReload(modelResource);
                if (!canReload) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Add a listener that would listen to the workspace change events.
     */
    public void addNotificationListener( ModelWorkspaceNotificationListener listener ) {
        ArgCheck.isNotNull(listener);
        if (this.workspaceListeners.contains(listener)) {
            return;
        }
        this.workspaceListeners.add(listener);
    }

    /**
     * Remove a listener that would listen to the workspace change events.
     */
    public void removeNotificationListener( ModelWorkspaceNotificationListener listener ) {
        this.workspaceListeners.remove(listener);
    }

    /**
     * Remove a listener that would listen to the workspace change events.
     */
    public void removeAllNotificationListeners() {
        this.workspaceListeners.clear();
    }

    /**
     * Return the listeners
     */
    Collection getNotificationListeners() {
        return new ArrayList(this.workspaceListeners); // create a copy to prevent concurrent modifications
    }

    /**
     * Fire ModelWorkspaceNotification, iteratively make all registered listeners aware of this notification.
     */
    public void fire( ModelWorkspaceNotification notification ) throws CoreException {

        // System.out.println("\n[ModelWorkspaceManager.fire] TOP, notification is: " +
        // ((ModelWorkspaceNotificationImpl)notification).getNotificationTypePhrase() );

        ArgCheck.isNotNull(notification);
        IResourceDelta delta = notification.getDelta();
        int eventType = notification.getEventType();
        // The delta is null upon project close events
        boolean reloadedResource = false;
        // change the workspace to add or remove items if needed
        if (delta != null && notification.isPostChange()) {
            reloadedResource = changeWorkspace(notification);
        }
        // process predelete notifications to cleanup indexes and resources
        if (notification.isPreDelete()) {
            reloadedResource = changeWorkspace(notification);
        }

        if (eventType == ModelWorkspaceNotification.CLOSING) {
            // notifier should be a project but check to be sure
            if (notification.isProject() && (notification.getNotifier() instanceof IProject)) {
                // delete search indexes for closing project
                deleteIndexes((IProject)notification.getNotifier());
            }
        }

        // notify all listners about the notification
        Iterator listenIter = getNotificationListeners().iterator();
        while (listenIter.hasNext()) {
            ModelWorkspaceNotificationListener listener = (ModelWorkspaceNotificationListener)listenIter.next();
            switch (eventType) {
                case Notification.ADD: {
                    listener.notifyAdd(notification);
                }
                    break;
                case Notification.REMOVE: {
                    listener.notifyRemove(notification);
                }
                    break;
                case Notification.MOVE: {
                    listener.notifyMove(notification);
                }
                    break;
                case Notification.SET: {
                    listener.notifyRename(notification);
                }
                    break;
                case ModelWorkspaceNotification.CHANGE: {
                    listener.notifyChanged(notification);
                    if (reloadedResource) {
                        final ModelWorkspaceNotification reloadedNotification = new ModelWorkspaceNotificationImpl(
                                                                                                                   ModelWorkspaceNotification.RELOADED,
                                                                                                                   notification.getDelta(),
                                                                                                                   ((ModelWorkspaceNotificationImpl)notification).getChangeEvent());
                        listener.notifyReloaded(reloadedNotification);
                    }
                }
                    break;
                case ModelWorkspaceNotification.OPEN: {
                    listener.notifyOpen(notification);
                }
                    break;
                case ModelWorkspaceNotification.CLOSING: {
                    listener.notifyClosing(notification);
                }
                    break;

                default:
                    throw new ModelerCoreException(
                                                   ModelerCore.Util.getString("ModelWorkspaceManager.Illegal_notification,_notification_type_not_recognized___1") + eventType); //$NON-NLS-1$
            }
        }
    }

    /**
     * Deletes all index files associated with the specified <code>IResource</code>.
     * 
     * @param theResource the resource whose indexes are being deleted
     * @return <code>true</code> if all indexes have been successfully deleted; <code>false</code> otherwise.
     * @throws CoreException if a problem occurs
     * @since 5.0.1
     */
    public boolean deleteIndexes( IResource theResource ) throws CoreException {
        // delete search indexes for closing project
        return deleteIndexes(theResource, new SearchIndexResourceVisitor());
    }

    /**
     * Deletes all index files associated with the specified <code>IResource</code>.
     * 
     * @param theResource the resource whose indexes are being deleted
     * @param theVisitor the visitor to use
     * @return <code>true</code> if all indexes have been successfully deleted; <code>false</code> otherwise.
     * @throws CoreException if a problem occurs
     * @since 5.0.1
     */
    public boolean deleteIndexes( IResource theResource,
                                  SearchIndexResourceVisitor theVisitor ) throws CoreException {
        theResource.accept(theVisitor);
        File[] indexFiles = theVisitor.getIndexFiles();
        return IndexUtil.deleteIndexFiles(indexFiles, true);
    }

    /**
     * Change the worspace by adding or removing items from it.
     * 
     * @param notification Notification for adding and removing items to workspace
     * @throws CoreException
     */
    private boolean changeWorkspace( ModelWorkspaceNotification notification ) throws CoreException {

        // get the added/removed/moved/renamed resource
        final IResource resource = (IResource)notification.getNotifier();

        // do nothing for non-model projects and resources in them
        IProject iProject = resource.getProject();
        if (iProject.exists()) {
            // Projects don't have model natures when closed ...
            if (iProject.isOpen() && !ModelerCore.hasModelNature(iProject)) {
                return false;
            }
        }

        // do nothing for non-model
        if (resource.getType() == IResource.FILE) {
            if (!ModelUtil.isModelFile(resource, false) && !ModelUtil.isVdbArchiveFile(resource)) {
                return false;
            }
            // set the workspace as not indexed for any model file change
            this.setIndexType(ModelResource.NOT_INDEXED);
            // non-project, non-folder resources
        } else if (resource.getType() != IResource.PROJECT && resource.getType() != IResource.FOLDER) {
            return false;
        }

        // get the existing resource item for this
        ModelProjectImpl project = (ModelProjectImpl)this.modelWorkspace.findModelProject(resource);

        int eventType = ((ModelWorkspaceNotificationImpl)notification).getEventType();
        switch (eventType) {
            case Notification.ADD:
                add(resource, project);
                break;
            case Notification.REMOVE:
                try {
                    TransactionUtil.executeNonUndoable(new IOperation() {

                        public void execute() throws CoreException {
                            remove(resource);
                        }
                    }, this);
                } catch (CoreException err) {
                    throw err;
                } catch (Exception err) {
                    ModelerCore.Util.log(err);
                }
                break;
            // remove the old item and create a new one
            case Notification.MOVE:
                break;
            case Notification.SET:
                final IPath oldPath = notification.getDelta().getMovedFromPath();
                final ModelWorkspaceItem oldItem = this.modelWorkspace.getWorkspaceItem(oldPath, resource.getType());
                if (oldItem != null && oldItem.getResource() != null) {
                    remove(oldItem.getResource());
                    add(resource, project);
                }
                break;
            case ModelWorkspaceNotification.CHANGE:
                // May be null:
                if (resource.getType() == IResource.FILE) {
                    final ModelWorkspaceItem resourceItem = this.modelWorkspace.getWorkspaceItem(resource.getFullPath(),
                                                                                                 resource.getType());
                    if (resourceItem != null && resourceItem instanceof ModelResourceImpl) {
                        final ModelResourceImpl model = (ModelResourceImpl)resourceItem;
                        return model.processContentsChange(notification);
                    }
                }
                break;
            case ModelWorkspaceNotification.OPEN:
                // when projects are opeaned and they are not part of the workspace yet,
                // add them to the workspace as they are opeaned
                if (resource instanceof IProject) {
                    if (project == null) {
                        project = new ModelProjectImpl((IProject)resource, this.modelWorkspace);
                        final ModelWorkspaceItemInfo parentInfo = (ModelWorkspaceItemInfo)getInfo(this.modelWorkspace);
                        if (parentInfo != null) {
                            parentInfo.addChild(project);
                        }
                    }
                    project.open(null);
                }
                break;
            default:
                throw new ModelerCoreException(
                                               ModelerCore.Util.getString("ModelWorkspaceManager.Illegal_notification,_notification_type_not_recognized___1") + eventType); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * @param resource
     * @param project
     * @since 4.3
     */
    private void add( final IResource resource,
                      ModelProjectImpl project ) {
        // there should be no existing item for the resource being added
        final ModelWorkspaceItem resourceItem = this.modelWorkspace.getWorkspaceItem(resource.getFullPath(), resource.getType());
        if (resourceItem != null) {
            // if there is return, no need to add
            return;
        }

        // create new ModelWorkspaceItem for resource
        ModelWorkspaceItem newResource = null;
        ModelWorkspaceItem parentItem = null;
        if (resource instanceof IProject && project == null) {
            project = new ModelProjectImpl((IProject)resource, this.modelWorkspace);
            newResource = project;
            parentItem = this.modelWorkspace;
        } else if (!(resource instanceof IProject)) {
            // 11/4/03 LLP: We are periodically getting into this logic with a resource for which we
            // can not resolve a project. This is causing a NPE in the first line of the try below.
            // To resolve this, return in this case, but log a warning message so we can see how
            // often this situation is occurring.

            // 11/19/03 GC: This should not occur any more, this was occuring when
            // projects are getting opeaned and they are not getting added to the workspace.
            if (project == null) {
                final String msg = ModelerCore.Util.getString("ModelWorkspaceManager.Encountered_null_project_for_resource_of_type_{0}_1", resource.getClass().getName()); //$NON-NLS-1$  
                ModelerCore.Util.log(IStatus.WARNING, msg);
                return;
            }

            // create the item and update the cache
            try {
                if (ModelerCore.DEBUG_MODEL_WORKSPACE || ModelerCore.DEBUG_MODEL_WORKSPACE_EVENT) {
                    final Object[] params = new Object[] {resource};
                    final String debugMsg = ModelerCore.Util.getString("ModelWorkspaceManager.DEBUG.Creating_ModelResource_instance_for_0_2", params); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.INFO, debugMsg);
                }
                if (resource.getType() == IResource.FILE) {
                    newResource = project.createModelResource((IFile)resource);
                } else if (resource.getType() == IResource.FOLDER) {
                    newResource = project.createModelFolder((IFolder)resource);
                }
                parentItem = this.modelWorkspace.getParent(resource);
            } catch (Exception e) {
                ModelerCore.Util.log(IStatus.ERROR,
                                     e,
                                     ModelerCore.Util.getString("ModelWorkspaceManager.Error_creating_new_model_workspace_item___{0}_1", e.getMessage())); //$NON-NLS-1$
            }
        } else if (resource instanceof IProject && project != null) {
            parentItem = this.modelWorkspace;
        } else {
            ModelerCore.Util.log(IStatus.ERROR,
                                 ModelerCore.Util.getString("ModelWorkspaceManager.Unable_to_create_workspace_item_for_{0}_2", resource.getFullPath())); //$NON-NLS-1$
            return;
        }

        if (parentItem == null) {
            parentItem = this.modelWorkspace.getParent(resource);
            ModelerCore.Util.log(IStatus.ERROR,
                                 ModelerCore.Util.getString("ModelWorkspaceManager.Unable_to_find_parent_item_for_{0})_3", resource.getFullPath())); //$NON-NLS-1$
            return;
        }
        final ModelWorkspaceItemInfo parentInfo = (ModelWorkspaceItemInfo)getInfo(parentItem);
        if (parentInfo != null) {
            parentInfo.addChild(newResource);
        }
    }

    /**
     * @param resource
     * @throws CoreException
     * @since 4.3
     */
    void remove( final IResource resource ) throws CoreException {
        // May be null:
        final ModelWorkspaceItem resourceItem = this.modelWorkspace.getWorkspaceItem(resource.getFullPath(), resource.getType());
        // clean up indexes and resources for all model children
        removeResourcesRecursively(resource);
        if (resourceItem instanceof Openable) {
            ((Openable)resourceItem).close();
        }
        final ModelWorkspaceItem parentItem = this.modelWorkspace.getParent(resource);
        if (parentItem != null) {
            final ModelWorkspaceItemInfo parentInfo = (ModelWorkspaceItemInfo)getInfo(parentItem);
            // Info will be null if the parentItem is not open... so just return
            if (parentInfo != null) {
                parentInfo.removeChild(resourceItem);
            }
        }
        // parentItem will be null if the remove event for the parent is processed first
    }

    /**
     * Navigate the given IResource, find children that are models and clean up their index files and emf resources.
     * 
     * @param resource The IResource to navigate
     * @throws CoreException
     * @since 4.2
     */
    private void removeResourcesRecursively( final IResource resource ) throws CoreException {
        ArgCheck.isNotNull(resource);

        // type of resource
        int resourceType = resource.getType();

        // Per defect 10957, resourceItem may be null, so can't use it to clean up index files
        if (resource.getLocation() != null && resourceType == IResource.FILE) {
            // Remove the runtime index file associated with the resource being removed
            String runtimeIndexFileName = IndexUtil.getRuntimeIndexFileName(resource);
            File runtimeIndexFile = new File(IndexUtil.INDEX_PATH, runtimeIndexFileName);
            if (runtimeIndexFile.exists()) {
                getIndexManager().disposeIndex(runtimeIndexFileName);
            }

            // Remove the search index file associated with the resource being removed
            String searchIndexFileName = IndexUtil.getSearchIndexFileName(resource);
            File searchIndexFile = new File(IndexUtil.INDEX_PATH, searchIndexFileName);
            if (searchIndexFile.exists()) {
                getIndexManager().disposeIndex(searchIndexFileName);
            }

            // Remove the underlying Emf resource
            final ModelWorkspaceItem resourceItem = this.modelWorkspace.getWorkspaceItem(resource.getFullPath(), IResource.FILE);
            if (resourceItem != null && resourceItem instanceof ModelResourceImpl) {
                ((ModelResourceImpl)resourceItem).removeEmfResource();
            }

            // remove XMIHeader from cache
            this.FileToxmlHeaderMap.remove(resource.getRawLocation().toOSString());
        }

        if (resource.exists() && resource.isAccessible() && resourceType != IResource.FILE) {
            class ResourceVisitor implements IResourceVisitor {
                List resources = new ArrayList();

                public boolean visit( final IResource resource ) {
                    if (isIncludedResource(resource)) {
                        resources.add(resource);
                    }
                    return true;
                }

                public List getFileResources() {
                    return resources;
                }

                private boolean isIncludedResource( final IResource resource ) {
                    if (resource == null || !resource.exists()) {
                        return false;
                    }
                    if (ModelUtil.isModelFile(resource) || ModelUtil.isXsdFile(resource) || ModelUtil.isVdbArchiveFile(resource)) {
                        return true;
                    }
                    return false;
                }
            }

            final ResourceVisitor visitor = new ResourceVisitor();
            // collect all IResources for model files
            resource.accept(visitor);

            // for each model clean up index files and emf resoures
            Collection childResources = visitor.getFileResources();
            for (final Iterator rscIter = childResources.iterator(); rscIter.hasNext();) {
                IResource child = (IResource)rscIter.next();
                removeResourcesRecursively(child);
            }
        }
    }

    /**
     * Set the type indicating one or more ModelResources in the workspace have been indexed to the given type.
     */
    void setIndexType( int type ) {
        this.indexType = type;
    }

    /**
     * Find all {@link org.eclipse.core.resources.IResource}s whose {@linkcom.metamatrix.modeler.core.workspace.ModelResource}s
     * are not indexed to the given index type.
     * 
     * @param indexType The indexType of the ModelResource.
     * @return The collection of IResource objects
     * @throws CoreException
     */
    public Collection getNonIndexedResources( final int indexType ) throws CoreException {
        class ResourceVisitor implements IResourceVisitor {
            Collection resources = new ArrayList();

            public boolean visit( IResource resource ) throws CoreException {
                if (resource != null && resource.getType() == IResource.FILE) {
                    ModelWorkspaceItem modelWorkspaceItem = findModelWorkspaceItem(resource);
                    if (modelWorkspaceItem != null && modelWorkspaceItem instanceof ModelResource) {
                        ModelResource mResource = (ModelResource)modelWorkspaceItem;
                        int modelIndexType = mResource.getIndexType();
                        switch (indexType) {
                            case ModelResource.INDEXED:
                                if (modelIndexType != ModelResource.INDEXED) {
                                    resources.add(mResource.getResource());
                                }
                                break;
                            case ModelResource.METADATA_INDEXED:
                                if (modelIndexType != ModelResource.INDEXED && modelIndexType != ModelResource.METADATA_INDEXED) {
                                    resources.add(mResource.getResource());
                                }
                                break;
                            case ModelResource.SEARCH_INDEXED:
                                if (modelIndexType != ModelResource.INDEXED && modelIndexType != ModelResource.SEARCH_INDEXED) {
                                    resources.add(mResource.getResource());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                return true;
            }

            public Collection getResources() {
                return resources;
            }
        }

        // If there are no resources that require indexing then return immediately
        if (this.indexType == ModelResource.INDEXED || this.indexType == indexType) {
            return Collections.EMPTY_LIST;
        }
        // assuming when some one gets non-indexed resources they would build them
        setIndexType(indexType);

        ResourceVisitor visitor = new ResourceVisitor();
        // collect all IResources for model files
        ModelProject[] projects = ModelerCore.getModelWorkspace().getModelProjects();
        for (int i = 0; i < projects.length; i++) {
            ModelProject mProject = projects[i];
            if (mProject != null && mProject.isOpen()) {
                mProject.getProject().accept(visitor);
            }
        }

        return visitor.getResources();
    }

    /**
     * This method can be called to release all resources. Currently, this simply shuts down the {@link #getModelContainer() model
     * container}, which can be reinitialized after this method is called with the {@link #getModelContainer()} method.
     * 
     * @throws CoreException
     */
    private void shutdownManager() throws CoreException {
        if (container != null) {
            try {
                if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("ModelWorkspaceManager.DEBUG.Shutting_down_model_container")); //$NON-NLS-1$
                }
                container.shutdown();
                if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("ModelWorkspaceManager.DEBUG.Completed_shuting_down_model_container")); //$NON-NLS-1$
                }
            } catch (ModelerCoreException e) {
                throw new CoreException(
                                        new Status(
                                                   IStatus.ERROR,
                                                   ModelerCore.PLUGIN_ID,
                                                   1,
                                                   ModelerCore.Util.getString("ModelWorkspaceManager.Error_shutting_down_the_model_container_2"), e)); //$NON-NLS-1$
            } finally {
                container = null;
            }
        }

        if (workspaceListeners != null) {
            workspaceListeners.clear();
        }
        /*
         * Need to tell the markerManager to dispose so it's cache of eObject/Marker maps can be cleand up.
         */
        if (markerManager != null) {
            markerManager.dispose();
        }
    }

    /**
     * @return
     */
    public ModelWorkspace getModelWorkspace() {
        return this.modelWorkspace;
    }

    /**
     * Convenience method to get access the ModelMarkerManager
     * 
     * @return
     * @since 5.0
     */
    public ModelMarkerManager getMarkerManager() {
        return this.markerManager;
    }

    /**
     * Returns the info for the element.
     */
    public Object getInfo( ModelWorkspaceItem item ) {
        return this.cache.getInfo(item);
    }

    /**
     * Returns the info for this element without disturbing the cache ordering.
     */
    // TODO: should be synchronized, could answer uninitialized info or if cache is in middle of rehash, could even answer
    // distinct
    // element info
    protected Object peekAtInfo( ModelWorkspaceItem item ) {
        return this.cache.peekAtInfo(item);
    }

    /**
     * @param item
     */
    public void removeInfo( ModelWorkspaceItem item ) {
        this.cache.removeInfo(item);
    }

    /**
     * @param key
     * @param value
     */
    public void putInfo( ModelWorkspaceItem item,
                         Object info ) {
        this.cache.putInfo(item, info);
    }

    /**
     * @return
     */
    public Container getModelContainer() throws CoreException {
        if (container == null) {
            container = ModelerCore.getModelContainer();
        }
        return container;
    }

    /**
     * Returns the {@link ModelWorkspaceItem} corresponding to the given file, or <code>null</code> if unable to associate the
     * given file with a {@link ModelWorkspaceItem}.
     * <p>
     * The file must be one of:
     * <ul>
     * <li>a <code>.mmm</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>a <code>.xml</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * </ul>
     * <p>
     * Creating a {@link ModelWorkspaceItem} has the side effect of creating and opening all of the item's parents if they are not
     * yet open.
     * <p>
     */
    public static ModelResource create( IFile file,
                                        ModelProject project ) {
        if (file == null || !ModelerCore.hasModelNature(file.getProject())) {
            return null;
        }
        if (project == null) {
            project = manager.getModelWorkspace().getModelProject(file);
        }
        if (file.getFileExtension() != null && ModelUtil.isModelFile(file)) {
            try {
                ModelResource resource = (ModelResource)project.findModelWorkspaceItem(file);
                if (resource != null) {
                    return resource;
                }
                // Else it was not found, so it must be created ...
                resource = (ModelResource)ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(file, true);
                return resource;
            } catch (ModelWorkspaceException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the {@link ModelWorkspaceItem} corresponding to the given folder, or <code>null</code> if unable to associate the
     * given file with a {@link ModelWorkspaceItem}. Creating a {@link ModelWorkspaceItem} has the side effect of creating and
     * opening all of the item's parents if they are not yet open.
     * <p>
     */
    public static ModelWorkspaceItem create( IFolder folder,
                                             ModelProject project ) {
        if (project == null) {
            project = manager.getModelWorkspace().getModelProject(folder);
        }
        try {
            ModelWorkspaceItem folderItem = project.findModelWorkspaceItem(folder);
            if (folderItem != null) {
                return folderItem;
            }
            // Else it was not found, so it must be created ...
            folderItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(folder, true);
            return folderItem;
        } catch (ModelWorkspaceException e) {
            return null;
        }
    }

    /**
     * Returns the {@link ModelWorkspaceItem} corresponding to the given resource, or <code>null</code> if unable to associate the
     * given resource with a {@link ModelWorkspaceItem workspace item}.
     * <p>
     * The resource must be one of:
     * <ul>
     * <li>a project - the element returned is the corresponding {@link ModelProject}</li>
     * <li>a <code>.mmm</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>a <code>.xml</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>the workspace root resource - the element returned is the {@link ModelWorkspace}</li>
     * </ul>
     * <p>
     * Creating a {@link ModelWorkspaceItem} has the side effect of creating and opening all of the item's parents if they are not
     * yet open.
     * 
     * @param resource the given resource
     * @param project the model project that the resource exists in, if known
     * @return the {@link ModelWorkspaceItem} corresponding to the given resource, or <code>null</code> if unable to associate the
     *         given resource with a {@link ModelWorkspaceItem workspace item}
     */
    public static ModelWorkspaceItem create( final IResource resource,
                                             final ModelProject project ) {
        if (resource == null || (resource.getProject() != null && !ModelerCore.hasModelNature(resource.getProject()))) {
            return null;
        }
        int type = resource.getType();
        switch (type) {
            case IResource.PROJECT:
                return ModelerCore.create((IProject)resource);
            case IResource.FILE:
                return create((IFile)resource, project);
            case IResource.FOLDER:
                return create((IFolder)resource, project);
            case IResource.ROOT:
                return ModelerCore.create((IWorkspaceRoot)resource);
            default:
                return null;
        }
    }

    /**
     * Returns the {@link ModelWorkspaceItem} represented by the <code>String</code> memento.
     */
    public ModelWorkspaceItem getHandleFromMemento( final String memento ) {
        if (memento == null) {
            return null;
        }
        ModelWorkspace modelWorkspace = getModelWorkspace();
        if (memento.equals("")) { // workspace memento //$NON-NLS-1$
            return modelWorkspace;
        }
        return null;
    }

    public void clearCache() {
        this.cache.clear();
    }

    /**
     * Return the {@link ModelResource} that contains the opened {@link Resource EMF resource}.
     * 
     * @param resource
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    public ModelResource findModelResource( final Resource resource ) {
        ArgCheck.isNotNull(resource);
        return ModelBufferManager.getDefaultBufferManager().getModelResource(resource);
    }

    /**
     * Return the existing {@link ModelWorkspaceItem} that represents the supplied resource.
     * 
     * @param resource the resource
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    public ModelWorkspaceItem findModelWorkspaceItem( final IResource resource ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(resource);
        return findModelWorkspaceItem(resource, false);
    }

    /**
     * Return the existing {@link ModelWorkspaceItem} that is at the supplied resource path.
     * 
     * @param resourcePath the resource path at the given loacation
     * @param resourceType The type of the resource
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    public ModelWorkspaceItem findModelWorkspaceItem( final IPath resourcePath,
                                                      int resourceType ) {
        ArgCheck.isNotNull(resourcePath);
        return modelWorkspace.getWorkspaceItem(resourcePath, resourceType);
    }

    /**
     * Return the existing {@link ModelWorkspaceItem} that represents the supplied resource.
     * 
     * @param resource the resource
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    public ModelWorkspaceItem findModelWorkspaceItem( final IResource resource,
                                                      final boolean createIfRequired ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(resource);

        // Get the project and the path of the model file relative to the project ...
        final IProject proj = resource.getProject();

        // check if this is a model project
        if (!ModelerCore.hasModelNature(proj)) {
            return null;
        }

        final IPath pathInProject = resource.getProjectRelativePath();

        // Find the ModelProject
        final ModelProject modelProject = this.getModelWorkspace().getModelProject(proj);

        // Iterate over the segments, finding the corresponding model folder(s) and model resource
        ModelWorkspaceItem parent = modelProject;
        int numFolders = pathInProject.segmentCount(); // should be at least 1
        if (resource instanceof IFile) {
            // See if the file is a model ...
            if (!ModelUtil.isModelFile(resource)) {
                return null; // it's a non-model resource
            }
            --numFolders;
        }
        for (int i = 0; i < numFolders; ++i) {
            final String folderName = pathInProject.segment(i);
            final ModelWorkspaceItem child = parent.getChild(folderName);

            if (child == null) {

                // get the workspace resource
                // i+1 is safe because i is always < numFolders, and uptoSegment takes a count,
                // not a segment index or offset or anything.
                final IFolder underlyingFolder = proj.getFolder(pathInProject.uptoSegment(i + 1));
                Assertion.isNotNull(underlyingFolder);
                final ModelFolder newFolder = new ModelFolderImpl(underlyingFolder, parent);
                final Object parentInfo = ((ModelWorkspaceItemImpl)parent).getItemInfo();
                if (parentInfo instanceof ModelFolderInfo) {
                    ((ModelFolderInfo)parentInfo).addChild(newFolder);
                    parent = newFolder;
                } else if (parentInfo instanceof ModelProjectInfo) {
                    ((ModelProjectInfo)parentInfo).addChild(newFolder);
                    parent = newFolder;
                }
            } else {
                parent = child;
            }
        }

        if (resource instanceof IFile) {
            // Get the ModelResource ...
            ModelWorkspaceItem result = parent.getChild(resource);
            if (result == null && createIfRequired) {
                final String name = resource.getName();
                result = new ModelResourceImpl(parent, name);
            }
            return result;
        }
        // else return the folder
        return parent;
    }

    private void addChangeNotifier( final Container container ) {
        if (container != null) {
            ChangeNotifier notifier = container.getChangeNotifier();
            if (notifier != null) {
                notifier.addListener(new NotificationProcessor());
            }
        }
    }

    /**
     * Indicates if the resource is a model and is open.
     * 
     * @param theResource the resource being checked
     * @return <code>true</code>if a model and is open; <code>false</code> otherwise.
     * @throws IllegalArgumentException if resource is <code>null</code>
     * @since 4.2
     */
    public boolean isModelOpen( final IResource theResource ) {
        ArgCheck.isNotNull(theResource);
        boolean result = false;

        if (ModelUtil.isModelFile(theResource, true)) {
            try {
                ModelWorkspaceItem item = findModelWorkspaceItem(theResource, false);

                if (item != null) {
                    result = (getInfo(item) != null);
                }
            } catch (ModelWorkspaceException theException) {
            }
        }

        return result;
    }

    public XMIHeader getCachedXmiHeader( final File resource ) {
        XMIHeaderCachedObject headerCachedObject = (XMIHeaderCachedObject)FileToxmlHeaderMap.get(resource.getAbsolutePath());
        if (headerCachedObject != null) {
            if (!headerCachedObject.isModified(resource)) {
                return headerCachedObject.getXMIHeader();
            }
            FileToxmlHeaderMap.remove(resource.getAbsolutePath());
        }

        return null;
    }

    public void setXmiHeaderToCache( final File resource,
                                     XMIHeader header ) {
        XMIHeaderCachedObject headerCachedObject = new XMIHeaderCachedObject(header, resource.lastModified());
        FileToxmlHeaderMap.put(resource.getAbsolutePath(), headerCachedObject);
    }

    public ModelWorkspaceSearch getModelWorkspaceSearch() {
        return this.modelWorkspaceSearch;
    }

    public ModelWorkspaceIndexManager getIndexManager() {
        return this.indexManager;
    }

    /**
     * Visitor that collects IResources for which ModelResources need to be created, these include IProjects, IFolders and
     * IFiles(model, XSD and VDB files.)
     */
    class ModelIResourceCollectorVisitor implements IResourceVisitor {
        List resources = new ArrayList();

        public boolean visit( final IResource resource ) {
            if (isIncludedResource(resource)) {
                resources.add(resource);
                if (resource.getType() != IResource.FILE) {
                    return true;
                }
            }
            return false;
        }

        public List getResources() {
            return resources;
        }

        private boolean isIncludedResource( final IResource resource ) {
            if (resource == null || !resource.exists()) {
                return false;
            }
            if (resource.getType() == IResource.PROJECT) {
                return ModelerCore.hasModelNature((IProject)resource);
            } else if ((resource.getType() == IResource.FILE)) {
                if (ModelUtil.isModelFile(resource) || ModelUtil.isXsdFile(resource) || ModelUtil.isVdbArchiveFile(resource)) {
                    return true;
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Compare IResources in a collection and order them as IRoot, IProject, IFolder and IFile.
     */
    private class IResourceComparator implements Comparator {

        public IResourceComparator() {
            super();
        }

        public int compare( Object rsc1,
                            Object rsc2 ) {
            ArgCheck.isInstanceOf(IResource.class, rsc1);
            ArgCheck.isInstanceOf(IResource.class, rsc2);

            IResource resource1 = (IResource)rsc1;
            IResource resource2 = (IResource)rsc2;

            return resource2.getType() - resource1.getType();
        }

        @Override
        public boolean equals( Object anObject ) {
            if (this == anObject) {
                return true;
            }
            if (anObject == this) {
                return true;
            }
            if (anObject == null || anObject.getClass() != this.getClass()) {
                return false;
            }
            return true;
        }
    }

    class NotificationProcessor implements INotifyChangedListener {

        public void notifyChanged( Notification notification ) {
            // If the notification is just a touch, don't do anything.
            if (notification.isTouch()) {
                return;
            }
            if (notification instanceof SourcedNotification) {
                final Collection chain = ((SourcedNotification)notification).getNotifications();
                for (Iterator iter = chain.iterator(); iter.hasNext();) {
                    Notification n = (Notification)iter.next();
                    checkResourceForIndexing(n);
                }
            } else {
                checkResourceForIndexing(notification);
            }
        }

        private void checkResourceForIndexing( final Notification notification ) {
            final Object target = notification.getNotifier();
            if (notification.isTouch()) {
                return;
            }
            switch (notification.getEventType()) {
                case Notification.ADD:
                case Notification.ADD_MANY:
                case Notification.REMOVE:
                case Notification.REMOVE_MANY:
                case Notification.SET:
                case Notification.UNSET:
                case Notification.MOVE:
                    refreshResourceIndexType(target);
                    break;
                default:
                    // do nothing
            }
        }

        private void refreshResourceIndexType( final Object obj ) {
            if (obj == null) {
                return;
            }
            if (obj instanceof Resource && ((Resource)obj).isModified()) {
                ModelResource mResource = ModelWorkspaceManager.this.findModelResource((Resource)obj);
                if (mResource != null) {
                    if (mResource.getIndexType() == ModelResource.NOT_INDEXED) {
                        return;
                    }
                    mResource.refreshIndexType();
                    ModelWorkspaceManager.this.setIndexType(ModelResource.NOT_INDEXED);
                }
            } else if (obj instanceof EObject && ((EObject)obj).eResource() != null) {
                Resource eResource = ((EObject)obj).eResource();
                if (eResource.isModified()) {
                    ModelResource mResource = ModelWorkspaceManager.this.findModelResource(eResource);
                    if (mResource != null) {
                        if (mResource.getIndexType() == ModelResource.NOT_INDEXED) {
                            return;
                        }
                        mResource.setIndexType(ModelResource.NOT_INDEXED);
                        ModelWorkspaceManager.this.setIndexType(ModelResource.NOT_INDEXED);
                    }
                }
            }
        }
    }

    private class XMIHeaderCachedObject {
        private XMIHeader header;
        private long lastModified;

        XMIHeaderCachedObject( XMIHeader header,
                               long lastModified ) {
            XMIHeaderCachedObject.this.header = header;
            XMIHeaderCachedObject.this.lastModified = lastModified;
        }

        XMIHeader getXMIHeader() {
            return header;
        }

        boolean isModified( File file ) {
            return file.lastModified() != XMIHeaderCachedObject.this.lastModified;
        }
    }
}

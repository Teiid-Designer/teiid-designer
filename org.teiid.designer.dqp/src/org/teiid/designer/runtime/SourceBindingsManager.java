/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.IRefactorResourceListener;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * The <code>SourceBindingsManager</code> manages the {@link SourceBinding}s registered on its associated execution server.
 */
public class SourceBindingsManager implements IResourceChangeListener, IRefactorResourceListener {

    private static final int VDB_VERSION = 1;

    private final ExecutionAdmin admin;
    private final Map<String, SourceBinding> bindingsByModelNameMap;
//    private VdbEditingContext hiddenVdb;
    private final WorkspaceNotificationListener workspaceListener;

    /**
     * @param admin the execution admin associated with this manager (never <code>null</code>)
     */
    public SourceBindingsManager( ExecutionAdmin admin ) {
        ArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        this.admin = admin;
        this.bindingsByModelNameMap = new HashMap<String, SourceBinding>();

        // hookup listening
        this.workspaceListener = new WorkspaceNotificationListener();
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(this.workspaceListener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        ((ModelerCore)ModelerCore.getPlugin()).addRefactorResourceListener(this);
    }

    /**
     * Creates a new <code>SourceBinding</code> object, adds the input connector binding to the object and sets the UUID and Path
     * properties to the values from the <code>ModelResource</code>.
     * 
     * @param modelResource the model whose source binding is being created (never <code>null</code>)
     * @param connector the connector being added to the binding (never <code>null</code>)
     * @since 5.0
     */
    public void createSourceBinding( ModelResource modelResource,
                                     Connector connector ) {
        ArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        ArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        createSourceBinding(modelResource, Collections.singleton(connector));
    }

    private void createSourceBinding( ModelResource modelResource,
                                      Set<Connector> connectors ) {
        String modelName = modelResource.getItemName();

        // collect connector names
        String[] connectorNames = new String[connectors.size()];
        int i = 0;

        for (Connector connector : connectors) {
            connectorNames[i] = connector.getName();
            ++i;
        }

        try {
//            ensureModelExistsInHiddenVdb();
            this.admin.getAdminApi().assignBindingsToModel(connectorNames,
                                                           getHiddenVdbName(),
                                                           Integer.toString(VDB_VERSION),
                                                           modelName);
            SourceBinding newBinding = new SourceBinding(modelName,
                                                         modelResource.getParent().getPath().makeRelative().toString(),
                                                         connectors);
            this.bindingsByModelNameMap.put(modelName, newBinding);

            // notify listeners
            fireConfigurationEvent(ExecutionConfigurationEvent.createAddSourceBindingEvent(newBinding));
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e.getMessage());
        }
    }
//
//    /**
//     * Make sure the hidden workspace preview VDB exists on server
//     * 
//     * @throws Exception if there is a problem retrieving or creating the VDB
//     */
//    private void ensureHiddenVdbExists() {
//        if (this.hiddenVdb == null) {
//            try {
//                // TODO create VDB at the right location
//                IPath path = DqpPlugin.getInstance().getStateLocation().append(getHiddenVdbName());
//                this.hiddenVdb = VdbEditPlugin.createVdbEditingContext(path);
//                this.hiddenVdb.open(); // create file
//            } catch (Exception e) {
//                Util.log(e);
//                this.hiddenVdb = null;
//                throw new Exception(); // TODO need error msg
//            }
//
//            try {
//                this.admin.getAdminApi().deployVDB(getHiddenVdbName(), path.toFile().toURI().toURL());
//            } catch (Exception e) {
//                Util.log(e);
//                throw new Exception(); // TODO need error msg
//            }
//        }
//        VDB vdb = this.admin.getAdminApi().getVDB(getHiddenVdbName(), VDB_VERSION);
//
//    }
//
//    private void ensureModelExistsInHiddenVdb( ModelResource modelResource ) throws Exception {
//        ensureHiddenVdbExists();
//        this.hiddenVdb.addModel(null, modelResource.getPath(), false); // TODO not sure what this does if model already added
//    }

    void fireConfigurationEvent( ExecutionConfigurationEvent event ) {
        assert (event != null);
        this.admin.getEventManager().notifyListeners(event);
    }

    /**
     * Obtains the connectors bound to the specified model.
     * 
     * @param modelName the name of the model whose connectors are being requested (never <code>null</code> or empty)
     * @return the connectors (never <code>null</code> but can be empty)
     * @since 5.0
     */
    public Collection<Connector> getConnectorsForModel( String modelName ) {
        ArgCheck.isNotEmpty(modelName, "modelName"); //$NON-NLS-1$
        SourceBinding binding = this.bindingsByModelNameMap.get(modelName);
        if (binding == null) return Collections.emptyList();
        return binding.getConnectors();
    }

    private String getHiddenVdbName() {
        // TODO figure out good name
        return "W_O_R_K_S_P_A_C_E__P_R_E_V_I_E_W__V_D_B"; //$NON-NLS-1$
    }

    /**
     * Helper method which finds and returns the <code>JdbcSource</code> object inside a <code>ModelResource</code>
     * 
     * @param modelResource
     * @return
     * @since 5.0
     */
    public JdbcSource getJdbcSource( ModelResource modelResource ) {
        Collection allEObjects = null;

        try {
            allEObjects = modelResource.getEObjects();
        } catch (ModelWorkspaceException theException) {
            Util.log(IStatus.ERROR, theException.getMessage());
        }
        if (allEObjects != null && !allEObjects.isEmpty()) {
            for (Iterator iter = allEObjects.iterator(); iter.hasNext();) {
                Object nextObj = iter.next();
                if (nextObj instanceof JdbcSource) {
                    return (JdbcSource)nextObj;
                }
            }
        }

        return null;
    }

    /**
     * @param modelName the name of the model whose binding is being requested (never <code>null</code> or empty)
     * @return the binding or <code>null</code>
     * @since 5.0
     */
    public SourceBinding getSourceBinding( String modelName ) {
        ArgCheck.isNotEmpty(modelName, "modelName"); //$NON-NLS-1$
        return this.bindingsByModelNameMap.get(modelName);
    }

    /**
     * @param connector the connector whose source bindings are being requested (never <code>null</code>)
     * @return the source bindings (never <code>null</code>)
     * @since 7.0
     */
    public Collection<SourceBinding> getSourceBindings( Connector connector ) {
        ArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        Collection<SourceBinding> result = new ArrayList<SourceBinding>();

        for (SourceBinding binding : this.bindingsByModelNameMap.values()) {
            if (binding.getConnectors().contains(connector)) result.add(binding);
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Helper method to determine if a model is mapped to a source. Basically it checks if the WorkspaceBindings.def file contains
     * a source binding to the input <code>ModelResource</code>
     * 
     * @param modelResource
     * @return
     * @since 5.0
     */
    public boolean hasSourceBinding( ModelResource modelResource ) {
        ArgCheck.isNotNull(modelResource, "modelResource.getItemName()"); //$NON-NLS-1$
        return (getSourceBinding(modelResource.getItemName()) != null);
    }

    /**
     * @see com.metamatrix.modeler.core.refactor.IRefactorResourceListener#notifyRefactored(com.metamatrix.modeler.core.refactor.RefactorResourceEvent)
     * @since 5.0
     */
    public void notifyRefactored( RefactorResourceEvent theEvent ) {
        switch (theEvent.getType()) {
            case RefactorResourceEvent.TYPE_MOVE: {
                if (theEvent.getResource() instanceof IFile) {
                    resetSourceBinding(theEvent.getResource().getName(), (IFile)theEvent.getResource());
                } else if (theEvent.getResource() instanceof IFolder) {
                    // Folder we refactored plus it's contents
                    resetSourceBindings((IFolder)theEvent.getResource());
                }
            }
                break;

            case RefactorResourceEvent.TYPE_RENAME: {
                if (theEvent.getResource() instanceof IFile) {
                    resetSourceBinding(theEvent.getOriginalPath().lastSegment(), (IFile)theEvent.getResource());
                } else if (theEvent.getResource() instanceof IFolder) {
                    // Folder we refactored plus it's contents
                    resetSourceBindings((IFolder)theEvent.getResource());
                }
            }
                break;

            case RefactorResourceEvent.TYPE_DELETE: {
                removeSourceBinding(theEvent.getOriginalPath().lastSegment());
            }
                break;
        }
    }

    /**
     * Refreshes the {@link SourceBinding}s in the hidden workspace VDB found on the server.
     * 
     * @throws Exception if there is a problem getting the hidden VDB
     */
    void refresh() throws Exception {
        this.bindingsByModelNameMap.clear();
        VDB vdb = this.admin.getAdminApi().getVDB(getHiddenVdbName(), VDB_VERSION);

        if (vdb != null) {
            for (Model model : vdb.getModels()) {
                List<String> connectorNames = model.getConnectorBindingNames();

                if (!connectorNames.isEmpty()) {
                    Set<Connector> connectors = new HashSet<Connector>(connectorNames.size());

                    for (String connectorName : connectorNames) {
                        Connector connector = this.admin.getConnector(connectorName);

                        if (connector == null) {
                            // TODO should this ever happen? if it does should we remove the binding now?
                            Util.log(IStatus.ERROR, Util.getString("hiddenVdbConnectorNotFound", connectorName, model.getName())); //$NON-NLS-1$
                        } else {
                            connectors.add(connector);
                        }
                    }

                    if (!connectors.isEmpty()) {
                        SourceBinding binding = new SourceBinding(model.getName(), model.getPath(), connectors);
                        this.bindingsByModelNameMap.put(model.getName(), binding);
                    }
                }
            }
        }

        // notify listeners
        fireConfigurationEvent(ExecutionConfigurationEvent.createRefreshSourceBindingsEvent());
    }

    /**
     * Removes the source binding for the input <code>ModelResource</code>.
     * 
     * @param modelResource
     * @since 5.0
     */
    public void removeSourceBinding( ModelResource modelResource ) {
        ArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        String modelName = modelResource.getItemName();
        removeSourceBinding(modelName);
    }

    /**
     * Removes the source binding for the specified model.
     * 
     * @param modelName the name of the model whose source binding is being removed (never <code>null</code> or empty)
     * @since 5.0
     */
    public void removeSourceBinding( String modelName ) {
        ArgCheck.isNotEmpty(modelName, "modelName"); //$NON-NLS-1$

        if (this.bindingsByModelNameMap.containsKey(modelName)) {
            try {
                if (this.bindingsByModelNameMap.size() == 1) {
                    assert (this.bindingsByModelNameMap.containsKey(modelName));
                    this.admin.getAdminApi().deleteVDB(getHiddenVdbName(), VDB_VERSION);
                } else {
                    // other models still have bindings just clear binding for this model on the server
                    this.admin.getAdminApi().assignBindingToModel("", getHiddenVdbName(), Integer.toString(VDB_VERSION), modelName); //$NON-NLS-1$
                }

                // clear local cache
                SourceBinding removedBinding = this.bindingsByModelNameMap.remove(modelName);

                // notify listeners
                fireConfigurationEvent(ExecutionConfigurationEvent.createRemoveSourceBindingEvent(removedBinding));
            } catch (AdminException e) {
                Util.log(IStatus.ERROR, e, Util.getString("problemRemovingSourceBinding", modelName)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Removes the source binding.
     * 
     * @param binding the binding being removed (never <code>null</code>)
     * @throws IllegalArgumentException if a source binding was not removed
     * @since 5.0
     */
    public void removeSourceBinding( SourceBinding binding ) {
        removeSourceBinding(binding.getName());
    }

    /**
     * Removes the source bindings associated with models under a project.
     * 
     * @param projectName the name of the project whose contained source bindings are being removed (never <code>null</code> or
     *        empty)
     * @since 5.0
     */
    private void removeSourceBindingsForProject( String projectName ) {
        ArgCheck.isNotEmpty(projectName, "projectName"); //$NON-NLS-1$
        Collection<SourceBinding> allBindings = new ArrayList<SourceBinding>(this.bindingsByModelNameMap.values());

        for (SourceBinding binding : allBindings) {
            if (binding.getContainerPath().startsWith(projectName)) {
                removeSourceBinding(binding.getName()); // TODO this fires event after each remove, may just want one event at end
            }
        }
    }

    /**
     * @param modelResource the model whose binding is being reset (never <code>null</code>)
     */
    private void resetSourceBinding( ModelResource modelResource ) {
        ArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$

        SourceBinding binding = getSourceBinding(modelResource.getItemName());

        // only reset if one currently exists
        if (binding != null) {
            removeSourceBinding(binding);
            createSourceBinding(modelResource, binding.getConnectors());
        }
    }

    private void resetSourceBinding( String modelNameWithExtension,
                                     IFile modelFile ) {
        ModelResource mr = null;

        try {
            mr = ModelUtil.getModelResource(modelFile, false);

            if (mr != null) {
                resetSourceBinding(mr);
            }
        } catch (ModelWorkspaceException theException) {
            Util.log(IStatus.ERROR, theException.getMessage());
        }
    }

    /*
     * This method 
     */
    private void resetSourceBindings( IFolder refactoredFolder ) {
        try {
            IResource[] children = refactoredFolder.members();

            for (int i = 0; i < children.length; i++) {
                if (ModelUtil.isModelFile(children[i])) {
                    resetSourceBinding(children[i].getFullPath().lastSegment(), (IFile)children[i]);
                } else if (children[i] instanceof IFolder) {
                    resetSourceBindings((IFolder)children[i]);
                }
            }
        } catch (CoreException theException) {
            Util.log(IStatus.ERROR, theException.getMessage());
        }
    }

    void shutdown() {
        if (this.workspaceListener != null) {
            ModelWorkspaceManager.getModelWorkspaceManager().removeNotificationListener(this.workspaceListener);
        }

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        ((ModelerCore)ModelerCore.getPlugin()).removeRefactorResourceListener(this);
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.2
     */
    public void resourceChanged( IResourceChangeEvent theEvent ) {
        IResource resource = theEvent.getResource();

        if (resource != null) {
            if (ResourceChangeUtilities.isPreDelete(theEvent)) {
                if (resource instanceof IProject) { // if Project, then delete all contained model's source bindings
                    removeSourceBindingsForProject(resource.getName());
                } else { // if Model, then only do model's source binding
                    String nm = resource.getName();

                    if (nm != null) {
                        removeSourceBinding(nm);
                    }
                }
            } else if (ResourceChangeUtilities.isPostChange(theEvent)) {
                if (theEvent.getDelta().getResource() instanceof IWorkspaceRoot) {
                    fireConfigurationEvent(ExecutionConfigurationEvent.createRefreshSourceBindingsEvent());
                }
            }
        }
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    private class WorkspaceNotificationListener implements ModelWorkspaceNotificationListener {

        public WorkspaceNotificationListener() {
        }

        public void notifyAdd( ModelWorkspaceNotification notification ) {
        }

        public void notifyRemove( ModelWorkspaceNotification notification ) {
        }

        public void notifyMove( ModelWorkspaceNotification notification ) {
        }

        public void notifyRename( ModelWorkspaceNotification notification ) {
        }

        public void notifyOpen( ModelWorkspaceNotification notification ) {
            // project opened
            fireConfigurationEvent(ExecutionConfigurationEvent.createRefreshSourceBindingsEvent());
        }

        public void notifyClosing( ModelWorkspaceNotification notification ) {
        }

        public void notifyChanged( Notification theNotification ) {
        }

        public void notifyReloaded( ModelWorkspaceNotification notification ) {
        }

        public void notifyClean( final IProject proj ) {
        }
    }

}

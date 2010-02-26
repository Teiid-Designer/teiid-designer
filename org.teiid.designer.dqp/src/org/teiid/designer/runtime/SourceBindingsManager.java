/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.core.refactor.IRefactorResourceListener;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.BasicWorkspaceDefn;
import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceDefnReaderWriter;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * This class is designed to manage a SourceBindings.xml file containing all workspace model-to-connector bindings. This file
 * resides in the runtime workspace's .metadata/.plugins/com.metamatrix.modeler.dqp/workspaceConfig directory. If the workspace is
 * deleted (i.e. .metadata directory), the bindings will have to be re-created. Connectors and Connector Types are managed by the
 * <code>ExecutionManager</code>s.
 * 
 * @since 5.0
 */
public class SourceBindingsManager
    implements IChangeNotifier, IExecutionConfigurationListener, IResourceChangeListener, IRefactorResourceListener {

    private Collection listenerList;

    private final File defnFile;

    private Properties headerProps;

    private final ServerManager serverManager;
    
    private BasicWorkspaceDefn workspaceDefn;

    private WorkspaceDefnReaderWriter defReaderWriter = new WorkspaceDefnReaderWriter();

    /**
     * Constructor initialized with a File representing the WorkspaceBindings.def
     * 
     * @param defnFile
     * @since 5.0
     */
    public SourceBindingsManager( ServerManager serverManager,
                                  File defnFile ) {
        this.defnFile = defnFile;
        this.headerProps = new Properties();
        this.listenerList = new ArrayList(1);
        this.serverManager = serverManager;

        WorkspaceNotificationListener listener = new WorkspaceNotificationListener();
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(listener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        if (!listenerList.contains(theListener)) {
            this.listenerList.add(theListener);
        }
    }

    /**
     * Creates a new <code>SourceModelInfo</code> object, adds the input connector binding to the object and sets the UUID and
     * Path properties to the values from the <code>ModelResource</code>.
     * 
     * @param modelResource
     * @param binding
     * @since 5.0
     */
    public void createSourceBinding( ModelResource modelResource,
                                     Connector connector ) {
        SourceModelInfo modelInfo = new SourceModelInfo(modelResource.getItemName());
        modelInfo.addConnector(connector);

        try {
            modelInfo.setUuid(modelResource.getUuid());
            modelInfo.setContainerPath(modelResource.getParent().getPath().makeRelative().toString());
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }

        getWorkspaceDefn().addModelInfo(modelInfo);

        fireChangeEvent();

        save();
    }

    /**
     * Returns a collection of any ConnectorsBindngs bound to the input <code>ModelResource</code>
     * 
     * @param String
     * @return
     * @since 5.0
     */
    public Collection<Connector> getConnectorsForModel( String modelName ) {
        Collection<Connector> connectors = Collections.emptyList();
        ModelInfo modelInfo = getWorkspaceDefn().getModel(modelName);

        if (modelInfo != null && modelInfo.getConnectorBindingNames() != null) {
            Object[] names = modelInfo.getConnectorBindingNames().toArray();
            connectors = new ArrayList<Connector>(names.length);
            String name = null;
            Connector connector = null;

            for (int i = 0; i < names.length; i++) {
                name = (String)names[i];
                connector = this.executionAdmin.getConnector(name);

                if (connector != null) {
                    connectors.add(connector);
                }
            }
        }
        return connectors;
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
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
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

    public Collection<SourceModelInfo> getModelsForConnector( Connector connector ) {
        return getWorkspaceDefn().getModelsForConnector(connector);
    }

    /**
     * @param modelName
     * @return
     * @since 5.0
     */
    public SourceModelInfo getSourceModelInfo( String modelName ) {
        return (SourceModelInfo)getWorkspaceDefn().getModel(modelName);
    }

    /**
     * Returns the BasicWorkspacDefn instance which acts as the model for the WorkspaceBindings.def file.
     * 
     * @return
     * @since 5.0
     */
    public BasicWorkspaceDefn getWorkspaceDefn() {
        if (this.workspaceDefn == null) {
            workspaceDefn = new BasicWorkspaceDefn();
        }
        return this.workspaceDefn;
    }

    /**
     * Loads the WorkspaceBindings.def file
     * 
     * @throws IOException
     * @throws Exception
     * @since 5.0
     */
    public void load() throws IOException, Exception {
        if (this.defnFile.exists() && this.defnFile.length() > 0) {
            workspaceDefn = defReaderWriter.read(new FileInputStream(this.defnFile));
        }
    }

    /**
     * Helper method to determine if a model is mapped to a source. Basically it checks if the WorkspaceBindings.def file contains
     * a source binding to the input <code>ModelResource</code>
     * 
     * @param modelResource
     * @return
     * @since 5.0
     */
    public boolean modelIsMappedToSource( ModelResource modelResource ) {
        ModelInfo modelInfo = getWorkspaceDefn().getModel(modelResource.getItemName());
        return modelInfo != null;
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.listenerList.remove(theListener);
    }

    /**
     * Removes the source binding for the input <code>ModelResource</code>.
     * 
     * @param modelResource
     * @since 5.0
     */
    public void removeSourceBinding( ModelResource modelResource ) {
        String modelName = modelResource.getItemName();
        removeSourceBinding(modelName);
    }

    /**
     * Removes the source binding for the input model name.
     * 
     * @param modelName
     * @since 5.0
     */
    public void removeSourceBinding( String modelName ) {
        getWorkspaceDefn().removeModelInfo(modelName);
        fireChangeEvent();
        save();
    }

    /**
     * Removes the source binding for the input <code>ModelInfo</code>
     * 
     * @param modelInfo
     * @since 5.0
     */
    public void removeSourceBinding( ModelInfo modelInfo ) {
        removeSourceBinding(modelInfo.getName());
    }

    /**
     * Helper method to remove source bindings for a collection of objects which may be of type: <code>ModelInfo</code>
     * <code>String</code> <code>ModelResource</code>
     * 
     * @param objects
     * @since 5.0
     */
    public void removeSourceBindings( Collection objects ) {
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            Object nextObj = iter.next();
            if (nextObj instanceof ModelInfo) {
                removeSourceBinding((ModelInfo)nextObj);
            } else if (nextObj instanceof String) {
                removeSourceBinding((String)nextObj);
            } else if (nextObj instanceof ModelResource) {
                removeSourceBinding((ModelResource)nextObj);
            }
        }
    }

    /**
     * Removes the source bindings associated with models under a project.
     * 
     * @param modelName
     * @since 5.0
     */
    public void removeSourceBindingForProject( String projectName ) {
        getWorkspaceDefn().removeModelInfosForProject(projectName);
        fireChangeEvent();
        save();
    }

    /*
     * Private method which will reset the SourceModelInfo Name, UUID & Container Path values
     * If NAME is CHANGED, the SourceModelInfo is removed and re-added to the the configuration to sync up it's MAP and an event
     * is fired to allow DQP to react.
     * 
     * If the NAME is NOT CHANGED, then we just replace the UUID and Container Path.
     * 
     * In all cases, the configuration is saved.
     */
    private void resetSourceInfo( ModelResource modelResource,
                                  SourceModelInfo modelInfo ) {
        boolean replaceInfo = false;

        if (!modelResource.getItemName().equalsIgnoreCase(modelInfo.getName())) {
            // Remove the current model info
            getWorkspaceDefn().removeModelInfo(modelInfo.getName());

            // Reset the values in the model info.
            modelInfo.setName(modelResource.getItemName());

            replaceInfo = true;
        }

        try {
            modelInfo.setUuid(modelResource.getUuid());
            modelInfo.setContainerPath(modelResource.getParent().getPath().makeRelative().toString());
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }

        if (replaceInfo) {
            // Add the info back to the defn
            getWorkspaceDefn().addModelInfo(modelInfo);

            fireChangeEvent();
        }

        save();
    }

    private void save() {
        try {
            defReaderWriter.write(new FileOutputStream(this.defnFile), getWorkspaceDefn(), headerProps);
        } catch (IOException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        } catch (Exception theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }
    }

    /**
     * @since 4.3
     */
    protected void fireChangeEvent() {
        if (!this.listenerList.isEmpty()) {
            for (Iterator it = listenerList.iterator(); it.hasNext();) {
                IChangeListener listener = (IChangeListener)it.next();
                listener.stateChanged(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.ExecutionConfigurationEvent)
     */
    @Override
    public void configurationChanged( ExecutionConfigurationEvent event ) {
        // Need to check all source bindings and remove any "stale" ones. Basicaly
        // Need to check if any connector bindings have been removed.
        Collection staleModelInfos = new ArrayList();

        for (SourceModelInfo nextModelInfo : workspaceDefn.getModels()) {
            Collection<Connector> connectors = nextModelInfo.getConnectors();

            if (!allBindingsExist(connectors)) {
                staleModelInfos.add(nextModelInfo);
            }
        }

        if (!staleModelInfos.isEmpty()) {
            removeSourceBindings(staleModelInfos);
        }
    }

    /*
     * Helper method that checks if a collection os binding names still exist in the ConfigurationManager
     */
    private boolean allBindingsExist( Collection<Connector> connectors ) {
        // TODO if server is down but we have a source binding we don't want to remove binding
        // TODO do we need an OfflineConnector class???
        for (Connector connector : connectors) {
            if (this.executionAdmin.getBinding(nextName) == null) {
                return false;
            }
        }
        return true;
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
            fireChangeEvent();
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

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.2
     */
    public void resourceChanged( IResourceChangeEvent theEvent ) {
        IResource resource = theEvent.getResource();

        if (resource != null) {
            if (ResourceChangeUtilities.isPreDelete(theEvent)) {
                if (resource instanceof IProject) { // if Project, then delete all contained model's source bindings
                    removeSourceBindingForProject(resource.getName());
                } else { // if Model, then only do model's source binding
                    String nm = resource.getName();

                    if (nm != null) {
                        removeSourceBinding(nm);
                    }
                }
            } else if (ResourceChangeUtilities.isPostChange(theEvent)) {
                if (theEvent.getDelta().getResource() instanceof WorkspaceRoot) {
                    fireChangeEvent();
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.refactor.IRefactorResourceListener#notifyRefactored(com.metamatrix.modeler.core.refactor.RefactorResourceEvent)
     * @since 5.0
     */
    public void notifyRefactored( RefactorResourceEvent theEvent ) {
        switch (theEvent.getType()) {
            case RefactorResourceEvent.TYPE_MOVE: {
                if (theEvent.getResource() instanceof IFile) {
                    resetSourceModelInfo(theEvent.getResource().getName(), (IFile)theEvent.getResource());
                } else if (theEvent.getResource() instanceof IFolder) {
                    // Folder we refactored plus it's contents
                    resetForRefactoredFolder(theEvent.getResource());
                }
            }
                break;

            case RefactorResourceEvent.TYPE_RENAME: {
                if (theEvent.getResource() instanceof IFile) {
                    resetSourceModelInfo(theEvent.getOriginalPath().lastSegment(), (IFile)theEvent.getResource());
                } else if (theEvent.getResource() instanceof IFolder) {
                    // Folder we refactored plus it's contents
                    resetForRefactoredFolder(theEvent.getResource());
                }
            }
                break;

            case RefactorResourceEvent.TYPE_DELETE: {
                // if(
                removeSourceBinding(theEvent.getOriginalPath().lastSegment());
            }
                break;
        }
    }

    private void resetSourceModelInfo( String modelNameWithExtension,
                                       IFile newModelResource ) {
        ModelResource mr = null;

        try {
            mr = ModelUtil.getModelResource(newModelResource, false);
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }
        if (mr != null) {
            SourceModelInfo smi = getSourceModelInfo(modelNameWithExtension);
            if (smi != null) {
                resetSourceInfo(mr, smi);
            }
        }
    }

    /*
     * This method 
     */
    private void resetForRefactoredFolder( IResource refactoredFolder ) {
        if (refactoredFolder instanceof IFolder) {
            try {
                IResource[] children = ((IFolder)refactoredFolder).members();

                for (int i = 0; i < children.length; i++) {
                    if (ModelUtil.isModelFile(children[i])) {
                        resetSourceModelInfo(children[i].getFullPath().lastSegment(), (IFile)children[i]);
                    } else if (children[i] instanceof IFolder) {
                        resetForRefactoredFolder(children[i]);
                    }
                }
            } catch (CoreException theException) {
                DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
            }

        }
    }

}

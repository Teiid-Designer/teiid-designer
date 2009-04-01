/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

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

import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.refactor.IRefactorResourceListener;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;


/** 
 * This class is designed to manage a WorkspaceBindings.def file containing all workspace model-to-connector bindings.
 * 
 * This file resides in the runtime workspace's .metadata/.plugins/com.metamatrix.modeler.dqp/workspaceConfig directory.  If the
 * workspace is deleted (i.e. .metadata directory), the bindings will have to be re-created.
 * 
 * Connector Bindings (i.e. Connectors) and Connector Types are still managed by <code>ConfigurationManager</code> via a configuration.xml
 * file.
 * @since 5.0
 */
public class WorkspaceConfigurationManager implements IChangeNotifier, IChangeListener, IResourceChangeListener, IRefactorResourceListener {
    
    public enum BindingAssignmentResult {SUCCESS, MULTIPLE_TYPES, TYPE_NOT_FOUND, ERROR}

    //private static final String PREFIX = I18nUtil.getPropertyPrefix(WorkspaceConfigurationManager.class);

    private Collection listenerList;
    
    private File defnFile;
    
    private BasicWorkspaceDefn workspaceDefn;
    
    private Properties headerProps;
    
    ConfigurationManager configMgr = DqpPlugin.getInstance().getConfigurationManager();
    
    private WorkspaceDefnReaderWriter defReaderWriter = new WorkspaceDefnReaderWriter();
    
    private BasicConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);
    
    /**
     * Constructor initialized with a File representing the WorkspaceBindings.def 
     * @param defnFile
     * @since 5.0
     */
    public WorkspaceConfigurationManager(File defnFile) {
        this.defnFile = defnFile;
        this.headerProps = new Properties();
        listenerList = new ArrayList(1);
        configMgr.addChangeListener(this);
        
        WorkspaceNotificationListener listener = new WorkspaceNotificationListener();
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(listener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    /**
     * Returns the BasicWorkspacDefn instance which acts as the model for the WorkspaceBindings.def file. 
     * @return
     * @since 5.0
     */
    public BasicWorkspaceDefn getWorkspaceDefn() {
        if( this.workspaceDefn == null ) {
            workspaceDefn = new BasicWorkspaceDefn();
        }
        return this.workspaceDefn;
    }

    /**
     * Saves the WorkspaceBindings.def file.
     * @throws IOException
     * @throws Exception
     * @since 5.0
     */
    public void save() throws IOException, Exception {
        defReaderWriter.write(new FileOutputStream(this.defnFile), getWorkspaceDefn(), headerProps);
    }
    
    private void internalSave() {
        try {
            save();
        } catch (IOException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        } catch (Exception theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }
    }
    
    /**
     * Loads the WorkspaceBindings.def file 
     * @throws IOException
     * @throws Exception
     * @since 5.0
     */
    public void load() throws IOException, Exception {
        if( this.defnFile.exists() && this.defnFile.length() > 0 ) {
            workspaceDefn = defReaderWriter.read(new FileInputStream(this.defnFile));
        }

        if( workspaceDefn != null ) {
            
        }
    }
    
    /**
     * Helper method to determine if a model is mapped to a source. Basically it checks if the WorkspaceBindings.def file contains
     * a source binding to the input <code>ModelResource</code> 
     * @param modelResource
     * @return
     * @since 5.0
     */
    public boolean modelIsMappedToSource(ModelResource modelResource) {
        ModelInfo modelInfo = getWorkspaceDefn().getModel(modelResource.getItemName());
        
        return modelInfo != null;
    }
    
    /**
     * Returns a collection of any ConnectorsBindngs bound to the input <code>ModelResource</code> 
     * @param String
     * @return
     * @since 5.0
     */
    public Collection<ConnectorBinding> getBindingsForModel(String modelName) {
        Collection<ConnectorBinding> bindings = Collections.emptyList();
        
        ModelInfo modelInfo = getWorkspaceDefn().getModel(modelName);
        
        if( modelInfo != null && modelInfo.getConnectorBindingNames() != null ) {
            Object[] bindingNames = modelInfo.getConnectorBindingNames().toArray();

            bindings = new ArrayList<ConnectorBinding>(bindingNames.length);
            String bindingName = null;
            ConnectorBinding binding = null;
            for(int i=0; i<bindingNames.length; i++ ) {
                bindingName = (String)bindingNames[i];
                binding = configMgr.getBinding(bindingName);
            }
            if( binding != null ) {
                bindings.add(binding);
            }
        }
        return bindings;
    }
    
    /**
     *  
     * @param modelName
     * @return
     * @since 5.0
     */
    public SourceModelInfo getSourceModelInfo(String modelName) {
        return (SourceModelInfo)getWorkspaceDefn().getModel(modelName);
    }
    
    /**
     * Helper method which finds and returns the <code>JdbcSource</code> object inside a <code>ModelResource</code>
     * @param modelResource
     * @return
     * @since 5.0
     */
    public JdbcSource getJdbcSource(ModelResource modelResource) {
        Collection allEObjects = null;
        
        try {
            allEObjects = modelResource.getEObjects();
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }
        if( allEObjects != null && !allEObjects.isEmpty() ) {
            for( Iterator iter = allEObjects.iterator(); iter.hasNext(); ) {
                Object nextObj = iter.next();
                if( nextObj instanceof JdbcSource ) {
                    return (JdbcSource)nextObj;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Creates a new <code>SourceModelInfo</code> object, adds the input connector binding to the object and sets the UUID and Path properties
     * to the values from the <code>ModelResource</code>. 
     * @param modelResource
     * @param binding
     * @since 5.0
     */
    public void createSourceBinding(ModelResource modelResource, ConnectorBinding binding) {
        SourceModelInfo modelInfo = new SourceModelInfo(modelResource.getItemName());
        modelInfo.addConnectorBindingByName(binding.getName());
        try {
            modelInfo.setUuid(modelResource.getUuid());
            modelInfo.setContainerPath(modelResource.getParent().getPath().makeRelative().toString());
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }

        getWorkspaceDefn().addModelInfo(modelInfo);
        
        fireChangeEvent();
        
        internalSave();
    }
    
    /**
     * Removes the source binding for the input <code>ModelResource</code>.
     * @param modelResource
     * @since 5.0
     */
    public void removeSourceBinding(ModelResource modelResource) {
        String modelName = modelResource.getItemName();

        removeSourceBinding(modelName);
    }
    
    /**
     * Removes the source binding for the input model name. 
     * @param modelName
     * @since 5.0
     */
    public void removeSourceBinding(String modelName) {

        getWorkspaceDefn().removeModelInfo(modelName);
        
        fireChangeEvent();
        
        internalSave();
    }
    
    /**
     * Removes the source binding for the input <code>ModelInfo</code> 
     * @param modelInfo
     * @since 5.0
     */
    public void removeSourceBinding(ModelInfo modelInfo) {

        removeSourceBinding(modelInfo.getName());
    }
    
    /**
     * Helper method to remove source bindings for a collection of objects which may be of type:
     *   <code>ModelInfo</code>
     *   <code>String</code> 
     *   <code>ModelResource</code> 
     * @param objects
     * @since 5.0
     */
    public void removeSourceBindings(Collection objects) {
        for( Iterator iter = objects.iterator(); iter.hasNext(); ) {
            Object nextObj = iter.next();
            if( nextObj instanceof ModelInfo ) {
                removeSourceBinding((ModelInfo)nextObj);
            } else if( nextObj instanceof String ) {
                removeSourceBinding((String)nextObj);
            } else if( nextObj instanceof ModelResource ) {
                removeSourceBinding((ModelResource)nextObj);
            }
        }
    }
    
    
    /**
     * Removes the source bindings associated with models under a project. 
     * @param modelName
     * @since 5.0
     */
    public void removeSourceBindingForProject(String projectName) {

        getWorkspaceDefn().removeModelInfosForProject(projectName);
        
        fireChangeEvent();
        
        internalSave();
    }
    
    /**
     * Creates a copy of an input connector binding and sets the name to the newName provided. 
     * @param binding
     * @param newName
     * @param addBindingToConfig
     * @return
     * @throws CloneNotSupportedException
     * @since 5.0
     */
    public ConnectorBinding cloneConnectorBinding(ConnectorBinding binding, String newName, boolean addBindingToConfig) throws Exception {
        ConnectorBinding newBinding = editor.createConnectorComponent(Configuration.NEXT_STARTUP_ID, binding, newName, null);
        
        
        if( newBinding != null && addBindingToConfig) {
            this.configMgr.addBinding(newBinding);
        }
        return newBinding;
        
    }
    

    /**
     * Utility method to return a collection of connector bindings from the <code>ConfigurationManager</code> 
     * @return
     * @since 5.0
     */
    public Collection getConnectorBindings() {
        return this.configMgr.getConnectorBindings();
    }
    
    public Collection getModelsForBinding(String connectorBindingName) {
        return getWorkspaceDefn().getModelsForBinding(connectorBindingName);
    }
    
    /**
     *  
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener(IChangeListener theListener) {

        if(listenerList.contains(theListener)) return;
        
        listenerList.add(theListener);
    }
    
    /**
     *  
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener(IChangeListener theListener) {
        listenerList.remove(theListener);
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
    private void resetSourceInfo(ModelResource modelResource, SourceModelInfo modelInfo) {
        boolean replaceInfo = false;
        
        if( !modelResource.getItemName().equalsIgnoreCase(modelInfo.getName()) ) {
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
        
        if( replaceInfo ) {
            // Add the info back to the defn
            getWorkspaceDefn().addModelInfo(modelInfo);
            
            fireChangeEvent();
        }
        
        internalSave();
    }
    
    /**
     *  
     * 
     * @since 4.3
     */
    protected void fireChangeEvent() {               
        if(listenerList == null || listenerList.isEmpty()) return;
        
        for(Iterator it = listenerList.iterator(); it.hasNext();) {
            IChangeListener listener = (IChangeListener)it.next();
            listener.stateChanged(this);
        }
    }

    /**
     * Should be wired to listen for ConfigurationManager changed events.
     * 
     * This method checks all current source bindings (i.e. SourceModelInfo's), gets any binding names and verifies
     * they still exist in the ConfigurationManager. If not, then these source bindings are removed also.
     * @param theSource
     * @since 5.0
     */
    public void stateChanged(IChangeNotifier theSource) {
        // Need to check all source bindings and remove any "stale" ones. Basicaly
        if( theSource == configMgr) {
            // Need to check if any connector bindings have been removed.
            Collection staleModelInfos = new ArrayList();
            Collection allModelInfos = new ArrayList(workspaceDefn.getModels());
            
            for( Iterator iter = allModelInfos.iterator(); iter.hasNext(); ) {
                SourceModelInfo nextModelInfo = (SourceModelInfo)iter.next();
                Collection bindingNames = nextModelInfo.getConnectorBindingNames();
                if( !allBindingsExist(bindingNames) ) {
                    staleModelInfos.add(nextModelInfo);
                }
            }
            
            if( ! staleModelInfos.isEmpty() ) {
                removeSourceBindings(staleModelInfos);
            }
        }
    }
    
    
    /*
     * Helper method that checks if a collection os binding names still exist in the ConfigurationManager
     */
    private boolean allBindingsExist(Collection bindingNames) {
        for( Iterator iter = bindingNames.iterator(); iter.hasNext(); ) {
            String nextName = (String)iter.next();
            if( configMgr.getBinding(nextName) == null ) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param modelName
     *            the model name being used to create the binding name
     * @return the unique binding name
     * @since 5.5.3
     */
    public String createConnectorBindingName(String modelName) {
        String newBindingName = ModelerDqpUtils.createNewBindingName(modelName);
        WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getWorkspaceConfig();

        if (!wsConfigMgr.isUniqueBindingName(newBindingName)) {
            StringBuffer temp = new StringBuffer(newBindingName);

            int i = 0;
            temp.append(i);

            while (!wsConfigMgr.isUniqueBindingName(temp.toString())) {
                // remove the last index from the name
                temp.setLength(temp.length() - Integer.toString(i).length());

                // add the next index to the name
                ++i;
                temp.append(i);
            }

            newBindingName = temp.toString();
        }

        return newBindingName.toString();
    }

    /**
     * Finds or creates a <code>ConnectorBinding</code> for the specified model and JDBC source. Once found or created the
     * binding is assigned into the workspace configuration. If more than one matching binding is found the first one is assigned.
     * If no matching binding is found, one is created if possible.
     * 
     * @param modelResource
     *            the model that needs a binding
     * @param jdbcSource
     *            the source information
     * @return <code>true</code> if the connector binding was assigned; <code>false</code> if a binding could not be created
     *         because multiple connector types were found, no connector types were found, or if an exception was caught creating
     *         the binding
     * @since 5.5.3
     */
    public BindingAssignmentResult assignConnectorBinding(ModelResource modelResource,
                                                          JdbcSource jdbcSource,
                                                          String password) throws Exception {
        BindingAssignmentResult bindingAssigned = BindingAssignmentResult.SUCCESS;
        Collection<ConnectorBinding> bindingMatches = findMatchingConnectorBindings(jdbcSource);

        if (bindingMatches.isEmpty()) {
            Collection<ConnectorBindingType> bindingTypeMatches = findMatchingConnectorBindingTypes(jdbcSource);
            int numTypesFound = bindingTypeMatches.size();

            if (numTypesFound == 1) {
                ConnectorBindingType type = bindingTypeMatches.iterator().next();
                bindingAssigned = assignConnectorBinding(modelResource, type, jdbcSource, password);
            } else if (numTypesFound > 1) {
                bindingAssigned = BindingAssignmentResult.MULTIPLE_TYPES;
            } else {
                bindingAssigned = BindingAssignmentResult.TYPE_NOT_FOUND;
                String key = I18nUtil.getPropertyPrefix(WorkspaceConfigurationManager.class) + "missingConnectorBindingType"; //$NON-NLS-1$
                String msg = DqpPlugin.Util.getString(key, new Object[] {
                    modelResource.getItemName(), jdbcSource.getDriverName()
                });
                DqpPlugin.Util.log(IStatus.ERROR, msg);
            }
        } else {
            ConnectorBinding binding = bindingMatches.iterator().next();
            
            try {
                ModelerDqpUtils.setConnectorBindingPassword(binding, password);
            } catch (Exception e) {
                // still want to create the source binding if setting the password failed
                DqpPlugin.Util.log(e);
            }
            
            createSourceBinding(modelResource, binding);
        }

        return bindingAssigned;
    }

    public BindingAssignmentResult assignConnectorBinding(ModelResource modelResource,
                                                          ConnectorBindingType connectorBindingType,
                                                          JdbcSource jdbcSource,
                                                          String password) {
        BindingAssignmentResult result = BindingAssignmentResult.SUCCESS;
        String name = modelResource.getItemName();
        
        // if item name includes the file extension remove it
        int index = name.indexOf('.');
        
        if (index != -1) {
            name = name.substring(0, index);
        }
        
        String newBindingName = createConnectorBindingName(name);

        try {
        	Properties props = new Properties();
        	props.setProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_PASSWORD, password);
            ConnectorBinding binding = this.configMgr.createConnectorBinding(jdbcSource, connectorBindingType, newBindingName, props);
            
            try {
                ModelerDqpUtils.setConnectorBindingPassword(binding, password);
            } catch (Exception e) {
                // still want to create the source binding if setting the password failed
                DqpPlugin.Util.log(e);
            }
            
            createSourceBinding(modelResource, binding);
        } catch (Exception e) {
            result = BindingAssignmentResult.ERROR;
            String key = I18nUtil.getPropertyPrefix(WorkspaceConfigurationManager.class) + "errorCreatingConnectorBinding"; //$NON-NLS-1$
            String msg = DqpPlugin.Util.getString(key, new Object[] {
                modelResource.getItemName(), connectorBindingType.getName()
            });
            DqpPlugin.Util.log(IStatus.ERROR, e, msg);
        }

        return result;
    }

    public Collection<ConnectorBindingType> findMatchingConnectorBindingTypes(JdbcSource jdbcSource) {
        Collection<ConnectorBindingType> matches = new ArrayList<ConnectorBindingType>();

        for (Iterator itr = this.configMgr.getConnectorTypes().iterator(); itr.hasNext();) {
            ConnectorBindingType bindingType = (ConnectorBindingType)itr.next();
            Properties connectorTypeProps = bindingType.getDefaultPropertyValues();
            String driverClassName = connectorTypeProps.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);

            if (StringUtil.isEmpty(driverClassName)) {
                continue;
            }

            if (StringUtil.isEmpty(jdbcSource.getDriverClass()) || !driverClassName.equalsIgnoreCase(jdbcSource.getDriverClass())) {
                continue;
            }

            matches.add(bindingType);
        }

        return matches;
    }

    public Collection<ConnectorBinding> findMatchingConnectorBindings(JdbcSource jdbcSource) {
        Collection<ConnectorBinding> matches = new ArrayList<ConnectorBinding>();

        for (Iterator itr = getConnectorBindings().iterator(); itr.hasNext();) {
            ConnectorBinding binding = (ConnectorBinding)itr.next();
            String driverClassName = binding.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);
            String url = binding.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL);
            String user = binding.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER);

            if (StringUtil.isEmpty(driverClassName) || StringUtil.isEmpty(url)) {
                continue;
            }

            // check driver class
            if (StringUtil.isEmpty(jdbcSource.getDriverClass()) || !driverClassName.equals(jdbcSource.getDriverClass())) {
                continue;
            }

            // check url
            if (StringUtil.isEmpty(jdbcSource.getUrl()) || !url.equalsIgnoreCase(jdbcSource.getUrl())) {
                continue;
            }

            // check user
            String userName = jdbcSource.getUsername();
            if ((StringUtil.isEmpty(userName) && !StringUtil.isEmpty(user))
                || (!StringUtil.isEmpty(userName) && StringUtil.isEmpty(user))
                || ((!StringUtil.isEmpty(userName) && !StringUtil.isEmpty(user)) && !user.equalsIgnoreCase(userName))) {
                continue;
            }

            matches.add(binding);
        }

        return matches;
    }
    
    /**
     * @param proposedName
     *            the name being checked
     * @return <code>true</code> if a binding by that name does not currently exist in the workspace
     * @since 5.5.3
     */
    public boolean isUniqueBindingName(String proposedName) {
        return (this.configMgr.getBinding(proposedName) == null);
    }
    
    public ConfigurationManager getConfigurationManager() {
        return this.configMgr;
    }
    
    // ==================================================================================
    //                        I N N E R   C L A S S
    // ==================================================================================
    
    private class WorkspaceNotificationListener implements ModelWorkspaceNotificationListener {
        
        public WorkspaceNotificationListener() {
        }
        public void notifyAdd(ModelWorkspaceNotification notification) {
        }
        public void notifyRemove(ModelWorkspaceNotification notification) {
        }
        public void notifyMove(ModelWorkspaceNotification notification) {
        }
        public void notifyRename(ModelWorkspaceNotification notification) {
        }
        public void notifyOpen(ModelWorkspaceNotification notification) {
            fireChangeEvent();
        }
        public void notifyClosing(ModelWorkspaceNotification notification) {
        }
        public void notifyClosed(ModelWorkspaceNotification notification) {
        }
        public void notifyChanged(Notification theNotification) {
        }
        public void notifyChange(ModelWorkspaceNotification notification) {
        }
        public void notifyReloaded(ModelWorkspaceNotification notification) {
        }
        public void notifyClean(final IProject proj) {
        }
        
    }
    
    /** 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.2
     */
    public void resourceChanged(IResourceChangeEvent theEvent) {
        IResource resource = theEvent.getResource();

        if (resource != null ) {
            if (ResourceChangeUtilities.isPreDelete(theEvent)) {
                
                if( resource instanceof IProject ) {  // if Project, then delete all contained model's source bindings
                    removeSourceBindingForProject(resource.getName());
                } else { // if Model, then only do model's source  binding
                    String nm = resource.getName();
                    if( nm != null ) {
                        removeSourceBinding(nm);
                    }
                }
            } else if( ResourceChangeUtilities.isPostChange(theEvent) ) {
                if( theEvent.getDelta().getResource() instanceof WorkspaceRoot ) {
                    fireChangeEvent();
                }
            }
        }
    }

    /**
     *  
     * @see com.metamatrix.modeler.core.refactor.IRefactorResourceListener#notifyRefactored(com.metamatrix.modeler.core.refactor.RefactorResourceEvent)
     * @since 5.0
     */
    public void notifyRefactored(RefactorResourceEvent theEvent) {
        switch( theEvent.getType() ) {
            case RefactorResourceEvent.TYPE_MOVE: {
                if( theEvent.getResource() instanceof IFile ) {
                    resetSourceModelInfo(theEvent.getResource().getName(), (IFile)theEvent.getResource());
                } else if( theEvent.getResource() instanceof IFolder ) {
                    // Folder we refactored plus it's contents
                    resetForRefactoredFolder( theEvent.getResource() );
                }
            } break;
            
            case RefactorResourceEvent.TYPE_RENAME: {
                if( theEvent.getResource() instanceof IFile ) {
                    resetSourceModelInfo(theEvent.getOriginalPath().lastSegment(), (IFile)theEvent.getResource());
                } else if( theEvent.getResource() instanceof IFolder ) {
                    // Folder we refactored plus it's contents
                    resetForRefactoredFolder( theEvent.getResource() );
                }
            } break;
            
            case RefactorResourceEvent.TYPE_DELETE: {
                // if( 
                removeSourceBinding(theEvent.getOriginalPath().lastSegment());
            } break;
        }
    }
    
    private void resetSourceModelInfo(String modelNameWithExtension, IFile newModelResource) {
        ModelResource mr = null;
        
        try {
            mr = ModelUtil.getModelResource(newModelResource, false);
        } catch (ModelWorkspaceException theException) {
            DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
        }
        if( mr != null ) {
            SourceModelInfo smi = getSourceModelInfo(modelNameWithExtension);
            if( smi != null ) {
                resetSourceInfo(mr, smi);
            }
        }
    }
    
    /*
     * This method 
     */
    private void resetForRefactoredFolder(IResource refactoredFolder) {
        if( refactoredFolder instanceof IFolder ) {
            try {
                IResource[] children = ((IFolder)refactoredFolder).members();
                
                for(int i=0; i<children.length; i++ ) {
                    if( ModelUtil.isModelFile(children[i]) ) {
                        resetSourceModelInfo(children[i].getFullPath().lastSegment(), (IFile)children[i]);
                    } else if( children[i] instanceof IFolder ) {
                        resetForRefactoredFolder(children[i]);
                    }
                }
            } catch (CoreException theException) {
                DqpPlugin.Util.log(IStatus.ERROR, theException.getMessage());
            }

        }
    }
}

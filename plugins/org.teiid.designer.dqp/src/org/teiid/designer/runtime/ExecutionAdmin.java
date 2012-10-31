/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.osgi.util.NLS;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminFactory;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterUtil;
import org.teiid.designer.runtime.connection.IPasswordProvider;
import org.teiid.designer.runtime.connection.ModelConnectionMatcher;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.vdb.Vdb;
import org.teiid.jdbc.TeiidDriver;


/**
 *
 *
 * @since 8.0
 */
public class ExecutionAdmin {

    private final Admin admin;
    protected Map<String, TeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Map<String, TeiidDataSource> dataSourceByNameMap;
    protected Set<String> dataSourceTypeNames;
    private final EventManager eventManager;
    private final TeiidServer teiidServer;
    // private Set<VDB> vdbs;
    private Set<TeiidVdb> teiidVdbs;
    private final ModelConnectionMatcher connectionMatcher;

    private boolean loaded = false;

    public enum PingType {
        ADMIN, JDBC;
    }
    
    /**
     * Constructor used for testing purposes only. 
     * 
     * DO NOT MAKE PUBLIC!!
     * 
     * @param admin the associated Teiid Admin API (never <code>null</code>)
     * @param teiidServer the server this admin belongs to (never <code>null</code>)
     * @throws Exception if there is a problem connecting the server
     */
    protected ExecutionAdmin(Admin admin, TeiidServer teiidServer) throws Exception {
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        
        this.admin = admin;
        this.teiidServer = teiidServer;
        this.eventManager = teiidServer.getEventManager();
        this.connectionMatcher = new ModelConnectionMatcher();
        
        init();
    }
    
    /**
     * Default Constructor 
     * 
     * @param teiidServer the server this admin belongs to (never <code>null</code>)
     * 
     * @throws Exception if there is a problem connecting the server
     */
    public ExecutionAdmin(TeiidServer teiidServer) throws Exception {
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        
        TeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        char[] passwordArray = null;
        if (teiidAdminInfo.getPassword() != null) {
            passwordArray = teiidAdminInfo.getPassword().toCharArray();
        }
        
        this.admin = AdminFactory.getInstance().createAdmin( teiidServer.getHost(), 
                                                                                              teiidAdminInfo.getPortNumber(), 
                                                                                              teiidAdminInfo.getUsername(),
                                                                                              passwordArray);

        this.teiidServer = teiidServer;
        this.eventManager = teiidServer.getEventManager();
        this.connectionMatcher = new ModelConnectionMatcher();

        init();
    }

    /**
     * @param name the name of the data source
     * @return true if data source exists with the provided name. else false.
     */
    public boolean dataSourceExists( String name ) {
        // Check if exists, return false
        if (this.dataSourceNames.contains(name)) {
            return true;
        }

        return false;
    }

    /**
     * Removes the data source from the teiid server (if exists)
     * 
     * @param jndiName the source jndi name
     * @throws Exception if failer in deleting data source on server
     */
    public void deleteDataSource( String jndiName ) throws Exception {
        // Check if exists, return false
        if (this.dataSourceNames.contains(jndiName)) {
            this.admin.deleteDataSource(jndiName);

            if (!this.admin.getDataSourceNames().contains(jndiName)) {
                this.dataSourceNames.remove(jndiName);
                TeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);

                if (tds != null) {
                    this.dataSourceByNameMap.remove(jndiName);
                    this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveDataSourceEvent(tds));

                }
            }
        }
        //    	
        // // TODO: I18n
        // throw new Exception(Util.getString("errorDeletingDataSource", jndiName, getServer().getUrl()));
    }

    /**
     * Deploys the VDB (IFile) to the related Teiid server
     * 
     * @param vdbFile the vdb file
     * @return the deployed VDB
     * @throws Exception if deployment fails
     */
    public VDB deployVdb( IFile vdbFile ) throws Exception {
        CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        String vdbName = vdbFile.getFullPath().lastSegment();
        String vdbNameNoExt = vdbFile.getFullPath().removeFileExtension().lastSegment();
        
        admin.deploy(vdbName, vdbFile.getContents());
              
        // Refresh VDBs list
        refreshVDBs();

        // TODO should get version from vdbFile
        VDB vdb = admin.getVDB(vdbNameNoExt, 1);

        // If the VDB is still loading, refresh again and potentially start refresh job
        if(vdb.getStatus().equals(VDB.Status.LOADING) && vdb.getValidityErrors().isEmpty()) {
            // Give a 0.5 sec pause for the VDB to finish loading metadata.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }   
            // Refresh again to update vdb states
            refreshVDBs();
            vdb = admin.getVDB(vdbNameNoExt, 1);
            // Determine if still loading, if so start refresh job.  User will get dialog that the
            // vdb is still loading - and try again in a few seconds
            if(vdb.getStatus().equals(VDB.Status.LOADING) && vdb.getValidityErrors().isEmpty()) {
                final Job refreshVDBsJob = new RefreshVDBsJob(vdbNameNoExt);
                refreshVDBsJob.schedule();
            }
        }

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createDeployVDBEvent(vdb));
        
        return vdb;
    }
    
    /**
     * Deploys the input Vdb archive file to the related Teiid server
     * 
     * @param vdb the local Vdb to deploy
     * @return the deployed VDB
     * @throws Exception if deployment fails
     */
    public VDB deployVdb( Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        return deployVdb(vdb.getFile());
    }

    /**
     * Closes the admin and re-sets the cached items from the server
     */
    public void disconnect() {
    	// 
    	this.admin.close();
        this.translatorByNameMap = new HashMap<String, TeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, TeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        this.teiidVdbs = Collections.emptySet();
    }

    /**
     * Returns a teiid data source object if it exists in this server
     * 
     * @param name the data source name
     * @return the teiid data source object (can be <code>null</code>)
     */
    public TeiidDataSource getDataSource(String name) {
        return this.dataSourceByNameMap.get(name);
    }
    
    @SuppressWarnings("javadoc")
	public Collection<TeiidDataSource> getDataSources() {
        return this.dataSourceByNameMap.values();
    }

    @SuppressWarnings("javadoc")
	public Set<String> getDataSourceTypeNames() {
        return this.dataSourceTypeNames;
    }

    /**
     * @return the event manager (never <code>null</code>)
     */
    public EventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * @param model the model containing source info
     * @param jndiName the JNDI name
     * @param previewVdb the model's preview vdb
     * @param passwordProvider the password provider
     * @return Teiid data source object
     * @throws Exception if data source creation fails
     */
    public TeiidDataSource getOrCreateDataSource( IFile model,
                                                  String jndiName,
                                                  boolean previewVdb,
                                                  IPasswordProvider passwordProvider ) throws Exception {

        // first check to see if DS with that name already exists
        TeiidDataSource dataSource = getDataSource(jndiName);

        if (dataSource != null) {
            return dataSource;
        }

        // need to create a DS
        ModelResource modelResource = ModelUtil.getModelResource(model, true);
        ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
        IConnectionInfoProvider connInfoProvider = manager.getProvider(modelResource);
        IConnectionProfile modelConnectionProfile = connInfoProvider.getConnectionProfile(modelResource);
        		
        Properties props = connInfoProvider.getTeiidRelatedProperties(modelConnectionProfile);
        String dataSourceType = connInfoProvider.getDataSourceType();

        if (!props.isEmpty()) {
            // The data source property key represents what's needed as a property for the Teiid Data Source
            // This is provided by the getDataSourcePasswordPropertyKey() method.
            String dsPasswordKey = connInfoProvider.getDataSourcePasswordPropertyKey();
            boolean requiresPassword = (dsPasswordKey != null && connInfoProvider.requiresPassword(modelConnectionProfile));

            if (modelConnectionProfile != null) {
                String pwd = null;

                // Check Password
                if (requiresPassword) {
                    // Check connection info provider. Property will be coming in with a key = "password"
                    pwd = modelConnectionProfile.getBaseProperties().getProperty(connInfoProvider.getPasswordPropertyKey());

                    if (pwd == null) {
                        IConnectionProfile existingConnectionProfile = ProfileManager.getInstance().getProfileByName(modelConnectionProfile.getName());

                        if (existingConnectionProfile != null) {
                            // make sure the password property is there. if not get from connection profile.
                            // Use DTP's constant for profile: IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID =
                            // org.eclipse.datatools.connectivity.db.password
                            // DTP's connection profile "password" key, if exists for a profile type, is returned via the
                            // provider's getPasswordPropertyKey() method. This can be different than
                            // getDataSourcePasswordPropertyKey().
                            if (props.getProperty(connInfoProvider.getPasswordPropertyKey()) == null) {
                                pwd = existingConnectionProfile.getBaseProperties().getProperty(connInfoProvider.getPasswordPropertyKey());
                            }
                        }

                        if ((pwd == null) && (passwordProvider != null)) {
                            pwd = passwordProvider.getPassword(modelResource.getItemName(), modelConnectionProfile.getName());
                        }
                    }

                    if (pwd != null) {
                        props.setProperty(dsPasswordKey, pwd);
                    }
                }

                if (!requiresPassword || (pwd != null)) {
                    TeiidDataSource tds = getOrCreateDataSource(jndiName, jndiName, dataSourceType, props);
                    tds.setPreview(previewVdb);
                    return tds;
                }
            }
        }

        return null;
    }

    /**
     * @param displayName the JNDI display name
     * @param jndiName the JNDI name
     * @param typeName the translator type name
     * @param properties the list of teiid-related connection properties
     * @return true if data source is created. false if it already exists
     * @throws Exception if data source creation fails
     */
    public TeiidDataSource getOrCreateDataSource( String displayName,
                                                  String jndiName,
                                                  String typeName,
                                                  Properties properties ) throws Exception {
        CoreArgCheck.isNotEmpty(displayName, "displayName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(jndiName, "jndiName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(typeName, "typeName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(properties, "properties"); //$NON-NLS-1$

        // Check if exists, return false
        if (dataSourceExists(jndiName)) {
            TeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);
            if (tds != null) {
                return tds;
            }
        }

        // For JDBC types, find the matching installed driver.  This is done currently by matching
        // the profile driver classname to the installed driver classname
        String connProfileDriverClass = properties.getProperty("driver-class");  //$NON-NLS-1$
        if("connector-jdbc".equals(typeName)) {  //$NON-NLS-1$
            // List of driver jars on the connection profile
            String jarList = properties.getProperty("jarList");  //$NON-NLS-1$
            
            // Get first driver name with the driver class that matches the connection profile
            String dsNameMatch = getDSMatchForDriverClass(connProfileDriverClass);
            
            // If a matching datasource was found, set typename
            if(dsNameMatch!=null) {
                typeName=dsNameMatch;
            // No matching datasource, attempt to deploy the driver if jarList is populated.
            } else if(jarList!=null && jarList.trim().length()>0) {
                // Try to deploy the jars
                deployJars(this.admin,jarList);
                
                refresh();
                
                // Retry the name match after deployment.
                dsNameMatch = getDSMatchForDriverClass(connProfileDriverClass);
                if(dsNameMatch!=null) {
                    typeName=dsNameMatch;
                }
            }
        }
        // Verify the "typeName" exists.
        if (!this.dataSourceTypeNames.contains(typeName)) {
            if("connector-jdbc".equals(typeName)) {  //$NON-NLS-1$
                throw new Exception(Util.getString("jdcbSourceForClassNameNotFound", connProfileDriverClass, getServer()));  //$NON-NLS-1$
            } else {
                throw new Exception(Util.getString("dataSourceTypeDoesNotExist", typeName, getServer())); //$NON-NLS-1$
            }
        }

        this.admin.createDataSource(jndiName, typeName, properties);

        refreshDataSourceNames();

        // Check that local name list contains new jndiName
        if (dataSourceExists(jndiName)) {
            String nullStr = null;
            TeiidDataSource tds = new TeiidDataSource(nullStr, jndiName, typeName, properties, this);

            this.dataSourceByNameMap.put(jndiName, tds);
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));

            return tds;
        }

        // We shouldn't get here if data source was created
        throw new Exception(Util.getString("errorCreatingDataSource", jndiName, typeName, getServer())); //$NON-NLS-1$
    }

    /*
     * Look for an installed driver that has the driverClass which matches the supplied driverClass name.
     * 
     * @param driverClass the driver class to match
     * @return the name of the matching driver, null if not found
     */
    private String getDSMatchForDriverClass(String driverClass) throws Exception {
        if (driverClass == null)
            return null;
        
        // Get the installed JDBC Driver mappings
        return TeiidServerAdapterUtil.getJDBCDriver(teiidServer.getParent(), driverClass);
    }
    
    /*
     * Deploy all jars in the supplied jarList
     * @param admin the Admin instance
     * @param jarList the colon-separated list of jar path locations
     */
    private void deployJars(Admin admin, String jarList) {
        // Path Entries are colon separated
        String[] jarPathStrs = jarList.split("[:]");  //$NON-NLS-1$

        // Attempt to deploy each jar
        for(String jarPathStr: jarPathStrs) {
            File theFile = new File(jarPathStr);
            if(theFile.exists()) {
                if(theFile.canRead()) {
                    String fileName = theFile.getName();
                    InputStream iStream = null;
                    try {
                        iStream = new FileInputStream(theFile);
                    } catch (FileNotFoundException ex) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.JarDeploymentJarNotFound, theFile.getPath()));
                        continue;
                    }
                    try {
                        admin.deploy(fileName, iStream);
                    } catch (Exception ex) {
                        // Jar deployment failed
                        Util.log(IStatus.ERROR, ex, NLS.bind(Messages.JarDeploymentFailed, theFile.getPath()));
                    }
                } else {
                    // Could not read the file
                    Util.log(IStatus.ERROR, NLS.bind(Messages.JarDeploymentJarNotReadable, theFile.getPath()));
                }
            } else {
                // The file was not found
                Util.log(IStatus.ERROR, NLS.bind(Messages.JarDeploymentJarNotFound, theFile.getPath()));
            }

        }
    }
    
    /**
     * @return the server who owns this admin object (never <code>null</code>)
     */
    public TeiidServer getServer() {
        return this.teiidServer;
    }

    /**
     * @param name the translator name (never <code>null</code> or empty)
     * @return a TeiidTranslator
     * @since 7.0
     */
    public TeiidTranslator getTranslator( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.translatorByNameMap.get(name);
    }

    /**
     * 
     * @return collection of Teiid translators
     */
    public Collection<TeiidTranslator> getTranslators() {
        return this.translatorByNameMap.values();
    }

    /**
     * @param name the name of the VDB being requested (never <code>null</code> or empty)
     * @return the VDB or <code>null</code> if not found
     * @since 7.0
     */
    public VDB getVdb( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$

        for (TeiidVdb vdb : this.teiidVdbs) {
            if (vdb.getName().equals(name)) return vdb.getVdb();
        }

        return null;
    }

    /**
     * @return an unmodifiable set of VDBs deployed on the server
     */
    public Set<TeiidVdb> getVdbs() {
        return this.teiidVdbs;
    }
    
    private void init() throws Exception {
        this.translatorByNameMap = new HashMap<String, TeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, TeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        refreshVDBs();
    }

    private void internalSetPropertyValue( TeiidTranslator translator,
                                           String propName,
                                           String value,
                                           boolean notify ) throws Exception {
        if (translator.isValidPropertyValue(propName, value) == null) {
            String oldValue = translator.getPropertyValue(propName);

            // don't set if value has not changed
            if (oldValue == null) {
                if (value == null) return;
            } else if (oldValue.equals(value)) return;

            // set value
            // this.admin.setTranslatorProperty(translator.getName(), propName, value);

            if (notify) {
                // TODO: Will we ever update Translator properties in TEIID Server?
                // this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
            }
        } else {
            throw new Exception(Util.getString("invalidPropertyValue", value, propName)); //$NON-NLS-1$
        }
    }

    /**
     * @throws Exception if refreshing admin connection fails
     */
    public void load() throws Exception {
        if (!this.loaded) {
            refresh();
            this.loaded = true;
        }
    }
    
    public void close() {
        admin.close();
    }

    /**
     * Refreshes the cached lists and maps of current Teiid objects
     * @throws Exception if refreshing admin connection fails
     */
    public void refresh() throws Exception {
        // populate translator map
        refreshTranslators(this.admin.getTranslators());

        // populate data source type names set
        this.dataSourceTypeNames = new HashSet(this.admin.getDataSourceTemplateNames());

        // populate data source names list
        refreshDataSourceNames();

        this.dataSourceByNameMap.clear();
        Collection<TeiidDataSource> tdsList = connectionMatcher.findTeiidDataSources(this.dataSourceNames, this);
        for (TeiidDataSource ds : tdsList) {
            this.dataSourceByNameMap.put(ds.getName(), ds);
        }

        // populate VDBs and source bindings
        refreshVDBs();

        // notify listeners
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this.teiidServer));
    }

    protected void refreshDataSourceNames() throws Exception {
        // populate data source names list
        this.dataSourceNames = new ArrayList(this.admin.getDataSourceNames());
    }

    /**
     * Refreshes the local collection of Translators on the referenced Teiid server.
     * 
     * @param translators
     * @throws Exception
     */
    protected void refreshTranslators( Collection<? extends Translator> translators ) throws Exception {
        for (Translator translator : translators) {
            if (translator.getName() != null) {
                Collection<? extends PropertyDefinition> propDefs = this.admin.getTemplatePropertyDefinitions(translator.getName());
                this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, this));
            }
        }
    }

    protected void refreshVDBs() throws Exception {
        Collection<? extends VDB> vdbs = Collections.unmodifiableCollection(this.admin.getVDBs());
        Set<TeiidVdb> tmpVdbs = new HashSet();

        for (VDB vdb : vdbs) {
            tmpVdbs.add(new TeiidVdb(vdb, this));
        }

        this.teiidVdbs = Collections.unmodifiableSet(tmpVdbs);
    }

    /**
     * @param translator the translator whose properties are being changed (never <code>null</code>)
     * @param changedProperties a collection of properties that have changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( TeiidTranslator translator,
                               Properties changedProperties ) throws Exception {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(changedProperties.entrySet(), "changedProperties"); //$NON-NLS-1$

        if (changedProperties.size() == 1) {
            String name = changedProperties.stringPropertyNames().iterator().next();
            setPropertyValue(translator, name, changedProperties.getProperty(name));
        } else {

            for (String name : changedProperties.stringPropertyNames()) {
                internalSetPropertyValue(translator, name, changedProperties.getProperty(name), false);
            }
            // this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
        }
    }

    /**
     * @param translator the translator whose property is being changed (never <code>null</code>)
     * @param propName the name of the property being changed (never <code>null</code> or empty)
     * @param value the new value
     * @throws Exception if there is a problem setting the property
     * @since 7.0
     */
    public void setPropertyValue( TeiidTranslator translator,
                                  String propName,
                                  String value ) throws Exception {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propName, "propName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(value, "value"); //$NON-NLS-1$
        internalSetPropertyValue(translator, propName, value, true);
    }

    /**
     * 
     * @param vdbName the vdb name
     * @throws Exception if undeploying vdb fails
     */
    public void undeployVdb( String vdbName) throws Exception {
        this.admin.undeploy(appendVdbExtension(vdbName));
        VDB vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb));
        }
    }
    
    /**
     * 
     * @param vdbName the vdb name
     * @param vdbVersion the vdb version
     * @throws Exception if undeploying vdb fails
     */
    public void undeployVdb( String vdbName, int vdbVersion ) throws Exception {
        this.admin.undeploy(appendVdbExtension(vdbName));
        VDB vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb));
        }
    }

    /**
     * @param vdb the vdb to undeploy
     * @throws Exception if undeploying vdb fails
     */
    public void undeployVdb( VDB vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$

        /* Seems that full name of vdb is actually the name.vdb */
        admin.undeploy(appendVdbExtension(vdb.getName()));

        refreshVDBs();

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb));
    }
    
    /**
     * Append the vdb file extension to the vdb name 
     * if not already appended.
     * 
     * @param vdbName
     * @return
     */
    private String appendVdbExtension(String vdbName) {
        if (vdbName.endsWith(Vdb.FILE_EXTENSION))
            return vdbName;
        
        return vdbName + Vdb.FILE_EXTENSION;
    }
    
    /**
     * Ping the admin client to determine whether if is still connected
     * 
     * @return
     */
    public IStatus ping(PingType pingType) {
        String msg = Util.getString("cannotConnectToServer", teiidServer.getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        try {
            if (this.admin == null)
                throw new Exception(msg);
            
            switch(pingType) {
                case JDBC:
                    return pingJdbc();
                case ADMIN:
                default:
                    return pingAdmin();
            }
        }
        catch (Exception ex) {
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
        }
    }
    
    private IStatus pingAdmin() throws Exception {
        admin.getSessions();
        return Status.OK_STATUS;
    }
    
    private IStatus pingJdbc() {
        String host = teiidServer.getHost();
        TeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();
        
        Connection teiidJdbcConnection = null;
        String url = "jdbc:teiid:ping@mm://" + host + ':' + teiidJdbcInfo.getPort(); //$NON-NLS-1$
        
        try {
            admin.deploy("ping-vdb.xml", new ByteArrayInputStream(TeiidServerUtils.TEST_VDB.getBytes())); //$NON-NLS-1$
            
            try{
                String urlAndCredentials = url + ";user=" + teiidJdbcInfo.getUsername() + ";password=" + teiidJdbcInfo.getPassword() + ';';  //$NON-NLS-1$ //$NON-NLS-2$              
                teiidJdbcConnection = TeiidDriver.getInstance().connect(urlAndCredentials, null);
               //pass
            } catch(SQLException ex){
                String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
                return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
            } finally {
                admin.undeploy("ping-vdb.xml"); //$NON-NLS-1$
                
                if( teiidJdbcConnection != null ) {
                    teiidJdbcConnection.close();
                }
                admin.close();
            }
        } catch (AdminException ex) {
            String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
        } catch (Exception ex) {
            String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
        }
        
        return Status.OK_STATUS;
    }
    
    /**
     * Executes VDB refresh when a VDB is loading - as a background job.
     */
    class RefreshVDBsJob extends Job {

        String vdbName;
        
        /**
         * @param vdbName
         */
        public RefreshVDBsJob(String vdbName ) {
            super("VDB Refresh"); //$NON-NLS-1$
            this.vdbName = vdbName;
            
            setSystem(false);
            setUser(true);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            monitor.beginTask("VDB Refresh", //$NON-NLS-1$
                              IProgressMonitor.UNKNOWN);

            try {
                waitForVDBLoad(this.vdbName);
            } catch (Exception ex) {
            }

            monitor.done();

            return Status.OK_STATUS;
        }
        
        /*
         * Wait for the VDB to finish loading.  Will check status every 5 secs and return when the VDB is loaded.
         * If not loaded within 30sec, it will timeout
         * @param vdbName the name of the VDB
         */
        
        private void waitForVDBLoad(String vdbName) throws Exception {
            int timeoutInSecs = 30;  // time out after 30secs
            
            long waitUntil = System.currentTimeMillis() + timeoutInSecs*1000;
            if (timeoutInSecs < 0) {
                waitUntil = Long.MAX_VALUE;
            }
            boolean first = true;
            do {
                // Pause 5 sec before subsequent attempts
                if (!first) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    first = false;
                }
                // Get the VDB using admin API
                VDB vdb = getVdb(vdbName);
                // Determine if VDB is loading, or whether to wait
                if(vdb!=null) {
                    VDB.Status vdbStatus = vdb.getStatus();
                    // return if no models in VDB, or VDB has errors (done loading)
                    if(vdb.getModels().isEmpty() || vdbStatus.equals(VDB.Status.FAILED) || 
                       vdbStatus.equals(VDB.Status.REMOVED) || vdbStatus.equals(VDB.Status.ACTIVE)) {
                        refresh();
                        return;
                    }
                    // If the VDB Status is LOADING, but a validity error was found - return
                    if(vdbStatus.equals(VDB.Status.LOADING) && !vdb.getValidityErrors().isEmpty()) {
                        refresh();
                        return;
                    }
                } else {
                    refresh();
                    return;
                }
            } while (System.currentTimeMillis() < waitUntil);
            refresh();
            return;
        }

    }

}

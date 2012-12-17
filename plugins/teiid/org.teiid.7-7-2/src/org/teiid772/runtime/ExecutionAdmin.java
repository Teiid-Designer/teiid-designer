/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.runtime;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminFactory;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.jdbc.TeiidDriver;
import org.teiid.logging.LogManager;



/**
 *
 *
 * @since 8.0
 */
public class ExecutionAdmin implements IExecutionAdmin {

    private static String PLUGIN_ID = "org.teiid.8-2";  //$NON-NLS-1$
    
    /**
     * Test VDB model
     */
    public static final String TEST_VDB = "<vdb name=\"ping\" version=\"1\">" + //$NON-NLS-1$
    "<model visible=\"true\" name=\"Foo\" type=\"VIRTUAL\" path=\"/dummy/Foo\">" + //$NON-NLS-1$
    "<source name=\"s\" translator-name=\"loopback\"/>" + //$NON-NLS-1$
    "</model>" + //$NON-NLS-1$
    "</vdb>"; //$NON-NLS-1$ +
    
    private final Admin admin;
    protected Map<String, ITeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Map<String, ITeiidDataSource> dataSourceByNameMap;
    protected Set<String> dataSourceTypeNames;
    private final EventManager eventManager;
    private final ITeiidServer teiidServer;
    private Map<String, ITeiidVdb> teiidVdbs;
    private final ModelConnectionMatcher connectionMatcher;

    private boolean loaded = false;

    /**
     * Constructor used for testing purposes only. 
     * 
     * @param admin the associated Teiid Admin API (never <code>null</code>)
     * @param teiidServer the server this admin belongs to (never <code>null</code>)
     * @throws Exception if there is a problem connecting the server
     */
    ExecutionAdmin(Admin admin, ITeiidServer teiidServer) throws Exception {
        ArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        ArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        
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
    public ExecutionAdmin(ITeiidServer teiidServer) throws Exception {
        ArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        
        ITeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        char[] passwordArray = null;
        if (teiidAdminInfo.getPassword() != null) {
            passwordArray = teiidAdminInfo.getPassword().toCharArray();
        }
        
        this.admin = AdminFactory.getInstance().createAdmin(teiidAdminInfo.getUsername(), 
                                                                                             passwordArray, 
                                                                                             teiidAdminInfo.getUrl());

        this.teiidServer = teiidServer;
        this.eventManager = teiidServer.getEventManager();
        this.connectionMatcher = new ModelConnectionMatcher();

        init();
    }

    @Override
    public boolean dataSourceExists( String name ) {
        // Check if exists, return false
        if (this.dataSourceNames.contains(name)) {
            return true;
        }

        return false;
    }

    @Override
    public void deleteDataSource( String jndiName ) throws Exception {
        // Check if exists, return false
        if (this.dataSourceNames.contains(jndiName)) {
            this.admin.deleteDataSource(jndiName);

            if (!this.admin.getDataSourceNames().contains(jndiName)) {
                this.dataSourceNames.remove(jndiName);
                ITeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);

                if (tds != null) {
                    this.dataSourceByNameMap.remove(jndiName);
                    this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveDataSourceEvent(tds));

                }
            }
        }
    }

    @Override
    public void deployVdb( IFile vdbFile ) throws Exception {
        ArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        String vdbName = vdbFile.getFullPath().lastSegment();
        String vdbNameNoExt = vdbFile.getFullPath().removeFileExtension().lastSegment();
        
        admin.deployVDB(vdbName, vdbFile.getContents());
              
        // Refresh VDBs list
        refreshVDBs();

        // TODO should get version from vdbFile
        VDB vdb = admin.getVDB(vdbNameNoExt, 1);

        // If the VDB is still loading, refresh again and potentially start refresh job
        if(! vdb.getStatus().equals(VDB.Status.ACTIVE) && vdb.getValidityErrors().isEmpty()) {
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
            if(! vdb.getStatus().equals(VDB.Status.ACTIVE) && vdb.getValidityErrors().isEmpty()) {
                final Job refreshVDBsJob = new RefreshVDBsJob(vdbNameNoExt);
                refreshVDBsJob.schedule();
            }
        }

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createDeployVDBEvent(vdb.getName()));
    }
    
    @Override
    public void disconnect() {
    	// 
    	this.admin.close();
        this.translatorByNameMap = new HashMap<String, ITeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, ITeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        this.teiidVdbs = new HashMap<String, ITeiidVdb>();
    }

    @Override
    public ITeiidDataSource getDataSource(String name) {
        return this.dataSourceByNameMap.get(name);
    }
    
    @Override
	public Collection<ITeiidDataSource> getDataSources() {
        return this.dataSourceByNameMap.values();
    }

    @Override
	public Set<String> getDataSourceTypeNames() {
        return this.dataSourceTypeNames;
    }

    /**
     * @return the event manager (never <code>null</code>)
     */
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public ITeiidDataSource getOrCreateDataSource( String displayName,
                                                  String jndiName,
                                                  String typeName,
                                                  Properties properties ) throws Exception {
        ArgCheck.isNotEmpty(displayName, "displayName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(jndiName, "jndiName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(typeName, "typeName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(properties, "properties"); //$NON-NLS-1$

        // Check if exists, return false
        if (dataSourceExists(jndiName)) {
            ITeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);
            if (tds != null) {
                return tds;
            }
        }

        // Verify the "typeName" exists.
        if (!this.dataSourceTypeNames.contains(typeName)) {
            throw new Exception(NLS.bind(Messages.dataSourceTypeDoesNotExist, typeName, getServer()));
        }

        this.admin.createDataSource(jndiName, typeName, properties);

        refreshDataSourceNames();

        // Check that local name list contains new jndiName
        if (dataSourceExists(jndiName)) {
            String nullStr = null;
            ITeiidDataSource tds = new TeiidDataSource(nullStr, jndiName, typeName, properties);

            this.dataSourceByNameMap.put(jndiName, tds);
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));

            return tds;
        }

        // We shouldn't get here if data source was created
        throw new Exception(NLS.bind(Messages.errorCreatingDataSource, jndiName, typeName));
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
                        LogManager.logError(getClass().getSimpleName(), ex, NLS.bind(Messages.JarDeploymentJarNotFound, theFile.getPath()));
                        continue;
                    }
                    try {
                        admin.deployVDB(fileName, iStream);
                    } catch (Exception ex) {
                        // Jar deployment failed
                        LogManager.logError(getClass().getSimpleName(), ex, NLS.bind(Messages.JarDeploymentFailed, theFile.getPath()));
                    }
                } else {
                    // Could not read the file
                    LogManager.logError(getClass().getSimpleName(), NLS.bind(Messages.JarDeploymentJarNotReadable, theFile.getPath()));
                }
            } else {
                // The file was not found
                LogManager.logError(getClass().getSimpleName(), NLS.bind(Messages.JarDeploymentJarNotFound, theFile.getPath()));
            }

        }
    }
    
    /**
     * @return the server who owns this admin object (never <code>null</code>)
     */
    public ITeiidServer getServer() {
        return this.teiidServer;
    }

    @Override
    public ITeiidTranslator getTranslator( String name ) {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.translatorByNameMap.get(name);
    }

    @Override
    public Collection<ITeiidTranslator> getTranslators() {
        return Collections.unmodifiableCollection(translatorByNameMap.values());
    }

    @Override
    public ITeiidVdb getVdb( String name ) {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$

        return teiidVdbs.get(name);
    }
    
    @Override
    public boolean hasVdb(String name) throws Exception {
        return getVdb(name) != null;
    }
    
    @Override
    public boolean isVdbActive(String vdbName) throws Exception {
        if (! hasVdb(vdbName))
            return false;
        
        return getVdb(vdbName).isActive();
    }
    
    @Override
    public boolean isVdbLoading(String vdbName) throws Exception {
        if (! hasVdb(vdbName))
            return false;
        
        return getVdb(vdbName).isLoading();
    }
    
    @Override
    public boolean hasVdbFailed(String vdbName) throws Exception {
        if (! hasVdb(vdbName))
            return false;
        
        return getVdb(vdbName).hasFailed();
    }
    
    @Override
    public boolean wasVdbRemoved(String vdbName) throws Exception {
        if (! hasVdb(vdbName))
            return false;
        
        return getVdb(vdbName).wasRemoved();
    }
    
    @Override
    public List<String> retrieveVdbValidityErrors(String vdbName) throws Exception {
        if (! hasVdb(vdbName))
            return Collections.emptyList();
        
        return getVdb(vdbName).getValidityErrors();
    }

    @Override
    public Collection<ITeiidVdb> getVdbs() {
        return Collections.unmodifiableCollection(teiidVdbs.values());
    }
    
    private void init() throws Exception {
        this.translatorByNameMap = new HashMap<String, ITeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, ITeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        refreshVDBs();
    }

    private void internalSetPropertyValue( ITeiidTranslator translator,
                                           String propName,
                                           String value,
                                           boolean notify ) throws Exception {
        if (translator.isValidPropertyValue(propName, value) == null) {
            String oldValue = translator.getPropertyValue(propName);

            // don't set if value has not changed
            if (oldValue == null) {
                if (value == null) return;
            } else if (oldValue.equals(value)) return;

            if (notify) {
                // TODO: Will we ever update Translator properties in TEIID Server?
                // this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
            }
        } else {
            throw new Exception(NLS.bind(Messages.invalidPropertyValue, value, propName));
        }
    }

    /**
     * @throws Exception if refreshing admin connection fails
     */
    @Override
    public void connect() throws Exception {
        if (!this.loaded) {
            refresh();
            this.loaded = true;
        }
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
        Collection<ITeiidDataSource> tdsList = connectionMatcher.findTeiidDataSources(this.dataSourceNames);
        for (ITeiidDataSource ds : tdsList) {
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
                this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, teiidServer));
            }
        }
    }

    protected void refreshVDBs() throws Exception {
        Collection<? extends VDB> vdbs = Collections.unmodifiableCollection(this.admin.getVDBs());
        
        teiidVdbs = new HashMap<String, ITeiidVdb>();

        for (VDB vdb : vdbs) {
            teiidVdbs.put(vdb.getName(), new TeiidVdb(vdb, teiidServer));
        }
    }

    /**
     * @param translator the translator whose properties are being changed (never <code>null</code>)
     * @param changedProperties a collection of properties that have changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( ITeiidTranslator translator,
                               Properties changedProperties ) throws Exception {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        ArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(changedProperties.entrySet(), "changedProperties"); //$NON-NLS-1$

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
    public void setPropertyValue( ITeiidTranslator translator,
                                  String propName,
                                  String value ) throws Exception {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(propName, "propName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(value, "value"); //$NON-NLS-1$
        internalSetPropertyValue(translator, propName, value, true);
    }

    @Override
    public void undeployVdb( String vdbName) throws Exception {
        ITeiidVdb vdb = getVdb(vdbName);
        this.admin.deleteVDB(appendVdbExtension(vdbName), vdb.getVersion());
        vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb.getName()));
        }
    }
    
    /**
     * 
     * @param vdbName the vdb name
     * @param vdbVersion the vdb version
     * @throws Exception if undeploying vdb fails
     */
    public void undeployVdb( String vdbName, int vdbVersion ) throws Exception {
        this.admin.deleteVDB(appendVdbExtension(vdbName), vdbVersion);
        ITeiidVdb vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb.getName()));
        }
    }
    
    /**
     * Append the vdb file extension to the vdb name 
     * if not already appended.
     * 
     * @param vdbName
     * @return
     */
    private String appendVdbExtension(String vdbName) {
        if (vdbName.endsWith(ITeiidVdb.VDB_EXTENSION))
            return vdbName;
        
        return vdbName + ITeiidVdb.VDB_DOT_EXTENSION;
    }
    
    @Override
    public IStatus ping(PingType pingType) {
        String msg = NLS.bind(Messages.cannotConnectToServer, teiidServer.getTeiidAdminInfo().getUsername());
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
        ITeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();
        
        Connection teiidJdbcConnection = null;
        String url = "jdbc:teiid:ping@mm://" + host + ':' + teiidJdbcInfo.getPort(); //$NON-NLS-1$
        
        try {
            admin.deployVDB("ping-vdb.xml", (InputStream)new ByteArrayInputStream(TEST_VDB.getBytes())); //$NON-NLS-1$
            try{
                String urlAndCredentials = url + ";user=" + teiidJdbcInfo.getUsername() + ";password=" + teiidJdbcInfo.getPassword() + ';';  //$NON-NLS-1$ //$NON-NLS-2$              
                teiidJdbcConnection = TeiidDriver.getInstance().connect(urlAndCredentials, null);
               //pass
            } catch(SQLException ex){
                String msg = NLS.bind(Messages.serverDeployUndeployProblemPingingTeiidJdbc, url);
                return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
            } finally {
                admin.deleteVDB("ping", 1); //$NON-NLS-1$
                
                if( teiidJdbcConnection != null ) {
                    teiidJdbcConnection.close();
                }
            }
        } catch (Exception ex) {
            String msg = NLS.bind(Messages.serverDeployUndeployProblemPingingTeiidJdbc, url);
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
        }
        
        return Status.OK_STATUS;
    }
    
    @Override
    public String getAdminDriverPath() {
        return Admin.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    }
    
    @Override
    public Driver getTeiidDriver(String driverClass) throws Exception {
        Class<?> klazz = getClass().getClassLoader().loadClass(driverClass);
        Object driver = klazz.newInstance();
        if (driver instanceof Driver)
            return (Driver) driver;
        
        throw new Exception(NLS.bind(Messages.cannotLoadDriverClass, driverClass));
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
                ITeiidVdb vdb = getVdb(vdbName);
                // Determine if VDB is loading, or whether to wait
                if(vdb!=null) {
                    // return if no models in VDB, or VDB has errors (done loading)
                    if(vdb.hasModels() || vdb.hasFailed() || 
                       vdb.wasRemoved() || vdb.isActive()) {
                        refresh();
                        return;
                    }
                    // If the VDB Status is LOADING, but a validity error was found - return
                    if(vdb.isLoading() && !vdb.getValidityErrors().isEmpty()) {
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

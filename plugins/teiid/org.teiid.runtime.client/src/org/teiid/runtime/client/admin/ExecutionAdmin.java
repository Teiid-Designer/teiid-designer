/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin;

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
import org.eclipse.wst.server.core.IServer;
import org.jboss.dmr.ModelNode;
import org.jboss.ide.eclipse.as.core.server.v7.management.AS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.IAS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerUtil;
import org.jboss.ide.eclipse.as.management.core.ModelDescriptionConstants;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.ITeiidConnectionInfo;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidTranslator.TranslatorPropertyType;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.spi.TeiidExecutionException;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.jdbc.TeiidDriver;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidRuntimePlugin;



/**
 *
 *
 * @since 8.0
 */
public class ExecutionAdmin implements IExecutionAdmin {

    private static String PLUGIN_ID = "org.teiid.runtime.client";  //$NON-NLS-1$
    private static String DYNAMIC_VDB_SUFFIX = "-vdb.xml"; //$NON-NLS-1$
    private static int VDB_LOADING_TIMEOUT_SEC = 300;

    private final Admin admin;
    protected Map<String, ITeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Map<String, ITeiidDataSource> dataSourceByNameMap;
    protected Set<String> dataSourceTypeNames;
    private final EventManager eventManager;
    private final ITeiidServer teiidServer;
    private final AdminSpec adminSpec;
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
        this.adminSpec = AdminSpec.getInstance(teiidServer.getServerVersion());
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

        this.adminSpec = AdminSpec.getInstance(teiidServer.getServerVersion());

        this.admin = adminSpec.createAdmin(teiidServer);
        ArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.teiidServer = teiidServer;
        this.eventManager = teiidServer.getEventManager();
        this.connectionMatcher = new ModelConnectionMatcher();

        init();
    }

    private boolean isLessThanTeiidEight() {
        ITeiidServerVersion minVersion = teiidServer.getServerVersion().getMinimumVersion();
        return minVersion.isLessThan(Version.TEIID_8_0.get());
    }
    
    private boolean isLessThanTeiidEightSeven() {
        ITeiidServerVersion minVersion = teiidServer.getServerVersion().getMinimumVersion();
        return minVersion.isLessThan(Version.TEIID_8_7.get());
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
    public void deleteDataSource( String dsName ) throws Exception {
        // Check if exists, return false
        if (this.dataSourceNames.contains(dsName)) {
            this.admin.deleteDataSource(dsName, this.dataSourceNames);

            ITeiidDataSource tds = this.dataSourceByNameMap.get(dsName);
            
            refreshDataSources();

            if (tds != null) {
                this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveDataSourceEvent(tds));
            }
        }
    }

    @Override
    public void deployVdb( IFile vdbFile ) throws Exception {
        ArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        String vdbDeploymentName = vdbFile.getFullPath().lastSegment();
        String vdbName = vdbFile.getFullPath().removeFileExtension().lastSegment();
    
        // For Teiid Version less than 8.7, do explicit undeploy (TEIID-2873)
    	if(isLessThanTeiidEightSeven()) {
    		undeployVdb(vdbName);
    	}
        
        doDeployVdb(vdbDeploymentName, getVdbName(vdbName), 1, vdbFile.getContents());
    }
    
	@Override
	public void deployVdb(IFile vdbFile, int version) throws Exception {
		ArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$
		String vdbDeploymentName = vdbFile.getFullPath().lastSegment();
		String vdbName = vdbFile.getFullPath().removeFileExtension()
				.lastSegment();
		// For Teiid Version less than 8.7, do explicit undeploy (TEIID-2873)
		if (isLessThanTeiidEightSeven()) {
			undeployVdb(vdbName);
		}
		int vdbVersion = 1;
		if (version > 1) {
			vdbVersion = version;
		}
		doDeployVdb(vdbDeploymentName, getVdbName(vdbName), vdbVersion,
				vdbFile.getContents());
	}
    
    @Override
    public void deployDynamicVdb( String deploymentName, InputStream inStream ) throws Exception {
        ArgCheck.isNotNull(deploymentName, "deploymentName"); //$NON-NLS-1$
        ArgCheck.isNotNull(inStream, "inStream"); //$NON-NLS-1$

        // Check dynamic VDB deployment name
        if(!deploymentName.endsWith(DYNAMIC_VDB_SUFFIX)) {
            throw new Exception(Messages.getString(Messages.ExecutionAdmin.dynamicVdbInvalidName, deploymentName));
        }
        
        // Get VDB name
        String vdbName = deploymentName.substring(0, deploymentName.indexOf(DYNAMIC_VDB_SUFFIX));

        // For Teiid Version less than 8.7, do explicit undeploy (TEIID-2873)
    	if(isLessThanTeiidEightSeven()) {
    		undeployDynamicVdb(vdbName);
    	}
    	
        // Deploy the VDB
        // TODO: Dont assume vdbVersion
        doDeployVdb(deploymentName,vdbName,1,inStream);
    }
    
    private void doDeployVdb(String deploymentName, String vdbName, int vdbVersion, InputStream inStream) throws Exception {
        adminSpec.deploy(admin, deploymentName, inStream);
        // Give a 0.5 sec pause for the VDB to finish loading metadata.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }   
                      
        // Refresh VDBs list
        refreshVDBs();

        // TODO should get version from vdbFile
        VDB vdb = admin.getVDB(vdbName, vdbVersion);

        // If the VDB is still loading, refresh again and potentially start refresh job
        if(vdb.getStatus().equals(adminSpec.getLoadingVDBStatus()) && vdb.getValidityErrors().isEmpty()) {
            // Give a 0.5 sec pause for the VDB to finish loading metadata.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }   
            // Refresh again to update vdb states
            refreshVDBs();
            vdb = admin.getVDB(vdbName, vdbVersion);
            // Determine if still loading, if so start refresh job.  User will get dialog that the
            // vdb is still loading - and try again in a few seconds
            if(vdb.getStatus().equals(adminSpec.getLoadingVDBStatus()) && vdb.getValidityErrors().isEmpty()) {
                final Job refreshVDBsJob = new RefreshVDBsJob(vdbName);
                refreshVDBsJob.schedule();
            }
        }

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createDeployVDBEvent(vdb.getName()));
    }
    

	private String getVdbName(String originalVdbName) throws Exception {
		String vdbName = originalVdbName;
		int firstIndex = vdbName.indexOf('.');
		int lastIndex = vdbName.lastIndexOf('.');
		if (firstIndex != -1) {
			if (firstIndex != lastIndex) {
				throw new Exception(Messages.getString(Messages.ExecutionAdmin.invalidVdbName, originalVdbName));
			}
			vdbName = vdbName.substring(0, firstIndex);
		}
		return vdbName;
	}
    
    @Override
    public String getSchema(String vdbName, int vdbVersion, String modelName) throws Exception {
        if (isLessThanTeiidEight()) {
            // Limited schema support in 77x, just return empty string here
            return ""; //$NON-NLS-1$
        }

        return admin.getSchema(vdbName, vdbVersion, modelName, null, null);
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
                                                  String dsName,
                                                  String typeName,
                                                  Properties properties ) throws Exception {
        ArgCheck.isNotEmpty(displayName, "displayName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(dsName, "dsName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(typeName, "typeName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(properties, "properties"); //$NON-NLS-1$

        // Check if exists, return false
        if (dataSourceExists(dsName)) {
            ITeiidDataSource tds = this.dataSourceByNameMap.get(dsName);
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
                throw new TeiidExecutionException(
                		ITeiidDataSource.ERROR_CODES.JDBC_DRIVER_SOURCE_NOT_FOUND,
                		Messages.getString(Messages.ExecutionAdmin.jdbcSourceForClassNameNotFound, connProfileDriverClass, getServer()));
            } else {
                throw new TeiidExecutionException(
                		ITeiidDataSource.ERROR_CODES.DATA_SOURCE_TYPE_DOES_NOT_EXIST_ON_SERVER,
                		Messages.getString(Messages.ExecutionAdmin.dataSourceTypeDoesNotExist, typeName, getServer()));
            }
        }

        ITeiidTranslator teiidTranslator = getTranslator(typeName);
        this.admin.createDataSource(dsName, typeName, this.dataSourceNames, properties);

        refreshDataSources();

        // Check that local name list contains new dsName
        ITeiidDataSource tds = this.dataSourceByNameMap.get(dsName);
        if( tds != null ) {
        	this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));
        	return tds;
        } 

        // We shouldn't get here if data source was created
        throw new TeiidExecutionException(
        		ITeiidDataSource.ERROR_CODES.DATA_SOURCE_COULD_NOT_BE_CREATED,
        		Messages.getString(Messages.ExecutionAdmin.errorCreatingDataSource, dsName, typeName));
    }

    /**
     * Look for an installed driver that has the driverClass which matches the supplied driverClass name.
     * 
     * @param requestDriverClass the driver class to match
     * @return the name of the matching driver, null if not found
     */
    private String getDSMatchForDriverClass(String requestDriverClass) throws Exception {
        if (requestDriverClass == null)
            return null;

        if (!getServer().isParentConnected())
            return null;

        ModelNode request = new ModelNode();
        request.get(ModelDescriptionConstants.OP).set("installed-drivers-list"); //$NON-NLS-1$

        ModelNode address = new ModelNode();
        address.add(ModelDescriptionConstants.SUBSYSTEM, "datasources"); //$NON-NLS-1$
        request.get(ModelDescriptionConstants.OP_ADDR).set(address);

        try {
            String requestString = request.toJSONString(true);
            IServer parentServer = getServer().getParent();

            //
            // Add the timeout to a properties map
            //
            int timeout = teiidServer.getParentRequestTimeout();
            Map<String, Object> props = new HashMap<String, Object>();
            props.put(IAS7ManagementDetails.PROPERTY_TIMEOUT, timeout);

            AS7ManagementDetails as7ManagementDetails = new AS7ManagementDetails(parentServer, props);
            String resultString = JBoss7ManagerUtil.getService(parentServer).execute(as7ManagementDetails, requestString);
            ModelNode operationResult = ModelNode.fromJSONString(resultString);

            List<ModelNode> driverList = operationResult.asList();
            for (ModelNode driver : driverList) {
                String driverClassName = driver.get("driver-class-name").asString(); //$NON-NLS-1$
                String driverName = driver.get("driver-name").asString(); //$NON-NLS-1$

                if (requestDriverClass.equalsIgnoreCase(driverClassName)) return driverName;
            }

        } catch (Exception ex) {
            // Failed to get mapping
            TeiidRuntimePlugin.logError(getClass().getSimpleName(), ex, Messages.getString(Messages.ExecutionAdmin.failedToGetDriverMappings, requestDriverClass));
        }

        return null;
    }
    
    /*
     * Deploy all jars in the supplied jarList
     * @param admin the Admin instance
     * @param jarList the colon-separated list of jar path locations
     */
    private void deployJars(Admin admin, String jarList) {
        // Path Entries are separated by the file system path separator (WINDOWS = ';', LINUX = ':')
    	String splitter = "[" + File.pathSeparatorChar + "]"; //$NON-NLS-1$ //$NON-NLS-2$

        String[] jarPathStrs = jarList.split(splitter); 

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
                        TeiidRuntimePlugin.logError(getClass().getSimpleName(), ex, Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotFound, theFile.getPath()));
                        continue;
                    }
                    try {
                        adminSpec.deploy(admin, fileName, iStream);
                    } catch (Exception ex) {
                        // Jar deployment failed
                        TeiidRuntimePlugin.logError(getClass().getSimpleName(), ex, Messages.getString(Messages.ExecutionAdmin.jarDeploymentFailed, theFile.getPath()));
                    }
                } else {
                    // Could not read the file
                    TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotReadable, theFile.getPath()));
                }
            } else {
                // The file was not found
                TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotFound, theFile.getPath()));
            }

        }
    }
    
    @Override
    public void deployDriver(File jarOrRarFile) throws Exception {
        if(jarOrRarFile.exists()) {
            if(jarOrRarFile.canRead()) {
                String fileName = jarOrRarFile.getName();
                InputStream iStream = null;
                try {
                    iStream = new FileInputStream(jarOrRarFile);
                } catch (FileNotFoundException ex) {
                    TeiidRuntimePlugin.logError(getClass().getSimpleName(), ex, Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotFound, jarOrRarFile.getPath()));
                    throw ex;
                }
                try {
                    adminSpec.deploy(admin, fileName, iStream);
                    refreshDataSourceTypes();
                } catch (Exception ex) {
                    // Jar deployment failed
                    TeiidRuntimePlugin.logError(getClass().getSimpleName(), ex, Messages.getString(Messages.ExecutionAdmin.jarDeploymentFailed, jarOrRarFile.getPath()));
                    throw ex;
                }
            } else {
                // Could not read the file
                TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotReadable, jarOrRarFile.getPath()));
            }
        } else {
            // The file was not found
            TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.jarDeploymentJarNotFound, jarOrRarFile.getPath()));
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
    public Set<String> getDataSourceTemplateNames() throws Exception {
        return this.admin.getDataSourceTemplateNames();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
        Collection<? extends PropertyDefinition> propDefs = this.admin.getTemplatePropertyDefinitions(templateName);

        Collection<TeiidPropertyDefinition> teiidPropDefns = new ArrayList<TeiidPropertyDefinition>();
        
        for (PropertyDefinition propDefn : propDefs) {
            TeiidPropertyDefinition teiidPropertyDefn = new TeiidPropertyDefinition();
            
            teiidPropertyDefn.setName(propDefn.getName());
            teiidPropertyDefn.setDisplayName(propDefn.getDisplayName());
            teiidPropertyDefn.setDescription(propDefn.getDescription());
            teiidPropertyDefn.setPropertyTypeClassName(propDefn.getPropertyTypeClassName());
            teiidPropertyDefn.setDefaultValue(propDefn.getDefaultValue());
            teiidPropertyDefn.setAllowedValues(propDefn.getAllowedValues());
            teiidPropertyDefn.setModifiable(propDefn.isModifiable());
            teiidPropertyDefn.setConstrainedToAllowedValues(propDefn.isConstrainedToAllowedValues());
            teiidPropertyDefn.setAdvanced(propDefn.isAdvanced());
            teiidPropertyDefn.setRequired(propDefn.isRequired());
            teiidPropertyDefn.setMasked(propDefn.isMasked());
            
            teiidPropDefns.add(teiidPropertyDefn);
        }
        
        return teiidPropDefns;
    }

    /*
     * (non-Javadoc)
     * @see org.teiid.designer.runtime.spi.IExecutionAdmin#getDataSourceProperties(java.lang.String)
     */
    @Override
    public Properties getDataSourceProperties(String name) throws Exception {
        if (isLessThanTeiidEight()) {
            // Teiid 7.7.x does not support
            return null;
        }

        return getDataSource(name).getProperties();
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
                                           TranslatorPropertyType type,
                                           boolean notify ) throws Exception {
        if (translator.isValidPropertyValue(propName, value, type) == null) {
            String oldValue = translator.getPropertyValue(propName, type);

            // don't set if value has not changed
            if (oldValue == null) {
                if (value == null) return;
            } else if (oldValue.equals(value)) return;

            if (notify) {
                // TODO: Will we ever update Translator properties in TEIID Server?
                // this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
            }
        } else {
            throw new Exception(Messages.getString(Messages.ExecutionAdmin.invalidPropertyValue, value, propName));
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

        // populate data source type names set
        refreshDataSourceTypes();

        refreshDataSources();
        
        // populate translator map
        refreshTranslators();

        // populate VDBs and source bindings
        refreshVDBs();

        // notify listeners
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this.teiidServer));
    }
    
    protected void refreshDataSources() throws Exception {
        this.dataSourceByNameMap.clear();
        this.dataSourceNames.clear();
        
        Map<String, ITeiidDataSource> actualDataSources = this.admin.getDataSources(connectionMatcher);
        
        for( String dsName : actualDataSources.keySet() ) {
        	ITeiidDataSource ds = actualDataSources.get(dsName);
        	
        	this.dataSourceByNameMap.put(dsName, ds);
        	this.dataSourceNames.add(dsName);
        }
    }

    /**
     * Refreshes the local collection of Translators on the referenced Teiid server.
     * 
     * @param translators
     * @throws Exception
     */
    protected void refreshTranslators() throws Exception {
    	Collection<? extends Translator> translators = this.admin.getTranslators();
        for (Translator translator : translators ) {
            if (translator.getName() != null) {
                if( teiidServer.getServerVersion().isLessThan(Version.TEIID_8_6.get())) {
                	Collection<? extends PropertyDefinition> propDefs = this.admin.getTemplatePropertyDefinitions(translator.getName());
                	this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, teiidServer));
                } else if( teiidServer.getServerVersion().isLessThan(Version.TEIID_8_7.get())) {
                	@SuppressWarnings("deprecation")
					Collection<? extends PropertyDefinition> propDefs = this.admin.getTranslatorPropertyDefinitions(translator.getName());
                	this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, teiidServer));
                } else { // TEIID SERVER VERSION 8.7 AND HIGHER
                	Collection<? extends PropertyDefinition> propDefs  = 
                			this.admin.getTranslatorPropertyDefinitions(translator.getName(), Admin.TranlatorPropertyType.OVERRIDE, translators);
                	Collection<? extends PropertyDefinition> importPropDefs  = 
                			this.admin.getTranslatorPropertyDefinitions(translator.getName(), Admin.TranlatorPropertyType.IMPORT, translators);
                	Collection<? extends PropertyDefinition> extPropDefs  = 
                			this.admin.getTranslatorPropertyDefinitions(translator.getName(), Admin.TranlatorPropertyType.EXTENSION_METADATA, translators);
                	this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, importPropDefs, extPropDefs, teiidServer));
                }
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
    
    protected void refreshDataSourceTypes() throws Exception {
        // populate data source type names set
        this.dataSourceTypeNames = new HashSet<String>(this.admin.getDataSourceTemplateNames());
    }

    /**
     * @param translator the translator whose properties are being changed (never <code>null</code>)
     * @param changedProperties a collection of properties that have changed (never <code>null</code> or empty)
     * @param type the translator property type
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( ITeiidTranslator translator,
                               Properties changedProperties,
                               TranslatorPropertyType type) throws Exception {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        ArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(changedProperties.entrySet(), "changedProperties"); //$NON-NLS-1$

        if (changedProperties.size() == 1) {
            String name = changedProperties.stringPropertyNames().iterator().next();
            setPropertyValue(translator, name, changedProperties.getProperty(name), type);
        } else {

            for (String name : changedProperties.stringPropertyNames()) {
                internalSetPropertyValue(translator, name, changedProperties.getProperty(name), type, false);
            }
            // this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
        }
    }

    /**
     * @param translator the translator whose property is being changed (never <code>null</code>)
     * @param propName the name of the property being changed (never <code>null</code> or empty)
     * @param value the new value
     * @param type the translator property type
     * @throws Exception if there is a problem setting the property
     * @since 7.0
     */
    public void setPropertyValue( ITeiidTranslator translator,
                                  String propName,
                                  String value,
                                  TranslatorPropertyType type) throws Exception {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(propName, "propName"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(value, "value"); //$NON-NLS-1$
        internalSetPropertyValue(translator, propName, value, type, true);
    }

    @Override
    public void undeployVdb( String vdbName) throws Exception {
        ITeiidVdb vdb = getVdb(vdbName);
        if(vdb!=null) {
        	String deploymentName = vdb.getPropertyValue("deployment-name"); //$NON-NLS-1$
        	if( deploymentName != null ) {
        		adminSpec.undeploy(admin, deploymentName, vdb.getVersion());
        	} else {
        		throw new Exception(Messages.getString(Messages.ExecutionAdmin.cannotUndeployVdbNoDeploymentName, vdbName));
            }
        }
        vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb != null) {
        	this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb.getName()));
        }
    }
    
    @Override
    public void undeployDynamicVdb( String vdbName) throws Exception {
        ITeiidVdb vdb = getVdb(vdbName);
        if(vdb!=null) {
        	adminSpec.undeploy(admin, appendDynamicVdbSuffix(vdbName), vdb.getVersion());
        }
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
        adminSpec.undeploy(admin, appendVdbExtension(vdbName), vdbVersion);
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
    
    /**
     * Append the suffix for dynamic VDB to the vdb name if not already appended.
     * 
     * @param vdbName
     * @return
     */
    private String appendDynamicVdbSuffix(String vdbName) {
        if (vdbName.endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX))
            return vdbName;
        
        return vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX;
    }
    
    @Override
    public IStatus ping(PingType pingType) {
        String msg = Messages.getString(Messages.ExecutionAdmin.cannotConnectToServer, teiidServer.getTeiidAdminInfo().getUsername());
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
        
        String protocol = ITeiidConnectionInfo.MM;
        if (teiidJdbcInfo.isSecure())
            protocol = ITeiidConnectionInfo.MMS;

        Connection teiidJdbcConnection = null;
        String url = "jdbc:teiid:ping@" + protocol + host + ':' + teiidJdbcInfo.getPort(); //$NON-NLS-1$
        
        try {
            adminSpec.deploy(admin, PING_VDB, new ByteArrayInputStream(adminSpec.getTestVDB().getBytes()));
            
            try{
                String urlAndCredentials = url + ";";  //$NON-NLS-1$             
                TeiidDriver teiidDriver = TeiidDriver.getInstance();
                teiidDriver.setTeiidVersion(teiidServer.getServerVersion());
                Properties props = new Properties();
                props.put("user", teiidJdbcInfo.getUsername());
                props.put("password", teiidJdbcInfo.getPassword());
                teiidJdbcConnection = teiidDriver.connect(urlAndCredentials, props);
               //pass
            } catch(SQLException ex){
                String msg = Messages.getString(Messages.ExecutionAdmin.serverDeployUndeployProblemPingingTeiidJdbc, url);
                return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
            } finally {
                adminSpec.undeploy(admin, PING_VDB, 1);
                
                if( teiidJdbcConnection != null ) {
                    teiidJdbcConnection.close();
                }
            }
        } catch (Exception ex) {
            String msg = Messages.getString(Messages.ExecutionAdmin.serverDeployUndeployProblemPingingTeiidJdbc, url);
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
        
        throw new Exception(Messages.getString(Messages.ExecutionAdmin.cannotLoadDriverClass, driverClass));
    }

    @Override
    @Deprecated
    @Removed(Version.TEIID_8_0)
    public void mergeVdbs( String sourceVdbName, int sourceVdbVersion, 
                                            String targetVdbName, int targetVdbVersion ) throws Exception {
        if (!AnnotationUtils.isApplicable(getClass().getMethod("mergeVdbs", String.class, int.class, String.class, int.class), getServer().getServerVersion()))  //$NON-NLS-1$
            throw new UnsupportedOperationException(Messages.getString(Messages.ExecutionAdmin.mergeVdbUnsupported));

        admin.mergeVDBs(sourceVdbName, sourceVdbVersion, targetVdbName, targetVdbVersion);        
    }

    /**
     * Executes VDB refresh when a VDB is loading - as a background job.
     */
    class RefreshVDBsJob extends Job {

        String vdbName;
        
        /**
         * RefreshVDBsJob constructor
         * @param vdbName the name of the VDB to monitor
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
            long waitUntil = System.currentTimeMillis() + VDB_LOADING_TIMEOUT_SEC*1000;
            boolean first = true;
            do {
                // Pauses 5 sec
                if (!first) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    first = false;
                }
                
                // Refreshes from adminApi
                refreshVDBs();
                
                // Get the VDB
                ITeiidVdb vdb = getVdb(vdbName);
                
                // Stop waiting if any conditions have been met
                if(vdb!=null) {
                	boolean hasValidityErrors = !vdb.getValidityErrors().isEmpty();
                	if(  !vdb.hasModels() || vdb.hasFailed()  || !vdb.isLoading() 
                	   || vdb.isActive()  || vdb.wasRemoved() || hasValidityErrors ) {
                		return;
                	} 
                } else {
                    return;
                }
            } while (System.currentTimeMillis() < waitUntil);
            
            refreshVDBs();
            return;
        }

    }
    
}
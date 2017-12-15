/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.common.xml.XmlUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent.EventType;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent.TargetType;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public final class ImportManager implements IExecutionConfigurationListener {

    private static final String DYNAMIC_VDB_SUFFIX = "-vdb.xml";  //$NON-NLS-1$
    public static final String IMPORT_SRC_MODEL = "SrcModel";  //$NON-NLS-1$
    private static final String JNDI_PROPERTY_KEY = "jndi-name";  //$NON-NLS-1$
    private static final String CR = StringConstants.CARRIAGE_RETURN;
    
    /**
     * The Teiid Instance being used for importers (may be <code>null</code>).
     */
    private volatile AtomicReference<ITeiidServer> importServer = new AtomicReference<ITeiidServer>();

	private static ImportManager instance;

	/**
	 * @return singleton instance
	 */
	public static ImportManager getInstance() {
	    if (instance == null) {
	        instance = new ImportManager();
	    }

	    return instance;
	}

	private ImportManager() {}

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.spi.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.spi.ExecutionConfigurationEvent)
     */
    @Override
    public void configurationChanged( ExecutionConfigurationEvent event ) {
        if (event.getEventType().equals(EventType.DEFAULT) && event.getTargetType().equals(TargetType.SERVER)) {
            setImportServer(event.getUpdatedServer());
        }
    }

    private void setImportServer( ITeiidServer teiidServer ) {
        // set new server
        this.importServer.set(teiidServer);
    }
    
    private ITeiidServer getImportServer() {
        return this.importServer.get();
    }
    
    
    /**
     * Determine if a valid server is available for dynamic vdb import.  The server must be
     * running, and it must be version 8.x or higher.
     * @return 'true' if the server is valid
     */
    public boolean isValidImportServer() {
        ITeiidServer importServer = getImportServer();
        // If no server, or not connected - invalid
        if(importServer==null || !importServer.isConnected()) {
            return false;
        }
        
        // If this is a Teiid 7 server, we cant do this type of import
        ITeiidServerVersion version = importServer.getServerVersion();
        if(version.isSevenServer()) {
            return false;
        }
        
        return true;
    }
    
    public boolean vdbExists(String vdbName) {
    	try {
			return getImportServer().getVdb(vdbName) != null;
		} catch (Exception e) {
			return false;
		}
    }
    
    /**
     * Get the dataSource with the specified Name.  The supplied name is the 'pool-name' property specified on the datasource. 
     * @param sourceName datasource name
     * @return the DataSource
     * @throws Exception the exception
     */
    private ITeiidDataSource getDataSource( String sourceName ) throws Exception {
        return getImportServer().getDataSource(sourceName);
    }

    /**
     * @param vdbName name to use for the VDB
     * @param sourceName the dataSource to use for the import
     * @param translatorName the name of the translator
     * @param modelPropertyMap the Map of optional model properties
     * @param monitor the progress monitor
     * @return status of the deployment
     */
    public IStatus deployDynamicVdb(String vdbName, String sourceName, String translatorName, Map<String,String> modelPropertyMap, IProgressMonitor monitor) {
    	// Work remaining for progress monitor
    	int workRemaining = 100;
    	
        IStatus resultStatus = Status.OK_STATUS;
        
        // Deployment name for vdb must end in '-vdb.xml'
        String deploymentName = vdbName+DYNAMIC_VDB_SUFFIX; 

        // Deploy the desired source
        String importSourceModel = vdbName+IMPORT_SRC_MODEL; 
        
        // Get the DataSource
        ITeiidDataSource dataSource;
        try {
            dataSource = getDataSource(sourceName);
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerGetDatasourceError, sourceName));
            return resultStatus;
        }
        
        // data source could be null;
        if( dataSource == null ) {
        	return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerGetDatasourceError, sourceName));
        }
        
        monitor.worked(10);
        workRemaining -= 10;
        
        String sourceJndiName = dataSource.getPropertyValue(JNDI_PROPERTY_KEY);
        if(CoreStringUtil.isEmpty(sourceJndiName)) {
        	sourceJndiName=sourceName;
        }
        // Get Dynamic VDB string
        String dynamicVdbString = createDynamicVdb(vdbName,1,translatorName,importSourceModel,sourceJndiName,modelPropertyMap);

        // If an import VDB with the supplied name exists, undeploy it first
        ITeiidVdb deployedImportVdb;
        try {
            deployedImportVdb = getImportServer().getVdb(vdbName);
            if( deployedImportVdb != null ) {
                getImportServer().undeployDynamicVdb(deployedImportVdb.getName());
            }
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerUndeployVdbError, vdbName));
            return resultStatus;
        }
        monitor.worked(10);
        workRemaining -= 10;
        
        // Deploy the Dynamic VDB
        try {
            getImportServer().deployDynamicVdb(deploymentName,new ByteArrayInputStream(dynamicVdbString.getBytes("UTF-8"))); //$NON-NLS-1$
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerDeployVdbError, vdbName));
            return resultStatus;
        }
        monitor.worked(10);
        workRemaining -= 10;

        // Wait until vdb is done loading, up to timeout sec
        int timeoutSec = DqpPlugin.getInstance().getPreferences().getInt(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);

        boolean finishedLoading = false;
        try {
            finishedLoading = waitForVDBLoad(vdbName,timeoutSec,monitor,workRemaining);
        } catch (InterruptedException ie) {
            resultStatus = new Status(IStatus.CANCEL, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbLoadingInterruptedError, vdbName));
            return resultStatus;
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbLoadingError, vdbName));
            return resultStatus;
        }
        
        // If the VDB finished loading, check Active state
        if(finishedLoading) {
            boolean isVDBActive;
            try {
                isVDBActive = getImportServer().isVdbActive(vdbName);
            } catch (Exception ex) {
                resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbGetStateError, vdbName));
                return resultStatus;
            }
            // VDB Active = success
            if(isVDBActive) {
                resultStatus = Status.OK_STATUS;
            } else {
            	StringBuilder sb = new StringBuilder();
            	
                try {
                    deployedImportVdb = getImportServer().getVdb(vdbName);
                    if( deployedImportVdb != null ) {
                    	sb.append(Messages.ImportManagerVdbDeploymentErrors + CR + CR);
    	        		
    	        		for( String error : deployedImportVdb.getValidityErrors()) {
    	        			sb.append(error).append(CR);
    	        		}
                    }
                } catch (Exception ex) {
                }
                sb.append(CR).append(NLS.bind(Messages.ImportManagerVdbInactiveStateError, vdbName));
                
                resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, sb.toString());
            }
        } else {
        	StringBuilder sb = new StringBuilder();
        	sb.append(NLS.bind(Messages.ImportManagerVdbLoadingNotCompleteError, timeoutSec));
            try {
                deployedImportVdb = getImportServer().getVdb(vdbName);
                if( deployedImportVdb != null ) {
	        		sb.append(Messages.ImportManagerVdbDeploymentErrors + CR + CR);
	        		
	        		for( String error : deployedImportVdb.getValidityErrors()) {
	        			sb.append(error).append(CR);
	        		}
                }
            } catch (Exception ex) {
            }
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, sb.toString());
        }

        return resultStatus;
    }
    
    public String createDynamicVdbString(String vdbName, String sourceName, String translatorName, Map<String,String> modelPropertyMap) {
        // Deploy the desired source
        String importSourceModel = vdbName+IMPORT_SRC_MODEL; 
        
        // Get the DataSource
        ITeiidDataSource dataSource;
        try {
            dataSource = getDataSource(sourceName);
        } catch (Exception ex) {
            return Messages.ImportManagerDynamicVdbTextCannotBeGeneratedError;
        }
        
        String sourceJndiName = dataSource.getPropertyValue(JNDI_PROPERTY_KEY);
        if(CoreStringUtil.isEmpty(sourceJndiName)) {
        	sourceJndiName=sourceName;
        }
        // Get Dynamic VDB string
        return createDynamicVdb(vdbName,1,translatorName,importSourceModel,sourceJndiName,modelPropertyMap);
    }

    /**
     * Get the Schema DDL for the import model in the supplied VDB
     * @param vdbName the name of the VDB
     * @return the Schema DDL for the import VDB Src Model
     * @throws Exception the exception
     */
    public String getSchema(String vdbName) throws Exception {
        return getImportServer().getSchema(vdbName, "1", vdbName+IMPORT_SRC_MODEL);
    }
    
    /**
     * Undeploy the importer vdb (and datasource)
     * @param importerVdbName the vdb name
     * @return status of the operations
     */
    public IStatus undeployVdb(String importerVdbName) {
        IStatus resultStatus = null;
        
        // If an import VDB with the supplied name exists, undeploy it
        ITeiidVdb deployedImportVdb;
        try {
            deployedImportVdb = getImportServer().getVdb(importerVdbName);
            if( deployedImportVdb != null ) {
                getImportServer().undeployDynamicVdb(deployedImportVdb.getName());
            }
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerUndeployVdbError, importerVdbName));
        }
        
        return resultStatus;
    }

    /*
     * Get the deploymentName for the supplied VDB
     * @param deployedVdb the vdb
     * @return the vdb deployment name
     */
    private String getVdbDeploymentName(ITeiidVdb deployedVdb) { 
        String fullVdbName = deployedVdb.getPropertyValue("deployment-name"); //$NON-NLS-1$
        return fullVdbName;
    }
        
    /*
     * Create a new, blank deployment for the provided vdbName and version
     * @param vdbName name of the VDB
     * @param vdbVersion the VDB version
     * @param translatorName the translator
     * @param datasourceName the dataSource name
     * @param datasourceJndeName the dataSource jndi name
     * @param modelProps the model properties
     * @return the VDB deployment string
     */
    public String createDynamicVdb(String vdbName, int vdbVersion,  String translatorName, String datasourceName, String datasourceJndiName, 
    		                        Map<String,String> modelProps) {
        StringBuffer sb = new StringBuffer();
        String deploymentName = vdbName+DYNAMIC_VDB_SUFFIX;
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"); //$NON-NLS-1$
        sb.append("\n<vdb name=\""+vdbName+"\" version=\""+vdbVersion+"\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("\n\t<description>Importer VDB</description>"); //$NON-NLS-1$
        sb.append("\n\t<property name=\"UseConnectorMetadata\" value=\"true\" />"); //$NON-NLS-1$
        sb.append("\n\t<property name=\"deployment-name\" value=\""+deploymentName+"\" />"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("\n\t<model name=\""+datasourceName+"\" type=\"PHYSICAL\" visible=\"true\">"); //$NON-NLS-1$ //$NON-NLS-2$
        Set<String> propNames = modelProps.keySet();
        for(String propName : propNames) {
        	String propValue = XmlUtil.escapeCharacterData(modelProps.get(propName));
            sb.append("\n\t\t<property name=\""+propName+"\" value=\""+propValue+"\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        sb.append("\n\t\t<source name=\""+datasourceName+"\" translator-name=\""+translatorName+"\" connection-jndi-name=\""+datasourceJndiName+"\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("\n\t</model>"); //$NON-NLS-1$
        sb.append("\n</vdb>"); //$NON-NLS-1$
        return sb.toString();
    }
    
    /*
     * Helper method - waits for the VDB to finish loading
     * @param vdbName the name of the VDB
     * @param timeoutInSecs time to wait before timeout
     * @param monitor the progress monitor
     * @param workRemaining the number of work units remaining
     * @return 'true' if vdb found and is not 'Loading', 'false' otherwise.
     */
    private boolean waitForVDBLoad(String vdbName, int timeoutInSecs, IProgressMonitor monitor, int workRemaining) throws Exception {
    	final int sleepDurationSec = 5;
    	int increments = timeoutInSecs / sleepDurationSec;
    	int workIncrement = Math.round((float)workRemaining / increments);
    	
        long waitUntil = System.currentTimeMillis() + timeoutInSecs*1000;
        
        // Timeout of zero or less means no timeout...
        if (timeoutInSecs < 1) {
            waitUntil = Long.MAX_VALUE;
        }
        
        boolean first = true;
        do {
            // Pause 5 sec before subsequent attempts
            if (!first) {
                try {
                    Thread.sleep(sleepDurationSec*1000);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                first = false;
            }
            monitor.worked(workIncrement);
            
            // Check for cancellation request.  If cancelled, throw InterruptedException
            if(monitor.isCanceled()) {
            	monitor.setCanceled(true);
            	throw new InterruptedException("The operation was cancelled"); //$NON-NLS-1$
            }
            
            ITeiidVdb vdb = getImportServer().getVdb(vdbName);
            
            // Check to see if VDB is actually registered in Designer's VDB cache, else it's still waiting for deployment
            
            if( vdb != null ) {
            	if( !vdb.isLoading() || vdb.hasFailed() || vdb.isActive() || !vdb.getValidityErrors().isEmpty() ) {
            		return true;
            	}
            }
        } while (System.currentTimeMillis() < waitUntil);
        return false;
    }
    
    /**
     * Get the server display name
     * @return the display name
     * @throws Exception the exception
     */
    public String getDisplayName() throws Exception {
        return getImportServer().getParentName();
    }
    
    /**
     * Get the server translators
     * @return the collection of translators
     * @throws Exception the exception
     */
    public Collection<ITeiidTranslator> getTranslators() throws Exception {
        return getImportServer().getTranslators();
    }
    
    /**
     * Get the translator
     * @return the collection of translators
     * @throws Exception the exception
     */
    public ITeiidTranslator getTranslator(String name) throws Exception {
    	for( ITeiidTranslator translator : getTranslators() ) {
    		if( name.equalsIgnoreCase(translator.getName()) ) return translator;
    	}
        return null;
    }


    /**
     * Delete dataSource with the provided jndiName
     * @param jndiName the source jndi name
     * @throws Exception the exception
     */
    public void deleteDataSource(String jndiName) throws Exception {
        getImportServer().deleteDataSource(jndiName);
    }

    /**
     * Deploy the specified driver file
     * @param jarOrRarFile the driver file
     * @throws Exception the exception
     */
    public void deployDriver(File jarOrRarFile) throws Exception {
        getImportServer().deployDriver(jarOrRarFile);
    }

    /**
     * Get the model schema for the specified vdb
     * @param vdbName the Vdb name
     * @param vdbVersion the Vdb version
     * @param modelName the model name
     * @return the Schema DDL
     * @throws Exception the exception
     */
    public String getSchema(String vdbName,
                            String vdbVersion,
                            String modelName) throws Exception {
        return getImportServer().getSchema(vdbName, vdbVersion, modelName);
    }

    /**
     * Get the Teiid Instance data sources
     * @return the collection of Data Sources
     * @throws Exception the exception
     */
    public Collection<ITeiidDataSource> getDataSources() throws Exception {
        return getImportServer().getDataSources();
    }

    /**
     * Get the Servers DataSource Template names
     * @return the Set of Template names
     * @throws Exception the exception
     */
    public Set<String> getDataSourceTemplateNames() throws Exception {
        return getImportServer().getDataSourceTemplateNames();
    }

    /**
     * Get the property definitions for the provided template name
     * @param templateName the template name
     * @return the collection of TeiidPropertyDefinition
     * @throws Exception the exception
     */
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
        return getImportServer().getTemplatePropertyDefns(templateName);
    }

    /**
     * Get or create the specified datasource
     * @param displayName the source display name
     * @param jndiName the jndi name
     * @param typeName the ds type 
     * @param properties the datasource properties
     * @return the Created data source
     * @throws Exception the exception
     */
    public ITeiidDataSource getOrCreateDataSource(String displayName,
                                                  String jndiName,
                                                  String typeName,
                                                  Properties properties) throws Exception {
        return getImportServer().getOrCreateDataSource(displayName, jndiName, typeName, properties);
    }
    
    /**
     * Undeploy the specified VDB
     * @param vdbName the vdb name
     * @throws Exception the exception
     */
    public void undeployDynamicVdb(String vdbName) throws Exception {
        getImportServer().undeployDynamicVdb(vdbName);
    }
    
    /**
     * Deploy the specified dynamic vdb
     * @param deploymentName the deployment name
     * @param inStream dynamic vdb inputStream
     * @throws Exception the exception
     */
    public void deployDynamicVdb(String deploymentName, InputStream inStream) throws Exception {
        getImportServer().deployDynamicVdb(deploymentName, inStream);
    }
    
    /**
     * Get the Properties for the specified DataSource
     * @param dataSourceName the dataSource name
     * @return the Properties for the data source
     * @throws Exception the exception
     */
    public Properties getDataSourceProperties(String dataSourceName) throws Exception {
        return getImportServer().getDataSourceProperties(dataSourceName);
    }
    
    /**
     * Return the version of the current import server - null if not defined or not connected
     * @return the Teiid Instance version
     */
    public ITeiidServerVersion getServerVersion() {
        ITeiidServer importServer = getImportServer();
        // If no server, or not connected - invalid
        if(importServer==null || !importServer.isConnected()) {
            return null;
        }
        return importServer.getServerVersion();
    }

}

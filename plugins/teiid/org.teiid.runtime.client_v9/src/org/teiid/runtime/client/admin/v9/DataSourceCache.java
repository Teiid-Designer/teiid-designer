package org.teiid.runtime.client.admin.v9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.as.cli.Util;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.impl.PropertyDefinitionMetadata;
import org.teiid.adminapi.jboss.MetadataMapper;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidRuntimePlugin;
import org.teiid.runtime.client.admin.TeiidDataSource;
import org.teiid.runtime.client.admin.v9.AdminUtil.DataSourceType;


public class DataSourceCache implements AdminConstants {
    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
	
	private AdminConnectionManager manager;
	
    // ----------------------------
	private Collection<String> poolNames;
	private Map<String, CommonDataSource> poolNameToTeiidDataSourceMap;
	private Map<String, CommonDataSource> jndiNameToTeiidDataSourceMap;
	private Set<String> installedJdbcDrivers = new HashSet<String>();
	
    
	private HashMap<String, Collection<PropertyDefinition>> translatorTempPropDefs;
	
	public DataSourceCache(AdminConnectionManager adminConnectionManager, ITeiidServer teiidServer) {
		super();
		this.manager = adminConnectionManager;
		this.poolNameToTeiidDataSourceMap = new HashMap<String, CommonDataSource>();
		this.jndiNameToTeiidDataSourceMap = new HashMap<String, CommonDataSource>();
		this.installedJdbcDrivers = new HashSet<String>();
		this.poolNames = new HashSet<String>();
	}
	
	public void refresh() throws AdminException {
//		System.out.println("       >>>  DatasourceCache.refresh() ===========");
		
		this.poolNameToTeiidDataSourceMap.clear();
		this.jndiNameToTeiidDataSourceMap.clear();
		this.installedJdbcDrivers.clear();
		this.poolNames.clear();
		
		
		this.poolNames.addAll(this.manager.getChildNodeNames(DATASOURCES, DATA_SOURCE));
		Collection<String> xaDSList = new ArrayList<String>();
		xaDSList = this.manager.getChildNodeNames(DATASOURCES, XA_DATA_SOURCE);
		this.poolNames.addAll(xaDSList);
		
		boolean isXA = false;
		
		// Now create Data sources
		for( String poolName : this.poolNames ) {
            if (poolName.equalsIgnoreCase("DefaultDS") || poolName.equalsIgnoreCase("JmsXA")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

			Properties dsProperties = new Properties();
			
			if( xaDSList.contains(poolName) ) {
				this.manager.cliCall(READ_RESOURCE,
						new String[] { SUBSYSTEM, DATASOURCES, XA_DATA_SOURCE, poolName}, null,
						new DataSourceProperties(dsProperties));
				isXA = true;
			} else {
				this.manager.cliCall(READ_RESOURCE,
					new String[] { SUBSYSTEM, DATASOURCES, DATA_SOURCE, poolName}, null,
					new DataSourceProperties(dsProperties));

			}
			
			// Find the JNDI name which is REQUIRED
			String jndiName = dsProperties.getProperty(JNDI_NAME);
			if( jndiName == null ) {
				TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.requiredJndiNameMissing, poolName));
				continue;
			}
			
			String dsName = AdminUtil.removeJavaPrefix(jndiName);
			
            TeiidDataSource tds = new TeiidDataSource(dsName, jndiName, poolName, "<unknown>"); //$NON-NLS-1$			

            // Transfer properties to the ITeiidDataSource
			tds.getProperties().putAll(dsProperties);
			
			AdminUtil.DataSourceType type = DataSourceType.DATA_SOURCE;
			if( isXA ) {
				type = DataSourceType.XA_DATA_SOURCE;
			}
			
			CommonDataSource cds = new CommonDataSource(tds, type, null);
			
			this.poolNameToTeiidDataSourceMap.put(poolName, cds);
			this.jndiNameToTeiidDataSourceMap.put(jndiName, cds);
		}
		
		refreshInstalledJdbcDrivers();
	}
	
	/**
	 * @return installed jdbc drivers
	 * @throws AdminException
	 */
	public void refreshInstalledJdbcDrivers() throws AdminException {
		installedJdbcDrivers = new HashSet<String>();
		installedJdbcDrivers.addAll(manager.getChildNodeNames(DATASOURCES, JDBC_DRIVER));

		if (!manager.isDomainMode()) {				//'installed-driver-list' not available in the domain mode.
			final ModelNode request = manager.buildRequest(DATASOURCES, INSTALLED_DRIVERS_LIST);
	        try {
	            ModelNode outcome = manager.execute(request);
	            if (Util.isSuccess(outcome)) {
		            List<String> drivers = manager.getList(outcome, new AbstractMetadatMapper() {
						@Override
						public String unwrap(ITeiidServerVersion teiidVersion, ModelNode node) {
							if (node.hasDefined(DRIVER_NAME)) {
								return node.get(DRIVER_NAME).asString();
							}
							return null;
						}
					});
		            installedJdbcDrivers.addAll(drivers);
	            }
	        } catch (IOException e) {
	        	throw new AdminComponentException(e);
	        }
		}
		else {
			// TODO: AS7 needs to provide better way to query the deployed JDBC drivers
			List<String> deployments = manager.getChildNodeNames(null, DEPLOYMENT);
            for (String deployment:deployments) {
            	if (!deployment.contains(TRANSLATOR) && deployment.endsWith(DEPLOYMENT)) {
            		installedJdbcDrivers.add(deployment);
            	}
            }
		}
	}

	public void delete(CommonDataSource cds) throws AdminException {
		if( cds.isXADataSource() ) {
			this.manager.deleteSubsystem(cds.getPoolName(),DATASOURCES, XA_DATA_SOURCE);
		} else {
			this.manager.deleteSubsystem(cds.getPoolName(),DATASOURCES, DATA_SOURCE);
		}
	}
	
	public CommonDataSource create(String displayName, String jndiName,	String templateName, Properties properties)	throws AdminException {
		// take incoming properties and create a data source through Teiid and CLI and return a CommonDataSource object
    	// build properties
    	Collection<PropertyDefinition> dsProperties = getTemplatePropertyDefinitions(templateName);
    	ArrayList<String> parameters = new ArrayList<String>();
        if (properties != null) {
        	parameters.add(CONNECTION_URL);
        	parameters.add(properties.getProperty(CONNECTION_URL));

            for (PropertyDefinition prop : dsProperties) {
            	if (prop.getName().equals(CONNECTION_PROPERTIES)) {
            		continue;
            	}
            	String value = properties.getProperty(prop.getName());
            	if (value != null) {
                	parameters.add(prop.getName());
                	parameters.add(value);
            	}
            }
        }
        else {
        	 throw new AdminComponentException(Messages.gs(Messages.TEIID.TEIID70005));
        }

    	parameters.add(JNDI_NAME);
    	parameters.add(AdminUtil.addJavaPrefix(jndiName));
    	parameters.add(DRIVER_NAME);
    	parameters.add(templateName);
    	parameters.add(POOL_NAME);
    	parameters.add(displayName);

    	// add data source
		manager.cliCall(ADD, new String[] { SUBSYSTEM, DATASOURCES,DATA_SOURCE, displayName },
				parameters.toArray(new String[parameters.size()]),
				new ResultCallback());

        // add connection properties that are specific to driver
        String cp = properties.getProperty(CONNECTION_PROPERTIES);
        if (cp != null) {
        	StringTokenizer st = new StringTokenizer(cp, ",");
        	while(st.hasMoreTokens()) {
        		String prop = st.nextToken();
        		String key = prop.substring(0, prop.indexOf('='));
        		String value = prop.substring(prop.indexOf('=')+1);
        		addConnectionProperty(displayName, key, value);
        	}
        }

		return null;
	}
	
	public boolean isDataSourceType(String type) {
		return AdminUtil.isJdbcTranslator(type);
	}
	
	public ITeiidDataSource getDataSourceWithPoolName(String poolName) {
		return poolNameToTeiidDataSourceMap.get(poolName);
	}
	
	public Map<String, CommonDataSource> getPoolNameMap() {
		return  poolNameToTeiidDataSourceMap;
	}
	
	public CommonDataSource getDataSourceWithJndiName(String poolName) {
		return jndiNameToTeiidDataSourceMap.get(poolName);
	}
	
	public Map<String, CommonDataSource> getJndiNameMap() {
		return  jndiNameToTeiidDataSourceMap;
	}
	
	public Collection<CommonDataSource> getDataSources() {
		return poolNameToTeiidDataSourceMap.values();
	}
	
	public Set<String> getInstalledJdbcDrivers() {
		return installedJdbcDrivers;
	}
	
	private void addConnectionProperty(String deploymentName, String key, String value) throws AdminException {
		if (value == null || value.trim().isEmpty()) {
			throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70054, key));
		}
		manager.cliCall(ADD, new String[] { SUBSYSTEM, DATASOURCES,DATA_SOURCE, deploymentName,
				CONNECTION_PROPERTIES, key },
				new String[] {VALUE, value }, new ResultCallback());
	}
	
	public Collection<PropertyDefinition> getTemplatePropertyDefinitions(String templateName) throws AdminException {
		if( translatorTempPropDefs == null ) {
			this.translatorTempPropDefs = new HashMap<String, Collection<PropertyDefinition>>();
		}
		
		Collection<PropertyDefinition> props = this.translatorTempPropDefs.get(templateName);
		
		if(  props == null ) {
			props = loadTemplatePropertyDefinition(templateName);
			this.translatorTempPropDefs.put(templateName, props);
		}

        return props;
	}
	
	private Collection<PropertyDefinition> loadTemplatePropertyDefinition(String templateName) throws AdminException {
		if( translatorTempPropDefs == null ) {
			this.translatorTempPropDefs = new HashMap<String, Collection<PropertyDefinition>>();
		}
		
		Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
		
		BuildPropertyDefinitions builder = new BuildPropertyDefinitions();
		
		
    	// get JDBC properties
		manager.cliCall(READ_RESOURCE_DESCRIPTION, new String[] {SUBSYSTEM, DATASOURCES,DATA_SOURCE, templateName}, null, builder);

        // add driver specific properties
        PropertyDefinitionMetadata cp = new PropertyDefinitionMetadata();
        cp.setName(CONNECTION_PROPERTIES);
        cp.setDisplayName("Additional Driver Properties");
        cp.setDescription("The connection-properties element allows you to pass in arbitrary connection properties to the Driver.connect(url, props) method. Supply comma separated name-value pairs"); //$NON-NLS-1$
        cp.setRequired(false);
        cp.setAdvanced(true);
        props = builder.getPropertyDefinitions();
        props.add(cp);
		
    	return props;
	}
	
	class AbstractMetadatMapper implements MetadataMapper<String>{
		@Override
		public String unwrap(ITeiidServerVersion teiidVersion, ModelNode node) {
			return null;
		}
		@Override
		public ModelNode describe(ITeiidServerVersion teiidVersion, ModelNode node) {
			return null;
		}
	}
}

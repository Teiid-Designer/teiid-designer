/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v8;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.impl.PropertyDefinitionMetadata;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidRuntimePlugin;
import org.teiid.runtime.client.admin.TeiidDataSource;

public class ResourceAdapterCache implements AdminConstants {

	private AdminConnectionManager manager;
	
    // ----------------------------
	private Collection<String> resourceAdapterIDs;
	private Map<String, String> poolNameToIDMap;
	// Map of RA pool-name to data source
	private Map<String, CommonDataSource> poolNameToTeiidDataSourceMap;
	private Map<String, CommonDataSource> jndiNameToTeiidDataSourceMap;
	private Map<String, String> raIDToModuleMap;
	
	private Set<String> deployedResourceAdaptorNames;
	
	private HashMap<String, Collection<PropertyDefinition>> resoureAdapterTempPropDefs;
	
	public ResourceAdapterCache(AdminConnectionManager adminConnectionManager, ITeiidServer teiidServer) {
		super();
		this.manager = adminConnectionManager;
		this.poolNameToIDMap = new  HashMap<String, String>();
		this.poolNameToTeiidDataSourceMap = new HashMap<String, CommonDataSource>();
		this.jndiNameToTeiidDataSourceMap = new HashMap<String, CommonDataSource>();
		this.raIDToModuleMap = new HashMap<String, String>();
		this.resourceAdapterIDs = new HashSet<String>();
		this.deployedResourceAdaptorNames = new HashSet<String>();
	}

	public void refresh() throws AdminException {
//		System.out.println("      >>>  ResourceAdapterCache.refresh() ===========");
		
		poolNameToTeiidDataSourceMap.clear();
		jndiNameToTeiidDataSourceMap.clear();
		raIDToModuleMap.clear();
		deployedResourceAdaptorNames.clear();
		poolNameToIDMap.clear();
		resourceAdapterIDs.clear();
		
		// This will return a list of resource adapter ID's, including those with NO <connection-definitions>
		resourceAdapterIDs.addAll(manager.getChildNodeNames(RESOURCE_ADAPTERS, RESOURCE_ADAPTER));
		
		
        // NOte that a ResourceAdapter can have multiple connection definitions in it. We need to get the definitions and
		// map the pool-names to the ID's
		
		poolNameToIDMap.clear();
		
		for( String resAdapterId : resourceAdapterIDs ) {
			getRAConnectionDefinitions(poolNameToIDMap, resAdapterId);
		}
		
		// Now create Data sources
		for( String poolName : poolNameToIDMap.keySet() ) {
			// get ID for RA
			String resAdapterID = poolNameToIDMap.get(poolName);
			
            if (resAdapterID.equalsIgnoreCase("DefaultDS") || resAdapterID.equalsIgnoreCase("JmsXA")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            
			// Get properties including the JNDI name which is REQUIRED
			Properties dsProperties = new Properties();
			
			manager.cliCall(READ_RESOURCE,
						new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID, CONNECTION_DEFINITIONS, poolName},
						null, new ConnectionFactoryProperties(dsProperties, resAdapterID, poolName, null, manager));
            
			
			// Find the JNDI name which is REQUIRED
			String jndiName = dsProperties.getProperty(JNDI_NAME);
			if( jndiName == null ) {
				TeiidRuntimePlugin.logError(getClass().getSimpleName(), Messages.getString(Messages.ExecutionAdmin.requiredJndiNameMissing, poolName));
				continue;
			}
			
			String dsName = AdminUtil.removeJavaPrefix(jndiName);
			
            TeiidDataSource tds = new TeiidDataSource(dsName, jndiName, "<unknown>"); //$NON-NLS-1$

            // Transfer properties to the ITeiidDataSource
			tds.getProperties().putAll(dsProperties);
			
			// Now need to add driver-name?
			// figure out driver-name
			String driverName = dsProperties.getProperty(DRIVER_NAME);
            if (driverName == null) {
            	driverName = getModuleDriverName(resAdapterID);
            }


			CommonDataSource cds = new CommonDataSource(tds, AdminUtil.DataSourceType.RESOURCE_ADAPTER, resAdapterID);
			
			this.poolNameToTeiidDataSourceMap.put(poolName, cds);
			this.jndiNameToTeiidDataSourceMap.put(jndiName, cds);
        	if( driverName != null ) {
        		tds.getProperties().put(DRIVER_NAME, driverName);
        		raIDToModuleMap.put(resAdapterID,  driverName);
        	}
		}
		
		loadDeployedResourceAdaptorNames();

	}
	
	private void getRAConnectionDefinitions(final Map<String, String> datasourceNames, final String rarName) throws AdminException {
		manager.cliCall(READ_RESOURCE, new String[] {SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, rarName}, null, new ResultCallback() {
			@Override
			public void onSuccess(ModelNode outcome, ModelNode result) throws AdminProcessingException {
	        	if (result.hasDefined(CONNECTION_DEFINITIONS)) {
	        		List<ModelNode> connDefs = result.get(CONNECTION_DEFINITIONS).asList();
	        		for (ModelNode conn:connDefs) {
	        			Iterator<String> it = conn.keys().iterator();
	        			if (it.hasNext()) {
	        				datasourceNames.put(it.next(), rarName);
	        			}
	        		}
	        	}
	        }
			@Override
			public void onFailure(String msg) throws AdminProcessingException {
				// no-op
			}
		});
	}

	private void loadDeployedResourceAdaptorNames() throws AdminException {
        Set<String> templates = new HashSet<String>();
        
        List<String> deployments = manager.getChildNodeNames(null, DEPLOYMENT);
        for (String deployment:deployments) {
            if (deployment.endsWith(DOT_RAR)) {
                templates.add(deployment);
            }
        }
        this.deployedResourceAdaptorNames.addAll(templates);
    }
	
	private String getModuleDriverName(String resAdapterID) throws AdminException {
		final List<String> props = new ArrayList<String>();
		
		manager.cliCall(READ_RESOURCE,
				new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID},
				null, new ResultCallback() {
					@Override
					void onSuccess(ModelNode outcome, ModelNode result) throws AdminException {
			    		List<ModelNode> properties = outcome.get(RESULT).asList();
			    		
		        		for (ModelNode prop:properties) {
		        			if (!prop.getType().equals(ModelType.PROPERTY)) {
		        				continue;
		        			}
		    				org.jboss.dmr.Property p = prop.asProperty();			        			
							if (p.getName().equals(MODULE)) {
								props.add(p.getValue().asString());
								break;
							}
		        		}
					}
				});
		String value = props.get(0);
		
		return value;
	}
	
	public void delete(CommonDataSource cds) throws AdminException {

		this.manager.cliCall(REMOVE, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, cds.getResourceAdapterID(),
				CONNECTION_DEFINITIONS, cds.getDisplayName() }, null,
				new ResultCallback());
	}
	
	public CommonDataSource create(String displayName, String jndiName,	String templateName, Properties properties)	throws AdminException {
		// take incoming properties and create a data source through Teiid and CLI and return a CommonDataSource object

		if( resourceAdapterIDs.contains(templateName ) ) {
			createResourceAdapter(displayName, templateName, properties);
	        return null;
	    }
		return null;
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
	
	public Set<String> getAllResourceAdapterNames() {
		Set<String> names = new HashSet<String>();
		names.addAll(resourceAdapterIDs);
		names.addAll(deployedResourceAdaptorNames);
		return names;
	}
	
	private void createResourceAdapter(String deploymentName, String resAdapterID, Properties properties)	throws AdminException {
		
		boolean raExists = false;
		
		if (!deployedResourceAdaptorNames.contains(resAdapterID) && !this.resourceAdapterIDs.contains(resAdapterID)) {
			///subsystem=resource-adapters/resource-adapter=fileDS:add
			addArchiveResourceAdapter(resAdapterID);
		}

		//AS-4776 HACK - BEGIN
		else {
			// add duplicate resource adapter AS-4776 Workaround
			String moduleName = getResourceAdapterModuleName(resAdapterID);
			// Check for existing mapped module
			if( raIDToModuleMap.get(deploymentName) != null ) {
				// remove it
				this.manager.cliCall(REMOVE, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, deploymentName}, null,
						new ResultCallback());
			}
			addModuleResourceAdapter(deploymentName, moduleName);
			resAdapterID = deploymentName;
		}
		//AS-4776 HACK - END
		
		BuildPropertyDefinitions bpd = new BuildPropertyDefinitions();
		buildResourceAdapterProperties(resAdapterID, bpd);
		ArrayList<PropertyDefinition> jcaSpecific = bpd.getPropertyDefinitions();

		///subsystem=resource-adapters/resource-adapter=fileDS/connection-definitions=fileDS:add(jndi-name=java\:\/fooDS)
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(JNDI_NAME);
        parameters.add(AdminUtil.addJavaPrefix(deploymentName));
        parameters.add(ENABLED);
        parameters.add(TRUE);
        if (properties.getProperty(CLASS_NAME) != null) {
	        parameters.add(CLASS_NAME);
	        parameters.add(properties.getProperty(CLASS_NAME));
        }

        // add jca specific proeprties
        for (PropertyDefinition pd:jcaSpecific) {
            if (properties.getProperty(pd.getName()) != null) {
    	        parameters.add(pd.getName());
    	        parameters.add(properties.getProperty(pd.getName()));
            }
        }

        if( raExists ) {
        	
        } else {
			this.manager.cliCall(ADD, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID,
					CONNECTION_DEFINITIONS, deploymentName },
					parameters.toArray(new String[parameters.size()]), new ResultCallback());
        }
        // add all the config properties
        Enumeration<?> keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
        	boolean add = true;
        	String key = (String)keys.nextElement();
        	if (key.equals(CLASS_NAME)) {
        		add = false;
        	}
        	for (PropertyDefinition pd:jcaSpecific) {
        		if (key.equals(pd.getName())) {
        			add = false;
        			break;
        		}
        	}
        	if (add) {
        		addConfigProperty(resAdapterID, deploymentName, key, properties.getProperty(key));
        	}
        }

        // activate
        activateConnectionFactory(resAdapterID);
	}
	
	// /subsystem=resource-adapters/resource-adapter=teiid-connector-ws.rar:add(archive=teiid-connector-ws.rar, transaction-support=NoTransaction)
	private void addArchiveResourceAdapter(String rarName) throws AdminException {
		this.manager.cliCall(ADD, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, rarName },
				new String[] { ARCHIVE, rarName, TRANSACTION_SUPPORT,NO_TRANSACTION },
				new ResultCallback());
	}
	
	private String getResourceAdapterModuleName(String resAdapterID)
			throws AdminException {
		final List<String> props = new ArrayList<String>();
		
		String value = this.raIDToModuleMap.get(resAdapterID);
		if( value != null ) return value;
		
		this.manager.cliCall(READ_RESOURCE,
				new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID},
				null, new ResultCallback() {
					@Override
					void onSuccess(ModelNode outcome, ModelNode result) throws AdminException {
			    		List<ModelNode> properties = outcome.get(RESULT).asList();
			    		
		        		for (ModelNode prop:properties) {
		        			if (!prop.getType().equals(ModelType.PROPERTY)) {
		        				continue;
		        			}
		    				org.jboss.dmr.Property p = prop.asProperty();			        			
							if (p.getName().equals(MODULE)) {
								props.add(p.getValue().asString());
							}
		        		}
					}
				});
		value = props.get(0);
			
		if( value != null ) {
			 this.raIDToModuleMap.put(resAdapterID, value);
		}
		return value;
	}
	
	private void addModuleResourceAdapter(String resAdapterID, String moduleName) throws AdminException {
		this.manager.cliCall(ADD, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID },
				new String[] { MODULE, moduleName, TRANSACTION_SUPPORT,NO_TRANSACTION },
				new ResultCallback());			
	}
	
	/**
	 * /subsystem=resource-adapters/resource-adapter=teiid-connector-ws.rar/connection-definitions=foo:read-resource-description
	 */
	private void buildResourceAdapterProperties(String resAdapterID, BuildPropertyDefinitions builder) throws AdminException {
		this.manager.cliCall(READ_RESOURCE_DESCRIPTION, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, resAdapterID,
				CONNECTION_DEFINITIONS, ANY }, null, builder);
	}
	
	// /subsystem=resource-adapters/resource-adapter=fileDS/connection-definitions=fileDS/config-properties=ParentDirectory2:add(value=/home/rareddy/testing)
	private void addConfigProperty(String rarName, String deploymentName, String key, String value) throws AdminException {
		if (value == null || value.trim().isEmpty()) {
			throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70054, key));
		}
		this.manager.cliCall(ADD, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, rarName,
				CONNECTION_DEFINITIONS, deploymentName,
				CONFIG_PROPERTIES, key},
				new String[] {VALUE, value}, new ResultCallback());
	}

	// /subsystem=resource-adapters/resource-adapter=fileDS:activate
	private void activateConnectionFactory(String rarName) throws AdminException {
		this.manager.cliCall(ACTIVATE, new String[] { SUBSYSTEM, RESOURCE_ADAPTERS, RESOURCE_ADAPTER, rarName },
				null, new ResultCallback());
	}
	
	public Collection<PropertyDefinition> getTemplatePropertyDefinitions(String templateName) throws AdminException {
		if( resoureAdapterTempPropDefs == null ) {
			this.resoureAdapterTempPropDefs = new HashMap<String, Collection<PropertyDefinition>>();
		}
		
		Collection<PropertyDefinition> props = this.resoureAdapterTempPropDefs.get(templateName);
		
		if(  props == null ) {
			props = loadTemplatePropertyDefinition(templateName);
			this.resoureAdapterTempPropDefs.put(templateName, props);
		}

        return props;
	}
	
	public boolean isResourceAdapter(String name) {
		return resourceAdapterIDs.contains(name);
	}
	
	private Collection<PropertyDefinition> loadTemplatePropertyDefinition(String templateName) throws AdminException {
		if( resoureAdapterTempPropDefs == null ) {
			this.resoureAdapterTempPropDefs = new HashMap<String, Collection<PropertyDefinition>>();
		}
		
		Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
		
		BuildPropertyDefinitions builder = new BuildPropertyDefinitions();

    	// get resource adapter node properties
		manager.cliCall(READ_RAR_DESCRIPTION, new String[] {SUBSYSTEM, TEIID}, new String[] {RAR_NAME, templateName}, builder);
		
    	// get resource adapter properties
		manager.cliCall(READ_RESOURCE_DESCRIPTION, new String[] {
				SUBSYSTEM, RESOURCE_ADAPTERS,
				RESOURCE_ADAPTER, templateName, 
				CONNECTION_DEFINITIONS, ANY}, null, builder);


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
	
    @SuppressWarnings("unchecked")
	public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {

        Collection<? extends PropertyDefinition> propDefs = getTemplatePropertyDefinitions(templateName);
        
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
	
}

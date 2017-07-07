/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jboss.as.controller.client.ModelControllerClient;
import org.teiid.adminapi.AdminException;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.WorkspaceUUIDService;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.runtime.client.admin.v8.AdminUtil;
import org.teiid.runtime.client.admin.v8.CommonDataSource;
import org.teiid.runtime.client.admin.v8.DataSourceCache;
import org.teiid.runtime.client.admin.v8.ResourceAdapterCache;
import org.teiid.runtime.client.admin.v8.TranslatorCache;

public class ConnectionManager {
	
    protected Map<String, CommonDataSource> dataSourceByJndiNameMap;
    protected Map<String, CommonDataSource> dataSourceByPoolNameMap;
    protected Set<String> dataSourceTypeNames;
    
    protected Collection<ITeiidDataSource> allDataSources;
    
    private DataSourceCache dataSourceCache;
    private ResourceAdapterCache resourceAdapterCache;
    private TranslatorCache translatorCache;
    
    /**
     * Constructor used for testing purposes only. 
     * 
     * @param admin the associated Teiid Admin API (never <code>null</code>)
     * @param teiidServer the server this admin belongs to (never <code>null</code>)
     * @throws Exception if there is a problem connecting the server
     */
    ConnectionManager(ITeiidServer teiidServer, ModelControllerClient connection) throws Exception {
        ArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        ArgCheck.isNotNull(connection, "connection"); //$NON-NLS-1$
        
        this.dataSourceCache = new DataSourceCache(connection, teiidServer);
        this.resourceAdapterCache = new ResourceAdapterCache(connection, teiidServer);
        this.translatorCache = new TranslatorCache(connection, teiidServer);
        
        this.allDataSources = new ArrayList<ITeiidDataSource>();
    }
    
    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
    public static final String PREVIEW_PREFIX = "PREVIEW_"; //$NON-NLS-1$
    
    public Collection<ITeiidDataSource> findTeiidDataSources( Collection<String> names) {
        Collection<ITeiidDataSource> dataSources = new ArrayList<ITeiidDataSource>();

        for (String name : names) {
            if (name.equalsIgnoreCase("DefaultDS") || name.equalsIgnoreCase("JmsXA")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            TeiidDataSource tds = new TeiidDataSource(name, name, "<unknown>"); //$NON-NLS-1$
            
            if (name.startsWith(PREVIEW_PREFIX)) {
                UUID workspaceUuid = WorkspaceUUIDService.getInstance().getUUID();
                if (name.length() > workspaceUuid.toString().length() + 8) {
                    tds.setPreview(true);
                }
            }
            dataSources.add(tds);
        }

        return dataSources;
    }

    
    public boolean dataSourceExists( String name ) {
    	// Note that dataSourceNames actually contains a list of pool-name's for data sources.
    	// If we're doing a JNDI name check, we need to look at 
    	return this.dataSourceByJndiNameMap.get(name) != null;
    }

    public void deleteDataSource( String dsName ) throws Exception {
    	// dsName may be JNDI or Pool name
    	CommonDataSource cds = getDataSource(dsName);

        // Check if exists, return false
    	if( cds != null) {
    		if( cds.isResourceAdapter() ) {
    			this.resourceAdapterCache.delete(cds);
    		} else {
    			this.dataSourceCache.delete(cds);
    		}

        } else {
        	System.out.println("  ##$#$#$#  >> Did not find a data source to delete:  " + dsName);
        }
    }
    
	public void createDataSource(String jndiName,	String dataSourceType, boolean isJdbc, Properties properties)	throws AdminException {
		// if the deploymentName contains java: prefix, remove it
		String displayName = AdminUtil.removeJavaPrefix(jndiName);

		if( isJdbc )  {
			this.dataSourceCache.create(displayName, jndiName, dataSourceType, properties);
		} else {
			this.resourceAdapterCache.create(displayName, jndiName, dataSourceType, properties);
		}
	}
    
    public void disconnect() {
        this.dataSourceByJndiNameMap = new HashMap<String, CommonDataSource>();
        this.dataSourceByPoolNameMap = new HashMap<String, CommonDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
    }
    
    public CommonDataSource getDataSource(String jndiOrPoolName) {
    	
    	CommonDataSource ds = this.dataSourceByJndiNameMap.get(jndiOrPoolName);
    	if( ds == null ) {
    		ds = this.dataSourceByPoolNameMap.get(jndiOrPoolName);
    	}

        return ds;
    }
    
	public Collection<ITeiidDataSource> getDataSources() {
        return this.allDataSources;
    }

	public Set<String> getDataSourceTypeNames() {
        return this.dataSourceTypeNames;
    }
	
	public boolean dataSourceTypeExists(String typeName) {
		return this.dataSourceTypeNames.contains(typeName);
	}
	
    public void init() throws Exception {
        this.dataSourceByPoolNameMap = new HashMap<String, CommonDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        this.dataSourceByJndiNameMap = new HashMap<String, CommonDataSource>();
    }
    
    protected void refreshDataSources() throws Exception {
    	//System.out.println("    >>>  ConnectionManager.refreshDataSources()   ===========");
    	this.dataSourceCache.refresh();
    	this.resourceAdapterCache.refresh();
    	
    	this.dataSourceByJndiNameMap.clear();
    	this.dataSourceByPoolNameMap.clear();
    	this.allDataSources.clear();
    	
    	this.dataSourceByPoolNameMap.putAll(this.dataSourceCache.getPoolNameMap());
    	this.dataSourceByJndiNameMap.putAll(this.dataSourceCache.getJndiNameMap());
    	this.dataSourceByPoolNameMap.putAll(this.resourceAdapterCache.getPoolNameMap());
    	this.dataSourceByJndiNameMap.putAll(this.resourceAdapterCache.getJndiNameMap());

    	for( CommonDataSource cds : dataSourceByJndiNameMap.values() ) {
    		allDataSources.add(cds.getTds());
    	}
    }
    
    public Collection<String> getDataSourcePoolNames() {
    	return dataSourceByPoolNameMap.keySet();
    }
    
    public Collection<String> getDataSourceJndiNames() {
    	return dataSourceByJndiNameMap.keySet();
    }
    
    protected void refreshDataSourceTypes() throws Exception {
		Set<String> templates = new HashSet<String>();
			templates.addAll(this.dataSourceCache.getInstalledJdbcDrivers());
			templates.addAll(this.resourceAdapterCache.getAllResourceAdapterNames());
        // populate data source type names set
        this.dataSourceTypeNames = new HashSet<String>(templates);
    }
    
    protected void refreshTranslators() throws Exception {
    	this.translatorCache.refresh();
    }
    
    public ITeiidTranslator getTranslator(String name) {
    	return this.translatorCache.getTranslator(name);
    }
    
    public Collection<ITeiidTranslator> getTranslators() {
    	return this.translatorCache.getTranslators();
    }
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
    	Collection<TeiidPropertyDefinition> result = new ArrayList<TeiidPropertyDefinition>();
    	result.addAll(this.translatorCache.getTemplatePropertyDefns(templateName));
    	result.addAll(dataSourceCache.getTemplatePropertyDefns(templateName));
    	return result;
    }
}

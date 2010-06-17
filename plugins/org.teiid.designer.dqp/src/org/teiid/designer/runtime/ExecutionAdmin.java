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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.designer.runtime.connection.ModelConnectionMatcher;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.CoreArgCheck;

/**
 *
 */
public class ExecutionAdmin {

    private final Admin admin;
    protected Map<String, TeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Map<String, TeiidDataSource> dataSourceByNameMap;
    protected Set<String> dataSourceTypeNames;
    private final EventManager eventManager;
    private final Server server;
    private Set<VDB> vdbs;
    private final ModelConnectionMatcher connectionMatcher;
    
    private boolean loaded = false;

    /**
     * @param admin the associated Teiid Admin API (never <code>null</code>)
     * @param server the server this admin belongs to (never <code>null</code>)
     * @param eventManager the event manager used to fire events (never <code>null</code>)
     * @throws Exception if there is a problem connecting the server
     */
    public ExecutionAdmin( Admin admin,
                           Server server,
                           EventManager eventManager ) throws Exception {
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(eventManager, "eventManager"); //$NON-NLS-1$

        this.admin = admin;
        this.eventManager = eventManager;
        this.server = server;
        this.connectionMatcher = new ModelConnectionMatcher(this);
        
        init();
//        refresh();
    }
    
    private void init() throws Exception {
    	this.translatorByNameMap = new HashMap<String, TeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, TeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        this.vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
    }
    /**
     * 
     * @throws Exception
     */
    public void load() throws Exception {
    	if( !this.loaded ) {
    		refresh();
    		this.loaded = true;
    	}
    }
    

    public Collection<TeiidDataSource> getDataSources() {
        return this.dataSourceByNameMap.values();
    }
    
    public Set<String> getDataSourceTypeNames() {
    	return this.dataSourceTypeNames;
    }
    
    /**
     * 
     * @param name
     * @param typeName
     * @param properties
     * @return true if data source is created. false if it already exists
     * @throws Exception
     */
    public TeiidDataSource getOrCreateDataSource(String displayName, String jndiName, String typeName, Properties properties) throws Exception {
    	CoreArgCheck.isNotEmpty(displayName, "displayName"); //$NON-NLS-1$
    	CoreArgCheck.isNotEmpty(jndiName, "jndiName"); //$NON-NLS-1$
    	CoreArgCheck.isNotEmpty(typeName, "typeName"); //$NON-NLS-1$
    	CoreArgCheck.isNotEmpty(properties, "properties"); //$NON-NLS-1$
    	
    	// Check if exists, return false
    	if( dataSourceExists(jndiName)) {
    		return this.dataSourceByNameMap.get(jndiName);
    	}
    	
    	// Verify the "typeName" exists.
    	if( !this.dataSourceTypeNames.contains(typeName) ) {
    		throw new Exception(Util.getString("dataSourceTypeDoesNotExist", typeName, getServer().getUrl())); //$NON-NLS-1$
    	}
    	
    	this.admin.createDataSource(jndiName, typeName, properties);
    	
    	refreshDataSourceNames();
    	
    	// Check that local name list contains new jndiName
    	if( dataSourceExists(jndiName) ) {
    		TeiidDataSource tds = new TeiidDataSource(displayName, jndiName, typeName);
    		this.dataSourceByNameMap.put(jndiName, tds);
    		
    		this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));
    		
    		return tds;
    	}
    	
    	// We shouldn't get here if data source was created
    	throw new Exception(Util.getString("errorCreatingDataSource", jndiName, typeName, getServer().getUrl())); //$NON-NLS-1$
    }
    
    /**
     * 
     * @param name
     * @return true if data source exists with the provided name. else false.
     * @throws Exception
     */
    public boolean dataSourceExists(String name) throws Exception {
    	// Check if exists, return false
    	if( this.dataSourceNames.contains(name)) {
    		return true;
    	}
    	
    	return false;
    }

    public void deleteDataSource(String jndiName) throws Exception {
    	// Check if exists, return false
    	if( this.dataSourceNames.contains(jndiName)) {
    		this.admin.deleteDataSource(jndiName);

	    	if( !this.admin.getDataSourceNames().contains(jndiName) ) {
	    		this.dataSourceNames.remove(jndiName);
	    		TeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);
	    		if( tds != null ) {
		    		this.dataSourceByNameMap.remove(jndiName);
		    		this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveDataSourceEvent(tds));
		    		
	    		}
	    	}
	    	return;
    	} else {
    		return;
    	}
//    	
//    	// TODO: I18n
//    	throw new Exception(Util.getString("errorDeletingDataSource", jndiName, getServer().getUrl()));
    }
    
    /**
     * @param vdb
     * @return
     */
    public VDB deployVdb( Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$

        VDB deployedVdb = null; // this.admin.deployVDB(vdb.getName(), vdb);

        return deployedVdb;
    }

    /**
     * @param vdb
     * @return
     */
    public VDB deployVdb( IFile vdbFile ) throws Exception {
        CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        String vdbName = vdbFile.getFullPath().lastSegment();
        
        admin.deployVDB(vdbName, vdbFile.getContents());


        return admin.getVDB(vdbName, 1);
    }

    Admin getAdminApi() {
        return this.admin;
    }

    /**
     * @param name the translator name (never <code>null</code> or empty)
     * @return
     * @throws Exception
     * @since 7.0
     */
    public TeiidTranslator getTranslator( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.translatorByNameMap.get(name);
    }

    public Collection<TeiidTranslator> getTranslators() {
        return this.translatorByNameMap.values();
    }

    /**
     * @return the event manager (never <code>null</code>)
     */
    EventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * @return the server who owns this admin object (never <code>null</code>)
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * @return an unmodifiable set of VDBs deployed on the server
     */
    public Set<VDB> getVdbs() {
        return this.vdbs;
    }

    /**
     * @param name the name of the VDB being requested (never <code>null</code> or empty)
     * @return the VDB or <code>null</code> if not found
     * @since 7.0
     */
    public VDB getVdb( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$

        for (VDB vdb : this.vdbs) {
            if (vdb.getName().equals(name)) return vdb;
        }

        return null;
    }

    public void refresh() throws Exception {
        // populate translator map
        refreshTranslators(this.admin.getTranslators());
        
        // populate data source type names set
        this.dataSourceTypeNames = new HashSet(this.admin.getDataSourceTemplateNames());

        // populate data source names list
        refreshDataSourceNames();
        
        Collection<TeiidDataSource> tdsList = connectionMatcher.findTeiidDataSources();
        for( TeiidDataSource ds :tdsList ) {
        	this.dataSourceByNameMap.put(ds.getName(), ds);
        }

        // populate VDBs and source bindings
        // TODO may need to filter out hidden vdb
        this.vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
    }
    
    protected void refreshVDBs() throws Exception {
    	this.vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
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
    protected void refreshTranslators( Collection<Translator> translators ) throws Exception {
        for (Translator translator : translators) {
        	if( translator.getName() != null ) {
        		Collection<PropertyDefinition> propDefs = this.admin.getTemplatePropertyDefinitions(translator.getName());
	            this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, this));
        	}
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
//            this.admin.setTranslatorProperty(translator.getName(), propName, value);

            if (notify) {
            	// TODO: Will we ever update Translator properties in TEIID Server?
                //this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
            }
        } else {
            throw new Exception(Util.getString("invalidPropertyValue", value, propName)); //$NON-NLS-1$
        }
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
            // TODO stop translator??

            for (String name : changedProperties.stringPropertyNames()) {
                internalSetPropertyValue(translator, name, changedProperties.getProperty(name), false);
            }

            // TODO restart translator??

            //this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
        }
    }

}

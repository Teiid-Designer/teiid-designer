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
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.CoreArgCheck;

/**
 *
 */
public class ExecutionAdmin {

    private final Admin admin;
    protected Map<String, TeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Set<String> dataSourceTypeNames;
    private final EventManager eventManager;
    private final Server server;
    private Set<VDB> vdbs;
    
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
        
        refresh();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void load() throws Exception {
    	if( !loaded ) {
    		refresh();
    	}
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
    public boolean getOrCreateDataSource(String name, String typeName, Properties properties) throws Exception {
    	// Check if exists, return false
    	if( this.dataSourceNames.contains(name)) {
    		return false;
    	}
    	
    	// Verify the "typeName" exists.
    	if( !this.dataSourceTypeNames.contains(typeName) ) {
    		throw new Exception(Util.getString("dataSourceTypeDoesNotExist", typeName, getServer().getUrl())); //$NON-NLS-1$
    	}
    	this.admin.createDataSource(name, typeName, properties);
    	
    	if( this.admin.getDataSourceNames().contains(name) ) {
    		this.dataSourceNames.add(name);
    		return true;
    	}
    	
    	throw new Exception(Util.getString("errorCreatingDataSource", name, typeName, getServer().getUrl())); //$NON-NLS-1$
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

        VDB deployedVdb = null; // this.admin.deployVDB(vdb.getName(), vdb);

        return deployedVdb;
    }

    Admin getAdminApi() {
        return this.admin;
    }

    /**
     * @param name the translator name (never <code>null</code> or empty)
     * @return
     * @throws Exception
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
     */
    public VDB getVdb( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$

        for (VDB vdb : this.vdbs) {
            if (vdb.getName().equals(name)) return vdb;
        }

        return null;
    }

    public void refresh() throws Exception {
        this.translatorByNameMap = new HashMap<String, TeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceTypeNames = new HashSet<String>();
        // populate translator map
        refreshTranslators(this.admin.getTranslators());
        
        // populate data source type names set
        dataSourceTypeNames = new HashSet(this.admin.getDataSourceTemplateNames());

        // populate data source names list
        dataSourceNames = new ArrayList(this.admin.getDataSourceNames());

        // populate VDBs and source bindings
        // TODO may need to filter out hidden vdb
        this.vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
    }

    protected void refreshTranslators( Collection<Translator> translators ) throws Exception {
        for (Translator translator : translators) {
        	// TODO: FIX THIS : Remove [!translator.getName().equalsIgnoreCase("file")] code cause it's a hack to get around 
        	// A Teiid Exception
        	if( translator.getName() != null && !translator.getName().equalsIgnoreCase("file")) {
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
                this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
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

            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(translator));
        }
    }

}

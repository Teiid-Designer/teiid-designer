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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.connection.IPasswordProvider;
import org.teiid.designer.runtime.connection.ModelConnectionMatcher;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

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
    // private Set<VDB> vdbs;
    private Set<TeiidVdb> teiidVdbs;
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
        this.connectionMatcher = new ModelConnectionMatcher();

        init();
        // refresh();
    }

    /**
     * @param name
     * @return true if data source exists with the provided name. else false.
     * @throws Exception
     */
    public boolean dataSourceExists( String name ) throws Exception {
        // Check if exists, return false
        if (this.dataSourceNames.contains(name)) {
            return true;
        }

        return false;
    }

    /**
     * Removes the data source from the teiid server (if exists)
     * 
     * @param jndiName
     * @throws Exception
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
     * @param vdb
     * @return
     */
    public VDB deployVdb( IFile vdbFile ) throws Exception {
        CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        String vdbName = vdbFile.getFullPath().lastSegment();
        String vdbNameNoExt = vdbFile.getFullPath().removeFileExtension().lastSegment();

        admin.deployVDB(vdbName, vdbFile.getContents());

        refreshVDBs();

        // TODO should get version from vdbFile
        VDB vdb = admin.getVDB(vdbNameNoExt, 1);

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createDeployVDBEvent(vdb));

        return vdb;
    }

    /**
     * Deploys the input Vdb archive file to the related Teiid server
     * 
     * @param vdb
     * @return
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
    
    Admin getAdminApi() {
        return this.admin;
    }

    /**
     * Returns a teiid data source object if it exists in this server
     * 
     * @param name
     * @return the teiid data source object (can be <code>null</code>)
     */
    public TeiidDataSource getDataSource(String name) {
        return this.dataSourceByNameMap.get(name);
    }
    
    /**
     * REturns a list of
     * 
     * @return
     */
    public Collection<TeiidDataSource> getDataSources() {
        return this.dataSourceByNameMap.values();
    }

    public Set<String> getDataSourceTypeNames() {
        return this.dataSourceTypeNames;
    }

    /**
     * @return the event manager (never <code>null</code>)
     */
    EventManager getEventManager() {
        return this.eventManager;
    }

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
     * @param name
     * @param typeName
     * @param properties
     * @return true if data source is created. false if it already exists
     * @throws Exception
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

        // Verify the "typeName" exists.
        if (!this.dataSourceTypeNames.contains(typeName)) {
            throw new Exception(Util.getString("dataSourceTypeDoesNotExist", typeName, getServer())); //$NON-NLS-1$
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

    /**
     * @return the server who owns this admin object (never <code>null</code>)
     */
    public Server getServer() {
        return this.server;
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
     * @throws Exception
     */
    public void load() throws Exception {
        if (!this.loaded) {
            refresh();
            this.loaded = true;
        }
    }

    /**
     * @param sourceVdbName (excluding .vdb extension) the name of the VDB being merged into the target VDB
     * @param sourceVdbVersion the version of the source VDB
     * @param targetVdbName (excluding .vdb extension) the name of the VDB being merged into
     * @param targetVdbVersion the version of the target VDB
     * @throws Exception if there is a problem with the merge
     */
    public void mergeVdbs( String sourceVdbName,
                           int sourceVdbVersion,
                           String targetVdbName,
                           int targetVdbVersion ) throws Exception {
        this.admin.mergeVDBs(sourceVdbName, sourceVdbVersion, targetVdbName, targetVdbVersion);
    }

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
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this.server));
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
            if (translator.getName() != null) {
                Collection<PropertyDefinition> propDefs = this.admin.getTemplatePropertyDefinitions(translator.getName());
                this.translatorByNameMap.put(translator.getName(), new TeiidTranslator(translator, propDefs, this));
            }
        }
    }

    protected void refreshVDBs() throws Exception {
        Set<VDB> vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
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

    public void undeployVdb( String vdbName,
                             int vdbVersion ) throws Exception {
        this.admin.deleteVDB(vdbName, vdbVersion);
        VDB vdb = getVdb(vdbName);

        refreshVDBs();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb));
        }
    }

    /**
     * @param vdb
     * @return
     */
    public void undeployVdb( VDB vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$

        admin.deleteVDB(vdb.getName(), vdb.getVersion());

        refreshVDBs();

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb));
    }

}

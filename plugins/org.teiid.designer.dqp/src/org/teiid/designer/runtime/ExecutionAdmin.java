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
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.connection.IConnectionProperties;
import org.teiid.designer.runtime.connection.ModelConnectionMatcher;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;

/**
 *
 */
public class ExecutionAdmin {

    private final Admin admin;
    protected Map<String, TeiidTranslator> translatorByNameMap;
    protected Collection<String> dataSourceNames;
    protected Map<String, TeiidDataSource> dataSourceByNameMap;
    protected Map<String, TeiidDataSource> workspaceDataSourceByNameMap;
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
        this.connectionMatcher = new ModelConnectionMatcher(this);

        init();
        // refresh();
    }

    private void init() throws Exception {
        this.translatorByNameMap = new HashMap<String, TeiidTranslator>();
        this.dataSourceNames = new ArrayList<String>();
        this.dataSourceByNameMap = new HashMap<String, TeiidDataSource>();
        this.workspaceDataSourceByNameMap = new HashMap<String, TeiidDataSource>();
        this.dataSourceTypeNames = new HashSet<String>();
        refreshVDBs();
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

    public Collection<TeiidDataSource> getDataSources() {
        return this.dataSourceByNameMap.values();
    }

    public Set<String> getDataSourceTypeNames() {
        return this.dataSourceTypeNames;
    }

    public TeiidDataSource getOrCreateDataSource( IFile model,
                                                  String jndiName ) throws Exception {
        String displayName = model.getFullPath().removeFileExtension().lastSegment();
        ModelResource modelResource = ModelUtil.getModelResource(model, true);
        ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
        IConnectionInfoProvider connInfoProvider = manager.getProvider(modelResource);
        Properties props = connInfoProvider.getConnectionProperties(modelResource);
        String dsName = connInfoProvider.getDataSourceType();

        // make sure the password property is there. if not get from connection profile.
        if (!props.isEmpty() && props.getProperty(connInfoProvider.getPasswordPropertyKey()) == null) {
            IConnectionProfile connectionProfile = connInfoProvider.getConnectionProfile(modelResource);

            if (connectionProfile == null) {
                throw new Exception(Util.getString("errorCreatingDataSource", //$NON-NLS-1$
                                                   jndiName,
                                                   IConnectionProperties.JDBC_DS_TYPE,
                                                   getServer().getUrl()));
            }

            connectionProfile = ProfileManager.getInstance().getProfileByName(connectionProfile.getName());
            
            if (connectionProfile != null) {
                connectionProfile.getBaseProperties().getProperty(connInfoProvider.getPasswordPropertyKey());
                props.setProperty(connInfoProvider.getPasswordPropertyKey(),
                                  connectionProfile.getBaseProperties().getProperty(connInfoProvider.getPasswordPropertyKey()));
                return getOrCreateDataSource(displayName, dsName, IConnectionProperties.JDBC_DS_TYPE, props);
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
            if (tds == null) {
                tds = this.workspaceDataSourceByNameMap.get(jndiName);
            }
            return tds;
        }

        // Verify the "typeName" exists.
        if (!this.dataSourceTypeNames.contains(typeName)) {
            throw new Exception(Util.getString("dataSourceTypeDoesNotExist", typeName, getServer().getUrl())); //$NON-NLS-1$
        }

        this.admin.createDataSource(jndiName, typeName, properties);

        refreshDataSourceNames();

        // Check that local name list contains new jndiName
        if (dataSourceExists(jndiName)) {
            TeiidDataSource tds = connectionMatcher.findTeiidDataSource(jndiName, this);

            if (tds != null) {
                this.workspaceDataSourceByNameMap.put(jndiName, tds);
                this.dataSourceByNameMap.put(jndiName, tds);
                this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));
            }
            return tds;
        }

        // We shouldn't get here if data source was created
        throw new Exception(Util.getString("errorCreatingDataSource", jndiName, typeName, getServer().getUrl())); //$NON-NLS-1$
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

    public void deleteDataSource( String jndiName ) throws Exception {
        // Check if exists, return false
        if (this.dataSourceNames.contains(jndiName)) {
            this.admin.deleteDataSource(jndiName);

            if (!this.admin.getDataSourceNames().contains(jndiName)) {
                this.dataSourceNames.remove(jndiName);
                TeiidDataSource tds = this.dataSourceByNameMap.get(jndiName);
                if (tds == null) {
                    tds = this.workspaceDataSourceByNameMap.get(jndiName);
                }
                if (tds != null) {
                    this.dataSourceByNameMap.remove(jndiName);
                    this.workspaceDataSourceByNameMap.remove(jndiName);
                    this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveDataSourceEvent(tds));

                }
            }
        }
        //    	
        // // TODO: I18n
        // throw new Exception(Util.getString("errorDeletingDataSource", jndiName, getServer().getUrl()));
    }

    /**
     * @param vdb
     * @return
     */
    public VDB deployVdb( Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$
        return deployVdb(vdb.getFile());
    }

    /**
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

    public Collection<TeiidDataSource> getWorkspaceDataSources() {
        return this.workspaceDataSourceByNameMap.values();
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
    public Set<TeiidVdb> getVdbs() {
        return this.teiidVdbs;
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

        Collection<TeiidDataSource> tdsList = connectionMatcher.findWorkspaceTeiidDataSources(this);
        for (TeiidDataSource ds : tdsList) {
            this.workspaceDataSourceByNameMap.put(ds.getName(), ds);
        }

        tdsList = connectionMatcher.findTeiidDataSources(this.dataSourceNames, this);
        for (TeiidDataSource ds : tdsList) {
            this.dataSourceByNameMap.put(ds.getName(), ds);
        }

        // populate VDBs and source bindings
        // TODO may need to filter out hidden vdb
        refreshVDBs();
    }

    protected void refreshVDBs() throws Exception {
        Set<VDB> vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
        Set<TeiidVdb> tmpVdbs = new HashSet();

        for (VDB vdb : vdbs) {
            tmpVdbs.add(new TeiidVdb(vdb, this));
        }

        this.teiidVdbs = Collections.unmodifiableSet(tmpVdbs);
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

}

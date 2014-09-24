/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.translators.TranslatorOverride;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.connection.TranslatorUtils;
import org.teiid.designer.runtime.importer.ImportManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.panels.PropertyItem;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *  TeiidImportManager
 *  manager object for use with the TeiidImportWizard
 *  
 *  @since 8.1
 */
public class TeiidImportManager implements ITeiidImportServer, UiConstants {

    private static final String PREVIEW_DATASOURCE_PREFIX = "PREVIEW_";  //$NON-NLS-1$
    
    private IPath targetModelLocation = null;
    private String targetModelName = null;
    private String translatorName = null;
    private String dataSourceName = null;
    private String dataSourceJndiName = null;
    private String dataSourceDriverName = null;
    private Properties dataSourceProps = null;
    private Map<String,String> optionalImportProps = new HashMap<String,String>();
    private boolean createConnectionProfile = true;
    private TranslatorOverride translatorOverride;
    
    IStatus vdbDeploymentStatus = null;
    private ConnectionInfoHelper connectionInfoHelper = new ConnectionInfoHelper();
    private DdlImporter ddlImporter;
    private File ddlFile;
    private String uniqueImportVdbName;
    private boolean redeploy = false;
    
    /**
     * Set the data source name
     * @param dsName the data source name
     */
    public void setDataSourceName(String dsName) {
        // If different datasource is selected, reset deployment status
        if(areDifferent(this.dataSourceName,dsName)) {
            this.vdbDeploymentStatus = null;
        }
        this.dataSourceName = dsName;
    }
    
    /**
     * Get the current DataSource name
     * @return the DataSource name
     */
    public String getDataSourceName() {
        return this.dataSourceName;
    }
    
    /**
     * Set the data source JNDI name
     * @param dsJndiName the data source JDNI name
     */
    public void setDataSourceJndiName(String dsJndiName) {
        this.dataSourceJndiName = dsJndiName;
    }
    
    /**
     * Get the current DataSource jndi name
     * @return the jndi name
     */
    public String getDataSourceJndiName() {
        return this.dataSourceJndiName;
    }
    
    /**
     * Set the DataSource driver name.  Whenever it's reset, set deployment status to invalid so that user
     * must reValidate
     * @param driverName the data source driver name
     */
    public void setDataSourceDriverName(String driverName) {
        // If different driver is selected, reset deployment status
        if(areDifferent(this.dataSourceDriverName,driverName)) {
            this.vdbDeploymentStatus = null;
        }
        this.dataSourceDriverName = driverName;
    }
    
    /**
     * Get the current DataSource driver name
     * @return the DataSource driver name
     */
    public String getDataSourceDriverName() {
        return this.dataSourceDriverName;
    }
    
    /**
     * Set the DataSource properties.  Whenever it's reset, set deployment status to invalid so that user
     * must reValidate
     * @param props the data source properties
     */
    public void setDataSourceProperties(Properties props) {
        this.dataSourceProps = props;
        this.vdbDeploymentStatus = null;
    }
    
    /**
     * Get the current DataSource properties
     * @return the DataSource properties
     */
    public Properties getDataSourceProperties() {
        return this.dataSourceProps;
    }
    
    /**
     * @return the translatorName
     */
    public String getTranslatorName() {
        return this.translatorName;
    }

    /**
     * @param translatorName the translatorName to set
     */
    public void setTranslatorName(String translatorName) {
        // If different translator is selected, reset deployment status
        if(areDifferent(this.translatorName,translatorName)) {
            this.vdbDeploymentStatus = null;
            if( translatorName != null ) {
            	this.translatorOverride = createTranslatorOverride(translatorName, new Properties());
            } else {
            	// TO make sure the wrong override property isn't cached when translatorName set to NULL
            	this.translatorOverride = null;
            }
        }
        this.translatorName = translatorName;
    }
    
    /**
     * Add Optional Import Property
     * @param name the optional property name
     * @param value the optional property value
     */
    public void addOptionalImportProperty(String name, String value) {
    	this.optionalImportProps.put(name,value);
    	this.vdbDeploymentStatus = null;
    }
    
    /**
     * Remove Optional Import Property
     * @param name the optional property name
     */
    public void removeOptionalImportProperty(String name) {
        this.optionalImportProps.remove(name);
        this.vdbDeploymentStatus = null;
    }
    
    /**
     * @return the optional properties map
     */
    public Map<String,String> getOptionalImportProps() {
        Map<String, String> allProps = new HashMap<String, String>();
        for( String key : this.optionalImportProps.keySet()) {
        	allProps.put(key, this.optionalImportProps.get(key));
        }
        
        // Add import properties from override object
        if( translatorOverride != null && this.translatorOverride.getProperties().length > 0 ) {
        	// process them
        	for( TranslatorOverrideProperty prop : this.translatorOverride.getProperties() ) {
        		if( prop.hasOverridenValue() ) {
        			String value = prop.getOverriddenValue();
        			String key = prop.getDefinition().getId();
        			allProps.put(key, value);
        		}
        	}
        }
        
        return allProps;
    }

    /**
     * Determine if the Importer Server is Valid
     * @return 'true' if we have a valid server, 'false' if not.
     */
    public boolean isValidImportServer() {
        return getServerImportManager().isValidImportServer();
    }
    
    /**
     * Deploy a dynamic VDB using the current DataSource and Translator
     * @return the deployment status
     */
    public IStatus deployDynamicVdb() {
        vdbDeploymentStatus = null;
 
        final String translatorName = getTranslatorName();
        final String dataSourceName = getDataSourceName();
        final Map<String,String> optionalImportPropMap = getOptionalImportProps();
        boolean infoGood = false;
        if(translatorName!=null && dataSourceName!=null) {
            infoGood=true;
        }
        // Create Runnable if the profile is valid
        if(isValidImportServer() && infoGood) {

            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        monitor.beginTask(NLS.bind(Messages.TeiidImportManager_deployVdbMsg, getTimeoutPrefSecs()), 100); 
                        vdbDeploymentStatus = getServerImportManager().deployDynamicVdb(getCurrentImportVdbName(),dataSourceName,translatorName,optionalImportPropMap,monitor); 
                    } catch (Throwable e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            };
            
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                vdbDeploymentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
                UTIL.log(vdbDeploymentStatus);
            } catch (InterruptedException e) {
                vdbDeploymentStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.TeiidImportManager_deployVdbInterruptedMsg);
                UTIL.log(vdbDeploymentStatus);
            }
        }
        
        return vdbDeploymentStatus;
    }
    
    /**
     * Get the DynamicVdb xml string
     * @return the xml string
     */
    public String getDynamicVdbString() {
        return getServerImportManager().createDynamicVdbString(getCurrentImportVdbName(), dataSourceName, translatorName, getOptionalImportProps());
    }

    private String getTimeoutPrefSecs() {
        String timeoutStr = DqpPlugin.getInstance().getPreferences().get(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
        try {
			Integer.parseInt(timeoutStr);
		} catch (NumberFormatException ex1) {
			timeoutStr = PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT;
		}
        return timeoutStr;
    }
    
    /**
     * Undeploy the dynamic VDB and datasource
     * @return the deployment status
     */
    public IStatus undeployDynamicVdb() {
        this.vdbDeploymentStatus = null;
        String undeployVdbName = getCurrentImportVdbName();
        this.uniqueImportVdbName = null;
        return getServerImportManager().undeployVdb(undeployVdbName);
    }
    
    /**
     * Inject the ConnectionProfile into the target model.
     * @param monitor the progress monitor
     */
    private boolean injectProfileIntoTarget(final IProgressMonitor monitor) {
        if( this.targetModelLocation == null ) {
            return false;
        }
        
        IPath modelPath = new Path(targetModelLocation.toOSString()).append(this.targetModelName);
        if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
            modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
        }
        
        IResource targetModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
        ModelResource targetModelResc = ModelUtilities.getModelResourceForIFile((IFile)targetModel, false);
        if( targetModelResc!=null) {
        	
        	ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
        	IConnectionInfoProvider connInfoProvider = manager.getProviderFromProfileID("org.teiid.designer.datatools.profiles.jbossds.JBossDsConnectionProfile");  //$NON-NLS-1$
            ProfileManager pm = ProfileManager.getInstance();
            Properties props = new Properties();
            props.put(IJBossDsProfileConstants.JNDI_PROP_ID, getDataSourceJndiName()); 
            props.put(IJBossDsProfileConstants.TRANSLATOR_PROP_ID, getTranslatorName());
            try {
            	String dsName = getDataSourceName();
            	String cpName = "TeiidImportCP_" + dsName; //$NON-NLS-1$
            	// If connection profile with this name exists, delete it first
            	IConnectionProfile cp = pm.getProfileByName(cpName); 
            	if(cp!=null) pm.deleteProfile(cp);
            	
            	// Create a 'JBossDs' profile for use with this importer.  The only properties are the source JNDI name and translator.
				cp = pm.createProfile(cpName, "JBoss DS Profile", "org.teiid.designer.datatools.profiles.jbossds.JBossDsConnectionProfile", props); //$NON-NLS-1$ //$NON-NLS-2$ 
				connInfoProvider.setConnectionInfo(targetModelResc,cp);
			} catch (Exception ex) {
	            UTIL.log(ex);
			}
        	
            try {
            	targetModelResc.save(monitor, true);
            } catch (Exception error) {
                ErrorHandler.toExceptionDialog(error);
            }
            return true;
        }
       
        return false;
    }
    
    /**
     * Return the schema DDL for the currently deployed dynamic import VDB
     * @return the schema DDL
     */
    public String getDdl( ) {
    	boolean success = true;
    	
        String ddl = null;
        try {
            ddl = getServerImportManager().getSchema(getCurrentImportVdbName());
        } catch (Exception ex) {
            UTIL.log(ex);
        	ddl = Messages.TeiidImportManager_getDdlErrorMsg;
        	success = false;
        }
                
        String modifiedDdl = null;
        // If successful getting the DDL, write it to the temp file
        try {
        	if(success) {
                // TEIIDDES-2127 - With 8.7, teiid started to schema-qualify the constraint references.
                // Now we must modify the DDL to remove qualifiers - since we will never have cross-schema references with this import.
        		if(ddl!=null) {
        			String importVdbSourceModel = getCurrentImportVdbName() + ImportManager.IMPORT_SRC_MODEL + "."; //$NON-NLS-1$
        			modifiedDdl = ddl.replaceAll(importVdbSourceModel, ""); //$NON-NLS-1$
        		}
                
        		writeDdlToTempFile(modifiedDdl);
        	} else {
        		writeDdlToTempFile(""); //$NON-NLS-1$
        	}
        } catch (Exception ex) {
            UTIL.log(ex);
            WidgetUtil.showError(ex);
        }
        return modifiedDdl;
    }
    
    /*
     * Determine if two string values are different
     * @param str1 string1
     * @param str2 string2
     * @return 'true' if the strings are different, 'false' if not
     */
    private boolean areDifferent(String str1, String str2) {
        // str1 is empty, but str2 is not
        if(CoreStringUtil.isEmpty(str1) && !CoreStringUtil.isEmpty(str2)) {
            return true;
        }
        // str2 is empty, but str1 is not
        if(CoreStringUtil.isEmpty(str2) && !CoreStringUtil.isEmpty(str1)) {
            return true;
        }
        // both empty
        if(CoreStringUtil.isEmpty(str1) && CoreStringUtil.isEmpty(str2)) {
            return false;
        }
        // both empty
        if(str1.equalsIgnoreCase(str2)) {
            return false;
        }
        return true;
    }
    
    /**
     * Return Temporary DDL so we can test the wizard for now.
     * @return the temporary DDL
     */
    public String getTemporaryDDL() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("CREATE TABLE ACCOUNT\n"); //$NON-NLS-1$
        sb.append("(\n"); //$NON-NLS-1$
        sb.append("ACCOUNT_ID   NUMBER(10) DEFAULT ('0') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("SSN          CHAR(10),\n"); //$NON-NLS-1$
        sb.append("STATUS       CHAR(10),\n"); //$NON-NLS-1$
        sb.append("TYPE         CHAR(10),\n"); //$NON-NLS-1$
        sb.append("DATEOPENED   DATE DEFAULT ('CURRENT_TIMESTAMP') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("DATECLOSED   DATE DEFAULT ('0000-00-00 00:00:00') NOT NULL\n"); //$NON-NLS-1$
        sb.append(");\n"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        sb.append("CREATE TABLE HOLDINGS\n"); //$NON-NLS-1$
        sb.append("(\n"); //$NON-NLS-1$
        sb.append("TRANSACTION_ID   NUMBER(10) NOT NULL,\n"); //$NON-NLS-1$
        sb.append("ACCOUNT_ID       NUMBER(10),\n"); //$NON-NLS-1$
        sb.append("PRODUCT_ID       NUMBER(10),\n"); //$NON-NLS-1$
        sb.append("PURCHASE_DATE    DATE DEFAULT ('CURRENT_TIMESTAMP') NOT NULL,\n"); //$NON-NLS-1$
        sb.append("SHARES_COUNT     NUMBER(10)\n"); //$NON-NLS-1$
        sb.append(");\n"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        
        return sb.toString();
    }

    /**
     * Get the server ImportManager instance
     * @return the ImportManager
     */
    public ImportManager getServerImportManager() {
        return ImportManager.getInstance();
    }

    /**
     * Determine if the VDB is Deployed
     * @return 'true' if deployed, 'false' if not.
     */
    public boolean isVdbDeployed() {
        return (this.vdbDeploymentStatus!=null && this.vdbDeploymentStatus.isOK()) ? true : false;
    }
    
    /**
     * Get the VDB Deployment status.
     * @return the Status
     */
    public IStatus getVdbDeploymentStatus() {
        return this.vdbDeploymentStatus;
    }
    
    /**
     * Set the Target Model Location
     * @param targetPath the location path for the target
     */
    public void setTargetModelLocation(IPath targetPath) {
        this.targetModelLocation=targetPath;
        if(this.ddlImporter!=null) {
            this.ddlImporter.setModelFolder(targetPath.toString());
        }
    }
    
    /**
     * Get the current target path
     * @return the path for the target model
     */
    public IPath getTargetModelLocation() {
        return this.targetModelLocation;
    }
    
    /**
     * Set the Target Model Name
     * @param targetModelName the name for the target model
     */
    public void setTargetModelName(String targetModelName) {
        this.targetModelName=targetModelName;
        if(this.ddlImporter!=null) {
            this.ddlImporter.setModelName(targetModelName);
        }
    }    
    
    /**
     * Get the current target model name
     * @return the name for the target model
     */
    public String getTargetModelName() {
        return this.targetModelName;
    }
        
    /**
     * Determine if the target model already exists in the workspace
     * @return 'true' if the model exists, 'false' if not.
     */
    public boolean targetModelExists() {
        if( this.targetModelLocation == null || this.targetModelName == null ) {
            return false;
        }
        
        IPath modelPath = new Path(targetModelLocation.toOSString()).append(this.targetModelName);
        if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
            modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
        }
        
        ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
        if( item != null ) {
            return true;
        }
            
        return false;
    }
    
    /**
     * Get the CreateConnectionProfile flag
	 * @return 'true' if a connection profile is to be created, 'false' if not.
	 */
	public boolean isCreateConnectionProfile() {
		return this.createConnectionProfile;
	}

	/**
	 * Set the CreateConnectionProfile status flag
	 * @param createConnectionProfile 'true' if a connection profile is to be created
	 */
	public void setCreateConnectionProfile(boolean createConnectionProfile) {
		this.createConnectionProfile = createConnectionProfile;
	}

    /**
     * Determine if the Target Model's Connection Profile is compatible with the currently selected data source.
     * If targetModel has a connection profile:
     *   - allow import if connection-url are same
     * If targetModel does not have a connection profile:
     *   - allow the import
     * In either case, this importer does not inject connection properties into the model that is produced.
     * @return 'true' if compatible, 'false' if not.
     */
    public boolean isTargetModelConnectionProfileCompatible() {
        if( this.targetModelLocation == null ) {
            return false;
        }
        
        IPath modelPath = new Path(targetModelLocation.toOSString()).append(this.targetModelName);
        if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
            modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
        }
        
        IResource targetModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
        ModelResource targetModelResc = ModelUtilities.getModelResourceForIFile((IFile)targetModel, false);
        if( targetModelResc != null ) {
            IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(targetModelResc);
        
            // No connection profile for target model - allow the import
            if( profile == null) {
                return true;
            } else {
                // Get the connection profile URL from target Model props
                Properties profileProps = profile.getBaseProperties();
                String targetModelUrl = profileProps.getProperty(PropertyItem.CONNECTION_URL_DISPLAYNAME);
                if (targetModelUrl==null) targetModelUrl = profileProps.getProperty(PropertyItem.CONNECTION_ENDPOINT_DISPLAYNAME);
                
                // Get the importer DataSource Url property
                Properties importDsProps = getDataSourceProperties();
                String importDsUrl = null;
                if(importDsProps!=null) {
                    importDsUrl = importDsProps.getProperty(PropertyItem.CONNECTION_URL_DISPLAYNAME);
                    if(importDsUrl==null) {
                        importDsUrl = importDsProps.getProperty(PropertyItem.CONNECTION_ENDPOINT_DISPLAYNAME);
                    }
                }
                if(importDsUrl!=null && importDsUrl.equalsIgnoreCase(targetModelUrl)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#deleteDataSource(java.lang.String)
     */
    @Override
    public void deleteDataSource(String jndiName) throws Exception {
        getServerImportManager().deleteDataSource(jndiName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#deployDriver(java.io.File)
     */
    @Override
    public void deployDriver(File jarOrRarFile) throws Exception {
        getServerImportManager().deployDriver(jarOrRarFile);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getSchema(java.lang.String, int, java.lang.String)
     */
    @Override
    public String getSchema(String vdbName,
                            int vdbVersion,
                            String modelName) throws Exception {
        return getServerImportManager().getSchema(vdbName, vdbVersion, modelName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSources()
     */
    @Override
    public Collection<ITeiidDataSource> getDataSources() throws Exception {
    	// Filters the PREVIEW sources from the results
    	Collection<ITeiidDataSource> resultSources = new ArrayList<ITeiidDataSource>();
        Collection<ITeiidDataSource> teiidSources = getServerImportManager().getDataSources();
        for(ITeiidDataSource dSource : teiidSources) {
        	String sourceName = dSource.getName();
        	if(sourceName!=null && !sourceName.startsWith(PREVIEW_DATASOURCE_PREFIX)) {
        		resultSources.add(dSource);
        	}
        }
        return resultSources;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSourceTemplateNames()
     */
    @Override
    public Set<String> getDataSourceTemplateNames() throws Exception {
        return getServerImportManager().getDataSourceTemplateNames();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getTemplatePropertyDefns(java.lang.String)
     */
    @Override
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
        return getServerImportManager().getTemplatePropertyDefns(templateName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getOrCreateDataSource(java.lang.String, java.lang.String, java.lang.String, java.util.Properties)
     */
    @Override
    public ITeiidDataSource getOrCreateDataSource(String displayName,
                                                  String jndiName,
                                                  String typeName,
                                                  Properties properties) throws Exception {
        return getServerImportManager().getOrCreateDataSource(displayName, jndiName, typeName, properties);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getTranslators()
     */
    @Override
    public Collection<ITeiidTranslator> getTranslators() throws Exception {
        return getServerImportManager().getTranslators();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDisplayName()
     */
    @Override
    public String getDisplayName() throws Exception {
        return getServerImportManager().getDisplayName();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#undeployDynamicVdb(java.lang.String)
     */
    @Override
    public void undeployDynamicVdb(String vdbName) throws Exception {
        getServerImportManager().undeployDynamicVdb(vdbName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.wizard.ITeiidImportServer#getDataSourceProperties(java.lang.String)
     */
    @Override
    public Properties getDataSourceProperties(String sourceName) throws Exception {
        return getServerImportManager().getDataSourceProperties(sourceName);
    }
    
	/* (non-Javadoc)
	 * @see org.teiid.designer.teiidimporter.ui.wizard.ITeiidImportServer#getTeiidServerVersion()
	 */
	@Override
	public ITeiidServerVersion getTeiidServerVersion() throws Exception {
    	return getServerImportManager().getServerVersion();
	}
    
    // ----------------------------------------------------------------------------
    // DDL Import functionality
    // ----------------------------------------------------------------------------
    
    /**
     * Initialize the DdlImporter
     * @param projects the open projects
     */
    public void initDdlImporter(IProject[] projects) {
        ddlImporter = new DdlImporter(projects);
        ddlImporter.setModelType(ModelType.PHYSICAL_LITERAL);
        try {
            ddlFile = File.createTempFile("DdlTemp", ".ddl"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IOException ex) {
            UTIL.log(ex);
        }
        this.ddlImporter.setDdlFileName(ddlFile.getAbsolutePath().toString());
        // Specify the Teiid Parser
        this.ddlImporter.setSpecifiedParser("TEIID"); //$NON-NLS-1$
    }
    
    /**
     * Get the DdlImporter instance
     * @return the DdlImporter instance
     */
    public DdlImporter getDdlImporter() {
        return ddlImporter;
    }
    
    /**
     * Get the TranslatorOverride
     * @return the TranslatorOverride
     */
    public TranslatorOverride getTranslatorOverride() {
    	return this.translatorOverride;
    }
    
    /*
     * Writes the DDL String to a Temporary File, to pass to the DDL Importer
     * @param ddl the DDL string
     * @return the temp file that was created.
     */
    private void writeDdlToTempFile(String ddl) throws Exception {
        if(ddlFile!=null && ddlFile.canWrite()) {
            FileOutputStream tempOutputStream = new FileOutputStream(ddlFile);
            PrintStream out = null;
            try {
                out = new PrintStream(tempOutputStream);
                out.print(ddl);
            }
            finally {
                if (out != null) out.close();
            }
        }
    }
    
    /**
     * Delete the DDL temp file, if it exists
     */
    public void deleteDdlTempFile() {
        // Delete the temp DDL file
      if(ddlFile!=null && ddlFile.exists()) {
          ddlFile.delete();
      }
    }
    
    /**
     * Save the Model, using the DDL Difference Report
     * @param shell the shell
     * @return 'true' if the operation was successful, 'false' if not.
     */
    public boolean saveUsingDdlDiffReport(Shell shell) {
        try {
        	// Use the importer to process the difference report, generating the model
            if (ddlImporter.getDifferenceReport() == null) return false;

            final Exception[] saveException = new Exception[1];
            new ProgressMonitorDialog(shell).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    monitor.beginTask(Messages.TeiidImportManager_ImportingMsg, 100);
                    monitor.worked(50);
                    try {
                        ddlImporter.save(monitor, 50);
                    } catch (Exception ex) {
                        saveException[0] = ex;
                    }
                    // Create a ConnectionProfile and injects into model
                    if(isCreateConnectionProfile()) {
                    	injectProfileIntoTarget(monitor);
                    }
                    monitor.done();
                }
            });

            if (saveException[0] != null)
                throw saveException[0];

        } catch (final InterruptedException error) {
            undeployDynamicVdb();
            return false;
        } catch (final Exception error) {
            ErrorHandler.toExceptionDialog(error);
            undeployDynamicVdb();
            return false;
        }
        
        return true;
    }
    
    private TranslatorOverride createTranslatorOverride(String translatorName, Properties existingProperties) {
    	Properties importerProperties = new Properties();
    	TranslatorOverride override = new TranslatorOverride(translatorName, importerProperties);
    	
    	try {
			ITeiidTranslator translator = this.getServerImportManager().getTranslator(translatorName);

			if (translator != null) {
				PropertyDefinition[] propertyDefinitionsFromServer = 
						TranslatorUtils.getTranslatorPropertyDefinitions(translatorName, ITeiidTranslator.TranslatorPropertyType.IMPORT);
				
			    List<PropertyDefinition> defaultServerPropDefns = new ArrayList<PropertyDefinition>();
	            // assume all server properties are new
	            for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
	            	//System.out.println("propDefn ID = " + propDefn.getId() + " Display Name = " + propDefn.getDisplayName());
	            	defaultServerPropDefns.add(propDefn);
	            }

			    if (!existingProperties.isEmpty()) {
			        // translator properties already exist, match with server props
			        for (Object key : importerProperties.keySet()) {
			        	String keyStr = (String)key;
			        	String value = (String)importerProperties.get(key);
			        	
			            PropertyDefinition serverPropDefn = null;

			            // see if property definitions from server already exist in overridden translator
			            for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
			                // found a matching one
			                if (keyStr.equals(propDefn.getId())) {
			                    serverPropDefn = propDefn;
			                    defaultServerPropDefns.remove(serverPropDefn); // Remove it from cached list
			                    break;
			                }
			            }

			            if (serverPropDefn != null) {
			            	TranslatorOverrideProperty newProp = new TranslatorOverrideProperty(new TranslatorPropertyDefinition(serverPropDefn), value);
			                // found existing property so update defn and use value from old defn
			            	override.addProperty(newProp);
			            }
			        }
			    }
			    
			    for (PropertyDefinition propDefn : defaultServerPropDefns) {
			    	override.addProperty(new TranslatorOverrideProperty(new TranslatorPropertyDefinition(propDefn), null));
			    }
			}
		} catch (Exception error) {
			error.printStackTrace();
            WidgetUtil.showError(error);
		}
        
        return override;
    }
    
    /*
     * This method provides a means to check the server for existing import VDB's that may exist due to other users importing through Designer/Teiid
     * 
     * It checks for default name, then appends a numeric value to the name
     */
    private String getCurrentImportVdbName() {
    	if( uniqueImportVdbName == null ) {
    		String importVdbName = Messages.TeiidImportManager_ImportVDBName;
    		// Does it exist on the server?
    		int count = 1;
    		boolean vdbExists = getServerImportManager().vdbExists(importVdbName);
    		while (vdbExists ) {
    			importVdbName = Messages.TeiidImportManager_ImportVDBName + count;
    		
    			vdbExists = getServerImportManager().vdbExists(importVdbName);
    			if( count > 100 ) { // SHOULD NEVER EVER HAVE MORE THAN A COUPLE ON A SERVER.. so 100 should be enough
    				break;
    			}
    			count++;
    		}
    		uniqueImportVdbName = importVdbName;
    	}
    	
    	return uniqueImportVdbName;
    }
    
    public boolean shouldRedeploy() {
    	return redeploy;
    }
    
    public void setRedeploy(boolean changed) {
    	redeploy = changed;
    }
    
}

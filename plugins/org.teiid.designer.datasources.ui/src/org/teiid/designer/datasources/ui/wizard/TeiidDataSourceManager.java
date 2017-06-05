/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.wizard;

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
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.datasources.ui.panels.PropertyItem;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.connection.TranslatorUtils;
import org.teiid.designer.runtime.importer.ImportManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *  TeiidImportManager
 *  manager object for use with the TeiidImportWizard
 *  
 *  @since 8.1
 */
public class TeiidDataSourceManager implements ITeiidImportServer, UiConstants {

    private static final String PREVIEW_DATASOURCE_PREFIX = "PREVIEW_";  //$NON-NLS-1$
    
    private String dataSourceName = null;
    private String dataSourceJndiName = null;
    private String dataSourceDriverName = null;
    private Properties dataSourceProps = null;
    private Map<String,String> optionalImportProps = new HashMap<String,String>();
    private boolean createConnectionProfile = true;
    
    IStatus vdbDeploymentStatus = null;
    private String translatorName = null;
    private TranslatorOverride translatorOverride;

    
    /**
     * Set the data source name
     * @param dsName the data source name
     */
    public void setDataSourceName(String dsName) {
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
     * Determine if the Importer Server is Valid
     * @return 'true' if we have a valid server, 'false' if not.
     */
    public boolean isValidImportServer() {
        return getServerImportManager().isValidImportServer();
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
     * Get the server ImportManager instance
     * @return the ImportManager
     */
    public ImportManager getServerImportManager() {
        return ImportManager.getInstance();
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
                            String vdbVersion,
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
        try {
			Collection<ITeiidDataSource> teiidSources = getServerImportManager().getDataSources();
			for(ITeiidDataSource dSource : teiidSources) {
				String sourceName = dSource.getName();
				if(sourceName!=null && !sourceName.startsWith(PREVIEW_DATASOURCE_PREFIX)) {
					resultSources.add(dSource);
				}
			}
		} catch (Exception e) {
			if( e.getMessage() != null && !e.getMessage().contains("No Teiid Instance found") ) {
				UiConstants.UTIL.log(e);
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
     * Get the TranslatorOverride
     * @return the TranslatorOverride
     */
    public TranslatorOverride getTranslatorOverride() {
    	return this.translatorOverride;
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
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.teiid.designer.ExtensionRegistryUtils;
import org.teiid.designer.IExtensionRegistryCallback;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.connection.spi.IPasswordProvider;
import org.teiid.designer.runtime.spi.FailedTeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 * @since 8.0
 */
public class TeiidDataSourceFactory {

    /**
     * Get the password provider if one has been set
     *
     * @return the password provider
     * @throws Exception
     */
    public static IPasswordProvider getPasswordProvider() throws Exception {

        final List<IPasswordProvider> passwordProviders = new ArrayList<IPasswordProvider>();
        IExtensionRegistryCallback<IPasswordProvider> callback = new IExtensionRegistryCallback<IPasswordProvider>() {

            @Override
            public String getExtensionPointId() {
                return IPasswordProvider.PASSWORD_PROVIDER_EXTENSION_POINT_ID;
            }

            @Override
            public String getElementId() {
                return IPasswordProvider.PASSWORD_PROVIDER_ELEMENT_ID;
            }

            @Override
            public String getAttributeId() {
                return CLASS_ATTRIBUTE_ID;
            }

            @Override
            public boolean isSingle() {
                return true;
            }

            @Override
            public void process(IPasswordProvider instance, IConfigurationElement element) {
                passwordProviders.add(instance);
            }
        };

        ExtensionRegistryUtils.createExtensionInstances(callback);

        if (passwordProviders.isEmpty()) {
            DqpPlugin.Util.log("No password provider implementation installed."); //$NON-NLS-1$
            return null;
        }

        return passwordProviders.get(0);
    }

    /**
     * 
     * Create a data source for the given server. If a data source with the given
     * jndi name already exists then this is returned.
     * 
     * @param teiidServer
     * @param model
     * @param jndiName
     * @param previewVdb
     * 
     * @return a data source with the given jndi name
     * 
     * @throws Exception
     */
    public ITeiidDataSource createDataSource( ITeiidServer teiidServer, 
                                                   IFile model,
                                                   String jndiName,
                                                   boolean previewVdb) throws Exception {
  
         // first check to see if DS with that name already exists
         ITeiidDataSource dataSource = teiidServer.getDataSource(jndiName);

         if (dataSource != null) {
             return dataSource;
         }

         // need to create a DS
         ModelResource modelResource = ModelUtil.getModelResource(model, true);
         ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
         
         if(! manager.hasProvider(modelResource) ) {
        	 return new FailedTeiidDataSource(
        			 model.getFullPath().removeFileExtension().lastSegment(),
        			 jndiName, ITeiidDataSource.ERROR_CODES.NO_CONNECTION_PROVIDER);
         }
         
         IConnectionInfoProvider connInfoProvider = manager.getProvider(modelResource);
         if (connInfoProvider == null)
             return null;

         IConnectionProfile modelConnectionProfile = connInfoProvider.getConnectionProfile(modelResource);
         
         if (modelConnectionProfile == null)
             return new FailedTeiidDataSource(
        			 model.getFullPath().removeFileExtension().lastSegment(),
        			 jndiName, ITeiidDataSource.ERROR_CODES.NO_CONNECTION_PROFILE_DEFINED_IN_MODEL);
         
         Properties props = connInfoProvider.getTeiidRelatedProperties(modelConnectionProfile);
         if (props.isEmpty())
             return new FailedTeiidDataSource(
        			 model.getFullPath().removeFileExtension().lastSegment(),
        			 jndiName, ITeiidDataSource.ERROR_CODES.NO_TEIID_RELATED_PROPERTIES_IN_PROFILE);
         
         String dataSourceType = connInfoProvider.getDataSourceType();

         // The data source property key represents what's needed as a property for the Teiid Data Source
         // This is provided by the getDataSourcePasswordPropertyKey() method.
         String dsPasswordKey = connInfoProvider.getDataSourcePasswordPropertyKey();
         boolean requiresPassword = (dsPasswordKey != null && connInfoProvider.requiresPassword(modelConnectionProfile));
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

                 IPasswordProvider passwordProvider = getPasswordProvider();
                if ((pwd == null) && (passwordProvider != null)) {
                     pwd = passwordProvider.getPassword(modelResource.getItemName(), modelConnectionProfile.getName());
                 }
             }

             if (pwd != null) {
                 props.setProperty(dsPasswordKey, pwd);
             }
         }
         
         if( requiresPassword && pwd == null )  {
        	 return new FailedTeiidDataSource(
        	 model.getFullPath().removeFileExtension().lastSegment(),
             jndiName, ITeiidDataSource.ERROR_CODES.DATASOURCE_REQUIRED_PASSWORD_NOT_DEFINED); 
    	 } else {
             ITeiidDataSource tds = teiidServer.getOrCreateDataSource(jndiName, jndiName, dataSourceType, props);
             tds.setPreview(previewVdb);
             return tds;
         }

     }
}

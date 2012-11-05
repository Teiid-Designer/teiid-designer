/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.connection.IPasswordProvider;

/**
 * @since 8.0
 */
public class TeiidDataSourceFactory {

    /**
     * 
     * Create a data source for the given server. If a data source with the given
     * jndi name already exists then this is returned.
     * 
     * @param teiidServer
     * @param model
     * @param jndiName
     * @param previewVdb
     * @param passwordProvider
     * 
     * @return a data source with the given jndi name
     * 
     * @throws Exception
     */
    public ITeiidDataSource createDataSource( ITeiidServer teiidServer, 
                                                   IFile model,
                                                   String jndiName,
                                                   boolean previewVdb,
                                                   IPasswordProvider passwordProvider ) throws Exception {
  
         // first check to see if DS with that name already exists
         ITeiidDataSource dataSource = teiidServer.getDataSource(jndiName);

         if (dataSource != null) {
             return dataSource;
         }        
         
         // need to create a DS
         ModelResource modelResource = ModelUtil.getModelResource(model, true);
         ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
         IConnectionInfoProvider connInfoProvider = manager.getProvider(modelResource);
         IConnectionProfile modelConnectionProfile = connInfoProvider.getConnectionProfile(modelResource);
         
         if (modelConnectionProfile == null)
             return null;
         
         Properties props = connInfoProvider.getTeiidRelatedProperties(modelConnectionProfile);
         if (props.isEmpty())
             return null;
         
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

                 if ((pwd == null) && (passwordProvider != null)) {
                     pwd = passwordProvider.getPassword(modelResource.getItemName(), modelConnectionProfile.getName());
                 }
             }

             if (pwd != null) {
                 props.setProperty(dsPasswordKey, pwd);
             }
         }

         if (!requiresPassword || (pwd != null)) {
             ITeiidDataSource tds = teiidServer.getOrCreateDataSource(jndiName, jndiName, dataSourceType, props);
             tds.setPreview(previewVdb);
             return tds;
         }

         return null;
     }
}

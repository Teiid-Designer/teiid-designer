package org.teiid.designer.runtime.preview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidDataSourceFactory;
import org.teiid.designer.runtime.connection.spi.IPasswordProvider;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;

public class DataSourceHelper {
	Map<String, ModelResource> jndiNameModelMap;
	Set<String> missingJndiNames;
	IStatus status;
	Properties teiidRelatedProperties = new Properties();

	public DataSourceHelper() {
		jndiNameModelMap =  new HashMap<String, ModelResource>();
		missingJndiNames =  new HashSet<String>();
	}
	
	public void addJndiName(String jndiName, ModelResource mr) {
		if( jndiNameModelMap != null ) {
			jndiNameModelMap.put(jndiName, mr);
		}
	}

	public IStatus getStatus() {
		return status;
	}
	
	public boolean checkDeployments() {
		status = Status.OK_STATUS;
		
		// Assume the server is defined and active
		ITeiidServer teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
		if( teiidServer == null ) {
			// SHOULD NEVER GET HERE
			status = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, Messages.DataSourceHelper_noServerDefined); //$NON-NLS-1$);
			return false;
		}
		try {
			// If any JNDI name doesn't exist on the server...
			// Show Error Dialog
			// return false
			for( String jndiName : jndiNameModelMap.keySet() ) {
				ITeiidDataSource ds = teiidServer.getDataSource(jndiName);
				if( ds == null && jndiName != null) {
					missingJndiNames.add(jndiName);
				}
			}
		} catch (Exception e) {
			status = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, Messages.DataSourceHelper_errorCheckingDataSourceDeployments);
			return false;
		}
		
		if( !missingJndiNames.isEmpty() ) {
			status = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, Messages.DataSourceHelper_requiredDataSourcesMissing);
			return false;
		}
		
		return true;
	}
	
	public IStatus createMissingDataSources() {
		IStatus status = Status.OK_STATUS;
		for( String jndiName : missingJndiNames ) {
			try {
				status = handleCreateDataSource(jndiNameModelMap.get(jndiName), jndiName);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.DataSourceHelper_dataSourceFailedToDeploy, jndiName));
			}
		}
		
		return status;
	}
	
    public IStatus handleCreateDataSource(ModelResource modelResource, String jndiName) throws Exception {
    	IStatus status = Status.OK_STATUS;
    	
		String shortJndiName = JndiUtil.removeJavaPrefix(jndiName);
    	
    	DataSourceConnectionHelper helper = new DataSourceConnectionHelper(modelResource);

        IStatus propertiesStatus = passwordOk(helper);
        
        if( propertiesStatus.getSeverity() == IStatus.ERROR ) return propertiesStatus;
        	
        String dsType = helper.getDataSourceType();
			
		getServer().getOrCreateDataSource(shortJndiName, jndiName, dsType, teiidRelatedProperties);
    	
    	return status;
    }
    
    private IStatus passwordOk(DataSourceConnectionHelper helper) throws Exception {
    	IConnectionInfoProvider provider = helper.getProvider();
    	if( provider == null ) 
    		return new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID, "No Connection Info Provider");
    	
    	IConnectionProfile profile = helper.getConnectionProfile();
    	if( profile == null )
    		return new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID, "No Connection Profile");
    	
        // The data source property key represents what's needed as a property for the Teiid Data Source
        // This is provided by the getDataSourcePasswordPropertyKey() method.
        String dsPasswordKey = helper.getProvider().getDataSourcePasswordPropertyKey();
        boolean requiresPassword = (dsPasswordKey != null && provider.requiresPassword(profile));
        String pwd = null;
        
        teiidRelatedProperties = provider.getTeiidRelatedProperties(profile);

        // Check Password
        if (requiresPassword) {
            // Check connection info provider. Property will be coming in with a key = "password"
            pwd = profile.getBaseProperties().getProperty(provider.getPasswordPropertyKey());

            if (pwd == null) {
                IConnectionProfile existingConnectionProfile = ProfileManager.getInstance().getProfileByName(profile.getName());

                if (existingConnectionProfile != null) {
                    // make sure the password property is there. if not get from connection profile.
                    // Use DTP's constant for profile: IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID =
                    // org.eclipse.datatools.connectivity.db.password
                    // DTP's connection profile "password" key, if exists for a profile type, is returned via the
                    // provider's getPasswordPropertyKey() method. This can be different than
                    // getDataSourcePasswordPropertyKey().
                    if (teiidRelatedProperties.getProperty(provider.getPasswordPropertyKey()) == null) {
                        pwd = existingConnectionProfile.getBaseProperties().getProperty(provider.getPasswordPropertyKey());
                    }
                }

                IPasswordProvider passwordProvider = TeiidDataSourceFactory.getPasswordProvider();
                if ((pwd == null) && (passwordProvider != null)) {
                    pwd = passwordProvider.getPassword(helper.getModelResource().getItemName(), profile.getName());
                }
            }

            if (pwd != null) {
            	teiidRelatedProperties.setProperty(dsPasswordKey, pwd);
            } else {
            	return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, Messages.DataSourceHelper_requiredPasswordWasNotSet);
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private IConnectionInfoProvider getProvider(ModelResource mr) throws Exception {
        ConnectionInfoProviderFactory providerFactory = new ConnectionInfoProviderFactory();

        return providerFactory.getProvider(mr);

    }
    
	public static ITeiidServer getServer() {
		return ModelerCore.getTeiidServerManager().getDefaultServer();
	}
	
    
    public String getDataSourceType(ModelResource mr) {
    	IConnectionInfoProvider provider = null;
    	
        try {
            provider = getProvider(mr);
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }

        if (provider != null) {
            return provider.getDataSourceType();
        }
        
        return null;
    }
}

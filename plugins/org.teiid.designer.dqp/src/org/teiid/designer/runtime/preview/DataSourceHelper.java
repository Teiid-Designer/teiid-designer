package org.teiid.designer.runtime.preview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;

public class DataSourceHelper {
	Map<String, ModelResource> jndiNameModelMap;
	Set<String> missingJndiNames;
	IStatus status;

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
	
		for( String jndiName : missingJndiNames ) {
			try {
				handleCreateDataSource(jndiNameModelMap.get(jndiName), jndiName);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.DataSourceHelper_dataSourceFailedToDeploy, jndiName));
			}
		}
		
		return Status.OK_STATUS;
	}
	
    public boolean handleCreateDataSource(ModelResource modelResource, String jndiName) throws Exception {
    	boolean didDeployDS = false;
    	
    	DataSourceConnectionHelper helper = new DataSourceConnectionHelper(modelResource, null);

        Properties connProps = getModelConnectionProperties(modelResource);
        	
        String dsType = helper.getDataSourceType();
			
		getServer().getOrCreateDataSource(jndiName, jndiName, dsType, connProps);
		didDeployDS = true;
    	
    	return didDeployDS;
    }
    
    public Properties getModelConnectionProperties(ModelResource mr) throws ModelWorkspaceException {

        IConnectionInfoProvider provider = null;

        try {
            provider = getProvider(mr);
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }

        if (provider != null) {
            Properties properties = provider.getConnectionProperties(mr);
            
            if (properties != null && !properties.isEmpty()) {
                return properties;
            }
        }
        
        return null;
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

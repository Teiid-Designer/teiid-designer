package org.teiid.designer.datatools.connection;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.runtime.spi.ITeiidServer;

public class DataSourceConnectionHelper {
	final ModelResource mr;
	final IConnectionProfile cp;

	public DataSourceConnectionHelper(ModelResource mr, IConnectionProfile connectionProfile) {
		super();
		
		this.mr = mr;
		this.cp = connectionProfile;
	}
	
    public Properties getModelConnectionProperties() {

        IConnectionInfoProvider provider = null;

        try {
            provider = getProvider();
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }

        if (provider != null) {
            Properties properties = provider.getTeiidRelatedProperties(cp);
            
            if (properties != null && !properties.isEmpty()) {
                return properties;
            }
        }
        
        return null;
    }
    
    public String getDataSourceType() {
    	IConnectionInfoProvider provider = null;
    	
        try {
            provider = getProvider();
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }

        if (provider != null) {
            return provider.getDataSourceType();
        }
        
        return null;
    }
    
    public String getTranslatorType() {
        IConnectionInfoProvider provider = null;

        try {
            provider = getProvider();
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }

        if (provider != null) {
            return provider.getTranslatorName(mr);
        }
        
        return null;
    }
    
    private IConnectionInfoProvider getProvider(  ) throws Exception {
        ConnectionInfoProviderFactory providerFactory = new ConnectionInfoProviderFactory();

        return providerFactory.getProvider(mr);

    }
    
	public static boolean isServerDefined() {
        // Check to see if server is available and connected
        ITeiidServer server = ModelerCore.getTeiidServerManager().getDefaultServer();
        
        return server != null;
	}
	
	public static boolean isServerConnected() {
		ITeiidServer server = ModelerCore.getTeiidServerManager().getDefaultServer();

        return server != null && server.isConnected();
	}
	
	public static ITeiidServer getServer() {
		return ModelerCore.getTeiidServerManager().getDefaultServer();
	}
}

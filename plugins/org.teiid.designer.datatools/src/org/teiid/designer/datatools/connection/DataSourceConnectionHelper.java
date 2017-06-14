package org.teiid.designer.datatools.connection;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.runtime.spi.ITeiidServer;

public class DataSourceConnectionHelper {
	final ModelResource mr;
	private IConnectionProfile cp;
	private IConnectionInfoProvider provider;

	public DataSourceConnectionHelper(ModelResource mr, IConnectionProfile connectionProfile) {
		super();
		
		this.mr = mr;
		this.cp = connectionProfile;
	}
	
	public DataSourceConnectionHelper(ModelResource mr) {
		super();
		
		this.mr = mr;
		this.cp = null;
		try {
			this.cp = this.getProvider().getConnectionProfile(this.mr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public Properties getModelConnectionProperties() {
    	IConnectionInfoProvider theProvider = null;
    	
        try {
        	theProvider = getProvider();
        } catch (Exception e) {
            // If provider throws exception its OK because some models may not have connection info.
        }
        
        if( theProvider != null ) {
        	Properties properties = theProvider.getTeiidRelatedProperties(cp);
        
	        if (properties != null && !properties.isEmpty()) {
	            return properties;
	        }
        }
        
        return new Properties();
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
    
    public IConnectionProfile getConnectionProfile() {
    	return this.cp;
    }
    
    public String getTranslatorType() {
        if (provider != null) {
            return provider.getTranslatorName(mr);
        }
        
        return null;
    }
    
    public IConnectionInfoProvider getProvider(  ) throws Exception {
    	if( provider == null ) {
    		provider = new ConnectionInfoProviderFactory().getProvider(this.mr);
    	}
        return provider;
    }
    
    public ModelResource getModelResource() {
    	return this.mr;
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

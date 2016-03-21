package org.teiid.designer.datatools.profiles.ws;

import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.properties.Property;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;

/**
 * @since 8.0
 */
public class WSConnectionInfoProvider extends ConnectionInfoHelper implements
		IConnectionInfoProvider {

    public final static String WS_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String WS_CONNECTION_FACTORY = "org.teiid.resource.adapter.ws.WSManagedConnectionFactory"; //$NON-NLS-1$
    public final static String HEADER_PARAMETER = "header_param";

    @Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
    	Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();
        
        removeHeaderParameters(props);

        String url = readEndPointProperty(props);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != user && !user.isEmpty()) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ICredentialsCommon.USERNAME_PROP_ID, user);
        }

        String security = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (security == null) {
            security = SecurityType.None.name();
        }
        
        connectionProps.setProperty(CONNECTION_NAMESPACE
                + ICredentialsCommon.SECURITY_TYPE_ID, security);

        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ws"); //$NON-NLS-1$
        getHelper().setProperties(modelResource, connectionProps);
		
	}

	private void removeHeaderParameters(Properties props) {
		 Enumeration<Object> keys = props.keys();

	    while (keys.hasMoreElements()) {
	      String key = (String) keys.nextElement();
	      if (key.startsWith(HEADER_PARAMETER)){
	    	  props.remove(key);
	      }
	    }
		
	}

	@Override
	public String getPasswordPropertyKey() {
		return ICredentialsCommon.PASSWORD_PROP_ID;
	}

	@Override
	public String getDataSourcePasswordPropertyKey() {
		return ICredentialsCommon.PASSWORD_PROP_ID;
	}

	@Override
	public String getDataSourceType() {
	    return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.WS);
	}

	@Override
	public Properties getTeiidRelatedProperties(
			IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String url = readEndPointProperty(props);
        if (null != url) {
            connectionProps.setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String contextFactory = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (null != contextFactory && !contextFactory.isEmpty()) {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID, contextFactory);
        }
        else {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID,
                    SecurityType.None.name());
        }
        
        if (!connectionProps.getProperty(ICredentialsCommon.SECURITY_TYPE_ID).equals(SecurityType.None.name())){
        	 String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
             if (null != user && !user.equals(StringConstants.EMPTY_STRING)) {
                 connectionProps.setProperty(ICredentialsCommon.USERNAME_PROP_ID, user);
             }
             
             String password = props.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
             if( password != null ) {
             	connectionProps.setProperty(ICredentialsCommon.PASSWORD_PROP_ID, password);
             }
        }
       
        connectionProps.setProperty(WS_CLASSNAME, WS_CONNECTION_FACTORY);
        return connectionProps;
    }

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		Properties props = connectionProfile.getBaseProperties();
		
		String contextFactory = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
		if( null != contextFactory && !contextFactory.isEmpty() && !contextFactory.equalsIgnoreCase(IWSProfileConstants.SecurityType.None.name()) ) {
			props.remove(ICredentialsCommon.USERNAME_PROP_ID);
			return true;
		}
		
		return false;
	}
	
	

}

package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;

/**
 * @since 8.2
 */
public class ODataConnectionInfoProvider extends ConnectionInfoHelper implements
		IConnectionInfoProvider {

    public final static String ODATA_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String ODATA_CONNECTION_FACTORY = "org.teiid.resource.adapter.ws.WSManagedConnectionFactory"; //$NON-NLS-1$

    @Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String url = readEndPointProperty(props);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ICredentialsCommon.USERNAME_PROP_ID, user);
        }
        
        String password = props.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
        if (null != password) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ICredentialsCommon.PASSWORD_PROP_ID, password);
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

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "odata"); //$NON-NLS-1$
        getHelper().setProperties(modelResource, connectionProps);
		
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
        
        String password = props.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
        if( password != null ) {
        	connectionProps.setProperty(ICredentialsCommon.PASSWORD_PROP_ID, password);
        }
        
        String url = readEndPointProperty(props);
        if (null != url) {
            connectionProps.setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(ICredentialsCommon.USERNAME_PROP_ID, user);
        }
        
        String requestTimeout = props.getProperty(IWSProfileConstants.DS_REQUEST_TIMEOUT);
        if (null != requestTimeout) {
            connectionProps.setProperty(IWSProfileConstants.DS_REQUEST_TIMEOUT, requestTimeout);
        }

        String contextFactory = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (null != contextFactory) {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID, contextFactory);
        }
        else {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID,
                    SecurityType.None.name());
        }

        connectionProps.setProperty(ODATA_CLASSNAME, ODATA_CONNECTION_FACTORY);
        return connectionProps;
    }

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		Properties props = connectionProfile.getBaseProperties();
		
		String contextFactory = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
		if( contextFactory != null && !contextFactory.equalsIgnoreCase(IWSProfileConstants.SecurityType.None.name()) ) {
			return true;
		}
		
		return false;
	}
	
	

}

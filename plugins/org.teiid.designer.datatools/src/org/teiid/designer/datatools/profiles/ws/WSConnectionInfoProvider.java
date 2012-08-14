package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;

/**
 * @since 8.0
 */
public class WSConnectionInfoProvider extends ConnectionInfoHelper implements
		IConnectionInfoProvider {

	@Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String url = readURLProperty(props);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != user) {
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
		return "connector-ws"; //$NON-NLS-1$
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
        
        String url = readURLProperty(props);
        if (null != url) {
            connectionProps.setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, url);
        }

        String user = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(ICredentialsCommon.USERNAME_PROP_ID, user);
        }

        String contextFactory = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (null != contextFactory) {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID, contextFactory);
        }
        else {
            connectionProps.setProperty(ICredentialsCommon.SECURITY_TYPE_ID,
                    SecurityType.None.name());
        }

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

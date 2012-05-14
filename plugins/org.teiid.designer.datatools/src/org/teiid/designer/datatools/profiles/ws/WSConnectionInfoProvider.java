package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

public class WSConnectionInfoProvider extends ConnectionInfoHelper implements
		IConnectionInfoProvider {

	@Override
	public void setConnectionInfo(ModelResource modelResource,
			IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String url = props.getProperty(IWSProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.URL_PROP_ID, url);
        }

        String user = props.getProperty(IWSProfileConstants.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.USERNAME_PROP_ID, user);
        }

        String security = props.getProperty(IWSProfileConstants.SECURITY_TYPE_ID);
        if (null != security) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + IWSProfileConstants.SECURITY_TYPE_ID, security);
        }
        
        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ws"); //$NON-NLS-1$
        getHelper().setProperties(modelResource, connectionProps);
		
	}

	@Override
	public String getPasswordPropertyKey() {
		return IWSProfileConstants.PASSWORD_PROP_ID;
	}

	@Override
	public String getDataSourcePasswordPropertyKey() {
		return IWSProfileConstants.PASSWORD_PROP_ID;
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
        
        String password = props.getProperty(IWSProfileConstants.PASSWORD_PROP_ID);
        if( password != null ) {
        	connectionProps.setProperty(IWSProfileConstants.PASSWORD_PROP_ID, password);
        }
        
        String url = props.getProperty(IWSProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(IWSProfileConstants.URL_PROP_ID, url);
        }

        String user = props.getProperty(IWSProfileConstants.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(IWSProfileConstants.USERNAME_PROP_ID, user);
        }

        String contextFactory = props.getProperty(IWSProfileConstants.SECURITY_TYPE_ID);
        if (null != contextFactory) {
            connectionProps.setProperty(IWSProfileConstants.SECURITY_TYPE_ID, contextFactory);
        }

        return connectionProps;
    }

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		Properties props = connectionProfile.getBaseProperties();
		
		String contextFactory = props.getProperty(IWSProfileConstants.SECURITY_TYPE_ID);
		if( contextFactory != null && !contextFactory.equalsIgnoreCase(IWSProfileConstants.SecurityType.None.name()) ) {
			return true;
		}
		
		return false;
	}
	
	

}

package org.teiid.designer.datatools.profiles.jdg;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;

public class JDG7ConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider, IJDGProfileConstants.PropertyKeys {
	
    @Override
	public String getDataSourcePasswordPropertyKey() {
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return IJDGProfileConstants.JDG7_RA_TYPE; //$NON-NLS-1$
	}

	@Override
	public String getPasswordPropertyKey() {
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String result = props.getProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID);
		
		result = props.getProperty(REMOTE_SERVER_LIST);
		if (null != result) {
			connectionProps.setProperty(REMOTE_SERVER_LIST, result);
		}
		
		result = props.getProperty(TRUST_FILE_STORE_NAME);
		if (null != result) {
			connectionProps.setProperty(TRUST_FILE_STORE_NAME, result);
		}
		
		result = props.getProperty(TRUST_STORE_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(TRUST_STORE_PASSWORD, result);
		}
		
		result = props.getProperty(KEY_STORE_FILE_NAME);
		if (null != result) {
			connectionProps.setProperty(KEY_STORE_FILE_NAME, result);
		}
		
		result = props.getProperty(KEY_STORE_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(KEY_STORE_PASSWORD, result);
		}
		
		result = props.getProperty(AUTHENTICATION_SERVER_NAME);
		if (null != result) {
			connectionProps.setProperty(AUTHENTICATION_SERVER_NAME, result);
		}
		
		result = props.getProperty(AUTHENTICATION_REALM);
		if (null != result) {
			connectionProps.setProperty(AUTHENTICATION_REALM, result);
		}
		
		result = props.getProperty(SASL_MECHANISM);
		if (null != result) {
			connectionProps.setProperty(SASL_MECHANISM, result);
		}
		
		result = props.getProperty(AUTHENTICATION_USER_NAME);
		if (null != result) {
			connectionProps.setProperty(AUTHENTICATION_USER_NAME, result);
		}
		
		result = props.getProperty(AUTHENTICATION_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(AUTHENTICATION_PASSWORD, result);
		}
		
		connectionProps.setProperty(CLASS_NAME, IJDGProfileConstants.REQUIRED_JDG7_CLASS_NAME);
		
        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

//		String result = props.getProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID);
//		if (null != result) {
//			connectionProps.setProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, result);
//		}
		
		
		String result = props.getProperty(REMOTE_SERVER_LIST);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + REMOTE_SERVER_LIST, result);
		}

		
		getHelper().removeProperties(modelResource,	CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);
		connectionProps.setProperty(CLASS_NAME, IJDGProfileConstants.REQUIRED_JDG7_CLASS_NAME);


		getHelper().setProperties(modelResource, connectionProps);
		
		result = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
		if (null != result) {
			setJNDIName(modelResource, result);
		}
		
		result = props.getProperty(TRUST_FILE_STORE_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + TRUST_FILE_STORE_NAME, result);
		}
		
		result = props.getProperty(TRUST_STORE_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + TRUST_STORE_PASSWORD, result);
		}
		
		result = props.getProperty(KEY_STORE_FILE_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + KEY_STORE_FILE_NAME, result);
		}
		
		result = props.getProperty(KEY_STORE_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + KEY_STORE_PASSWORD, result);
		}
		
		result = props.getProperty(AUTHENTICATION_SERVER_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + AUTHENTICATION_SERVER_NAME, result);
		}
		
		result = props.getProperty(AUTHENTICATION_REALM);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + AUTHENTICATION_REALM, result);
		}
		
		result = props.getProperty(SASL_MECHANISM);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + SASL_MECHANISM, result);
		}
		
		result = props.getProperty(AUTHENTICATION_USER_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + AUTHENTICATION_USER_NAME, result);
		}
		
		result = props.getProperty(AUTHENTICATION_PASSWORD);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + AUTHENTICATION_PASSWORD, result);
		}


		addMaterializationTranslatorOverrideProperties(modelResource);
	}

	@Override
	public Properties getCommonProfileProperties(IConnectionProfile profile) {
		return super.getCommonProfileProperties(profile);
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		String translatorName = null;
		Properties translatorProps = null;
		try {
			translatorProps = getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE);
		} catch (ModelWorkspaceException ex) {
		}
		if(translatorProps!=null && !translatorProps.isEmpty()) {
			translatorName = translatorProps.getProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY);
		}
		
		if(translatorName!=null && !translatorName.isEmpty()) return translatorName;
		
		return "unknown"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
	
	private void addMaterializationTranslatorOverrideProperties(ModelResource mr) {
		Properties props = new Properties();
		props.setProperty(SUPPORTS_DIRECT_QUERY_PROCEDURE, Boolean.toString(true));
		props.setProperty(SUPPORTS_NATIVE_QUERIES, Boolean.toString(true));
		replaceTranlatorOverrideProperties(mr,  props);
	}
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jdg;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;

public class JDGConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider, IJDGProfileConstants.PropertyKeys {
	
    @Override
	public String getDataSourcePasswordPropertyKey() {
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return IJDGProfileConstants.JDG_RA_TYPE; //$NON-NLS-1$
	}

	@Override
	public String getPasswordPropertyKey() {
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
//        String result = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
//        if (null != result) {
//            connectionProps.setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, result);
//        }
        
        String result = props.getProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(TRANSLATOR_NAME_KEY, result);
		}
		
		result = props.getProperty(CACHE_TYPE_MAP);
		if (null != result) {
			connectionProps.setProperty(CACHE_TYPE_MAP, result);
		}
		
		result = props.getProperty(REMOTE_SERVER_LIST);
		if (null != result) {
			connectionProps.setProperty(REMOTE_SERVER_LIST, result);
		}
		
		result = props.getProperty(CACHE_JNDI_NAME);
		if (null != result) {
			connectionProps.setProperty(CACHE_JNDI_NAME, result);
		}
		
		result = props.getProperty(HOT_ROD_CLIENT_PROPERTIES_FILE);
		if (null != result) {
			connectionProps.setProperty(HOT_ROD_CLIENT_PROPERTIES_FILE, result);
		}
		
		result = props.getProperty(STAGING_CACHE_NAME);
		if (null != result) {
			connectionProps.setProperty(STAGING_CACHE_NAME, result);
		}
		
		result = props.getProperty(ALIAS_CACHE_NAME);
		if (null != result) {
			connectionProps.setProperty(ALIAS_CACHE_NAME, result);
		}
		
		result = props.getProperty(PROTOBUF_DEFINITION_FILE);
		if (null != result) {
			connectionProps.setProperty(PROTOBUF_DEFINITION_FILE, result);
		}
		
		result = props.getProperty(MESSAGE_MARSHALLERS);
		if (null != result) {
			connectionProps.setProperty(MESSAGE_MARSHALLERS, result);
		}
		
		result = props.getProperty(MESSAGE_DESCRIPTOR);
		if (null != result) {
			connectionProps.setProperty(MESSAGE_DESCRIPTOR, result);
		}
		
		result = props.getProperty(MODULE);
		if (null != result) {
			connectionProps.setProperty(MODULE, result);
		}
		
		connectionProps.setProperty(CLASS_NAME, IJDGProfileConstants.REQUIRED_CLASS_NAME);
		
		System.out.println("JDGConnectionInfoProvider.getTeiidRelatedProps()");
		System.out.println("  ====================================");
		for( Object prop : connectionProps.keySet() ) {
			System.out.println("   key = " + (String)prop + "  value = " + connectionProps.getProperty((String)prop));
		}
		System.out.println("  ====================================");

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

//		String result = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
//		if (null != result) {
//			connectionProps.setProperty(CONNECTION_NAMESPACE + IJBossDsProfileConstants.JNDI_PROP_ID, result);
//		}

		String result = props.getProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, result);
		}
		
		result = props.getProperty(ALIAS_CACHE_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + ALIAS_CACHE_NAME, result);
		}
		
		result = props.getProperty(CACHE_TYPE_MAP);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + CACHE_TYPE_MAP, result);
		}
		
		result = props.getProperty(REMOTE_SERVER_LIST);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + REMOTE_SERVER_LIST, result);
		}
		
		result = props.getProperty(CACHE_JNDI_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + CACHE_JNDI_NAME, result);
		}
		
		result = props.getProperty(HOT_ROD_CLIENT_PROPERTIES_FILE);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + HOT_ROD_CLIENT_PROPERTIES_FILE, result);
		}
		
		result = props.getProperty(STAGING_CACHE_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + STAGING_CACHE_NAME, result);
		}
		
		result = props.getProperty(ALIAS_CACHE_NAME);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + ALIAS_CACHE_NAME, result);
		}
		
		
		result = props.getProperty(PROTOBUF_DEFINITION_FILE);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + PROTOBUF_DEFINITION_FILE, result);
		}
		
		result = props.getProperty(MESSAGE_MARSHALLERS);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + MESSAGE_MARSHALLERS, result);
		}
		
		result = props.getProperty(MESSAGE_DESCRIPTOR);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + MESSAGE_DESCRIPTOR, result);
		}
		
		result = props.getProperty(MODULE);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + MODULE, result);
		}
		
		getHelper().removeProperties(modelResource,	CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);
		connectionProps.setProperty(CLASS_NAME, IJDGProfileConstants.REQUIRED_CLASS_NAME);


		getHelper().setProperties(modelResource, connectionProps);
		
		result = props.getProperty(IJBossDsProfileConstants.JNDI_PROP_ID);
		if (null != result) {
			setJNDIName(modelResource, result);
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

package org.teiid.designer.runtime.connection;

import java.util.Properties;
import java.util.Set;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;

import com.metamatrix.core.util.CoreArgCheck;

public class ConnectionProfileFactory {
	
	public ConnectionProfileFactory() {
		super();
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param id
	 * @param props
	 * @return
	 */
	public ConnectionProfile createConnectionProfile(String name, String description, String id, Properties props) {
		CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(id, "id"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(props, "props"); //$NON-NLS-1$
		
		ConnectionProfile profile = new ConnectionProfile(name, description, id);
		profile.setProperties(id, props);
		return profile;
	}
	
	
	/**
	 * Returns the collective properties of a <code>ConnectionProfile</code> to include name, description and provider id
	 * in addition to it's base properties. These properties are also prefixed with a custom namespace for storage in
	 * a model resource "annotation"
	 * 
	 * @param connectionProfile the connection profile
	 * @return the name-spaced properties for the connection profile
	 */
	public Properties getNamespacedProperties(IConnectionProfile connectionProfile) {
		CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$
		
		Properties baseProps = connectionProfile.getBaseProperties();
		Properties connProps = new Properties();
		connProps.put(ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_NAME_KEY, connectionProfile.getName());
		connProps.put(ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_DESCRIPTION_KEY, connectionProfile.getDescription());
		connProps.put(ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY, connectionProfile.getProviderId());
		Set<Object> keys = baseProps.keySet();
		for(Object  nextKey : keys ) {
			connProps.put(ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + nextKey, baseProps.get(nextKey));
		}
		return connProps;
	}
}

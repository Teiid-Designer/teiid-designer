package org.teiid.designer.datatools.profiles.ldap;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * @since 8.0
 */
public class LDAPConnectionFactory implements IConnectionFactory {

	@Override
	public IConnection createConnection(IConnectionProfile profile) {
		LDAPConnection connection = new LDAPConnection(profile,
				getClass());
		connection.open();
		return connection;
	}

	@Override
	public IConnection createConnection(IConnectionProfile profile, String uid,
			String pwd) {
		return createConnection(profile);
	}

}

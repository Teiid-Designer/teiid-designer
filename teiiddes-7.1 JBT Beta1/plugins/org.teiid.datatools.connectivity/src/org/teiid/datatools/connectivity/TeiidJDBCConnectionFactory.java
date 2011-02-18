package org.teiid.datatools.connectivity;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

public class TeiidJDBCConnectionFactory implements IConnectionFactory {

	@Override
	public IConnection createConnection(IConnectionProfile profile) {
		TeiidJDBCConnection connection = new TeiidJDBCConnection(profile,
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

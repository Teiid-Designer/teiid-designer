package org.teiid.designer.datatools.profiles.modeshape;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

public class ModeShapeJDBCConnectionFactory implements IConnectionFactory {

	@Override
	public IConnection createConnection(IConnectionProfile profile) {
		ModeShapeJDBCConnection connection = new ModeShapeJDBCConnection(profile,
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

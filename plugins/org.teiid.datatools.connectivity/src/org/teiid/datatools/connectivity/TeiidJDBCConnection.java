package org.teiid.datatools.connectivity;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;

public class TeiidJDBCConnection extends JDBCConnection {

	public TeiidJDBCConnection(IConnectionProfile profile, Class factoryClass) {
		super(profile, factoryClass);
	}

}

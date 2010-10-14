package org.teiid.designer.datatools.profiles.teiidadmin;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

public class ConnectionFactory implements IConnectionFactory {

    @Override
    public IConnection createConnection( IConnectionProfile profile ) {
        TeiidAdminConnection connection = new TeiidAdminConnection(profile);
        return connection;
    }

    @Override
    public IConnection createConnection( IConnectionProfile profile,
                                         String uid,
                                         String pwd ) {
        TeiidAdminConnection connection = new TeiidAdminConnection(profile, uid, pwd);
        return connection;
    }

}

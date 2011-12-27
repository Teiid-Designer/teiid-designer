package org.teiid.designer.datatools.salesforce;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

public class ConnectionFactory implements IConnectionFactory {

    @Override
    public IConnection createConnection( IConnectionProfile profile ) {
        SalesForceConnection connection = new SalesForceConnection(profile);
        return connection;
    }

    @Override
    public IConnection createConnection( IConnectionProfile profile,
                                         String uid,
                                         String pwd ) {
        SalesForceConnection connection = new SalesForceConnection(profile, uid, pwd);
        return connection;
    }

}

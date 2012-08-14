package org.teiid.designer.modelgenerator.salesforce.datatools;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * @since 8.0
 */
public class ConnectionFactory implements IConnectionFactory {

    @Override
    public IConnection createConnection( IConnectionProfile profile ) {
        SalesforceDatatoolsConnection connection = new SalesforceDatatoolsConnection(profile);
        return connection;
    }

    @Override
    public IConnection createConnection( IConnectionProfile profile,
                                         String uid,
                                         String pwd ) {
        SalesforceDatatoolsConnection connection = new SalesforceDatatoolsConnection(profile, uid, pwd);
        return connection;
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.salesforce;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.impl.Connection;

/**
 * 
 */
public class SalesForceConnection implements IConnection {

    private IConnectionProfile savedProfile;
    private Connection connection;
    private Throwable connectException;

    /**
     * @param profile
     */
    public SalesForceConnection( IConnectionProfile profile ) {
        savedProfile = profile;
        if (null == connection) {
            connection = new Connection();
            try {
                Properties props = profile.getBaseProperties();
                String username = props.getProperty(ISalesForceProfileConstants.USERNAME_PROP_ID);
                String password = props.getProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID);

                // If the user did not supply a sandbox URL, then URL_PROP_ID should be empty
                URL connectionURL = null;
                String stringUrl = props.getProperty(ISalesForceProfileConstants.URL_PROP_ID);
                if (null != stringUrl) {

                    try {
                        connectionURL = new URL(stringUrl);
                    } catch (MalformedURLException e) {
                        connectException = e;
                        connection = null;
                        return;
                    }
                }
                connection.login(username, password, connectionURL);
            } catch (Exception e) {
                connectException = e;
                connection = null;
            }

        }
    }

    /**
     * @param profile
     * @param uid
     * @param pwd
     */
    public SalesForceConnection( IConnectionProfile profile,
                                 String uid,
                                 String pwd ) {
        savedProfile = profile;
        if (null == connection) {
            connection = new Connection();
            try {
                Properties props = profile.getBaseProperties();
                String stringUrl = props.getProperty(ISalesForceProfileConstants.URL_PROP_ID);
                URL connectionURL;
                try {
                    connectionURL = new URL(stringUrl);
                } catch (MalformedURLException e) {
                    connectException = e;
                    connection = null;
                    return;
                }
                connection.login(uid, pwd, connectionURL);
            } catch (Exception e) {
                connectException = e;
                connection = null;
            }

        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#close()
     */
    @Override
    public void close() {
        connection = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getConnectException()
     */
    @Override
    public Throwable getConnectException() {
        return connectException;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getConnectionProfile()
     */
    @Override
    public IConnectionProfile getConnectionProfile() {
        return savedProfile;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getRawConnection()
     */
    @Override
    public Object getRawConnection() {
        return connection;
    }

}

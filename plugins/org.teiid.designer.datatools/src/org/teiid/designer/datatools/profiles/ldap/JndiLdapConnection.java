/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 *
 * Additional code taken from Apache Directory Studio (http://directory.apache.org/studio)
 * licensed under the http://www.apache.org/licenses/LICENSE-2.0
 */
package org.teiid.designer.datatools.profiles.ldap;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;
import org.teiid.core.designer.util.StringConstants;

/**
 * @since 8.0
 */
public class JndiLdapConnection extends JDBCConnection implements ILdapProfileConstants, StringConstants {

    /**
     * @param profile
     * @param factoryClass
     */
    public JndiLdapConnection(IConnectionProfile profile, Class factoryClass) {
        super(profile, factoryClass);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection#open()
     */
    @Override
    public void open() {
        if (mConnection != null) {
            close();
        }

        mConnection = null;
        mConnectException = null;

        try {
            initializeLDAPContext(this.getConnectionProfile());
        } catch (Exception e) {
            mConnectException = e;
        }
    }

    @Override
    protected Object createConnection(ClassLoader cl) throws Throwable {
        initializeLDAPContext(getConnectionProfile());
        return mConnection;
    }

    /**
     * Setup a standard initial LDAP context using JNDI's context factory. This method may be extended to support Sun-specific and
     * AD-specific contexts, in order to support the different paging implementations they provide.
     */
    private void initializeLDAPContext(IConnectionProfile connectionProfile) throws Exception {
        LDAPConnectionFactory factory = new LDAPConnectionFactory();
        Connection connection = factory.convert(connectionProfile);
        JNDIConnectionWrapper wrapper = new JNDIConnectionWrapper(connection);

        NullProgressMonitor ipm = new NullProgressMonitor();
        StudioProgressMonitor monitor = new StudioProgressMonitor(ipm);
        wrapper.connect(monitor);
        wrapper.bind(monitor);

        Exception exception = monitor.getException();
        if (exception != null)
            throw exception;

        mConnection = wrapper;
    }

    /**
     * Closes LDAP context, effectively closing the connection to LDAP. (non-Javadoc)
     */
    @Override
    public void close() {
        if (mConnection instanceof JNDIConnectionWrapper) {
            ((JNDIConnectionWrapper)mConnection).disconnect();
            mConnection = null;
        }
    }

}

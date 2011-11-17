package org.teiid.designer.datatools.profiles.ldap;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;

public class LDAPConnection extends JDBCConnection {

    private InitialLdapContext initCtx;

    public LDAPConnection( IConnectionProfile profile,
                           Class factoryClass ) {
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
            initializeLDAPContext(this.getConnectionProfile().getBaseProperties());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object createConnection( ClassLoader cl ) throws Throwable {
        Properties props = getConnectionProfile().getBaseProperties();

        return initializeLDAPContext(props); // super.createConnection(cl);
    }

    /**
     * Setup a standard initial LDAP context using JNDI's context factory. This method may be extended to support Sun-specific and
     * AD-specific contexts, in order to support the different paging implementations they provide.
     * 
     * @return the initial LDAP Context
     */
    private InitialContext initializeLDAPContext( Properties props ) throws NamingException {
        // Create the root context.
        InitialLdapContext initContext;

        Hashtable connenv = new Hashtable();
        connenv.put(Context.INITIAL_CONTEXT_FACTORY, props.getProperty(ILdapProfileConstants.CONTEXT_FACTORY));
        connenv.put(Context.PROVIDER_URL, props.getProperty(ILdapProfileConstants.URL_PROP_ID));
        connenv.put(Context.SECURITY_PRINCIPAL, props.getProperty(ILdapProfileConstants.USERNAME_PROP_ID));
        connenv.put(Context.SECURITY_CREDENTIALS, props.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID));

        try {
            initContext = new InitialLdapContext(connenv, null);
        } catch (NamingException ne) {
            throw ne;
        }
        return initContext;
    }

    /**
     * Closes LDAP context, effectively closing the connection to LDAP. (non-Javadoc)
     */
    @Override
    public void close() {
        if (initCtx != null) {
            try {
                initCtx.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

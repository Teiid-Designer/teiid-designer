/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ldap;

import java.util.Properties;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.util.StringConstants;

/**
 * @since 8.0
 */
public class LDAPConnectionFactory implements IConnectionFactory, ILdapProfileConstants, StringConstants {

	@Override
	public IConnection createConnection(IConnectionProfile profile) {
		JndiLdapConnection connection = new JndiLdapConnection(profile, getClass());
		connection.open();
		return connection;
	}

	@Override
	public IConnection createConnection(IConnectionProfile profile, String uid,
			String pwd) {
		return createConnection(profile);
	}

    /**
     * @param profile connection profile
     *
     * @return an ldap directory connection from the values in the given profile
     */
    public Connection convert(IConnectionProfile profile) {
        ConnectionParameter parameter = new ConnectionParameter();
        parameter.setExtendedBoolProperty(IBrowserConnection.CONNECTION_PARAMETER_FETCH_BASE_DNS, true);
        parameter.setExtendedIntProperty(IBrowserConnection.CONNECTION_PARAMETER_COUNT_LIMIT, 1000);
        parameter.setExtendedIntProperty(IBrowserConnection.CONNECTION_PARAMETER_TIME_LIMIT, 0);
        parameter.setExtendedIntProperty(IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
                                         Connection.ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal());
        parameter.setExtendedIntProperty(IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
                                         Connection.AliasDereferencingMethod.ALWAYS.ordinal());

        Properties properties = profile.getBaseProperties();

        String baseDN = properties.getProperty(BASE_DN);
        parameter.setExtendedProperty(IBrowserConnection.CONNECTION_PARAMETER_BASE_DN, baseDN != null ? baseDN : EMPTY_STRING);

        String scheme = properties.getProperty(SCHEME_PROP_ID);
        String host = properties.getProperty(HOST_PROP_ID);
        Integer port = null;
        try {
            port = Integer.valueOf(properties.getProperty(PORT_PROP_ID));
        } catch (NumberFormatException ex) {
            // Do Nothing
        }

        if (scheme == null || host == null || port == null) {
            String url = properties.getProperty(URL_PROP_ID);
            try {
                LDAPUrl ldapUrl = new LDAPUrl(url);
                scheme = ldapUrl.getScheme();
                host = ldapUrl.getHost();
                port = ldapUrl.getPort();
            } catch (Exception ex) {
                // Do Nothing
            }
        }

		parameter.setHost(host);
		parameter.setPort(port);

		if (LDAP_SCHEME.equals(scheme))
			parameter.setEncryptionMethod(EncryptionMethod.NONE);
		else if (LDAP_SCHEME.equals(scheme))
			parameter.setEncryptionMethod(EncryptionMethod.LDAPS);

		if (AUTHMETHOD_NONE.equals(properties.getProperty(AUTHENTICATION_METHOD)))
				parameter.setAuthMethod(AuthenticationMethod.NONE);
		else if (AUTHMETHOD_SIMPLE.equals(properties.getProperty(AUTHENTICATION_METHOD)))
				parameter.setAuthMethod(AuthenticationMethod.SIMPLE);

        parameter.setBindPrincipal(properties.getProperty(USERNAME_PROP_ID));
        parameter.setBindPassword(properties.getProperty(PASSWORD_PROP_ID));

        parameter.setNetworkProvider(NetworkProvider.JNDI);

    	Connection connection = new Connection(parameter);
    	return connection;
	}

	/**
     * Gets the default LDAP context factory.
     * 
     * Right now the following context factories are supported:
     * <ul>
     * <li>com.sun.jndi.ldap.LdapCtxFactory</li>
     * </ul>
     * 
     * @return the default LDAP context factory
     */
    public static String getDefaultLdapContextFactory()
    {
        String defaultLdapContextFactory = EMPTY_STRING;

		try {
			String sun = "com.sun.jndi.ldap.LdapCtxFactory"; //$NON-NLS-1$
			Class.forName(sun);
			defaultLdapContextFactory = sun;
		} catch (ClassNotFoundException e) {
			// Do Nothing
		}

		return defaultLdapContextFactory;
    }
}

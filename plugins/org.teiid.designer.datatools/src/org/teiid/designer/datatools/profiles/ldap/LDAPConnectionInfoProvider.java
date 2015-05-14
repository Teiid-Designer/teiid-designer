package org.teiid.designer.datatools.profiles.ldap;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;

/**
 * @since 8.0
 */
public class LDAPConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

    /* LDAP Binding Properties and sample values
     * 
     * LdapAuthMethod = None / Simple
     * LdapAdminUserDN
     * LdapAdminUserPassword
     * LdapStartTLS (encryption) = true/false
     * LdapUrl = ldap[s]://<ldapServer>:<389>
     * LdapContextFactory = com.sun.jndi.ldap.LdapCtxFactory
     */

    @Override
    public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
        throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String url = props.getProperty(ILdapProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ILdapProfileConstants.URL_PROP_ID, url);
        }

        String authMethod = props.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD);
        if (null != authMethod) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ILdapProfileConstants.AUTHENTICATION_METHOD, authMethod);
        }

        String user = props.getProperty(ILdapProfileConstants.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ILdapProfileConstants.USERNAME_PROP_ID, user);
        }

        String contextFactory = props.getProperty(ILdapProfileConstants.CONTEXT_FACTORY);
        if (null != contextFactory) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ILdapProfileConstants.CONTEXT_FACTORY, contextFactory);
        }

        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ldap"); //$NON-NLS-1$
        getHelper().setProperties(modelResource, connectionProps);

    }

    @Override
    public String getPasswordPropertyKey() {
        return ILdapProfileConstants.PASSWORD_PROP_ID;
    }

    @Override
    public String getDataSourcePasswordPropertyKey() {
        //
        // This is the property key for the data source but it happens to
        // be the same as the property key used by the connection profile
        //
        return ILdapProfileConstants.PASSWORD_PROP_ID;
    }

    @Override
    public String getDataSourceType() {
        return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.LDAP);
    }

    @Override
    public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();

        Properties props = connectionProfile.getBaseProperties();

        String password = props.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID);
        if (password != null) {
            connectionProps.setProperty(ILdapProfileConstants.PASSWORD_PROP_ID, password);
        }

        String url = props.getProperty(ILdapProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(ILdapProfileConstants.URL_PROP_ID, url);
        }

        String authMethod = props.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD);
        if (null != authMethod) {
            connectionProps.setProperty(ILdapProfileConstants.AUTHENTICATION_METHOD, authMethod);
        }

        String user = props.getProperty(ILdapProfileConstants.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(ILdapProfileConstants.USERNAME_PROP_ID, user);
        }

        String contextFactory = props.getProperty(ILdapProfileConstants.CONTEXT_FACTORY);
        if (null != contextFactory) {
            connectionProps.setProperty(ILdapProfileConstants.CONTEXT_FACTORY, contextFactory);
        }
        connectionProps.setProperty(ILdapProfileConstants.LDAP_CLASSNAME, ILdapProfileConstants.LDAP_CONNECTION_FACTORY);

        return connectionProps;
    }

    @Override
    public boolean requiresPassword(IConnectionProfile connectionProfile) {
        Properties props = connectionProfile.getBaseProperties();
        String authMethod = props.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD);
        return ILdapProfileConstants.AUTHMETHOD_SIMPLE.equals(authMethod);
    }
}

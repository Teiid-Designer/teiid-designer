package org.teiid.designer.datatools.profiles.ldap;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

public class LDAPConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {
	public final static String LDAP_DATASOURCE_PASSWORD = "password"; //$NON-NLS-1$
	public final static String LDAP_DATASOURCE_URL = "url"; //$NON-NLS-1$
    /* LDAP Binding Properties and sample values
     * 
     * LdapAdminUserDN
     * LdapAdminUserPassword
     * LdapUrl = ldap://<ldapServer>:<389>
     * LdapContextFactory = com.sun.jndi.ldap.LdapCtxFactory
     */

    @Override
    public void setConnectionInfo( ModelResource modelResource,
                                   IConnectionProfile connectionProfile ) throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String url = props.getProperty(ILdapProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ILdapProfileConstants.URL_PROP_ID, url);
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

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getPasswordPropertyKey()
     */
    @Override
    public String getPasswordPropertyKey() {
        return "LdapAdminUserPassword"; //$NON-NLS-1$
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getDataSourcePasswordPropertyKey()
     */
    public String getDataSourcePasswordPropertyKey() {
        return "LdapAdminUserPassword"; //$NON-NLS-1$
    }

    @Override
    public String getDataSourceType() {
        return "connector-ldap"; //$NON-NLS-1$
    }

    @Override
    public Properties getTeiidRelatedProperties( IConnectionProfile connectionProfile ) {
        Properties connectionProps = new Properties();
        
        Properties props = connectionProfile.getBaseProperties();
        
        String password = props.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID);
        if( password != null ) {
        	connectionProps.setProperty(ILdapProfileConstants.PASSWORD_PROP_ID, password);
        }
        
        String url = props.getProperty(ILdapProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(ILdapProfileConstants.URL_PROP_ID, url);
        }

        String user = props.getProperty(ILdapProfileConstants.USERNAME_PROP_ID);
        if (null != user) {
            connectionProps.setProperty(ILdapProfileConstants.USERNAME_PROP_ID, user);
        }

        String contextFactory = props.getProperty(ILdapProfileConstants.CONTEXT_FACTORY);
        if (null != contextFactory) {
            connectionProps.setProperty(ILdapProfileConstants.CONTEXT_FACTORY, contextFactory);
        }

        return connectionProps;
    }
    
	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return true;
	}
}

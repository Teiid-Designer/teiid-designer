/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;

/**
 * Abstract super-class implementation of LDAP content provider
 */
public abstract class AbstractLdapContentProvider implements ITreeContentProvider {

    protected static final Object[] EMPTY_ARRAY = new Object[0];

    // LDAP-specific properties
    private static final String LDAP_AUTH_TYPE = "simple"; //$NON-NLS-1$
    private static final String LDAP_REFERRAL_MODE = "follow"; //$NON-NLS-1$
    private static Integer TIMEOUT = 3000;

    private InitialLdapContext ldapContext = null;

    private LdapImportWizardManager importManager;

    /**
     * Create new instance
     *
     * @param manager
     */
    public AbstractLdapContentProvider(LdapImportWizardManager manager) {
        this.importManager = manager;
    }

    /**
     * @return the importManager
     */
    public LdapImportWizardManager getImportManager() {
        return this.importManager;
    }

    protected InitialLdapContext getLdapContext() throws NamingException {
        if (ldapContext == null) {
            // Create the root context.
            IConnectionProfile profile = importManager.getConnectionProfile();
            Properties properties = profile.getBaseProperties();

            Hashtable connenv = new Hashtable();
            connenv.put(Context.INITIAL_CONTEXT_FACTORY, properties.getProperty(ILdapProfileConstants.CONTEXT_FACTORY));
            connenv.put(Context.PROVIDER_URL, properties.getProperty(ILdapProfileConstants.URL_PROP_ID));
            connenv.put(Context.SECURITY_PRINCIPAL, properties.getProperty(ILdapProfileConstants.USERNAME_PROP_ID));
            connenv.put(Context.SECURITY_CREDENTIALS, properties.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID));
            connenv.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTH_TYPE);
            connenv.put(Context.REFERRAL, LDAP_REFERRAL_MODE);
            connenv.put("com.sun.jndi.ldap.connect.timeout", TIMEOUT.toString()); //$NON-NLS-1$

            // Enable connection pooling for the Initial context.
            connenv.put("com.sun.jndi.ldap.connect.pool", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            connenv.put("com.sun.jndi.ldap.connect.pool.debug", "fine"); //$NON-NLS-1$ //$NON-NLS-2$

            ldapContext = new InitialLdapContext(connenv, null);
        }

        return ldapContext;
    }

    @Override
    public void dispose() {
        if (ldapContext != null) {
            try {
                ldapContext.close();
            } catch (NamingException ex) {
                // No need to log exception
            }
        }
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing to do
    }
}

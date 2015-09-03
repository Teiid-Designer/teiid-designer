package org.teiid.designer.datatools.profiles.ldap;

import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

/**
 * The ldap connection profile wizard
 */
public class LdapConnectionProfileWizard extends NewConnectionProfileWizard implements DatatoolsUiConstants {

    Properties profileProperties = new Properties();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#addCustomPages()
     */
    @Override
    public void addCustomPages() {
    	LdapProfileDetailsWizardPage page1 = new LdapProfileDetailsWizardPage(UTIL.getString("LdapProfileDetailsWizardPage.Name")); //$NON-NLS-1$
    	LdapProfileAuthenticationWizardPage page2 = new LdapProfileAuthenticationWizardPage(UTIL.getString("LdapProfileAuthenticationWizardPage.Name")); //$NON-NLS-1$
    	page1.setPageComplete(false);
    	page2.setPageComplete(false);
        addPage(page1);
        addPage(page2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#getProfileProperties()
     */
    @Override
    public Properties getProfileProperties() {
        return profileProperties;
    }

}

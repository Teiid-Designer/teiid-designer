package org.teiid.designer.datatools.profiles.ldap;

import java.util.Properties;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

public class ConnectionProfileWizard extends NewConnectionProfileWizard implements DatatoolsUiConstants {

    Properties profileProperties = new Properties();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#addCustomPages()
     */
    @Override
    public void addCustomPages() {
    	LdapProfileDetailsWizardPage page = new LdapProfileDetailsWizardPage(UTIL.getString("LdapProfileDetailsWizardPage.Name")); //$NON-NLS-1$
    	page.setPageComplete(false);
        addPage(page);
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

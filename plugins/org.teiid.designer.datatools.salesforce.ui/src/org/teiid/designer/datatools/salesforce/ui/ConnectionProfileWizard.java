package org.teiid.designer.datatools.salesforce.ui;

import java.util.Properties;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;

public class ConnectionProfileWizard extends NewConnectionProfileWizard {

    Properties profileProperties = new Properties();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#addCustomPages()
     */
    @Override
    public void addCustomPages() {
        addPage(new SalesForceProfileDetailsWizardPage(Messages.getString("SalesForceProfileDetailsWizardPage.Name"))); //$NON-NLS-1$
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

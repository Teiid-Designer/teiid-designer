package org.teiid.designer.modelgenerator.salesforce.ui;

import java.util.Properties;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;

/**
 * @since 8.0
 */
public class ConnectionProfileWizard extends NewConnectionProfileWizard implements ModelGeneratorSalesforceUiConstants {

    Properties profileProperties = new Properties();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#addCustomPages()
     */
    @Override
    public void addCustomPages() {
        addPage(new SalesForceProfileDetailsWizardPage(UTIL.getString("SalesForceProfileDetailsWizardPage.Name"))); //$NON-NLS-1$
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

package org.teiid.datatools.connectivity.ui;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleNewConnectionProfileWizard;

public class ConnectionProfileWizard extends ExtensibleNewConnectionProfileWizard {

    public ConnectionProfileWizard() {
        super(new TeiidProfileDetailsWizardPage("detailsPage")); //$NON-NLS-1$
		setWindowTitle(Messages
				.getString("ConnectionProfileWizard.WizardTitle")); //$NON-NLS-1$
    }
}

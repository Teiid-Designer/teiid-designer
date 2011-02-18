package org.teiid.datatools.connectivity.ui;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsWizardPage;

public class TeiidProfileDetailsWizardPage extends ExtensibleProfileDetailsWizardPage {

    public TeiidProfileDetailsWizardPage( String pageName ) {
        super(pageName, ITeiidDriverConstants.TEIID_CATEGORY);
    }

}

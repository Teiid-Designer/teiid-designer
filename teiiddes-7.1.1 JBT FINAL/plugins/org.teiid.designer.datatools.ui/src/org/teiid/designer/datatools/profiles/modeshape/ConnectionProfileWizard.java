package org.teiid.designer.datatools.profiles.modeshape;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleNewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

public class ConnectionProfileWizard extends ExtensibleNewConnectionProfileWizard {

    public ConnectionProfileWizard() {
        super(new  ModeShapeProfileDetailsWizardPage("detailsPage")); //$NON-NLS-1$
		setWindowTitle(DatatoolsUiPlugin.UTIL.getString(
				"ConnectionProfileWizard.ModeShape.WizardTitle")); //$NON-NLS-1$
    }
}

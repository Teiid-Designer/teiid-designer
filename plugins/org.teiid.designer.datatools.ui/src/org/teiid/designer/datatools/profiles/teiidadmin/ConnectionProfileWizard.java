package org.teiid.designer.datatools.profiles.teiidadmin;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleNewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

public class ConnectionProfileWizard extends ExtensibleNewConnectionProfileWizard implements DatatoolsUiConstants {

	public ConnectionProfileWizard() {
        super(new TeiidAdminProfileDetailsWizardPage("detailsPage")); //$NON-NLS-1$
		setWindowTitle(DatatoolsUiPlugin.getDefault().getPluginUtil()
				.getString("ConnectionProfileWizard.WizardTitle")); //$NON-NLS-1$
    }

}

package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

public class ConnectionProfileWizard extends NewConnectionProfileWizard
		implements DatatoolsUiConstants {
	
	Properties profileProperties = new Properties();

	@Override
	public void addCustomPages() {
		addPage(new WSProfileDetailsWizardPage(UTIL.getString("WSProfileDetailsWizardPage.Name"))); //$NON-NLS-1$
	}

	@Override
	public Properties getProfileProperties() {
		return profileProperties;
	}

}

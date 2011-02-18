package org.teiid.designer.datatools.ui.dialogs;

import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Adds the connection profile properties to the Dialog so that they
 * can be inserted into the TeiidDriverUIContributor later
 */
public class TeiidCPWizardDialog extends WizardDialog implements
		IDriverUIContributorInformation {

	Properties profileProps;
	
	public TeiidCPWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	public Properties getProperties() {
		return profileProps;
	}

	@Override
	public void setProperties(Properties properties) {
		profileProps = properties;
	}

}

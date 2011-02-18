/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import java.util.Properties;

import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.IHelpConstants;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizardPage;
import org.eclipse.datatools.help.HelpUtil;
import org.eclipse.swt.widgets.Composite;

public class ConnectionProfileWizard extends
NewConnectionProfileWizard {

	private TeiidProfileDetailsWizardPage wizardPage = null;

	private boolean isWizardPageCreated = true;

	public ConnectionProfileWizard() {
		super();
		wizardPage = new TeiidProfileDetailsWizardPage("detailsPage", this); //$NON-NLS-1$
		setWindowTitle(Messages
				.getString("ConnectionProfileWizard.WizardTitle")); //$NON-NLS-1$
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setData(HelpUtil.CONTEXT_PROVIDER_KEY, this);
		HelpUtil.setHelp(getShell(), HelpUtil.getContextId(
				IHelpConstants.GENERIC_DB_PROFILE_WIZARD,
				ConnectivityUIPlugin.getDefault().getBundle().getSymbolicName()));
	}

	public void addCustomPages() {
		addPage(wizardPage);
		setSkipProfileNamePage(true);
	}

	public Properties getProfileProperties() {
		return wizardPage.getProperties();
	}

	public NewConnectionProfileWizardPage getProfilePage() {
		return mProfilePage;
	}

	public boolean canFinish() {
		// This guarantees the Ping button is correctly enabled/disabled.
		if (isWizardPageCreated) {
			isWizardPageCreated = false;
			wizardPage.determinePageCompletion();
		}
		return super.canFinish();
	}

}

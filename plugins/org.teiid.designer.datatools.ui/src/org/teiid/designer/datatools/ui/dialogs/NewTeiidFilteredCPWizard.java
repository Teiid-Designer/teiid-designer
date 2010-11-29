/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.ui.dialogs;

import org.eclipse.datatools.connectivity.internal.ui.wizards.CPWizardSelectionPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewFilteredCPWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * 
 */
public class NewTeiidFilteredCPWizard extends NewFilteredCPWizard {

	// TODO find a way to not duplicate this value, complicated by the designtime/runtime feature separation.
    public static final String TEIID_PROFILE_ID = "org.teiid.datatools.connectivity.connectionProfile"; //$NON-NLS-1$	
	
    private String profileName;
    private String profileDescription;
    
	public NewTeiidFilteredCPWizard(String catagoryID) {
		super(new NewCPWizardProfileFilter(catagoryID), null);
	}

	public NewTeiidFilteredCPWizard() {
		super(new NewCPWizardProfileFilter(
				TEIID_PROFILE_ID), null);
	}

	public NewTeiidFilteredCPWizard(String profileName, String profileDescription) {
		super(new NewCPWizardProfileFilter(
				TEIID_PROFILE_ID), null);
		this.profileName = profileName;
		this.profileDescription = profileDescription;
	}
	
	public void addPages() {
		super.addPages();
		IWizardPage[] pages = getPages();
		CPWizardSelectionPage wizardSelectionPage = (CPWizardSelectionPage) pages[0];
		if(null != profileName) {
			wizardSelectionPage.setProfileName(profileName);
		}
		if(null != profileDescription) {
			wizardSelectionPage.setProfileDescription(profileDescription);
		}
	}
	
	public IWizardPage getStartingPage() {
		IWizardPage page = super.getStartingPage();
		CPWizardSelectionPage wizardSelectionPage = (CPWizardSelectionPage) page;
		if(null != profileName) {
			wizardSelectionPage.setProfileName(profileName);
		}
		if(null != profileDescription) {
			wizardSelectionPage.setProfileDescription(profileDescription);
		}
		return page;
	}
}
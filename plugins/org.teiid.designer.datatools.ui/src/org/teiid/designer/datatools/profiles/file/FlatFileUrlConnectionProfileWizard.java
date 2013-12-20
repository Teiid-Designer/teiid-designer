/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.file;

import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

public class FlatFileUrlConnectionProfileWizard extends NewConnectionProfileWizard
		implements DatatoolsUiConstants {

	Properties profileProperties = new Properties();

	@Override
	public void addCustomPages() {
		FlatFileUrlProfileDetailsWizardPage page = new FlatFileUrlProfileDetailsWizardPage(UTIL.getString("FlatFileUrlProfileDetailsWizardPage.Name")); //$NON-NLS-1$
		page.setPageComplete(false);
		addPage(page);
	}

	@Override
	public Properties getProfileProperties() {
		return profileProperties;
	}

}
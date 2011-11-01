/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.xml;

import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

public class XmlUrlConnectionProfileWizard extends NewConnectionProfileWizard
		implements DatatoolsUiConstants {

	Properties profileProperties = new Properties();

	@Override
	public void addCustomPages() {
		addPage(new XmlUrlProfileDetailsWizardPage(UTIL.getString("XmlProfileDetailsWizardPage.Name"))); //$NON-NLS-1$
	}

	@Override
	public Properties getProfileProperties() {
		return profileProperties;
	}

}
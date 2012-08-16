/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.util.Properties;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.modelgenerator.wsdl.WSDLReader;

/**
 * Wizard for create a connection profile for a SOAP wsdl
 */
public class WSSoapConnectionProfileWizard extends NewConnectionProfileWizard implements DatatoolsUiConstants {

	private Properties profileProperties = new Properties();
	
	private WSDLReader wsdlReader = new WSDLReader();

	@Override
	public void addCustomPages() {
		addPage(new WSSoapProfileDetailsWizardPage(UTIL.getString("WSSoapConnectionProfileWizard.Name"))); //$NON-NLS-1$
		addPage(new WSSoapProfileEndPointWizardPage(UTIL.getString("WSSoapConnectionProfileWizard.Name"))); //$NON-NLS-1$
	}

	@Override
	public Properties getProfileProperties() {
		return profileProperties;
	}
	
	/**
	 * @return the wsdlReader
	 */
	public WSDLReader getWsdlReader() {
		return wsdlReader;
	}

}

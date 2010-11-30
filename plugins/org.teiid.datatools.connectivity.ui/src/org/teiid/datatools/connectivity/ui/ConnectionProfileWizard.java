/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleNewConnectionProfileWizard;

public class ConnectionProfileWizard extends ExtensibleNewConnectionProfileWizard {

    public ConnectionProfileWizard() {
        super(new TeiidProfileDetailsWizardPage("detailsPage")); //$NON-NLS-1$
		setWindowTitle(Messages
				.getString("ConnectionProfileWizard.WizardTitle")); //$NON-NLS-1$
    }
}

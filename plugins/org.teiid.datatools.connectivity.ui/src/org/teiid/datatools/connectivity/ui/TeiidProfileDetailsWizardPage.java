/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsWizardPage;

public class TeiidProfileDetailsWizardPage extends ExtensibleProfileDetailsWizardPage {

    public TeiidProfileDetailsWizardPage( String pageName ) {
        super(pageName, ITeiidDriverConstants.TEIID_CATEGORY);
    }

}

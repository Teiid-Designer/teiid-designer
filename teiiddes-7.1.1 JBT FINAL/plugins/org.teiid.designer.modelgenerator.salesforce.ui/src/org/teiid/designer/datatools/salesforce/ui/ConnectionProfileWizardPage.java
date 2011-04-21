/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.salesforce.ui;

import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizardPage;

/**
 * 
 */
public class ConnectionProfileWizardPage extends NewConnectionProfileWizardPage {

    /**
     * Constructor
     */
    public ConnectionProfileWizardPage() {
        this("NewConnectionProfileWizardPage"); //$NON-NLS-1$
    }

    /**
     * Constructor
     * 
     * @param name
     */
    public ConnectionProfileWizardPage( String name ) {
        super(name);
        setTitle(ConnectivityUIPlugin.getDefault().getResourceString("NewConnectionProfileWizardPage.title")); //$NON-NLS-1$
        setDescription(ConnectivityUIPlugin.getDefault().getResourceString("NewConnectionProfileWizardPage.desc")); //$NON-NLS-1$
    }

}

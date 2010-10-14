/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.teiidadmin;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsWizardPage;

/**
 * 
 */
public class TeiidAdminProfileDetailsWizardPage extends ExtensibleProfileDetailsWizardPage {

    public TeiidAdminProfileDetailsWizardPage( String pageName ) {
        super(pageName, ITeiidAdminProfileConstants.TEIID_ADMIN_CATEGORY);
    }
    
}
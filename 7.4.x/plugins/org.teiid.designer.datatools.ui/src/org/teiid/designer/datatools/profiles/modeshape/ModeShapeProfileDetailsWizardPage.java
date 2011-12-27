package org.teiid.designer.datatools.profiles.modeshape;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsWizardPage;

public class ModeShapeProfileDetailsWizardPage extends ExtensibleProfileDetailsWizardPage {

    public ModeShapeProfileDetailsWizardPage( String pageName ) {
        super(pageName, IModeShapeDriverConstants.MODESHAPE_CATEGORY);
    }
}

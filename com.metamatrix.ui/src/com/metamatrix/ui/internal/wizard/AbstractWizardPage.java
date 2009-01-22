/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;

/**<p>
 * </p>
 * @since 4.0
 */
public abstract class AbstractWizardPage extends WizardPage implements IPersistentWizardPage {
    //============================================================================================================================
    // Constructors
    
    /**<p>
     * </p>
     * @param name
     * @param title
     * @since 4.0
     */
    public AbstractWizardPage(final String name, final String title) {
        super(name, title, null);
    }
    
    //============================================================================================================================
    // Implemented Methods
    
    /**<p>
     * Does nothing.
     * </p>
     * @see com.metamatrix.ui.internal.wizard.IPersistentWizardPage#saveSettings()
     * @since 4.0
     */
    public void saveSettings() {
    }
    
    //============================================================================================================================
    // Overridden Methods
    
    /**
     * Overriden since this method gets called a lot and because super's implementation called
     * wizard.getNextPage() and that method potentially could be used to perform work (work needed to be
     * done between pages). 
     * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        IWizard wizard = getWizard();
        
        return (wizard instanceof AbstractWizard) ? ((AbstractWizard)wizard).canFlipToNextPage(this)
                                                  : super.canFlipToNextPage();
    }

}

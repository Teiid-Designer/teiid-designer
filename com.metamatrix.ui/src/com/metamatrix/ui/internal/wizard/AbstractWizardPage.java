/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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

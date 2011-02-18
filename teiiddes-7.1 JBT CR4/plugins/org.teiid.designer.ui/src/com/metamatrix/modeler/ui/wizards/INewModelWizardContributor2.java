/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;


/**
 * Extends the <code>INewModelWizardContributor</code> interface to give the contributor
 * the ability to have more control of which {@link org.eclipse.jface.wizard.IWizardPage}s are shown
 * and in what order.
 * @since 5.0.1
 */
public interface INewModelWizardContributor2 extends INewModelWizardContributor {

    /**
     * Obtains the next <code>IWizardPage</code> to display. 
     * @param thePage the page currently being shown
     * @return the next page
     * @since 5.0.1
     */
    IWizardPage getNextPage(IWizardPage thePage);

    /**
     * Obtains the previous <code>IWizardPage</code> to the specified page. 
     * @param thePage the page currently being shown
     * @return the previous page
     * @since 5.0.1
     */
    IWizardPage getPreviousPage(IWizardPage thePage);

}

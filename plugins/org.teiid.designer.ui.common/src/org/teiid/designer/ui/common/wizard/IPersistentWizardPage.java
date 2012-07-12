/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.wizard;

import org.eclipse.jface.wizard.IWizardPage;

/**<p>
 * </p>
 * @since 4.0
 */
public interface IPersistentWizardPage extends IWizardPage {
    //============================================================================================================================
    // MVC Controller Methods
    
    /**<p>
     * </p>
     * @since 4.0
     */
    void saveSettings();
}

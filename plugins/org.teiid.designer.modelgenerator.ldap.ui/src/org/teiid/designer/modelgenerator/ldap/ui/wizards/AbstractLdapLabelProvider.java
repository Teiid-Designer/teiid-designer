/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiPlugin;

/**
 * Abstract implementation of LDAP label provider
 */
public abstract class AbstractLdapLabelProvider extends LabelProvider {

    /**
     * Ticked box image
     */
    protected final static Image CHECKED_IMAGE = ModelGeneratorLdapUiPlugin.getDefault().getImage(
                                                                                                ModelGeneratorLdapUiConstants.Images.CHECKED_CHECKBOX);
    /**
     * Unticked box image
     */
    protected final static Image UNCHECKED_IMAGE = ModelGeneratorLdapUiPlugin.getDefault().getImage(
                                                                                                  ModelGeneratorLdapUiConstants.Images.UNCHECKED_CHECKBOX);

    private final LdapImportWizardManager importManager;

    /**
     * Create new instance
     *
     * @param manager
     */
    public AbstractLdapLabelProvider(LdapImportWizardManager manager) {
        this.importManager = manager;
    }

    /**
     * @return the importManager
     */
    public LdapImportWizardManager getImportManager() {
        return this.importManager;
    }
}

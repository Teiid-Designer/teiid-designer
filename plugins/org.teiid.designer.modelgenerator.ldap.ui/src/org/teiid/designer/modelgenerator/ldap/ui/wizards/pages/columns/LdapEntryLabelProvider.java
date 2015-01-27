/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns;

import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapLabelProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class LdapEntryLabelProvider extends AbstractLdapLabelProvider {

    /**
     * @param manager
     */
    public LdapEntryLabelProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ILdapEntryNode) {
            return ((ILdapEntryNode)element).getLabel();
        }

        if (element instanceof ILdapAttributeNode) {
            return ((ILdapAttributeNode)element).getId();
        }

        return null;
    }
}

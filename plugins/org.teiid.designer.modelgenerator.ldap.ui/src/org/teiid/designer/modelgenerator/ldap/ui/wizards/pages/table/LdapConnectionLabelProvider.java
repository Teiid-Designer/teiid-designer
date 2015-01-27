/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapLabelProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 * Tree viewer label provider for LDAP connection
 */
public class LdapConnectionLabelProvider extends AbstractLdapLabelProvider {

    /**
     * Create new instance
     *
     * @param manager
     */
    public LdapConnectionLabelProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ILdapEntryNode) {
            ILdapEntryNode entryNode = (ILdapEntryNode) element;
            if (entryNode.isRoot()) {
                return entryNode.getLabel();
            }

            return entryNode.getSourceBaseName();
        }

        return null;
    }
}

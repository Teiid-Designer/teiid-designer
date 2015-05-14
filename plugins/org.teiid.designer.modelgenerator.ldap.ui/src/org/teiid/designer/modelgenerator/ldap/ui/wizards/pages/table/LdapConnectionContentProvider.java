/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapContentProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.impl.ConnectionNode;

/**
 * Tree viewer content provider for an LDAP connection
 */
public class LdapConnectionContentProvider extends AbstractLdapContentProvider {

    /**
     * Create new instance
     *
     * @param manager
     */
    public LdapConnectionContentProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof LdapImportWizardManager) {
            ConnectionNode node = ((LdapImportWizardManager)inputElement).getConnectionNode();
            return new Object[] {node};
        }

        if (inputElement instanceof ILdapEntryNode) {
            return getChildren(inputElement);
        }

        return EMPTY_ARRAY;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ILdapEntryNode)
            return ((ILdapEntryNode)element).hasChildren();

        return false;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (!(parentElement instanceof ILdapEntryNode))
            return EMPTY_ARRAY;

        ILdapEntryNode parentNode = (ILdapEntryNode)parentElement;
        return parentNode.getChildren();
    }

    @Override
    public Object getParent(Object element) {
        if (!(element instanceof ILdapEntryNode))
            return EMPTY_ARRAY;

        ILdapEntryNode node = (ILdapEntryNode)element;
        return node.getParent();
    }
}

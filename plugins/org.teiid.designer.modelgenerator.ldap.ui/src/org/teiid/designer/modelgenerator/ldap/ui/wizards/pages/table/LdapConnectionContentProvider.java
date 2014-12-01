/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.table;

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
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
            ConnectionNode node = new ConnectionNode((LdapImportWizardManager) inputElement);
            return new Object[] { node };
        }

        if (inputElement instanceof ILdapEntryNode) {
            return getChildren(inputElement);
        }

        return EMPTY_ARRAY;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ILdapEntryNode)
            return true;

        return false;
    }

    /**
     * @param contextNode
     * @return the child entries of the given node
     *
     * @throws NamingException
     */
    private List<ILdapEntryNode> findChildEntries(ILdapEntryNode contextNode) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(null);

        NamingEnumeration<?> searchEnumeration = getLdapContext().search(contextNode.getSourceName(), "objectClass=*", searchControls); //$NON-NLS-1$
        List<ILdapEntryNode> results = new ArrayList<ILdapEntryNode>();
        while (searchEnumeration != null && searchEnumeration.hasMore()) {
            SearchResult result = (SearchResult) searchEnumeration.next();
            ILdapEntryNode node = getImportManager().newEntry(result, contextNode);
            results.add(node);
        }
        return results;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (! (parentElement instanceof ILdapEntryNode))
            return EMPTY_ARRAY;

        try {
            ILdapEntryNode parentNode = (ILdapEntryNode) parentElement;
            List<ILdapEntryNode> entries = findChildEntries(parentNode);
            return entries.toArray();

        } catch (NamingException ex) {
            getImportManager().notifyError(ex);
            ModelGeneratorLdapUiConstants.UTIL.log(ex);
            return EMPTY_ARRAY;
        }
    }

    @Override
    public Object getParent(Object element) {
        if (! (element instanceof ILdapEntryNode))
            return EMPTY_ARRAY;

        ILdapEntryNode node = (ILdapEntryNode) element;
        return node.getParent();
    }
}

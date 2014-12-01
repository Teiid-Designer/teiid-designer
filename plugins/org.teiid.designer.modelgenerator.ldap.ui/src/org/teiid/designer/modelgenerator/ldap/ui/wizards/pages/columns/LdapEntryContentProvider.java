/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.columns;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.AbstractLdapContentProvider;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class LdapEntryContentProvider extends AbstractLdapContentProvider {

    /**
     * @param manager
     */
    public LdapEntryContentProvider(LdapImportWizardManager manager) {
        super(manager);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(getImportManager());
    }

    private String getObjectClassFilter(ILdapEntryNode contextNode) {
        String nameInSourceArray[] = contextNode.getSourceNameSuffix().split("\\?");  //$NON-NLS-1$
        String objectClass = null;

        if(nameInSourceArray.length >= 3) {
            objectClass = nameInSourceArray[2];
        }
        // if there is no specification in the Name In Source,
        // see if the connector property is set to true.  If
        // it is, use the Name of the class for the restriction.
        if(objectClass == null || objectClass.equals("")) {  //$NON-NLS-1$
            return "objectClass=*"; //$NON-NLS-1$
        } else {
            return "objectClass=" + objectClass; //$NON-NLS-1$
        }
    }

    /**
     * Search for the child entries of this node then return
     * a cumulative collection of their attributes
     *
     * @param contextNode
     * @return the attributes of the given node's children
     *
     * @throws NamingException
     */
    private Collection<ILdapAttributeNode> findChildAttributes(ILdapEntryNode contextNode) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(null);

        String filter = getObjectClassFilter(contextNode);
        NamingEnumeration<?> searchEnumeration = getLdapContext().search(contextNode.getSourceName(), filter, searchControls);
        Map<Integer, ILdapAttributeNode> childAttributes = new HashMap<Integer, ILdapAttributeNode>();

        /*
         * For each child entry in the enumeration, find their attributes
         */
        while (searchEnumeration != null && searchEnumeration.hasMore()) {
            SearchResult searchResult = (SearchResult) searchEnumeration.next();
            Attributes resultAttrs = searchResult.getAttributes();
            if (resultAttrs.size() == 0)
                continue;

            try {
                NamingEnumeration<? extends Attribute> attrEnum = resultAttrs.getAll();

                /*
                 * Enumerate through each attribute in order to build an ldapAttributeNode
                 * to display and store in the import manager
                 */
                while(attrEnum.hasMore()) {
                    Attribute attribute = attrEnum.next();
                    ILdapAttributeNode newAttribute = getImportManager().newAttribute(contextNode, attribute);

                    /*
                     * Check whether this attribute has already been added to the
                     * child attribute map. We want the existing attribute in the set
                     * in order to increment its cost statistics
                     */
                    ILdapAttributeNode childAttribute = childAttributes.get(newAttribute.hashCode());
                    if (childAttribute == null) {
                        childAttributes.put(newAttribute.hashCode(), newAttribute);
                        childAttribute = newAttribute;
                    }

                    NamingEnumeration<?> valuesEnum = attribute.getAll();
                    if (valuesEnum == null) {
                        childAttribute.incrementNullValueCount();
                        continue;
                    }

                    /*
                     * Need to apply the values of this attribute in order
                     * to determine the number of distinct values.
                     */
                    while (valuesEnum.hasMore()) {
                        Object value = valuesEnum.next();
                        childAttribute.addValue(value);
                    }
                }
            } catch (NamingException ex) {
                ModelGeneratorLdapUiConstants.UTIL.log(ex);
            }
        }

        return childAttributes.values();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof LdapImportWizardManager) {
            return getImportManager().getSelectedEntries().toArray();
        }

        if (parentElement instanceof ILdapEntryNode) {

            try {
                ILdapEntryNode parentNode = (ILdapEntryNode) parentElement;
                Collection<ILdapAttributeNode> childAttributes = findChildAttributes(parentNode);
                return childAttributes.toArray();

            } catch (NamingException ex) {
                getImportManager().notifyError(ex);
                ModelGeneratorLdapUiConstants.UTIL.log(ex);
                return EMPTY_ARRAY;
            }
        }

        return EMPTY_ARRAY;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ILdapAttributeNode)
            return ((ILdapAttributeNode)element).getAssociatedEntry();

        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ILdapEntryNode)
            return true;

        return false;
    }

}

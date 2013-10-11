/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import java.util.Collection;



/**
 *
 */
public interface ILdapEntryNode {

    /**
     * LDAP context separator
     */
    String SEPARATOR = ","; //$NON-NLS-1$

    /**
     * @return parent of this node
     */
    ILdapEntryNode getParent();

    /**
     * @return the non-qualified name of the ldap node
     */
    String getSourceBaseName();

    /**
     * @return the label
     */
    String getLabel();

    /**
     * @param label
     */
    void setLabel(String label);

    /**
     * @return source name suffix
     */
    String getSourceNameSuffix();

    /**
     * @param tableNameSuffix
     */
    void setSourceNameSuffix(String tableNameSuffix);

    /**
     * @return the fully qualified name of the ldap node
     */
    String getSourceName();

    /**
     * @return is this the root node
     */
    boolean isRoot();

    /**
     * @return Is this node context relative
     */
    boolean isRelative();

    /**
     * @return attributes
     */
    Collection<ILdapAttributeNode> getAttributes();

    /**
     * Add the given attribute
     *
     * @param attribute
     *
     * @return true if added, false otherwise
     */
    boolean addAttribute(ILdapAttributeNode attribute);

    /**
     * Remove the given attribute
     *
     * @param attribute
     *
     * @return true if removed, false otherwise
     */
    boolean removeAttribute(ILdapAttributeNode attribute);
}

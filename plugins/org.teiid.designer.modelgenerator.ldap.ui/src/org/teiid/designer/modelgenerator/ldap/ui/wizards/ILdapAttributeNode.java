/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

/**
 *
 */
public interface ILdapAttributeNode {

    /**
     * @return the id
     */
    String getId();

    /**
     * @return the associated entry
     */
    ILdapEntryNode getAssociatedEntry();

    /**
     * @return the label
     */
    String getLabel();

    /**
     * @param label
     */
    void setLabel(String label);
}

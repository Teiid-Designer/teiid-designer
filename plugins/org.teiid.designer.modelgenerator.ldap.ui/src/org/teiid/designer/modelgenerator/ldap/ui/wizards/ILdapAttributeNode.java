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
     * Value used for the default length
     */
    int DEFAULT_VALUE_LENGTH = 32768;

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

    /**
     * @return distinct value count
     */
    int getDistinctValueCount();

    /**
     * Increment the null value count
     */
    void incrementNullValueCount();

    /**
     * @return null value count
     */
    int getNullValueCount();

    /**
     * Add a value of this attribute. Used for calculation of a distinct value count
     *
     * @param value
     */
    void addValue(Object value);

    /**
     * @return the length of the longest value
     */
    int getMaximumValueLength();
}

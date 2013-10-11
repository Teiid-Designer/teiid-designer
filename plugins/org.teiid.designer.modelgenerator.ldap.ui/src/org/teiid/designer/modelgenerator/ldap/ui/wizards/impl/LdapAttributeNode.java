/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.impl;

import javax.naming.directory.Attribute;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;

/**
 *
 */
public class LdapAttributeNode implements ILdapAttributeNode {

    private String label;

    private final String id;

    private final ILdapEntryNode associatedEntry;

    /**
     * @param associatedEntry
     * @param attribute
     */
    public LdapAttributeNode(ILdapEntryNode associatedEntry, Attribute attribute) {
        this.associatedEntry = associatedEntry;
        this.id = attribute.getID();
        this.label = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ILdapEntryNode getAssociatedEntry() {
        return associatedEntry;
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to set
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.associatedEntry == null) ? 0 : this.associatedEntry.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LdapAttributeNode other = (LdapAttributeNode)obj;
        if (this.id == null) {
            if (other.id != null) return false;
        } else if (!this.id.equals(other.id)) return false;
        if (this.associatedEntry == null) {
            if (other.associatedEntry != null) return false;
        } else if (!this.associatedEntry.equals(other.associatedEntry)) return false;
        return true;
    }
}

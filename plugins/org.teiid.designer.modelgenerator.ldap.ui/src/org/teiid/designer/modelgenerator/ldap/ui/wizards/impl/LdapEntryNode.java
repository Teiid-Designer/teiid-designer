/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;

/**
 *
 */
public class LdapEntryNode implements ILdapEntryNode {

    private final ILdapEntryNode parent;

    private final IEntry entry;

    private String label;

    /*
     * Used for the suffix of source name which can include a scope, eg.
     * sourceBaseName?search_scope?objectClass_name
     */
    private String sourceNameSuffix = EMPTY_STRING;


    private final Set<ILdapAttributeNode> attributes = new HashSet<ILdapAttributeNode>();


    /**
     * @param parent
     * @param entry
     */
    public LdapEntryNode(ILdapEntryNode parent, IEntry entry) {
        this.parent = parent;
        this.entry = entry;
        setLabel(getSourceBaseName());
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public ILdapEntryNode getParent() {
        return parent;
    }

    @Override
    public IEntry getEntry() {
        return entry;
    }

    @Override
    public Object[] getChildren() {
        return new Object[0];
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    /**
     * @return the name
     */
    @Override
    public String getSourceBaseName() {
        return entry.getRdn().getName();
    }

    @Override
    public String getSourceName() {
        return entry.getDn().getName();
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
    public String getSourceNameSuffix() {
        return sourceNameSuffix;
    }

    @Override
    public void setSourceNameSuffix(String sourceNameSuffix) {
        this.sourceNameSuffix = sourceNameSuffix;
    }

    @Override
    public boolean addAttribute(ILdapAttributeNode attribute) {
        return attributes.add(attribute);
    }

    @Override
    public boolean removeAttribute(ILdapAttributeNode attribute) {
        return attributes.remove(attribute);
    }

    @Override
    public Collection<ILdapAttributeNode> getAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.parent == null) ? 0 : this.parent.hashCode());
        result = prime * result + ((this.entry == null) ? 0 : this.entry.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LdapEntryNode other = (LdapEntryNode)obj;
        if (this.parent == null) {
            if (other.parent != null) return false;
        } else if (!this.parent.equals(other.parent)) return false;
        if (this.entry == null) {
            if (other.entry != null) return false;
        } else if (!this.entry.equals(other.entry)) return false;
        return true;
    }
}

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
import javax.naming.directory.SearchResult;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;

/**
 *
 */
public class LdapEntryNode implements ILdapEntryNode {

    private String label;

    /*
     * Used for the suffix of source name which can include a scope, eg.
     * sourceBaseName?search_scope?objectClass_name
     */
    private String sourceNameSuffix = ""; //$NON-NLS-1$

    private final String sourceBaseName;

    private final ILdapEntryNode parent;

    private final boolean relative;

    private final Set<ILdapAttributeNode> attributes = new HashSet<ILdapAttributeNode>();

    /**
     * @param searchResult
     * @param parent
     */
    public LdapEntryNode(SearchResult searchResult, ILdapEntryNode parent) {
        this.sourceBaseName = searchResult.getName();
        this.parent = parent;
        this.relative = searchResult.isRelative();
        setLabel(getSourceBaseName());
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isRelative() {
        return relative;
    }

    @Override
    public ILdapEntryNode getParent() {
        return parent;
    }

    /**
     * @return the name
     */
    @Override
    public String getSourceBaseName() {
        return this.sourceBaseName;
    }

    @Override
    public String getSourceName() {
        if (isRelative()) {
            return getSourceBaseName() + SEPARATOR + parent.getSourceName();
        }

        return getSourceBaseName();
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
        result = prime * result + (this.relative ? 1231 : 1237);
        result = prime * result + ((this.sourceBaseName == null) ? 0 : this.sourceBaseName.hashCode());
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
        if (this.relative != other.relative) return false;
        if (this.sourceBaseName == null) {
            if (other.sourceBaseName != null) return false;
        } else if (!this.sourceBaseName.equals(other.sourceBaseName)) return false;
        return true;
    }
}

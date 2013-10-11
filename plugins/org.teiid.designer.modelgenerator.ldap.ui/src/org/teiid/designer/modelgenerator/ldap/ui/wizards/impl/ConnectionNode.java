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
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class ConnectionNode implements ILdapEntryNode {

    private static final String SLASH = "/"; //$NON-NLS-1$

    private String rootDN;

    private String context;

    /**
     * @param manager
     */
    public ConnectionNode(LdapImportWizardManager manager) {
        IConnectionProfile connectionProfile = manager.getConnectionProfile();
        Properties properties = connectionProfile.getBaseProperties();
        this.rootDN = properties.getProperty(ILdapProfileConstants.ROOT_DN_SUFFIX_PROP_ID);
        this.context = properties.getProperty(ILdapProfileConstants.URL_PROP_ID);
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isRelative() {
        return false;
    }

    @Override
    public ILdapEntryNode getParent() {
        return null;
    }

    @Override
    public String getSourceName() {
        return rootDN;
    }

    @Override
    public String getSourceBaseName() {
        return rootDN;
    }

    @Override
    public String getLabel() {
        return context + SLASH + rootDN;
    }

    @Override
    public void setLabel(String label) {
        // Do nothing
    }

    @Override
    public String getSourceNameSuffix() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public void setSourceNameSuffix(String tableNameSuffix) {
        // Do nothing
    }

    @Override
    public boolean addAttribute(ILdapAttributeNode attribute) {
        return false;
    }

    @Override
    public boolean removeAttribute(ILdapAttributeNode attribute) {
        return false;
    }

    @Override
    public Collection<ILdapAttributeNode> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.context == null) ? 0 : this.context.hashCode());
        result = prime * result + ((this.rootDN == null) ? 0 : this.rootDN.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ConnectionNode other = (ConnectionNode)obj;
        if (this.context == null) {
            if (other.context != null) return false;
        } else if (!this.context.equals(other.context)) return false;
        if (this.rootDN == null) {
            if (other.rootDN != null) return false;
        } else if (!this.rootDN.equals(other.rootDN)) return false;
        return true;
    }
}

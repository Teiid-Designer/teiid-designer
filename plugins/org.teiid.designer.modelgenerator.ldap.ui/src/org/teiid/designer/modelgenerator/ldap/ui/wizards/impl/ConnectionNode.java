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
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;

/**
 *
 */
public class ConnectionNode implements ILdapEntryNode {

	private final LdapImportWizardManager manager;

    private final String context;

    /**
     * @param manager
     */
    public ConnectionNode(LdapImportWizardManager manager) {
        this.manager = manager;
        IConnectionProfile connectionProfile = manager.getConnectionProfile();
        Properties properties = connectionProfile.getBaseProperties();
        this.context = properties.getProperty(ILdapProfileConstants.URL_PROP_ID);
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public IEntry getEntry() {
        return manager.getBrowserConnection().getRootDSE();
    }

    @Override
    public ILdapEntryNode getParent() {
        return null;
    }

    @Override
    public Object[] getChildren() {
        return manager.getSelectedEntries().toArray();
    }

    @Override
    public boolean hasChildren() {
        return !manager.getSelectedEntries().isEmpty();
    }

    @Override
    public String getSourceName() {
        return context;
    }

    @Override
    public String getSourceBaseName() {
        return context;
    }

    @Override
    public String getLabel() {
        return context;
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
        return true;
    }
}

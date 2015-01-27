/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import org.eclipse.core.resources.IContainer;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.core.util.KeyInValueHashMap;
import org.teiid.designer.core.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.modelgenerator.ldap.RelationalModelBuilder;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.impl.LdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.impl.LdapEntryNode;

/**
 * LDAP Import Manager - Business Object for interacting with GUI
 *
 * @since 8.0
 */
/**
 * @author phantomjinx
 *
 *
 * @since 8.0
 */
public class LdapImportWizardManager implements IChangeNotifier {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private String sourceModelName;
	private boolean sourceModelExists;
    private IContainer sourceModelLocation;

    private IConnectionProfile connectionProfile;

    private Properties designerProperties;

    private Collection<IChangeListener> listeners;

    // Transient field for communicating exceptions from tree providers
    // to the pages on which they are resident
    private Exception error = null;

    // Flag to determine whether wizard is synchronising
    // and manager should avoid notifying pages too frequently
    private boolean synchronising;

    /**
     * The set of {@link LdapEntryNode}s selected to be tables
     * in the source model
     */
    private class LdapEntryKeyAdapter implements KeyFromValueAdapter<Integer, ILdapEntryNode> {
        @Override
        public Integer getKey(ILdapEntryNode value) {
            return value.hashCode();
        }
    }

    private KeyInValueHashMap<Integer, ILdapEntryNode> ldapEntryNodes = new KeyInValueHashMap<Integer, ILdapEntryNode>(new LdapEntryKeyAdapter());

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Create new instance
     */
    public LdapImportWizardManager() {
        this.listeners = new ArrayList<IChangeListener>(5);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(LdapImportWizardManager.class.getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    /**
	 * @return connectionProfile
	 */
	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	/**
	 * @param connectionProfile Sets connectionProfile to the specified value.
	 */
	public void setConnectionProfile(IConnectionProfile connectionProfile) {
	    if (this.connectionProfile == connectionProfile)
	        return;

		this.connectionProfile = connectionProfile;
		notifyChanged();
	}

	/**
	 *
	 * @return sourceModelName the source relational model name
	 */
	public String getSourceModelName() {
        return this.sourceModelName;
	}

	/**
	 *
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setSourceModelName(String sourceModelName) {
        if (this.sourceModelName != null && this.sourceModelName.equals(sourceModelName))
            return;

		this.sourceModelName = sourceModelName;
		notifyChanged();
	}

	/**
	 *
	 * @return sourceModelLocation the target location where the source model is going to be created
	 */
	public IContainer getSourceModelLocation() {
		return this.sourceModelLocation;
	}

	/**
	 * @param sourceModelLocation the target location where the view model either exists or is going to be created
	 */
	public void setSourceModelLocation(IContainer sourceModelLocation) {
        if (this.sourceModelLocation != null && this.sourceModelLocation.equals(sourceModelLocation))
            return;

        this.sourceModelLocation = sourceModelLocation;
        notifyChanged();
	}

	/**
	 * @param sourceModelExists does the source model already exist
	 */
	public void setSourceModelExists(boolean sourceModelExists) {
	    if (this.sourceModelExists == sourceModelExists)
	        return;

		this.sourceModelExists = sourceModelExists;
		notifyChanged();
	}

	/**
	 * @return whether source model exists
	 */
	public boolean sourceModelExists() {
		return this.sourceModelExists;
	}

	/**
	 * Create a new {@link ILdapEntryNode} if one is not already present
	 *
	 * @param searchResult
	 * @param contextNode
	 *
	 * @return existing or new entry node
	 */
	public ILdapEntryNode newEntry(SearchResult searchResult, ILdapEntryNode contextNode) {
	    ILdapEntryNode newNode = new LdapEntryNode(searchResult, contextNode);
	    ILdapEntryNode currNode = ldapEntryNodes.get(newNode.hashCode());

	    if (currNode != null)
	        return currNode;

	    return newNode;
	}

	/**
	 * Add an entry to the collection of selected entries
	 *
	 * @param entryNode
	 */
	public void addEntry(ILdapEntryNode entryNode) {
	    if (ldapEntryNodes.containsKey(entryNode.hashCode())) {
	        return;
	    }

	    ldapEntryNodes.add(entryNode);
	    notifyChanged();
	}

	/**
     * Removes an entry from the set of selected entries
     *
     * @param entryNode
     */
    public void removeEntry(ILdapEntryNode entryNode) {
        if (ldapEntryNodes.remove(entryNode) != null)
            notifyChanged();
    }

    /**
     * Whether the given entry has been selected
     *
     * @param entryNode
     *
     * @return true if the entry has been selected, false otherwise
     */
    public boolean entrySelected(ILdapEntryNode entryNode) {
        return ldapEntryNodes.containsKey(entryNode.hashCode());
    }

    /**
     * @return whether there are any selected entries
     */
    public boolean hasSelectedEntries() {
        return ldapEntryNodes.isEmpty();
    }

    /**
     * @return selected entries
     */
    public Collection<ILdapEntryNode> getSelectedEntries() {
        return Collections.unmodifiableCollection(ldapEntryNodes.values());
    }

    /**
     * Create a new {@link ILdapAttributeNode} if one is not already present
     *
     * @param attribute
     * @param contextNode
     *
     * @return existing or new entry node
     */
    public ILdapAttributeNode newAttribute(ILdapEntryNode contextNode, Attribute attribute) {
        ILdapAttributeNode newAttributeNode = new LdapAttributeNode(contextNode, attribute);

        ILdapEntryNode entryNode = ldapEntryNodes.get(contextNode.hashCode());
        if (entryNode == null) {
            // odd situation where entry node has not been added
            entryNode = contextNode;
        }

        Collection<ILdapAttributeNode> attributeNodes = entryNode.getAttributes();
        if (attributeNodes.contains(newAttributeNode)) {
            for (ILdapAttributeNode attributeNode : attributeNodes) {
                if (attributeNode.equals(newAttributeNode))
                    return attributeNode;
            }
        }

        return newAttributeNode;
    }

    /**
     * Add an attribute to the collection of selected attributes
     *
     * @param attribute
     */
    public void addAttribute(ILdapAttributeNode attribute) {
        ILdapEntryNode associatedEntry = attribute.getAssociatedEntry();

        // Prefer the version already in the import manager
        associatedEntry = ldapEntryNodes.get(associatedEntry.hashCode());
        if (associatedEntry == null)
            return;

        associatedEntry.addAttribute(attribute);
        notifyChanged();
    }

    /**
     * Removes an attribute from the set of selected attributes
     *
     * @param attribute
     */
    public void removeAttribute(ILdapAttributeNode attribute) {
        ILdapEntryNode associatedEntry = attribute.getAssociatedEntry();

        // Prefer the version already in the import manager
        associatedEntry = ldapEntryNodes.get(associatedEntry.hashCode());
        if (associatedEntry == null)
            return;

        if (associatedEntry.removeAttribute(attribute))
            notifyChanged();
    }

    /**
     * Whether the given attribute has been selected
     *
     * @param attribute
     *
     * @return true if the attribute has been selected, false otherwise
     */
    public boolean attributeSelected(ILdapAttributeNode attribute) {
        ILdapEntryNode associatedEntry = attribute.getAssociatedEntry();

        associatedEntry = ldapEntryNodes.get(associatedEntry.hashCode());
        if (associatedEntry == null) {
            // entry has not been selected so stands to reason its attributes will not have either
            return false;
        }

        return associatedEntry.getAttributes().contains(attribute);
    }

    /**
     * @return whether there are attributes for each selected entries
     */
    public boolean hasAttributesForEachSelectedEntry() {
        if (ldapEntryNodes.isEmpty())
            return false;

        for (ILdapEntryNode entry : ldapEntryNodes.values()) {
            if (entry.getAttributes().isEmpty())
                return false;
        }

        return true;
    }

    /**
     * @return all selected attributes
     */
    public Collection<ILdapAttributeNode> getSelectedAttributes() {
        Collection<ILdapAttributeNode> allAttributes = new ArrayList<ILdapAttributeNode>();
        for (ILdapEntryNode entry : ldapEntryNodes.values()) {
            allAttributes.addAll(entry.getAttributes());
        }

        return Collections.unmodifiableCollection(allAttributes);
    }

	/**
     * Clears the list of selected entry nodes
     */
    public void clearEntries() {
        ldapEntryNodes.clear();
        notifyChanged();
    }

    /**
     * Set the designer properties
     *
     * @param properties
     */
    public void setDesignerProperties(Properties properties) {
        this.designerProperties = properties;
    }

    /**
     * @return designer related properties
     */
    public Properties getDesignerProperties() {
        return this.designerProperties;
    }

    /**
     * Set an individual designer related property
     *
     * @param key
     * @param value
     */
    public void setDesignerProperty(String key, String value) {
        if( this.designerProperties != null ) {
            this.designerProperties.put(key, value);
        }
    }

    /**
     * Based on the values of this manager, build a model
     */
    void createModel() {
      RelationalModelBuilder modelBuilder = new RelationalModelBuilder();
      try {
          modelBuilder.modelEntries(getSourceModelLocation(), getSourceModelName(),
                                                         getConnectionProfile(), getSelectedEntries());
      } catch (Exception e) {
          ModelGeneratorLdapUiConstants.UTIL.log(e);
      }
    }

    /**
     * Notify listeners of a change of state
     */
    public void notifyChanged() {
        if (isSynchronising()) {
            /*
             * In middle of major operation so protect performance
             * by notifying of state needlessly. When synchronising
             * is switched back on then a notify change should be called.
             */
            return;
        }

        for( IChangeListener listener: this.listeners ) {
            listener.stateChanged(this);
        }
    }

    @Override
    public void addChangeListener(IChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeChangeListener(IChangeListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * @return the error
     */
    public Exception getError() {
        return this.error;
    }

    /**
     * Notify listeners of any generated errors.
     * Note, the error field is transient in that its
     * immediately nullified after calling this.
     *
     * @param error
     */
    public void notifyError(Exception error) {
        this.error = error;
        notifyChanged();
        this.error = null;
    }

    /**
     * @return synchronising
     */
    public boolean isSynchronising() {
        return this.synchronising;
    }

    /**
     * @param synchronising
     */
    public void setSynchronising(boolean synchronising) {
        this.synchronising = synchronising;
    }
}

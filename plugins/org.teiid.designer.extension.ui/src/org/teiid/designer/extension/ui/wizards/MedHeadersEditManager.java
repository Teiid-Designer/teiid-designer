/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * EditManager to keep track of add and remove changes to the original list of MedHeaders
 */
public class MedHeadersEditManager {

    private List<ModelExtensionDefinitionHeader> currentMedHeaderList;
    private List<ModelExtensionDefinitionHeader> originalMedHeaderList;
    private List<ModelExtensionDefinition> medsToAddList;
    private List<String> namespacesToRemoveList;
    private List<String> namespacesToUpdateList;

    /**
     * Constructor for MedHeadersEditManager.
     */
    public MedHeadersEditManager( List<ModelExtensionDefinitionHeader> headerList ) {
        CoreArgCheck.isNotNull(headerList, "headerList is null"); //$NON-NLS-1$

        // Keep a copy of the original list and also a working list.
        this.originalMedHeaderList = headerList;
        this.currentMedHeaderList = new ArrayList(headerList);
        this.medsToAddList = new ArrayList<ModelExtensionDefinition>();
        this.namespacesToRemoveList = new ArrayList<String>();
        this.namespacesToUpdateList = new ArrayList<String>();
    }

    public void addModelExtensionDefinition(ModelExtensionDefinition med) {
        CoreArgCheck.isNotNull(med, "med is null"); //$NON-NLS-1$

        // if the current MED header list does not contain a match already, then add this med
        ModelExtensionDefinitionHeader medHeader = med.getHeader();
        if (!this.currentMedHeaderList.contains(medHeader)) {
            this.currentMedHeaderList.add(medHeader);

            // Include in the 'add' list - If the original Med list does not contain this
            if (!this.originalMedHeaderList.contains(medHeader)) {
                this.medsToAddList.add(med);
            }
        }

        // If the added meds prefix was on the remove list, take it off
        String nsPrefix = med.getNamespacePrefix();
        this.namespacesToRemoveList.remove(nsPrefix);
    }

    public void addModelExtensionDefinitions( List<ModelExtensionDefinition> medList ) {
        CoreArgCheck.isNotNull(medList, "medList is null"); //$NON-NLS-1$
        for (ModelExtensionDefinition med : medList) {
            addModelExtensionDefinition(med);
        }
    }

    public void updateModelExtensionDefinition( ModelExtensionDefinition med ) {
        CoreArgCheck.isNotNull(med, "med is null"); //$NON-NLS-1$

        // if the current MED header list does not contain a match already, then add this med
        ModelExtensionDefinitionHeader updateMedHeader = med.getHeader();

        // replace current header with this new one
        for (ModelExtensionDefinitionHeader header : this.currentMedHeaderList) {
            if (header.getNamespacePrefix().equals(updateMedHeader.getNamespacePrefix())) {
                this.currentMedHeaderList.remove(header);
                this.currentMedHeaderList.add(updateMedHeader);
                break;
            }
        }

        this.namespacesToUpdateList.add(updateMedHeader.getNamespacePrefix());
    }

    public void removeModelExtensionDefinition( String nsPrefix ) {
        CoreArgCheck.isNotNull(nsPrefix, "Namespace Prefix is null"); //$NON-NLS-1$

        // Add the namespace prefix to the remove list (if original list contains it)
        if (getListIndexOfNamespace(this.originalMedHeaderList, nsPrefix) != -1) {
            this.namespacesToRemoveList.add(nsPrefix);
        }

        // If there is a MED with a matching namespace in the Add List, remove it
        int addListIndex = getListIndexOfNamespace(getHeaderList(this.medsToAddList), nsPrefix);
        if (addListIndex != -1) {
            this.medsToAddList.remove(addListIndex);
        }

        // If there is a MED with a matching namespace in the Update List, remove it
        this.namespacesToUpdateList.remove(nsPrefix);

        // Remove the MED with matching namespace from the current list.
        int currentListIndex = getListIndexOfNamespace(this.currentMedHeaderList, nsPrefix);
        if (currentListIndex != -1) {
            this.currentMedHeaderList.remove(currentListIndex);
        }

    }

    /**
     * @return currentMedHeaderList
     */
    public List<ModelExtensionDefinitionHeader> getCurrentHeaders() {
        return currentMedHeaderList;
    }

    /**
     * @return originalMedHeaderList
     */
    public List<ModelExtensionDefinitionHeader> getOriginalHeaders() {
        return originalMedHeaderList;
    }

    /**
     * @return medsToAddList
     */
    public List<ModelExtensionDefinition> getModelExtensionDefnsToAdd() {
        return medsToAddList;
    }

    /**
     * @return namespacesToRemoveList
     */
    public List<String> getNamespacesToRemove() {
        return namespacesToRemoveList;
    }

    /**
     * @return namespacesToRemoveList
     */
    public List<String> getNamespacesToUpdate() {
        return this.namespacesToUpdateList;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("\nMED Headers Edit Manager: \n"); //$NON-NLS-1$
        text.append("Original Headers: \n"); //$NON-NLS-1$
        for (ModelExtensionDefinitionHeader header : originalMedHeaderList) {
            text.append("  NSPrefix: " + header.getNamespacePrefix() + ", Version: " + header.getVersion() + '\n'); //$NON-NLS-1$ //$NON-NLS-2$
        }
        text.append("Current Headers: \n"); //$NON-NLS-1$
        for (ModelExtensionDefinitionHeader header : currentMedHeaderList) {
            text.append("  NSPrefix: " + header.getNamespacePrefix() + ", Version: " + header.getVersion() + '\n'); //$NON-NLS-1$ //$NON-NLS-2$
        }
        text.append("MEDs to ADD: \n"); //$NON-NLS-1$
        for (ModelExtensionDefinition med : medsToAddList) {
            text.append("  NSPrefix: " + med.getNamespacePrefix() + ", Version: " + med.getVersion() + '\n'); //$NON-NLS-1$ //$NON-NLS-2$
        }
        text.append("NsPrefixes to REMOVE: \n"); //$NON-NLS-1$
        for (String nsPrefix : namespacesToRemoveList) {
            text.append("  NSPrefix: " + nsPrefix + '\n'); //$NON-NLS-1$ 
        }
        text.append("NsPrefixes to UPDATE: \n"); //$NON-NLS-1$
        for (String nsPrefix : this.namespacesToUpdateList) {
            text.append("  NSPrefix: " + nsPrefix + '\n'); //$NON-NLS-1$ 
        }

        return text.toString();
    }

    private List<ModelExtensionDefinitionHeader> getHeaderList( List<ModelExtensionDefinition> medList ) {
        CoreArgCheck.isNotNull(medList, "medList is null"); //$NON-NLS-1$
        List<ModelExtensionDefinitionHeader> headerList = new ArrayList(medList.size());
        for (ModelExtensionDefinition med : medList) {
            headerList.add(med.getHeader());
        }
        return headerList;
    }

    private int getListIndexOfNamespace( List<ModelExtensionDefinitionHeader> medHeaderList,
                                         String namespace ) {
        int resultIndex = -1;
        for (int i = 0; i < medHeaderList.size(); i++) {
            ModelExtensionDefinitionHeader med = medHeaderList.get(i);
            if (med.getNamespacePrefix().equals(namespace)) {
                resultIndex = i;
                break;
            }
        }
        return resultIndex;
    }

}

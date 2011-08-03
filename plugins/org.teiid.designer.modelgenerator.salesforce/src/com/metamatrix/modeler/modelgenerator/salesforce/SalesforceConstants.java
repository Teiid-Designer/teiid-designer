/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

/**
 * Various SalesForce constants
 */
public interface SalesforceConstants {

    String EXTENDED_METAMODEL_ID = "org.teiid.designer.model.extension.salesforce"; //$NON-NLS-1$
    String NAMESPACE = "http://org.teiid.designer/metamodels/Salesforce"; //$NON-NLS-1$
    String NAMESPACE_PREFIX = "salesforce"; //$NON-NLS-1$

    /**
     * Relational column extension property simple identifiers (no namesace).
     */
    interface SF_Column {
        String CALCULATED = "Calculated"; //$NON-NLS-1$
        String CUSTOM = "Custom"; //$NON-NLS-1$
        String DEFAULTED = "Defaulted on Create"; //$NON-NLS-1$
        String PICKLIST_VALUES = "Picklist Values"; //$NON-NLS-1$
    }

    /**
     * Relational table extension property simple identifiers (no namespace).
     */
    interface SF_Table {
        String CUSTOM = "Custom"; //$NON-NLS-1$
        String SUPPORTS_CREATE = "Supports Create"; //$NON-NLS-1$
        String SUPPORTS_DELETE = "Supports Delete"; //$NON-NLS-1$
        String SUPPORTS_LOOKUP = "Supports ID Lookup"; //$NON-NLS-1$
        String SUPPORTS_MERGE = "Supports Merge"; //$NON-NLS-1$
        String SUPPORTS_QUERY = "Supports Query"; //$NON-NLS-1$
        String SUPPORTS_REPLICATE = "Supports Replicate"; //$NON-NLS-1$
        String SUPPORTS_RETRIEVE = "Supports Retrieve"; //$NON-NLS-1$
        String SUPPORTS_SEARCH = "Supports Search"; //$NON-NLS-1$
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import org.teiid.designer.extension.manager.ExtensionPropertiesManager;
import org.teiid.designer.extension.manager.IExtensionPropertiesHandler;

/**
 * Various SalesForce constants
 */
public interface SalesforceConstants {
	
	String MODEL_EXTENSION_ID = "org.teiid.designer.model.extension.salesforce"; //$NON-NLS-1$
	String NAMESPACE = "http://org.teiid.designer/metamodels/Salesforce"; //$NON-NLS-1$
	String ID = "salesforce"; //$NON-NLS-1$
	String DISPLAY_NAME = "Salesforce"; //$NON-NLS-1$
	String EXT_PROP_NAMESPACE_PREFIX = ExtensionPropertiesManager.createExtendedModelNamespace(ID);
	
	// Need to construct unique ID key : ext-id:salesforce
	String EXTENSION_FULL_ID_KEY = IExtensionPropertiesHandler.EXTENSION_ID_PREFIX + ID;
	// Need to construct unique CND key : ext-cnd:salesforce
	String EXTENSION_FULL_CND_KEY = IExtensionPropertiesHandler.EXTENSION_CND_PREFIX + ID;
	// Need to construct unique Namespace key : ext-namespace:salesforce
	String EXTENSION_FULL_NAMESPACE_KEY = IExtensionPropertiesHandler.EXTENSION_NAMESPACE_PREFIX + ID;
	
	/**
	 * Character Constants
	 */
	char CR = '\n';
	char SP = ' ';
	char EQ = '=';
	char DASH = '-';
	
	String FALSE_STR = Boolean.FALSE.toString();
	String[] ALLOWED_BOOLEAN_VALUES = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };
	
	String TABLE_NODE_TYPE_NAME = "salesforce:tableCapabilities"; //$NON-NLS-1$
	String COLUMN_NODE_TYPE_NAME = "salesforce:columnCapabilities"; //$NON-NLS-1$

	/**
	 * The following constants define the CND-specific property names and property types
	 */
	interface CND {
		// PROPERTY NAMES
		String SUPPORTS_CREATE = ID + ":supportsCreate"; //$NON-NLS-1$
		String SUPPORTS_DELETE = ID + ":supportsDelete"; //$NON-NLS-1$
		String CUSTOM = ID + ":custom"; //$NON-NLS-1$
		String SUPPORTS_LOOKUP = ID + ":supportsIDLookup"; //$NON-NLS-1$
		String SUPPORTS_MERGE = ID + ":supportsMerge"; //$NON-NLS-1$
		String SUPPORTS_QUERY = ID + ":supportsQuery"; //$NON-NLS-1$
		String SUPPORTS_REPLICATE = ID + ":supportsReplicate"; //$NON-NLS-1$
		String SUPPORTS_RETRIEVE = ID + ":supportsRetrieve"; //$NON-NLS-1$
		String SUPPORTS_SEARCH = ID + ":supportsSearch"; //$NON-NLS-1$
		String DEFAULTED = ID + ":defaultedOnCreate"; //$NON-NLS-1$
		String CALCULATED = ID + ":calculated"; //$NON-NLS-1$
		String PICKLIST_VALUES = ID + ":picklistValues"; //$NON-NLS-1$
		// PROPERTY TYPES
		String BOOLEAN_TYPE_STR = "(boolean)"; //$NON-NLS-1$
		String STRING_TYPE_STR = "(string)"; //$NON-NLS-1$
		// DEFAULT VALUES
		String FALSE_DEFAULT_STR = "'false'"; //$NON-NLS-1$
	}
	
	/**
	 * The following String defines a constant to store the mapping between the CND table property name and the actual
	 * Name segment of the property stored in the tag. Since CND properties have no inherent Description or Display
	 * Name, we needed a way to persist and find the Display Name
	 */
    interface SF_Table {
    	String SUPPORTS_CREATE = "Supports Create"; //$NON-NLS-1$
    	String SUPPORTS_DELETE = "Supports Delete"; //$NON-NLS-1$
    	String CUSTOM = "Custom"; //$NON-NLS-1$
    	String SUPPORTS_LOOKUP = "Supports ID Lookup"; //$NON-NLS-1$
    	String SUPPORTS_MERGE = "Supports Merge"; //$NON-NLS-1$
    	String SUPPORTS_QUERY = "Supports Query"; //$NON-NLS-1$
    	String SUPPORTS_REPLICATE = "Supports Replicate"; //$NON-NLS-1$
    	String SUPPORTS_RETRIEVE = "Supports Retrieve"; //$NON-NLS-1$
    	String SUPPORTS_SEARCH = "Supports Search"; //$NON-NLS-1$
    }
    
	/**
	 * The following String defines a constant to store the mapping between the CND column property name and the actual
	 * Name segment of the property stored in the tag. Since CND properties have no inherent Description or Display
	 * Name, we needed a way to persist and find the Display Name
	 */
    interface SF_Column {
    	String CUSTOM = "Custom"; //$NON-NLS-1$
    	String DEFAULTED = "Defaulted on Create"; //$NON-NLS-1$
    	String CALCULATED = "Calculated"; //$NON-NLS-1$
    	String PICKLIST_VALUES = "Picklist Values"; //$NON-NLS-1$
    }
}

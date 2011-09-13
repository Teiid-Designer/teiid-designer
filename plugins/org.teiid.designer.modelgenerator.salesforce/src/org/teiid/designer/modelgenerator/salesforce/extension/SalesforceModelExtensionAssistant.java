/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce.extension;

import java.util.HashMap;
import java.util.Map;

import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.SF_Column;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.SF_Table;

/**
 * The <code>SalesforceModelExtensionAssistant</code> provides help when working with the Salesforce model extension properties.
 */
public final class SalesforceModelExtensionAssistant extends ModelObjectExtensionAssistant {

    /**
     * A mapping of property keys from those in the model extension definition to the keys required by the translator.
     */
    private static Map<String, String> propIds;

    public SalesforceModelExtensionAssistant() {
        if (propIds == null) {
            propIds = new HashMap<String, String>();
            propIds.put("custom", SF_Table.CUSTOM); //$NON-NLS-1$ 
            propIds.put("supportsCreate", SF_Table.SUPPORTS_CREATE); //$NON-NLS-1$ 
            propIds.put("supportsDelete", SF_Table.SUPPORTS_DELETE); //$NON-NLS-1$ 
            propIds.put("supportsIdLookup", SF_Table.SUPPORTS_LOOKUP); //$NON-NLS-1$ 
            propIds.put("supportsMerge", SF_Table.SUPPORTS_MERGE); //$NON-NLS-1$ 
            propIds.put("supportsQuery", SF_Table.SUPPORTS_QUERY); //$NON-NLS-1$ 
            propIds.put("supportsReplicate", SF_Table.SUPPORTS_REPLICATE); //$NON-NLS-1$ 
            propIds.put("supportsRetrieve", SF_Table.SUPPORTS_RETRIEVE); //$NON-NLS-1$ 
            propIds.put("supportsSearch", SF_Table.SUPPORTS_SEARCH); //$NON-NLS-1$ 
            propIds.put("calculated", SF_Column.CALCULATED); //$NON-NLS-1$ 
            propIds.put("defaultedOnCreate", SF_Column.DEFAULTED); //$NON-NLS-1$ 
            propIds.put("picklistValues", SF_Column.PICKLIST_VALUES); //$NON-NLS-1$ 
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#createPropertyDefinition(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public ModelExtensionPropertyDefinition createPropertyDefinition( String id,
                                                                      String displayName,
                                                                      String type,
                                                                      String required,
                                                                      String defaultValue,
                                                                      String fixedValue,
                                                                      String advanced,
                                                                      String masked,
                                                                      String index ) {
        String mappedId = propIds.get(id);

        if (!CoreStringUtil.isEmpty(mappedId)) {
            id = mappedId;
        }

        return super.createPropertyDefinition(id, displayName, type, required, defaultValue, fixedValue, advanced, masked, index);
    }

}

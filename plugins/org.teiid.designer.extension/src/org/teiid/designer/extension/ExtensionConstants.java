/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.util.Comparator;
import java.util.Locale;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;

/**
 * Common extension constants.
 */
public interface ExtensionConstants {

    String PLUGIN_ID = ExtensionConstants.class.getPackage().getName();

    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    String SCHEMA_FILENAME = "modelExtension.xsd"; //$NON-NLS-1$

    String MED_EXTENSION = "mxd"; //$NON-NLS-1$

    String DOT_MED_EXTENSION = '.' + MED_EXTENSION;

    /**
     * The identifier of the model extension definition (MED) file project builder.
     */
    String MED_BUILDER_ID = "org.teiid.designer.extension.ui.modelExtensionDefinitionBuilder"; //$NON-NLS-1$
    
    /**
     * The model extension definition schema attribute names.
     */
    interface Attributes {
        String ADVANCED = "advanced"; //$NON-NLS-1$
        String DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
        String FIXED_VALUE = "fixedValue"; //$NON-NLS-1$
        String INDEX = "index"; //$NON-NLS-1$
        String LOCALE = "locale"; //$NON-NLS-1$
        String MASKED = "masked"; //$NON-NLS-1$
        String METAMODEL_URI = "metamodelUri"; //$NON-NLS-1$
        String NAME = "name"; //$NON-NLS-1$
        String NAMESPACE_PREFIX = "namespacePrefix"; //$NON-NLS-1$
        String NAMESPACE_URI = "namespaceUri"; //$NON-NLS-1$
        String REQUIRED = "required"; //$NON-NLS-1$
        String TYPE = "type"; //$NON-NLS-1$
        String VERSION = "version"; //$NON-NLS-1$
    }

    interface Namespaces {
        String NS_XSI = "xsi"; //$NON-NLS-1$
        String NS_MED = "p"; //$NON-NLS-1$
        String NS_XSI_VALUE = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
        String NS_MED_VALUE = "http://www.jboss.org/teiiddesigner/ext/2012"; //$NON-NLS-1$
        String NS_KEY = "http://www.w3.org/2000/xmlns/"; //$NON-NLS-1$
        String NS_SCHEMALOC = "xsi:schemaLocation"; //$NON-NLS-1$
        String NS_SCHEMALOC_VALUE = NS_MED_VALUE + ' ' + NS_MED_VALUE + '/' + SCHEMA_FILENAME;
    }

    /**
     * The model extension definition schema element names.
     */
    interface Elements {
        String ALLOWED_VALUE = "allowedValue"; //$NON-NLS-1$
        String DESCRIPTION = "description"; //$NON-NLS-1$
        String DISPLAY = "display"; //$NON-NLS-1$
        String EXTENDED_METACLASS = "extendedMetaclass"; //$NON-NLS-1$
        String MODEL_EXTENSION = "modelExtension"; //$NON-NLS-1$
        String MODEL_TYPE = "modelType"; //$NON-NLS-1$
        String PROPERTY = "property"; //$NON-NLS-1$
    }

    Comparator LOCALE_COMPARATOR = new Comparator<Locale>() {

        /**
         * @param thisLocale the first locale to be compared
         * @param thatLocale the second locale to be compared
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
         *         second
         */
        @Override
        public int compare( Locale thisLocale,
                            Locale thatLocale ) {
            return thisLocale.getDisplayLanguage().compareTo(thatLocale.getDisplayLanguage());
        }
    };

    /**
     * The default Model Extension Definition (MED) operation names.
     */
    interface MedOperations {
        String ADD_MED_TO_MODEL = "ADD_MED_TO_MODEL"; //$NON-NLS-1$
        String DELETE_MED_FROM_MODEL = "DELETE_MED_FROM_MODEL"; //$NON-NLS-1$
        String DELETE_MED_FROM_REGISTRY = "DELETE_MED_FROM_REGISTRY"; //$NON-NLS-1$

        String ADD_METACLASS = "ADD_METACLASS"; //$NON-NLS-1$
        String DELETE_METACLASS = "DELETE_METACLASS"; //$NON-NLS-1$

        String ADD_PROPERTY_DEFINITION = "ADD_PROPERTY_DEFINITION"; //$NON-NLS-1$
        String DELETE_PROPERTY_DEFINITION = "DELETE_PROPERTY_DEFINITION"; //$NON-NLS-1$

        String CHANGE_HEADER_INFO = "CHANGE_HEADER_INFO"; //$NON-NLS-1$
        
        String SHOW_IN_REGISTRY = "SHOW_IN_REGISTRY"; //$NON-NLS-1$
    }

}

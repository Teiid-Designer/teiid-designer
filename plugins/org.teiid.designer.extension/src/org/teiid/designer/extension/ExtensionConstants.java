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
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;
import org.teiid.core.designer.util.StringConstants;


/**
 * Common extension constants.
 *
 * @since 8.0
 */
public interface ExtensionConstants extends StringConstants {

    /**
     * The plugin identifier. Value is {@value}.
     */
    String PLUGIN_ID = ExtensionConstants.class.getPackage().getName();

    /**
     * The bundles logging and i18n utilities.
     */
    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    /**
     * The model extension definition schema file name. Value is {@value}.
     */
    String SCHEMA_FILENAME = "modelExtension.xsd"; //$NON-NLS-1$

    /**
     * The model extension definition file extension. Value is {@value}.
     */
    String MED_EXTENSION = "mxd"; //$NON-NLS-1$

    /**
     * The model extension definition file extension prefixed with a dot.
     */
    String DOT_MED_EXTENSION = DOT + MED_EXTENSION;

    /**
     * The identifier of the model extension definition (MED) file project builder.
     */
    String MED_BUILDER_ID = "org.teiid.designer.extension.ui.modelExtensionDefinitionBuilder"; //$NON-NLS-1$

    /**
     * Name of the hidden project used to store MED mxd files that are either built-in or imported
     */
    String BUILTIN_MEDS_PROJECT_NAME = "BuiltInMedsProject"; //$NON-NLS-1$

    /**
     * Directory used to store med imported from a Teiid Instance
     */
    String TEIID_IMPORT_DIRECTORY = "imported"; //$NON-NLS-1$

    /**
     * Type of mxd file being managed
     */
    public enum MxdType {
        /**
         * Custom mxd files created by users
         */
        USER,

        /**
         * mxd files imported from teiid instances
         */
        IMPORTED;
    }

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

    /**
     * Namespace-related names found in the model extension definition schema.
     */
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

    /**
     * Compares {@link Locale}s based on display language.
     */
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
        
        /**
         * An operation that would indicate a MED has been saved to a model.
         */
        String SHOW_CONTAINED_IN_MODEL = "SHOW_CONTAINED_IN_MODEL"; //$NON-NLS-1$

        String SHOW_IN_REGISTRY = "SHOW_IN_REGISTRY"; //$NON-NLS-1$
    }

}

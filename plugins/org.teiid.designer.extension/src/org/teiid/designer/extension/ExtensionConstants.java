/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;

/**
 * Common extension constants.
 */
public interface ExtensionConstants {

    String PLUGIN_ID = ExtensionConstants.class.getPackage().getName();

    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    String SCHEMA_FILENAME = "modelExtension.xsd"; //$NON-NLS-1$

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
        String NS_MED_VALUE = "http://org.teiid.modelExtension/2011"; //$NON-NLS-1$
        String NS_KEY = "http://www.w3.org/2000/xmlns/"; //$NON-NLS-1$
        String NS_SCHEMALOC = "xsi:schemaLocation"; //$NON-NLS-1$
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
        String PROPERTY = "property"; //$NON-NLS-1$
    }

}

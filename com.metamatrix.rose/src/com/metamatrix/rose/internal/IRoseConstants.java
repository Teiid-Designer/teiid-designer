/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * Constants intended for use only by classes within this plug-in.
 * 
 * @since 4.1
 */
public interface IRoseConstants extends com.metamatrix.rose.IRoseConstants {

    /**
     * Contains private constants and methods used by other constants within this class.
     * 
     * @since 4.1
     */
    class _ {
        static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

        private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(IRoseConstants.class);

        static String getString( final String text ) {
            return UTIL.getString(I18N_PREFIX + text);
        }
    }

    /**
     * The path variable value that represents the Rose model's folder.
     * 
     * @since 4.1
     */
    String MODEL_PATH_SYMBOL = "&"; //$NON-NLS-1$

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.1
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, _.BUNDLE_NAME, ResourceBundle.getBundle(_.BUNDLE_NAME));

    /**
     * The prefix that appears before all Rose path variables within Rose units.
     * 
     * @since 4.1
     */
    String VARIABLE_PREFIX = "$"; //$NON-NLS-1$

    /**
     * Constants related to Rose metamodel extension properties
     * 
     * @since 4.1
     */
    interface IMetamodelExtensionProperties {

        /**
         * Indicates whether a UML Element is locked, meaning it cannot be modified or deleted by the Rose importer.
         * 
         * @since 4.1
         */
        String LOCKED = _.getString("locked"); //$NON-NLS-1$

        /**
         * The name of the Rose node from which a UML Element was imported.
         * 
         * @since 4.1
         */
        String NAME_IN_SOURCE = _.getString("nameInSource"); //$NON-NLS-1$

        /**
         * The QUID of the Rose node from which a UML Element was imported.
         * 
         * @since 4.1
         */
        String QUID = _.getString("quid"); //$NON-NLS-1$

        /**
         * The path of the Rose unit from which a UML Element was imported.
         * 
         * @since 4.1
         */
        String SOURCE = _.getString("source"); //$NON-NLS-1$
    }

    /**
     * @since 4.1
     */
    interface IReferenceTypes {

        /**
         * @since 4.1
         */
        String ASSOCIATION_CLASS = _.getString("associationClass"); //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String GENERALIZATION = _.getString("generalization"); //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String OWNER = _.getString("owner"); //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String TYPE = _.getString("type"); //$NON-NLS-1$
    }
}

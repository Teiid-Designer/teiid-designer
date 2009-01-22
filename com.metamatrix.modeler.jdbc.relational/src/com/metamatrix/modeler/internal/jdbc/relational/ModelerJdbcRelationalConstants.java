/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.relational;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * @since 4.0
 */
public interface ModelerJdbcRelationalConstants extends com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants {

    /**
     * Contains private constants and utility methods used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelerJdbcRelationalConstants.class);

        static final String BUNDLE_NAME = com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID
                                          + ".i18n"; //$NON-NLS-1$

        /**
         * @since 4.0
         */
        static String getString( final String id ) {
            return Util.getString(I18N_PREFIX + id);
        }
    }

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /**
     * Common messages.
     * 
     * @since 4.0
     */
    public static interface Messages {
        String MODEL_NOT_RELATIONAL_MESSAGE = PC.getString("modelNotRelationalMessage"); //$NON-NLS-1$
    }
}

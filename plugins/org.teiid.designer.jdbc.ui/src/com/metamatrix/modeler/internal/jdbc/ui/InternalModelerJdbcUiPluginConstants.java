/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.jdbc.ui.ModelerJdbcUiConstants;

/**
 * @since 4.0
 */
public interface InternalModelerJdbcUiPluginConstants extends ModelerJdbcUiConstants {

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    PluginUtil Util = ModelerJdbcUiConstants.Util;

    /**
     * Contains debug contexts defined by this plug-in.
     * 
     * @since 4.0
     */
    interface Debug {
    }

    /**
     * Contains widget constants.
     * 
     * @since 4.0
     */
    interface Widgets {
        class PC {
            private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(Widgets.class);

            static String getString( final String id ) {
                return Util.getString(I18N_PREFIX + id);
            }
        }

        String CLASS_NAME_LABEL = PC.getString("classNameLabel"); //$NON-NLS-1$
        String DRIVER_LABEL = PC.getString("driverLabel"); //$NON-NLS-1$
        String NAME_LABEL = PC.getString("nameLabel"); //$NON-NLS-1$
        String URL_LABEL = PC.getString("urlLabel"); //$NON-NLS-1$
        String URL_SYNTAX_LABEL = PC.getString("urlSyntaxLabel"); //$NON-NLS-1$
        String USER_NAME_LABEL = PC.getString("userNameLabel"); //$NON-NLS-1$
        String SELECT_DRIVER_ITEM = "<Select Driver>"; //$NON-NLS-1$
    }
}

/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.jdbc.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;
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
        static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

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
